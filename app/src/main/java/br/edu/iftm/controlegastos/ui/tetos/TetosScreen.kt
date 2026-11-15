package br.edu.iftm.controlegastos.ui.tetos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.iftm.controlegastos.data.model.Categoria
import br.edu.iftm.controlegastos.ui.componentes.BotaoVoltar
import br.edu.iftm.controlegastos.ui.componentes.Icone
import br.edu.iftm.controlegastos.util.Formatador

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TetosScreen(viewModel: TetosViewModel, aoVoltar: () -> Unit) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    LaunchedEffect(estado.salvo) { if (estado.salvo) aoVoltar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tetos mensais") },
                navigationIcon = { BotaoVoltar(aoVoltar) },
            )
        },
    ) { espacos ->
        if (estado.carregando) {
            Column(Modifier.fillMaxSize().padding(espacos).padding(32.dp)) { CircularProgressIndicator() }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacos)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                "Quanto você pode gastar por mês em cada categoria? Deixe em branco para não ter limite.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))

            Categoria.entries.forEach { categoria ->
                val erro = estado.erros[categoria]
                OutlinedTextField(
                    value = estado.valores[categoria].orEmpty(),
                    onValueChange = { viewModel.alterar(categoria, it) },
                    label = { Text(categoria.rotulo) },
                    leadingIcon = { Icone(categoria.icone, descricao = null, cor = MaterialTheme.colorScheme.primary) },
                    prefix = { Text("R$ ") },
                    placeholder = { Text("Sem teto") },
                    singleLine = true,
                    isError = erro != null,
                    supportingText = erro?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Card(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Orçamento total do mês")
                    Text(
                        Formatador.moeda(estado.totalCentavos),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Button(onClick = viewModel::salvar, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                Text("Salvar tetos")
            }
        }
    }
}
