package com.example.appcarnavalextraordinaria.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovimiento(movimiento: MovimientoEntity): Long

    @Query("SELECT * FROM movimientos WHERE partidaId = :partidaId ORDER BY timestamp ASC")
    fun getMovimientosByPartida(partidaId: Int): Flow<List<MovimientoEntity>>

    @Query("SELECT * FROM movimientos WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMovimientosByUser(userId: Int): Flow<List<MovimientoEntity>>

    @Query("DELETE FROM movimientos WHERE partidaId = :partidaId")
    suspend fun deleteMovimientosDePartida(partidaId: Int)
}
