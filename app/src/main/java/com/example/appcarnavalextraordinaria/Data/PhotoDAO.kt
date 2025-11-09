package com.example.appcarnavalextraordinaria.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ProgressEntity)

    @Query("SELECT * FROM progress WHERE userId = :userId") // ✅ coincide con tableName
    suspend fun getProgress(userId: Int): List<ProgressEntity>

    @Query("SELECT * FROM progress WHERE userId = :userId ORDER BY lastAccess DESC")
    fun getProgressByUser(userId: Int): kotlinx.coroutines.flow.Flow<List<ProgressEntity>>

    @Update
    suspend fun update(progress: ProgressEntity)
}
