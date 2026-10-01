package com.example.conversormoedas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CotacoesTest {

    private val cotacoes = Cotacoes(
        porDolar = mapOf("USD" to 1.0, "BRL" to 5.0, "EUR" to 0.8),
        atualizadoEm = 0L,
    )

    @Test
    fun converteDolarParaReal() {
        assertEquals(50.0, cotacoes.converter(10.0, Moeda.USD, Moeda.BRL), 1e-9)
    }

    @Test
    fun converteEntreMoedasQueNaoSaoDolar() {
        // 1 EUR = 1.25 USD = 6.25 BRL
        assertEquals(6.25, cotacoes.taxa(Moeda.EUR, Moeda.BRL), 1e-9)
    }

    @Test
    fun mesmaMoedaTemTaxaUm() {
        assertEquals(1.0, cotacoes.taxa(Moeda.BRL, Moeda.BRL), 1e-9)
    }

    @Test
    fun cotacoesAproximadasTemTodasAsMoedas() {
        assertTrue(Cotacoes.APROXIMADAS.temTodas())
    }

    @Test
    fun leValoresDigitados() {
        assertEquals(10.5, lerValor("10,5")!!, 1e-9)
        assertEquals(10.5, lerValor("10.5")!!, 1e-9)
        assertEquals(1234.56, lerValor("1.234,56")!!, 1e-9)
        assertNull(lerValor(""))
        assertNull(lerValor("1,2,3"))
    }
}
