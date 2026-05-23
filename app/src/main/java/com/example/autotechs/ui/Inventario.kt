package com.example.autotechs.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.remote.RetrofitClient
import com.example.autotechs.data.repository.MaterialRepository
import com.example.autotechs.databinding.ActivityInventarioBinding
import kotlinx.coroutines.launch

class Inventario : AppCompatActivity() {

    private lateinit var binding: ActivityInventarioBinding
    private lateinit var viewModel: MaterialViewModel
    private lateinit var adapter: MaterialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar la barra superior
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Configuración manual de ViewModel
        val database = AppDatabase.getDatabase(this)
        val repository = MaterialRepository(database.materialDao(), RetrofitClient.apiService)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MaterialViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[MaterialViewModel::class.java]

        // Configurar RecyclerView
        adapter = MaterialAdapter { material ->
            // Ir a la vista de editar pasándole el materialId
            val intent = Intent(this, Inventario_editar::class.java).apply {
                putExtra("materialId", material.id)
            }
            startActivity(intent)
        }
        binding.rvMateriales.layoutManager = LinearLayoutManager(this)
        binding.rvMateriales.adapter = adapter

        // Ir a crear nuevo material
        binding.btnCrearNuevo.setOnClickListener {
            val intent = Intent(this, Inventario_crud::class.java)
            startActivity(intent)
        }

        // Observar la lista de materiales desde Room
        lifecycleScope.launch {
            viewModel.materiales.collect { lista ->
                adapter.submitList(lista)
            }
        }
    }
}
