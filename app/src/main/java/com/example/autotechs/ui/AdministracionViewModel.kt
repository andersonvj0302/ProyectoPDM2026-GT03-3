package com.example.autotechs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotechs.data.local.entity.PagoEntity
import com.example.autotechs.data.local.entity.QuejaEntity
import com.example.autotechs.data.repository.AdministracionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AdministracionViewModel(private val repository: AdministracionRepository) : ViewModel() {

    // Pagos
    val pagos: Flow<List<PagoEntity>> = repository.allPagos

    fun insertPago(expedienteId: Int, monto: Double, modalidad: String) {
        viewModelScope.launch {
            val pago = PagoEntity(
                expedienteId = expedienteId,
                monto = monto,
                modalidad = modalidad
            )
            repository.insertPago(pago)
        }
    }

    fun updatePago(id: Int, expedienteId: Int, monto: Double, modalidad: String) {
        viewModelScope.launch {
            val pago = PagoEntity(
                id = id,
                expedienteId = expedienteId,
                monto = monto,
                modalidad = modalidad
            )
            repository.updatePago(pago)
        }
    }

    fun deletePago(pago: PagoEntity) {
        viewModelScope.launch {
            repository.deletePago(pago)
        }
    }

    // Quejas
    val quejas: Flow<List<QuejaEntity>> = repository.allQuejas

    fun insertQueja(expedienteId: Int, descripcion: String, justificada: Boolean, montoReintegro: Double) {
        viewModelScope.launch {
            val queja = QuejaEntity(
                expedienteId = expedienteId,
                descripcion = descripcion,
                justificada = justificada,
                montoReintegro = montoReintegro
            )
            repository.insertQueja(queja)
        }
    }

    fun updateQueja(id: Int, expedienteId: Int, descripcion: String, justificada: Boolean, montoReintegro: Double) {
        viewModelScope.launch {
            val queja = QuejaEntity(
                id = id,
                expedienteId = expedienteId,
                descripcion = descripcion,
                justificada = justificada,
                montoReintegro = montoReintegro
            )
            repository.updateQueja(queja)
        }
    }

    fun deleteQueja(queja: QuejaEntity) {
        viewModelScope.launch {
            repository.deleteQueja(queja)
        }
    }
}
