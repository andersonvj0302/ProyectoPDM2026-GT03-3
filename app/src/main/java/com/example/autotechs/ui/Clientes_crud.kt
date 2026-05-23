package com.example.autotechs.ui

import com.example.autotechs.R

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.remote.RetrofitClient
import com.example.autotechs.data.repository.ClienteRepository
import com.example.autotechs.databinding.ActivityClientesCrudBinding
import kotlinx.coroutines.launch

class Clientes_crud : AppCompatActivity() {

    private lateinit var binding: ActivityClientesCrudBinding
    private lateinit var viewModel: ClienteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientesCrudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnGuardar.setOnClickListener {
            val nombre = binding.etNombre.text.toString()
            val dui = binding.etDui.text.toString()
            val telefono = binding.etTelefono.text.toString()
            val email = binding.etEmail.text.toString()

            if (nombre.isBlank() || dui.isBlank() || telefono.isBlank()) {
                Toast.makeText(this, "Por favor complete los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.guardarCliente(nombre, dui, telefono, email)
        }

        observeViewModel()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        // Inyectar ApiService para sincronizar operaciones CRUD con la API REST
        val repository = ClienteRepository(database.clienteDao(), RetrofitClient.apiService)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ClienteViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[ClienteViewModel::class.java]
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.operacionState.collect { state ->
                when (state) {
                    is OperacionState.Loading -> {
                        binding.btnGuardar.isEnabled = false
                        binding.btnGuardar.text = "Guardando..."
                    }
                    is OperacionState.Success -> {
                        Toast.makeText(this@Clientes_crud, state.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is OperacionState.Error -> {
                        binding.btnGuardar.isEnabled = true
                        binding.btnGuardar.text = "GUARDAR CLIENTE"
                        Toast.makeText(this@Clientes_crud, state.error, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }
}

