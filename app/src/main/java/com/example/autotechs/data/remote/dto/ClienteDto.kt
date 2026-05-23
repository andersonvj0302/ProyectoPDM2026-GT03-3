package com.example.autotechs.data.remote.dto

import com.example.autotechs.data.local.entity.ClienteEntity

/**
 * DTO para Cliente.
 * Representa los datos del cliente tal como los maneja la API REST.
 */
data class ClienteDto(
    val id: Int = 0,
    val nombre: String,
    val dui: String,
    val telefono: String,
    val email: String
)

// --- Funciones de mapeo bidireccional ---

/** Convierte un DTO de la API a una entidad Room */
fun ClienteDto.toEntity(): ClienteEntity {
    return ClienteEntity(
        id = this.id,
        nombre = this.nombre,
        dui = this.dui,
        telefono = this.telefono,
        email = this.email
    )
}

/** Convierte una entidad Room a un DTO para la API */
fun ClienteEntity.toDto(): ClienteDto {
    return ClienteDto(
        id = this.id,
        nombre = this.nombre,
        dui = this.dui,
        telefono = this.telefono,
        email = this.email
    )
}
