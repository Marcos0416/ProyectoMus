package com.example.appcarnavalextraordinaria.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserEntity::class,
        PartidaEntity::class,
        MovimientoEntity::class,
        ProgressEntity::class,
        TestEntity::class,
        QuestionEntity::class,
        TestResultEntity::class
    ],
    version = 19, // Incrementa la versión
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun partidaDao(): PartidaDao
    abstract fun movimientoDao(): MovimientoDao
    abstract fun progressDao(): ProgressDao
    abstract fun testDao(): TestDao
    abstract fun testResultDao(): TestResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            println("DEBUG: Base de datos CREADA desde cero")
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            println("DEBUG: Base de datos ABIERTA")
                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}