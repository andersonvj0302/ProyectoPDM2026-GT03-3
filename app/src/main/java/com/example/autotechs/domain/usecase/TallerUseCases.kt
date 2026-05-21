package com.example.autotechs.domain.usecase

import com.example.autotechs.data.local.entity.FaseReparacionEntity

class AvanzarFaseUseCase {
    
    // Cambia el estado de una fase a COMPLETADO
    fun marcarComoCompletada(fase: FaseReparacionEntity): FaseReparacionEntity {
        if (fase.estado == "COMPLETADO") return fase
        return fase.copy(estado = "COMPLETADO")
    }

    // Inicia una fase (cambia de PENDIENTE a EN_PROCESO)
    fun iniciarFase(fase: FaseReparacionEntity): FaseReparacionEntity {
        if (fase.estado == "PENDIENTE") {
            return fase.copy(estado = "EN_PROCESO")
        }
        return fase
    }
}

class ControlCalidadUseCase {

    // Evalúa si pasa revisión. Si NO pasa, la fase actual debe retroceder.
    fun evaluarRevision(pasaRevision: Boolean, faseActual: FaseReparacionEntity, notas: String): FaseReparacionEntity {
        return if (pasaRevision) {
            faseActual.copy(estado = "COMPLETADO", notasControlCalidad = "Aprobado: $notas")
        } else {
            faseActual.copy(estado = "RECHAZADO", notasControlCalidad = "Rechazado: $notas - Requiere reproceso")
        }
    }
}
