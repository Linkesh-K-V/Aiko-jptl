package com.example.noignore.japanese.engine

import com.example.noignore.japanese.model.JapaneseCategory
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.japanese.util.JapaneseRomajiHelper

data class SearchFilter(
    val query: String = "",
    val level: String? = null, // "N5", "N4", "N3", "N2", "N1", null for all
    val category: JapaneseCategory? = null,
    val maxStrokeCount: Int? = null,
    val radical: String? = null
)

/**
 * Intelligent Multi-Modal Japanese Search Engine.
 * Supports exact Kanji, Kana reading, fuzzy Romaji transliteration, English meaning,
 * radical filtering, and stroke count constraints.
 */
object JlptSearchEngine {

    fun search(items: List<JapaneseItem>, filter: SearchFilter): List<JapaneseItem> {
        val q = filter.query.trim().lowercase()
        val qRomaji = if (q.isNotBlank()) JapaneseRomajiHelper.toRomaji(q).lowercase() else ""

        return items.filter { item ->
            // Level constraint
            if (filter.level != null && !item.jlptLevel.equals(filter.level, ignoreCase = true)) {
                return@filter false
            }

            // Category constraint
            if (filter.category != null && item.category != filter.category) {
                return@filter false
            }

            // Stroke count constraint
            if (filter.maxStrokeCount != null && item.strokeCount > 0 && item.strokeCount > filter.maxStrokeCount) {
                return@filter false
            }

            // Radical constraint
            if (!filter.radical.isNullOrBlank() && !item.radical.contains(filter.radical, ignoreCase = true)) {
                return@filter false
            }

            // Text query
            if (q.isBlank()) return@filter true

            val jpMatch = item.japanese.lowercase().contains(q)
            val readingMatch = item.reading.lowercase().contains(q)
            val meaningMatch = item.meaning.lowercase().contains(q)
            val romajiMatch = item.romaji.lowercase().contains(q) ||
                    (qRomaji.isNotBlank() && item.romaji.lowercase().contains(qRomaji)) ||
                    (qRomaji.isNotBlank() && JapaneseRomajiHelper.toRomaji(item.reading).lowercase().contains(qRomaji))
            val onyomiMatch = item.onyomi.lowercase().contains(q)
            val kunyomiMatch = item.kunyomi.lowercase().contains(q)
            val radicalMatch = item.radical.lowercase().contains(q)

            jpMatch || readingMatch || meaningMatch || romajiMatch || onyomiMatch || kunyomiMatch || radicalMatch
        }
    }
}
