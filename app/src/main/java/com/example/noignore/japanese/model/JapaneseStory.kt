package com.example.noignore.japanese.model

/**
 * Individual sentence inside a Japanese short story.
 * Includes furigana (hiragana reading guide), romaji, and contextual English translation.
 */
data class StorySentence(
    val id: Int,
    val japanese: String,
    val furigana: String,
    val romaji: String,
    val english: String
)

/**
 * Key vocabulary item in a story.
 */
data class StoryVocab(
    val word: String,
    val reading: String,
    val meaning: String
)

/**
 * Reading comprehension question for a story.
 */
data class StoryQuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

/**
 * Graded Japanese short story for reading & listening practice.
 */
data class JapaneseStory(
    val id: String,
    val title: String,
    val titleEnglish: String,
    val jlptLevel: String, // "N5", "N4", "N3"
    val emoji: String,
    val summary: String,
    val sentences: List<StorySentence>,
    val keyVocabulary: List<StoryVocab>,
    val comprehensionQuestions: List<StoryQuizQuestion>,
    val estimatedReadMinutes: Int = 2,
    var isCompleted: Boolean = false
) {
    /**
     * Concatenated full Japanese text for continuous TTS playback.
     */
    val fullJapaneseText: String
        get() = sentences.joinToString(" ") { it.japanese }
}
