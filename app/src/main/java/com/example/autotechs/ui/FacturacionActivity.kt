package com.example.autotechs.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.repository.AdministracionRepository
import com.example.autotechs.databinding.ActivityFacturacionCrudBinding
import com.example.autotechs.domain.model.Poliza
import com.example.autotechs.domain.model.Presupuesto
import com.example.autotechs.domain.model.TipoPoliza
import com.example.autotechs.domain.usecase.ValidarSeguroUseCase

class FacturacionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFacturacionCrudBinding
    private lateinit var viewModel: AdministracionViewModel

    private val modalidades = listOf("EFECTIVO", "TARJETA_CREDITO", "TARJETA_DEBITO", "CHEQUE", "BITCOIN")
    private val tiposPoliza = listOf("DEDUCIBLE", "FRANQUICIA")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacturacionCrudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar barra superior
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

        // Cargar modalidades en el spinner de pago
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modalidades)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerModalidad.adapter = spinnerAdapter

        // Configurar sección de Seguro
        binding.cbAplicaSeguro.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutSeguro.visibility = if (isChecked) android.view.View.VISIBLE else android.view.View.GONE
        }

        // Cargar tipos de póliza
        val polizaAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposPoliza)
        polizaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipoPoliza.adapter = polizaAdapter

        binding.btnCalcularSeguro.setOnClickListener {
            val totalStr = binding.etMonto.text.toString().trim()
            val valRefStr = binding.etValorReferencia.text.toString().trim()

            if (totalStr.isEmpty() || valRefStr.isEmpty()) {
                Toast.makeText(this, "Por favor complete el monto base y el valor de referencia", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val total = totalStr.toDoubleOrNull()
            val valRef = valRefStr.toDoubleOrNull()

            if (total == null || valRef == null) {
                Toast.makeText(this, "Valores numéricos inválidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tipoSeleccionado = binding.spinnerTipoPoliza.selectedItem.toString()
            val tipoPoliza = if (tipoSeleccionado == "DEDUCIBLE") TipoPoliza.DEDUCIBLE else TipoPoliza.FRANQUICIA

            val poliza = Poliza(tipo = tipoPoliza, valorReferencia = valRef, ceroDeducible = false)
            // Presupuesto simulado basado en el monto total de la reparación ingresado
            val presupuesto = Presupuesto(costoManoObra = total, costoMateriales = 0.0, costoRepuestos = 0.0, tiempoEstimadoHoras = 1)

            val resultado = ValidarSeguroUseCase().invocar(presupuesto, poliza)

            binding.tvResultadoSeguro.text = """
                Resumen de Cobertura:
                • Tipo: $tipoSeleccionado
                • Estado: ${resultado.mensaje}
                • Seguro Cubre: $${String.format("%.2f", resultado.montoAseguradora)}
                • Paga Cliente: $${String.format("%.2f", resultado.montoCliente)}
            """.trimIndent()

            // Actualizar automáticamente el input de Monto a Pagar con la cuota del cliente calculada!
            binding.etMonto.setText(resultado.montoCliente.toString())
        }

        // Si se recibe un expedienteId y monto sugerido por intent (desde el monitor de taller o similar)
        val expedienteIdSugerido = intent.getIntExtra("expedienteId", -1)
        val montoEstablecido = intent.getDoubleExtra("monto", -1.0)

        if (expedienteIdSugerido != -1) {
            binding.etExpedienteId.setText(expedienteIdSugerido.toString())
        }
        if (montoEstablecido != -1.0) {
            binding.etMonto.setText(montoEstablecido.toString())
        }

        binding.btnProcesarPago.setOnClickListener {
            val expedienteIdStr = binding.etExpedienteId.text.toString().trim()
            val montoStr = binding.etMonto.text.toString().trim()
            val modalidad = binding.spinnerModalidad.selectedItem.toString()

            if (expedienteIdStr.isEmpty() || montoStr.isEmpty()) {
                Toast.makeText(this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val expedienteId = expedienteIdStr.toIntOrNull()
            val monto = montoStr.toDoubleOrNull()

            if (expedienteId == null || monto == null) {
                Toast.makeText(this, "Datos numéricos inválidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guardar en la base de datos local
            viewModel.insertPago(expedienteId, monto, modalidad)
            Toast.makeText(this, "Pago de $$monto registrado exitosamente vía $modalidad", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
