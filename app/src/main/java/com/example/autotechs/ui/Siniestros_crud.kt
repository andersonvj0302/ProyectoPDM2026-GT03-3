package com.example.autotechs.ui

import android.widget.ArrayAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.local.entity.ClienteEntity
import com.example.autotechs.data.repository.RecepcionRepository
import com.example.autotechs.data.repository.ClienteRepository
import com.example.autotechs.databinding.ActivitySiniestrosCrudBinding
import kotlinx.coroutines.launch

class Siniestros_crud : AppCompatActivity() {

    private lateinit var binding: ActivitySiniestrosCrudBinding
    private lateinit var viewModel: SiniestrosViewModel
    private var listaClientes: List<ClienteEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiniestrosCrudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración manual del ViewModel (con Hilt por ahora para mantenerlo simple)
        val database = AppDatabase.getDatabase(this)
        val repository = RecepcionRepository(database.recepcionDao())
        val clienteRepository = ClienteRepository(database.clienteDao())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SiniestrosViewModel(repository, clienteRepository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[SiniestrosViewModel::class.java]

        // Botón atrás
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Cargar clientes en el Spinner
        lifecycleScope.launch {
            viewModel.clientes.collect { clientes ->
                listaClientes = clientes
                val nombres = clientes.map { it.nombre }
                val adapter = ArrayAdapter(this@Siniestros_crud, android.R.layout.simple_spinner_item, nombres)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spClientes.adapter = adapter
            }
        }

        // Listener del botón guardar
        binding.btnGuardarSiniestro.setOnClickListener {
            val vin = binding.etVin.text.toString()
            val placa = binding.etPlaca.text.toString()
            val marca = binding.etMarca.text.toString()
            val modelo = binding.etModelo.text.toString()
            val posCliente = binding.spClientes.selectedItemPosition
            val dEstructural = binding.cbDanoEstructural.isChecked
            val dEstetico = binding.cbDanoEstetico.isChecked
            val dMecanico = binding.cbDanoMecanico.isChecked

            if (vin.isBlank() || placa.isBlank() || posCliente < 0) {
                Toast.makeText(this, "VIN, Placa y Cliente son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val clienteId = listaClientes[posCliente].id

            viewModel.registrarSiniestro(
                vin, placa, marca, modelo, clienteId, dEstructural, dEstetico, dMecanico
            )
        }

        // Observar los estados de la UI
        lifecycleScope.launch {
            viewModel.registroState.collect { state ->
                when (state) {
                    is RegistroState.Idle -> { /* Nada */ }
                    is RegistroState.Loading -> {
                        binding.btnGuardarSiniestro.isEnabled = false
                        binding.btnGuardarSiniestro.text = "Guardando..."
                    }
                    is RegistroState.Success -> {
                        binding.btnGuardarSiniestro.isEnabled = true
                        binding.btnGuardarSiniestro.text = "REGISTRAR RECEPCIÓN"
                        Toast.makeText(this@Siniestros_crud, state.message, Toast.LENGTH_LONG).show()
                        finish() // Cerrar actividad
                    }
                    is RegistroState.Error -> {
                        binding.btnGuardarSiniestro.isEnabled = true
                        binding.btnGuardarSiniestro.text = "REGISTRAR RECEPCIÓN"
                        Toast.makeText(this@Siniestros_crud, state.error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
