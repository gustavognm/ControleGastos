package br.edu.iftm.controlegastos.ui.compra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.iftm.controlegastos.data.model.Categoria
import br.edu.iftm.controlegastos.data.model.Compra
import br.edu.iftm.controlegastos.data.model.FormaPagamento
import br.edu.iftm.controlegastos.data.repository.GastosRepository
import br.edu.iftm.controlegastos.util.ValidadorValor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NovaCompraUiState(
    val valor: String = "",
    val categoria: Categoria? = null,
    val descricao: String = "",
    val formaPagamento: FormaPagamento? = null,
    val erroValor: String? = null,
    val erroCategoria: String? = null,
    val erroDescricao: String? = null,
    val erroPagamento: String? = null,
    val salvando: Boolean = false,
    val salvo: Boolean = false,
)

class NovaCompraViewModel(
    private val repositorio: GastosRepository,
    private val userId: String,
) : ViewModel() {

    private val _estado = MutableStateFlow(NovaCompraUiState())
    val estado: StateFlow<NovaCompraUiState> = _estado.asStateFlow()

    fun alterarValor(texto: String) {
        // Só deixa digitar números, vírgula e ponto (bloqueia letras e o sinal de menos).
        val filtrado = texto.filter { it.isDigit() || it == ',' || it == '.' }.take(13)
        _estado.update { it.copy(valor = filtrado, erroValor = null) }
    }

    fun selecionarCategoria(categoria: Categoria) =
        _estado.update { it.copy(categoria = categoria, erroCategoria = null) }

    fun alterarDescricao(texto: String) =
        _estado.update { it.copy(descricao = texto.take(MAX_DESCRICAO), erroDescricao = null) }

    fun selecionarPagamento(forma: FormaPagamento) =
        _estado.update { it.copy(formaPagamento = forma, erroPagamento = null) }

    fun salvar() {
        val atual = _estado.value
        if (atual.salvando) return

        val resultadoValor = ValidadorValor.validar(atual.valor)
        val erroValor = when (resultadoValor) {
            is ValidadorValor.Resultado.Invalido -> resultadoValor.mensagem
            ValidadorValor.Resultado.Vazio -> "Informe o valor da compra."
            is ValidadorValor.Resultado.Valido -> null
        }
        val erroCategoria = if (atual.categoria == null) "Escolha uma categoria." else null
        val erroDescricao = if (atual.descricao.isBlank()) "Informe a descrição ou o estabelecimento." else null
        val erroPagamento = if (atual.formaPagamento == null) "Escolha a forma de pagamento." else null

        _estado.update {
            it.copy(
                erroValor = erroValor,
                erroCategoria = erroCategoria,
                erroDescricao = erroDescricao,
                erroPagamento = erroPagamento,
            )
        }
        if (resultadoValor !is ValidadorValor.Resultado.Valido ||
            erroCategoria != null || erroDescricao != null || erroPagamento != null
        ) return

        viewModelScope.launch {
            _estado.update { it.copy(salvando = true) }
            repositorio.adicionarCompra(
                Compra(
                    userId = userId,
                    valorCentavos = resultadoValor.centavos,
                    categoria = atual.categoria!!,
                    descricao = atual.descricao.trim(),
                    formaPagamento = atual.formaPagamento!!,
                    data = System.currentTimeMillis(),
                )
            )
            _estado.update { it.copy(salvando = false, salvo = true) }
        }
    }

    companion object {
        const val MAX_DESCRICAO = 60
    }
}
