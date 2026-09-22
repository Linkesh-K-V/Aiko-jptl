package com.example.noignore.data.jlpt

import com.example.noignore.japanese.model.JlptExamLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class JlptProgressRepository(private val dao: JlptProgressDao) {

    val allProgress: Flow<List<JlptProgressEntity>> = dao.getAllCategoryProgress()
    val targetExam: Flow<JlptExamTargetEntity?> = dao.getTargetExam()

    fun getProgressForLevel(level: String): Flow<List<JlptProgressEntity>> =
        dao.getProgressForLevel(level)

    suspend fun setTargetExamLevel(level: String) = withContext(Dispatchers.IO) {
        val existing = dao.getTargetExamSync()
        if (existing == null) {
            dao.setTargetExam(
                JlptExamTargetEntity(
                    id = 1,
                    targetLevel = level,
                    dailyGoalMinutes = 30,
                    updatedAt = System.currentTimeMillis()
                )
            )
        } else {
            dao.updateTargetLevel(level)
        }
    }

    suspend fun updateTargetExam(target: JlptExamTargetEntity) = withContext(Dispatchers.IO) {
        dao.setTargetExam(target)
    }

    suspend fun updateCompletedCount(level: String, category: String, count: Int) = withContext(Dispatchers.IO) {
        val current = dao.getProgress(level, category)
        val validCount = if (current != null) {
            count.coerceIn(0, current.totalItems)
        } else {
            count.coerceAtLeast(0)
        }
        dao.updateCompletedCount(level, category, validCount)
    }

    suspend fun incrementCompletedCount(level: String, category: String, delta: Int = 1) = withContext(Dispatchers.IO) {
        val current = dao.getProgress(level, category)
        if (current != null) {
            val newCount = (current.completedItems + delta).coerceIn(0, current.totalItems)
            dao.updateCompletedCount(level, category, newCount)
        }
    }

    suspend fun initializeDefaultsIfEmpty() = withContext(Dispatchers.IO) {
        if (dao.countCategories() == 0) {
            val initialList = mutableListOf<JlptProgressEntity>()

            // Generate initial progress categories for N5 through N1
            JlptExamLevel.entries.forEach { lvl ->
                val (readingCount, mockCount) = when (lvl) {
                    JlptExamLevel.N5 -> 25 to 5
                    JlptExamLevel.N4 -> 40 to 5
                    JlptExamLevel.N3 -> 60 to 6
                    JlptExamLevel.N2 -> 80 to 8
                    JlptExamLevel.N1 -> 100 to 10
                }

                // Initial sample progress so learners see lively state
                val initialKanjiDone = when (lvl) {
                    JlptExamLevel.N5 -> 24
                    JlptExamLevel.N4 -> 10
                    else -> 0
                }
                val initialVocabDone = when (lvl) {
                    JlptExamLevel.N5 -> 120
                    JlptExamLevel.N4 -> 25
                    else -> 0
                }
                val initialGrammarDone = when (lvl) {
                    JlptExamLevel.N5 -> 18
                    JlptExamLevel.N4 -> 5
                    else -> 0
                }
                val initialReadingDone = when (lvl) {
                    JlptExamLevel.N5 -> 4
                    else -> 0
                }
                val initialMockDone = when (lvl) {
                    JlptExamLevel.N5 -> 1
                    else -> 0
                }

                initialList.add(
                    JlptProgressEntity(
                        level = lvl.code,
                        category = "KANJI",
                        categoryTitle = "Kanji (漢字)",
                        totalItems = lvl.kanjiCount,
                        completedItems = initialKanjiDone,
                        reviewedItems = initialKanjiDone * 2,
                        accuracyRate = if (initialKanjiDone > 0) 88 else 0
                    )
                )
                initialList.add(
                    JlptProgressEntity(
                        level = lvl.code,
                        category = "VOCABULARY",
                        categoryTitle = "Vocabulary (語彙)",
                        totalItems = lvl.vocabCount,
                        completedItems = initialVocabDone,
                        reviewedItems = initialVocabDone * 2,
                        accuracyRate = if (initialVocabDone > 0) 85 else 0
                    )
                )
                initialList.add(
                    JlptProgressEntity(
                        level = lvl.code,
                        category = "GRAMMAR",
                        categoryTitle = "Grammar (文法)",
                        totalItems = lvl.grammarPoints,
                        completedItems = initialGrammarDone,
                        reviewedItems = initialGrammarDone * 2,
                        accuracyRate = if (initialGrammarDone > 0) 90 else 0
                    )
                )
                initialList.add(
                    JlptProgressEntity(
                        level = lvl.code,
                        category = "READING",
                        categoryTitle = "Reading (読解)",
                        totalItems = readingCount,
                        completedItems = initialReadingDone,
                        reviewedItems = initialReadingDone,
                        accuracyRate = if (initialReadingDone > 0) 80 else 0
                    )
                )
                initialList.add(
                    JlptProgressEntity(
                        level = lvl.code,
                        category = "MOCK_EXAMS",
                        categoryTitle = "Mock Exams (模擬試験)",
                        totalItems = mockCount,
                        completedItems = initialMockDone,
                        reviewedItems = initialMockDone,
                        accuracyRate = if (initialMockDone > 0) 92 else 0
                    )
                )
            }

            dao.insertAll(initialList)

            if (dao.getTargetExamSync() == null) {
                dao.setTargetExam(
                    JlptExamTargetEntity(
                        id = 1,
                        targetLevel = "N5",
                        dailyGoalMinutes = 30,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }
}
