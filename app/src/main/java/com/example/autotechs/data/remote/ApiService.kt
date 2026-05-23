package com.example.autotechs.data.remote

import com.example.autotechs.data.remote.dto.*
import retrofit2.http.*

/**
 * Interfaz que define todos los endpoints de la API REST.
 * Retrofit genera la implementación automáticamente.
 * Cada método es 'suspend' para funcionar con corrutinas de Kotlin.
 */
interface ApiService {

    // ═══════════════════════════════════════════
    //  USUARIOS - Autenticación y gestión
    // ═══════════════════════════════════════════

    /** Obtiene la lista completa de usuarios del servidor */
    @GET("api/usuarios")
    suspend fun getUsuarios(): List<UsuarioDto>

    /** Obtiene un usuario específico por su ID */
    @GET("api/usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): UsuarioDto

    /** Crea un nuevo usuario en el servidor (registro) */
    @POST("api/usuarios")
    suspend fun createUsuario(@Body usuario: UsuarioDto): UsuarioDto

    /** Realiza login en el servidor con credenciales */
    @POST("api/usuarios/login")
    suspend fun login(@Body credentials: LoginRequest): UsuarioDto

    // ═══════════════════════════════════════════
    //  CLIENTES - CRUD completo
    // ═══════════════════════════════════════════

    /** Lista todos los clientes registrados */
    @GET("api/clientes")
    suspend fun getClientes(): List<ClienteDto>

    /** Crea un nuevo cliente */
    @POST("api/clientes")
    suspend fun createCliente(@Body cliente: ClienteDto): ClienteDto

    /** Actualiza un cliente existente por ID */
    @PUT("api/clientes/{id}")
    suspend fun updateCliente(@Path("id") id: Int, @Body cliente: ClienteDto): ClienteDto

    /** Elimina un cliente por ID */
    @DELETE("api/clientes/{id}")
    suspend fun deleteCliente(@Path("id") id: Int)

    // ═══════════════════════════════════════════
    //  VEHÍCULOS - CRUD completo
    // ═══════════════════════════════════════════

    /** Lista todos los vehículos registrados */
    @GET("api/vehiculos")
    suspend fun getVehiculos(): List<VehiculoDto>

    /** Crea un nuevo vehículo */
    @POST("api/vehiculos")
    suspend fun createVehiculo(@Body vehiculo: VehiculoDto): VehiculoDto

    /** Actualiza un vehículo existente por VIN */
    @PUT("api/vehiculos/{vin}")
    suspend fun updateVehiculo(@Path("vin") vin: String, @Body vehiculo: VehiculoDto): VehiculoDto

    /** Elimina un vehículo por VIN */
    @DELETE("api/vehiculos/{vin}")
    suspend fun deleteVehiculo(@Path("vin") vin: String)

    // ═══════════════════════════════════════════
    //  EXPEDIENTES (Siniestros) - Lectura y creación
    // ═══════════════════════════════════════════

    /** Lista todos los expedientes/siniestros */
    @GET("api/expedientes")
    suspend fun getExpedientes(): List<ExpedienteDto>

    /** Crea un nuevo expediente de siniestro */
    @POST("api/expedientes")
    suspend fun createExpediente(@Body expediente: ExpedienteDto): ExpedienteDto

    // ═══════════════════════════════════════════
    //  PRESUPUESTOS - Lectura y creación
    // ═══════════════════════════════════════════

    /** Lista todos los presupuestos */
    @GET("api/presupuestos")
    suspend fun getPresupuestos(): List<PresupuestoDto>

    /** Crea un nuevo presupuesto */
    @POST("api/presupuestos")
    suspend fun createPresupuesto(@Body presupuesto: PresupuestoDto): PresupuestoDto

    // ═══════════════════════════════════════════
    //  MATERIALES (Inventario) - CRUD completo
    // ═══════════════════════════════════════════

    /** Lista todos los materiales del inventario */
    @GET("api/materiales")
    suspend fun getMateriales(): List<MaterialDto>

    /** Crea un nuevo material */
    @POST("api/materiales")
    suspend fun createMaterial(@Body material: MaterialDto): MaterialDto

    /** Actualiza un material existente */
    @PUT("api/materiales/{id}")
    suspend fun updateMaterial(@Path("id") id: Int, @Body material: MaterialDto): MaterialDto

    /** Elimina un material por ID */
    @DELETE("api/materiales/{id}")
    suspend fun deleteMaterial(@Path("id") id: Int)

    // ═══════════════════════════════════════════
    //  PAGOS - CRUD
    // ═══════════════════════════════════════════

    /** Lista todos los pagos registrados */
    @GET("api/pagos")
    suspend fun getPagos(): List<PagoDto>

    /** Registra un nuevo pago */
    @POST("api/pagos")
    suspend fun createPago(@Body pago: PagoDto): PagoDto

    /** Actualiza un pago existente */
    @PUT("api/pagos/{id}")
    suspend fun updatePago(@Path("id") id: Int, @Body pago: PagoDto): PagoDto

    /** Elimina un pago por ID */
    @DELETE("api/pagos/{id}")
    suspend fun deletePago(@Path("id") id: Int)

    // ═══════════════════════════════════════════
    //  QUEJAS (Reclamos post-venta) - CRUD
    // ═══════════════════════════════════════════

    /** Lista todas las quejas/reclamos */
    @GET("api/quejas")
    suspend fun getQuejas(): List<QuejaDto>

    /** Registra una nueva queja */
    @POST("api/quejas")
    suspend fun createQueja(@Body queja: QuejaDto): QuejaDto

    /** Actualiza una queja existente */
    @PUT("api/quejas/{id}")
    suspend fun updateQueja(@Path("id") id: Int, @Body queja: QuejaDto): QuejaDto

    /** Elimina una queja por ID */
    @DELETE("api/quejas/{id}")
    suspend fun deleteQueja(@Path("id") id: Int)

    // ═══════════════════════════════════════════
    //  FASES DE REPARACIÓN - Lectura y actualización
    // ═══════════════════════════════════════════

    /** Obtiene las fases de reparación de un expediente */
    @GET("api/fases/{expedienteId}")
    suspend fun getFases(@Path("expedienteId") expedienteId: Int): List<FaseReparacionDto>

    /** Actualiza el estado de una fase de reparación */
    @PUT("api/fases/{id}")
    suspend fun updateFase(@Path("id") id: Int, @Body fase: FaseReparacionDto): FaseReparacionDto
}
