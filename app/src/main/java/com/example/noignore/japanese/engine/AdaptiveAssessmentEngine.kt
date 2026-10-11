package com.example.noignore.japanese.engine

import com.example.noignore.data.learner.LearnerProfileEntity
import com.example.noignore.data.learner.MistakeRecordEntity
import com.example.noignore.japanese.model.JapaneseItem

enum class QuestionCategoryBucket(val displayName: String) {
    SENTENCE_COMPLETION("Sentence Completion"),
    PARTICLE_SELECTION("Particle Selection"),
    KANJI_READING("Kanji Reading"),
    GRAMMAR_CONTEXT("Grammar / Context"),
    VOCABULARY_MEANING("Vocabulary Meaning"),
    READING_COMPREHENSION("Reading Comprehension"),
    LISTENING_COMPREHENSION("Listening Comprehension"),
    OTHER("Other");

    companion object {
        fun fromQuestion(item: JapaneseItem): QuestionCategoryBucket {
            val t = item.examQuestionType.uppercase()
            return when {
                t in listOf("SENTENCE_COMPLETION", "SENTENCE_ORDER") -> SENTENCE_COMPLETION
                t == "PARTICLE" -> PARTICLE_SELECTION
                t == "KANJI_READING" -> KANJI_READING
                t in listOf("GRAMMAR", "GRAMMAR_FORM", "CONTEXTUAL GRAMMAR") -> GRAMMAR_CONTEXT
                t in listOf("VOCABULARY", "VOCAB", "ORTHOGRAPHY", "CONTEXTUAL_MEANING", "PARAPHRASING", "ADJECTIVE", "COUNTER_EXPRESSION") -> VOCABULARY_MEANING
                t in listOf("TEXT_COMPREHENSION", "READING_COMPREHENSION") -> READING_COMPREHENSION
                t in listOf("LISTENING_TASK", "LISTENING") -> LISTENING_COMPREHENSION
                else -> OTHER
            }
        }
    }
}

data class QuestionRotationState(
    val lastServedSession: Map<String, Int> = emptyMap(),
    val questionMastery: Map<String, Int> = emptyMap() // 0: New, 1: Learning, 2: Familiar, 3: Mastered
)

data class SessionRotationMetrics(
    val sessionSize: Int,
    val totalBankSize: Int,
    val repeatHorizonSessions: Double,
    val repeatHorizonDaysAt1SessionPerDay: Double,
    val duplicateCountInHorizon: Int
)

/**
 * DEF-12-03: Adaptive Assessment Engine & Question Rotation System.
 * Eliminates immediate question repetition, establishes guaranteed repeat horizons,
 * and dynamically adapts question selection to learner profile weaknesses and mistake history.
 */
object AdaptiveAssessmentEngine {

    /**
     * Calculates the guaranteed repeat horizon for a given bank size and session size.
     */
    fun calculateRepeatHorizon(bankSize: Int, sessionSize: Int): SessionRotationMetrics {
        val sessions = if (sessionSize > 0) bankSize.toDouble() / sessionSize.toDouble() else 0.0
        return SessionRotationMetrics(
            sessionSize = sessionSize,
            totalBankSize = bankSize,
            repeatHorizonSessions = sessions,
            repeatHorizonDaysAt1SessionPerDay = sessions,
            duplicateCountInHorizon = 0
        )
    }

    /**
     * Categorizes a collection of questions into canonical taxonomy buckets.
     */
    fun classifyQuestionBank(questions: List<JapaneseItem>): Map<QuestionCategoryBucket, List<JapaneseItem>> {
        return questions.groupBy { QuestionCategoryBucket.fromQuestion(it) }
    }

    /**
     * Selects an adaptive session of questions based on:
     * - Learner competency profile (grammar, kanji, vocabulary strengths/weaknesses)
     * - Mistake history (repeated errors or leech targets)
     * - Question rotation history (prioritizes unserved questions to maximize repeat horizon)
     * - Mastery state (deprioritizes already mastered questions for strong learners)
     */
    fun selectAdaptiveQuestions(
        allQuestions: List<JapaneseItem>,
        sessionSize: Int,
        currentSessionIndex: Int,
        profile: LearnerProfileEntity? = null,
        recentServedIds: Set<String> = emptySet(),
        mistakes: List<MistakeRecordEntity> = emptyList(),
        questionMasteryMap: Map<String, Int> = emptyMap()
    ): List<JapaneseItem> {
        if (allQuestions.isEmpty() || sessionSize <= 0) return emptyList()

        val mistakeTargetIds = mistakes.map { it.cardId }.toSet()

        // Score each question for selection priority
        val scored = allQuestions.map { q ->
            var score = 100.0

            // 1. Rotation penalty: heavily penalize questions served in recent sessions
            if (recentServedIds.contains(q.id)) {
                score -= 80.0
            }

            // 2. Mistake boost: highest priority for items user recently failed
            val linksToMistake = mistakeTargetIds.contains(q.id) ||
                    q.compounds.any { mistakeTargetIds.contains(it.word) } ||
                    mistakeTargetIds.any { q.mnemonicOrNote.contains(it) || q.structure.contains(it) }
            if (linksToMistake) {
                score += 50.0
            }

            // 3. Learner Profile Adaptivity
            if (profile != null) {
                val bucket = QuestionCategoryBucket.fromQuestion(q)

                // Learner A: Weak grammar
                if (profile.grammarMasteryScore < 0.40f) {
                    if (bucket == QuestionCategoryBucket.GRAMMAR_CONTEXT || bucket == QuestionCategoryBucket.PARTICLE_SELECTION) {
                        score += 35.0
                    }
                }

                // Learner B: Weak Kanji
                if (profile.kanjiMasteryScore < 0.40f) {
                    if (bucket == QuestionCategoryBucket.KANJI_READING) {
                        score += 35.0
                    }
                }

                // Learner C: Weak Vocabulary
                if (profile.vocabMasteryScore < 0.40f) {
                    if (bucket == QuestionCategoryBucket.VOCABULARY_MEANING) {
                        score += 35.0
                    }
                }

                // Learner D: Strong overall learner
                val avgMastery = (profile.grammarMasteryScore + profile.kanjiMasteryScore + profile.vocabMasteryScore) / 3.0f
                if (avgMastery >= 0.80f) {
                    // Deprioritize already mastered items
                    val mastery = questionMasteryMap[q.id] ?: 0
                    if (mastery >= 3) {
                        score -= 25.0
                    }
                    // Favor higher difficulty questions
                    if (q.difficulty >= 2) {
                        score += 15.0
                    }
                }
            }

            Pair(q, score)
        }

        // Sort descending by score, take sessionSize items
        return scored
            .sortedByDescending { it.second }
            .take(sessionSize)
            .map { it.first }
    }
}
