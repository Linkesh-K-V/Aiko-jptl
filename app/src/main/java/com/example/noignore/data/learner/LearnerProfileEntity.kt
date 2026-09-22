package com.example.noignore.data.learner

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persistent Learner Model reflecting the learner's multi-dimensional mastery.
 * Feeds adaptive missions, weakness detection, and progressive immersion.
 */
@Entity(tableName = "learner_profile")
data class LearnerProfileEntity(
    @PrimaryKey
    val id: String = "default_profile",
    val kanaMasteryScore: Float = 0.0f, // 0.0 to 1.0 (Hiragana & Katakana mastery)
    val vocabMasteryScore: Float = 0.0f, // 0.0 to 1.0
    val kanjiMasteryScore: Float = 0.0f,
    val grammarMasteryScore: Float = 0.0f,
    val readingMasteryScore: Float = 0.0f,
    val listeningMasteryScore: Float = 0.0f,
    val recallStrengthScore: Float = 0.0f,
    val averageResponseTimeMs: Long = 3000L,
    val confidenceScore: Float = 0.5f,
    val preferredStudyDurationMinutes: Int = 20,
    val preferredDifficulty: String = "BALANCED", // GENTLE, BALANCED, INTENSIVE
    val immersionLevel: String = "BEGINNER", // BEGINNER (JP+EN), INTERMEDIATE (JP+Min EN), ADVANCED (Mostly JP), JAPANESE_ONLY
    val lastCalculatedMs: Long = System.currentTimeMillis()
)
