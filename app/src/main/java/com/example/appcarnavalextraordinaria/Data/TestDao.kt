package com.example.appcarnavalextraordinaria.Data



import androidx.room.*
import kotlinx.coroutines.flow.Flow

// En tu TestDao.kt - asegúrate de tener TODOS estos métodos
@Dao
interface TestDao {
    @Insert
    suspend fun insertTest(test: TestEntity): Long

    @Insert
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("SELECT * FROM tests")
    fun getAllTests(): Flow<List<TestEntity>>

    @Query("SELECT * FROM questions WHERE testId = :testId ORDER BY id")
    fun getQuestionsForTest(testId: Int): Flow<List<QuestionEntity>>

    @Query("DELETE FROM tests")
    suspend fun deleteAllTests()

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()

    @Query("SELECT * FROM tests WHERE id = :testId LIMIT 1")
    suspend fun getTestById(testId: Int): TestEntity?
}
