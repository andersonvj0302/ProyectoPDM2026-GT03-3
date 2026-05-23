package com.example.autotechs.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.remote.RetrofitClient
import com.example.autotechs.data.repository.MaterialRepository
import com.example.autotechs.databinding.ActivityInventarioCrudBinding

class Inventario_crud : AppCompatActivity() {

    private lateinit var binding: ActivityInventarioCrudBinding
    private lateinit var viewModel: MaterialViewModel

    private val fasesArray = arrayOf(
        "1. Desarme",
        "2. Enderezado",
        "3. Preparación",
        "4. Pintura",
        "5. Secado y Pulido",
        "6. Armado y Control"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventarioCrudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar la barra superior
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Configuración manual del ViewModel
        val database = AppDatabase.getDatabase(this)
        val repository = MaterialRepository(database.materialDao(), RetrofitClient.apiService)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MaterialViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[MaterialViewModel::class.java]

        // Configurar Spinner de fases
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fasesArray)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spFases.adapter = spinnerAdapter

        // Guardar Material
        binding.btnGuardar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val cantidadStr = binding.etCantidad.text.toString().trim()
            val costoUnitarioStr = binding.etCostoUnitario.text.toString().trim()
            val expedienteIdStr = binding.etExpedienteId.text.toString().trim()
            val faseIndex = binding.spFases.selectedItemPosition + 1 // Las fases son 1-indexed

            if (nombre.isEmpty() || cantidadStr.isEmpty() || costoUnitarioStr.isEmpty() || expedienteIdStr.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cantidad = cantidadStr.toIntOrNull()
            val costoUnitario = costoUnitarioStr.toDoubleOrNull()
            val expedienteId = expedienteIdStr.toIntOrNull()

            if (cantidad == null || costoUnitario == null || expedienteId == null) {
                Toast.makeText(this, "Datos numéricos inválidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Insertar en base de datos Room
            viewModel.insertMaterial(nombre, cantidad, costoUnitario, expedienteId, faseIndex)
            Toast.makeText(this, "Material registrado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
