package com.example.autotechs.data.remote.dto

import com.example.autotechs.data.local.entity.PresupuestoEntity

/**
 * DTO para Presupuesto.
 * Contiene los costos desglosados de la reparación.
 */
data class PresupuestoDto(
    val id: Int = 0,
    val expedienteId: Int,
    val costoManoObra: Double,
    val costoMateriales: Double,
    val costoRepuestos: Double,
    val tiempoEstimadoHoras: Int
)

// --- Funciones de mapeo bidireccional ---

/** Convierte un DTO de la API a una entidad Room */
fun PresupuestoDto.toEntity(): PresupuestoEntity {
    return PresupuestoEntity(
        id = this.id,
        expedienteId = this.expedienteId,
        costoManoObra = this.costoManoObra,
        costoMateriales = this.costoMateriales,
        costoRepuestos = this.costoRepuestos,
        tiempoEstimadoHoras = this.tiempoEstimadoHoras
    )
}

/** Convierte una entidad Room a un DTO para la API */
fun PresupuestoEntity.toDto(): PresupuestoDto {
    return PresupuestoDto(
        id = this.id,
        expedienteId = this.expedienteId,
        costoManoObra = this.costoManoObra,
        costoMateriales = this.costoMateriales,
        costoRepuestos = this.costoRepuestos,
        tiempoEstimadoHoras = this.tiempoEstimadoHoras
    )
}
