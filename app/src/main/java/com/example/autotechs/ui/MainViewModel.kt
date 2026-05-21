package com.example.autotechs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotechs.data.local.entity.UsuarioEntity
import com.example.autotechs.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        fetchUsuarios()
    }

    private fun fetchUsuarios() {
        viewModelScope.launch {
            try {
                repository.allUsuarios.collect { usuarios ->
                    _uiState.value = UiState.Success(usuarios)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<UsuarioEntity>) : UiState()
    data class Error(val message: String) : UiState()
}
