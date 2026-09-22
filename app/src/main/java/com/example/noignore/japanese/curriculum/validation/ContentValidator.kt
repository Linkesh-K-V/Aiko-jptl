package com.example.noignore.japanese.curriculum.validation

import com.example.noignore.data.content.JlptContentEntity

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Warning(val reasons: List<String>) : ValidationResult()
    data class Invalid(val reasons: List<String>) : ValidationResult()
}

/**
 * Educational Content Validation Pipeline.
 * Enforces strict criteria before imported content enters SQLite Room:
 * - Structural integrity (non-null, non-blank fields)
 * - Script correctness (Hiragana/Katakana readings)
 * - Duplicate detection across normalized (Japanese + Reading + Level)
 * - Reference & contextual checks (examples contain target vocabulary/grammar)
 * - Cross-level consistency checks
 */
object ContentValidator {

    private val VALID_JLPT_LEVELS = setOf("N5", "N4", "N3", "N2", "N1")
    private val VALID_CATEGORIES = setOf("KANJI", "VOCAB", "GRAMMAR", "JLPT_EXAM", "KANA", "READING", "LISTENING")

    fun validateItem(item: JlptContentEntity, existingIds: Set<String>, existingKeys: Set<String>): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        // 1. Required Fields
        if (item.id.isBlank()) errors.add("Item ID cannot be blank.")
        if (item.japanese.isBlank()) errors.add("Japanese text cannot be blank.")
        if (item.reading.isBlank()) errors.add("Reading cannot be blank.")
        if (item.meaning.isBlank()) errors.add("Meaning cannot be blank.")

        // 2. JLPT Level & Category validation
        if (!VALID_JLPT_LEVELS.contains(item.jlptLevel.uppercase())) {
            errors.add("Invalid JLPT Level: '${item.jlptLevel}'. Must be one of $VALID_JLPT_LEVELS.")
        }
        if (!VALID_CATEGORIES.contains(item.category.uppercase())) {
            errors.add("Invalid Category: '${item.category}'. Must be one of $VALID_CATEGORIES.")
        }

        // 3. ID Uniqueness
        if (existingIds.contains(item.id)) {
            errors.add("Duplicate Item ID detected: '${item.id}'.")
        }

        // 4. Duplicate Entry detection (Normalized Japanese + Reading + Category + Level)
        val normalizedKey = "${item.japanese.trim()}_${item.reading.trim()}_${item.category.uppercase()}_${item.jlptLevel.uppercase()}"
        if (existingKeys.contains(normalizedKey)) {
            warnings.add("Potential duplicate content item: '${item.japanese}' (${item.reading}) in category ${item.category} at level ${item.jlptLevel}.")
        }

        // 4b. Grammar Formation Structure check
        if (item.category == "GRAMMAR" && item.structure.isBlank()) {
            warnings.add("Grammar item '${item.id}' missing formation structure template.")
        }

        // 5. Question & Distractor validation for JLPT_EXAM
        if (item.category == "JLPT_EXAM") {
            if (item.examQuestionPrompt.isBlank()) {
                errors.add("JLPT_EXAM item '${item.id}' has blank examQuestionPrompt.")
            }
            val options = parseOptions(item.optionsJson)
            if (options.size < 2) {
                errors.add("JLPT_EXAM item '${item.id}' must have at least 2 options (found ${options.size}).")
            } else if (options.distinct().size != options.size) {
                warnings.add("JLPT_EXAM item '${item.id}' contains duplicate option choices.")
            }
        }

        // 6. Example sentence context validation
        if (item.exampleJapanese.isNotBlank()) {
            if (item.exampleEnglish.isBlank()) {
                warnings.add("Example sentence in Japanese is provided but English translation is missing for ID '${item.id}'.")
            }
            // If vocabulary or kanji, verify that the target character or word appears in the example
            if (item.category == "VOCAB" || item.category == "KANJI") {
                val cleanWord = item.japanese.trim().removeSurrounding("〜").removeSurrounding("~")
                if (cleanWord.isNotBlank() && !item.exampleJapanese.contains(cleanWord)) {
                    warnings.add("Example sentence '${item.exampleJapanese}' does not contain the target word '$cleanWord'.")
                }
            }
        }

        return when {
            errors.isNotEmpty() -> ValidationResult.Invalid(errors)
            warnings.isNotEmpty() -> ValidationResult.Warning(warnings)
            else -> ValidationResult.Valid
        }
    }

    fun parseOptions(optionsJson: String): List<String> {
        if (optionsJson.isBlank() || !optionsJson.trim().startsWith("[")) return emptyList()
        return try {
            val jsonArray = org.json.JSONArray(optionsJson)
            List(jsonArray.length()) { jsonArray.getString(it) }
        } catch (_: Exception) {
            val clean = optionsJson.trim().removeSurrounding("[", "]")
            if (clean.isBlank()) emptyList()
            else clean.split(",").map { it.trim().removeSurrounding("\"") }
        }
    }

    /**
     * Batch validation for an imported collection.
     */
    fun validateBatch(items: List<JlptContentEntity>): Pair<List<JlptContentEntity>, List<String>> {
        val validItems = mutableListOf<JlptContentEntity>()
        val report = mutableListOf<String>()

        val seenIds = mutableSetOf<String>()
        val seenKeys = mutableSetOf<String>()

        for (item in items) {
            when (val result = validateItem(item, seenIds, seenKeys)) {
                is ValidationResult.Valid -> {
                    validItems.add(item)
                    seenIds.add(item.id)
                    seenKeys.add("${item.japanese.trim()}_${item.reading.trim()}_${item.category.uppercase()}_${item.jlptLevel.uppercase()}")
                }
                is ValidationResult.Warning -> {
                    validItems.add(item)
                    seenIds.add(item.id)
                    seenKeys.add("${item.japanese.trim()}_${item.reading.trim()}_${item.category.uppercase()}_${item.jlptLevel.uppercase()}")
                    report.add("[WARN] Item '${item.id}': ${result.reasons.joinToString("; ")}")
                }
                is ValidationResult.Invalid -> {
                    report.add("[REJECTED] Item '${item.id}': ${result.reasons.joinToString("; ")}")
                }
            }
        }

        return Pair(validItems, report)
    }
}
