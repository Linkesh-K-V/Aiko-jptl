package com.example.noignore.data.jlpt

import androidx.room.Entity

@Entity(
    tableName = "jlpt_category_progress",
    primaryKeys = ["level", "category"]
)
data class JlptProgressEntity(
    val level: String, // "N5", "N4", "N3", "N2", "N1"
    val category: String, // "KANJI", "VOCABULARY", "GRAMMAR", "READING", "MOCK_EXAMS"
    val categoryTitle: String,
    val totalItems: Int,
    val completedItems: Int = 0,
    val reviewedItems: Int = 0,
    val accuracyRate: Int = 0,
    val lastStudiedAt: Long = System.currentTimeMillis()
)
