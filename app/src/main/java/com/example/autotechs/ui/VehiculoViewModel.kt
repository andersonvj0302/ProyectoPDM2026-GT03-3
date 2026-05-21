package com.example.autotechs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotechs.data.local.entity.VehiculoEntity
import com.example.autotechs.data.local.entity.ClienteEntity
import com.example.autotechs.data.repository.VehiculoRepository
import com.example.autotechs.data.repository.ClienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VehiculoViewModel(
    private val vehiculoRepository: VehiculoRepository,
    private val clienteRepository: ClienteRepository
) : ViewModel() {

    private val _vehiculos = MutableStateFlow<List<VehiculoEntity>>(emptyList())
    val vehiculos: StateFlow<List<VehiculoEntity>> = _vehiculos.asStateFlow()

    private val _clientes = MutableStateFlow<List<ClienteEntity>>(emptyList())
    val clientes: StateFlow<List<ClienteEntity>> = _clientes.asStateFlow()

    private val _operacionState = MutableStateFlow<OperacionState>(OperacionState.Idle)
    val operacionState: StateFlow<OperacionState> = _operacionState.asStateFlow()

    init {
        viewModelScope.launch {
            vehiculoRepository.allVehiculos.collectLatest {
                _vehiculos.value = it
            }
        }
        viewModelScope.launch {
            clienteRepository.allClientes.collectLatest {
                _clientes.value = it
            }
        }
    }

    fun guardarVehiculo(vin: String, placa: String, marca: String, modelo: String, clienteId: Int, isEdit: Boolean = false) {
        viewModelScope.launch {
            _operacionState.value = OperacionState.Loading
            try {
                val vehiculo = VehiculoEntity(vin, placa, marca, modelo, clienteId)
                if (!isEdit) {
                    vehiculoRepository.insertVehiculo(vehiculo)
                } else {
                    vehiculoRepository.updateVehiculo(vehiculo)
                }
                _operacionState.value = OperacionState.Success("Vehículo guardado correctamente")
            } catch (e: Exception) {
                _operacionState.value = OperacionState.Error(e.message ?: "Error al guardar vehículo")
            }
        }
    }

    fun eliminarVehiculo(vehiculo: VehiculoEntity) {
        viewModelScope.launch {
            _operacionState.value = OperacionState.Loading
            try {
                vehiculoRepository.deleteVehiculo(vehiculo)
                _operacionState.value = OperacionState.Success("Vehículo eliminado correctamente")
            } catch (e: Exception) {
                _operacionState.value = OperacionState.Error(e.message ?: "Error al eliminar vehículo")
            }
        }
    }
}
