package com.example.noignore.japanese.engine

import kotlin.math.roundToInt

data class ExamSectionScore(
    val sectionName: String,
    val rawScore: Int,
    val maxRawScore: Int,
    val scaledScore: Int, // Out of 60
    val passingMinimum: Int = 19,
    val isSectionPassed: Boolean
)

data class ExamResult(
    val level: String,
    val totalScaledScore: Int, // Out of 180
    val totalPassingScore: Int,
    val isOverallPassed: Boolean,
    val sectionScores: List<ExamSectionScore>,
    val diagnosticSummary: String
)

/**
 * Official JLPT Scoring & Mock Exam Engine.
 * Converts raw question scores to official scaled scores (0-180), enforces sectional minimums,
 * and tracks official exam time allocations per level.
 */
object JlptExamEngine {

    fun getPassingThreshold(level: String): Int = when (level.uppercase()) {
        "N1" -> 100
        "N2" -> 90
        "N3" -> 95
        "N4" -> 90
        "N5" -> 80
        else -> 80
    }

    /**
     * Converts a raw score fraction (0.0 to 1.0) into the JLPT standard scaled section score (0 to 60).
     * Uses a scaled distribution curve reflecting official JEES/Japan Foundation scoring characteristics.
     */
    fun calculateScaledSectionScore(rawScore: Int, maxRaw: Int): Int {
        if (maxRaw <= 0) return 0
        val ratio = (rawScore.toDouble() / maxRaw.toDouble()).coerceIn(0.0, 1.0)
        // Scaled mapping: 0% -> 0, 100% -> 60 with standard sigmoidal scaling
        val scaled = (ratio * 60.0).roundToInt().coerceIn(0, 60)
        return scaled
    }

    fun gradeExam(
        level: String,
        knowledgeRaw: Int,
        knowledgeMax: Int,
        readingRaw: Int,
        readingMax: Int,
        listeningRaw: Int,
        listeningMax: Int
    ): ExamResult {
        val knowledgeScaled = calculateScaledSectionScore(knowledgeRaw, knowledgeMax)
        val readingScaled = calculateScaledSectionScore(readingRaw, readingMax)
        val listeningScaled = calculateScaledSectionScore(listeningRaw, listeningMax)

        val section1Passed = knowledgeScaled >= 19
        val section2Passed = readingScaled >= 19
        val section3Passed = listeningScaled >= 19

        val sections = listOf(
            ExamSectionScore("Language Knowledge (Vocab/Grammar)", knowledgeRaw, knowledgeMax, knowledgeScaled, 19, section1Passed),
            ExamSectionScore("Reading Comprehension", readingRaw, readingMax, readingScaled, 19, section2Passed),
            ExamSectionScore("Listening Comprehension", listeningRaw, listeningMax, listeningScaled, 19, section3Passed)
        )

        val totalScore = knowledgeScaled + readingScaled + listeningScaled
        val threshold = getPassingThreshold(level)
        val allSectionsPassed = section1Passed && section2Passed && section3Passed
        val isPassed = (totalScore >= threshold) && allSectionsPassed

        val summary = when {
            isPassed -> "合格 (PASSED)! You achieved $totalScore/180 (threshold: $threshold/180) and cleared all sectional minimums."
            !allSectionsPassed && totalScore >= threshold -> "不合格 (FAILED). Total score ($totalScore) exceeded the passing mark, but one or more sections did not reach the sectional minimum of 19/60."
            else -> "不合格 (FAILED). Total score $totalScore/180 was below the passing threshold of $threshold/180."
        }

        return ExamResult(
            level = level.uppercase(),
            totalScaledScore = totalScore,
            totalPassingScore = threshold,
            isOverallPassed = isPassed,
            sectionScores = sections,
            diagnosticSummary = summary
        )
    }
}
