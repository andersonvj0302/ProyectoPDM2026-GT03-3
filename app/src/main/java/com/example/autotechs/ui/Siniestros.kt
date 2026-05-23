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
import com.example.autotechs.data.repository.RecepcionRepository
import com.example.autotechs.data.repository.ClienteRepository
import com.example.autotechs.databinding.ActivitySiniestrosBinding
import kotlinx.coroutines.launch

class Siniestros : AppCompatActivity() {

    private lateinit var binding: ActivitySiniestrosBinding
    private lateinit var viewModel: SiniestrosViewModel
    private lateinit var adapter: SiniestroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiniestrosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar la barra superior
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Configuración manual del ViewModel
        val database = AppDatabase.getDatabase(this)
        val apiService = RetrofitClient.apiService
        val repository = RecepcionRepository(database.recepcionDao(), apiService)
        val clienteRepository = ClienteRepository(database.clienteDao(), apiService)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SiniestrosViewModel(repository, clienteRepository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[SiniestrosViewModel::class.java]

        // Configurar RecyclerView
        adapter = SiniestroAdapter { siniestro ->
            // Navegar al Monitor de Taller pasando el expedienteId
            val intent = Intent(this, TallerMonitorActivity::class.java).apply {
                putExtra("expedienteId", siniestro.id)
            }
            startActivity(intent)
        }
        binding.rvSiniestros.layoutManager = LinearLayoutManager(this)
        binding.rvSiniestros.adapter = adapter

        // Acción al hacer clic en Crear Nuevo
        binding.btnCrearNuevo.setOnClickListener {
            val intent = Intent(this, Siniestros_crud::class.java)
            startActivity(intent)
        }

        // Observar datos del ViewModel
        lifecycleScope.launch {
            viewModel.siniestros.collect { lista ->
                adapter.submitList(lista)
            }
        }
    }
}
