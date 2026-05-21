package com.example.autotechs.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.repository.AdministracionRepository
import com.example.autotechs.databinding.ActivityFacturacionBinding
import kotlinx.coroutines.launch

class Facturacion : AppCompatActivity() {

    private lateinit var binding: ActivityFacturacionBinding
    private lateinit var viewModel: AdministracionViewModel
    private lateinit var adapter: PagoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacturacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar la barra superior
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Configuración manual del ViewModel
        val database = AppDatabase.getDatabase(this)
        val repository = AdministracionRepository(database.administracionDao())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AdministracionViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[AdministracionViewModel::class.java]

        // Configurar RecyclerView
        adapter = PagoAdapter { pago ->
            // Ir a la vista de editar pasándole el pagoId
            val intent = Intent(this, Facturacion_editar::class.java).apply {
                putExtra("pagoId", pago.id)
            }
            startActivity(intent)
        }
        binding.rvFacturas.layoutManager = LinearLayoutManager(this)
        binding.rvFacturas.adapter = adapter

        // Ir a crear nuevo pago (FacturacionActivity)
        binding.btnCrearNuevo.setOnClickListener {
            val intent = Intent(this, FacturacionActivity::class.java)
            startActivity(intent)
        }

        // Observar pagos
        lifecycleScope.launch {
            viewModel.pagos.collect { lista ->
                adapter.submitList(lista)
            }
        }
    }
}
