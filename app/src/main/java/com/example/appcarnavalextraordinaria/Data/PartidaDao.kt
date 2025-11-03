package com.example.appcarnavalextraordinaria.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDao {
    @Insert
    suspend fun insertPartida(partida: PartidaEntity): Long

    @Query("SELECT * FROM partidas WHERE createdBy = :userId ORDER BY fecha DESC")
    fun getPartidasByUser(userId: Int): Flow<List<PartidaEntity>>
}