package br.edu.iftm.controlegastos.ui.extrato

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.iftm.controlegastos.data.model.Categoria
import br.edu.iftm.controlegastos.data.model.Compra
import br.edu.iftm.controlegastos.ui.componentes.BotaoVoltar
import br.edu.iftm.controlegastos.ui.componentes.DialogoExcluirCompra
import br.edu.iftm.controlegastos.ui.componentes.Icone
import br.edu.iftm.controlegastos.ui.componentes.ItemCompra
import br.edu.iftm.controlegastos.util.Formatador

/** Extrato: todas as compras do mês, com filtro por categoria e exclusão. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtratoScreen(viewModel: ExtratoViewModel, aoVoltar: () -> Unit) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var compraParaExcluir by remember { mutableStateOf<Compra?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Extrato do mês")
                        Text(Formatador.mesAno(estado.mes), style = MaterialTheme.typography.bodySmall)
                    }
                },
                navigationIcon = { BotaoVoltar(aoVoltar) },
            )
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            if (estado.filtro == null) "Total do mês" else "Total em ${estado.filtro!!.rotulo}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            Formatador.moeda(estado.totalCentavos),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            if (estado.compras.size == 1) "1 compra" else "${estado.compras.size} compras",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            item { FiltroCategorias(estado.filtro, viewModel::filtrar) }

            if (estado.compras.isEmpty()) {
                item {
                    Text(
                        "Nenhuma compra encontrada.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(estado.compras, key = { it.id }) { compra ->
                ItemCompra(compra, aoExcluir = { compraParaExcluir = compra })
            }
        }
    }

    compraParaExcluir?.let { compra ->
        DialogoExcluirCompra(
            compra = compra,
            aoConfirmar = { viewModel.excluir(compra); compraParaExcluir = null },
            aoCancelar = { compraParaExcluir = null },
        )
    }
}

/** Linha de chips que rola para o lado: "Todas" + uma por categoria. */
@Composable
private fun FiltroCategorias(selecionada: Categoria?, aoSelecionar: (Categoria?) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FilterChip(selected = selecionada == null, onClick = { aoSelecionar(null) }, label = { Text("Todas") })
        }
        items(Categoria.entries) { categoria ->
            FilterChip(
                selected = selecionada == categoria,
                onClick = { aoSelecionar(categoria) },
                label = { Text(categoria.rotulo) },
                leadingIcon = { Icone(categoria.icone, descricao = null, tamanho = 18.dp) },
            )
        }
    }
}
