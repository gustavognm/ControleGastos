package br.edu.iftm.controlegastos

import br.edu.iftm.controlegastos.util.Consumo
import br.edu.iftm.controlegastos.util.NivelAlerta
import br.edu.iftm.controlegastos.util.ValidadorCampos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ConsumoTest {

    @Test fun semTeto() = assertEquals(NivelAlerta.SEM_TETO, Consumo.nivel(500, null))
    @Test fun verdeAbaixoDe75() = assertEquals(NivelAlerta.VERDE, Consumo.nivel(7_400, 10_000))
    @Test fun amareloEm75() = assertEquals(NivelAlerta.AMARELO, Consumo.nivel(7_500, 10_000))
    @Test fun amareloNoLimiteExato() = assertEquals(NivelAlerta.AMARELO, Consumo.nivel(10_000, 10_000))
    @Test fun vermelhoAoEstourar() = assertEquals(NivelAlerta.VERMELHO, Consumo.nivel(10_001, 10_000))

    @Test fun emailValido() = assertNull(ValidadorCampos.erroEmail("aluno@iftm.edu.br"))
    @Test fun emailInvalido() = assertNotNull(ValidadorCampos.erroEmail("aluno@"))
    @Test fun senhaCurta() = assertNotNull(ValidadorCampos.erroSenha("123"))
    @Test fun senhaOk() = assertNull(ValidadorCampos.erroSenha("123456"))
}
