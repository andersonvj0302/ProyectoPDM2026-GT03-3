package com.example.autotechs.ui

import com.example.autotechs.R

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.local.entity.VehiculoEntity
import com.example.autotechs.data.local.entity.ClienteEntity
import com.example.autotechs.data.remote.RetrofitClient
import com.example.autotechs.data.repository.VehiculoRepository
import com.example.autotechs.data.repository.ClienteRepository
import com.example.autotechs.databinding.ActivityVehiculosEditarBinding
import kotlinx.coroutines.launch

class Vehiculos_editar : AppCompatActivity() {

    private lateinit var binding: ActivityVehiculosEditarBinding
    private lateinit var viewModel: VehiculoViewModel
    private var vin: String = ""
    private var listaClientes: List<ClienteEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehiculosEditarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        loadVehiculoData()

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnActualizar.setOnClickListener {
            val placa = binding.etPlaca.text.toString()
            val marca = binding.etMarca.text.toString()
            val modelo = binding.etModelo.text.toString()
            val posCliente = binding.spClientes.selectedItemPosition

            if (placa.isBlank() || posCliente < 0) {
                Toast.makeText(this, "Por favor complete los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cliente = listaClientes[posCliente]
            viewModel.guardarVehiculo(vin, placa, marca, modelo, cliente.id, isEdit = true)
        }

        binding.btnBorrar.setOnClickListener {
            mostrarConfirmacionEliminar()
        }

        observeViewModel()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val apiService = RetrofitClient.apiService
        val vehiculoRepository = VehiculoRepository(database.vehiculoDao(), apiService)
        val clienteRepository = ClienteRepository(database.clienteDao(), apiService)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return VehiculoViewModel(vehiculoRepository, clienteRepository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[VehiculoViewModel::class.java]
    }

    private fun loadVehiculoData() {
        vin = intent.getStringExtra("VEHICULO_VIN") ?: ""
        binding.etVin.setText(vin)
        binding.etPlaca.setText(intent.getStringExtra("VEHICULO_PLACA"))
        binding.etMarca.setText(intent.getStringExtra("VEHICULO_MARCA"))
        binding.etModelo.setText(intent.getStringExtra("VEHICULO_MODELO"))
        
        if (vin.isBlank()) {
            Toast.makeText(this, "Error al cargar datos del vehículo", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun mostrarConfirmacionEliminar() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Vehículo")
            .setMessage("¿Estás seguro de que deseas eliminar este vehículo? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                val vehiculo = VehiculoEntity(
                    vin = vin,
                    placa = binding.etPlaca.text.toString(),
                    marca = binding.etMarca.text.toString(),
                    modelo = binding.etModelo.text.toString(),
                    clienteId = listaClientes[binding.spClientes.selectedItemPosition].id
                )
                viewModel.eliminarVehiculo(vehiculo)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.clientes.collect { clientes ->
                listaClientes = clientes
                val nombres = clientes.map { it.nombre }
                val adapter = ArrayAdapter(this@Vehiculos_editar, android.R.layout.simple_spinner_item, nombres)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spClientes.adapter = adapter
                
                // Seleccionar el dueño actual
                val currentClienteId = intent.getIntExtra("VEHICULO_CLIENTE_ID", -1)
                val index = clientes.indexOfFirst { it.id == currentClienteId }
                if (index != -1) {
                    binding.spClientes.setSelection(index)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.operacionState.collect { state ->
                when (state) {
                    is OperacionState.Loading -> {
                        binding.btnActualizar.isEnabled = false
                        binding.btnBorrar.isEnabled = false
                    }
                    is OperacionState.Success -> {
                        Toast.makeText(this@Vehiculos_editar, state.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is OperacionState.Error -> {
                        binding.btnActualizar.isEnabled = true
                        binding.btnBorrar.isEnabled = true
                        Toast.makeText(this@Vehiculos_editar, state.error, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }
}

