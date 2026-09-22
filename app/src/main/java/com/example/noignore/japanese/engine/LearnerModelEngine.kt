package com.example.noignore.japanese.engine

import com.example.noignore.data.AppDatabase
import com.example.noignore.data.learner.LearnerProfileEntity

enum class ImmersionMode(val displayName: String, val description: String) {
    BEGINNER("Bilingual Support", "Japanese with complete English meanings, romaji, and explanations."),
    INTERMEDIATE("Target Focused", "Japanese definitions with secondary English backup; romaji hidden."),
    ADVANCED("Rich Japanese", "Japanese primary descriptions and collocations with minimal English."),
    MONOLINGUAL("Japanese Only (国語辞典)", "100% monolingual dictionary and immersion mode.")
}

/**
 * Multi-Dimensional Learner Model Engine.
 * Quantifies learner competencies across vocabulary, kanji, grammar, reading, and listening,
 * recommending adaptive study pathways and progressive language immersion.
 */
object LearnerModelEngine {

    suspend fun getOrInitProfile(db: AppDatabase): LearnerProfileEntity {
        val dao = db.learnerProfileDao()
        val existing = dao.getProfile()
        if (existing != null) return existing

        val initial = LearnerProfileEntity(
            id = "default_profile",
            kanaMasteryScore = 0.1f,
            vocabMasteryScore = 0.1f,
            kanjiMasteryScore = 0.1f,
            grammarMasteryScore = 0.1f,
            readingMasteryScore = 0.1f,
            listeningMasteryScore = 0.1f,
            recallStrengthScore = 0.2f,
            averageResponseTimeMs = 2500L,
            confidenceScore = 0.5f,
            preferredStudyDurationMinutes = 20,
            preferredDifficulty = "BALANCED",
            immersionLevel = ImmersionMode.BEGINNER.name,
            lastCalculatedMs = System.currentTimeMillis()
        )
        dao.insertOrUpdate(initial)
        return initial
    }

    suspend fun updateCompetency(
        db: AppDatabase,
        category: String,
        isSuccess: Boolean,
        responseTimeMs: Long
    ) {
        val dao = db.learnerProfileDao()
        val profile = getOrInitProfile(db)

        val delta = if (isSuccess) 0.02f else -0.03f

        val newKana = if (category == "KANA") (profile.kanaMasteryScore + delta).coerceIn(0f, 1f) else profile.kanaMasteryScore
        val newVocab = if (category == "VOCAB") (profile.vocabMasteryScore + delta).coerceIn(0f, 1f) else profile.vocabMasteryScore
        val newKanji = if (category == "KANJI") (profile.kanjiMasteryScore + delta).coerceIn(0f, 1f) else profile.kanjiMasteryScore
        val newGrammar = if (category == "GRAMMAR") (profile.grammarMasteryScore + delta).coerceIn(0f, 1f) else profile.grammarMasteryScore
        val newReading = if (category == "READING") (profile.readingMasteryScore + delta).coerceIn(0f, 1f) else profile.readingMasteryScore
        val newListening = if (category == "LISTENING") (profile.listeningMasteryScore + delta).coerceIn(0f, 1f) else profile.listeningMasteryScore

        val avgTime = (profile.averageResponseTimeMs * 0.9 + responseTimeMs * 0.1).toLong()
        val avgScore = (newKana + newVocab + newKanji + newGrammar + newReading + newListening) / 6.0f

        val recommendedImmersion = when {
            avgScore >= 0.85f -> ImmersionMode.MONOLINGUAL.name
            avgScore >= 0.60f -> ImmersionMode.ADVANCED.name
            avgScore >= 0.35f -> ImmersionMode.INTERMEDIATE.name
            else -> ImmersionMode.BEGINNER.name
        }

        val updated = profile.copy(
            kanaMasteryScore = newKana,
            vocabMasteryScore = newVocab,
            kanjiMasteryScore = newKanji,
            grammarMasteryScore = newGrammar,
            readingMasteryScore = newReading,
            listeningMasteryScore = newListening,
            averageResponseTimeMs = avgTime,
            immersionLevel = recommendedImmersion,
            lastCalculatedMs = System.currentTimeMillis()
        )

        dao.update(updated)
    }
}
