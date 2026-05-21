package com.example.autotechs.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quejas")
data class QuejaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val expedienteId: Int,
    val descripcion: String,
    val justificada: Boolean,
    val montoReintegro: Double,
    val fechaQueja: Long = System.currentTimeMillis()
)
