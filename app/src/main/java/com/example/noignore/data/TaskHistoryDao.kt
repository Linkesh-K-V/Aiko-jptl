package com.example.noignore.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: TaskHistory)

    @Query("SELECT * FROM task_history WHERE date >= :start AND date <= :end ORDER BY date ASC")
    suspend fun getHistoryBetween(start: String, end: String): List<TaskHistory>

    @Query("SELECT * FROM task_history ORDER BY date DESC")
    suspend fun getAllHistory(): List<TaskHistory>

    @Query("SELECT COUNT(*) FROM task_history WHERE status = 'MISSED' AND date >= :start AND date <= :end")
    suspend fun countMissesBetween(start: String, end: String): Int

    @Query("SELECT COUNT(*) FROM task_history WHERE status = 'MISSED' AND date = :date")
    suspend fun countMissedOnDate(date: String): Int

    @Query("SELECT COUNT(*) FROM task_history WHERE status = 'DONE'")
    suspend fun countTotalDone(): Int
}
