package com.example.noignore.data.learner

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for tracking mistakes, confusions, and leeches (persistently failing cards).
 * Triggers pedagogical interventions: mnemonics, easier explanations, cloze exercises, contrast questions.
 */
@Entity(tableName = "mistake_records")
data class MistakeRecordEntity(
    @PrimaryKey
    val cardId: String,
    val jlptLevel: String = "N5",
    val category: String = "VOCAB",
    val lapseCount: Int = 1,
    val totalAttempts: Int = 1,
    val mistakeType: String = "READING_LAPSE", // PARTICLE_CONFUSION, KANJI_CONFUSION, READING_LAPSE, VOCAB_MEANING, GRAMMAR_NUANCE, LISTENING_DISTORTION
    val lastMistakeMs: Long = System.currentTimeMillis(),
    val isLeech: Boolean = false, // Triggered when lapseCount >= 4
    val interventionNotes: String = ""
)
