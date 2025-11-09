package com.example.appcarnavalextraordinaria.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        PartidaEntity::class,
        MovimientoEntity::class,
        ProgressEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // --- DAOs ---
    abstract fun userDao(): UserDao
    abstract fun partidaDao(): PartidaDao
    abstract fun movimientoDao(): MovimientoDao
    abstract fun progressDao(): ProgressDao

    // --- Singleton para obtener la base de datos ---
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si ya existe la instancia, la devuelve. Si no, la crea.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database" // nombre del archivo de la base de datos
                )
                    // ⚠️ Solo durante desarrollo: borra y recrea la BBDD si cambia el número de versión
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}