package com.example.autotechs.data.repository

import android.util.Log
import com.example.autotechs.data.local.dao.VehiculoDao
import com.example.autotechs.data.local.entity.VehiculoEntity
import com.example.autotechs.data.remote.ApiService
import com.example.autotechs.data.remote.dto.toDto
import com.example.autotechs.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de Vehículos con sincronización API REST.
 * CRUD local-first: Room primero, luego API.
 */
class VehiculoRepository(
    private val vehiculoDao: VehiculoDao,
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "VehiculoRepository"
    }

    /** Flujo reactivo con todos los vehículos locales */
    val allVehiculos: Flow<List<VehiculoEntity>> = vehiculoDao.getAllVehiculos()

    /** Obtiene vehículos filtrados por cliente */
    fun getVehiculosByCliente(clienteId: Int): Flow<List<VehiculoEntity>> {
        return vehiculoDao.getVehiculosByCliente(clienteId)
    }

    /** Inserta un vehículo localmente y lo envía a la API */
    suspend fun insertVehiculo(vehiculo: VehiculoEntity) {
        vehiculoDao.insertVehiculo(vehiculo)
        try {
            apiService.createVehiculo(vehiculo.toDto())
            Log.d(TAG, "Vehículo creado en API: ${vehiculo.placa}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al crear vehículo en API: ${e.message}")
        }
    }

    /** Actualiza un vehículo localmente y en la API */
    suspend fun updateVehiculo(vehiculo: VehiculoEntity) {
        vehiculoDao.updateVehiculo(vehiculo)
        try {
            apiService.updateVehiculo(vehiculo.vin, vehiculo.toDto())
            Log.d(TAG, "Vehículo actualizado en API: ${vehiculo.placa}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al actualizar vehículo en API: ${e.message}")
        }
    }

    /** Elimina un vehículo localmente y en la API */
    suspend fun deleteVehiculo(vehiculo: VehiculoEntity) {
        vehiculoDao.deleteVehiculo(vehiculo)
        try {
            apiService.deleteVehiculo(vehiculo.vin)
            Log.d(TAG, "Vehículo eliminado de API: VIN ${vehiculo.vin}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al eliminar vehículo de API: ${e.message}")
        }
    }

    /** Sincroniza vehículos desde la API y los guarda en Room */
    suspend fun syncVehiculos() {
        try {
            val remotos = apiService.getVehiculos()
            remotos.forEach { dto ->
                vehiculoDao.insertVehiculo(dto.toEntity())
            }
            Log.d(TAG, "Vehículos sincronizados: ${remotos.size} registros")
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar vehículos: ${e.message}")
        }
    }
}
