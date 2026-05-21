package com.example.autotechs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotechs.data.local.entity.ClienteEntity
import com.example.autotechs.data.local.entity.ExpedienteEntity
import com.example.autotechs.data.local.entity.PresupuestoEntity
import com.example.autotechs.data.local.entity.VehiculoEntity
import com.example.autotechs.data.repository.RecepcionRepository
import com.example.autotechs.data.repository.ClienteRepository
import com.example.autotechs.domain.usecase.CalcularPresupuestoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SiniestrosViewModel(
    private val repository: RecepcionRepository,
    private val clienteRepository: ClienteRepository,
    private val calcularPresupuestoUseCase: CalcularPresupuestoUseCase = CalcularPresupuestoUseCase()
) : ViewModel() {

    val clientes: Flow<List<ClienteEntity>> = clienteRepository.allClientes

    val siniestros: Flow<List<com.example.autotechs.data.local.entity.SiniestroWithVehiculo>> = 
        repository.allSiniestros

    private val _registroState = MutableStateFlow<RegistroState>(RegistroState.Idle)
    val registroState: StateFlow<RegistroState> = _registroState.asStateFlow()

    fun registrarSiniestro(
        vin: String,
        placa: String,
        marca: String,
        modelo: String,
        clienteId: Int,
        dEstructural: Boolean,
        dEstetico: Boolean,
        dMecanico: Boolean
    ) {
        viewModelScope.launch {
            _registroState.value = RegistroState.Loading
            try {
                // 1. Crear el vehículo
                val vehiculo = VehiculoEntity(vin, placa, marca, modelo, clienteId)

                // 2. Crear el expediente de inspección
                val expediente = ExpedienteEntity(
                    vehiculoVin = vin,
                    danosEstructurales = dEstructural,
                    danosEsteticos = dEstetico,
                    danosMecanicos = dMecanico,
                    fotosUris = "" // Por defecto vacío hasta implementar cámara
                )

                // 3. Generar un presupuesto inicial usando el Caso de Uso (Lógica del Algoritmo)
                // Se asume 5 horas por daño detectado como ejemplo base (ajustable)
                val horasEnderezado = if (dEstructural) 5 else 0
                val horasPintura = if (dEstetico) 5 else 0
                val horasArmado = if (dMecanico) 5 else 0

                val presupuesto = calcularPresupuestoUseCase.invocar(
                    horasEnderezado = horasEnderezado,
                    horasPintura = horasPintura,
                    horasArmado = horasArmado,
                    tarifaHora = 15.0, // Tarifa ejemplo $15/hr
                    costoMateriales = 0.0, // A ser llenado luego
                    costoRepuestos = 0.0   // A ser llenado luego
                )

                val presupuestoEntity = PresupuestoEntity(
                    expedienteId = 0, // Se llenará en el DAO Transaction
                    costoManoObra = presupuesto.costoManoObra,
                    costoMateriales = presupuesto.costoMateriales,
                    costoRepuestos = presupuesto.costoRepuestos,
                    tiempoEstimadoHoras = presupuesto.tiempoEstimadoHoras
                )

                // 4. Guardar todo en la BD de forma transaccional
                repository.registrarIngresoTaller(vehiculo, expediente, presupuestoEntity)

                _registroState.value = RegistroState.Success("¡Siniestro registrado con éxito! Total presupuesto estimado: $${presupuesto.total}")
            } catch (e: Exception) {
                _registroState.value = RegistroState.Error(e.message ?: "Error desconocido al registrar")
            }
        }
    }
}

sealed class RegistroState {
    object Idle : RegistroState()
    object Loading : RegistroState()
    data class Success(val message: String) : RegistroState()
    data class Error(val error: String) : RegistroState()
}
