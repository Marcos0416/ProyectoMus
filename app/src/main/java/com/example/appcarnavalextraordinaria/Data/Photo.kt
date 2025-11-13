package com.example.appcarnavalextraordinaria.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progreso")
data class ProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,               // FK hacia UserEntity.id
    val tutorialStep: Int,
    val score: Int,
    val lastAccess: Long = System.currentTimeMillis()
)

