package com.example.autotechs.domain.usecase

import com.example.autotechs.domain.model.Poliza
import com.example.autotechs.domain.model.Presupuesto
import com.example.autotechs.domain.model.TipoPoliza

data class ResultadoPagoSeguro(
    val montoCliente: Double,
    val montoAseguradora: Double,
    val mensaje: String
)

class ValidarSeguroUseCase {

    // Evalúa quién debe pagar la reparación (Cliente vs Aseguradora) basándose en las reglas de negocio
    fun invocar(presupuesto: Presupuesto, poliza: Poliza): ResultadoPagoSeguro {
        val total = presupuesto.total
        
        // Lógica de Deducible: El cliente paga hasta el monto del deducible (valorReferencia)
        return if (poliza.tipo == TipoPoliza.DEDUCIBLE) {
            if (total > poliza.valorReferencia) {
                // Si la reparación es mayor al deducible, el seguro cubre la diferencia
                ResultadoPagoSeguro(
                    montoCliente = poliza.valorReferencia,
                    montoAseguradora = total - poliza.valorReferencia,
                    mensaje = "Reparación Autorizada (Caso 1)"
                )
            } else {
                // Si la reparación es menor al deducible, el cliente asume todo el costo
                ResultadoPagoSeguro(
                    montoCliente = total,
                    montoAseguradora = 0.0,
                    mensaje = "Reparación NO Autorizada - Cliente paga 100% (Caso 3)"
                )
            }
        } else { 
            // Lógica de Franquicia: Si pasa un límite (valorReferencia), el seguro paga TODO.
            if (total > poliza.valorReferencia) {
                ResultadoPagoSeguro(
                    montoCliente = 0.0,
                    montoAseguradora = total,
                    mensaje = "Reparación Autorizada (Caso 2)"
                )
            } else {
                ResultadoPagoSeguro(
                    montoCliente = total,
                    montoAseguradora = 0.0,
                    mensaje = "Reparación NO Autorizada - Menor a Franquicia"
                )
            }
        }
    }
}
