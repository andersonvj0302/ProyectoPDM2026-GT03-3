package com.example.autotechs.data.remote.dto

import com.example.autotechs.data.local.entity.MaterialEntity

/**
 * DTO para Material (Inventario).
 * Representa los materiales usados en reparaciones.
 */
data class MaterialDto(
    val id: Int = 0,
    val expedienteId: Int,
    val faseReparacionId: Int,
    val nombre: String,
    val cantidad: Int,
    val costoUnitario: Double
)

// --- Funciones de mapeo bidireccional ---

/** Convierte un DTO de la API a una entidad Room */
fun MaterialDto.toEntity(): MaterialEntity {
    return MaterialEntity(
        id = this.id,
        expedienteId = this.expedienteId,
        faseReparacionId = this.faseReparacionId,
        nombre = this.nombre,
        cantidad = this.cantidad,
        costoUnitario = this.costoUnitario
    )
}

/** Convierte una entidad Room a un DTO para la API */
fun MaterialEntity.toDto(): MaterialDto {
    return MaterialDto(
        id = this.id,
        expedienteId = this.expedienteId,
        faseReparacionId = this.faseReparacionId,
        nombre = this.nombre,
        cantidad = this.cantidad,
        costoUnitario = this.costoUnitario
    )
}
