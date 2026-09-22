package com.example.noignore.japanese.model

/**
 * JLPT Star Question (並べ替え問題) where the student arranges 4 scrambled fragments
 * and identifies which tile lands in the star position (★).
 */
data class JlptStarSentenceQuestion(
    val id: String,
    val jlptLevel: String, // "N5", "N4", "N3", "N2", "N1"
    val sentencePrefix: String, // Context before the 4 blank spots
    val sentenceSuffix: String, // Context after the 4 blank spots
    val scrambledTiles: List<String>, // Exactly 4 parts to arrange
    val correctOrderIndices: List<Int>, // 0-based indices representing the canonical order of scrambledTiles
    val starPositionIndex: Int = 2, // Which position (0..3) has the ★ (defaults to 3rd blank, typical JLPT style)
    val fullSentenceJapanese: String,
    val fullSentenceReading: String,
    val englishMeaning: String,
    val grammarExplanation: String
) {
    val correctTileAtStar: String
        get() = scrambledTiles[correctOrderIndices[starPositionIndex]]
}

/**
 * Particle drill question focusing on high-frequency confusing Japanese particles
 * (は vs が, に vs で, を vs に, で vs に).
 */
data class ParticleDrillQuestion(
    val id: String,
    val jlptLevel: String,
    val sentenceWithBlank: String, // e.g. "図書館 [___] 本を借りました。"
    val blankFuriganaSentence: String,
    val correctParticle: String, // e.g. "で"
    val options: List<String>, // e.g. ["で", "に", "を", "は"]
    val englishMeaning: String,
    val grammaticalReason: String // Explanation why "で" is used (action venue vs static location)
)
