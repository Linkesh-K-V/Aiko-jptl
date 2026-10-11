package com.example.noignore.japanese.curriculum.canonical

import com.example.noignore.data.srs.FlashcardSrsEntity
import kotlin.math.max

/**
 * Single source of truth for canonical curriculum identifiers and duplicate reconciliation.
 * Reconciles the 71 Phase 4 vocabulary duplicate records (ID >= 98) back to the canonical
 * baseline records (ID <= 97) while safely preserving SRS review histories and learner models.
 */
object CanonicalCurriculumIds {

    /**
     * Verified mapping from legacy prototype identifiers (curric_*, k_*, v_*)
     * to authoritative JLPT N5 curriculum identifiers (n5_k_*, n5_v_*).
     * Every mapping represents exact identical Japanese character/word content.
     */
    val DUPLICATE_TO_CANONICAL_MAP: Map<String, String> = mapOf(
        // Legacy Deck Kanji (k_*)
        "k_1" to "n5_k_15",   // 日 (Sun, Day)
        "k_2" to "n5_k_16",   // 月 (Moon, Month)
        "k_3" to "n5_k_18",   // 水 (Water)
        "k_4" to "n5_k_17",   // 火 (Fire)
        "k_5" to "n5_k_19",   // 木 (Tree, Wood)
        "k_6" to "n5_k_20",   // 金 (Gold, Money)
        "k_7" to "n5_k_21",   // 土 (Earth, Soil)
        "k_8" to "n5_k_27",   // 本 (Book, Origin)
        "k_9" to "n5_k_23",   // 人 (Person)
        "k_10" to "n5_k_28",  // 学 (Study, Learn)
        "k_11" to "n5_k_29",  // 生 (Life, Birth)
        "k_12" to "n5_k_30",  // 先 (Previous, Ahead)
        "k_13" to "n5_k_59",  // 食 (Eat, Food)
        "k_14" to "n5_k_60",  // 飲 (Drink)
        "k_15" to "n5_k_57",  // 行 (Go, Act)

        // Legacy Deck Vocabulary (v_*)
        "v_3" to "n5_v_41",   // 毎日 (Every day)
        "v_4" to "n5_v_15",   // 友達 (Friend)
        "v_6" to "n5_v_133",  // 時間 (Time, Hours)

        // Legacy Lesson Curriculum Items (curric_*)
        "curric_1_1" to "n5_k_15",  // 日 (Sun, Day)
        "curric_1_2" to "n5_k_16",  // 月 (Moon, Month)
        "curric_1_3" to "n5_k_19",  // 木 (Tree, Wood)
        "curric_1_4" to "n5_v_89",  // おはようございます (Good morning)
        "curric_2_1" to "n5_k_18",  // 水 (Water)
        "curric_2_2" to "n5_k_17",  // 火 (Fire)
        "curric_2_3" to "n5_v_01",  // 食べる (To eat)
        "curric_2_4" to "n5_v_02",  // 飲む (To drink)
        "curric_2_5" to "n5_v_03",  // 行く (To go)
        "curric_3_3" to "n5_v_38",  // 今日 (Today)
        "curric_3_4" to "n5_v_39",  // 明日 (Tomorrow)
        "curric_4_1" to "n5_k_84",  // 何 (What)
        "curric_4_2" to "n5_v_265", // どこ (Where)
        "curric_4_3" to "n5_k_76",  // 駅 (Station)
        "curric_5_1" to "n5_k_23",  // 人 (Person)
        "curric_6_1" to "n5_k_80",  // 時 (Time, Hour)
        "curric_6_2" to "n5_k_81",  // 分 (Minute, Part)
        "curric_6_3" to "n5_k_83",  // 今 (Now)
        "curric_7_1" to "n5_v_27",  // ご飯 (Meal, Cooked rice)
        "curric_7_3" to "n5_v_33",  // お茶 (Green tea)
        "n5_v_212" to "n5_v_03",    // 行く (To go) duplicate reconciliation

        // Legacy N4 Prototype Grammar Duplicates reconciled to Certified N5 Baseline
        "n4_g_01" to "n5_g_18", // 〜たことがある (Duplicate of N5 experience)
        "n4_g_02" to "n5_g_37", // 〜すぎる (Duplicate of N5 excess)
        "n4_g_04" to "n5_g_37"  // 〜すぎる (Duplicate of N5 excess)
        // NOTE: Canonical N4 Kanji IDs (n4_k_04=員, n4_k_07=園, n4_k_10=都) must NEVER be rewritten
        // to N5 IDs as they are legitimate, canonical Phase 18+ N4 Kanji entities.
    )

    /**
     * Canonicalizes an ID, returning the canonical ID if it was a known duplicate.
     */
    fun canonicalize(id: String): String {
        return DUPLICATE_TO_CANONICAL_MAP[id] ?: id
    }

    /**
     * Returns true if the ID is a known duplicate record.
     */
    fun isDuplicate(id: String): Boolean {
        return DUPLICATE_TO_CANONICAL_MAP.containsKey(id)
    }

    /**
     * Consolidates two SRS states into a unified entity, preserving maximum review history,
     * repetitions, interval, and lapses.
     */
    fun consolidateSrs(primary: FlashcardSrsEntity, secondary: FlashcardSrsEntity): FlashcardSrsEntity {
        return primary.copy(
            repetition = max(primary.repetition, secondary.repetition),
            intervalDays = max(primary.intervalDays, secondary.intervalDays),
            easeFactor = (primary.easeFactor + secondary.easeFactor) / 2.0f,
            dueDateMs = max(primary.dueDateMs, secondary.dueDateMs),
            lastReviewedMs = max(primary.lastReviewedMs, secondary.lastReviewedMs),
            lapses = primary.lapses + secondary.lapses,
            totalReviews = primary.totalReviews + secondary.totalReviews,
            state = if (primary.repetition >= secondary.repetition) primary.state else secondary.state,
            lastRating = primary.lastRating ?: secondary.lastRating
        )
    }
}
