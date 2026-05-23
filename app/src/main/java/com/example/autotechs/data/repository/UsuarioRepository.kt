package com.example.autotechs.data.repository

import android.util.Log
import com.example.autotechs.data.local.dao.UsuarioDao
import com.example.autotechs.data.local.entity.UsuarioEntity
import com.example.autotechs.data.remote.ApiService
import com.example.autotechs.data.remote.dto.LoginRequest
import com.example.autotechs.data.remote.dto.toDto
import com.example.autotechs.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de Usuarios con patrón "local-first".
 * Siempre guarda en Room primero, luego intenta sincronizar con la API REST.
 * Si la API falla, los datos locales persisten sin afectar la experiencia del usuario.
 */
class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "UsuarioRepository"
    }

    /** Flujo reactivo con todos los usuarios de la BD local */
    val allUsuarios: Flow<List<UsuarioEntity>> = usuarioDao.getAllUsuarios()

    /** Inserta un usuario localmente y luego intenta enviarlo a la API */
    suspend fun insertUsuarioLocal(usuario: UsuarioEntity) {
        // Guardar primero en la base de datos local (Room)
        usuarioDao.insertUsuario(usuario)

        // Intentar sincronizar con la API REST
        try {
            apiService.createUsuario(usuario.toDto())
            Log.d(TAG, "Usuario sincronizado con la API: ${usuario.email}")
        } catch (e: Exception) {
            // Si falla la red, el usuario ya está guardado localmente
            Log.w(TAG, "No se pudo sincronizar usuario con API: ${e.message}")
        }
    }

    /** Login local: busca en la base de datos Room */
    suspend fun login(email: String, password: String): UsuarioEntity? {
        return usuarioDao.getUserByEmailAndPassword(email, password)
    }

    /** Busca un usuario por email en la BD local */
    suspend fun getUserByEmail(email: String): UsuarioEntity? {
        return usuarioDao.getUserByEmail(email)
    }

    /** Obtiene un usuario desde la API remota por ID */
    suspend fun fetchUsuarioRemoto(id: Int): UsuarioEntity {
        return apiService.getUsuario(id).toEntity()
    }

    /**
     * Sincroniza usuarios: descarga todos los usuarios de la API
     * y los guarda/actualiza en la base de datos local.
     */
    suspend fun syncUsuarios() {
        try {
            val remotos = apiService.getUsuarios()
            remotos.forEach { dto ->
                usuarioDao.insertUsuario(dto.toEntity())
            }
            Log.d(TAG, "Sincronización de usuarios completada: ${remotos.size} registros")
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar usuarios: ${e.message}")
        }
    }
}
