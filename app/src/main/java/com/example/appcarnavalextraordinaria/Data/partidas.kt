package com.example.appcarnavalextraordinaria.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partidas")
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fecha: Long = System.currentTimeMillis(),
    val createdBy: Int, // FK UserEntity.id
    var resultado: String,


)