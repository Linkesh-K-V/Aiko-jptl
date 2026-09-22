package com.example.noignore.data.srs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for Flashcard Spaced Repetition System (SRS).
 * Provides queries to schedule and surface flashcard reviews at optimal intervals.
 */
@Dao
interface FlashcardSrsDao {

    @Query("SELECT * FROM flashcard_srs WHERE cardId = :cardId LIMIT 1")
    fun getCardSrsFlow(cardId: String): Flow<FlashcardSrsEntity?>

    @Query("SELECT * FROM flashcard_srs WHERE cardId = :cardId LIMIT 1")
    suspend fun getCardSrs(cardId: String): FlashcardSrsEntity?

    @Query("SELECT * FROM flashcard_srs ORDER BY dueDateMs ASC")
    fun getAllCardsFlow(): Flow<List<FlashcardSrsEntity>>

    @Query("SELECT * FROM flashcard_srs")
    suspend fun getAllCardsSync(): List<FlashcardSrsEntity>

    /**
     * Surfaces cards that are due for review (dueDateMs <= currentTimeMs),
     * ordered by earliest due date first so the most urgent reviews surface at optimal intervals.
     */
    @Query("SELECT * FROM flashcard_srs WHERE dueDateMs <= :currentTimeMs ORDER BY dueDateMs ASC")
    fun getDueCardsFlow(currentTimeMs: Long): Flow<List<FlashcardSrsEntity>>

    @Query("SELECT * FROM flashcard_srs WHERE dueDateMs <= :currentTimeMs ORDER BY dueDateMs ASC")
    suspend fun getDueCardsSync(currentTimeMs: Long): List<FlashcardSrsEntity>

    @Query("SELECT * FROM flashcard_srs WHERE dueDateMs <= :currentTimeMs AND UPPER(jlptLevel) = UPPER(:jlptLevel) ORDER BY dueDateMs ASC")
    suspend fun getDueCardsByLevelSync(currentTimeMs: Long, jlptLevel: String): List<FlashcardSrsEntity>

    @Query("SELECT * FROM flashcard_srs WHERE dueDateMs <= :currentTimeMs AND category = :category ORDER BY dueDateMs ASC")
    suspend fun getDueCardsByCategorySync(currentTimeMs: Long, category: String): List<FlashcardSrsEntity>

    /**
     * Relearn queue: cards currently in active learning phase (e.g. marked 'Again').
     */
    @Query("SELECT * FROM flashcard_srs WHERE state = 'LEARNING' ORDER BY dueDateMs ASC")
    fun getLearningCardsFlow(): Flow<List<FlashcardSrsEntity>>

    @Query("SELECT * FROM flashcard_srs WHERE state = 'LEARNING' ORDER BY dueDateMs ASC")
    suspend fun getLearningCardsSync(): List<FlashcardSrsEntity>

    @Query("SELECT COUNT(*) FROM flashcard_srs WHERE state = 'NEW'")
    fun getNewCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM flashcard_srs WHERE state = 'LEARNING'")
    fun getLearningCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM flashcard_srs WHERE (state = 'REVIEW' OR state = 'MASTERED') AND dueDateMs <= :currentTimeMs")
    fun getDueReviewCountFlow(currentTimeMs: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM flashcard_srs")
    suspend fun getTotalTrackedCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: FlashcardSrsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<FlashcardSrsEntity>)

    @Query("DELETE FROM flashcard_srs WHERE cardId = :cardId")
    suspend fun deleteCard(cardId: String)

    @Query("DELETE FROM flashcard_srs")
    suspend fun clearAll()
}
