package com.example.autotechs.ui

import com.example.autotechs.R

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.local.entity.ClienteEntity
import com.example.autotechs.data.repository.VehiculoRepository
import com.example.autotechs.data.repository.ClienteRepository
import com.example.autotechs.databinding.ActivityVehiculosCrudBinding
import kotlinx.coroutines.launch

class Vehiculos_crud : AppCompatActivity() {

    private lateinit var binding: ActivityVehiculosCrudBinding
    private lateinit var viewModel: VehiculoViewModel
    private var listaClientes: List<ClienteEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehiculosCrudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnGuardar.setOnClickListener {
            val vin = binding.etVin.text.toString()
            val placa = binding.etPlaca.text.toString()
            val marca = binding.etMarca.text.toString()
            val modelo = binding.etModelo.text.toString()
            val posCliente = binding.spClientes.selectedItemPosition

            if (vin.isBlank() || placa.isBlank() || posCliente < 0) {
                Toast.makeText(this, "Por favor complete los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cliente = listaClientes[posCliente]
            viewModel.guardarVehiculo(vin, placa, marca, modelo, cliente.id)
        }

        observeViewModel()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val vehiculoRepository = VehiculoRepository(database.vehiculoDao())
        val clienteRepository = ClienteRepository(database.clienteDao())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return VehiculoViewModel(vehiculoRepository, clienteRepository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[VehiculoViewModel::class.java]
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.clientes.collect { clientes ->
                listaClientes = clientes
                val nombres = clientes.map { it.nombre }
                val adapter = ArrayAdapter(this@Vehiculos_crud, android.R.layout.simple_spinner_item, nombres)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spClientes.adapter = adapter
            }
        }

        lifecycleScope.launch {
            viewModel.operacionState.collect { state ->
                when (state) {
                    is OperacionState.Loading -> {
                        binding.btnGuardar.isEnabled = false
                        binding.btnGuardar.text = "Guardando..."
                    }
                    is OperacionState.Success -> {
                        Toast.makeText(this@Vehiculos_crud, state.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is OperacionState.Error -> {
                        binding.btnGuardar.isEnabled = true
                        binding.btnGuardar.text = "GUARDAR VEHÍCULO"
                        Toast.makeText(this@Vehiculos_crud, state.error, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }
}

