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

// Versión mínima pero útil
@Entity(tableName = "resultado_test")
data class TestResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val testId: Int,
    val score: Int,
    val maxScore: Int,
    val completedAt: Long = System.currentTimeMillis()
)
