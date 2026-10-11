package com.example.noignore.japanese.data

import com.example.noignore.japanese.model.AnkiRating
import com.example.noignore.japanese.model.SrsCardState
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Free Spaced Repetition Scheduler (FSRS v4.5) Implementation in Kotlin.
 *
 * Implements the DSR (Difficulty, Stability, Retrievability) memory model:
 * - Stability (S): Number of days for memory retention probability to decline from 100% to 90%.
 * - Difficulty (D): Inherent cognitive complexity of the item on a [1.0, 10.0] scale.
 * - Retrievability (R): Current probability of successful recall: R(t) = (1 + factor * (t / S))^(-1).
 *
 * Eliminates SuperMemo SM-2 "ease hell" and provides optimal, mathematically grounded spacing.
 */
object FsrsScheduler {

    const val TARGET_RETENTION = 0.90 // Desired recall probability (90%)
    const val DECAY_FACTOR = -0.5 // Standard power law forgetting parameter
    const val FACTOR = 0.19 // factor = (TARGET_RETENTION^(1/DECAY_FACTOR) - 1) = 0.9^(-2) - 1 ≈ 0.19

    // Default FSRS 4.5 initial weights [w0..w16]
    private val DEFAULT_W = doubleArrayOf(
        0.4072, 1.1827, 3.1262, 15.4722, // Initial stabilities for ratings [Again, Hard, Good, Easy]
        7.2102, 0.5316, 1.0651, 0.0234,  // Difficulty updates
        1.6160, 0.1544, 1.0824,          // Stability review success
        1.9813, 0.0953, 0.2975,          // Stability review failure (lapses)
        0.2208, 2.4077                   // Relearning parameters
    )

    data class FsrsParameters(
        val stability: Double,
        val difficulty: Double,
        val intervalDays: Int,
        val dueDateMs: Long,
        val state: SrsCardState,
        val estimatedRetention: Double
    )

    /**
     * Calculates the probability of recall R(t) after [elapsedDays] given memory stability [stability].
     */
    fun calculateRetrievability(elapsedDays: Double, stability: Double): Double {
        if (stability <= 0.0) return 0.0
        return (1.0 + FACTOR * (elapsedDays / stability)).pow(-1.0).coerceIn(0.01, 1.0)
    }

    /**
     * Calculates interval in days required for retrievability to drop to [requestRetention].
     */
    fun calculateNextInterval(stability: Double, requestRetention: Double = TARGET_RETENTION): Int {
        val interval = (stability / FACTOR) * (requestRetention.pow(-1.0) - 1.0)
        return max(1, interval.roundToInt())
    }

    /**
     * Computes next FSRS parameters for a card given current values and user recall rating.
     */
    fun schedule(
        currentStability: Double,
        currentDifficulty: Double,
        repetition: Int,
        lastReviewedMs: Long,
        rating: AnkiRating,
        nowMs: Long = System.currentTimeMillis()
    ): FsrsParameters {
        val oneDayMs = 24L * 60 * 60 * 1000
        val elapsedDays = if (lastReviewedMs > 0L) {
            max(0.0, (nowMs - lastReviewedMs).toDouble() / oneDayMs)
        } else {
            0.0
        }

        // Rating index: Again=1, Hard=2, Good=3, Easy=4
        val ratingIdx = rating.score

        if (repetition == 0 || currentStability <= 0.0) {
            // First time learning this card
            val initStability = DEFAULT_W[ratingIdx - 1]
            val initDifficulty = (DEFAULT_W[4] - (ratingIdx - 3) * DEFAULT_W[5]).coerceIn(1.0, 10.0)
            val nextInterval = when (rating) {
                AnkiRating.AGAIN -> 0
                AnkiRating.HARD -> 1
                AnkiRating.GOOD -> calculateNextInterval(initStability, TARGET_RETENTION)
                AnkiRating.EASY -> max(3, calculateNextInterval(initStability * 1.3, TARGET_RETENTION))
            }
            val dueDateMs = if (nextInterval == 0) {
                nowMs + (10 * 60 * 1000L) // 10m relearn queue
            } else {
                nowMs + (nextInterval.toLong() * oneDayMs)
            }
            val state = when (rating) {
                AnkiRating.AGAIN -> SrsCardState.LEARNING
                else -> if (nextInterval >= 21) SrsCardState.MASTERED else SrsCardState.REVIEW
            }

            return FsrsParameters(
                stability = initStability,
                difficulty = initDifficulty,
                intervalDays = nextInterval,
                dueDateMs = dueDateMs,
                state = state,
                estimatedRetention = 1.0
            )
        }

        // Review of previously learned card
        val currentRetrievability = calculateRetrievability(elapsedDays, currentStability)

        // Calculate updated difficulty D' with mean reversion towards D0(3) = DEFAULT_W[4]
        val meanReversionTarget = DEFAULT_W[4]
        val deltaD = -DEFAULT_W[6] * (ratingIdx - 3)
        val rawNextD = currentDifficulty + deltaD
        val nextDifficulty = (DEFAULT_W[7] * meanReversionTarget + (1.0 - DEFAULT_W[7]) * rawNextD).coerceIn(1.0, 10.0)

        val nextStability: Double
        val nextInterval: Int
        val nextState: SrsCardState

        if (rating == AnkiRating.AGAIN) {
            // Memory lapse: calculate post-lapse stability
            val lapseStability = DEFAULT_W[11] *
                    nextDifficulty.pow(-DEFAULT_W[12]) *
                    (currentStability + 1.0).pow(DEFAULT_W[13]) *
                    exp((1.0 - currentRetrievability) * DEFAULT_W[14])
            nextStability = max(0.1, lapseStability)
            nextInterval = 0 // Relearn today (<10m)
            nextState = SrsCardState.LEARNING
        } else {
            // Successful recall: stability expands based on retrievability and difficulty
            val hardMultiplier = if (rating == AnkiRating.HARD) DEFAULT_W[15] else 1.0
            val easyMultiplier = if (rating == AnkiRating.EASY) DEFAULT_W[16] else 1.0
            val stabilityIncrease = exp(DEFAULT_W[8]) *
                    (11.0 - nextDifficulty) *
                    currentStability.pow(-DEFAULT_W[9]) *
                    (exp((1.0 - currentRetrievability) * DEFAULT_W[10]) - 1.0) *
                    hardMultiplier * easyMultiplier

            nextStability = max(currentStability, currentStability * (1.0 + stabilityIncrease))
            val rawInterval = calculateNextInterval(nextStability, TARGET_RETENTION)
            nextInterval = max(1, rawInterval)
            nextState = if (nextInterval >= 21) SrsCardState.MASTERED else SrsCardState.REVIEW
        }

        val nextDueDateMs = if (nextInterval == 0) {
            nowMs + (10 * 60 * 1000L)
        } else {
            nowMs + (nextInterval.toLong() * oneDayMs)
        }

        return FsrsParameters(
            stability = nextStability,
            difficulty = nextDifficulty,
            intervalDays = nextInterval,
            dueDateMs = nextDueDateMs,
            state = nextState,
            estimatedRetention = currentRetrievability
        )
    }
}
