package br.edu.iftm.controlegastos.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.edu.iftm.controlegastos.data.model.Compra
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraDao {

    @Insert
    suspend fun inserir(compra: Compra): Long

    @Delete
    suspend fun excluir(compra: Compra)

    /**
     * Devolve um Flow: sempre que a tabela mudar, o Room emite a lista nova
     * automaticamente. É isso que deixa o painel "em tempo real".
     */
    @Query(
        """
        SELECT * FROM compras
        WHERE userId = :userId AND data BETWEEN :inicio AND :fim
        ORDER BY data DESC
        """
    )
    fun comprasDoPeriodo(userId: String, inicio: Long, fim: Long): Flow<List<Compra>>
}
