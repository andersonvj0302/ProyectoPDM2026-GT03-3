package com.example.autotechs.ui

import com.example.autotechs.R

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.local.entity.ClienteEntity
import com.example.autotechs.data.remote.RetrofitClient
import com.example.autotechs.data.repository.ClienteRepository
import com.example.autotechs.databinding.ActivityClientesEditarBinding
import kotlinx.coroutines.launch

class Clientes_editar : AppCompatActivity() {

    private lateinit var binding: ActivityClientesEditarBinding
    private lateinit var viewModel: ClienteViewModel
    private var clienteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientesEditarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        loadClienteData()

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnActualizar.setOnClickListener {
            val nombre = binding.etNombre.text.toString()
            val dui = binding.etDui.text.toString()
            val telefono = binding.etTelefono.text.toString()
            val email = binding.etEmail.text.toString()

            if (nombre.isBlank() || dui.isBlank() || telefono.isBlank()) {
                Toast.makeText(this, "Por favor complete los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.guardarCliente(nombre, dui, telefono, email, clienteId)
        }

        binding.btnBorrar.setOnClickListener {
            mostrarConfirmacionEliminar()
        }

        observeViewModel()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val repository = ClienteRepository(database.clienteDao(), RetrofitClient.apiService)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ClienteViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[ClienteViewModel::class.java]
    }

    private fun loadClienteData() {
        clienteId = intent.getIntExtra("CLIENTE_ID", -1)
        binding.etNombre.setText(intent.getStringExtra("CLIENTE_NOMBRE"))
        binding.etDui.setText(intent.getStringExtra("CLIENTE_DUI"))
        binding.etTelefono.setText(intent.getStringExtra("CLIENTE_TELEFONO"))
        binding.etEmail.setText(intent.getStringExtra("CLIENTE_EMAIL"))
        
        if (clienteId == -1) {
            Toast.makeText(this, "Error al cargar datos del cliente", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun mostrarConfirmacionEliminar() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Cliente")
            .setMessage("¿Estás seguro de que deseas eliminar este cliente? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                val cliente = ClienteEntity(
                    id = clienteId,
                    nombre = binding.etNombre.text.toString(),
                    dui = binding.etDui.text.toString(),
                    telefono = binding.etTelefono.text.toString(),
                    email = binding.etEmail.text.toString()
                )
                viewModel.eliminarCliente(cliente)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.operacionState.collect { state ->
                when (state) {
                    is OperacionState.Loading -> {
                        binding.btnActualizar.isEnabled = false
                        binding.btnBorrar.isEnabled = false
                    }
                    is OperacionState.Success -> {
                        Toast.makeText(this@Clientes_editar, state.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is OperacionState.Error -> {
                        binding.btnActualizar.isEnabled = true
                        binding.btnBorrar.isEnabled = true
                        Toast.makeText(this@Clientes_editar, state.error, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }
}

