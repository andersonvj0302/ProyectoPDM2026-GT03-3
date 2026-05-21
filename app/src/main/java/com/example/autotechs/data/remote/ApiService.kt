package com.example.autotechs.data.remote

import com.example.autotechs.data.local.entity.UsuarioEntity
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/usuarios/{id}")
    suspend fun getUsuarioRemoto(@Path("id") id: Int): UsuarioEntity
    
    // Add other API endpoints according to lineamientos.md
}
