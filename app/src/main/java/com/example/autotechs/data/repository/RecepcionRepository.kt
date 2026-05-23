package com.example.autotechs.data.repository

import android.util.Log
import com.example.autotechs.data.local.dao.RecepcionDao
import com.example.autotechs.data.local.entity.ExpedienteEntity
import com.example.autotechs.data.local.entity.PresupuestoEntity
import com.example.autotechs.data.local.entity.VehiculoEntity
import com.example.autotechs.data.remote.ApiService
import com.example.autotechs.data.remote.dto.toDto
import com.example.autotechs.data.remote.dto.toEntity

/**
 * Repositorio de Recepción (Siniestros) con sincronización API REST.
 * Maneja el ingreso completo de un vehículo al taller:
 * vehículo + expediente + presupuesto en una transacción.
 */
class RecepcionRepository(
    private val recepcionDao: RecepcionDao,
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "RecepcionRepository"
    }

    /**
     * Registra el ingreso completo al taller de forma transaccional en Room,
     * y luego sincroniza cada entidad con la API REST.
     */
    suspend fun registrarIngresoTaller(
        vehiculo: VehiculoEntity,
        expediente: ExpedienteEntity,
        presupuestoInicial: PresupuestoEntity
    ) {
        // Guardar todo en Room de forma transaccional
        recepcionDao.registrarIngresoCompleto(vehiculo, expediente, presupuestoInicial)

        // Sincronizar cada entidad con la API de forma independiente
        try {
            apiService.createVehiculo(vehiculo.toDto())
            Log.d(TAG, "Vehículo sincronizado con API: ${vehiculo.vin}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar vehículo: ${e.message}")
        }

        try {
            apiService.createExpediente(expediente.toDto())
            Log.d(TAG, "Expediente sincronizado con API")
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar expediente: ${e.message}")
        }

        try {
            apiService.createPresupuesto(presupuestoInicial.toDto())
            Log.d(TAG, "Presupuesto sincronizado con API")
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar presupuesto: ${e.message}")
        }
    }

    /** Busca un vehículo por VIN en la BD local */
    suspend fun getVehiculo(vin: String): VehiculoEntity? {
        return recepcionDao.getVehiculo(vin)
    }

    /** Flujo reactivo con los siniestros y sus vehículos asociados */
    val allSiniestros: kotlinx.coroutines.flow.Flow<List<com.example.autotechs.data.local.entity.SiniestroWithVehiculo>> =
        recepcionDao.getSiniestrosWithVehiculos()

    /** Sincroniza expedientes desde la API y los guarda en Room */
    suspend fun syncExpedientes() {
        try {
            val remotos = apiService.getExpedientes()
            remotos.forEach { dto ->
                recepcionDao.insertExpediente(dto.toEntity())
            }
            Log.d(TAG, "Expedientes sincronizados: ${remotos.size} registros")
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar expedientes: ${e.message}")
        }
    }
}
