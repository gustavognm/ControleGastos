package br.edu.iftm.controlegastos.ui.painel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.iftm.controlegastos.R
import br.edu.iftm.controlegastos.ui.componentes.Icone
import br.edu.iftm.controlegastos.ui.componentes.IconeCategoria
import br.edu.iftm.controlegastos.ui.componentes.ItemCompra
import br.edu.iftm.controlegastos.ui.theme.corDoNivel
import br.edu.iftm.controlegastos.util.ConsumoCategoria
import br.edu.iftm.controlegastos.util.Formatador
import br.edu.iftm.controlegastos.util.NivelAlerta
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PainelScreen(
    viewModel: PainelViewModel,
    emailUsuario: String?,
    irParaNovaCompra: () -> Unit,
    irParaTetos: () -> Unit,
    irParaExtrato: () -> Unit,
    aoSair: () -> Unit,
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Meus gastos", fontWeight = FontWeight.Bold)
                        emailUsuario?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    }
                },
                actions = {
                    TextButton(onClick = irParaTetos) { Text("Tetos") }
                    TextButton(onClick = aoSair) { Text("Sair") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = irParaNovaCompra) { Text("+  Nova compra") }
        },
    ) { espacos ->
        if (estado.carregando) {
            Column(Modifier.fillMaxSize().padding(espacos), Arrangement.Center, Alignment.CenterHorizontally) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(espacos),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { CartaoResumo(estado) }

            if (!estado.temTetos) {
                item { CartaoSemTetos(irParaTetos) }
            }

            item {
                Text("Consumo por categoria", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(estado.consumos, key = { it.categoria.name }) { CartaoCategoria(it) }

            item {
                Row(
                    Modifier.fillMaxWidth().padding(top = 4.dp),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically,
                ) {
                    Text("Últimas compras", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    TextButton(onClick = irParaExtrato) { Text("Ver extrato") }
                }
            }
            if (estado.compras.isEmpty()) {
                item {
                    Text(
                        "Nenhuma compra lançada neste mês. Toque em \"Nova compra\" para começar.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(estado.compras.take(PainelViewModel.QTD_ULTIMAS_COMPRAS), key = { it.id }) { compra ->
                ItemCompra(compra)
            }
        }
    }
}

/** Resumo financeiro global: total do mês, orçamento e saldo. */
@Composable
private fun CartaoResumo(estado: PainelUiState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth(),
    ) {
        val corTexto = MaterialTheme.colorScheme.onPrimary
        Column(Modifier.padding(20.dp)) {
            Text("Gasto em ${Formatador.mesAno(estado.mes)}", color = corTexto)
            Text(
                Formatador.moeda(estado.totalGastoCentavos),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = corTexto,
            )
            if (estado.temTetos) {
                Spacer(Modifier.height(12.dp))
                BarraConsumo(estado.fracaoGlobal, estado.nivelGlobal)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Column {
                        Text("Orçamento", style = MaterialTheme.typography.bodySmall, color = corTexto)
                        Text(Formatador.moeda(estado.orcamentoCentavos), color = corTexto, fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        val estourou = estado.saldoCentavos < 0
                        Text(
                            if (estourou) "Acima do limite" else "Saldo disponível",
                            style = MaterialTheme.typography.bodySmall,
                            color = corTexto,
                        )
                        Text(
                            Formatador.moeda(kotlin.math.abs(estado.saldoCentavos)),
                            color = corTexto,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartaoSemTetos(irParaTetos: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Defina seus tetos de gasto", fontWeight = FontWeight.Bold)
            Text("Escolha quanto pode gastar por mês em cada categoria para acompanhar as barras de consumo.")
            Spacer(Modifier.height(8.dp))
            Button(onClick = irParaTetos) { Text("Definir tetos") }
        }
    }
}

/** Card de uma categoria com barra de progresso colorida (verde -> amarelo -> vermelho). */
@Composable
private fun CartaoCategoria(consumo: ConsumoCategoria) {
    val cor = corDoNivel(consumo.nivel)
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp), Alignment.CenterVertically) {
                IconeCategoria(consumo.categoria, tamanho = 36.dp)
                Text(
                    consumo.categoria.rotulo,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                if (consumo.tetoCentavos != null) {
                    Text("${(consumo.fracao * 100).roundToInt()}%", color = cor, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                if (consumo.tetoCentavos == null) "${Formatador.moeda(consumo.gastoCentavos)} gastos • sem teto definido"
                else "${Formatador.moeda(consumo.gastoCentavos)} de ${Formatador.moeda(consumo.tetoCentavos)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            if (consumo.tetoCentavos != null) {
                Spacer(Modifier.height(8.dp))
                BarraConsumo(consumo.fracao, consumo.nivel)
                Spacer(Modifier.height(6.dp))
                val restante = consumo.restanteCentavos ?: 0
                val corStatus = if (consumo.nivel == NivelAlerta.VERDE) MaterialTheme.colorScheme.onSurfaceVariant else cor
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (consumo.nivel == NivelAlerta.VERMELHO) {
                        Icone(R.drawable.ic_alerta, descricao = "Alerta", tamanho = 16.dp, cor = cor)
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(
                        when (consumo.nivel) {
                            NivelAlerta.VERMELHO -> "Teto estourado em ${Formatador.moeda(-restante)}"
                            NivelAlerta.AMARELO -> "Atenção: restam ${Formatador.moeda(restante)}"
                            else -> "Restam ${Formatador.moeda(restante)}"
                        },
                        color = corStatus,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (consumo.nivel == NivelAlerta.VERDE) FontWeight.Normal else FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun BarraConsumo(fracao: Float, nivel: NivelAlerta) {
    LinearProgressIndicator(
        progress = { fracao.coerceIn(0f, 1f) },
        color = corDoNivel(nivel),
        trackColor = corDoNivel(nivel).copy(alpha = 0.2f),
        strokeCap = StrokeCap.Round,
        gapSize = 0.dp,
        drawStopIndicator = {},
        modifier = Modifier.fillMaxWidth().height(10.dp),
    )
}
