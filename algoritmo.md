ALGORITMO Sistema_Gestion_Taller_Siniestros
 
ESTRUCTURAS DE DATOS
    Usuario: ID, Nombre, Rol (Administrador, Tecnico, Aseguradora)
    Vehiculo: VIN, Placa, Marca, Modelo
    Presupuesto: ManoObra, Repuestos, Materiales, Total, TiempoEstimado
    Poliza: Tipo (Deducible o Franquicia), ValorReferencia, CeroDeducible (Si/No)
 
INICIO
 
    // 1. AUTENTICACIÓN Y ROLES
    ESCRIBIR "Ingrese usuario y contraseña:"
    LEER credenciales
    Si credenciales son validas ENTONCES
        Definir RolUsuario (Admin, Tecnico, Aseguradora)
    SINO
        DETENER "Acceso denegado"
 
    // 2. RECEPCIÓN E INSPECCIÓN INICIAL (Rol: Tecnico/Admin)
    ESCRIBIR "Registro de Vehículo y Evaluación de Daños"
    CREAR ExpedienteVehiculo
    SUBIR_FOTOS "Fotos_Siniestro"
    REGISTRAR Daños(Estructurales, Esteticos, Mecanicos)
 
    // 3. ELABORACIÓN DEL PRESUPUESTO
    CALCULAR CostoManoObra = (Horas_Enderezado + Horas_Pintura + Horas_Armado) * TarifaHora
    CALCULAR CostoMateriales = SUMAR_LISTA(Pintura, Masilla, Lijas, etc.)
    CALCULAR CostoRepuestos = CONSULTAR_PRECIO(Piezas_Nuevas)
    
    TotalPresupuesto = CostoManoObra + CostoMateriales + CostoRepuestos
    ESTABLECER TiempoEstimado
 
    // 4. PROCESO DE SEGURO Y VALIDACIÓN FINANCIERA (Rol: Aseguradora)
    SI Cliente_Tiene_Seguro = VERDADERO ENTONCES
        ESCRIBIR "Enviando presupuesto a plataforma de aseguradora..."
        LEER PolizaCliente
        
        SI Validar_Condiciones_Legales(Licencia, Sobriedad, Documentos) = FALSO ENTONCES
            DETENER "Reclamo Rechazado por Incumplimiento Contractual"
        FIN SI
 
        SI Validar_Justicia_Costos(TotalPresupuesto) = FALSO ENTONCES
            AJUSTAR Presupuesto
        FIN SI
 
        // Lógica de Deducible vs Franquicia
        SI PolizaCliente.Tipo = "Deducible" ENTONCES
            SI TotalPresupuesto > PolizaCliente.ValorReferencia ENTONCES
                MontoPagaCliente = PolizaCliente.ValorReferencia
                MontoPagaAseguradora = TotalPresupuesto - MontoPagaCliente
                ESCRIBIR "Reparación Autorizada (Caso 1)"
            SINO
                MontoPagaCliente = TotalPresupuesto
                MontoPagaAseguradora = 0
                ESCRIBIR "Reparación NO Autorizada por Seguro - Cliente paga el 100% (Caso 3)"
            FIN SI
 
        SINO SI PolizaCliente.Tipo = "Franquicia" ENTONCES
            SI TotalPresupuesto > PolizaCliente.ValorReferencia ENTONCES
                MontoPagaCliente = 0
                MontoPagaAseguradora = TotalPresupuesto
                ESCRIBIR "Reparación Autorizada (Caso 2)"
            SINO
                MontoPagaCliente = TotalPresupuesto
                MontoPagaAseguradora = 0
                ESCRIBIR "Reparación NO Autorizada por Seguro - Menor a Franquicia"
            FIN SI
        FIN SI
    SINO
        MontoPagaCliente = TotalPresupuesto
        ESCRIBIR "Cliente particular: Pago al 100%"
    FIN SI
 
    // 5. FLUJO DE TRABAJO EN TALLER (OPERATIVO)
    SI Reparacion_Aprobada = VERDADERO ENTONCES
        FASE 1: Desarme -> IDENTIFICAR piezas_reutilizables
        FASE 2: Enderezado -> USAR Bancos_Enderezado
        FASE 3: Preparacion -> REGISTRAR_CONSUMO(Lijas, Masillas, Desengrasantes)
        FASE 4: Pintura -> REGISTRAR_CONSUMO(Primer, Color, Barniz, Thinner)
        FASE 5: Secado y Pulido -> VERIFICAR Acabado Final
        FASE 6: Armado -> REINSTALAR Partes + Ajustar Alineacion
        
        CONTROL_CALIDAD:
            SI Pasa_Revision(Estetica, Funcionalidad) = FALSO ENTONCES
                REGRESAR A FASE Reparacion
            FIN SI
    FIN SI
 
    // 6. CIERRE, PAGO E INVENTARIO
    ACTUALIZAR Inventario (Restar materiales utilizados de stock)
    GENERAR Factura(MontoPagaCliente, MontoPagaAseguradora)
    REGISTRAR MetodoPago (Efectivo, Tarjeta, Transferencia)
    
    ENTREGAR Vehiculo
    ESCRIBIR "Explicar garantías al cliente"
 
    // 7. MÓDULO DE RECLAMOS (POST-VENTA)
    SI Cliente_Presenta_Inconformidad = VERDADERO ENTONCES
        EVALUAR "Garantía de Reparación"
        SI Procede_Garantia = VERDADERO ENTONCES
            RE-INGRESAR Vehiculo al Sistema (Costo $0 para cliente)
        FIN SI
    FIN SI
 
FIN
