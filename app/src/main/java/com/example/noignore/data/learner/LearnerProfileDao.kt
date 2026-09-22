package com.example.noignore.data.learner

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LearnerProfileDao {
    @Query("SELECT * FROM learner_profile WHERE id = :id LIMIT 1")
    fun getProfileFlow(id: String = "default_profile"): Flow<LearnerProfileEntity?>

    @Query("SELECT * FROM learner_profile WHERE id = :id LIMIT 1")
    suspend fun getProfile(id: String = "default_profile"): LearnerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: LearnerProfileEntity)

    @Update
    suspend fun update(profile: LearnerProfileEntity)
}
