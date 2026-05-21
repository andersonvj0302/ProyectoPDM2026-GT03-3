package com.example.autotechs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotechs.data.local.entity.ClienteEntity
import com.example.autotechs.data.repository.ClienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ClienteViewModel(private val repository: ClienteRepository) : ViewModel() {

    private val _clientes = MutableStateFlow<List<ClienteEntity>>(emptyList())
    val clientes: StateFlow<List<ClienteEntity>> = _clientes.asStateFlow()

    private val _operacionState = MutableStateFlow<OperacionState>(OperacionState.Idle)
    val operacionState: StateFlow<OperacionState> = _operacionState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allClientes.collectLatest {
                _clientes.value = it
            }
        }
    }

    fun guardarCliente(nombre: String, dui: String, telefono: String, email: String, id: Int = 0) {
        viewModelScope.launch {
            _operacionState.value = OperacionState.Loading
            try {
                val cliente = ClienteEntity(id, nombre, dui, telefono, email)
                if (id == 0) {
                    repository.insertCliente(cliente)
                } else {
                    repository.updateCliente(cliente)
                }
                _operacionState.value = OperacionState.Success("Cliente guardado correctamente")
            } catch (e: Exception) {
                _operacionState.value = OperacionState.Error(e.message ?: "Error al guardar cliente")
            }
        }
    }

    fun eliminarCliente(cliente: ClienteEntity) {
        viewModelScope.launch {
            _operacionState.value = OperacionState.Loading
            try {
                repository.deleteCliente(cliente)
                _operacionState.value = OperacionState.Success("Cliente eliminado correctamente")
            } catch (e: Exception) {
                _operacionState.value = OperacionState.Error(e.message ?: "Error al eliminar cliente")
            }
        }
    }
}

sealed class OperacionState {
    object Idle : OperacionState()
    object Loading : OperacionState()
    data class Success(val message: String) : OperacionState()
    data class Error(val error: String) : OperacionState()
}
