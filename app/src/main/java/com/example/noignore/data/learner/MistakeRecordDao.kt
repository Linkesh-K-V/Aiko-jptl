package com.example.noignore.data.learner

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MistakeRecordDao {
    @Query("SELECT * FROM mistake_records ORDER BY lastMistakeMs DESC")
    fun getAllMistakesFlow(): Flow<List<MistakeRecordEntity>>

    @Query("SELECT * FROM mistake_records WHERE isLeech = 1 ORDER BY lapseCount DESC")
    fun getLeechesFlow(): Flow<List<MistakeRecordEntity>>

    @Query("SELECT * FROM mistake_records WHERE isLeech = 1 ORDER BY lapseCount DESC")
    suspend fun getLeeches(): List<MistakeRecordEntity>

    @Query("SELECT * FROM mistake_records WHERE cardId = :cardId LIMIT 1")
    suspend fun getMistakeByCardId(cardId: String): MistakeRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: MistakeRecordEntity)

    @Query("DELETE FROM mistake_records WHERE cardId = :cardId")
    suspend fun deleteByCardId(cardId: String)

    @Query("SELECT COUNT(*) FROM mistake_records WHERE isLeech = 1")
    suspend fun countLeeches(): Int

    @Query("SELECT COUNT(*) FROM mistake_records WHERE isLeech = 1")
    fun countLeechesFlow(): Flow<Int>
}
