package com.example.noignore.japanese.curriculum.canonical

import com.example.noignore.data.srs.FlashcardSrsEntity
import kotlin.math.max

/**
 * Single source of truth for canonical curriculum identifiers and duplicate reconciliation.
 * Reconciles the 71 Phase 4 vocabulary duplicate records (ID >= 98) back to the canonical
 * baseline records (ID <= 97) while safely preserving SRS review histories and learner models.
 */
object CanonicalCurriculumIds {

    val DUPLICATE_TO_CANONICAL_MAP: Map<String, String> = mapOf(
        "n5_v_212" to "n5_v_03", // 行く (いく)
        "n5_v_213" to "n5_v_04", // 来る (くる)
        "n5_v_215" to "n5_v_05", // 見る (みる)
        "n5_v_216" to "n5_v_06", // 聞く (きく)
        "n5_v_219" to "n5_v_07", // 話す (はなす)
        "n5_v_220" to "n5_v_08", // 買う (かう)
        "n5_v_238" to "n5_v_09", // 大きい (おおきい)
        "n5_v_239" to "n5_v_10", // 小さい (ちいさい)
        "n5_v_240" to "n5_v_11", // 新しい (あたらしい)
        "n5_v_241" to "n5_v_12", // 古い (ふるい)
        "n5_v_242" to "n5_v_13", // 高い (たかい)
        "n5_v_243" to "n5_v_14", // 安い (やすい)
        "n5_v_246" to "n5_v_15", // 良い (いい)
        "n5_v_135" to "n5_v_16", // 家族 (かぞく)
        "n5_v_137" to "n5_v_19", // 兄 (あに)
        "n5_v_138" to "n5_v_21", // 姉 (あね)
        "n5_v_139" to "n5_v_23", // 弟 (おとうと)
        "n5_v_140" to "n5_v_24", // 妹 (いもうと)
        "n5_v_141" to "n5_v_27", // 友達 (ともだち)
        "n5_v_142" to "n5_v_28", // 犬 (いぬ)
        "n5_v_143" to "n5_v_29", // 猫 (ねこ)
        "n5_v_144" to "n5_v_31", // 魚 (さかな)
        "n5_v_145" to "n5_v_33", // りんご (りんご)
        "n5_v_146" to "n5_v_34", // お茶 (おちゃ)
        "n5_v_147" to "n5_v_36", // 飲み物 (のみもの)
        "n5_v_148" to "n5_v_38", // ご飯 (ごはん)
        "n5_v_149" to "n5_v_39", // 晩ご飯 (ばんごはん)
        "n5_v_150" to "n5_v_40", // パン (ぱん)
        "n5_v_151" to "n5_v_41", // 肉 (にく)
        "n5_v_152" to "n5_v_42", // 卵 (たまご)
        "n5_v_154" to "n5_v_43", // 時間 (じかん)
        "n5_v_155" to "n5_v_44", // 今 (いま)
        "n5_v_156" to "n5_v_45", // 朝 (あさ)
        "n5_v_157" to "n5_v_46", // 昼 (ひる)
        "n5_v_158" to "n5_v_47", // 晩 (ばん)
        "n5_v_159" to "n5_v_48", // 今日 (きょう)
        "n5_v_160" to "n5_v_49", // 明日 (あした)
        "n5_v_161" to "n5_v_53", // 年 (とし)
        "n5_v_162" to "n5_v_54", // 家 (いえ)
        "n5_v_163" to "n5_v_55", // 学校 (がっこう)
        "n5_v_164" to "n5_v_56", // 駅 (えき)
        "n5_v_165" to "n5_v_57", // 電車 (でんしゃ)
        "n5_v_166" to "n5_v_58", // 銀行 (ぎんこう)
        "n5_v_167" to "n5_v_60", // 病院 (びょういん)
        "n5_v_168" to "n5_v_61", // 車 (くるま)
        "n5_v_169" to "n5_v_62", // 学生 (がくせい)
        "n5_v_170" to "n5_v_63", // 先生 (せんせい)
        "n5_v_171" to "n5_v_64", // 会社 (かいしゃ)
        "n5_v_211" to "n5_v_65", // 食べる (たべる)
        "n5_v_214" to "n5_v_66", // 飲む (のむ)
        "n5_v_217" to "n5_v_67", // 読む (よむ)
        "n5_v_218" to "n5_v_68", // 書く (かく)
        "n5_v_221" to "n5_v_69", // する (する)
        "n5_v_222" to "n5_v_71", // 寝る (ねる)
        "n5_v_223" to "n5_v_72", // 起きる (おきる)
        "n5_v_224" to "n5_v_73", // 会う (あう)
        "n5_v_225" to "n5_v_74", // 待つ (まつ)
        "n5_v_226" to "n5_v_75", // 教える (おしえる)
        "n5_v_227" to "n5_v_76", // 習う (ならう)
        "n5_v_173" to "n5_v_77", // ここ (ここ)
        "n5_v_174" to "n5_v_78", // そこ (そこ)
        "n5_v_175" to "n5_v_79", // あそこ (あそこ)
        "n5_v_176" to "n5_v_80", // どこ (どこ)
        "n5_v_177" to "n5_v_81", // だれ (だれ)
        "n5_v_178" to "n5_v_82", // 何 (なに)
        "n5_v_179" to "n5_v_83", // いつ (いつ)
        "n5_v_180" to "n5_v_84", // どうして (どうして)
        "n5_v_181" to "n5_v_85", // どう (どう)
        "n5_v_182" to "n5_v_86", // これ (これ)
        "n5_v_183" to "n5_v_87", // それ (それ)
        "n5_v_184" to "n5_v_88"  // あれ (あれ)
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
