package com.example.autotechs.domain.usecase

import com.example.autotechs.data.local.entity.MaterialEntity
import com.example.autotechs.data.local.entity.TarifaEntity

class CalcularTarifaUseCase {

    // Calcula el subtotal basado en la tarifa aplicable
    fun calcularCostoManoObra(tarifa: TarifaEntity): Double {
        // Podríamos agregar recargos adicionales aquí según reglas de negocio
        return tarifa.costoBase
    }

    // Suma el costo total de todos los materiales gastados en la reparación
    fun calcularCostoMateriales(materiales: List<MaterialEntity>): Double {
        return materiales.sumOf { it.costoTotal }
    }

    // Calcula el gran total de la reparación (Mano de obra + Materiales)
    fun calcularTotalReparacion(tarifa: TarifaEntity, materiales: List<MaterialEntity>): Double {
        val manoObra = calcularCostoManoObra(tarifa)
        val costoMateriales = calcularCostoMateriales(materiales)
        return manoObra + costoMateriales
    }
}
