package com.example.autotechs.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "fases_reparacion",
    foreignKeys = [
        ForeignKey(
            entity = ExpedienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["expedienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FaseReparacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val expedienteId: Int,
    val nombreFase: String, // Desarme, Enderezado, Preparacion, Pintura, Secado, Armado
    val orden: Int, // Para mantener la secuencia lógica (1 al 6)
    val estado: String, // PENDIENTE, EN_PROCESO, COMPLETADO, RECHAZADO
    val notasControlCalidad: String = ""
)
