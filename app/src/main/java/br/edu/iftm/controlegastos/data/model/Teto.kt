package br.edu.iftm.controlegastos.data.model

import androidx.room.Entity

/**
 * Teto (limite) mensal de gastos de UMA categoria para UM usuário.
 * A chave é o par (userId, categoria): cada usuário tem no máximo um teto por categoria.
 */
@Entity(tableName = "tetos", primaryKeys = ["userId", "categoria"])
data class Teto(
    val userId: String,
    val categoria: Categoria,
    val valorCentavos: Long,
)
