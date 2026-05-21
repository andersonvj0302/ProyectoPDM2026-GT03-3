package com.example.autotechs.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.autotechs.data.local.entity.PagoEntity
import com.example.autotechs.data.local.entity.QuejaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdministracionDao {
    // Pagos CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPago(pago: PagoEntity)

    @Update
    suspend fun updatePago(pago: PagoEntity)

    @Delete
    suspend fun deletePago(pago: PagoEntity)

    @Query("SELECT * FROM pagos ORDER BY fechaPago DESC")
    fun getAllPagos(): Flow<List<PagoEntity>>

    @Query("SELECT * FROM pagos WHERE id = :id")
    suspend fun getPagoById(id: Int): PagoEntity?

    // Quejas CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueja(queja: QuejaEntity)

    @Update
    suspend fun updateQueja(queja: QuejaEntity)

    @Delete
    suspend fun deleteQueja(queja: QuejaEntity)

    @Query("SELECT * FROM quejas ORDER BY fechaQueja DESC")
    fun getAllQuejas(): Flow<List<QuejaEntity>>

    @Query("SELECT * FROM quejas WHERE id = :id")
    suspend fun getQuejaById(id: Int): QuejaEntity?
}
