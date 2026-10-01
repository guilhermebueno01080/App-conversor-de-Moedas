package com.example.conversormoedas

/**
 * Cotações de várias moedas em relação ao dólar (USD = 1).
 *
 * @param porDolar quanto 1 dólar vale em cada moeda, ex: "BRL" -> 5.40
 * @param atualizadoEm quando as cotações foram publicadas (milissegundos), ou null se são aproximadas
 */
data class Cotacoes(val porDolar: Map<String, Double>, val atualizadoEm: Long?) {

    /** Quanto 1 unidade de [de] vale em [para]. */
    fun taxa(de: Moeda, para: Moeda): Double {
        val valorDe = porDolar[de.codigo] ?: error("Sem cotação para ${de.codigo}")
        val valorPara = porDolar[para.codigo] ?: error("Sem cotação para ${para.codigo}")
        return valorPara / valorDe
    }

    fun converter(valor: Double, de: Moeda, para: Moeda): Double = valor * taxa(de, para)

    fun temTodas(): Boolean = Moeda.entries.all { porDolar.containsKey(it.codigo) }

    companion object {
        /** Valores aproximados, usados só na primeira vez que o app abre sem internet. */
        val APROXIMADAS = Cotacoes(
            porDolar = mapOf(
                "USD" to 1.0,
                "BRL" to 5.40,
                "EUR" to 0.86,
                "GBP" to 0.75,
                "ARS" to 1380.0,
                "CAD" to 1.39,
                "CHF" to 0.80,
                "JPY" to 148.0,
                "CNY" to 7.12,
            ),
            atualizadoEm = null,
        )
    }
}

/** Transforma o texto digitado ("1.234,56", "10,5", "10.5") em número. */
fun lerValor(texto: String): Double? {
    val limpo = texto.trim()
    if (limpo.isEmpty()) return null
    val normalizado = if (limpo.contains(',')) {
        limpo.replace(".", "").replace(',', '.')
    } else {
        limpo
    }
    return normalizado.toDoubleOrNull()
}
