package com.example.noignore.japanese.model

/**
 * Radical component breakdown for deep kanji deconstruction (Wanikani / Heisig style).
 */
data class KanjiRadicalPart(
    val radical: String,
    val name: String,
    val meaning: String,
    val position: String = "" // e.g. "Left (偏 - hen)", "Right (旁 - tsukuri)", "Top (冠 - kanmuri)", "Enclosure"
)

/**
 * Rich mnemonic story with visual breakdown and Wanikani-style component explanation.
 */
data class KanjiMnemonicBreakdown(
    val kanji: String,
    val components: List<KanjiRadicalPart>,
    val mnemonicStory: String,
    val strokeOrderTip: String = "",
    val confusableLookalikes: List<String> = emptyList(), // e.g. ["持", "待", "特"]
    val confusableNote: String = ""
)

/**
 * Core word collocation showing natural lexical pairings.
 * e.g. 傘 (umbrella) + 差す (to put up) -> 傘を差す
 */
data class WordCollocation(
    val phrase: String,
    val reading: String,
    val romaji: String,
    val englishMeaning: String,
    val pitchAccentPattern: String = "" // e.g. "Heiban (平板) [0]", "Atamadaka (頭高) [1]"
)

/**
 * Extended grammar breakdown showing formation formulas, register nuance, and common pitfalls.
 */
data class GrammarFormBreakdown(
    val pattern: String,
    val formationFormula: String, // e.g. "Verb [Dict Form] + ことがある"
    val politenessRegister: String, // "Polite (丁寧語)", "Casual (ため口)", "Honorific (尊敬語)", "Humble (謙譲語)"
    val nuanceNotes: String, // Difference from similar grammar
    val commonMistakesToAvoid: String,
    val realWorldExample: String,
    val realWorldReading: String,
    val realWorldEnglish: String
)
