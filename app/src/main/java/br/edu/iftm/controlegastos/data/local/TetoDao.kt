package br.edu.iftm.controlegastos.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import br.edu.iftm.controlegastos.data.model.Categoria
import br.edu.iftm.controlegastos.data.model.Teto
import kotlinx.coroutines.flow.Flow

@Dao
interface TetoDao {

    /** Insere ou atualiza (se a categoria já tiver teto). */
    @Upsert
    suspend fun salvar(tetos: List<Teto>)

    @Query("DELETE FROM tetos WHERE userId = :userId AND categoria IN (:categorias)")
    suspend fun remover(userId: String, categorias: List<Categoria>)

    @Query("SELECT * FROM tetos WHERE userId = :userId")
    fun tetosDoUsuario(userId: String): Flow<List<Teto>>
}
