package com.example.noignore.data.srs

import com.example.noignore.japanese.model.AnkiRating
import com.example.noignore.japanese.model.SrsCardData
import com.example.noignore.japanese.model.SrsCardState
import com.example.noignore.japanese.model.SrsDeckSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Repository responsible for the Spaced Repetition System (SRS) algorithm (SuperMemo SM-2).
 * Persists flashcard review histories into the Room database and schedules cards
 * based on cognitive recall performance, ensuring cards are surfaced at optimal intervals.
 */
class FlashcardSrsRepository(private val srsDao: FlashcardSrsDao) {

    companion object {
        const val ONE_DAY_MS = 24L * 60 * 60 * 1000
        const val RELEARN_INTERVAL_MS = 10L * 60 * 1000 // 10 minutes for failed items
        const val MINIMUM_EASE_FACTOR = 1.30f
        const val DEFAULT_EASE_FACTOR = 2.50f
        const val MASTERY_INTERVAL_DAYS = 21
    }

    /**
     * Reactive stream for an individual card's SRS state.
     */
    fun getCardSrsFlow(cardId: String): Flow<FlashcardSrsEntity?> {
        return srsDao.getCardSrsFlow(cardId)
    }

    /**
     * Synchronously retrieves a card's SRS entity from Room (suspending).
     */
    suspend fun getCardSrs(cardId: String): FlashcardSrsEntity? = withContext(Dispatchers.IO) {
        srsDao.getCardSrs(cardId)
    }

    /**
     * Reactive stream of due cards, ordered by urgency (earliest due date first).
     */
    fun getDueCardsFlow(currentTimeMs: Long = System.currentTimeMillis()): Flow<List<FlashcardSrsEntity>> {
        return srsDao.getDueCardsFlow(currentTimeMs)
    }

    /**
     * Suspended lookup for due cards, optionally filtered by JLPT level.
     */
    suspend fun getDueCards(
        currentTimeMs: Long = System.currentTimeMillis(),
        level: String? = null
    ): List<FlashcardSrsEntity> = withContext(Dispatchers.IO) {
        if (level != null && level != "ALL") {
            srsDao.getDueCardsByLevelSync(currentTimeMs, level)
        } else {
            srsDao.getDueCardsSync(currentTimeMs)
        }
    }

    /**
     * Relearn queue: cards currently marked 'Again' requiring short-term reinforcement.
     */
    fun getLearningQueueFlow(): Flow<List<FlashcardSrsEntity>> {
        return srsDao.getLearningCardsFlow()
    }

    /**
     * Applies the SuperMemo SM-2 Spaced Repetition algorithm based on the user's recall rating,
     * updates memory stability, schedules the next optimal review timestamp, and stores it in Room.
     *
     * @param cardId Unique identifier of the flashcard or exam question.
     * @param rating User's self-assessed recall performance (AGAIN, HARD, GOOD, EASY).
     * @param category Japanese deck category (Kanji, Vocabulary, Exam, etc.).
     * @param jlptLevel JLPT level (N5, N4, N3, N2, N1).
     * @param nowMs Review timestamp in milliseconds.
     * @return The updated [FlashcardSrsEntity] with recalculated scheduling parameters.
     */
    suspend fun recordReview(
        cardId: String,
        rating: AnkiRating,
        category: String = "",
        jlptLevel: String = "ALL",
        nowMs: Long = System.currentTimeMillis()
    ): FlashcardSrsEntity = withContext(Dispatchers.IO) {
        val existing = srsDao.getCardSrs(cardId) ?: FlashcardSrsEntity(
            cardId = cardId,
            category = category,
            jlptLevel = jlptLevel,
            repetition = 0,
            intervalDays = 0,
            easeFactor = DEFAULT_EASE_FACTOR,
            dueDateMs = 0L,
            lastReviewedMs = 0L,
            lapses = 0,
            state = SrsCardState.NEW.name,
            totalReviews = 0,
            lastRating = null
        )

        val updated = calculateSm2Schedule(existing, rating, nowMs, category, jlptLevel)
        srsDao.insertOrUpdate(updated)
        updated
    }

    /**
     * Spaced Repetition Scheduling Engine (SuperMemo SM-2 algorithm).
     */
    fun calculateSm2Schedule(
        current: FlashcardSrsEntity,
        rating: AnkiRating,
        nowMs: Long = System.currentTimeMillis(),
        category: String = current.category,
        jlptLevel: String = current.jlptLevel
    ): FlashcardSrsEntity {
        val currentRep = current.repetition
        val currentInterval = current.intervalDays
        val currentEase = current.easeFactor
        val lapses = current.lapses

        val newEaseFactor = when (rating) {
            AnkiRating.AGAIN -> max(MINIMUM_EASE_FACTOR, currentEase - 0.20f)
            AnkiRating.HARD -> max(MINIMUM_EASE_FACTOR, currentEase - 0.15f)
            AnkiRating.GOOD -> currentEase
            AnkiRating.EASY -> currentEase + 0.15f
        }

        val nextRepetition = if (rating == AnkiRating.AGAIN) 0 else currentRep + 1

        val (nextIntervalDays, nextDueDateMs, nextState) = when (rating) {
            AnkiRating.AGAIN -> {
                Triple(0, nowMs + RELEARN_INTERVAL_MS, SrsCardState.LEARNING.name)
            }
            AnkiRating.HARD -> {
                val nextDays = when (currentRep) {
                    0 -> 1
                    1 -> 2
                    else -> max(1, (currentInterval * 1.2f).roundToInt())
                }
                Triple(nextDays, nowMs + nextDays * ONE_DAY_MS, SrsCardState.REVIEW.name)
            }
            AnkiRating.GOOD -> {
                val nextDays = when (currentRep) {
                    0 -> 1
                    1 -> 3
                    else -> max(1, (currentInterval * currentEase).roundToInt())
                }
                Triple(nextDays, nowMs + nextDays * ONE_DAY_MS, SrsCardState.REVIEW.name)
            }
            AnkiRating.EASY -> {
                val nextDays = when (currentRep) {
                    0 -> 4
                    1 -> 7
                    else -> max(1, (currentInterval * newEaseFactor * 1.3f).roundToInt())
                }
                Triple(nextDays, nowMs + nextDays * ONE_DAY_MS, SrsCardState.REVIEW.name)
            }
        }

        return current.copy(
            category = if (category.isNotBlank()) category else current.category,
            jlptLevel = if (jlptLevel.isNotBlank()) jlptLevel else current.jlptLevel,
            repetition = nextRepetition,
            intervalDays = nextIntervalDays,
            easeFactor = newEaseFactor,
            dueDateMs = nextDueDateMs,
            lastReviewedMs = nowMs,
            lapses = if (rating == AnkiRating.AGAIN) lapses + 1 else lapses,
            state = nextState,
            totalReviews = current.totalReviews + 1,
            lastRating = rating.name
        )
    }

    /**
     * Modern FSRS v4.5 DSR scheduling algorithm.
     */
    fun calculateFsrsSchedule(
        current: FlashcardSrsEntity,
        rating: AnkiRating,
        nowMs: Long = System.currentTimeMillis(),
        category: String = current.category,
        jlptLevel: String = current.jlptLevel
    ): FlashcardSrsEntity {
        val currentRep = current.repetition
        val currentInterval = current.intervalDays
        val currentEase = current.easeFactor
        val lapses = current.lapses

        val currentStability = if (currentInterval > 0) {
            max(0.5, currentInterval.toDouble() * (currentEase / 2.50))
        } else {
            0.0
        }
        val currentDifficulty = ((3.0 - currentEase.coerceIn(1.30f, 3.0f)) * 3.33 + 5.0).coerceIn(1.0, 10.0)

        val fsrs = com.example.noignore.japanese.data.FsrsScheduler.schedule(
            currentStability = currentStability,
            currentDifficulty = currentDifficulty,
            repetition = currentRep,
            lastReviewedMs = current.lastReviewedMs,
            rating = rating,
            nowMs = nowMs
        )

        val newEaseFactor = when (rating) {
            AnkiRating.AGAIN -> max(MINIMUM_EASE_FACTOR, currentEase - 0.20f)
            AnkiRating.HARD -> max(MINIMUM_EASE_FACTOR, currentEase - 0.15f)
            AnkiRating.GOOD -> currentEase
            AnkiRating.EASY -> currentEase + 0.15f
        }

        return current.copy(
            category = if (category.isNotBlank()) category else current.category,
            jlptLevel = if (jlptLevel.isNotBlank()) jlptLevel else current.jlptLevel,
            repetition = if (rating == AnkiRating.AGAIN) 0 else currentRep + 1,
            intervalDays = fsrs.intervalDays,
            easeFactor = newEaseFactor,
            dueDateMs = fsrs.dueDateMs,
            lastReviewedMs = nowMs,
            lapses = if (rating == AnkiRating.AGAIN) lapses + 1 else lapses,
            state = fsrs.state.name,
            totalReviews = current.totalReviews + 1,
            lastRating = rating.name
        )
    }

    /**
     * Seeds initial deck cards into Room if they do not yet exist.
     */
    suspend fun seedInitialCardsIfEmpty(
        cardIds: List<Triple<String, String, String>> // (id, category, level)
    ) = withContext(Dispatchers.IO) {
        val count = srsDao.getTotalTrackedCount()
        if (count == 0) {
            val entities = cardIds.map { (id, category, level) ->
                FlashcardSrsEntity(
                    cardId = id,
                    category = category,
                    jlptLevel = level,
                    repetition = 0,
                    intervalDays = 0,
                    easeFactor = DEFAULT_EASE_FACTOR,
                    dueDateMs = 0L,
                    lastReviewedMs = 0L,
                    lapses = 0,
                    state = SrsCardState.NEW.name,
                    totalReviews = 0,
                    lastRating = null
                )
            }
            srsDao.insertAll(entities)
        }
    }

    /**
     * Provides reactive summary metrics of the SRS cognitive state directly from Room.
     */
    fun getDeckSummaryFlow(currentTimeMs: Long = System.currentTimeMillis()): Flow<SrsDeckSummary> {
        return combine(
            srsDao.getNewCountFlow(),
            srsDao.getLearningCountFlow(),
            srsDao.getDueReviewCountFlow(currentTimeMs),
            srsDao.getAllCardsFlow()
        ) { newCount, learningCount, dueReviewCount, allCards ->
            var retentionSum = 0f
            var reviewedCount = 0
            for (card in allCards) {
                if (card.lastReviewedMs > 0L) {
                    retentionSum += card.calculateMemoryRetention(currentTimeMs)
                    reviewedCount++
                }
            }
            val avgRetention = if (reviewedCount > 0) (retentionSum / reviewedCount) else 0.85f

            SrsDeckSummary(
                newCardsCount = newCount,
                learningCardsCount = learningCount,
                reviewCardsCount = dueReviewCount,
                totalStudiedToday = 0,
                avgRetentionRate = avgRetention
            )
        }
    }
}
