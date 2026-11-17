package br.edu.iftm.controlegastos.ui.painel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.iftm.controlegastos.data.model.Categoria
import br.edu.iftm.controlegastos.data.model.Compra
import br.edu.iftm.controlegastos.data.repository.GastosRepository
import br.edu.iftm.controlegastos.util.Consumo
import br.edu.iftm.controlegastos.util.ConsumoCategoria
import br.edu.iftm.controlegastos.util.NivelAlerta
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth

data class PainelUiState(
    val mes: YearMonth = YearMonth.now(),
    val consumos: List<ConsumoCategoria> = emptyList(),
    val compras: List<Compra> = emptyList(),
    val totalGastoCentavos: Long = 0,
    /** Soma dos tetos definidos (orçamento do mês). */
    val orcamentoCentavos: Long = 0,
    val carregando: Boolean = true,
) {
    /** Quanto ainda dá para gastar no mês. Negativo = estourou. */
    val saldoCentavos: Long get() = orcamentoCentavos - totalGastoCentavos
    val temTetos: Boolean get() = orcamentoCentavos > 0
    val nivelGlobal: NivelAlerta get() = Consumo.nivel(totalGastoCentavos, orcamentoCentavos)
    val fracaoGlobal: Float
        get() = if (orcamentoCentavos <= 0) 0f else totalGastoCentavos.toFloat() / orcamentoCentavos
}

class PainelViewModel(
    private val repositorio: GastosRepository,
    userId: String,
) : ViewModel() {

    /**
     * Junta (combine) as compras do mês e os tetos. Sempre que QUALQUER um dos dois
     * mudar no banco, o estado é recalculado e a tela se redesenha sozinha.
     */
    val estado: StateFlow<PainelUiState> =
        combine(repositorio.comprasDoMes(userId), repositorio.tetos(userId)) { compras, tetos ->
            val tetoPorCategoria = tetos.associate { it.categoria to it.valorCentavos }
            val gastoPorCategoria = compras.groupBy { it.categoria }
                .mapValues { (_, lista) -> lista.sumOf { it.valorCentavos } }

            PainelUiState(
                consumos = Categoria.entries.map { categoria ->
                    ConsumoCategoria(
                        categoria = categoria,
                        gastoCentavos = gastoPorCategoria[categoria] ?: 0,
                        tetoCentavos = tetoPorCategoria[categoria],
                    )
                },
                compras = compras,
                totalGastoCentavos = compras.sumOf { it.valorCentavos },
                orcamentoCentavos = tetos.sumOf { it.valorCentavos },
                carregando = false,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PainelUiState(),
        )

    fun excluir(compra: Compra) {
        viewModelScope.launch { repositorio.excluirCompra(compra) }
    }
}
