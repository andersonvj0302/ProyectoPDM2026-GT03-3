package com.example.autotechs.data.remote.dto

import com.example.autotechs.data.local.entity.UsuarioEntity

/**
 * DTO (Data Transfer Object) para Usuario.
 * Representa la estructura de datos que se envía/recibe de la API REST.
 * Está desacoplado de la entidad Room para mantener la separación de capas.
 */
data class UsuarioDto(
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val contrasena: String,
    val rol: String
)

/**
 * Data class para la petición de login a la API.
 */
data class LoginRequest(
    val email: String,
    val contrasena: String
)

// --- Funciones de mapeo bidireccional ---

/** Convierte un DTO de la API a una entidad Room para almacenar localmente */
fun UsuarioDto.toEntity(): UsuarioEntity {
    return UsuarioEntity(
        id = this.id,
        nombre = this.nombre,
        email = this.email,
        contrasena = this.contrasena,
        rol = this.rol
    )
}

/** Convierte una entidad Room a un DTO para enviar a la API */
fun UsuarioEntity.toDto(): UsuarioDto {
    return UsuarioDto(
        id = this.id,
        nombre = this.nombre,
        email = this.email,
        contrasena = this.contrasena,
        rol = this.rol
    )
}
