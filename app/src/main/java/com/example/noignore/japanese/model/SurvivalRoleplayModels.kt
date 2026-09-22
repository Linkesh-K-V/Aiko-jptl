package com.example.noignore.japanese.model

/**
 * Speaker in a real-world scenario dialogue.
 */
data class DialogueTurn(
    val speakerRole: String, // "店員 (Clerk)", "客 (Customer)", "アナウンス (Announcer)", "車掌 (Conductor)", "店長 (Manager)"
    val japaneseText: String,
    val readingText: String,
    val romajiText: String,
    val englishText: String,
    val culturalNuanceTip: String = "",
    val suggestedLearnerResponse: Boolean = false // If true, this turn is meant for the user to shadow or reply
)

/**
 * Practical Japanese Survival Scenario module.
 */
data class SurvivalRoleplayScenario(
    val id: String,
    val title: String,
    val japaneseTitle: String,
    val categoryEmoji: String,
    val locationDescription: String,
    val jlptLevel: String,
    val turns: List<DialogueTurn>,
    val survivalKeyPhrases: List<String> = emptyList()
)
