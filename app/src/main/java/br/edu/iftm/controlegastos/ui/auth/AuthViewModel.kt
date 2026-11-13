package br.edu.iftm.controlegastos.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.iftm.controlegastos.data.repository.AuthRepository
import br.edu.iftm.controlegastos.util.ValidadorCampos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Tudo o que as telas de Login e Cadastro precisam mostrar. */
data class AuthUiState(
    val email: String = "",
    val senha: String = "",
    val confirmacaoSenha: String = "",
    val erroEmail: String? = null,
    val erroSenha: String? = null,
    val erroConfirmacao: String? = null,
    val mensagemErro: String? = null,
    val carregando: Boolean = false,
    val autenticado: Boolean = false,
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _estado = MutableStateFlow(AuthUiState())
    val estado: StateFlow<AuthUiState> = _estado.asStateFlow()

    fun alterarEmail(valor: String) = _estado.update { it.copy(email = valor, erroEmail = null, mensagemErro = null) }
    fun alterarSenha(valor: String) = _estado.update { it.copy(senha = valor, erroSenha = null, mensagemErro = null) }
    fun alterarConfirmacao(valor: String) = _estado.update { it.copy(confirmacaoSenha = valor, erroConfirmacao = null) }

    fun entrar() {
        if (!validar(cadastro = false)) return
        executar { authRepository.entrar(it.email.trim(), it.senha) }
    }

    fun cadastrar() {
        if (!validar(cadastro = true)) return
        executar { authRepository.cadastrar(it.email.trim(), it.senha) }
    }

    /** Limpa a tela ao trocar entre Login e Cadastro. */
    fun limpar() = _estado.update { AuthUiState(email = it.email) }

    private fun validar(cadastro: Boolean): Boolean {
        val atual = _estado.value
        val erroEmail = ValidadorCampos.erroEmail(atual.email)
        val erroSenha = ValidadorCampos.erroSenha(atual.senha)
        val erroConfirmacao = if (cadastro && atual.senha != atual.confirmacaoSenha) "As senhas não conferem." else null
        _estado.update { it.copy(erroEmail = erroEmail, erroSenha = erroSenha, erroConfirmacao = erroConfirmacao) }
        return erroEmail == null && erroSenha == null && erroConfirmacao == null
    }

    private fun executar(acao: suspend (AuthUiState) -> Result<Unit>) {
        viewModelScope.launch {
            _estado.update { it.copy(carregando = true, mensagemErro = null) }
            val resultado = acao(_estado.value)
            _estado.update {
                it.copy(
                    carregando = false,
                    autenticado = resultado.isSuccess,
                    mensagemErro = resultado.exceptionOrNull()?.message,
                )
            }
        }
    }
}
