package br.edu.iftm.controlegastos.util

import java.math.BigDecimal

/**
 * Converte o texto digitado pelo usuário em centavos, com validação rigorosa.
 *
 * Aceita: "25", "25,90", "25.90", "1.200,50", "R$ 30,00"
 * Rejeita: "", "-10", "abc", "12,345", "0", "1,2,3"
 */
object ValidadorValor {

    /** Maior valor aceito: R$ 1.000.000,00 */
    const val VALOR_MAXIMO_CENTAVOS = 100_000_000L

    sealed interface Resultado {
        data class Valido(val centavos: Long) : Resultado
        data class Invalido(val mensagem: String) : Resultado
        data object Vazio : Resultado
    }

    private val formatoNumero = Regex("""^\d+(\.\d+)?$""")
    private val formatoMilhar = Regex("""^\d{1,3}(\.\d{3})+$""")

    fun validar(textoOriginal: String): Resultado {
        var texto = textoOriginal.trim().replace("R$", "").replace(" ", "").replace(" ", "")

        if (texto.isEmpty()) return Resultado.Vazio
        if (texto.contains('-')) return Resultado.Invalido("O valor não pode ser negativo.")

        texto = normalizarSeparadores(texto)
            ?: return Resultado.Invalido("Digite um número válido (ex.: 25,90).")

        if (!formatoNumero.matches(texto)) {
            return Resultado.Invalido("Digite apenas números (ex.: 25,90).")
        }
        val casasDecimais = texto.substringAfter('.', "").length
        if (casasDecimais > 2) return Resultado.Invalido("Use no máximo 2 casas decimais.")

        val centavos = BigDecimal(texto).movePointRight(2).toLong()
        return when {
            centavos <= 0 -> Resultado.Invalido("O valor deve ser maior que zero.")
            centavos > VALOR_MAXIMO_CENTAVOS -> Resultado.Invalido("Valor muito alto (máx. R$ 1.000.000,00).")
            else -> Resultado.Valido(centavos)
        }
    }

    /**
     * Padroniza para usar "." como separador decimal e nenhum separador de milhar.
     * Retorna null se a combinação de separadores não fizer sentido.
     */
    private fun normalizarSeparadores(texto: String): String? {
        val temVirgula = texto.contains(',')
        val temPonto = texto.contains('.')
        return when {
            // "1.200,50" -> ponto é milhar, vírgula é decimal
            temVirgula && temPonto -> {
                val parteInteira = texto.substringBefore(',')
                if (texto.count { it == ',' } > 1 || !formatoMilhar.matches(parteInteira)) null
                else texto.replace(".", "").replace(',', '.')
            }
            // "25,90"
            temVirgula -> if (texto.count { it == ',' } > 1) null else texto.replace(',', '.')
            // "1.200.000" -> só milhar
            temPonto && texto.count { it == '.' } > 1 ->
                if (formatoMilhar.matches(texto)) texto.replace(".", "") else null
            // "25.90" ou "25"
            else -> texto
        }
    }
}
