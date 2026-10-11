package com.example.noignore.japanese.util

import com.example.noignore.japanese.model.JapaneseItem

data class ParsedToken(
    val surface: String,
    val reading: String = "",
    val pos: String = "OTHER", // NOUN, VERB, ADJECTIVE, PARTICLE, AUXILIARY, PUNCTUATION, OTHER
    val matchedItem: JapaneseItem? = null
)

/**
 * Local Morphological Japanese Tokenizer & Sentence Mining Engine.
 *
 * Performs zero-latency offline segmentation and part-of-speech tagging of Japanese text:
 * - Differentiates kanji compounds (nouns/verbs/adjectives), kana inflections, and grammatical particles.
 * - Extracts vocabulary candidates and cross-references them with the app's comprehensive dictionary.
 * - Enables instant 1-tap card creation ("sentence mining") from any example sentence or reading passage.
 */
object JapaneseSentenceTokenizer {

    // Common particles in Japanese syntax
    private val PARTICLES = setOf(
        "は", "が", "を", "に", "へ", "で", "と", "から", "まで", "より",
        "も", "ね", "よ", "か", "の", "や", "など", "ば", "て", "ても", "でも"
    )

    // Common auxiliary endings
    private val AUXILIARIES = setOf(
        "です", "ます", "でした", "ました", "ません", "だ", "である", "たい",
        "ない", "なかった", "らしい", "そうだ", "ようだ", "すぎる"
    )

    // Common verb inflectional suffixes for stemming
    private val VERB_SUFFIXES = listOf(
        "ました", "ません", "てある", "ている", "ておく", "てみる",
        "ます", "たい", "ない", "た", "て", "ば"
    )

    private val PUNCTUATION = setOf(
        '。', '、', '！', '？', '「', '」', '（', '）', '…', ' ', '　', '・'
    )

    /**
     * Tokenizes a raw Japanese sentence into constituent morphological units,
     * identifying parts of speech and cross-referencing against available dictionary items.
     */
    fun tokenize(sentence: String, dictionary: List<JapaneseItem> = emptyList()): List<ParsedToken> {
        if (sentence.isBlank()) return emptyList()

        val dictMap = dictionary.associateBy { it.japanese }
        val tokens = mutableListOf<ParsedToken>()
        var index = 0
        val length = sentence.length

        while (index < length) {
            val char = sentence[index]

            // 1. Punctuation handling
            if (char in PUNCTUATION) {
                tokens.add(ParsedToken(surface = char.toString(), pos = "PUNCTUATION"))
                index++
                continue
            }

            // 2. Maximal dictionary matching (Greedy longest-match substring)
            var matched: JapaneseItem? = null
            var matchLen = 0
            val maxLookahead = minOf(12, length - index)

            for (len in maxLookahead downTo 1) {
                val candidate = sentence.substring(index, index + len)
                val item = dictMap[candidate]
                if (item != null) {
                    matched = item
                    matchLen = len
                    break
                }
            }

            if (matched != null && matchLen > 0) {
                val surface = sentence.substring(index, index + matchLen)
                val pos = when (matched.category.name) {
                    "KANJI" -> "NOUN"
                    "VOCAB" -> "VOCAB"
                    "GRAMMAR" -> "GRAMMAR"
                    else -> "OTHER"
                }
                tokens.add(
                    ParsedToken(
                        surface = surface,
                        reading = matched.reading,
                        pos = pos,
                        matchedItem = matched
                    )
                )
                index += matchLen
                continue
            }

            // 3. Multi-character Particle / Auxiliary matching
            var auxMatched = false
            for (aux in AUXILIARIES) {
                if (sentence.startsWith(aux, index)) {
                    tokens.add(ParsedToken(surface = aux, pos = "AUXILIARY"))
                    index += aux.length
                    auxMatched = true
                    break
                }
            }
            if (auxMatched) continue

            // 4. Single-character particle check (when preceded by a content word)
            val singleCharStr = char.toString()
            if (singleCharStr in PARTICLES && index > 0) {
                tokens.add(ParsedToken(surface = singleCharStr, pos = "PARTICLE"))
                index++
                continue
            }

            // 5. Morphological boundary grouping: Kanji run or Katakana run or Hiragana run
            val startIdx = index
            val isKanji = isKanjiChar(char)
            val isKatakana = isKatakanaChar(char)

            if (isKanji) {
                // Collect contiguous kanji run (often followed by okurigana)
                while (index < length && isKanjiChar(sentence[index])) {
                    index++
                }
                // Check if followed by inflectional okurigana (e.g., 食べる -> 食べ)
                var okuriEnd = index
                while (okuriEnd < length && isHiraganaChar(sentence[okuriEnd]) && sentence[okuriEnd].toString() !in PARTICLES) {
                    okuriEnd++
                    // Check if okurigana completes a known word
                    val wordWithOkuri = sentence.substring(startIdx, okuriEnd)
                    val dictHit = dictMap[wordWithOkuri]
                    if (dictHit != null) {
                        index = okuriEnd
                        matched = dictHit
                        break
                    }
                }
                val word = sentence.substring(startIdx, index)
                tokens.add(
                    ParsedToken(
                        surface = word,
                        pos = if (matched != null) "VOCAB" else "NOUN",
                        matchedItem = matched ?: dictMap[word]
                    )
                )
            } else if (isKatakana) {
                // Collect contiguous katakana loanword
                while (index < length && (isKatakanaChar(sentence[index]) || sentence[index] == 'ー')) {
                    index++
                }
                val word = sentence.substring(startIdx, index)
                tokens.add(ParsedToken(surface = word, pos = "NOUN", matchedItem = dictMap[word]))
            } else {
                // Hiragana grammatical or inflectional segment
                while (index < length && isHiraganaChar(sentence[index]) && sentence[index].toString() !in PARTICLES) {
                    index++
                }
                val word = sentence.substring(startIdx, index)
                if (word.isNotBlank()) {
                    tokens.add(ParsedToken(surface = word, pos = "OTHER", matchedItem = dictMap[word]))
                } else {
                    index++
                }
            }
        }

        return tokens
    }

    private fun isKanjiChar(c: Char): Boolean {
        return c.code in 0x4E00..0x9FAF || c.code in 0x3400..0x4DBF
    }

    private fun isHiraganaChar(c: Char): Boolean {
        return c.code in 0x3040..0x309F
    }

    private fun isKatakanaChar(c: Char): Boolean {
        return c.code in 0x30A0..0x30FF
    }
}
