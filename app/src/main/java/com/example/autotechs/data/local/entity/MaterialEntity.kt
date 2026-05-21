package com.example.autotechs.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materiales")
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val expedienteId: Int,
    val faseReparacionId: Int, // Fase en la que se utilizó
    val nombre: String,
    val cantidad: Int,
    val costoUnitario: Double
) {
    val costoTotal: Double
        get() = cantidad * costoUnitario
}
