package com.example.autotechs.data.remote.dto

import com.example.autotechs.data.local.entity.ExpedienteEntity

/**
 * DTO para Expediente (Siniestro).
 * Contiene la información de inspección de daños del vehículo.
 */
data class ExpedienteDto(
    val id: Int = 0,
    val vehiculoVin: String,
    val danosEstructurales: Boolean,
    val danosEsteticos: Boolean,
    val danosMecanicos: Boolean,
    val fotosUris: String = ""
)

// --- Funciones de mapeo bidireccional ---

/** Convierte un DTO de la API a una entidad Room */
fun ExpedienteDto.toEntity(): ExpedienteEntity {
    return ExpedienteEntity(
        id = this.id,
        vehiculoVin = this.vehiculoVin,
        danosEstructurales = this.danosEstructurales,
        danosEsteticos = this.danosEsteticos,
        danosMecanicos = this.danosMecanicos,
        fotosUris = this.fotosUris
    )
}

/** Convierte una entidad Room a un DTO para la API */
fun ExpedienteEntity.toDto(): ExpedienteDto {
    return ExpedienteDto(
        id = this.id,
        vehiculoVin = this.vehiculoVin,
        danosEstructurales = this.danosEstructurales,
        danosEsteticos = this.danosEsteticos,
        danosMecanicos = this.danosMecanicos,
        fotosUris = this.fotosUris
    )
}
