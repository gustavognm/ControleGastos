package br.edu.iftm.controlegastos.util

import br.edu.iftm.controlegastos.data.model.Categoria

/**
 * Nível de alerta de uma categoria, usado para escolher a cor da barra:
 * VERDE (tranquilo) -> AMARELO (perto do limite) -> VERMELHO (estourou).
 */
enum class NivelAlerta { SEM_TETO, VERDE, AMARELO, VERMELHO }

/** Quanto já foi gasto em uma categoria comparado ao seu teto. */
data class ConsumoCategoria(
    val categoria: Categoria,
    val gastoCentavos: Long,
    val tetoCentavos: Long?,
) {
    /** 0.0 = nada gasto, 1.0 = teto atingido, >1.0 = estourou. */
    val fracao: Float
        get() = if (tetoCentavos == null || tetoCentavos <= 0) 0f
        else gastoCentavos.toFloat() / tetoCentavos

    val nivel: NivelAlerta get() = Consumo.nivel(gastoCentavos, tetoCentavos)

    /** Positivo = quanto ainda pode gastar; negativo = quanto passou do teto. */
    val restanteCentavos: Long? get() = tetoCentavos?.minus(gastoCentavos)
}

object Consumo {

    /** A partir de 75% do teto, a barra fica amarela. */
    const val LIMIAR_AMARELO = 0.75

    fun nivel(gasto: Long, teto: Long?): NivelAlerta {
        if (teto == null || teto <= 0) return NivelAlerta.SEM_TETO
        val fracao = gasto.toDouble() / teto
        return when {
            fracao > 1.0 -> NivelAlerta.VERMELHO
            fracao >= LIMIAR_AMARELO -> NivelAlerta.AMARELO
            else -> NivelAlerta.VERDE
        }
    }
}
