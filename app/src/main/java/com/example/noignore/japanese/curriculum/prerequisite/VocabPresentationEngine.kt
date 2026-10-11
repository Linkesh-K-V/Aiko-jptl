package com.example.noignore.japanese.curriculum.prerequisite

import com.example.noignore.japanese.model.JapaneseCategory
import com.example.noignore.japanese.model.JapaneseItem

data class VocabPresentationFormat(
    val primaryDisplay: String,
    val secondaryDisplay: String,
    val showSecondary: Boolean,
    val isKanaFirst: Boolean,
    val isReadableWithoutFurigana: Boolean
)

/**
 * DEF-12-02: Level-C Vocabulary Presentation Engine.
 * Ensures all Level-C vocabulary (words containing Kanji outside the 104 core N5 set)
 * remain immediately readable for beginners via Kana-first display.
 * When the learner has mastered the Kanji, standard Kanji presentation is dynamically restored.
 */
object VocabPresentationEngine {

    const val KANA_FIRST = "KANA_FIRST"
    const val KANJI_STANDARD = "KANJI_STANDARD"

    // 104 Canonical N5 Kanji Characters
    val N5_CORE_KANJI: Set<String> = setOf(
        "一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
        "百", "千", "万", "円", "口", "日", "月", "火", "水", "木",
        "金", "土", "年", "左", "右", "上", "下", "中", "大", "小",
        "山", "川", "花", "雨", "天", "気", "名", "目", "耳", "手",
        "足", "力", "人", "男", "女", "子", "父", "母", "友", "本",
        "先", "生", "学", "校", "何", "前", "後", "午", "間", "東",
        "西", "南", "北", "白", "赤", "青", "立", "休", "見", "聞",
        "語", "読", "書", "買", "話", "行", "来", "出", "入", "食",
        "飲", "村", "分", "時", "半", "今", "町", "肉", "長", "高",
        "安", "新", "古", "多", "少", "車", "駅", "社", "国", "店",
        "道", "魚", "茶", "黒"
    )

    /**
     * Determines whether an item is Level-C vocabulary.
     * Defined as: N5 Vocabulary containing one or more Kanji characters outside the 104 core N5 Kanji set.
     */
    fun isLevelCVocab(item: JapaneseItem, n5KanjiSet: Set<String> = N5_CORE_KANJI): Boolean {
        if (item.category != JapaneseCategory.VOCAB) return false
        if (item.displayScript == KANA_FIRST) return true
        val kanjiInWord = VocabPrerequisiteModel.extractKanji(item.japanese)
        if (kanjiInWord.isEmpty()) return false
        return kanjiInWord.any { !n5KanjiSet.contains(it) }
    }

    /**
     * Checks if all Kanji in the word are mastered by the learner.
     */
    fun isKanjiMasteredForWord(item: JapaneseItem, masteredKanji: Set<String>): Boolean {
        val kanjiInWord = VocabPrerequisiteModel.extractKanji(item.japanese)
        if (kanjiInWord.isEmpty()) return true
        return kanjiInWord.all { masteredKanji.contains(it) }
    }

    /**
     * Resolves presentation rules according to learner status and settings.
     *
     * In Absolute Beginner / Kana-known mode (or when unmastered Kanji):
     * - Primary display: Hiragana reading (e.g. つくえ)
     * - Secondary display: Kanji (e.g. 机)
     * - Even if furigana is OFF, the word is 100% readable because Hiragana is the primary text.
     *
     * Once Kanji is mastered (or in advanced mode):
     * - Primary display: Kanji (e.g. 机)
     * - Secondary display: Reading (e.g. つくえ)
     */
    fun resolvePresentation(
        item: JapaneseItem,
        isBeginnerMode: Boolean,
        isFuriganaEnabled: Boolean,
        masteredKanji: Set<String> = emptySet(),
        n5KanjiSet: Set<String> = N5_CORE_KANJI
    ): VocabPresentationFormat {
        val isLevelC = isLevelCVocab(item, n5KanjiSet)
        val kanjiMastered = isKanjiMasteredForWord(item, masteredKanji)
        val readingText = item.reading.ifBlank { item.japanese }

        val shouldUseKanaFirst = (isLevelC || item.displayScript == KANA_FIRST) && (!kanjiMastered || isBeginnerMode)

        return if (shouldUseKanaFirst) {
            VocabPresentationFormat(
                primaryDisplay = readingText,
                secondaryDisplay = item.japanese,
                showSecondary = true,
                isKanaFirst = true,
                isReadableWithoutFurigana = true // Hiragana is directly visible!
            )
        } else {
            VocabPresentationFormat(
                primaryDisplay = item.japanese,
                secondaryDisplay = readingText,
                showSecondary = isFuriganaEnabled,
                isKanaFirst = false,
                isReadableWithoutFurigana = kanjiMastered || VocabPrerequisiteModel.extractKanji(item.japanese).isEmpty()
            )
        }
    }
}
