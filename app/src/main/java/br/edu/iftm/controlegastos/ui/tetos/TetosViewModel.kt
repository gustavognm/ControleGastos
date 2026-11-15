package br.edu.iftm.controlegastos.ui.tetos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.iftm.controlegastos.data.model.Categoria
import br.edu.iftm.controlegastos.data.repository.GastosRepository
import br.edu.iftm.controlegastos.util.Formatador
import br.edu.iftm.controlegastos.util.ValidadorValor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TetosUiState(
    /** Texto digitado em cada campo (um por categoria). */
    val valores: Map<Categoria, String> = Categoria.entries.associateWith { "" },
    val erros: Map<Categoria, String> = emptyMap(),
    val carregando: Boolean = true,
    val salvo: Boolean = false,
) {
    /** Soma dos valores válidos digitados, para mostrar o orçamento total ao vivo. */
    val totalCentavos: Long
        get() = valores.values.sumOf {
            (ValidadorValor.validar(it) as? ValidadorValor.Resultado.Valido)?.centavos ?: 0L
        }
}

class TetosViewModel(
    private val repositorio: GastosRepository,
    private val userId: String,
) : ViewModel() {

    private val _estado = MutableStateFlow(TetosUiState())
    val estado: StateFlow<TetosUiState> = _estado.asStateFlow()

    init {
        // Preenche os campos com os tetos já salvos.
        viewModelScope.launch {
            val salvos = repositorio.tetos(userId).first().associate { it.categoria to it.valorCentavos }
            _estado.update { estado ->
                estado.copy(
                    valores = Categoria.entries.associateWith { categoria ->
                        salvos[categoria]?.let(Formatador::valorParaCampo) ?: ""
                    },
                    carregando = false,
                )
            }
        }
    }

    fun alterar(categoria: Categoria, texto: String) {
        val filtrado = texto.filter { it.isDigit() || it == ',' || it == '.' }.take(13)
        _estado.update { it.copy(valores = it.valores + (categoria to filtrado), erros = it.erros - categoria) }
    }

    fun salvar() {
        val valoresConvertidos = mutableMapOf<Categoria, Long?>()
        val erros = mutableMapOf<Categoria, String>()

        _estado.value.valores.forEach { (categoria, texto) ->
            when (val r = ValidadorValor.validar(texto)) {
                is ValidadorValor.Resultado.Valido -> valoresConvertidos[categoria] = r.centavos
                ValidadorValor.Resultado.Vazio -> valoresConvertidos[categoria] = null // sem teto
                is ValidadorValor.Resultado.Invalido -> erros[categoria] = r.mensagem
            }
        }

        _estado.update { it.copy(erros = erros) }
        if (erros.isNotEmpty()) return

        viewModelScope.launch {
            repositorio.salvarTetos(userId, valoresConvertidos)
            _estado.update { it.copy(salvo = true) }
        }
    }
}
