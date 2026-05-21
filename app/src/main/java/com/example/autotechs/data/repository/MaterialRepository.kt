package com.example.autotechs.data.repository

import com.example.autotechs.data.local.dao.MaterialDao
import com.example.autotechs.data.local.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow

class MaterialRepository(private val materialDao: MaterialDao) {

    val allMateriales: Flow<List<MaterialEntity>> = materialDao.getAllMateriales()

    suspend fun insert(material: MaterialEntity) {
        materialDao.insertMaterial(material)
    }

    suspend fun update(material: MaterialEntity) {
        materialDao.updateMaterial(material)
    }

    suspend fun delete(material: MaterialEntity) {
        materialDao.deleteMaterial(material)
    }

    suspend fun getMaterialById(id: Int): MaterialEntity? {
        return materialDao.getMaterialById(id)
    }

    fun getMaterialesPorExpediente(expedienteId: Int): Flow<List<MaterialEntity>> {
        return materialDao.getMaterialesPorExpediente(expedienteId)
    }
}
