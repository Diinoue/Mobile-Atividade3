package com.example.grimorio

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
    }

    fun abrirPersonagem(view: View) {
        startActivity(Intent(this, PersonagemActivity::class.java))
    }

    fun abrirCriatura(view: View) {
        startActivity(Intent(this, CriaturaActivity::class.java))
    }
}
