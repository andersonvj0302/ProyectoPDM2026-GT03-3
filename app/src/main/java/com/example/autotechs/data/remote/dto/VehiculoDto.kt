package com.example.autotechs.data.remote.dto

import com.example.autotechs.data.local.entity.VehiculoEntity

/**
 * DTO para Vehículo.
 * Representa los datos del vehículo tal como los maneja la API REST.
 */
data class VehiculoDto(
    val vin: String,
    val placa: String,
    val marca: String,
    val modelo: String,
    val clienteId: Int
)

// --- Funciones de mapeo bidireccional ---

/** Convierte un DTO de la API a una entidad Room */
fun VehiculoDto.toEntity(): VehiculoEntity {
    return VehiculoEntity(
        vin = this.vin,
        placa = this.placa,
        marca = this.marca,
        modelo = this.modelo,
        clienteId = this.clienteId
    )
}

/** Convierte una entidad Room a un DTO para la API */
fun VehiculoEntity.toDto(): VehiculoDto {
    return VehiculoDto(
        vin = this.vin,
        placa = this.placa,
        marca = this.marca,
        modelo = this.modelo,
        clienteId = this.clienteId
    )
}
