package com.example.noignore.japanese.engine

import com.example.noignore.data.AppDatabase
import com.example.noignore.data.learner.MistakeRecordEntity
import com.example.noignore.japanese.model.JapaneseItem

enum class MistakeCategory(val label: String, val diagnosticHint: String) {
    PARTICLE_CONFUSION("Particle Confusion", "Focus on the functional case role (target vs location vs topic)."),
    KANJI_CONFUSION("Kanji Visual Confusion", "Analyze radical components to distinguish visually similar characters."),
    READING_LAPSE("Onyomi vs Kunyomi Lapse", "Single kanji usually use Kunyomi; multi-kanji compounds usually use Onyomi."),
    VOCAB_MEANING("Meaning/Collocation Misunderstanding", "Observe natural collocations and example context."),
    GRAMMAR_NUANCE("Grammar Pattern Nuance", "Verify verb conjugation requirements and speaker intent."),
    LISTENING_DISTORTION("Acoustic Distortion", "Focus on subtle consonant distinction and pitch shifts.")
}

data class LeechIntervention(
    val cardId: String,
    val targetWord: String,
    val mistakeCategory: MistakeCategory,
    val explanation: String,
    val contrastHint: String,
    val clozePrompt: String,
    val suggestedAction: String
)

/**
 * Intelligent Pedagogical Leech & Weakness Intervention Engine.
 * Detects persistently failing items (leeches) and generates structured diagnostic advice,
 * minimal contrast pairs, and radical deconstructions.
 */
object LeechInterventionEngine {

    const val LEECH_THRESHOLD_LAPSES = 4

    suspend fun recordMistake(
        db: AppDatabase,
        item: JapaneseItem,
        mistakeType: MistakeCategory
    ) {
        val mistakeDao = db.mistakeRecordDao()
        val existing = mistakeDao.getMistakeByCardId(item.id)

        val lapses = (existing?.lapseCount ?: 0) + 1
        val attempts = (existing?.totalAttempts ?: 0) + 1
        val isLeech = lapses >= LEECH_THRESHOLD_LAPSES

        val interventionNote = if (isLeech) {
            "Persistently failing card ($lapses lapses). Needs radical/cloze intervention."
        } else {
            existing?.interventionNotes ?: ""
        }

        val record = MistakeRecordEntity(
            cardId = item.id,
            jlptLevel = item.jlptLevel,
            category = item.category.name,
            lapseCount = lapses,
            totalAttempts = attempts,
            mistakeType = mistakeType.name,
            lastMistakeMs = System.currentTimeMillis(),
            isLeech = isLeech,
            interventionNotes = interventionNote
        )

        mistakeDao.insertOrUpdate(record)
    }

    fun generateIntervention(item: JapaneseItem, record: MistakeRecordEntity): LeechIntervention {
        val category = try {
            MistakeCategory.valueOf(record.mistakeType)
        } catch (_: Exception) {
            MistakeCategory.READING_LAPSE
        }

        val contrastHint = when (category) {
            MistakeCategory.PARTICLE_CONFUSION -> "Pay close attention to transitive marker 'を' vs intransitive 'が', or destination 'に' vs action place 'で'."
            MistakeCategory.KANJI_CONFUSION -> "Examine radical parts: '${item.radical}'. Stroke count: ${item.strokeCount}."
            MistakeCategory.READING_LAPSE -> "Remember: Kunyomi is '${item.kunyomi}' and Onyomi is '${item.onyomi}'."
            MistakeCategory.VOCAB_MEANING -> "Meaning: '${item.meaning}'. Context: '${item.exampleJapanese}'."
            MistakeCategory.GRAMMAR_NUANCE -> "Grammar note: ${item.mnemonicOrNote.ifBlank { "Check preceding verb form." }}"
            MistakeCategory.LISTENING_DISTORTION -> "Listen closely to pitch: ${PitchAccentEngine.analyzePitchAccent(item.japanese, item.reading).pattern.labelEn}."
        }

        val clozePrompt = if (item.exampleJapanese.contains(item.japanese)) {
            item.exampleJapanese.replace(item.japanese, "[＿＿＿＿]")
        } else {
            "${item.reading} [＿＿＿＿]"
        }

        return LeechIntervention(
            cardId = item.id,
            targetWord = item.japanese,
            mistakeCategory = category,
            explanation = "This item has lapsed ${record.lapseCount} times. Aiko recommends reviewing its components.",
            contrastHint = contrastHint,
            clozePrompt = clozePrompt,
            suggestedAction = "Practice in 5-minute Focus Mode until retention stabilizes."
        )
    }
}
