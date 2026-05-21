package com.example.autotechs.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tarifas")
data class TarifaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tallerId: Int, // Referencia a una empresa/taller
    val tipoVehiculo: String, // SEDAN, SUV, PICKUP, etc.
    val tipoDano: String, // ESTRUCTURAL, ESTETICO, MECANICO
    val costoBase: Double
)
