package com.example.autotechs.data.repository

import com.example.autotechs.data.local.dao.RecepcionDao
import com.example.autotechs.data.local.entity.ExpedienteEntity
import com.example.autotechs.data.local.entity.PresupuestoEntity
import com.example.autotechs.data.local.entity.VehiculoEntity

class RecepcionRepository(private val recepcionDao: RecepcionDao) {

    suspend fun registrarIngresoTaller(
        vehiculo: VehiculoEntity,
        expediente: ExpedienteEntity,
        presupuestoInicial: PresupuestoEntity
    ) {
        // Llama a la transacción que guarda todo garantizando consistencia de datos
        recepcionDao.registrarIngresoCompleto(vehiculo, expediente, presupuestoInicial)
    }

    suspend fun getVehiculo(vin: String): VehiculoEntity? {
        return recepcionDao.getVehiculo(vin)
    }

    val allSiniestros: kotlinx.coroutines.flow.Flow<List<com.example.autotechs.data.local.entity.SiniestroWithVehiculo>> = 
        recepcionDao.getSiniestrosWithVehiculos()
}
