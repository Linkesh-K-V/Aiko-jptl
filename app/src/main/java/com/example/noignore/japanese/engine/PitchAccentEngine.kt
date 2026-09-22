package com.example.noignore.japanese.engine

enum class PitchPattern(val labelJp: String, val labelEn: String, val ruleDescription: String) {
    HEIBAN("平板 (0)", "Flat (Heiban)", "Starts low, rises on 2nd mora, stays high through the following particle."),
    ATAMADAKA("頭高 (1)", "Head-high (Atamadaka)", "Starts high on the 1st mora, drops immediately to low for all subsequent morae."),
    NAKADAKA("中高 (2+)", "Middle-high (Nakadaka)", "Starts low, rises, peaks on an internal mora, and drops before the end."),
    ODAKA("尾高 (尾)", "Tail-high (Odaka)", "Starts low, rises, stays high through the word, then drops on the attached particle.")
}

data class PitchAccentInfo(
    val word: String,
    val reading: String,
    val pattern: PitchPattern,
    val accentMoraIndex: Int, // 0 = heiban, 1 = atamadaka, 2..k = nakadaka, -1 = odaka
    val pitchGraph: List<Boolean> // true = High, false = Low for each mora
)

/**
 * Standard Tokyo Dialect Pitch Accent Reference Engine.
 * Formulates mora breakdown, pitch pattern classification, and visual high-low binary contours.
 */
object PitchAccentEngine {

    /**
     * Splits a Japanese kana reading into its constituent linguistic morae.
     * Small kana (ゃ, ゅ, ょ, ゎ, ぁ, ぃ, ぅ, ぇ, ぉ, っ) attach to the preceding mora.
     */
    fun splitIntoMorae(reading: String): List<String> {
        val morae = mutableListOf<String>()
        val smallChars = setOf('ゃ', 'ゅ', 'ょ', 'ゎ', 'ぁ', 'ぃ', 'ぅ', 'ぇ', 'ぉ', 'ャ', 'ュ', 'ョ', 'ァ', 'ィ', 'ゥ', 'ェ', 'ォ')
        
        var i = 0
        while (i < reading.length) {
            val c = reading[i]
            if (i + 1 < reading.length && smallChars.contains(reading[i + 1])) {
                morae.add("${c}${reading[i + 1]}")
                i += 2
            } else {
                morae.add(c.toString())
                i++
            }
        }
        return morae
    }

    /**
     * Estimates or retrieves pitch accent for common baseline vocabulary words.
     */
    fun analyzePitchAccent(word: String, reading: String): PitchAccentInfo {
        val morae = splitIntoMorae(reading)
        val moraCount = morae.size.coerceAtLeast(1)

        // Known pitch dictionary lookups
        val (pattern, accentIndex) = when (word) {
            "日本", "にほん" -> Pair(PitchPattern.HEIBAN, 0)
            "水", "みず" -> Pair(PitchPattern.HEIBAN, 0)
            "本", "ほん" -> Pair(PitchPattern.ATAMADAKA, 1)
            "雨", "あめ" -> Pair(PitchPattern.ATAMADAKA, 1)
            "飴", "あめ" -> Pair(PitchPattern.HEIBAN, 0)
            "箸", "はし" -> Pair(PitchPattern.ATAMADAKA, 1)
            "橋", "はし" -> Pair(PitchPattern.ODAKA, -1)
            "端", "はし" -> Pair(PitchPattern.HEIBAN, 0)
            "食べる", "たべる" -> Pair(PitchPattern.NAKADAKA, 2)
            "飲む", "のむ" -> Pair(PitchPattern.ATAMADAKA, 1)
            "見る", "みる" -> Pair(PitchPattern.ATAMADAKA, 1)
            "行く", "いく" -> Pair(PitchPattern.HEIBAN, 0)
            "来る", "くる" -> Pair(PitchPattern.ATAMADAKA, 1)
            "先生", "せんせい" -> Pair(PitchPattern.NAKADAKA, 3)
            "学生", "がくせい" -> Pair(PitchPattern.HEIBAN, 0)
            "学校", "がっこう" -> Pair(PitchPattern.HEIBAN, 0)
            "友達", "ともだち" -> Pair(PitchPattern.HEIBAN, 0)
            else -> Pair(PitchPattern.HEIBAN, 0)
        }

        val graph = mutableListOf<Boolean>()
        when (pattern) {
            PitchPattern.HEIBAN -> {
                for (idx in 0 until moraCount) {
                    graph.add(idx != 0) // Low on mora 0, High on mora 1..n
                }
            }
            PitchPattern.ATAMADAKA -> {
                for (idx in 0 until moraCount) {
                    graph.add(idx == 0) // High on mora 0, Low thereafter
                }
            }
            PitchPattern.NAKADAKA -> {
                for (idx in 0 until moraCount) {
                    graph.add(idx in 1 until accentIndex)
                }
            }
            PitchPattern.ODAKA -> {
                for (idx in 0 until moraCount) {
                    graph.add(idx != 0) // High across word, drops on following particle
                }
            }
        }

        return PitchAccentInfo(
            word = word,
            reading = reading,
            pattern = pattern,
            accentMoraIndex = accentIndex,
            pitchGraph = graph
        )
    }
}
