package com.example.noignore.japanese.curriculum.prerequisite

import com.example.noignore.data.learner.LearnerProfileEntity
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.ui.onboarding.BeginnerStartingLevel

/**
 * Unified Central Prerequisite Engine.
 * Authoritative system defining prerequisites, gating, and pedagogical sequencing
 * across Kana, Vocabulary, Grammar, Kanji, Reading, and Listening.
 */
object PrerequisiteEngine {

    const val KANJI_MIN_KANA_MASTERY_THRESHOLD = 0.50f
    const val READING_MIN_KANA_MASTERY_THRESHOLD = 0.60f
    const val READING_MIN_VOCAB_MASTERY_THRESHOLD = 0.30f
    const val READING_MIN_GRAMMAR_MASTERY_THRESHOLD = 0.30f

    /**
     * Kanji Gating: Beginners cannot be assigned heavy Kanji study until basic
     * Kana literacy is established.
     */
    fun isKanjiGated(kanaMasteryScore: Float): Boolean {
        return kanaMasteryScore < KANJI_MIN_KANA_MASTERY_THRESHOLD
    }

    /**
     * Reading Readiness Gating: Learner must possess sufficient Kana decoding speed,
     * vocabulary base, and basic grammar before encountering passage reading.
     */
    fun isReadingReady(
        kanaMasteryScore: Float,
        vocabMasteryScore: Float,
        grammarMasteryScore: Float
    ): Boolean {
        return kanaMasteryScore >= READING_MIN_KANA_MASTERY_THRESHOLD &&
                vocabMasteryScore >= READING_MIN_VOCAB_MASTERY_THRESHOLD &&
                grammarMasteryScore >= READING_MIN_GRAMMAR_MASTERY_THRESHOLD
    }

    /**
     * Listening Readiness Gating.
     */
    fun isListeningReady(
        kanaMasteryScore: Float,
        vocabMasteryScore: Float
    ): Boolean {
        return kanaMasteryScore >= READING_MIN_KANA_MASTERY_THRESHOLD &&
                vocabMasteryScore >= READING_MIN_VOCAB_MASTERY_THRESHOLD
    }

    // Delegation to Kana Prerequisite Model
    fun getKanaStage(ch: String): Int = KanaPrerequisiteModel.getStageForCharacter(ch)
    fun getKanaStageForId(id: String): Int = KanaPrerequisiteModel.getStageForId(id)

    fun isKanaUnlocked(
        ch: String,
        kanaMasteryScore: Float,
        completedLessonIds: Set<Int> = emptySet()
    ): Boolean = KanaPrerequisiteModel.isCharacterUnlocked(ch, kanaMasteryScore, completedLessonIds)

    fun filterAccessibleKana(
        items: List<JapaneseItem>,
        kanaMasteryScore: Float,
        completedLessonIds: Set<Int> = emptySet()
    ): List<JapaneseItem> = KanaPrerequisiteModel.filterAccessibleKana(items, kanaMasteryScore, completedLessonIds)

    // Delegation to Vocab Prerequisite Model
    fun evaluateVocabulary(
        item: JapaneseItem,
        knownKana: Set<Char>,
        knownKanji: Set<String> = emptySet(),
        knownGrammarIds: Set<String> = emptySet()
    ): VocabPrerequisiteResult = VocabPrerequisiteModel.evaluatePrerequisites(item, knownKana, knownKanji, knownGrammarIds)

    fun filterAppropriateVocab(
        items: List<JapaneseItem>,
        knownKana: Set<Char>,
        knownKanji: Set<String> = emptySet(),
        knownGrammarIds: Set<String> = emptySet()
    ): List<JapaneseItem> = VocabPrerequisiteModel.getPrioritizedBeginnerVocab(items, knownKana, knownKanji, knownGrammarIds)

    // Delegation to Grammar Prerequisite Graph
    fun getGrammarPrerequisites(grammarId: String): List<String> =
        GrammarPrerequisiteGraph.getDirectPrerequisites(grammarId)

    fun getAllGrammarPrerequisites(grammarId: String): Set<String> =
        GrammarPrerequisiteGraph.getAllTransitivePrerequisites(grammarId)

    fun isGrammarUnlocked(grammarId: String, masteredGrammarIds: Set<String>): Boolean =
        GrammarPrerequisiteGraph.isGrammarUnlocked(grammarId, masteredGrammarIds)

    fun getPedagogicalGrammarSequence(): List<String> =
        GrammarPrerequisiteGraph.getPedagogicalSequence()

    /**
     * Initializes or recalibrates the Learner Profile scores according to the chosen starting level,
     * ensuring immediate pedagogical synchronization.
     */
    fun createStartingProfile(
        level: BeginnerStartingLevel,
        dailyTargetMinutes: Int = 15
    ): LearnerProfileEntity {
        return when (level) {
            BeginnerStartingLevel.ABSOLUTE_BEGINNER -> {
                LearnerProfileEntity(
                    id = "default_profile",
                    kanaMasteryScore = 0.05f,
                    vocabMasteryScore = 0.0f,
                    kanjiMasteryScore = 0.0f,
                    grammarMasteryScore = 0.0f,
                    readingMasteryScore = 0.0f,
                    listeningMasteryScore = 0.0f,
                    recallStrengthScore = 0.1f,
                    preferredStudyDurationMinutes = dailyTargetMinutes,
                    immersionLevel = "BEGINNER",
                    lastCalculatedMs = System.currentTimeMillis()
                )
            }
            BeginnerStartingLevel.SOME_KANA -> {
                LearnerProfileEntity(
                    id = "default_profile",
                    kanaMasteryScore = 0.85f,
                    vocabMasteryScore = 0.20f,
                    kanjiMasteryScore = 0.10f,
                    grammarMasteryScore = 0.15f,
                    readingMasteryScore = 0.15f,
                    listeningMasteryScore = 0.15f,
                    recallStrengthScore = 0.5f,
                    preferredStudyDurationMinutes = dailyTargetMinutes,
                    immersionLevel = "BEGINNER",
                    lastCalculatedMs = System.currentTimeMillis()
                )
            }
            BeginnerStartingLevel.ADVANCED_EXPLORER -> {
                LearnerProfileEntity(
                    id = "default_profile",
                    kanaMasteryScore = 1.0f,
                    vocabMasteryScore = 0.60f,
                    kanjiMasteryScore = 0.50f,
                    grammarMasteryScore = 0.50f,
                    readingMasteryScore = 0.50f,
                    listeningMasteryScore = 0.50f,
                    recallStrengthScore = 0.7f,
                    preferredStudyDurationMinutes = dailyTargetMinutes,
                    immersionLevel = "INTERMEDIATE",
                    lastCalculatedMs = System.currentTimeMillis()
                )
            }
        }
    }

    /**
     * Checks if learner satisfies N5 certification criteria.
     */
    fun isN5Certified(profile: LearnerProfileEntity, diagnosticScore: Float = 0f): Boolean {
        return com.example.noignore.japanese.curriculum.transition.LearnerTransitionManager
            .evaluateN5Certification(profile, diagnosticScore).isCertified
    }

    /**
     * Checks if N4 Bridge requirements are satisfied.
     */
    fun isN4BridgeComplete(bridgeSessionsCompleted: Int, bridgeDiagnosticScore: Float): Boolean {
        return com.example.noignore.japanese.curriculum.transition.LearnerTransitionManager
            .evaluateN4Bridge(bridgeSessionsCompleted, bridgeDiagnosticScore).isBridgeComplete
    }

    /**
     * Checks if the learner is educationally ready to study N4 content.
     */
    fun isN4Ready(
        stage: com.example.noignore.japanese.curriculum.transition.LearnerStage,
        profile: LearnerProfileEntity
    ): Boolean {
        return stage.isN4Accessible() && profile.kanaMasteryScore >= 0.90f && profile.grammarMasteryScore >= 0.85f
    }

    /**
     * Centralized content access check ensuring uncertified learners cannot access
     * unready levels or content without required prerequisites.
     */
    fun isContentUnlocked(
        contentId: String,
        contentLevel: String,
        contentCategory: String,
        stage: com.example.noignore.japanese.curriculum.transition.LearnerStage,
        profile: LearnerProfileEntity,
        masteredIds: Set<String> = emptySet()
    ): Boolean {
        // N4+ content is strictly locked unless learner is in N4_ACTIVE or N4_COMPLETED
        if (contentLevel.uppercase() == "N4") {
            if (!stage.isN4Accessible()) return false
        }

        return when (contentCategory.uppercase()) {
            "KANA" -> true
            "KANJI" -> !isKanjiGated(profile.kanaMasteryScore)
            "READING" -> isReadingReady(profile.kanaMasteryScore, profile.vocabMasteryScore, profile.grammarMasteryScore)
            "LISTENING" -> isListeningReady(profile.kanaMasteryScore, profile.vocabMasteryScore)
            "GRAMMAR" -> isGrammarUnlocked(contentId, masteredIds)
            else -> true
        }
    }
}
