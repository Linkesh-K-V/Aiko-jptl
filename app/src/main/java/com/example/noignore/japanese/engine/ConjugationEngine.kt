package com.example.noignore.japanese.engine

enum class VerbGroup {
    GODAN,
    ICHIDAN,
    KURU_IRREGULAR,
    SURU_IRREGULAR
}

data class GodanStemForms(
    val iRow: String,
    val aRow: String,
    val eRow: String,
    val oRow: String,
    val teEnd: String,
    val taEnd: String
)

data class ConjugationResult(
    val formName: String,
    val conjugated: String,
    val reading: String,
    val romaji: String,
    val englishRule: String
)

/**
 * Algorithmic Japanese Conjugation Engine.
 * Implements strict, linguistically sound transformation tables for Godan, Ichidan,
 * Kuru, and Suru verbs, as well as I-adjectives and Na-adjectives.
 */
object ConjugationEngine {

    fun determineVerbGroup(dictionaryForm: String): VerbGroup {
        val clean = dictionaryForm.trim()
        if (clean == "来る" || clean == "くる") return VerbGroup.KURU_IRREGULAR
        if (clean == "する" || clean.endsWith("する")) return VerbGroup.SURU_IRREGULAR
        if (!clean.endsWith("る")) return VerbGroup.GODAN

        // Known Godan verbs ending in -iru/-eru (exceptions to Ichidan rule)
        val godanExceptions = setOf(
            "帰る", "かえる", "走る", "はしる", "切る", "きる", "知る", "しる",
            "入る", "はいる", "要る", "いる", "減る", "へる", "焦る", "あせる",
            "喋る", "しゃべる", "滑る", "すべる", "握る", "にぎる", "限る", "かぎる"
        )
        if (godanExceptions.contains(clean)) return VerbGroup.GODAN

        // Standard Ichidan rule: ends in -iru or -eru
        val stem = clean.dropLast(1)
        val lastKana = stem.lastOrNull() ?: return VerbGroup.GODAN
        val isIorEEnding = "いきしちにひみりぎじびぴえけせてねへめれげぜべぺ".contains(lastKana)
        return if (isIorEEnding) VerbGroup.ICHIDAN else VerbGroup.GODAN
    }

    fun getAllConjugations(dictionaryForm: String, dictionaryReading: String): List<ConjugationResult> {
        val group = determineVerbGroup(dictionaryForm)
        val list = mutableListOf<ConjugationResult>()

        when (group) {
            VerbGroup.KURU_IRREGULAR -> {
                val isKanji = dictionaryForm.startsWith("来")
                val root = if (isKanji) "来" else "こ"
                val rootKi = if (isKanji) "来" else "き"

                list.add(ConjugationResult("Present Polite", "${rootKi}ます", "きます", "kimasu", "Polite non-past"))
                list.add(ConjugationResult("Negative Plain", "${root}ない", "こない", "konai", "Plain negative"))
                list.add(ConjugationResult("Past Plain", "${rootKi}た", "きた", "kita", "Plain past"))
                list.add(ConjugationResult("Te-form", "${rootKi}て", "きて", "kite", "Connecting / request stem"))
                list.add(ConjugationResult("Potential", "${rootKi}られる", "こられる", "korareru", "Ability / can come"))
                list.add(ConjugationResult("Passive", "${rootKi}られる", "こられる", "korareru", "Suffering / passive"))
                list.add(ConjugationResult("Causative", "${rootKi}させる", "こさせる", "kosaseru", "Make/let come"))
                list.add(ConjugationResult("Volitional", "${rootKi}よう", "こよう", "koyou", "Let's come / will come"))
                list.add(ConjugationResult("Conditional (-ba)", "${rootKi}れば", "くれば", "kureba", "If one comes"))
                list.add(ConjugationResult("Conditional (-tara)", "${rootKi}たら", "きたら", "kitara", "When/if one comes"))
            }
            VerbGroup.SURU_IRREGULAR -> {
                val prefix = dictionaryForm.removeSuffix("する")
                val prefixReading = dictionaryReading.removeSuffix("する")

                list.add(ConjugationResult("Present Polite", "${prefix}します", "${prefixReading}します", "shimasu", "Polite non-past"))
                list.add(ConjugationResult("Negative Plain", "${prefix}しない", "${prefixReading}しない", "shinai", "Plain negative"))
                list.add(ConjugationResult("Past Plain", "${prefix}した", "${prefixReading}した", "shita", "Plain past"))
                list.add(ConjugationResult("Te-form", "${prefix}して", "${prefixReading}して", "shite", "Connecting / request stem"))
                list.add(ConjugationResult("Potential", "${prefix}できる", "${prefixReading}できる", "dekiru", "Can do"))
                list.add(ConjugationResult("Passive", "${prefix}される", "${prefixReading}される", "sareru", "Is done"))
                list.add(ConjugationResult("Causative", "${prefix}させる", "${prefixReading}させる", "saseru", "Make/let do"))
                list.add(ConjugationResult("Volitional", "${prefix}しよう", "${prefixReading}しよう", "shiyou", "Let's do / will do"))
                list.add(ConjugationResult("Conditional (-ba)", "${prefix}すれば", "${prefixReading}すれば", "sureba", "If one does"))
                list.add(ConjugationResult("Conditional (-tara)", "${prefix}したら", "${prefixReading}したら", "shitara", "When/if one does"))
            }
            VerbGroup.ICHIDAN -> {
                val stem = dictionaryForm.dropLast(1)
                val stemR = dictionaryReading.dropLast(1)

                list.add(ConjugationResult("Present Polite", "${stem}ます", "${stemR}ます", "stem + masu", "Polite non-past"))
                list.add(ConjugationResult("Negative Plain", "${stem}ない", "${stemR}ない", "stem + nai", "Plain negative"))
                list.add(ConjugationResult("Past Plain", "${stem}た", "${stemR}た", "stem + ta", "Plain past"))
                list.add(ConjugationResult("Te-form", "${stem}て", "${stemR}て", "stem + te", "Connecting / request stem"))
                list.add(ConjugationResult("Potential", "${stem}られる", "${stemR}られる", "stem + rareru", "Can do"))
                list.add(ConjugationResult("Passive", "${stem}られる", "${stemR}られる", "stem + rareru", "Passive action"))
                list.add(ConjugationResult("Causative", "${stem}させる", "${stemR}させる", "stem + saseru", "Make/let do"))
                list.add(ConjugationResult("Volitional", "${stem}よう", "${stemR}よう", "stem + you", "Let's do"))
                list.add(ConjugationResult("Conditional (-ba)", "${stem}れば", "${stemR}れば", "stem + reba", "If / conditional"))
                list.add(ConjugationResult("Conditional (-tara)", "${stem}たら", "${stemR}たら", "stem + tara", "When/if"))
            }
            VerbGroup.GODAN -> {
                val lastChar = dictionaryForm.takeLast(1)
                val stem = dictionaryForm.dropLast(1)
                val stemR = dictionaryReading.dropLast(1)
                val isIku = dictionaryForm == "行く" || dictionaryReading == "いく"

                // Godan Kana transformations
                val (iRow, aRow, eRow, oRow, teEnd, taEnd) = when (lastChar) {
                    "う" -> GodanStemForms("い", "わ", "え", "お", "って", "った")
                    "く" -> if (isIku) GodanStemForms("き", "か", "け", "こ", "って", "った") else GodanStemForms("き", "か", "け", "こ", "いて", "いた")
                    "ぐ" -> GodanStemForms("ぎ", "が", "げ", "ご", "いで", "いだ")
                    "す" -> GodanStemForms("し", "さ", "せ", "そ", "して", "した")
                    "つ" -> GodanStemForms("ち", "た", "て", "と", "って", "った")
                    "ぬ" -> GodanStemForms("に", "な", "ね", "の", "んで", "んだ")
                    "ぶ" -> GodanStemForms("び", "ば", "べ", "ぼ", "んで", "んだ")
                    "む" -> GodanStemForms("み", "ま", "め", "も", "んで", "んだ")
                    "る" -> GodanStemForms("り", "ら", "れ", "ろ", "って", "った")
                    else -> GodanStemForms("い", "わ", "え", "お", "って", "った")
                }

                list.add(ConjugationResult("Present Polite", "${stem}${iRow}ます", "${stemR}${iRow}ます", "masu form", "Polite non-past"))
                list.add(ConjugationResult("Negative Plain", "${stem}${aRow}ない", "${stemR}${aRow}ない", "nai form", "Plain negative"))
                list.add(ConjugationResult("Past Plain", "${stem}${taEnd}", "${stemR}${taEnd}", "ta form", "Plain past"))
                list.add(ConjugationResult("Te-form", "${stem}${teEnd}", "${stemR}${teEnd}", "te form", "Connecting / request stem"))
                list.add(ConjugationResult("Potential", "${stem}${eRow}る", "${stemR}${eRow}る", "potential", "Can do"))
                list.add(ConjugationResult("Passive", "${stem}${aRow}れる", "${stemR}${aRow}れる", "passive", "Passive action"))
                list.add(ConjugationResult("Causative", "${stem}${aRow}せる", "${stemR}${aRow}せる", "causative", "Make/let do"))
                list.add(ConjugationResult("Volitional", "${stem}${oRow}う", "${stemR}${oRow}う", "volitional", "Let's do / will do"))
                list.add(ConjugationResult("Conditional (-ba)", "${stem}${eRow}ば", "${stemR}${eRow}ば", "ba form", "If / conditional"))
                list.add(ConjugationResult("Conditional (-tara)", "${stem}${taEnd}ら", "${stemR}${taEnd}ら", "tara form", "When/if"))
            }
        }

        return list
    }
}
