package com.example.autotechs.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehiculos",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VehiculoEntity(
    @PrimaryKey(autoGenerate = false)
    val vin: String,
    val placa: String,
    val marca: String,
    val modelo: String,
    val clienteId: Int
)

@Entity(
    tableName = "expedientes",
    foreignKeys = [
        ForeignKey(
            entity = VehiculoEntity::class,
            parentColumns = ["vin"],
            childColumns = ["vehiculoVin"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExpedienteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val vehiculoVin: String,
    val danosEstructurales: Boolean,
    val danosEsteticos: Boolean,
    val danosMecanicos: Boolean,
    val fotosUris: String // Representación separada por comas de las URIs de fotos
)

@Entity(
    tableName = "presupuestos",
    foreignKeys = [
        ForeignKey(
            entity = ExpedienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["expedienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PresupuestoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val expedienteId: Int,
    val costoManoObra: Double,
    val costoMateriales: Double,
    val costoRepuestos: Double,
    val tiempoEstimadoHoras: Int
)

data class SiniestroWithVehiculo(
    val id: Int,
    val vehiculoVin: String,
    val danosEstructurales: Boolean,
    val danosEsteticos: Boolean,
    val danosMecanicos: Boolean,
    val placa: String,
    val marca: String,
    val modelo: String
)
