package com.example.autotechs.data.local.dao

import androidx.room.*
import com.example.autotechs.data.local.entity.VehiculoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehiculoDao {
    @Query("SELECT * FROM vehiculos ORDER BY placa ASC")
    fun getAllVehiculos(): Flow<List<VehiculoEntity>>

    @Query("SELECT * FROM vehiculos WHERE vin = :vin")
    suspend fun getVehiculoByVin(vin: String): VehiculoEntity?

    @Query("SELECT * FROM vehiculos WHERE clienteId = :clienteId")
    fun getVehiculosByCliente(clienteId: Int): Flow<List<VehiculoEntity>>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehiculo(vehiculo: VehiculoEntity)

    @Update
    suspend fun updateVehiculo(vehiculo: VehiculoEntity)

    @Delete
    suspend fun deleteVehiculo(vehiculo: VehiculoEntity)
}
