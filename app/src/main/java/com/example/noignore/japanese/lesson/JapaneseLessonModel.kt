package com.example.noignore.japanese.lesson

import com.example.noignore.japanese.model.JapaneseCategory
import com.example.noignore.japanese.model.JapaneseItem

/**
 * Represents a structured curriculum lesson with curated Japanese items,
 * recap questions from prerequisites, and on-the-spot reinforcement queue.
 */
data class Lesson(
    val id: Int,
    val lessonNumber: Int,
    val title: String,
    val japaneseTitle: String,
    val level: String, // "JLPT N5", "JLPT N4", etc.
    val description: String,
    val iconEmoji: String,
    val coreItems: List<JapaneseItem>
)

data class RecapQuestion(
    val id: String,
    val targetItem: JapaneseItem,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class RecapResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val failedItems: List<JapaneseItem>
)
