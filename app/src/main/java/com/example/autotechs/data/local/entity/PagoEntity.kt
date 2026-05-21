package com.example.autotechs.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pagos")
data class PagoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val expedienteId: Int,
    val monto: Double,
    val modalidad: String, // EFECTIVO, TARJETA_CREDITO, TARJETA_DEBITO, CHEQUE, BITCOIN
    val fechaPago: Long = System.currentTimeMillis()
)
