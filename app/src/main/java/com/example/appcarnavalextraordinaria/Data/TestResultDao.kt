package com.example.appcarnavalextraordinaria.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TestResultDao {
    @Insert
    suspend fun insertTestResult(testResult: TestResultEntity)

    @Query("SELECT * FROM resultado_test WHERE testId = :userId ORDER BY completedAt DESC")
    fun getTestResultsByUser(userId: Int): Flow<List<TestResultEntity>>

    @Query("SELECT * FROM resultado_test WHERE testId = :testId AND userId = :userId")
    suspend fun getTestResultForTest(userId: Int, testId: Int): TestResultEntity?

    @Query("SELECT COUNT(*) FROM resultado_test WHERE userId = :userId")
    suspend fun getCompletedTestsCount(userId: Int): Int

    @Query("SELECT AVG(score * 100.0 / score) FROM resultado_test WHERE userId = :userId")
    suspend fun getAverageScore(userId: Int): Double?
}