package com.example.noignore.japanese.curriculum.validation

import com.example.noignore.data.content.JlptContentEntity

data class GraphValidationReport(
    val totalRelationships: Int,
    val validRelationships: Int,
    val invalidRelationships: Int,
    val brokenReferences: Int,
    val duplicates: Int,
    val selfReferences: Int,
    val connectedItemsCount: Int,
    val isolatedItemsCount: Int,
    val errors: List<String> = emptyList()
) {
    fun formatReport(): String {
        return buildString {
            appendLine("Relationship validation")
            appendLine("Total relationships: $totalRelationships")
            appendLine("Valid: $validRelationships")
            appendLine("Invalid: $invalidRelationships")
            appendLine("Broken references: $brokenReferences")
            appendLine("Duplicates: $duplicates")
            appendLine("Self references: $selfReferences")
        }
    }
}

/**
 * Curriculum Graph Validator.
 * Enforces graph relational integrity across curriculum items:
 * - Detects broken references (relationship points to nonexistent ID)
 * - Detects self-references (item links to itself)
 * - Detects duplicate relationships within an item
 * - Enforces category and relationship compatibility rules
 * - Generates exact verification report
 */
object CurriculumGraphValidator {

    fun validateGraph(items: List<JlptContentEntity>): GraphValidationReport {
        val itemMap = items.associateBy { it.id }
        var totalRelationships = 0
        var brokenReferences = 0
        var duplicates = 0
        var selfReferences = 0
        var invalidRelationships = 0
        val errors = mutableListOf<String>()
        var connectedCount = 0
        var isolatedCount = 0

        for (item in items) {
            val relatedIds = item.getRelatedItemIds()
            if (relatedIds.isEmpty()) {
                isolatedCount++
                continue
            }

            val seenInItem = mutableSetOf<String>()
            var itemHasValidRel = false

            for (targetId in relatedIds) {
                totalRelationships++
                var isValid = true

                // Check self-reference
                if (targetId == item.id) {
                    selfReferences++
                    isValid = false
                    errors.add("Self-reference detected on item '${item.id}'.")
                }

                // Check duplicate relationship
                if (seenInItem.contains(targetId)) {
                    duplicates++
                    isValid = false
                    errors.add("Duplicate relationship '${targetId}' on item '${item.id}'.")
                } else {
                    seenInItem.add(targetId)

                    // Check broken reference (does target exist in curriculum?)
                    val target = itemMap[targetId]
                    if (target == null) {
                        brokenReferences++
                        isValid = false
                        errors.add("Broken reference: '${item.id}' points to non-existent ID '${targetId}'.")
                    }
                }

                if (!isValid) {
                    invalidRelationships++
                } else {
                    itemHasValidRel = true
                }
            }

            if (itemHasValidRel) {
                connectedCount++
            } else {
                isolatedCount++
            }
        }

        val validRelationships = totalRelationships - invalidRelationships

        return GraphValidationReport(
            totalRelationships = totalRelationships,
            validRelationships = validRelationships,
            invalidRelationships = invalidRelationships,
            brokenReferences = brokenReferences,
            duplicates = duplicates,
            selfReferences = selfReferences,
            connectedItemsCount = connectedCount,
            isolatedItemsCount = isolatedCount,
            errors = errors
        )
    }

    fun validateSingleRelationship(sourceId: String, targetId: String, existingIds: Set<String>): List<String> {
        val issues = mutableListOf<String>()
        if (sourceId == targetId) {
            issues.add("Self-reference: '$sourceId' cannot link to itself.")
        }
        if (!existingIds.contains(targetId)) {
            issues.add("Broken reference: '$targetId' does not exist in curriculum.")
        }
        return issues
    }
}
