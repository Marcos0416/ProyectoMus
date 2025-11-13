package com.example.appcarnavalextraordinaria.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String, // O mejor: passwordHash si vas a cifrar, siempre HASH nunca el real
    val email: String?,

    val dateJoined: Long = System.currentTimeMillis()
)