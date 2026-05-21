package com.example.autotechs.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.autotechs.data.local.dao.UsuarioDao
import com.example.autotechs.data.local.entity.UsuarioEntity
import com.example.autotechs.data.local.entity.ExpedienteEntity
import com.example.autotechs.data.local.entity.PresupuestoEntity
import com.example.autotechs.data.local.entity.VehiculoEntity
import com.example.autotechs.data.local.entity.FaseReparacionEntity
import com.example.autotechs.data.local.dao.RecepcionDao
import com.example.autotechs.data.local.dao.TallerDao
import com.example.autotechs.data.local.entity.TarifaEntity
import com.example.autotechs.data.local.entity.MaterialEntity
import com.example.autotechs.data.local.entity.PagoEntity
import com.example.autotechs.data.local.entity.QuejaEntity
import com.example.autotechs.data.local.entity.ClienteEntity
import com.example.autotechs.data.local.dao.ClienteDao
import com.example.autotechs.data.local.dao.VehiculoDao
import com.example.autotechs.data.local.dao.MaterialDao
import com.example.autotechs.data.local.dao.AdministracionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Define la configuración de la base de datos, incluyendo las tablas (entities) y la versión.
@Database(
    entities = [
        UsuarioEntity::class, 
        VehiculoEntity::class, 
        ExpedienteEntity::class, 
        PresupuestoEntity::class,
        FaseReparacionEntity::class,
        TarifaEntity::class,
        MaterialEntity::class,
        PagoEntity::class,
        QuejaEntity::class,
        ClienteEntity::class
    ], 
    version = 8, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Retorna el Data Access Object (DAO) para interactuar con la tabla de Usuarios
    abstract fun usuarioDao(): UsuarioDao
    
    // Retorna el DAO para Recepción y Siniestros
    abstract fun recepcionDao(): RecepcionDao

    // Retorna el DAO para Taller y Fases de Reparación
    abstract fun tallerDao(): TallerDao

    // Retorna el DAO para Clientes
    abstract fun clienteDao(): ClienteDao

    // Retorna el DAO para Vehículos
    abstract fun vehiculoDao(): VehiculoDao

    // Retorna el DAO para Materiales (Inventario)
    abstract fun materialDao(): MaterialDao

    // Retorna el DAO para Administración (Pagos y Quejas)
    abstract fun administracionDao(): AdministracionDao

    companion object {
        // Volatile asegura que los cambios en INSTANCE sean visibles inmediatamente para otros hilos.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Implementa el patrón Singleton para asegurar que solo exista una instancia de la BD en toda la app.
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "autotechs_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(SeedDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // Callback que inserta el usuario administrador y dueño al crear o abrir la base de datos
    private class SeedDatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            seedUsers()
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            seedUsers()
        }

        private fun seedUsers() {
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = database.usuarioDao()

                    // Crear usuario Administrador si no existe
                    val existingAdmin = dao.getUserByEmail("admin")
                    if (existingAdmin == null) {
                        val admin = UsuarioEntity(
                            nombre = "Administrador",
                            email = "admin",
                            contrasena = "root",
                            rol = "Administrador"
                        )
                        dao.insertUsuario(admin)
                    }

                    // Crear usuario Dueño del Taller si no existe
                    val existingDueno = dao.getUserByEmail("dueno")
                    if (existingDueno == null) {
                        val dueno = UsuarioEntity(
                            nombre = "Dueño del Taller",
                            email = "dueno",
                            contrasena = "root",
                            rol = "Dueño"
                        )
                        dao.insertUsuario(dueno)
                    }
                }
            }
        }
    }
}
