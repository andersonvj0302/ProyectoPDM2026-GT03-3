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
import com.example.autotechs.data.remote.RetrofitClient
import com.example.autotechs.data.repository.ClienteRepository
import com.example.autotechs.databinding.ActivityClientesBinding
import kotlinx.coroutines.launch

class ClientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientesBinding
    private lateinit var viewModel: ClienteViewModel
    private lateinit var adapter: ClienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()

        binding.btnCrearNuevo.setOnClickListener {
            val intent = Intent(this, Clientes_crud::class.java)
            startActivity(intent)
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        observeViewModel()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        // Se inyecta el ApiService de Retrofit para sincronización con API REST
        val repository = ClienteRepository(database.clienteDao(), RetrofitClient.apiService)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ClienteViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[ClienteViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = ClienteAdapter { cliente ->
            val intent = Intent(this, Clientes_editar::class.java).apply {
                putExtra("CLIENTE_ID", cliente.id)
                putExtra("CLIENTE_NOMBRE", cliente.nombre)
                putExtra("CLIENTE_DUI", cliente.dui)
                putExtra("CLIENTE_TELEFONO", cliente.telefono)
                putExtra("CLIENTE_EMAIL", cliente.email)
            }
            startActivity(intent)
        }
        binding.rvClientes.layoutManager = LinearLayoutManager(this)
        binding.rvClientes.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.clientes.collect { lista ->
                adapter.submitList(lista)
            }
        }
    }
}

