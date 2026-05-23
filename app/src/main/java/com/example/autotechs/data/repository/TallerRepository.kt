package com.example.autotechs.data.repository

import android.util.Log
import com.example.autotechs.data.local.dao.TallerDao
import com.example.autotechs.data.local.entity.FaseReparacionEntity
import com.example.autotechs.data.remote.ApiService
import com.example.autotechs.data.remote.dto.toDto
import com.example.autotechs.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de Taller (Fases de Reparación) con sincronización API REST.
 * Gestiona el flujo operativo de las 6 fases de reparación de un siniestro.
 */
class TallerRepository(
    private val tallerDao: TallerDao,
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "TallerRepository"
    }

    /** Obtiene las fases de un expediente como flujo reactivo */
    fun getFases(expedienteId: Int): Flow<List<FaseReparacionEntity>> {
        return tallerDao.getFasesPorExpediente(expedienteId)
    }

    /**
     * Inicializa las 6 fases de reparación para un expediente nuevo.
     * Las fases siguen el flujo operativo del taller según el algoritmo.
     */
    suspend fun inicializarFases(expedienteId: Int) {
        val fasesIniciales = listOf(
            FaseReparacionEntity(expedienteId = expedienteId, nombreFase = "Desarme", orden = 1, estado = "PENDIENTE"),
            FaseReparacionEntity(expedienteId = expedienteId, nombreFase = "Enderezado", orden = 2, estado = "PENDIENTE"),
            FaseReparacionEntity(expedienteId = expedienteId, nombreFase = "Preparación", orden = 3, estado = "PENDIENTE"),
            FaseReparacionEntity(expedienteId = expedienteId, nombreFase = "Pintura", orden = 4, estado = "PENDIENTE"),
            FaseReparacionEntity(expedienteId = expedienteId, nombreFase = "Secado y Pulido", orden = 5, estado = "PENDIENTE"),
            FaseReparacionEntity(expedienteId = expedienteId, nombreFase = "Armado y Control", orden = 6, estado = "PENDIENTE")
        )
        tallerDao.insertFases(fasesIniciales)
    }

    /** Actualiza una fase localmente y la sincroniza con la API */
    suspend fun actualizarFase(fase: FaseReparacionEntity) {
        tallerDao.updateFase(fase)
        try {
            apiService.updateFase(fase.id, fase.toDto())
            Log.d(TAG, "Fase '${fase.nombreFase}' sincronizada con API: ${fase.estado}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar fase con API: ${e.message}")
        }
    }

    /** Obtiene las fases una sola vez (no reactivo), útil para lógica interna */
    suspend fun getFasesUnaVez(expedienteId: Int): List<FaseReparacionEntity> {
        return tallerDao.getFasesUnaVez(expedienteId)
    }

    /** Sincroniza fases de un expediente desde la API */
    suspend fun syncFases(expedienteId: Int) {
        try {
            val remotas = apiService.getFases(expedienteId)
            remotas.forEach { dto ->
                tallerDao.updateFase(dto.toEntity())
            }
            Log.d(TAG, "Fases sincronizadas para expediente $expedienteId")
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar fases: ${e.message}")
        }
    }
}
