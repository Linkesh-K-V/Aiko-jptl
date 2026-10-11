package com.example.noignore.japanese.model

data class ReadingPassageSentence(
    val japanese: String,
    val furigana: String,
    val romaji: String,
    val english: String
)

data class ReadingLesson(
    val id: String,
    val title: String,
    val titleEnglish: String,
    val jlptLevel: String, // "N5", "N4", "N3"
    val genre: String, // "Daily Diary", "Notice / Sign", "Email Note", "Recipe", "Short Article"
    val emoji: String,
    val sentences: List<ReadingPassageSentence>,
    val grammarNote: String,
    val keyVocabulary: List<StoryVocab>,
    val questions: List<StoryQuizQuestion>
) {
    val fullJapanese: String
        get() = sentences.joinToString(" ") { it.japanese }
}
