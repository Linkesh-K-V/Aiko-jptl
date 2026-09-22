package com.example.noignore.data.mission

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyMissionDao {
    @Query("SELECT * FROM daily_missions WHERE dateString = :dateString LIMIT 1")
    fun getMissionFlow(dateString: String): Flow<DailyMissionEntity?>

    @Query("SELECT * FROM daily_missions WHERE dateString = :dateString LIMIT 1")
    suspend fun getMission(dateString: String): DailyMissionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(mission: DailyMissionEntity)

    @Update
    suspend fun update(mission: DailyMissionEntity)

    @Query("UPDATE daily_missions SET isCompleted = 1, completedAtMs = :completedAt WHERE dateString = :dateString")
    suspend fun markCompleted(dateString: String, completedAt: Long = System.currentTimeMillis())
}
