package com.example.autotechs.data.repository

import com.example.autotechs.data.local.dao.VehiculoDao
import com.example.autotechs.data.local.entity.VehiculoEntity
import kotlinx.coroutines.flow.Flow

class VehiculoRepository(private val vehiculoDao: VehiculoDao) {
    val allVehiculos: Flow<List<VehiculoEntity>> = vehiculoDao.getAllVehiculos()

    fun getVehiculosByCliente(clienteId: Int): Flow<List<VehiculoEntity>> {
        return vehiculoDao.getVehiculosByCliente(clienteId)
    }

    suspend fun insertVehiculo(vehiculo: VehiculoEntity) {
        vehiculoDao.insertVehiculo(vehiculo)
    }

    suspend fun updateVehiculo(vehiculo: VehiculoEntity) {
        vehiculoDao.updateVehiculo(vehiculo)
    }

    suspend fun deleteVehiculo(vehiculo: VehiculoEntity) {
        vehiculoDao.deleteVehiculo(vehiculo)
    }
}
