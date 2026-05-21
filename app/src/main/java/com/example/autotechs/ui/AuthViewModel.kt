package com.example.autotechs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotechs.data.local.SessionManager
import com.example.autotechs.data.local.entity.UsuarioEntity
import com.example.autotechs.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: UsuarioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, contrasena: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val usuario = repository.login(email, contrasena)
                if (usuario != null) {
                    sessionManager.saveSession(usuario.email, usuario.nombre, usuario.rol)
                    _authState.value = AuthState.Success(usuario)
                } else {
                    _authState.value = AuthState.Error("Credenciales incorrectas")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun registrar(nombre: String, email: String, contrasena: String, rol: String = "Cliente") {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val existingUser = repository.getUserByEmail(email)
                if (existingUser != null) {
                    _authState.value = AuthState.Error("El email ya está registrado")
                    return@launch
                }

                val nuevoUsuario = UsuarioEntity(
                    nombre = nombre,
                    email = email,
                    contrasena = contrasena,
                    rol = rol
                )
                repository.insertUsuarioLocal(nuevoUsuario)
                _authState.value = AuthState.RegisterSuccess
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error al registrar usuario")
            }
        }
    }

    fun logout() {
        sessionManager.logout()
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val usuario: UsuarioEntity) : AuthState()
    object RegisterSuccess : AuthState()
    data class Error(val error: String) : AuthState()
}
