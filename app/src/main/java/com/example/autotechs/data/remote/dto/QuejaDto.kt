package com.example.autotechs.data.remote.dto

import com.example.autotechs.data.local.entity.QuejaEntity

/**
 * DTO para Queja (Reclamo post-venta).
 * Representa una queja del cliente con su evaluación de reintegro.
 */
data class QuejaDto(
    val id: Int = 0,
    val expedienteId: Int,
    val descripcion: String,
    val justificada: Boolean,
    val montoReintegro: Double,
    val fechaQueja: Long = System.currentTimeMillis()
)

// --- Funciones de mapeo bidireccional ---

/** Convierte un DTO de la API a una entidad Room */
fun QuejaDto.toEntity(): QuejaEntity {
    return QuejaEntity(
        id = this.id,
        expedienteId = this.expedienteId,
        descripcion = this.descripcion,
        justificada = this.justificada,
        montoReintegro = this.montoReintegro,
        fechaQueja = this.fechaQueja
    )
}

/** Convierte una entidad Room a un DTO para la API */
fun QuejaEntity.toDto(): QuejaDto {
    return QuejaDto(
        id = this.id,
        expedienteId = this.expedienteId,
        descripcion = this.descripcion,
        justificada = this.justificada,
        montoReintegro = this.montoReintegro,
        fechaQueja = this.fechaQueja
    )
}
