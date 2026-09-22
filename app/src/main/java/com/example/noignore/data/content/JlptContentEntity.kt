package com.example.noignore.data.content

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.noignore.japanese.model.JapaneseCategory
import com.example.noignore.japanese.model.JapaneseItem

/**
 * Room entity storing the structured JLPT learning items (Kanji, Vocabulary, Grammar, Questions).
 * Backed by structured assets and pre-populated into SQLite Room as the Single Source of Truth.
 */
@Entity(
    tableName = "jlpt_content_items",
    indices = [
        Index(value = ["jlptLevel"]),
        Index(value = ["category"]),
        Index(value = ["jlptLevel", "category"]),
        Index(value = ["japanese"])
    ]
)
data class JlptContentEntity(
    @PrimaryKey
    val id: String,
    val japanese: String,
    val reading: String,
    val romaji: String = "",
    val meaning: String,
    val category: String = "VOCAB", // KANJI, VOCAB, GRAMMAR, JLPT_EXAM, KANA
    val jlptLevel: String = "N5", // N5, N4, N3, N2, N1
    val onyomi: String = "",
    val kunyomi: String = "",
    val strokeCount: Int = 0,
    val radical: String = "",
    val structure: String = "", // Grammar formation rule / structural template
    val mnemonicOrNote: String = "",
    val exampleJapanese: String = "",
    val exampleReading: String = "",
    val exampleRomaji: String = "",
    val exampleEnglish: String = "",
    val partOfSpeech: String = "",
    val difficulty: Int = 1,
    val tags: String = "",
    val relatedItems: String = "",
    val audioReference: String = "",
    val source: String = "EDRDG/Tatoeba/Curriculum",
    val examQuestionType: String = "",
    val examQuestionPrompt: String = "",
    val optionsJson: String = "", // JSON array string e.g. "[\"opt1\",\"opt2\"]"
    val mastery: Int = 0,
    val reviewCount: Int = 0,
    val lastReviewedAt: Long = 0L
) {
    fun getRelatedItemIds(): List<String> {
        if (relatedItems.isBlank()) return emptyList()
        return relatedItems.split(",").map { it.trim() }.filter { it.isNotBlank() }
    }

    fun toJapaneseItem(): JapaneseItem {
        val cat = try {
            JapaneseCategory.valueOf(category)
        } catch (_: Exception) {
            JapaneseCategory.VOCAB
        }

        val optionsList = try {
            if (optionsJson.isNotBlank() && optionsJson.startsWith("[")) {
                val clean = optionsJson.trim().removeSurrounding("[", "]")
                if (clean.isBlank()) emptyList()
                else clean.split(",").map { it.trim().removeSurrounding("\"") }
            } else emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        return JapaneseItem(
            id = id,
            japanese = japanese,
            reading = reading,
            romaji = romaji,
            meaning = meaning,
            category = cat,
            jlptLevel = jlptLevel,
            onyomi = onyomi,
            kunyomi = kunyomi,
            strokeCount = strokeCount,
            radical = radical,
            structure = structure,
            mnemonicOrNote = mnemonicOrNote,
            exampleJapanese = exampleJapanese,
            exampleReading = exampleReading,
            exampleRomaji = exampleRomaji,
            exampleEnglish = exampleEnglish,
            examQuestionType = examQuestionType,
            examQuestionPrompt = examQuestionPrompt,
            options = optionsList,
            mastery = mastery,
            reviewCount = reviewCount,
            lastReviewedAt = lastReviewedAt
        )
    }

    companion object {
        fun fromJapaneseItem(item: JapaneseItem, source: String = "OfficialCurriculum"): JlptContentEntity {
            val optionsJson = if (item.options.isNotEmpty()) {
                "[" + item.options.joinToString(",") { "\"${it.replace("\"", "\\\"")}\"" } + "]"
            } else ""

            return JlptContentEntity(
                id = item.id,
                japanese = item.japanese,
                reading = item.reading,
                romaji = item.romaji,
                meaning = item.meaning,
                category = item.category.name,
                jlptLevel = item.jlptLevel,
                onyomi = item.onyomi,
                kunyomi = item.kunyomi,
                strokeCount = item.strokeCount,
                radical = item.radical,
                structure = item.structure,
                mnemonicOrNote = item.mnemonicOrNote,
                exampleJapanese = item.exampleJapanese,
                exampleReading = item.exampleReading,
                exampleRomaji = item.exampleRomaji,
                exampleEnglish = item.exampleEnglish,
                examQuestionType = item.examQuestionType,
                examQuestionPrompt = item.examQuestionPrompt,
                optionsJson = optionsJson,
                mastery = item.mastery,
                reviewCount = item.reviewCount,
                lastReviewedAt = item.lastReviewedAt,
                source = source
            )
        }
    }
}
