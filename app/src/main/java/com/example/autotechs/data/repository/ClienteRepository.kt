package com.example.autotechs.data.repository

import android.util.Log
import com.example.autotechs.data.local.dao.ClienteDao
import com.example.autotechs.data.local.entity.ClienteEntity
import com.example.autotechs.data.remote.ApiService
import com.example.autotechs.data.remote.dto.toDto
import com.example.autotechs.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de Clientes con sincronización local-first.
 * Las operaciones CRUD se ejecutan primero en Room y luego se sincronizan con la API.
 */
class ClienteRepository(
    private val clienteDao: ClienteDao,
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "ClienteRepository"
    }

    /** Flujo reactivo con todos los clientes locales */
    val allClientes: Flow<List<ClienteEntity>> = clienteDao.getAllClientes()

    /** Obtiene un cliente por ID desde la BD local */
    suspend fun getClienteById(id: Int): ClienteEntity? {
        return clienteDao.getClienteById(id)
    }

    /** Inserta un cliente localmente y lo envía a la API */
    suspend fun insertCliente(cliente: ClienteEntity) {
        clienteDao.insertCliente(cliente)
        try {
            apiService.createCliente(cliente.toDto())
            Log.d(TAG, "Cliente creado en API: ${cliente.nombre}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al crear cliente en API: ${e.message}")
        }
    }

    /** Actualiza un cliente localmente y en la API */
    suspend fun updateCliente(cliente: ClienteEntity) {
        clienteDao.updateCliente(cliente)
        try {
            apiService.updateCliente(cliente.id, cliente.toDto())
            Log.d(TAG, "Cliente actualizado en API: ${cliente.nombre}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al actualizar cliente en API: ${e.message}")
        }
    }

    /** Elimina un cliente localmente y en la API */
    suspend fun deleteCliente(cliente: ClienteEntity) {
        clienteDao.deleteCliente(cliente)
        try {
            apiService.deleteCliente(cliente.id)
            Log.d(TAG, "Cliente eliminado de API: ID ${cliente.id}")
        } catch (e: Exception) {
            Log.w(TAG, "Error al eliminar cliente de API: ${e.message}")
        }
    }

    /**
     * Sincroniza clientes desde la API: descarga todos y los guarda en Room.
     * Útil para obtener datos creados desde otros dispositivos o la web.
     */
    suspend fun syncClientes() {
        try {
            val remotos = apiService.getClientes()
            remotos.forEach { dto ->
                clienteDao.insertCliente(dto.toEntity())
            }
            Log.d(TAG, "Clientes sincronizados: ${remotos.size} registros")
        } catch (e: Exception) {
            Log.w(TAG, "Error al sincronizar clientes: ${e.message}")
        }
    }
}
