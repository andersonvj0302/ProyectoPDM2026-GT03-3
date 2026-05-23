package com.example.autotechs.data.local.dao

import androidx.room.*
import com.example.autotechs.data.local.entity.ClienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes ORDER BY nombre ASC")
    fun getAllClientes(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM clientes WHERE id = :id")
    suspend fun getClienteById(id: Int): ClienteEntity?

    /** Busca un cliente por email — usado para tracking de reparación del cliente */
    @Query("SELECT * FROM clientes WHERE email = :email LIMIT 1")
    suspend fun getClienteByEmail(email: String): ClienteEntity?



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCliente(cliente: ClienteEntity)

    @Update
    suspend fun updateCliente(cliente: ClienteEntity)

    @Delete
    suspend fun deleteCliente(cliente: ClienteEntity)
}
