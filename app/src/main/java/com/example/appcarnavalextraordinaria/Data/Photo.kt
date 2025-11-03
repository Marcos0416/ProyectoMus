package com.example.appcarnavalextraordinaria.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress_table")
data class ProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val tutorialStep: Int,
    val score: Int,
    val lastAccess: Long
)
