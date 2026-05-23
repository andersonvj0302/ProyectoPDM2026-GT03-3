package com.example.autotechs.data.repository

import android.util.Log
import com.example.autotechs.data.local.dao.MaterialDao
import com.example.autotechs.data.local.entity.MaterialEntity
import com.example.autotechs.data.remote.ApiService
import com.example.autotechs.data.remote.dto.toDto
import com.example.autotechs.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de Materiales (Inventario) con sincronización API REST.
 * Gestiona el registro de materiales usados en las reparaciones.
 */
class MaterialRepository(
    private val materialDao: MaterialDao,
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "MaterialRepository"
    }

    /** Flujo reactivo con todos los materiales locales */
    val allMateriales: Flow<List<MaterialEntity>> = materialDao.getAllMateriales()

    /** Inserta un material localmente y lo envía a la API */
    suspend fun insert(material: MaterialEntity) {
        materialDao.insertMaterial(material)
        try {
            apiService.createMaterial(material.toDto())
            Log.d(TAG, "Material creado en API: ${material.nombre}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al crear material en API: ${e.message}")
        }
    }

    /** Actualiza un material localmente y en la API */
    suspend fun update(material: MaterialEntity) {
        materialDao.updateMaterial(material)
        try {
            apiService.updateMaterial(material.id, material.toDto())
            Log.d(TAG, "Material actualizado en API: ${material.nombre}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al actualizar material en API: ${e.message}")
        }
    }

    /** Elimina un material localmente y de la API */
    suspend fun delete(material: MaterialEntity) {
        materialDao.deleteMaterial(material)
        try {
            apiService.deleteMaterial(material.id)
            Log.d(TAG, "Material eliminado de API: ID ${material.id}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al eliminar material de API: ${e.message}")
        }
    }

    /** Busca un material por ID en la BD local */
    suspend fun getMaterialById(id: Int): MaterialEntity? {
        return materialDao.getMaterialById(id)
    }

    /** Obtiene materiales filtrados por expediente */
    fun getMaterialesPorExpediente(expedienteId: Int): Flow<List<MaterialEntity>> {
        return materialDao.getMaterialesPorExpediente(expedienteId)
    }

    /** Sincroniza materiales desde la API y los guarda en Room */
    suspend fun syncMateriales() {
        try {
            val remotos = apiService.getMateriales()
            remotos.forEach { dto ->
                materialDao.insertMaterial(dto.toEntity())
            }
            Log.d(TAG, "Materiales sincronizados: ${remotos.size} registros")
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar materiales: ${e.message}")
        }
    }
}
