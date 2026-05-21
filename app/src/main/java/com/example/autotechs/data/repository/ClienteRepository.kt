package com.example.autotechs.data.repository

import com.example.autotechs.data.local.dao.ClienteDao
import com.example.autotechs.data.local.entity.ClienteEntity
import kotlinx.coroutines.flow.Flow

class ClienteRepository(private val clienteDao: ClienteDao) {
    val allClientes: Flow<List<ClienteEntity>> = clienteDao.getAllClientes()

    suspend fun getClienteById(id: Int): ClienteEntity? {
        return clienteDao.getClienteById(id)
    }

    suspend fun insertCliente(cliente: ClienteEntity) {
        clienteDao.insertCliente(cliente)
    }

    suspend fun updateCliente(cliente: ClienteEntity) {
        clienteDao.updateCliente(cliente)
    }

    suspend fun deleteCliente(cliente: ClienteEntity) {
        clienteDao.deleteCliente(cliente)
    }
}
