package com.example.autotechs.domain.model

data class Poliza(
    val tipo: TipoPoliza, // Deducible o Franquicia
    val valorReferencia: Double,
    val ceroDeducible: Boolean
)

enum class TipoPoliza {
    DEDUCIBLE, FRANQUICIA
}

data class Presupuesto(
    val costoManoObra: Double,
    val costoMateriales: Double,
    val costoRepuestos: Double,
    val tiempoEstimadoHoras: Int
) {
    val total: Double get() = costoManoObra + costoMateriales + costoRepuestos
}
