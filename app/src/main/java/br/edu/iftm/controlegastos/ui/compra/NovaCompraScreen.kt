package br.edu.iftm.controlegastos.ui.compra

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.iftm.controlegastos.data.model.Categoria
import br.edu.iftm.controlegastos.data.model.FormaPagamento
import br.edu.iftm.controlegastos.ui.componentes.BotaoVoltar
import br.edu.iftm.controlegastos.ui.componentes.Icone

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NovaCompraScreen(viewModel: NovaCompraViewModel, aoVoltar: () -> Unit) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    LaunchedEffect(estado.salvo) { if (estado.salvo) aoVoltar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova compra") },
                navigationIcon = { BotaoVoltar(aoVoltar) },
            )
        },
    ) { espacos ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacos)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = estado.valor,
                onValueChange = viewModel::alterarValor,
                label = { Text("Valor (R$)") },
                placeholder = { Text("0,00") },
                prefix = { Text("R$ ") },
                singleLine = true,
                isError = estado.erroValor != null,
                supportingText = estado.erroValor?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                textStyle = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
            )

            Titulo("Categoria *")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Categoria.entries.forEach { categoria ->
                    FilterChip(
                        selected = estado.categoria == categoria,
                        onClick = { viewModel.selecionarCategoria(categoria) },
                        label = { Text(categoria.rotulo) },
                        leadingIcon = { Icone(categoria.icone, descricao = null, tamanho = 18.dp) },
                    )
                }
            }
            MensagemErro(estado.erroCategoria)

            OutlinedTextField(
                value = estado.descricao,
                onValueChange = viewModel::alterarDescricao,
                label = { Text("Descrição / estabelecimento") },
                placeholder = { Text(estado.categoria?.exemploEstabelecimento ?: "Ex.: Posto Petrobras") },
                singleLine = true,
                isError = estado.erroDescricao != null,
                supportingText = {
                    Text(estado.erroDescricao ?: "${estado.descricao.length}/${NovaCompraViewModel.MAX_DESCRICAO}")
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
            )

            Titulo("Forma de pagamento *")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FormaPagamento.entries.forEach { forma ->
                    FilterChip(
                        selected = estado.formaPagamento == forma,
                        onClick = { viewModel.selecionarPagamento(forma) },
                        label = { Text(forma.rotulo) },
                    )
                }
            }
            MensagemErro(estado.erroPagamento)

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = viewModel::salvar,
                enabled = !estado.salvando,
                modifier = Modifier.fillMaxWidth().height(50.dp),
            ) { Text("Salvar compra") }
        }
    }
}

@Composable
private fun Titulo(texto: String) {
    Text(texto, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
}

@Composable
private fun MensagemErro(texto: String?) {
    if (texto != null) Text(texto, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
}
