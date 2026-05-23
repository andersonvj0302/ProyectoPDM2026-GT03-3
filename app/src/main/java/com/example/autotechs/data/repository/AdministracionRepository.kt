package com.example.autotechs.data.repository

import android.util.Log
import com.example.autotechs.data.local.dao.AdministracionDao
import com.example.autotechs.data.local.entity.PagoEntity
import com.example.autotechs.data.local.entity.QuejaEntity
import com.example.autotechs.data.remote.ApiService
import com.example.autotechs.data.remote.dto.toDto
import com.example.autotechs.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de Administración (Pagos y Quejas) con sincronización API REST.
 * Gestiona tanto los registros de pago como los reclamos post-venta.
 */
class AdministracionRepository(
    private val administracionDao: AdministracionDao,
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "AdminRepository"
    }

    // ═══════ PAGOS ═══════

    /** Flujo reactivo con todos los pagos locales */
    val allPagos: Flow<List<PagoEntity>> = administracionDao.getAllPagos()

    /** Inserta un pago localmente y lo envía a la API */
    suspend fun insertPago(pago: PagoEntity) {
        administracionDao.insertPago(pago)
        try {
            apiService.createPago(pago.toDto())
            Log.d(TAG, "Pago sincronizado con API: $${pago.monto}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar pago: ${e.message}")
        }
    }

    /** Actualiza un pago localmente y en la API */
    suspend fun updatePago(pago: PagoEntity) {
        administracionDao.updatePago(pago)
        try {
            apiService.updatePago(pago.id, pago.toDto())
            Log.d(TAG, "Pago actualizado en API: ID ${pago.id}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al actualizar pago en API: ${e.message}")
        }
    }

    /** Elimina un pago localmente y de la API */
    suspend fun deletePago(pago: PagoEntity) {
        administracionDao.deletePago(pago)
        try {
            apiService.deletePago(pago.id)
            Log.d(TAG, "Pago eliminado de API: ID ${pago.id}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al eliminar pago de API: ${e.message}")
        }
    }

    /** Busca un pago por ID en la BD local */
    suspend fun getPagoById(id: Int): PagoEntity? {
        return administracionDao.getPagoById(id)
    }

    // ═══════ QUEJAS ═══════

    /** Flujo reactivo con todas las quejas locales */
    val allQuejas: Flow<List<QuejaEntity>> = administracionDao.getAllQuejas()

    /** Inserta una queja localmente y la envía a la API */
    suspend fun insertQueja(queja: QuejaEntity) {
        administracionDao.insertQueja(queja)
        try {
            apiService.createQueja(queja.toDto())
            Log.d(TAG, "Queja sincronizada con API: expediente ${queja.expedienteId}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar queja: ${e.message}")
        }
    }

    /** Actualiza una queja localmente y en la API */
    suspend fun updateQueja(queja: QuejaEntity) {
        administracionDao.updateQueja(queja)
        try {
            apiService.updateQueja(queja.id, queja.toDto())
            Log.d(TAG, "Queja actualizada en API: ID ${queja.id}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al actualizar queja en API: ${e.message}")
        }
    }

    /** Elimina una queja localmente y de la API */
    suspend fun deleteQueja(queja: QuejaEntity) {
        administracionDao.deleteQueja(queja)
        try {
            apiService.deleteQueja(queja.id)
            Log.d(TAG, "Queja eliminada de API: ID ${queja.id}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al eliminar queja de API: ${e.message}")
        }
    }

    /** Busca una queja por ID en la BD local */
    suspend fun getQuejaById(id: Int): QuejaEntity? {
        return administracionDao.getQuejaById(id)
    }

    /** Sincroniza pagos desde la API */
    suspend fun syncPagos() {
        try {
            val remotos = apiService.getPagos()
            remotos.forEach { dto ->
                administracionDao.insertPago(dto.toEntity())
            }
            Log.d(TAG, "Pagos sincronizados: ${remotos.size} registros")
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar pagos: ${e.message}")
        }
    }

    /** Sincroniza quejas desde la API */
    suspend fun syncQuejas() {
        try {
            val remotos = apiService.getQuejas()
            remotos.forEach { dto ->
                administracionDao.insertQueja(dto.toEntity())
            }
            Log.d(TAG, "Quejas sincronizadas: ${remotos.size} registros")
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar quejas: ${e.message}")
        }
    }
}
