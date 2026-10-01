package com.example.conversormoedas

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Currency
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var repositorio: CotacaoRepositorio
    private lateinit var cotacoes: Cotacoes

    private var de = Moeda.EUR
    private var para = Moeda.USD

    private lateinit var campoValor: TextInputLayout
    private lateinit var edValor: TextInputEditText
    private lateinit var listaDe: MaterialAutoCompleteTextView
    private lateinit var listaPara: MaterialAutoCompleteTextView
    private lateinit var txResultado: TextView
    private lateinit var txTaxa: TextView
    private lateinit var txAtualizacao: TextView
    private lateinit var btAtualizar: MaterialButton

    private val brasil = Locale.forLanguageTag("pt-BR")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        campoValor = findViewById(R.id.campo_valor)
        edValor = findViewById(R.id.ed_valor)
        listaDe = findViewById(R.id.lista_de)
        listaPara = findViewById(R.id.lista_para)
        txResultado = findViewById(R.id.tx_resultado)
        txTaxa = findViewById(R.id.tx_taxa)
        txAtualizacao = findViewById(R.id.tx_atualizacao)
        btAtualizar = findViewById(R.id.bt_atualizar)

        savedInstanceState?.let {
            de = Moeda.valueOf(it.getString(CHAVE_DE, de.name))
            para = Moeda.valueOf(it.getString(CHAVE_PARA, para.name))
        }

        val rotulos = Moeda.entries.map { it.rotulo }.toTypedArray()
        listaDe.setSimpleItems(rotulos)
        listaPara.setSimpleItems(rotulos)
        listaDe.setOnItemClickListener { _, _, posicao, _ ->
            de = Moeda.entries[posicao]
            atualizarTela()
        }
        listaPara.setOnItemClickListener { _, _, posicao, _ ->
            para = Moeda.entries[posicao]
            atualizarTela()
        }

        edValor.doAfterTextChanged { atualizarTela() }
        findViewById<View>(R.id.bt_inverter).setOnClickListener { inverter(it) }
        btAtualizar.setOnClickListener { baixarCotacoes() }

        repositorio = CotacaoRepositorio(this)
        cotacoes = repositorio.salvas()
        atualizarTela()
        baixarCotacoes()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CHAVE_DE, de.name)
        outState.putString(CHAVE_PARA, para.name)
    }

    private fun baixarCotacoes() {
        btAtualizar.isEnabled = false
        txAtualizacao.setText(R.string.atualizando)
        lifecycleScope.launch {
            val novas = runCatching { repositorio.baixar() }.getOrNull()
            if (novas != null) cotacoes = novas
            btAtualizar.isEnabled = true
            atualizarTela(semInternet = novas == null)
        }
    }

    private fun inverter(botao: View) {
        de = para.also { para = de }
        botao.animate().rotationBy(180f).setDuration(250).start()
        atualizarTela()
    }

    private fun atualizarTela(semInternet: Boolean = false) {
        listaDe.setText(de.rotulo, false)
        listaPara.setText(para.rotulo, false)
        campoValor.prefixText = simbolo(de)

        val texto = edValor.text?.toString().orEmpty()
        val valor = lerValor(texto)
        campoValor.error = if (texto.isNotBlank() && valor == null) getString(R.string.valor_invalido) else null

        txResultado.text = formatar(cotacoes.converter(valor ?: 0.0, de, para), para)
        txTaxa.text = getString(
            R.string.taxa,
            formatar(1.0, de),
            formatar(cotacoes.taxa(de, para), para, casasMinimas = 4),
        )
        txAtualizacao.text = textoAtualizacao(semInternet)
    }

    private fun textoAtualizacao(semInternet: Boolean): String {
        val data = cotacoes.atualizadoEm
            ?: return getString(R.string.cotacao_aproximada)
        val quando = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, brasil)
            .format(Date(data))
        return getString(if (semInternet) R.string.cotacao_salva else R.string.cotacao_atual, quando)
    }

    private fun formatar(valor: Double, moeda: Moeda, casasMinimas: Int = 2): String {
        val formato = NumberFormat.getCurrencyInstance(brasil)
        formato.currency = Currency.getInstance(moeda.codigo)
        formato.minimumFractionDigits = casasMinimas
        formato.maximumFractionDigits = maxOf(casasMinimas, 2)
        return formato.format(valor)
    }

    private fun simbolo(moeda: Moeda): String = Currency.getInstance(moeda.codigo).getSymbol(brasil) + " "

    companion object {
        private const val CHAVE_DE = "de"
        private const val CHAVE_PARA = "para"
    }
}
