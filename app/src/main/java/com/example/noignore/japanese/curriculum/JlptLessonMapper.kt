package com.example.noignore.japanese.curriculum

import com.example.noignore.data.content.JlptContentEntity
import com.example.noignore.japanese.model.ListeningExercise
import com.example.noignore.japanese.model.ListeningExerciseType
import com.example.noignore.japanese.model.ReadingLesson
import com.example.noignore.japanese.model.ReadingPassageSentence
import com.example.noignore.japanese.model.StoryQuizQuestion
import com.example.noignore.japanese.model.StoryVocab

/**
 * Mapper connecting authoritative Room JlptContentEntity records (Category.READING & Category.LISTENING)
 * to domain UI models for ReadingLessonScreen and ListeningLessonScreen (DEF-12-01).
 */
object JlptLessonMapper {

    fun toReadingLesson(entity: JlptContentEntity): ReadingLesson {
        val lines = entity.japanese.lines().filter { it.isNotBlank() }
        val title = lines.firstOrNull()?.trim() ?: "読解レッスン"

        val readingLines = entity.reading.lines().filter { it.isNotBlank() }
        val romajiLines = entity.romaji.lines().filter { it.isNotBlank() }
        val meaningLines = entity.meaning.lines().filter { it.isNotBlank() }
        val titleEnglish = meaningLines.firstOrNull()?.trim() ?: "Reading Passage"

        val sentences = mutableListOf<ReadingPassageSentence>()

        if (lines.size > 2) {
            // Multi-line notices, announcements, signs, menus
            val contentJap = lines.drop(1)
            val contentRead = readingLines.drop(1)
            val contentRom = romajiLines.drop(1)
            val contentEng = meaningLines.drop(1)

            for (i in contentJap.indices) {
                sentences.add(
                    ReadingPassageSentence(
                        japanese = contentJap[i].trim(),
                        furigana = contentRead.getOrElse(i) { "" }.trim(),
                        romaji = contentRom.getOrElse(i) { "" }.trim(),
                        english = contentEng.getOrElse(i) { "" }.trim()
                    )
                )
            }
        } else {
            // Narrative passages: split by sentence delimiter '。'
            val passageJap = if (lines.size > 1) lines[1] else lines[0]
            val passageRead = if (readingLines.size > 1) readingLines[1] else readingLines.getOrElse(0) { "" }
            val passageRom = if (romajiLines.size > 1) romajiLines[1] else romajiLines.getOrElse(0) { "" }
            val passageEng = if (meaningLines.size > 1) meaningLines[1] else meaningLines.getOrElse(0) { "" }

            val rawSentsJap = passageJap.split("。").map { it.trim() }.filter { it.isNotBlank() }
            val rawSentsRead = passageRead.split("。").map { it.trim() }.filter { it.isNotBlank() }
            val rawSentsRom = passageRom.split(". ").map { it.trim() }.filter { it.isNotBlank() }
            val rawSentsEng = passageEng.split(". ").map { it.trim() }.filter { it.isNotBlank() }

            if (rawSentsJap.isNotEmpty()) {
                for (i in rawSentsJap.indices) {
                    val jText = rawSentsJap[i] + "。"
                    val rText = if (i < rawSentsRead.size) rawSentsRead[i] + "。" else ""
                    val roText = if (i < rawSentsRom.size) rawSentsRom[i].trimEnd('.') + "." else ""
                    val eText = if (i < rawSentsEng.size) rawSentsEng[i].trimEnd('.') + "." else ""

                    sentences.add(
                        ReadingPassageSentence(
                            japanese = jText,
                            furigana = rText,
                            romaji = roText,
                            english = eText
                        )
                    )
                }
            } else {
                sentences.add(
                    ReadingPassageSentence(
                        japanese = passageJap,
                        furigana = passageRead,
                        romaji = passageRom,
                        english = passageEng
                    )
                )
            }
        }

        val genre = when {
            entity.tags.contains("diary", ignoreCase = true) -> "Daily Diary"
            entity.tags.contains("notice", ignoreCase = true) || entity.tags.contains("sign", ignoreCase = true) -> "Notice / Sign"
            entity.tags.contains("email", ignoreCase = true) || entity.tags.contains("mail", ignoreCase = true) -> "Email Note"
            entity.tags.contains("recipe", ignoreCase = true) -> "Recipe"
            entity.tags.contains("shopping", ignoreCase = true) -> "Shopping Memo"
            entity.tags.contains("dialogue", ignoreCase = true) -> "Dialogue"
            else -> "Short Article"
        }

        val emoji = when (genre) {
            "Daily Diary" -> "📔"
            "Notice / Sign" -> "📋"
            "Email Note" -> "✉️"
            "Recipe" -> "🍳"
            "Shopping Memo" -> "🛒"
            "Dialogue" -> "💬"
            else -> "📖"
        }

        val optionsList = entity.toJapaneseItem().options

        val questions = if (entity.examQuestionPrompt.isNotBlank() && optionsList.isNotEmpty()) {
            listOf(
                StoryQuizQuestion(
                    id = "q_${entity.id}",
                    question = entity.examQuestionPrompt,
                    options = optionsList,
                    correctOptionIndex = 0,
                    explanation = entity.mnemonicOrNote.ifBlank { "Passage reference: $title" }
                )
            )
        } else emptyList()

        return ReadingLesson(
            id = entity.id,
            title = title,
            titleEnglish = titleEnglish,
            jlptLevel = entity.jlptLevel,
            genre = genre,
            emoji = emoji,
            sentences = sentences,
            grammarNote = entity.mnemonicOrNote.ifBlank { "Notice particle connections and context flow in this passage." },
            keyVocabulary = emptyList(),
            questions = questions
        )
    }

    fun toListeningExercise(entity: JlptContentEntity): ListeningExercise {
        val optionsList = entity.toJapaneseItem().options

        val type = when {
            entity.tags.contains("dialogue", ignoreCase = true) -> ListeningExerciseType.DIALOGUE
            entity.tags.contains("script", ignoreCase = true) || entity.tags.contains("kanji", ignoreCase = true) -> ListeningExerciseType.SCRIPT_RECOGNITION
            entity.tags.contains("particle", ignoreCase = true) -> ListeningExerciseType.PARTICLE_MATCH
            else -> ListeningExerciseType.COMPREHENSION
        }

        return ListeningExercise(
            id = entity.id,
            jlptLevel = entity.jlptLevel,
            audioText = entity.japanese,
            type = type,
            questionPrompt = entity.examQuestionPrompt.ifBlank { "Listen to the dialogue and answer the question." },
            speakerRole = if (entity.tags.contains("dialogue", ignoreCase = true)) "Conversation" else "Sensei Aiko",
            options = optionsList,
            correctOptionIndex = 0,
            furiganaTranscript = entity.reading,
            romaji = entity.romaji,
            englishTranslation = entity.meaning,
            explanation = entity.mnemonicOrNote.ifBlank { "Listen carefully for the key words mentioned in the audio dialogue." }
        )
    }
}
