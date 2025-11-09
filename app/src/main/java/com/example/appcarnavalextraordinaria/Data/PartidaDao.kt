package com.example.appcarnavalextraordinaria.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDao {
    @Insert
    suspend fun insertPartida(partida: PartidaEntity): Long

    @Query("UPDATE partidas SET resultado = :resultado WHERE id = :partidaId")
    suspend fun updateResultado(partidaId: Int, resultado: String)

    @Update
    suspend fun updatePartida(partida: PartidaEntity)

    @Query("SELECT * FROM partidas WHERE createdBy = :userId ORDER BY fecha DESC")
    fun getPartidasByUser(userId: Int): Flow<List<PartidaEntity>>

    @Query("SELECT * FROM partidas WHERE id = :id LIMIT 1")
    suspend fun getPartidaById(id: Int): PartidaEntity?
}