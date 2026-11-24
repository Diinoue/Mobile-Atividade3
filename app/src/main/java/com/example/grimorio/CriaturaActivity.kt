package com.example.grimorio

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CriaturaActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var btnBuscar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var ivCriatura: ImageView
    private lateinit var tvNome: TextView
    private lateinit var tvAviso: TextView
    private lateinit var hogwartsApi: HogwartsApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criatura)

        etNome = findViewById(R.id.etNome)
        btnBuscar = findViewById(R.id.btnBuscar)
        progressBar = findViewById(R.id.progressBar)
        ivCriatura = findViewById(R.id.ivCriatura)
        tvNome = findViewById(R.id.tvNome)
        tvAviso = findViewById(R.id.tvAviso)
        hogwartsApi = criarApi()
    } // Fim onCreate()

    fun buscarCriatura(view: View) {
        // Valida antes de consultar a API
        val nome = etNome.text.toString().trim()
        val erro = validar(nome)
        if (erro != null) {
            etNome.error = erro
            return
        }

        btnBuscar.isEnabled = false
        progressBar.visibility = View.VISIBLE
        ivCriatura.visibility = View.GONE
        tvNome.visibility = View.GONE
        tvAviso.visibility = View.GONE

        // Corrotina presa ao ciclo de vida da activity
        lifecycleScope.launch {
            try {
                // Request em segundo plano (IO)
                val lista = withContext(Dispatchers.IO) {
                    hogwartsApi.getCriaturas(nome).dados.orEmpty()
                }

                // De volta à Main: prefere o nome exato, senão usa o primeiro
                val criatura = lista.firstOrNull { it.nome.equals(nome, ignoreCase = true) }
                    ?: lista.firstOrNull()

                if (criatura == null) {
                    mostrarMensagem("Nenhuma criatura encontrada")
                } else {
                    mostrarCriatura(criatura.nome ?: "Não informado", criatura.imagem)
                }
            } catch (e: Exception) {
                Log.e("CriaturaActivity", "Erro ao buscar criatura", e)
                mostrarMensagem("Erro ao buscar. Verifique sua conexão e tente novamente.")
            }
        } // Fim da corrotina
    } // Fim buscarCriatura

    // O ProgressBar só some quando a imagem termina de carregar (ou falha)
    private fun mostrarCriatura(nome: String, urlImagem: String?) {
        tvNome.text = nome
        tvNome.visibility = View.VISIBLE
        ivCriatura.visibility = View.VISIBLE

        if (urlImagem.isNullOrBlank()) {
            imagemIndisponivel()
            return
        }
        // Coil carrega a imagem fora da thread principal
        ivCriatura.load(urlImagem) {
            listener(
                onSuccess = { _, _ -> terminarBusca() },
                onError = { _, _ -> imagemIndisponivel() }
            )
        }
    }

    // Imagem substituta do próprio app + texto "Imagem indisponível"
    private fun imagemIndisponivel() {
        ivCriatura.setImageResource(R.drawable.img_indisponivel)
        tvAviso.text = "Imagem indisponível"
        tvAviso.visibility = View.VISIBLE
        terminarBusca()
    }

    private fun mostrarMensagem(texto: String) {
        tvNome.text = texto
        tvNome.visibility = View.VISIBLE
        terminarBusca()
    }

    // Esconde o ProgressBar e libera o botão para uma nova busca
    private fun terminarBusca() {
        progressBar.visibility = View.GONE
        btnBuscar.isEnabled = true
    }
} // Fim da activity
