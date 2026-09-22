package com.example.noignore.japanese.model

import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

enum class AnkiRating(val label: String, val score: Int, val description: String) {
    AGAIN("Again", 1, "Complete blackout / forgot. Reset and repeat soon."),
    HARD("Hard", 2, "Recalled with struggle/hesitation. Shorter interval."),
    GOOD("Good", 3, "Recalled accurately with normal effort. Standard spacing."),
    EASY("Easy", 4, "Instant effortless recall. Maximum interval bonus.")
}

enum class SrsCardState(val displayName: String, val badge: String) {
    NEW("New", "🌱"),
    LEARNING("Learning", "⚡"),
    REVIEW("Review", "🧠"),
    MASTERED("Mastered", "👑")
}

data class SrsCardData(
    val itemId: String,
    val repetition: Int = 0,
    val intervalDays: Int = 0,
    val easeFactor: Float = 2.50f,
    val dueDateMs: Long = 0L,
    val lastReviewedMs: Long = 0L,
    val lapses: Int = 0,
    val state: SrsCardState = SrsCardState.NEW
) {
    /**
     * Anki Leech definition: 3 or more lapses (failed recall).
     */
    val isLeech: Boolean
        get() = lapses >= 3

    /**
     * Estimates the human brain retention probability based on the Ebbinghaus Forgetting Curve:
     * R(t) = e^(-t / S)
     * where t is elapsed days and S is memory stability (derived from intervalDays).
     */
    fun calculateMemoryRetention(currentTimeMs: Long = System.currentTimeMillis()): Float {
        if (lastReviewedMs == 0L || intervalDays <= 0) {
            return if (repetition > 0) 0.50f else 0.0f
        }
        val elapsedDays = max(0.0, (currentTimeMs - lastReviewedMs) / (1000.0 * 60 * 60 * 24))
        // Stability S (in days) increases with interval and repetitions
        val stability = max(0.8, intervalDays.toDouble() * (easeFactor / 2.5))
        val retention = exp(-elapsedDays / stability)
        return (retention.coerceIn(0.05, 1.0)).toFloat()
    }

    /**
     * Formatted string showing estimated interval for each of the 4 FSRS rating buttons.
     */
    fun estimateNextIntervalLabel(rating: AnkiRating): String {
        return try {
            val simulated = processRating(rating)
            if (simulated.intervalDays <= 0) {
                "< 10m"
            } else {
                "${simulated.intervalDays}d"
            }
        } catch (_: Exception) {
            when (rating) {
                AnkiRating.AGAIN -> "< 10m"
                AnkiRating.HARD -> "1d"
                AnkiRating.GOOD -> "3d"
                AnkiRating.EASY -> "7d"
            }
        }
    }

    /**
     * Executes spaced repetition calculation utilizing FSRS v4.5 with fallback to SM-2.
     */
    fun processRating(rating: AnkiRating, nowMs: Long = System.currentTimeMillis()): SrsCardData {
        // Derive initial stability from intervalDays and easeFactor
        val currentStability = if (intervalDays > 0) {
            max(0.5, intervalDays.toDouble() * (easeFactor / 2.50))
        } else {
            0.0
        }
        // Invert ease factor to difficulty scale [1..10]: ease 2.50 -> diff 5.0, ease 1.30 -> diff 9.0
        val currentDifficulty = ((3.0 - easeFactor.coerceIn(1.30f, 3.0f)) * 3.33 + 5.0).coerceIn(1.0, 10.0)

        val fsrsResult = com.example.noignore.japanese.data.FsrsScheduler.schedule(
            currentStability = currentStability,
            currentDifficulty = currentDifficulty,
            repetition = repetition,
            lastReviewedMs = lastReviewedMs,
            rating = rating,
            nowMs = nowMs
        )

        // Map FSRS stability back to an ease factor for backward compatibility and display
        val updatedEase = when (rating) {
            AnkiRating.AGAIN -> max(1.30f, easeFactor - 0.20f)
            AnkiRating.HARD -> max(1.30f, easeFactor - 0.15f)
            AnkiRating.GOOD -> easeFactor
            AnkiRating.EASY -> min(3.20f, easeFactor + 0.15f)
        }

        return this.copy(
            repetition = if (rating == AnkiRating.AGAIN) 0 else repetition + 1,
            intervalDays = fsrsResult.intervalDays,
            easeFactor = updatedEase,
            dueDateMs = fsrsResult.dueDateMs,
            lastReviewedMs = nowMs,
            lapses = if (rating == AnkiRating.AGAIN) lapses + 1 else lapses,
            state = fsrsResult.state
        )
    }
}

data class SrsDeckSummary(
    val newCardsCount: Int,
    val learningCardsCount: Int,
    val reviewCardsCount: Int,
    val totalStudiedToday: Int,
    val avgRetentionRate: Float
)
