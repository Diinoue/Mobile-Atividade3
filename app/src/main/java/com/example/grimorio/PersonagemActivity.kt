package com.example.grimorio

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PersonagemActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var btnBuscar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvResultado: TextView
    private lateinit var hogwartsApi: HogwartsApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personagem)

        etNome = findViewById(R.id.etNome)
        btnBuscar = findViewById(R.id.btnBuscar)
        progressBar = findViewById(R.id.progressBar)
        tvResultado = findViewById(R.id.tvResultado)
        hogwartsApi = criarApi()
    } // Fim onCreate()

    fun buscarPersonagem(view: View) {
        // Valida antes de consultar a API
        val nome = etNome.text.toString().trim()
        val erro = validar(nome)
        if (erro != null) {
            etNome.error = erro
            return
        }

        btnBuscar.isEnabled = false
        progressBar.visibility = View.VISIBLE
        tvResultado.visibility = View.GONE

        // Corrotina presa ao ciclo de vida da activity
        lifecycleScope.launch {
            try {
                // Request em segundo plano (IO)
                val lista = withContext(Dispatchers.IO) {
                    hogwartsApi.getPersonagens(nome).dados.orEmpty()
                }

                // De volta à Main: atualiza a tela com o primeiro resultado
                val personagem = lista.firstOrNull()
                tvResultado.text = if (personagem == null) {
                    "Nenhum personagem encontrado"
                } else {
                    "Nome: ${personagem.nome ?: "Não informado"}\n" +
                        "Espécie: ${personagem.especie ?: "Não informado"}\n" +
                        "Casa: ${personagem.casa ?: "Não informado"}"
                }
            } catch (e: Exception) {
                Log.e("PersonagemActivity", "Erro ao buscar personagem", e)
                tvResultado.text = "Erro ao buscar. Verifique sua conexão e tente novamente."
            }
            btnBuscar.isEnabled = true
            progressBar.visibility = View.GONE
            tvResultado.visibility = View.VISIBLE
        } // Fim da corrotina
    } // Fim buscarPersonagem
} // Fim da activity
