package com.example.noignore.japanese.engine

data class FuriganaSegment(
    val surface: String,
    val reading: String? = null // Null if kana or punctuation (no ruby needed)
)

/**
 * Intelligent Furigana Alignment Engine.
 * Matches kanji clusters with their corresponding kana readings to ensure precise
 * ruby placement without text desynchronization.
 */
object FuriganaEngine {

    fun isKanji(char: Char): Boolean {
        val code = char.code
        return (code in 0x4E00..0x9FFF) || (code in 0x3400..0x4DBF)
    }

    /**
     * Parses standard bracketed ruby format like `私[わたし]は学生[がくせい]です`
     * or aligns simple kanji+reading pairs.
     */
    fun parseBracketedFurigana(annotatedText: String): List<FuriganaSegment> {
        val segments = mutableListOf<FuriganaSegment>()
        val regex = Regex("([^\\s\\[\\]]+)\\[([^\\]]+)\\]")
        var lastIndex = 0

        for (match in regex.findAll(annotatedText)) {
            val range = match.range
            if (range.first > lastIndex) {
                val nonRubyText = annotatedText.substring(lastIndex, range.first)
                segments.add(FuriganaSegment(nonRubyText, null))
            }
            val surface = match.groupValues[1]
            val ruby = match.groupValues[2]
            segments.add(FuriganaSegment(surface, ruby))
            lastIndex = range.last + 1
        }

        if (lastIndex < annotatedText.length) {
            val remaining = annotatedText.substring(lastIndex)
            segments.add(FuriganaSegment(remaining, null))
        }

        return segments
    }

    /**
     * Auto-aligns a single Japanese word and its kana reading if no brackets are provided.
     * e.g., "食べる", "たべる" -> ["食"[た], "べる"[null]]
     */
    fun alignWord(kanjiWord: String, readingKana: String): List<FuriganaSegment> {
        if (!kanjiWord.any { isKanji(it) } || kanjiWord == readingKana) {
            return listOf(FuriganaSegment(kanjiWord, null))
        }

        // Find common kana suffix (okurigana)
        var suffixLen = 0
        while (suffixLen < kanjiWord.length && suffixLen < readingKana.length) {
            val kChar = kanjiWord[kanjiWord.length - 1 - suffixLen]
            val rChar = readingKana[readingKana.length - 1 - suffixLen]
            if (kChar == rChar && !isKanji(kChar)) {
                suffixLen++
            } else {
                break
            }
        }

        // Find common kana prefix
        var prefixLen = 0
        while (prefixLen < (kanjiWord.length - suffixLen) && prefixLen < (readingKana.length - suffixLen)) {
            val kChar = kanjiWord[prefixLen]
            val rChar = readingKana[prefixLen]
            if (kChar == rChar && !isKanji(kChar)) {
                prefixLen++
            } else {
                break
            }
        }

        val segments = mutableListOf<FuriganaSegment>()

        if (prefixLen > 0) {
            segments.add(FuriganaSegment(kanjiWord.substring(0, prefixLen), null))
        }

        val kanjiCore = kanjiWord.substring(prefixLen, kanjiWord.length - suffixLen)
        val readingCore = readingKana.substring(prefixLen, readingKana.length - suffixLen)

        if (kanjiCore.isNotBlank()) {
            segments.add(FuriganaSegment(kanjiCore, readingCore))
        }

        if (suffixLen > 0) {
            segments.add(FuriganaSegment(kanjiWord.substring(kanjiWord.length - suffixLen), null))
        }

        return segments
    }
}
