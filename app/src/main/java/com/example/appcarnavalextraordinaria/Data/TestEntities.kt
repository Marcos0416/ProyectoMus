package com.example.appcarnavalextraordinaria.Data



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tests")
data class TestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String? = null

)

@Entity(tableName = "preguntas")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val testId: Int,
    val section: String,
    val questionText: String,
    val options: String,
    val correctIndex: Int
)
