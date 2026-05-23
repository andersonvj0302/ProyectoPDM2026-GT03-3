package com.example.autotechs.data.remote.dto

import com.example.autotechs.data.local.entity.PagoEntity

/**
 * DTO para Pago.
 * Representa un registro de pago con su modalidad.
 */
data class PagoDto(
    val id: Int = 0,
    val expedienteId: Int,
    val monto: Double,
    val modalidad: String,  // EFECTIVO, TARJETA_CREDITO, TARJETA_DEBITO, CHEQUE, BITCOIN
    val fechaPago: Long = System.currentTimeMillis()
)

// --- Funciones de mapeo bidireccional ---

/** Convierte un DTO de la API a una entidad Room */
fun PagoDto.toEntity(): PagoEntity {
    return PagoEntity(
        id = this.id,
        expedienteId = this.expedienteId,
        monto = this.monto,
        modalidad = this.modalidad,
        fechaPago = this.fechaPago
    )
}

/** Convierte una entidad Room a un DTO para la API */
fun PagoEntity.toDto(): PagoDto {
    return PagoDto(
        id = this.id,
        expedienteId = this.expedienteId,
        monto = this.monto,
        modalidad = this.modalidad,
        fechaPago = this.fechaPago
    )
}
