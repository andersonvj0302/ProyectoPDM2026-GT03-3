package com.example.autotechs.domain.usecase

import com.example.autotechs.domain.model.Presupuesto

class CalcularPresupuestoUseCase {
    
    // Calcula el costo del presupuesto basado en la tarifa por hora y costos fijos de materiales/repuestos
    fun invocar(
        horasEnderezado: Int,
        horasPintura: Int,
        horasArmado: Int,
        tarifaHora: Double,
        costoMateriales: Double,
        costoRepuestos: Double
    ): Presupuesto {
        
        val totalHoras = horasEnderezado + horasPintura + horasArmado
        val costoManoObra = totalHoras * tarifaHora
        
        return Presupuesto(
            costoManoObra = costoManoObra,
            costoMateriales = costoMateriales,
            costoRepuestos = costoRepuestos,
            tiempoEstimadoHoras = totalHoras
        )
    }
}
