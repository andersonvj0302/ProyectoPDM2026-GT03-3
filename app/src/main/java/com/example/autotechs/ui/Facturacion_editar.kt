package com.example.autotechs.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.local.entity.PagoEntity
import com.example.autotechs.data.remote.RetrofitClient
import com.example.autotechs.data.repository.AdministracionRepository
import com.example.autotechs.databinding.ActivityFacturacionEditarBinding
import kotlinx.coroutines.launch

class Facturacion_editar : AppCompatActivity() {

    private lateinit var binding: ActivityFacturacionEditarBinding
    private lateinit var viewModel: AdministracionViewModel
    private var pagoId: Int = -1
    private var currentPago: PagoEntity? = null

    private val modalidades = listOf("EFECTIVO", "TARJETA_CREDITO", "TARJETA_DEBITO", "CHEQUE", "BITCOIN")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacturacionEditarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar barra superior
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Obtener pagoId
        pagoId = intent.getIntExtra("pagoId", -1)
        if (pagoId == -1) {
            Toast.makeText(this, "ID de Pago inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar manual del ViewModel
        val database = AppDatabase.getDatabase(this)
        val repository = AdministracionRepository(database.administracionDao(), RetrofitClient.apiService)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AdministracionViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[AdministracionViewModel::class.java]

        // Configurar Spinner de modalidades
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modalidades)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerModalidad.adapter = spinnerAdapter

        // Cargar datos del pago desde la base de datos Room
        lifecycleScope.launch {
            val pago = repository.getPagoById(pagoId)
            if (pago != null) {
                currentPago = pago
                binding.etExpedienteId.setText(pago.expedienteId.toString())
                binding.etMonto.setText(pago.monto.toString())
                
                val spinnerPosition = modalidades.indexOf(pago.modalidad).coerceAtLeast(0)
                binding.spinnerModalidad.setSelection(spinnerPosition)
            } else {
                Toast.makeText(this@Facturacion_editar, "Pago no encontrado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // Acción Actualizar
        binding.btnActualizar.setOnClickListener {
            val expedienteIdStr = binding.etExpedienteId.text.toString().trim()
            val montoStr = binding.etMonto.text.toString().trim()
            val modalidad = binding.spinnerModalidad.selectedItem.toString()

            if (expedienteIdStr.isEmpty() || montoStr.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val expedienteId = expedienteIdStr.toIntOrNull()
            val monto = montoStr.toDoubleOrNull()

            if (expedienteId == null || monto == null) {
                Toast.makeText(this, "Datos numéricos inválidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updatePago(pagoId, expedienteId, monto, modalidad)
            Toast.makeText(this, "Pago actualizado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Acción Eliminar con Confirmación
        binding.btnEliminar.setOnClickListener {
            currentPago?.let { pago ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar Pago")
                    .setMessage("¿Está seguro que desea eliminar este registro de pago?")
                    .setPositiveButton("Sí") { _, _ ->
                        viewModel.deletePago(pago)
                        Toast.makeText(this, "Pago eliminado", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
    }
}
