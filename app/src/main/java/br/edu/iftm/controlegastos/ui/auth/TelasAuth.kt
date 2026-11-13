package br.edu.iftm.controlegastos.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.iftm.controlegastos.R
import br.edu.iftm.controlegastos.ui.componentes.Icone

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    aoAutenticar: () -> Unit,
    irParaCadastro: () -> Unit,
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    LaunchedEffect(estado.autenticado) { if (estado.autenticado) aoAutenticar() }

    FormularioAuth(
        titulo = "Entrar",
        subtitulo = "Controle seus gastos do dia a dia",
        estado = estado,
        mostrarConfirmacao = false,
        textoBotao = "Entrar",
        aoClicarBotao = viewModel::entrar,
        textoLink = "Ainda não tem conta? Cadastre-se",
        aoClicarLink = { viewModel.limpar(); irParaCadastro() },
        aoAlterarEmail = viewModel::alterarEmail,
        aoAlterarSenha = viewModel::alterarSenha,
        aoAlterarConfirmacao = {},
    )
}

@Composable
fun CadastroScreen(
    viewModel: AuthViewModel,
    aoAutenticar: () -> Unit,
    voltarParaLogin: () -> Unit,
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    LaunchedEffect(estado.autenticado) { if (estado.autenticado) aoAutenticar() }

    FormularioAuth(
        titulo = "Criar conta",
        subtitulo = "Use seu e-mail e uma senha de pelo menos 6 caracteres",
        estado = estado,
        mostrarConfirmacao = true,
        textoBotao = "Cadastrar",
        aoClicarBotao = viewModel::cadastrar,
        textoLink = "Já tenho conta. Entrar",
        aoClicarLink = voltarParaLogin,
        aoAlterarEmail = viewModel::alterarEmail,
        aoAlterarSenha = viewModel::alterarSenha,
        aoAlterarConfirmacao = viewModel::alterarConfirmacao,
    )
}

/** Formulário compartilhado pelas telas de Login e Cadastro. */
@Composable
private fun FormularioAuth(
    titulo: String,
    subtitulo: String,
    estado: AuthUiState,
    mostrarConfirmacao: Boolean,
    textoBotao: String,
    aoClicarBotao: () -> Unit,
    textoLink: String,
    aoClicarLink: () -> Unit,
    aoAlterarEmail: (String) -> Unit,
    aoAlterarSenha: (String) -> Unit,
    aoAlterarConfirmacao: (String) -> Unit,
) {
    Scaffold { espacos ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacos)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icone(R.drawable.ic_carteira, descricao = null, tamanho = 64.dp, cor = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                subtitulo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = estado.email,
                onValueChange = aoAlterarEmail,
                label = { Text("E-mail") },
                singleLine = true,
                isError = estado.erroEmail != null,
                supportingText = estado.erroEmail?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = estado.senha,
                onValueChange = aoAlterarSenha,
                label = { Text("Senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = estado.erroSenha != null,
                supportingText = estado.erroSenha?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (mostrarConfirmacao) ImeAction.Next else ImeAction.Done,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            if (mostrarConfirmacao) {
                OutlinedTextField(
                    value = estado.confirmacaoSenha,
                    onValueChange = aoAlterarConfirmacao,
                    label = { Text("Confirmar senha") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = estado.erroConfirmacao != null,
                    supportingText = estado.erroConfirmacao?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            estado.mensagemErro?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = aoClicarBotao,
                enabled = !estado.carregando,
                modifier = Modifier.fillMaxWidth().height(50.dp),
            ) {
                if (estado.carregando) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text(textoBotao)
                }
            }
            TextButton(onClick = aoClicarLink, enabled = !estado.carregando) { Text(textoLink) }
        }
    }
}
