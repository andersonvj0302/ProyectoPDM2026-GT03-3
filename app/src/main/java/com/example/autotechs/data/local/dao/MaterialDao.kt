package com.example.autotechs.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.autotechs.data.local.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity)

    @Update
    suspend fun updateMaterial(material: MaterialEntity)

    @Delete
    suspend fun deleteMaterial(material: MaterialEntity)

    @Query("SELECT * FROM materiales")
    fun getAllMateriales(): Flow<List<MaterialEntity>>

    @Query("SELECT * FROM materiales WHERE id = :id")
    suspend fun getMaterialById(id: Int): MaterialEntity?

    @Query("SELECT * FROM materiales WHERE expedienteId = :expedienteId")
    fun getMaterialesPorExpediente(expedienteId: Int): Flow<List<MaterialEntity>>
}
