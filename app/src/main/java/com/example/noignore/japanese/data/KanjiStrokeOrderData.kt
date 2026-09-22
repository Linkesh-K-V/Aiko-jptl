package com.example.noignore.japanese.data

import androidx.compose.ui.geometry.Offset

/**
 * Normalized stroke path segment (relative coordinates 0.0f..1.0f).
 */
data class NormalizedStroke(
    val strokeIndex: Int,
    val description: String,
    val start: Offset,
    val end: Offset,
    val controlPoint: Offset? = null
)

/**
 * Stroke order dictionary providing normalized vector paths for common JLPT Kanji.
 * If a Kanji isn't explicitly defined, automatic heuristic stroke breakdown provides smooth fallback.
 */
object KanjiStrokeOrderData {

    private val strokeMap = mapOf(
        "一" to listOf(
            NormalizedStroke(1, "Horizontal stroke left to right", Offset(0.2f, 0.5f), Offset(0.8f, 0.5f))
        ),
        "二" to listOf(
            NormalizedStroke(1, "Top horizontal stroke", Offset(0.3f, 0.35f), Offset(0.7f, 0.35f)),
            NormalizedStroke(2, "Bottom horizontal stroke (wider)", Offset(0.2f, 0.68f), Offset(0.8f, 0.68f))
        ),
        "三" to listOf(
            NormalizedStroke(1, "Top horizontal stroke", Offset(0.3f, 0.28f), Offset(0.7f, 0.28f)),
            NormalizedStroke(2, "Middle horizontal stroke", Offset(0.35f, 0.48f), Offset(0.65f, 0.48f)),
            NormalizedStroke(3, "Bottom long horizontal stroke", Offset(0.18f, 0.72f), Offset(0.82f, 0.72f))
        ),
        "人" to listOf(
            NormalizedStroke(1, "Left sweeping stroke downwards", Offset(0.5f, 0.2f), Offset(0.25f, 0.85f), Offset(0.4f, 0.5f)),
            NormalizedStroke(2, "Right slant starting from middle of first stroke", Offset(0.42f, 0.45f), Offset(0.78f, 0.85f))
        ),
        "日" to listOf(
            NormalizedStroke(1, "Left vertical line downwards", Offset(0.28f, 0.22f), Offset(0.28f, 0.78f)),
            NormalizedStroke(2, "Top horizontal and right vertical corner", Offset(0.28f, 0.22f), Offset(0.72f, 0.78f), Offset(0.72f, 0.22f)),
            NormalizedStroke(3, "Middle inner horizontal bar", Offset(0.28f, 0.50f), Offset(0.72f, 0.50f)),
            NormalizedStroke(4, "Bottom closing horizontal bar", Offset(0.28f, 0.78f), Offset(0.72f, 0.78f))
        ),
        "月" to listOf(
            NormalizedStroke(1, "Left downward curving stroke", Offset(0.32f, 0.20f), Offset(0.25f, 0.82f)),
            NormalizedStroke(2, "Top horizontal line and right downward line with hook", Offset(0.32f, 0.20f), Offset(0.72f, 0.82f), Offset(0.72f, 0.20f)),
            NormalizedStroke(3, "Upper horizontal interior bar", Offset(0.32f, 0.42f), Offset(0.70f, 0.42f)),
            NormalizedStroke(4, "Lower horizontal interior bar", Offset(0.30f, 0.60f), Offset(0.68f, 0.60f))
        ),
        "木" to listOf(
            NormalizedStroke(1, "Horizontal cross bar", Offset(0.22f, 0.42f), Offset(0.78f, 0.42f)),
            NormalizedStroke(2, "Vertical spine with slight hook", Offset(0.50f, 0.18f), Offset(0.50f, 0.85f)),
            NormalizedStroke(3, "Left diagonal branch", Offset(0.50f, 0.42f), Offset(0.22f, 0.82f)),
            NormalizedStroke(4, "Right diagonal branch", Offset(0.50f, 0.42f), Offset(0.78f, 0.82f))
        ),
        "水" to listOf(
            NormalizedStroke(1, "Center vertical stroke with bottom left hook", Offset(0.50f, 0.18f), Offset(0.50f, 0.82f)),
            NormalizedStroke(2, "Left upper diagonal", Offset(0.48f, 0.38f), Offset(0.22f, 0.48f)),
            NormalizedStroke(3, "Left lower splash", Offset(0.20f, 0.82f), Offset(0.45f, 0.60f)),
            NormalizedStroke(4, "Right falling splash", Offset(0.55f, 0.40f), Offset(0.82f, 0.82f))
        ),
        "火" to listOf(
            NormalizedStroke(1, "Left flame dot", Offset(0.28f, 0.42f), Offset(0.35f, 0.58f)),
            NormalizedStroke(2, "Right flame dot", Offset(0.72f, 0.40f), Offset(0.65f, 0.55f)),
            NormalizedStroke(3, "Center left sweeping flame", Offset(0.50f, 0.20f), Offset(0.25f, 0.85f)),
            NormalizedStroke(4, "Center right sweeping flame", Offset(0.50f, 0.45f), Offset(0.78f, 0.85f))
        ),
        "土" to listOf(
            NormalizedStroke(1, "Upper horizontal stroke", Offset(0.30f, 0.42f), Offset(0.70f, 0.42f)),
            NormalizedStroke(2, "Center vertical pillar", Offset(0.50f, 0.20f), Offset(0.50f, 0.78f)),
            NormalizedStroke(3, "Long base horizontal stroke", Offset(0.18f, 0.78f), Offset(0.82f, 0.78f))
        ),
        "金" to listOf(
            NormalizedStroke(1, "Roof left slant", Offset(0.50f, 0.15f), Offset(0.22f, 0.42f)),
            NormalizedStroke(2, "Roof right slant", Offset(0.50f, 0.15f), Offset(0.78f, 0.42f)),
            NormalizedStroke(3, "Upper horizontal bar", Offset(0.32f, 0.46f), Offset(0.68f, 0.46f)),
            NormalizedStroke(4, "Middle vertical stem", Offset(0.50f, 0.46f), Offset(0.50f, 0.78f)),
            NormalizedStroke(5, "Left gold nugget dot", Offset(0.34f, 0.58f), Offset(0.42f, 0.66f)),
            NormalizedStroke(6, "Right gold nugget dot", Offset(0.66f, 0.58f), Offset(0.58f, 0.66f)),
            NormalizedStroke(7, "Lower horizontal bar", Offset(0.35f, 0.70f), Offset(0.65f, 0.70f)),
            NormalizedStroke(8, "Base wide foundation bar", Offset(0.18f, 0.82f), Offset(0.82f, 0.82f))
        ),
        "学" to listOf(
            NormalizedStroke(1, "Left roof dot", Offset(0.30f, 0.15f), Offset(0.35f, 0.25f)),
            NormalizedStroke(2, "Center roof dot", Offset(0.50f, 0.12f), Offset(0.50f, 0.22f)),
            NormalizedStroke(3, "Right roof stroke", Offset(0.70f, 0.15f), Offset(0.65f, 0.25f)),
            NormalizedStroke(4, "Crown left vertical", Offset(0.25f, 0.32f), Offset(0.28f, 0.42f)),
            NormalizedStroke(5, "Crown right corner", Offset(0.28f, 0.32f), Offset(0.75f, 0.42f), Offset(0.75f, 0.32f)),
            NormalizedStroke(6, "Child head horizontal and diagonal", Offset(0.35f, 0.52f), Offset(0.42f, 0.68f)),
            NormalizedStroke(7, "Child curved spine with hook", Offset(0.50f, 0.52f), Offset(0.52f, 0.90f)),
            NormalizedStroke(8, "Child horizontal arm bar", Offset(0.22f, 0.68f), Offset(0.78f, 0.68f))
        )
    )

    fun getStrokes(kanji: String, fallbackStrokeCount: Int = 4): List<NormalizedStroke> {
        val predefined = strokeMap[kanji]
        if (predefined != null) return predefined

        // Generate dynamic fallback guides evenly spaced across the canvas
        val count = fallbackStrokeCount.coerceIn(1, 12)
        return (1..count).map { idx ->
            val ratio = idx.toFloat() / (count + 1)
            NormalizedStroke(
                strokeIndex = idx,
                description = "Stroke $idx",
                start = Offset(0.25f, ratio),
                end = Offset(0.75f, ratio)
            )
        }
    }
}
