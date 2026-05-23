package com.example.autotechs.data.remote.dto

import com.example.autotechs.data.local.entity.FaseReparacionEntity

/**
 * DTO para Fase de Reparación.
 * Representa el estado de cada fase del proceso de reparación en taller.
 */
data class FaseReparacionDto(
    val id: Int = 0,
    val expedienteId: Int,
    val nombreFase: String,
    val orden: Int,
    val estado: String,         // PENDIENTE, EN_PROCESO, COMPLETADO, RECHAZADO
    val notasControlCalidad: String = ""
)

// --- Funciones de mapeo bidireccional ---

/** Convierte un DTO de la API a una entidad Room */
fun FaseReparacionDto.toEntity(): FaseReparacionEntity {
    return FaseReparacionEntity(
        id = this.id,
        expedienteId = this.expedienteId,
        nombreFase = this.nombreFase,
        orden = this.orden,
        estado = this.estado,
        notasControlCalidad = this.notasControlCalidad
    )
}

/** Convierte una entidad Room a un DTO para la API */
fun FaseReparacionEntity.toDto(): FaseReparacionDto {
    return FaseReparacionDto(
        id = this.id,
        expedienteId = this.expedienteId,
        nombreFase = this.nombreFase,
        orden = this.orden,
        estado = this.estado,
        notasControlCalidad = this.notasControlCalidad
    )
}
