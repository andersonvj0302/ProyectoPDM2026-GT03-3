import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.autotechs.data.local.AppDatabase
import com.example.autotechs.data.local.SessionManager
import com.example.autotechs.data.local.entity.FaseReparacionEntity
import com.example.autotechs.data.repository.TallerRepository
import com.example.autotechs.data.repository.MaterialRepository
import com.example.autotechs.databinding.ActivityTallerMonitorBinding
import kotlinx.coroutines.launch

class TallerMonitorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTallerMonitorBinding
    private lateinit var viewModel: TallerViewModel
    private var expedienteId: Int = 1
    private var modoLectura: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTallerMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar la barra superior
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Obtener expedienteId real pasado por el Intent
        expedienteId = intent.getIntExtra("expedienteId", 1)
        // Determinar si es modo solo lectura (Cliente / Aseguradora)
        val sessionManager = SessionManager(this)
        val role = sessionManager.getUserRole() ?: "Cliente"
        modoLectura = intent.getBooleanExtra("modoLectura", false)
                || role == "Cliente"
                || role == "Aseguradora"


        val database = AppDatabase.getDatabase(this)
        val repository = TallerRepository(database.tallerDao())
        val materialRepository = MaterialRepository(database.materialDao())
        
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TallerViewModel(repository, materialRepository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[TallerViewModel::class.java]

        // Cargar fases del taller
        viewModel.cargarFases(expedienteId)

        lifecycleScope.launch {
            viewModel.fasesState.collect { fases ->
                renderizarFases(fases)
            }
        }
    }

    private fun renderizarFases(fases: List<FaseReparacionEntity>) {
        binding.containerFases.removeAllViews()

        // Ordenar secuencialmente y buscar la primera fase incompleta
        val fasesOrdenadas = fases.sortedBy { it.orden }
        val primeraFaseIncompleta = fasesOrdenadas.firstOrNull { it.estado != "COMPLETADO" }

        for (fase in fasesOrdenadas) {
            val view = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(36, 36, 36, 36)
                setBackgroundColor(Color.parseColor("#1E1E1E"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 24)
                }
            }

            val tvNombre = TextView(this).apply {
                text = "${fase.orden}. ${fase.nombreFase}"
                setTextColor(Color.WHITE)
                textSize = 18f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val tvEstado = TextView(this).apply {
                val textoBase = "Estado: ${fase.estado}"
                text = if (fase.notasControlCalidad.isNotEmpty()) {
                    "$textoBase\nInfo: ${fase.notasControlCalidad}"
                } else {
                    textoBase
                }
                setTextColor(
                    when (fase.estado) {
                        "COMPLETADO" -> Color.parseColor("#4CAF50") // Green
                        "EN_PROCESO" -> Color.parseColor("#FFC107") // Yellow
                        "RECHAZADO" -> Color.parseColor("#F44336") // Red
                        else -> Color.GRAY
                    }
                )
                textSize = 14f
                setPadding(0, 8, 0, 16)
            }

            view.addView(tvNombre)
            view.addView(tvEstado)

            val puedeInteractuar = (fase.orden == primeraFaseIncompleta?.orden)

            if (fase.estado != "COMPLETADO") {
                if (fase.orden == 6 && fase.estado == "EN_PROCESO") {
                    // Diseño interactivo de doble botón para Control de Calidad
                    val layoutBotones = LinearLayout(this).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }

                    val btnAprobar = Button(this).apply {
                        text = "APROBAR"
                        layoutParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        ).apply {
                            marginEnd = 8
                        }
                        setBackgroundColor(Color.parseColor("#4CAF50"))
                        setTextColor(Color.WHITE)
                        isEnabled = puedeInteractuar

                        setOnClickListener {
                            viewModel.evaluarFase(fase, aprobado = true, notas = "Aprobado en revisión final")
                            Toast.makeText(this@TallerMonitorActivity, "Siniestro reparado y aprobado", Toast.LENGTH_SHORT).show()
                        }
                    }

                    val btnRechazar = Button(this).apply {
                        text = "RECHAZAR"
                        layoutParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )
                        setBackgroundColor(Color.parseColor("#F44336"))
                        setTextColor(Color.WHITE)
                        isEnabled = puedeInteractuar

                        setOnClickListener {
                            mostrarDialogoRechazo(fase)
                        }
                    }

                    layoutBotones.addView(btnAprobar)
                    layoutBotones.addView(btnRechazar)
                    view.addView(layoutBotones)

                } else {
                    // Botón de acción único para fases intermedias
                    val btnAccion = Button(this).apply {
                        text = when (fase.estado) {
                            "PENDIENTE" -> "Iniciar Fase"
                            "RECHAZADO" -> "Reiniciar Fase"
                            else -> "Marcar Completado"
                        }
                        
                        isEnabled = puedeInteractuar
                        
                        if (puedeInteractuar) {
                            setBackgroundColor(Color.parseColor("#62B2FD"))
                            setTextColor(Color.BLACK)
                        } else {
                            setBackgroundColor(Color.DKGRAY)
                            setTextColor(Color.LTGRAY)
                        }

                        setOnClickListener {
                            if (fase.estado == "PENDIENTE" || fase.estado == "RECHAZADO") {
                                viewModel.avanzarFase(fase)
                                Toast.makeText(this@TallerMonitorActivity, "Fase ${fase.nombreFase} iniciada", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.evaluarFase(fase, aprobado = true, notas = "Todo OK")
                                Toast.makeText(this@TallerMonitorActivity, "Fase ${fase.nombreFase} completada", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    view.addView(btnAccion)
                }
            }

            binding.containerFases.addView(view)
        }
    }

    private fun mostrarDialogoRechazo(fase: FaseReparacionEntity) {
        val input = EditText(this).apply {
            hint = "Escriba el motivo del rechazo"
        }
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp

        AlertDialog.Builder(this)
            .setTitle("Rechazar Control de Calidad")
            .setMessage("Por favor ingrese las observaciones del por qué se rechaza la fase:")
            .setView(input)
            .setPositiveButton("Enviar") { dialog, _ ->
                val notas = input.text.toString().trim()
                if (notas.isEmpty()) {
                    Toast.makeText(this, "Debe ingresar una observación", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.evaluarFase(fase, aprobado = false, notas = notas)
                    Toast.makeText(this, "Control de calidad rechazado. Reprocesando fases anteriores.", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
