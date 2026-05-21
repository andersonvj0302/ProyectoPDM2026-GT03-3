package com.example.autotechs.domain.usecase

import com.example.autotechs.data.local.entity.QuejaEntity

class ProcesarQuejaUseCase {

    /**
     * Evalúa una queja para determinar si procede un reintegro.
     * Regla de negocio simulada:
     * Si la queja es justificada, se reintegra un porcentaje del costo o un monto fijo.
     */
    fun evaluarQueja(expedienteId: Int, descripcion: String, justificada: Boolean, costoTotalReparacion: Double): QuejaEntity {
        // Si está justificada, se le reintegra el 20% del costo total de la reparación como compensación
        val reintegro = if (justificada) {
            costoTotalReparacion * 0.20
        } else {
            0.0
        }

        return QuejaEntity(
            expedienteId = expedienteId,
            descripcion = descripcion,
            justificada = justificada,
            montoReintegro = reintegro
        )
    }
}
