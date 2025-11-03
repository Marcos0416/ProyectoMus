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
    suspend fun insert(progress: ProgressEntity)

    @Query("SELECT * FROM progress_table WHERE username = :username")
    suspend fun getProgress(username: String): ProgressEntity?

    @Update
    suspend fun update(progress: ProgressEntity)
}
