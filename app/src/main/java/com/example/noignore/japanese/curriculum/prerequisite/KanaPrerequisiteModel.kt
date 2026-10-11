package com.example.noignore.japanese.curriculum.prerequisite

import com.example.noignore.japanese.model.JapaneseItem

/**
 * Kana Progression Stages for progressive mastery from zero-knowledge beginner to fluency.
 * Prevents overwhelming a learner with all 211 characters at once.
 */
enum class KanaStageType {
    HIRAGANA_VOWELS,          // Stage 1: あ い う え お
    HIRAGANA_KA_ROW,          // Stage 2: か き く け こ
    HIRAGANA_SA_ROW,          // Stage 3: さ し す せ そ
    HIRAGANA_TA_ROW,          // Stage 4: た ち つ て と
    HIRAGANA_NA_ROW,          // Stage 5: な に ぬ ね の
    HIRAGANA_HA_ROW,          // Stage 6: は ひ ふ へ ほ
    HIRAGANA_MA_ROW,          // Stage 7: ま み む め も
    HIRAGANA_YA_RA_ROWS,      // Stage 8: や ゆ よ ら り る れ ろ
    HIRAGANA_WA_WO_N,         // Stage 9: わ を ん
    HIRAGANA_DAKUTEN,         // Stage 10: が〜ぽ (voiced & semi-voiced)
    HIRAGANA_YOON,            // Stage 11: きゃ〜ぴょ (contracted sounds)
    KATAKANA_BASIC,           // Stage 12: ア〜ン (basic katakana)
    KATAKANA_EXTENDED         // Stage 13: ガ〜ポ & キャ〜ピョ
}

data class KanaStageInfo(
    val stageNumber: Int,
    val stageType: KanaStageType,
    val title: String,
    val characters: List<String>,
    val minimumKanaMasteryRequired: Float,
    val requiredLessonId: Int?
)

object KanaPrerequisiteModel {

    val STAGES: List<KanaStageInfo> = listOf(
        KanaStageInfo(
            stageNumber = 1,
            stageType = KanaStageType.HIRAGANA_VOWELS,
            title = "Hiragana Vowels (あ・い・う・え・お)",
            characters = listOf("あ", "い", "う", "え", "お"),
            minimumKanaMasteryRequired = 0.0f,
            requiredLessonId = null
        ),
        KanaStageInfo(
            stageNumber = 2,
            stageType = KanaStageType.HIRAGANA_KA_ROW,
            title = "Hiragana Ka-row (か・き・く・け・こ)",
            characters = listOf("か", "き", "く", "け", "こ"),
            minimumKanaMasteryRequired = 0.08f,
            requiredLessonId = 1
        ),
        KanaStageInfo(
            stageNumber = 3,
            stageType = KanaStageType.HIRAGANA_SA_ROW,
            title = "Hiragana Sa-row (さ・し・す・せ・そ)",
            characters = listOf("さ", "し", "す", "せ", "そ"),
            minimumKanaMasteryRequired = 0.16f,
            requiredLessonId = 2
        ),
        KanaStageInfo(
            stageNumber = 4,
            stageType = KanaStageType.HIRAGANA_TA_ROW,
            title = "Hiragana Ta-row (た・ち・つ・て・と)",
            characters = listOf("た", "ち", "つ", "て", "と"),
            minimumKanaMasteryRequired = 0.24f,
            requiredLessonId = 3
        ),
        KanaStageInfo(
            stageNumber = 5,
            stageType = KanaStageType.HIRAGANA_NA_ROW,
            title = "Hiragana Na-row (な・に・ぬ・ね・の)",
            characters = listOf("な", "に", "ぬ", "ね", "の"),
            minimumKanaMasteryRequired = 0.32f,
            requiredLessonId = 3
        ),
        KanaStageInfo(
            stageNumber = 6,
            stageType = KanaStageType.HIRAGANA_HA_ROW,
            title = "Hiragana Ha-row (は・ひ・ふ・へ・ほ)",
            characters = listOf("は", "ひ", "ふ", "へ", "ほ"),
            minimumKanaMasteryRequired = 0.40f,
            requiredLessonId = 4
        ),
        KanaStageInfo(
            stageNumber = 7,
            stageType = KanaStageType.HIRAGANA_MA_ROW,
            title = "Hiragana Ma-row (ま・み・む・め・も)",
            characters = listOf("ま", "み", "む", "め", "も"),
            minimumKanaMasteryRequired = 0.48f,
            requiredLessonId = 4
        ),
        KanaStageInfo(
            stageNumber = 8,
            stageType = KanaStageType.HIRAGANA_YA_RA_ROWS,
            title = "Hiragana Ya & Ra rows (や・ゆ・よ・ら・り・る・れ・ろ)",
            characters = listOf("や", "ゆ", "よ", "ら", "り", "る", "れ", "ろ"),
            minimumKanaMasteryRequired = 0.56f,
            requiredLessonId = 5
        ),
        KanaStageInfo(
            stageNumber = 9,
            stageType = KanaStageType.HIRAGANA_WA_WO_N,
            title = "Hiragana Wa, Wo, N (わ・を・ん)",
            characters = listOf("わ", "を", "ん"),
            minimumKanaMasteryRequired = 0.64f,
            requiredLessonId = 5
        ),
        KanaStageInfo(
            stageNumber = 10,
            stageType = KanaStageType.HIRAGANA_DAKUTEN,
            title = "Hiragana Dakuten & Handakuten (が〜ぽ)",
            characters = listOf(
                "が", "ぎ", "ぐ", "げ", "ご",
                "ざ", "じ", "ず", "ぜ", "ぞ",
                "だ", "ぢ", "づ", "で", "ど",
                "ば", "び", "ぶ", "べ", "ぼ",
                "ぱ", "ぴ", "ぷ", "ぺ", "ぽ"
            ),
            minimumKanaMasteryRequired = 0.72f,
            requiredLessonId = 5
        ),
        KanaStageInfo(
            stageNumber = 11,
            stageType = KanaStageType.HIRAGANA_YOON,
            title = "Hiragana Yōon Combinations (きゃ〜ぴょ)",
            characters = listOf(
                "きゃ", "きゅ", "きょ", "しゃ", "しゅ", "しょ",
                "ちゃ", "ちゅ", "ちょ", "にゃ", "にゅ", "にょ",
                "ひゃ", "ひゅ", "ひょ", "みゃ", "みゅ", "みょ",
                "りゃ", "りゅ", "りょ", "ぎゃ", "ぎゅ", "ぎょ",
                "じゃ", "じゅ", "じょ", "びゃ", "びゅ", "びょ",
                "ぴゃ", "ぴゅ", "ぴょ"
            ),
            minimumKanaMasteryRequired = 0.80f,
            requiredLessonId = 6
        ),
        KanaStageInfo(
            stageNumber = 12,
            stageType = KanaStageType.KATAKANA_BASIC,
            title = "Katakana Basic Syllabary (ア〜ン)",
            characters = listOf(
                "ア", "イ", "ウ", "エ", "オ",
                "カ", "キ", "ク", "ケ", "コ",
                "サ", "シ", "ス", "セ", "ソ",
                "タ", "チ", "ツ", "テ", "ト",
                "ナ", "ニ", "ヌ", "ネ", "ノ",
                "ハ", "ヒ", "フ", "ヘ", "ホ",
                "マ", "ミ", "ム", "メ", "モ",
                "ヤ", "ユ", "ヨ",
                "ラ", "リ", "ル", "レ", "ロ",
                "ワ", "ヲ", "ン"
            ),
            minimumKanaMasteryRequired = 0.85f,
            requiredLessonId = 6
        ),
        KanaStageInfo(
            stageNumber = 13,
            stageType = KanaStageType.KATAKANA_EXTENDED,
            title = "Katakana Dakuten & Yōon",
            characters = listOf(
                "ガ", "ギ", "グ", "ゲ", "ゴ",
                "ザ", "ジ", "ズ", "ゼ", "ゾ",
                "ダ", "ヂ", "ヅ", "デ", "ド",
                "バ", "ビ", "ブ", "ベ", "ボ",
                "パ", "ピ", "プ", "ペ", "ポ"
            ),
            minimumKanaMasteryRequired = 0.90f,
            requiredLessonId = 6
        )
    )

    private val charToStageMap: Map<String, Int> by lazy {
        val map = mutableMapOf<String, Int>()
        for (stage in STAGES) {
            for (ch in stage.characters) {
                map[ch] = stage.stageNumber
            }
        }
        map
    }

    fun getStageForCharacter(ch: String): Int {
        return charToStageMap[ch] ?: 13
    }

    fun getStageForId(id: String): Int {
        if (!id.startsWith("kana_")) return 1
        val isKatakana = id.startsWith("kana_k_")
        val numPart = id.substringAfterLast("_").toIntOrNull() ?: 1
        return if (isKatakana) {
            if (numPart <= 46) 12 else 13
        } else {
            when {
                numPart <= 5 -> 1
                numPart <= 10 -> 2
                numPart <= 15 -> 3
                numPart <= 20 -> 4
                numPart <= 25 -> 5
                numPart <= 30 -> 6
                numPart <= 35 -> 7
                numPart <= 43 -> 8
                numPart <= 46 -> 9
                numPart <= 71 -> 10
                else -> 11
            }
        }
    }

    fun getUnlockedStages(
        kanaMasteryScore: Float,
        completedLessonIds: Set<Int> = emptySet()
    ): Set<Int> {
        val unlocked = mutableSetOf(1) // Stage 1 is always unlocked
        for (stage in STAGES) {
            val lessonSatisfied = stage.requiredLessonId == null || completedLessonIds.contains(stage.requiredLessonId)
            val scoreSatisfied = kanaMasteryScore >= stage.minimumKanaMasteryRequired
            if (lessonSatisfied || scoreSatisfied) {
                unlocked.add(stage.stageNumber)
            }
        }
        return unlocked
    }

    fun isCharacterUnlocked(
        ch: String,
        kanaMasteryScore: Float,
        completedLessonIds: Set<Int> = emptySet()
    ): Boolean {
        val stage = getStageForCharacter(ch)
        return getUnlockedStages(kanaMasteryScore, completedLessonIds).contains(stage)
    }

    /**
     * Filters a list of Kana items so a beginner sees items appropriate for their current stage
     * plus the next target stage being introduced, preventing premature exposure.
     */
    fun filterAccessibleKana(
        items: List<JapaneseItem>,
        kanaMasteryScore: Float,
        completedLessonIds: Set<Int> = emptySet()
    ): List<JapaneseItem> {
        val unlockedStages = getUnlockedStages(kanaMasteryScore, completedLessonIds)
        val maxUnlocked = unlockedStages.maxOrNull() ?: 1
        // Allow up to current unlocked stages plus 1 active learning stage
        val allowableStages = (1..minOf(maxUnlocked + 1, 13)).toSet()

        return items.filter { item ->
            val stage = getStageForId(item.id).takeIf { it in 1..13 } ?: getStageForCharacter(item.japanese)
            stage in allowableStages
        }.sortedBy { item ->
            getStageForId(item.id).takeIf { it in 1..13 } ?: getStageForCharacter(item.japanese)
        }
    }
}
