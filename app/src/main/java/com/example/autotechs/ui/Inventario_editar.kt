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
import com.example.autotechs.data.local.entity.MaterialEntity
import com.example.autotechs.data.repository.MaterialRepository
import com.example.autotechs.databinding.ActivityInventarioEditarBinding
import kotlinx.coroutines.launch

class Inventario_editar : AppCompatActivity() {

    private lateinit var binding: ActivityInventarioEditarBinding
    private lateinit var viewModel: MaterialViewModel
    private var materialId: Int = -1
    private var currentMaterial: MaterialEntity? = null

    private val fasesArray = arrayOf(
        "1. Desarme",
        "2. Enderezado",
        "3. Preparación",
        "4. Pintura",
        "5. Secado y Pulido",
        "6. Armado y Control"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventarioEditarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar la barra superior
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Obtener materialId
        materialId = intent.getIntExtra("materialId", -1)
        if (materialId == -1) {
            Toast.makeText(this, "ID de Material inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar manual del ViewModel
        val database = AppDatabase.getDatabase(this)
        val repository = MaterialRepository(database.materialDao())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MaterialViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[MaterialViewModel::class.java]

        // Configurar Spinner de fases
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fasesArray)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spFases.adapter = spinnerAdapter

        // Cargar datos actuales desde Room
        lifecycleScope.launch {
            val material = repository.getMaterialById(materialId)
            if (material != null) {
                currentMaterial = material
                binding.etNombre.setText(material.nombre)
                binding.etCantidad.setText(material.cantidad.toString())
                binding.etCostoUnitario.setText(material.costoUnitario.toString())
                binding.etExpedienteId.setText(material.expedienteId.toString())
                
                // Las fases están 1-indexed
                val spinnerPosition = (material.faseReparacionId - 1).coerceIn(0, fasesArray.size - 1)
                binding.spFases.setSelection(spinnerPosition)
            } else {
                Toast.makeText(this@Inventario_editar, "Material no encontrado en la base de datos", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // Acción Actualizar
        binding.btnActualizar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val cantidadStr = binding.etCantidad.text.toString().trim()
            val costoUnitarioStr = binding.etCostoUnitario.text.toString().trim()
            val expedienteIdStr = binding.etExpedienteId.text.toString().trim()
            val faseIndex = binding.spFases.selectedItemPosition + 1

            if (nombre.isEmpty() || cantidadStr.isEmpty() || costoUnitarioStr.isEmpty() || expedienteIdStr.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cantidad = cantidadStr.toIntOrNull()
            val costoUnitario = costoUnitarioStr.toDoubleOrNull()
            val expedienteId = expedienteIdStr.toIntOrNull()

            if (cantidad == null || costoUnitario == null || expedienteId == null) {
                Toast.makeText(this, "Datos numéricos inválidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateMaterial(materialId, nombre, cantidad, costoUnitario, expedienteId, faseIndex)
            Toast.makeText(this, "Material actualizado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Acción Eliminar con Diálogo de Confirmación
        binding.btnEliminar.setOnClickListener {
            currentMaterial?.let { material ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar Material")
                    .setMessage("¿Está seguro que desea eliminar este material?")
                    .setPositiveButton("Sí") { _, _ ->
                        viewModel.deleteMaterial(material)
                        Toast.makeText(this, "Material eliminado", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
    }
}
