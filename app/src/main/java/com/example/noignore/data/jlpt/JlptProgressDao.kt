package com.example.noignore.data.jlpt

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JlptProgressDao {
    @Query("SELECT * FROM jlpt_category_progress ORDER BY level, category")
    fun getAllCategoryProgress(): Flow<List<JlptProgressEntity>>

    @Query("SELECT * FROM jlpt_category_progress WHERE level = :level ORDER BY category")
    fun getProgressForLevel(level: String): Flow<List<JlptProgressEntity>>

    @Query("SELECT * FROM jlpt_category_progress WHERE level = :level AND category = :category LIMIT 1")
    suspend fun getProgress(level: String, category: String): JlptProgressEntity?

    @Query("SELECT COUNT(*) FROM jlpt_category_progress")
    suspend fun countCategories(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: JlptProgressEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(list: List<JlptProgressEntity>)

    @Query("UPDATE jlpt_category_progress SET completedItems = :newCount, lastStudiedAt = :timestamp WHERE level = :level AND category = :category")
    suspend fun updateCompletedCount(level: String, category: String, newCount: Int, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM jlpt_exam_target WHERE id = 1 LIMIT 1")
    fun getTargetExam(): Flow<JlptExamTargetEntity?>

    @Query("SELECT * FROM jlpt_exam_target WHERE id = 1 LIMIT 1")
    suspend fun getTargetExamSync(): JlptExamTargetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setTargetExam(target: JlptExamTargetEntity)

    @Query("UPDATE jlpt_exam_target SET targetLevel = :targetLevel, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateTargetLevel(targetLevel: String, timestamp: Long = System.currentTimeMillis())
}
