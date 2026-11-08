package br.edu.iftm.controlegastos

import br.edu.iftm.controlegastos.util.ValidadorValor
import br.edu.iftm.controlegastos.util.ValidadorValor.Resultado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadorValorTest {

    private fun centavos(texto: String) = (ValidadorValor.validar(texto) as Resultado.Valido).centavos

    @Test fun aceitaInteiro() = assertEquals(2500L, centavos("25"))
    @Test fun aceitaVirgula() = assertEquals(2590L, centavos("25,90"))
    @Test fun aceitaPonto() = assertEquals(2590L, centavos("25.90"))
    @Test fun aceitaUmaCasa() = assertEquals(2550L, centavos("25,5"))
    @Test fun aceitaMilharBrasileiro() = assertEquals(120050L, centavos("1.200,50"))
    @Test fun aceitaMilharSemDecimal() = assertEquals(100_000_000L, centavos("1.000.000"))
    @Test fun aceitaPrefixoReal() = assertEquals(3000L, centavos("R$ 30,00"))

    @Test fun vazio() = assertEquals(Resultado.Vazio, ValidadorValor.validar("   "))

    @Test fun rejeitaNegativo() = assertInvalido("-10")
    @Test fun rejeitaLetras() = assertInvalido("abc")
    @Test fun rejeitaMisturaLetras() = assertInvalido("12a")
    @Test fun rejeitaZero() = assertInvalido("0,00")
    @Test fun rejeitaTresCasas() = assertInvalido("12,345")
    @Test fun rejeitaDuasVirgulas() = assertInvalido("1,2,3")
    @Test fun rejeitaMilharMalFormado() = assertInvalido("12.34,50")
    @Test fun rejeitaValorGigante() = assertInvalido("1000000,01")

    private fun assertInvalido(texto: String) =
        assertTrue("\"$texto\" deveria ser inválido", ValidadorValor.validar(texto) is Resultado.Invalido)
}
