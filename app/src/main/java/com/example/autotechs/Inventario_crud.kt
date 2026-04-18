package com.example.autotechs

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class Inventario_crud : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_inventario_crud)

        // Para ir a la vista del boton editar
        val btnEditar = findViewById<View>(R.id.btnEditar)

        // 2. Acción al hacer clic
        btnEditar.setOnClickListener {
            val intent = Intent(this, Inventario_editar::class.java)
            startActivity(intent)
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)

        // Habilitar el botón de "atrás" en la barra superior
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Regresa a la pantalla anterior (Inicio)
        }
    }
}