package com.example.autotechs.ui

import com.example.autotechs.R

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.repository.VehiculoRepository
import com.example.autotechs.data.repository.ClienteRepository
import com.example.autotechs.databinding.ActivityVehiculosBinding
import kotlinx.coroutines.launch

class Vehiculos : AppCompatActivity() {

    private lateinit var binding: ActivityVehiculosBinding
    private lateinit var viewModel: VehiculoViewModel
    private lateinit var adapter: VehiculoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehiculosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()

        binding.btnCrearNuevo.setOnClickListener {
            val intent = Intent(this, Vehiculos_crud::class.java)
            startActivity(intent)
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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

    private fun setupRecyclerView() {
        adapter = VehiculoAdapter { vehiculo ->
            val intent = Intent(this, Vehiculos_editar::class.java).apply {
                putExtra("VEHICULO_VIN", vehiculo.vin)
                putExtra("VEHICULO_PLACA", vehiculo.placa)
                putExtra("VEHICULO_MARCA", vehiculo.marca)
                putExtra("VEHICULO_MODELO", vehiculo.modelo)
                putExtra("VEHICULO_CLIENTE_ID", vehiculo.clienteId)
            }
            startActivity(intent)
        }
        binding.rvVehiculos.layoutManager = LinearLayoutManager(this)
        binding.rvVehiculos.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.vehiculos.collect { lista ->
                adapter.submitList(lista)
            }
        }
    }
}

