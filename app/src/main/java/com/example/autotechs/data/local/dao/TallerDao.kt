package com.example.autotechs.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.autotechs.data.local.entity.FaseReparacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TallerDao {
    
    // Inserta una o más fases iniciales para un expediente
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFases(fases: List<FaseReparacionEntity>)

    // Obtiene todas las fases de un expediente en orden secuencial
    @Query("SELECT * FROM fases_reparacion WHERE expedienteId = :expedienteId ORDER BY orden ASC")
    fun getFasesPorExpediente(expedienteId: Int): Flow<List<FaseReparacionEntity>>

    @Query("SELECT * FROM fases_reparacion WHERE expedienteId = :expedienteId")
    suspend fun getFasesUnaVez(expedienteId: Int): List<FaseReparacionEntity>

    // Actualiza una fase (ej. cambiar de PENDIENTE a COMPLETADO)
    @Update
    suspend fun updateFase(fase: FaseReparacionEntity)
}
