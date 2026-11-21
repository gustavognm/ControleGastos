package br.edu.iftm.controlegastos.ui.extrato

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.iftm.controlegastos.data.model.Categoria
import br.edu.iftm.controlegastos.data.model.Compra
import br.edu.iftm.controlegastos.data.repository.GastosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth

data class ExtratoUiState(
    val mes: YearMonth = YearMonth.now(),
    /** Categoria escolhida no filtro; null = todas. */
    val filtro: Categoria? = null,
    /** Compras do mês já filtradas, da mais recente para a mais antiga. */
    val compras: List<Compra> = emptyList(),
    val carregando: Boolean = true,
) {
    val totalCentavos: Long get() = compras.sumOf { it.valorCentavos }
}

class ExtratoViewModel(
    private val repositorio: GastosRepository,
    userId: String,
) : ViewModel() {

    private val filtro = MutableStateFlow<Categoria?>(null)

    /** Junta as compras do mês com o filtro escolhido; muda sozinho quando qualquer um muda. */
    val estado: StateFlow<ExtratoUiState> =
        combine(repositorio.comprasDoMes(userId), filtro) { compras, categoria ->
            ExtratoUiState(
                filtro = categoria,
                compras = if (categoria == null) compras else compras.filter { it.categoria == categoria },
                carregando = false,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExtratoUiState(),
        )

    fun filtrar(categoria: Categoria?) = filtro.update { categoria }

    fun excluir(compra: Compra) {
        viewModelScope.launch { repositorio.excluirCompra(compra) }
    }
}
