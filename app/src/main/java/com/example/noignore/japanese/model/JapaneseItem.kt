package com.example.noignore.japanese.model

enum class JapaneseCategory(val displayName: String, val emoji: String) {
    KANJI("Kanji", "🈁"),
    VOCAB("Vocabulary", "📖"),
    GRAMMAR("Grammar", "⛩️"),
    JLPT_EXAM("JLPT Exam", "🎯"),
    KANA("Kana", "🔤")
}

data class KanjiCompound(
    val word: String,
    val reading: String,
    val meaning: String,
    val romaji: String = ""
)

data class JapaneseItem(
    val id: String,
    val japanese: String,
    val reading: String,
    val romaji: String,
    val meaning: String,
    val category: JapaneseCategory,
    val jlptLevel: String = "N5", // "N5", "N4", "N3", "N2", "N1"
    val onyomi: String = "", // e.g. "ニチ, ジツ"
    val onyomiRomaji: String = "", // e.g. "nichi, jitsu"
    val kunyomi: String = "", // e.g. "ひ, -び, -か"
    val kunyomiRomaji: String = "", // e.g. "hi, -bi, -ka"
    val strokeCount: Int = 0, // e.g. 4
    val radical: String = "", // e.g. "日"
    val radicalRomaji: String = "", // e.g. "hi, nichi"
    val radicalMeaning: String = "", // e.g. "sun, day"
    val structure: String = "", // Grammar formation rule / structural template
    val mnemonicOrNote: String = "",
    val strokeOrderSteps: List<String> = emptyList(), // Step-by-step stroke descriptions
    val exampleJapanese: String = "",
    val exampleReading: String = "",
    val exampleRomaji: String = "",
    val exampleEnglish: String = "",
    val compounds: List<KanjiCompound> = emptyList(), // e.g. [今日(きょう), 日本(にほん)]
    val examQuestionType: String = "", // e.g. "Kanji Reading", "Orthography", "Contextual Grammar"
    val examQuestionPrompt: String = "", // Context sentence for JLPT exam drills
    val options: List<String> = emptyList(), // For multiple choice quiz (first option is the correct answer)
    var mastery: Int = 0, // 0: New, 1: Learning, 2: Familiar, 3: Mastered
    var reviewCount: Int = 0,
    var lastReviewedAt: Long = 0L
)
