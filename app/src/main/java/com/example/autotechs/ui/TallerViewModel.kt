package com.example.autotechs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotechs.data.local.entity.FaseReparacionEntity
import com.example.autotechs.data.repository.TallerRepository
import com.example.autotechs.data.repository.MaterialRepository
import com.example.autotechs.data.local.entity.MaterialEntity
import com.example.autotechs.domain.usecase.AvanzarFaseUseCase
import com.example.autotechs.domain.usecase.ControlCalidadUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TallerViewModel(
    private val repository: TallerRepository,
    private val materialRepository: MaterialRepository? = null,
    private val avanzarFaseUseCase: AvanzarFaseUseCase = AvanzarFaseUseCase(),
    private val controlCalidadUseCase: ControlCalidadUseCase = ControlCalidadUseCase()
) : ViewModel() {

    private val _fasesState = MutableStateFlow<List<FaseReparacionEntity>>(emptyList())
    val fasesState: StateFlow<List<FaseReparacionEntity>> = _fasesState.asStateFlow()

    fun cargarFases(expedienteId: Int) {
        viewModelScope.launch {
            repository.getFases(expedienteId).collect { fases ->
                if (fases.isEmpty()) {
                    repository.inicializarFases(expedienteId)
                } else {
                    _fasesState.value = fases
                }
            }
        }
    }

    fun avanzarFase(fase: FaseReparacionEntity) {
        viewModelScope.launch {
            val faseActualizada = avanzarFaseUseCase.iniciarFase(fase)
            repository.actualizarFase(faseActualizada)

            // Integración de Consumo Automático de Materiales del Inventario
            if (faseActualizada.estado == "EN_PROCESO") {
                materialRepository?.let { matRepo ->
                    when (faseActualizada.orden) {
                        3 -> { // Preparación
                            matRepo.insert(
                                MaterialEntity(
                                    expedienteId = faseActualizada.expedienteId,
                                    faseReparacionId = faseActualizada.id,
                                    nombre = "Consumo: Kit de Lijado y Masilla",
                                    cantidad = 2,
                                    costoUnitario = 15.0
                                )
                            )
                        }
                        4 -> { // Pintura
                            matRepo.insert(
                                MaterialEntity(
                                    expedienteId = faseActualizada.expedienteId,
                                    faseReparacionId = faseActualizada.id,
                                    nombre = "Consumo: Galón de Pintura Premium",
                                    cantidad = 1,
                                    costoUnitario = 45.0
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun evaluarFase(fase: FaseReparacionEntity, aprobado: Boolean, notas: String) {
        viewModelScope.launch {
            val faseEvaluada = controlCalidadUseCase.evaluarRevision(aprobado, fase, notas)
            repository.actualizarFase(faseEvaluada)

            if (!aprobado) {
                // Retroceder fases anteriores: resetear fase 4 (Pintura) y 5 (Secado y Pulido) a PENDIENTE
                val todasLasFases = repository.getFasesUnaVez(fase.expedienteId)
                todasLasFases.forEach { f ->
                    if (f.orden == 4 || f.orden == 5 || f.orden == 6) {
                        // Reseteamos fase 4, 5 y 6 a PENDIENTE para que vuelvan a pasar por el flujo
                        val estadoReseteado = if (f.orden == 6) "RECHAZADO" else "PENDIENTE"
                        repository.actualizarFase(f.copy(
                            estado = estadoReseteado,
                            notasControlCalidad = "Rechazo en Control de Calidad: $notas"
                        ))
                    }
                }
            }
        }
    }
}
