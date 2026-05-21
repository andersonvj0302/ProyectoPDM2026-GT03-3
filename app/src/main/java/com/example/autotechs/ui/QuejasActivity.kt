package com.example.autotechs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.local.entity.QuejaEntity
import com.example.autotechs.data.repository.AdministracionRepository
import com.example.autotechs.databinding.ActivityQuejasBinding
import com.example.autotechs.domain.usecase.ProcesarQuejaUseCase
import kotlinx.coroutines.launch

class QuejasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuejasBinding
    private lateinit var viewModel: AdministracionViewModel
    private lateinit var adapter: QuejaAdapter
    private val procesarQuejaUseCase = ProcesarQuejaUseCase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuejasBinding.inflate(layoutInflater)
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
        adapter = QuejaAdapter { queja ->
            mostrarDialogoEditarEliminar(queja)
        }
        binding.rvQuejas.layoutManager = LinearLayoutManager(this)
        binding.rvQuejas.adapter = adapter

        // FAB: Agregar Nueva Queja
        binding.btnCrearNuevo.setOnClickListener {
            mostrarDialogoNuevaQueja()
        }

        // Observar quejas de la base de datos
        lifecycleScope.launch {
            viewModel.quejas.collect { lista ->
                adapter.submitList(lista)
            }
        }
    }

    private fun mostrarDialogoNuevaQueja() {
        val context = this
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 40)
        }

        val etExpId = EditText(context).apply {
            hint = "ID del Expediente"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val etDesc = EditText(context).apply {
            hint = "Descripción del problema"
            minLines = 2
        }
        val etCosto = EditText(context).apply {
            hint = "Costo Total de Reparación ($)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText("1500.00") // Costo sugerido base
        }
        val cbJustificada = CheckBox(context).apply {
            text = "¿Queja Justificada?"
        }

        layout.addView(etExpId)
        layout.addView(etDesc)
        layout.addView(etCosto)
        layout.addView(cbJustificada)

        AlertDialog.Builder(context)
            .setTitle("Nueva Reclamación Post-Venta")
            .setMessage("Por favor ingrese los detalles para calcular la procedencia del reintegro:")
            .setView(layout)
            .setPositiveButton("Registrar") { dialog, _ ->
                val expIdStr = etExpId.text.toString().trim()
                val desc = etDesc.text.toString().trim()
                val costoStr = etCosto.text.toString().trim()
                val justificada = cbJustificada.isChecked

                if (expIdStr.isEmpty() || desc.isEmpty() || costoStr.isEmpty()) {
                    Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val expId = expIdStr.toIntOrNull()
                val costo = costoStr.toDoubleOrNull()

                if (expId == null || costo == null) {
                    Toast.makeText(context, "Datos numéricos inválidos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Evaluar la regla de negocio del usecase (20% de reintegro si es justificada)
                val quejaEvaluada = procesarQuejaUseCase.evaluarQueja(expId, desc, justificada, costo)

                // Guardar en base de datos local
                viewModel.insertQueja(quejaEvaluada.expedienteId, quejaEvaluada.descripcion, quejaEvaluada.justificada, quejaEvaluada.montoReintegro)

                val msg = if (quejaEvaluada.montoReintegro > 0) {
                    "Queja aprobada. Reintegro compensatorio: $${quejaEvaluada.montoReintegro}"
                } else {
                    "Queja registrada sin derecho a reintegro."
                }
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditarEliminar(queja: QuejaEntity) {
        val context = this
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 40)
        }

        val etDesc = EditText(context).apply {
            hint = "Descripción del problema"
            setText(queja.descripcion)
        }
        val cbJustificada = CheckBox(context).apply {
            text = "¿Queja Justificada?"
            isChecked = queja.justificada
        }

        layout.addView(etDesc)
        layout.addView(cbJustificada)

        AlertDialog.Builder(context)
            .setTitle("Editar Reclamación")
            .setView(layout)
            .setPositiveButton("Actualizar") { dialog, _ ->
                val desc = etDesc.text.toString().trim()
                val justificada = cbJustificada.isChecked

                if (desc.isEmpty()) {
                    Toast.makeText(context, "Ingrese una descripción", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Recalcular
                val nuevoReintegro = if (justificada) 300.0 else 0.0 // Compensación base de $300 si procede

                viewModel.updateQueja(queja.id, queja.expedienteId, desc, justificada, nuevoReintegro)
                Toast.makeText(context, "Queja actualizada", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNeutralButton("Eliminar") { dialog, _ ->
                viewModel.deleteQueja(queja)
                Toast.makeText(context, "Queja eliminada", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
