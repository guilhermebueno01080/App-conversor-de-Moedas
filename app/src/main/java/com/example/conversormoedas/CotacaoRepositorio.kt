package com.example.conversormoedas

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Busca as cotações do dia na internet (API gratuita, sem chave) e guarda a última
 * resposta no aparelho para o app funcionar mesmo sem conexão.
 */
class CotacaoRepositorio(context: Context) {

    private val prefs = context.getSharedPreferences("cotacoes", Context.MODE_PRIVATE)

    /** Última cotação salva no aparelho, ou as aproximadas se nunca baixou nenhuma. */
    fun salvas(): Cotacoes =
        prefs.getString(CHAVE_JSON, null)?.let { runCatching { lerJson(it) }.getOrNull() }
            ?: Cotacoes.APROXIMADAS

    /** Baixa as cotações atuais. Lança exceção se não tiver internet ou a resposta vier errada. */
    suspend fun baixar(): Cotacoes = withContext(Dispatchers.IO) {
        val conexao = URL(URL_API).openConnection() as HttpURLConnection
        try {
            conexao.connectTimeout = 10_000
            conexao.readTimeout = 10_000
            val json = conexao.inputStream.bufferedReader().use { it.readText() }
            val cotacoes = lerJson(json)
            prefs.edit { putString(CHAVE_JSON, json) }
            cotacoes
        } finally {
            conexao.disconnect()
        }
    }

    private fun lerJson(json: String): Cotacoes {
        val raiz = JSONObject(json)
        check(raiz.getString("result") == "success") { "Resposta inválida da API" }
        val rates = raiz.getJSONObject("rates")
        val porDolar = Moeda.entries
            .filter { rates.has(it.codigo) }
            .associate { it.codigo to rates.getDouble(it.codigo) }
        val cotacoes = Cotacoes(porDolar, raiz.getLong("time_last_update_unix") * 1000)
        check(cotacoes.temTodas()) { "Faltam moedas na resposta da API" }
        return cotacoes
    }

    companion object {
        private const val URL_API = "https://open.er-api.com/v6/latest/USD"
        private const val CHAVE_JSON = "ultima_resposta"
    }
}
