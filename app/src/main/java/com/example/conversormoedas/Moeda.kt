package com.example.conversormoedas

enum class Moeda(val codigo: String, val nome: String, val bandeira: String) {
    BRL("BRL", "Real brasileiro", "🇧🇷"),
    USD("USD", "Dólar americano", "🇺🇸"),
    EUR("EUR", "Euro", "🇪🇺"),
    GBP("GBP", "Libra esterlina", "🇬🇧"),
    ARS("ARS", "Peso argentino", "🇦🇷"),
    CAD("CAD", "Dólar canadense", "🇨🇦"),
    CHF("CHF", "Franco suíço", "🇨🇭"),
    JPY("JPY", "Iene japonês", "🇯🇵"),
    CNY("CNY", "Yuan chinês", "🇨🇳");

    /** Texto que aparece na lista de escolha, ex: "🇧🇷 BRL — Real brasileiro". */
    val rotulo: String get() = "$bandeira $codigo — $nome"
}
