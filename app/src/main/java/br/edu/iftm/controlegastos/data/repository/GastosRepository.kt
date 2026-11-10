package br.edu.iftm.controlegastos.data.repository

import br.edu.iftm.controlegastos.data.local.CompraDao
import br.edu.iftm.controlegastos.data.local.TetoDao
import br.edu.iftm.controlegastos.data.model.Categoria
import br.edu.iftm.controlegastos.data.model.Compra
import br.edu.iftm.controlegastos.data.model.Teto
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth
import java.time.ZoneId

/** Acesso aos dados de compras e tetos (Room). */
class GastosRepository(
    private val compraDao: CompraDao,
    private val tetoDao: TetoDao,
) {

    /** Compras do usuário feitas dentro do mês informado (padrão: mês atual). */
    fun comprasDoMes(userId: String, mes: YearMonth = YearMonth.now()): Flow<List<Compra>> {
        val zona = ZoneId.systemDefault()
        val inicio = mes.atDay(1).atStartOfDay(zona).toInstant().toEpochMilli()
        val fim = mes.plusMonths(1).atDay(1).atStartOfDay(zona).toInstant().toEpochMilli() - 1
        return compraDao.comprasDoPeriodo(userId, inicio, fim)
    }

    fun tetos(userId: String): Flow<List<Teto>> = tetoDao.tetosDoUsuario(userId)

    suspend fun adicionarCompra(compra: Compra) {
        compraDao.inserir(compra)
    }

    suspend fun excluirCompra(compra: Compra) = compraDao.excluir(compra)

    /**
     * Salva os tetos. Categorias com valor null ficam "sem teto" (são removidas).
     */
    suspend fun salvarTetos(userId: String, valores: Map<Categoria, Long?>) {
        val definidos = valores.filterValues { it != null }
            .map { (categoria, valor) -> Teto(userId, categoria, valor!!) }
        val removidos = valores.filterValues { it == null }.keys.toList()
        tetoDao.salvar(definidos)
        if (removidos.isNotEmpty()) tetoDao.remover(userId, removidos)
    }
}
