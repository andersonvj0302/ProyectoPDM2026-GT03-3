package com.example.autotechs.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.autotechs.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    // Retorna un flujo reactivo (Flow) con la lista de todos los usuarios.
    // Al usar Flow, cualquier cambio en la tabla 'usuarios' actualizará automáticamente la UI.
    @Query("SELECT * FROM usuarios")
    fun getAllUsuarios(): Flow<List<UsuarioEntity>>

    // Inserta un nuevo usuario en la base de datos.
    // Si ya existe un usuario con el mismo ID, lo reemplaza (REPLACE).
    // Es una función 'suspend' porque debe ejecutarse dentro de una Corrutina (asíncrono).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE email = :email AND contrasena = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UsuarioEntity?
}
