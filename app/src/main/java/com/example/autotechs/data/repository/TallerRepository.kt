package com.example.autotechs.data.repository

import com.example.autotechs.data.local.dao.TallerDao
import com.example.autotechs.data.local.entity.FaseReparacionEntity
import kotlinx.coroutines.flow.Flow

class TallerRepository(private val tallerDao: TallerDao) {

    fun getFases(expedienteId: Int): Flow<List<FaseReparacionEntity>> {
        return tallerDao.getFasesPorExpediente(expedienteId)
    }

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

    suspend fun actualizarFase(fase: FaseReparacionEntity) {
        tallerDao.updateFase(fase)
    }

    suspend fun getFasesUnaVez(expedienteId: Int): List<FaseReparacionEntity> {
        return tallerDao.getFasesUnaVez(expedienteId)
    }
}
