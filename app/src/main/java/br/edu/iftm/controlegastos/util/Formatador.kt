package br.edu.iftm.controlegastos.util

import java.text.NumberFormat
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatador {

    private val brasil: Locale = Locale.forLanguageTag("pt-BR")
    private val moeda = NumberFormat.getCurrencyInstance(brasil)
    private val dataHora = DateTimeFormatter.ofPattern("dd/MM 'às' HH:mm", brasil)
    private val mesAno = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", brasil)

    /** 123456 -> "R$ 1.234,56" */
    fun moeda(centavos: Long): String = moeda.format(centavos / 100.0)

    /** 123456 -> "1234,56" (para preencher campos de texto). */
    fun valorParaCampo(centavos: Long): String =
        "%d,%02d".format(centavos / 100, centavos % 100)

    fun dataHora(millis: Long): String =
        dataHora.format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()))

    /** "novembro de 2026" -> "Novembro de 2026" */
    fun mesAno(mes: YearMonth): String =
        mesAno.format(mes).replaceFirstChar { it.titlecase(brasil) }
}
