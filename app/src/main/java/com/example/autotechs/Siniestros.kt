package com.example.autotechs

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class Siniestros : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_siniestros)

        // Para pasar a la pantalla de Crud
        // 1. Referencia al botón usando el ID
        val btnCrear = findViewById<View>(R.id.btnCrearNuevo)

        // 2. Acción al hacer clic
        btnCrear.setOnClickListener {
            val intent = Intent(this, Siniestros_crud::class.java)
            startActivity(intent)
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}