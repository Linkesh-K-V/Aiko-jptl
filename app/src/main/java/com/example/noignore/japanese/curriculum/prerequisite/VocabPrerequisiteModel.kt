package com.example.noignore.japanese.curriculum.prerequisite

import com.example.noignore.japanese.model.JapaneseItem

enum class VocabPrerequisiteLevel(val label: String, val description: String) {
    LEVEL_A("Level A: Kana Only", "Can be learned and decoded with Kana only; zero Kanji dependency."),
    LEVEL_B("Level B: Basic Kanji Recognition", "Requires basic Kana literacy plus 1-2 elementary N5 Kanji."),
    LEVEL_C("Level C: Grammar Dependent", "Requires verb conjugations, adjective forms, or copula understanding."),
    LEVEL_D("Level D: Complex Compound", "Multi-concept phrases, complex time expressions, or counter compounds.")
}

data class VocabPrerequisiteResult(
    val itemId: String,
    val japanese: String,
    val reading: String,
    val level: VocabPrerequisiteLevel,
    val requiredKana: Set<Char>,
    val missingKana: Set<Char>,
    val requiredKanji: Set<String>,
    val missingKanji: Set<String>,
    val requiredGrammarIds: List<String>,
    val isAppropriate: Boolean,
    val pedagogicalRationale: String
)

object VocabPrerequisiteModel {

    // Regex ranges for Japanese scripts
    private val KANJI_REGEX = Regex("[\\u4E00-\\u9FFF]")
    private val HIRAGANA_REGEX = Regex("[\\u3040-\\u309F]")
    private val KATAKANA_REGEX = Regex("[\\u30A0-\\u30FF]")

    fun extractKanji(text: String): Set<String> {
        return KANJI_REGEX.findAll(text).map { it.value }.toSet()
    }

    fun extractKana(text: String): Set<Char> {
        return text.filter { HIRAGANA_REGEX.matches(it.toString()) || KATAKANA_REGEX.matches(it.toString()) }.toSet()
    }

    fun classifyVocabLevel(item: JapaneseItem): VocabPrerequisiteLevel {
        val kanjiInWord = extractKanji(item.japanese)

        val isGrammarDependent = item.japanese.endsWith("ます") ||
                item.japanese.endsWith("たい") ||
                item.japanese.contains("です") ||
                item.meaning.contains("want to", ignoreCase = true) ||
                item.meaning.contains("please", ignoreCase = true) ||
                item.meaning.startsWith("to ", ignoreCase = true) && (item.japanese.endsWith("る") || item.japanese.endsWith("う") || item.japanese.endsWith("く") || item.japanese.endsWith("す") || item.japanese.endsWith("つ") || item.japanese.endsWith("む"))

        val isComplexCompound = kanjiInWord.size >= 3 ||
                item.japanese.contains("何時") ||
                item.japanese.contains("分") ||
                item.japanese.contains("日") && kanjiInWord.size > 1 ||
                item.meaning.contains("every morning", ignoreCase = true)

        return when {
            isComplexCompound -> VocabPrerequisiteLevel.LEVEL_D
            isGrammarDependent -> VocabPrerequisiteLevel.LEVEL_C
            kanjiInWord.isNotEmpty() -> VocabPrerequisiteLevel.LEVEL_B
            else -> VocabPrerequisiteLevel.LEVEL_A
        }
    }

    fun evaluatePrerequisites(
        item: JapaneseItem,
        knownKana: Set<Char>,
        knownKanji: Set<String>,
        knownGrammarIds: Set<String>
    ): VocabPrerequisiteResult {
        val level = classifyVocabLevel(item)
        val readingSource = if (item.reading.isNotBlank()) item.reading else item.japanese
        val reqKana = extractKana(readingSource)
        val missingKana = reqKana - knownKana

        val reqKanji = extractKanji(item.japanese)
        val missingKanji = reqKanji - knownKanji

        val reqGrammar = mutableListOf<String>()
        if (item.japanese.endsWith("ます") || item.japanese.contains("ました")) {
            reqGrammar.add("n5_g_27")
        }
        if (item.japanese.contains("です")) {
            reqGrammar.add("n5_g_01")
        }
        if (item.japanese.contains("たい")) {
            reqGrammar.add("n5_g_12")
        }

        val missingGrammar = reqGrammar.filter { !knownGrammarIds.contains(it) }

        // A vocabulary item is appropriate if:
        // 1. Missing kana is 0 (or at most 1 when learner is in active training)
        // 2. If Level B, either kanji is known, or furigana is provided
        // 3. Dependent grammar is satisfied
        val kanaAppropriate = missingKana.isEmpty()
        val kanjiAppropriate = level == VocabPrerequisiteLevel.LEVEL_A || missingKanji.isEmpty() || item.reading.isNotBlank()
        val grammarAppropriate = missingGrammar.isEmpty()

        val isAppropriate = kanaAppropriate && kanjiAppropriate && grammarAppropriate

        val rationale = buildString {
            if (!kanaAppropriate) {
                append("Unfamiliar Kana: [${missingKana.joinToString(", ")}]. ")
            }
            if (missingKanji.isNotEmpty() && item.reading.isBlank()) {
                append("Unlearned Kanji without reading: [${missingKanji.joinToString(", ")}]. ")
            }
            if (missingGrammar.isNotEmpty()) {
                append("Missing dependent grammar: [${missingGrammar.joinToString(", ")}]. ")
            }
            if (isAppropriate) {
                append("All prerequisites satisfied. Perfectly aligned for study.")
            }
        }

        return VocabPrerequisiteResult(
            itemId = item.id,
            japanese = item.japanese,
            reading = item.reading,
            level = level,
            requiredKana = reqKana,
            missingKana = missingKana,
            requiredKanji = reqKanji,
            missingKanji = missingKanji,
            requiredGrammarIds = reqGrammar,
            isAppropriate = isAppropriate,
            pedagogicalRationale = rationale
        )
    }

    /**
     * Filters and sorts vocabulary items for a beginner, prioritizing Level A and Level B items
     * whose Kana are already known, preventing unexpected vocabulary barriers.
     */
    fun getPrioritizedBeginnerVocab(
        items: List<JapaneseItem>,
        knownKana: Set<Char>,
        knownKanji: Set<String> = emptySet(),
        knownGrammarIds: Set<String> = emptySet()
    ): List<JapaneseItem> {
        val evaluated = items.map { item ->
            Pair(item, evaluatePrerequisites(item, knownKana, knownKanji, knownGrammarIds))
        }

        return evaluated
            .sortedWith(
                compareBy<Pair<JapaneseItem, VocabPrerequisiteResult>> { !it.second.isAppropriate }
                    .thenBy { it.second.missingKana.size }
                    .thenBy { it.second.level.ordinal }
            )
            .map { it.first }
    }
}
