package com.example.noignore.japanese.model

/**
 * Represents a discrete study item inside a JLPT Topic (e.g. a specific particle,
 * verb pattern, kanji breakdown, or expression).
 */
data class JlptTopicItem(
    val id: String,
    val japanese: String,
    val reading: String,
    val romaji: String,
    val meaning: String,
    val patternFormula: String = "",
    val explanation: String = "",
    val exampleJapanese: String = "",
    val exampleReading: String = "",
    val exampleEnglish: String = "",
    val mnemonicTip: String = ""
)

/**
 * High-level topic category across all JLPT levels.
 */
enum class JlptTopicCategory(
    val label: String,
    val japaneseLabel: String,
    val emoji: String
) {
    ALL("All Topics", "全て", "🌟"),
    GRAMMAR("Grammar & Particles", "文法・助詞", "⛩️"),
    KANJI("Kanji & Radicals", "漢字・部首", "🈸"),
    VOCABULARY("Vocabulary Themes", "語彙", "📖"),
    EXPRESSIONS("Everyday Expressions", "日常会話", "🗣️"),
    LISTENING("Listening & Dialogue", "聴解", "🎧"),
    READING("Reading & Passages", "読解", "📚")
}

/**
 * A cohesive Topic Unit within a specific JLPT Level (N5, N4, N3, N2, N1).
 * Contains an overview, Teacher Aiko's advice, and structured list of items.
 */
data class JlptTopicUnit(
    val id: String,
    val level: JlptExamLevel,
    val category: JlptTopicCategory,
    val title: String,
    val japaneseTitle: String,
    val overview: String,
    val senseiTip: String,
    val items: List<JlptTopicItem>
)
