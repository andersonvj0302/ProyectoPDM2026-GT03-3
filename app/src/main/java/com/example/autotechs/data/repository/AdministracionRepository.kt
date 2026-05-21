package com.example.autotechs.data.repository

import com.example.autotechs.data.local.dao.AdministracionDao
import com.example.autotechs.data.local.entity.PagoEntity
import com.example.autotechs.data.local.entity.QuejaEntity
import kotlinx.coroutines.flow.Flow

class AdministracionRepository(private val administracionDao: AdministracionDao) {

    // Pagos Flow and CRUD
    val allPagos: Flow<List<PagoEntity>> = administracionDao.getAllPagos()

    suspend fun insertPago(pago: PagoEntity) {
        administracionDao.insertPago(pago)
    }

    suspend fun updatePago(pago: PagoEntity) {
        administracionDao.updatePago(pago)
    }

    suspend fun deletePago(pago: PagoEntity) {
        administracionDao.deletePago(pago)
    }

    suspend fun getPagoById(id: Int): PagoEntity? {
        return administracionDao.getPagoById(id)
    }

    // Quejas Flow and CRUD
    val allQuejas: Flow<List<QuejaEntity>> = administracionDao.getAllQuejas()

    suspend fun insertQueja(queja: QuejaEntity) {
        administracionDao.insertQueja(queja)
    }

    suspend fun updateQueja(queja: QuejaEntity) {
        administracionDao.updateQueja(queja)
    }

    suspend fun deleteQueja(queja: QuejaEntity) {
        administracionDao.deleteQueja(queja)
    }

    suspend fun getQuejaById(id: Int): QuejaEntity? {
        return administracionDao.getQuejaById(id)
    }
}
