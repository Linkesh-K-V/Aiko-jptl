package com.example.noignore.data.srs

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.noignore.japanese.model.AnkiRating
import com.example.noignore.japanese.model.SrsCardData
import com.example.noignore.japanese.model.SrsCardState
import kotlin.math.exp
import kotlin.math.max

/**
 * Room database entity storing the Spaced Repetition System (SRS) state
 * for individual flashcards, based on the SuperMemo SM-2 cognitive algorithm.
 */
@Entity(
    tableName = "flashcard_srs",
    indices = [
        Index(value = ["dueDateMs"]),
        Index(value = ["jlptLevel", "dueDateMs"]),
        Index(value = ["state"])
    ]
)
data class FlashcardSrsEntity(
    @PrimaryKey val cardId: String,
    val category: String = "",
    val jlptLevel: String = "ALL",
    val repetition: Int = 0,
    val intervalDays: Int = 0,
    val easeFactor: Float = 2.50f,
    val dueDateMs: Long = 0L,
    val lastReviewedMs: Long = 0L,
    val lapses: Int = 0,
    val state: String = "NEW", // NEW, LEARNING, REVIEW, MASTERED
    val totalReviews: Int = 0,
    val lastRating: String? = null
) {
    /**
     * Converts to the UI layer SrsCardData model.
     */
    fun toSrsCardData(): SrsCardData {
        val srsState = try {
            SrsCardState.valueOf(state)
        } catch (e: Exception) {
            if (repetition > 0) SrsCardState.REVIEW else SrsCardState.NEW
        }
        return SrsCardData(
            itemId = cardId,
            repetition = repetition,
            intervalDays = intervalDays,
            easeFactor = easeFactor,
            dueDateMs = dueDateMs,
            lastReviewedMs = lastReviewedMs,
            lapses = lapses,
            state = srsState
        )
    }

    /**
     * Estimates retention probability based on the Ebbinghaus Forgetting Curve:
     * R(t) = e^(-t / S)
     */
    fun calculateMemoryRetention(currentTimeMs: Long = System.currentTimeMillis()): Float {
        if (lastReviewedMs == 0L || intervalDays <= 0) {
            return if (repetition > 0) 0.50f else 0.0f
        }
        val elapsedDays = max(0.0, (currentTimeMs - lastReviewedMs) / (1000.0 * 60 * 60 * 24))
        val stability = max(0.8, intervalDays.toDouble() * (easeFactor / 2.5))
        val retention = exp(-elapsedDays / stability)
        return (retention.coerceIn(0.05, 1.0)).toFloat()
    }

    companion object {
        fun fromSrsCardData(
            data: SrsCardData,
            category: String = "",
            jlptLevel: String = "ALL",
            totalReviews: Int = 0,
            lastRating: String? = null
        ): FlashcardSrsEntity {
            return FlashcardSrsEntity(
                cardId = data.itemId,
                category = category,
                jlptLevel = jlptLevel,
                repetition = data.repetition,
                intervalDays = data.intervalDays,
                easeFactor = data.easeFactor,
                dueDateMs = data.dueDateMs,
                lastReviewedMs = data.lastReviewedMs,
                lapses = data.lapses,
                state = data.state.name,
                totalReviews = totalReviews,
                lastRating = lastRating
            )
        }
    }
}
