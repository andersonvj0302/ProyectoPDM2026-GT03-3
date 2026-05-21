package com.example.autotechs.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.autotechs.data.local.entity.ExpedienteEntity
import com.example.autotechs.data.local.entity.PresupuestoEntity
import com.example.autotechs.data.local.entity.SiniestroWithVehiculo
import com.example.autotechs.data.local.entity.VehiculoEntity

@Dao
interface RecepcionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehiculo(vehiculo: VehiculoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpediente(expediente: ExpedienteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresupuesto(presupuesto: PresupuestoEntity)

    @Query("SELECT * FROM vehiculos WHERE vin = :vin")
    suspend fun getVehiculo(vin: String): VehiculoEntity?

    @Query("SELECT * FROM expedientes WHERE vehiculoVin = :vin LIMIT 1")
    suspend fun getExpedienteByVin(vin: String): ExpedienteEntity?

    @Query("""
        SELECT e.id, e.vehiculoVin, e.danosEstructurales, e.danosEsteticos, e.danosMecanicos, 
               v.placa, v.marca, v.modelo 
        FROM expedientes e
        INNER JOIN vehiculos v ON e.vehiculoVin = v.vin
    """)
    fun getSiniestrosWithVehiculos(): kotlinx.coroutines.flow.Flow<List<SiniestroWithVehiculo>>
    
    // Transacción para registrar todo el ingreso de una sola vez
    @Transaction
    suspend fun registrarIngresoCompleto(
        vehiculo: VehiculoEntity,
        expediente: ExpedienteEntity,
        presupuesto: PresupuestoEntity
    ) {
        insertVehiculo(vehiculo)
        val expId = insertExpediente(expediente).toInt()
        val presupuestoFinal = presupuesto.copy(expedienteId = expId)
        insertPresupuesto(presupuestoFinal)
    }
}
