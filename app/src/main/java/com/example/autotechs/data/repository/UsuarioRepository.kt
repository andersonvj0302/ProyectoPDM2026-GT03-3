package com.example.autotechs.data.repository

import com.example.autotechs.data.local.dao.UsuarioDao
import com.example.autotechs.data.local.entity.UsuarioEntity
import com.example.autotechs.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val apiService: ApiService
) {

    val allUsuarios: Flow<List<UsuarioEntity>> = usuarioDao.getAllUsuarios()

    suspend fun insertUsuarioLocal(usuario: UsuarioEntity) {
        usuarioDao.insertUsuario(usuario)
    }

    suspend fun login(email: String, password: String): UsuarioEntity? {
        return usuarioDao.getUserByEmailAndPassword(email, password)
    }

    suspend fun getUserByEmail(email: String): UsuarioEntity? {
        return usuarioDao.getUserByEmail(email)
    }

    // Example of fetching remote data
    suspend fun fetchUsuarioRemoto(id: Int): UsuarioEntity {
        return apiService.getUsuarioRemoto(id)
    }
}
