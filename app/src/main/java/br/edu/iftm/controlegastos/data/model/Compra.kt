package br.edu.iftm.controlegastos.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Uma compra lançada pelo usuário.
 *
 * O valor é guardado em CENTAVOS (Long) e não em Double, para evitar erros de
 * arredondamento (ex.: 0.1 + 0.2 = 0.30000000000000004 em Double).
 */
@Entity(tableName = "compras", indices = [Index("userId", "data")])
data class Compra(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val valorCentavos: Long,
    val categoria: Categoria,
    val descricao: String,
    val formaPagamento: FormaPagamento,
    /** Momento da compra em milissegundos (System.currentTimeMillis()). */
    val data: Long,
)
