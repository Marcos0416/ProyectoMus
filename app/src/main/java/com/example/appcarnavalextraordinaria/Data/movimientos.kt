package com.example.appcarnavalextraordinaria.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val partidaId: Int, // FK PartidaEntity.id
    val userId: Int,    // FK UserEntity.id
    val ronda: String,
    val accion: String,
    val cantidad: Int?,
    val timestamp: Long = System.currentTimeMillis()
)