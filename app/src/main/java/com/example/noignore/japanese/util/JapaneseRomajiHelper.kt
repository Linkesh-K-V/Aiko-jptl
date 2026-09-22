package com.example.noignore.japanese.util

/**
 * Utility to provide automatic Kana-to-Romaji conversion and clean Romaji formatting
 * for Kanji Study and Renshuu practice.
 */
object JapaneseRomajiHelper {

    private val kanaToRomajiMap: Map<String, String> = mapOf(
        // Combinations (digraphs)
        "きゃ" to "kya", "きゅ" to "kyu", "きょ" to "kyo",
        "しゃ" to "sha", "しゅ" to "shu", "しょ" to "sho",
        "ちゃ" to "cha", "ちゅ" to "chu", "ちょ" to "cho",
        "にゃ" to "nya", "にゅ" to "nyu", "にょ" to "nyo",
        "ひゃ" to "hya", "ひゅ" to "hyu", "ひょ" to "hyo",
        "みゃ" to "mya", "みゅ" to "myu", "みょ" to "myo",
        "りゃ" to "rya", "りゅ" to "ryu", "りょ" to "ryo",
        "ぎゃ" to "gya", "ぎゅ" to "gyu", "ぎょ" to "gyo",
        "じゃ" to "ja", "じゅ" to "ju", "じょ" to "jo",
        "びゃ" to "bya", "びゅ" to "byu", "びょ" to "byo",
        "ぴゃ" to "pya", "ぴゅ" to "pyu", "ぴょ" to "pyo",
        "キャ" to "kya", "キュ" to "kyu", "キョ" to "kyo",
        "シャ" to "sha", "シュ" to "shu", "ショ" to "sho",
        "チャ" to "cha", "チュ" to "chu", "チョ" to "cho",
        "ニャ" to "nya", "ニュ" to "nyu", "ニョ" to "nyo",
        "ヒャ" to "hya", "ヒュ" to "hyu", "ヒョ" to "hyo",
        "ミャ" to "mya", "ミュ" to "myu", "ミョ" to "myo",
        "リャ" to "rya", "リュ" to "ryu", "リョ" to "ryo",
        "ギャ" to "gya", "ギュ" to "gyu", "ギョ" to "gyo",
        "ジャ" to "ja", "ジュ" to "ju", "ジョ" to "jo",
        "ビャ" to "bya", "ビュ" to "byu", "ビョ" to "byo",
        "ピャ" to "pya", "ピュ" to "pyu", "ピョ" to "pyo",

        // Basic Hiragana
        "あ" to "a", "い" to "i", "う" to "u", "え" to "e", "お" to "o",
        "か" to "ka", "き" to "ki", "く" to "ku", "け" to "ke", "こ" to "ko",
        "さ" to "sa", "し" to "shi", "す" to "su", "せ" to "se", "そ" to "so",
        "た" to "ta", "ち" to "chi", "つ" to "tsu", "て" to "te", "と" to "to",
        "な" to "na", "に" to "ni", "ぬ" to "nu", "ね" to "ne", "の" to "no",
        "は" to "ha", "ひ" to "hi", "ふ" to "fu", "へ" to "he", "ほ" to "ho",
        "ま" to "ma", "み" to "mi", "む" to "mu", "め" to "me", "も" to "mo",
        "や" to "ya", "ゆ" to "yu", "よ" to "yo",
        "ら" to "ra", "り" to "ri", "る" to "ru", "れ" to "re", "ろ" to "ro",
        "わ" to "wa", "を" to "wo", "ん" to "n",

        // Voiced & Semi-voiced Hiragana
        "が" to "ga", "ぎ" to "gi", "ぐ" to "gu", "げ" to "ge", "ご" to "go",
        "ざ" to "za", "じ" to "ji", "ず" to "zu", "ぜ" to "ze", "ぞ" to "zo",
        "だ" to "da", "ぢ" to "ji", "づ" to "zu", "で" to "de", "ど" to "do",
        "ば" to "ba", "び" to "bi", "ぶ" to "bu", "べ" to "be", "ぼ" to "bo",
        "ぱ" to "pa", "ぴ" to "pi", "ぷ" to "pu", "ぺ" to "pe", "ぽ" to "po",

        // Basic Katakana
        "ア" to "a", "イ" to "i", "ウ" to "u", "エ" to "e", "オ" to "o",
        "カ" to "ka", "キ" to "ki", "ク" to "ku", "ケ" to "ke", "コ" to "ko",
        "サ" to "sa", "シ" to "shi", "ス" to "su", "セ" to "se", "ソ" to "so",
        "タ" to "ta", "チ" to "chi", "ツ" to "tsu", "テ" to "te", "ト" to "to",
        "ナ" to "na", "ニ" to "ni", "ヌ" to "nu", "ネ" to "ne", "ノ" to "no",
        "ハ" to "ha", "ヒ" to "hi", "フ" to "fu", "ヘ" to "he", "ホ" to "ho",
        "マ" to "ma", "ミ" to "mi", "ム" to "mu", "メ" to "me", "モ" to "mo",
        "ヤ" to "ya", "ユ" to "yu", "ヨ" to "yo",
        "ラ" to "ra", "リ" to "ri", "ル" to "ru", "レ" to "re", "ロ" to "ro",
        "ワ" to "wa", "ヲ" to "wo", "ン" to "n",

        // Voiced & Semi-voiced Katakana
        "ガ" to "ga", "ギ" to "gi", "グ" to "gu", "ゲ" to "ge", "ゴ" to "go",
        "ザ" to "za", "ジ" to "ji", "ズ" to "zu", "ゼ" to "ze", "ゾ" to "zo",
        "ダ" to "da", "ヂ" to "ji", "ヅ" to "zu", "デ" to "de", "ド" to "do",
        "バ" to "ba", "ビ" to "bi", "ブ" to "bu", "ベ" to "be", "ボ" to "bo",
        "パ" to "pa", "ピ" to "pi", "プ" to "pu", "ペ" to "pe", "ポ" to "po",

        // Extended Katakana
        "ファ" to "fa", "フィ" to "fi", "フェ" to "fe", "フォ" to "fo",
        "ティ" to "ti", "ディ" to "di", "ドゥ" to "du", "ウィ" to "wi", "ウェ" to "we"
    )

    /**
     * Converts Kana text (Hiragana / Katakana) into Romaji.
     * Handles small tsu (っ/ッ) for consonant doubling and long vowels (ー).
     */
    fun toRomaji(kana: String): String {
        if (kana.isBlank()) return ""
        val sb = StringBuilder()
        var i = 0
        val len = kana.length

        while (i < len) {
            val ch = kana[i]

            // Small tsu: double next consonant
            if (ch == 'っ' || ch == 'ッ') {
                if (i + 1 < len) {
                    val nextSub = if (i + 2 <= len) kana.substring(i + 1, (i + 3).coerceAtMost(len)) else ""
                    val nextRomaji = kanaToRomajiMap[nextSub] ?: kanaToRomajiMap[kana[i + 1].toString()] ?: ""
                    if (nextRomaji.isNotEmpty()) {
                        sb.append(nextRomaji[0])
                    }
                }
                i++
                continue
            }

            // Long vowel mark
            if (ch == 'ー') {
                if (sb.isNotEmpty()) {
                    val lastChar = sb.last()
                    if ("aeiou".contains(lastChar, ignoreCase = true)) {
                        sb.append(lastChar)
                    }
                }
                i++
                continue
            }

            // Punctuation and separation symbols
            if (ch == '・' || ch == '、' || ch == ',' || ch == '/') {
                sb.append(" / ")
                i++
                continue
            }
            if (ch == ' ' || ch == '　') {
                sb.append(" ")
                i++
                continue
            }
            if (ch == '-' || ch == '・') {
                sb.append("-")
                i++
                continue
            }

            // Try 2-char combination first
            if (i + 2 <= len) {
                val pair = kana.substring(i, i + 2)
                val romajiPair = kanaToRomajiMap[pair]
                if (romajiPair != null) {
                    sb.append(romajiPair)
                    i += 2
                    continue
                }
            }

            // Try 1-char
            val single = ch.toString()
            val romajiSingle = kanaToRomajiMap[single]
            if (romajiSingle != null) {
                sb.append(romajiSingle)
            } else {
                sb.append(ch)
            }
            i++
        }

        return sb.toString().replace(Regex("\\s+"), " ").trim()
    }

    /**
     * Splits comma/slash separated readings (e.g., "ニチ, ジツ" or "ひ, -び, -か")
     * and produces paired readings with romaji:
     * e.g. "ニチ (nichi), ジツ (jitsu)"
     */
    fun formatReadingWithRomaji(readingsText: String, explicitRomaji: String = ""): String {
        if (readingsText.isBlank()) return ""
        if (explicitRomaji.isNotBlank()) {
            return "$readingsText ($explicitRomaji)"
        }
        val items = readingsText.split(",", "、", "/", "・").map { it.trim() }.filter { it.isNotBlank() }
        if (items.isEmpty()) return readingsText
        return items.joinToString(", ") { item ->
            val cleanKana = item.replace("-", "").replace("・", "")
            val rom = toRomaji(cleanKana)
            if (rom.isNotBlank()) "$item ($rom)" else item
        }
    }
}
