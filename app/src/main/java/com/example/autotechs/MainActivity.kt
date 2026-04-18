package com.example.autotechs

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.autotechs.R.id.btnIniciarSesion

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnIniciarSesion = findViewById<Button>(btnIniciarSesion)

        btnIniciarSesion.setOnClickListener {
            val formularioIniciarSesion = Intent(this, iniciar_sesion::class.java)
            startActivity(formularioIniciarSesion)
        }

        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)

        btnRegistrarse.setOnClickListener {
            val formularioRegistrarse = Intent(this, registrarse::class.java)
            startActivity(formularioRegistrarse)
        }
    }
}