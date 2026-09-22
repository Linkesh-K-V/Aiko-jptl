package com.example.noignore.japanese.data

import com.example.noignore.japanese.model.GrammarFormBreakdown
import com.example.noignore.japanese.model.KanjiMnemonicBreakdown
import com.example.noignore.japanese.model.KanjiRadicalPart
import com.example.noignore.japanese.model.WordCollocation

/**
 * Deep study material enrichment repository:
 * - Radical and Mnemonic deconstruction for core Kanji (Wanikani / Heisig style)
 * - Common word collocations with pitch accent hints
 * - Bunpro-style grammar breakdowns (formation formula, nuance, register, traps to avoid)
 */
object StudyMaterialEnrichmentRepository {

    private val mnemonicsMap: Map<String, KanjiMnemonicBreakdown> = mapOf(
        "日" to KanjiMnemonicBreakdown(
            kanji = "日",
            components = listOf(
                KanjiRadicalPart("日", "ひ (hi)", "Sun / Day", "Full enclosure")
            ),
            mnemonicStory = "A classic window framing the bright morning SUN. Look out through the window pane as a brand new DAY begins.",
            strokeOrderTip = "Top-to-bottom, left-to-right box closure: Left spine down, right corner down, middle bar, then seal the floor.",
            confusableLookalikes = listOf("白", "目", "田"),
            confusableNote = "日 (sun) has 1 horizontal line inside. 目 (eye) has 2 horizontal lines. 白 (white) has a drop on top."
        ),
        "月" to KanjiMnemonicBreakdown(
            kanji = "月",
            components = listOf(
                KanjiRadicalPart("月", "つき (tsuki)", "Moon / Month", "Left or full")
            ),
            mnemonicStory = "A CRESCENT MOON hanging in the twilight sky, with gentle wisps of night clouds drifting across its face.",
            strokeOrderTip = "Left sweeping crescent first, followed by top-and-right hanging hook.",
            confusableLookalikes = listOf("日", "明", "朋"),
            confusableNote = "月 curves to the left at the bottom, unlike the rigid straight box of 日."
        ),
        "水" to KanjiMnemonicBreakdown(
            kanji = "水",
            components = listOf(
                KanjiRadicalPart("氵", "さんずい (sanzui)", "Water drops", "Left side (偏)")
            ),
            mnemonicStory = "A rushing waterfall in the center splashing cool droplets to the left and right sides.",
            strokeOrderTip = "Vertical center spine with bottom hook FIRST! Then upper left, lower left, and right slope.",
            confusableLookalikes = listOf("氷", "永"),
            confusableNote = "氷 (koori - ice) has an extra drop on the top-left! Water (水) doesn't have that ice crystal."
        ),
        "火" to KanjiMnemonicBreakdown(
            kanji = "火",
            components = listOf(
                KanjiRadicalPart("火", "ひ (hi)", "Fire", "Stand-alone / Left"),
                KanjiRadicalPart("灬", "れんが (renga)", "Fire flames", "Bottom (脚)")
            ),
            mnemonicStory = "A blazing campfire where a person (人) jumps out of the way as bright sparks shoot off both sides!",
            strokeOrderTip = "Outer sparks first (left dot, right dot), then the tall person-shaped flame in the center.",
            confusableLookalikes = listOf("人", "大", "犬"),
            confusableNote = "Don't confuse with 人 (person) or 犬 (dog with collar dot). Notice the dual flame sparks!"
        ),
        "木" to KanjiMnemonicBreakdown(
            kanji = "木",
            components = listOf(
                KanjiRadicalPart("木", "き (ki)", "Tree / Wood", "Stand-alone / Left")
            ),
            mnemonicStory = "A mighty TREE showing its trunk, outstretched leafy branches above, and deep roots anchoring below into the earth.",
            strokeOrderTip = "Horizontal branch first, then the deep vertical trunk, followed by left roots and right roots.",
            confusableLookalikes = listOf("本", "禾", "休"),
            confusableNote = "本 (book/origin) puts a horizontal cut line across the roots! 休 shows a person resting against a tree."
        ),
        "金" to KanjiMnemonicBreakdown(
            kanji = "金",
            components = listOf(
                KanjiRadicalPart("人", "ひと (hito)", "Roof / Cover", "Top (冠)"),
                KanjiRadicalPart("王", "おう (ou)", "King / Valuable", "Interior"),
                KanjiRadicalPart("丷", "点 (ten)", "Gold nuggets / Drops", "Bottom interior")
            ),
            mnemonicStory = "Under the protective ROOF of the mountain mine, the KING guards glowing GOLD nuggets buried deep inside the earth.",
            strokeOrderTip = "Roof stroke first (left-right sweep), horizontal bars, spine, then the two gold nuggets, sealed by bottom bar.",
            confusableLookalikes = listOf("全", "会", "銀"),
            confusableNote = "全 (zen - all) lacks the gold nuggets inside! 銀 (gin - silver) has 金 on the left."
        ),
        "土" to KanjiMnemonicBreakdown(
            kanji = "土",
            components = listOf(
                KanjiRadicalPart("土", "つち (tsuchi)", "Soil / Earth", "Stand-alone / Left")
            ),
            mnemonicStory = "A fresh green sprout pushing up through the rich, fertile SOIL of the garden.",
            strokeOrderTip = "Short horizontal bar on top, vertical stem, then a LONGER bottom ground line.",
            confusableLookalikes = listOf("士", "干", "十"),
            confusableNote = "CRITICAL: 土 has a SHORTER top line and LONGER bottom line. 士 (samurai/scholar) has a LONGER top line!"
        ),
        "本" to KanjiMnemonicBreakdown(
            kanji = "本",
            components = listOf(
                KanjiRadicalPart("木", "き (ki)", "Tree", "Base frame"),
                KanjiRadicalPart("一", "いち (ichi)", "Cut line / Root marker", "Lower cross")
            ),
            mnemonicStory = "A TREE (木) with a marker at the trunk indicating its ORIGIN and roots. Wood pulp from trees is where BOOKS are born.",
            strokeOrderTip = "Write 木 completely first, then add the lower horizontal root marker bar.",
            confusableLookalikes = listOf("木", "休", "体"),
            confusableNote = "Look at the extra line across the roots of 木. That turns a tree into a book/origin."
        ),
        "人" to KanjiMnemonicBreakdown(
            kanji = "人",
            components = listOf(
                KanjiRadicalPart("人", "ひと (hito)", "Person / Human", "Stand-alone")
            ),
            mnemonicStory = "Two legs of a determined PERSON striding forward into their future.",
            strokeOrderTip = "Left sweeping leg first, then the right supporting leg joining midway down.",
            confusableLookalikes = listOf("入", "八"),
            confusableNote = "CRITICAL: In 人 (person), the LEFT stroke begins highest. In 入 (enter), the RIGHT stroke extends above the left!"
        ),
        "学" to KanjiMnemonicBreakdown(
            kanji = "学",
            components = listOf(
                KanjiRadicalPart("冖", "わかんむり (wakanmuri)", "School roof", "Middle"),
                KanjiRadicalPart("子", "こ (ko)", "Child", "Bottom (脚)")
            ),
            mnemonicStory = "Little hands waving excitedly atop the school ROOF where a bright young CHILD (子) is LEARNING new knowledge.",
            strokeOrderTip = "Three dots on top, then the roof crown, and finally the child (子) on the bottom.",
            confusableLookalikes = listOf("字", "学校", "教"),
            confusableNote = "字 (character) only has a single dot on top of the roof. 学 has 3 energetic study sparks!"
        ),
        "持" to KanjiMnemonicBreakdown(
            kanji = "持",
            components = listOf(
                KanjiRadicalPart("扌", "てへん (tehen)", "Hand", "Left side (偏)"),
                KanjiRadicalPart("寺", "てら (tera)", "Temple", "Right side (旁)")
            ),
            mnemonicStory = "With your HAND (扌), you carefully HOLD and carry the sacred incense into the peaceful TEMPLE (寺).",
            strokeOrderTip = "Hand radical on the left first (horizontal, vertical hook, upward sweep), then the temple on the right.",
            confusableLookalikes = listOf("待", "特", "詩"),
            confusableNote = "持 has 扌 (hand) = to HOLD. 待 has 彳 (steps/walking) = to WAIT. 特 has 牛 (cow) = SPECIAL."
        ),
        "待" to KanjiMnemonicBreakdown(
            kanji = "待",
            components = listOf(
                KanjiRadicalPart("彳", "ぎょうにんべん (gyouninben)", "Walking footsteps", "Left side (偏)"),
                KanjiRadicalPart("寺", "てら (tera)", "Temple", "Right side (旁)")
            ),
            mnemonicStory = "You pace your FOOTSTEPS (彳) outside the quiet TEMPLE (寺) gates while WAITING for your friend to arrive.",
            strokeOrderTip = "Two left diagonal steps first, then vertical spine, then temple (寺).",
            confusableLookalikes = listOf("持", "特", "行"),
            confusableNote = "Left side is 彳 (footsteps) -> patience to WAIT. Don't confuse with 扌 (hand) in 持!"
        )
    )

    private val collocationsMap: Map<String, List<WordCollocation>> = mapOf(
        "雨" to listOf(
            WordCollocation("雨が降る", "あめがふる", "ame ga furu", "It rains / Rain falls", "Heiban (平板) [0]"),
            WordCollocation("雨が止む", "あめがやむ", "ame ga yamu", "The rain stops", "Atamadaka (頭高) [1]"),
            WordCollocation("傘を差す", "かさをさす", "kasa o sasu", "To open / put up an umbrella", "Nakadaka (中高) [2]")
        ),
        "風邪" to listOf(
            WordCollocation("風邪をひく", "かぜをひく", "kaze o hiku", "To catch a cold", "Heiban (平板) [0]"),
            WordCollocation("風邪が治る", "かぜがなおる", "kaze ga naoru", "To recover from a cold", "Nakadaka (中高) [3]")
        ),
        "風呂" to listOf(
            WordCollocation("お風呂に入る", "おふろにはいる", "ofuro ni hairu", "To take a bath", "Nakadaka (中高) [2]"),
            WordCollocation("お湯を沸かす", "おゆをわかす", "oyu o wakasu", "To boil bath water / kettle", "Heiban (平板) [0]")
        ),
        "気" to listOf(
            WordCollocation("気をつける", "きをつける", "ki o tsukeru", "To take care / be careful", "Heiban (平板) [0]"),
            WordCollocation("気に入る", "きにいる", "ki ni iru", "To take a liking to / please", "Nakadaka (中高) [0]"),
            WordCollocation("気がする", "きがする", "ki ga suru", "To have a hunch / feel like", "Heiban (平板) [0]")
        ),
        "時間" to listOf(
            WordCollocation("時間に間に合う", "じかんにまにあう", "jikan ni maniau", "To be on time", "Nakadaka (中高) [3]"),
            WordCollocation("時間を守る", "じかんをまもる", "jikan o mamoru", "To be punctual / keep time", "Heiban (平板) [0]")
        ),
        "約束" to listOf(
            WordCollocation("約束を守る", "やくそくをまもる", "yakusoku o mamoru", "To keep a promise", "Heiban (平板) [0]"),
            WordCollocation("約束を破る", "やくそくをやぶる", "yakusoku o yaburu", "To break a promise", "Nakadaka (中高) [3]")
        )
    )

    private val grammarBreakdownsMap: Map<String, GrammarFormBreakdown> = mapOf(
        "g_n4_1" to GrammarFormBreakdown(
            pattern = "〜られる (Potential)",
            formationFormula = "Ichidan: Verb stem + られる (食べる → 食べられる)\nGodan: u-sound → e-sound + る (話す → 話せる, 飲む → 飲める)\nIrregular: くる → こられる, する → できる",
            politenessRegister = "Neutral / Conjugates like regular Ichidan verb",
            nuanceNotes = "Refers to innate or situational ability (can do). In modern casual Japanese, 'ra-nuki' (ら抜き言葉) is common for Ichidan: 食べれる instead of 食べられる.",
            commonMistakesToAvoid = "Particle change! The direct object particle を often changes to が with potential verbs: 日本語【を】話す → 日本語【が】話せる.",
            realWorldExample = "漢字が少し読めるようになりました。",
            realWorldReading = "かんじがすこしよめるようになりました。",
            realWorldEnglish = "I became able to read a little bit of kanji."
        ),
        "g_n4_3" to GrammarFormBreakdown(
            pattern = "〜たことがある",
            formationFormula = "Verb [Past Plain た-form] + ことがある (行ったことがある, 見たことがある)",
            politenessRegister = "Neutral base (add です or あります for polite speech)",
            nuanceNotes = "Expresses life experience ('have had the experience of'). NOT used for mundane routine events that happened this morning (use standard past tense instead).",
            commonMistakesToAvoid = "Don't use with recent single events like 'I had breakfast this morning'. Use it for milestones: 'Have you ever climbed Mt. Fuji?' (富士山に登ったことがありますか).",
            realWorldExample = "一度も納豆を食べたことがありません。",
            realWorldReading = "いちどもなっとうをたべたことがありません。",
            realWorldEnglish = "I have never eaten natto even once in my life."
        ),
        "g_n4_4" to GrammarFormBreakdown(
            pattern = "〜すぎる",
            formationFormula = "Verb stem + すぎる (飲みすぎる)\nい-Adj (drop い) + すぎる (高すぎる)\nな-Adj (drop な) + すぎる (静かすぎる)",
            politenessRegister = "Casual stem; inflects as Ichidan verb (すぎます, すぎた)",
            nuanceNotes = "Carries an inherent negative connotation: doing something to excess beyond moderate healthy limits.",
            commonMistakesToAvoid = "Don't say 美味しすぎる in formal business writing if you just mean 'extremely delicious' (use 大変美味しい instead).",
            realWorldExample = "昨日、お酒を飲みすぎました。",
            realWorldReading = "きのう、おさけをのみすぎました。",
            realWorldEnglish = "Yesterday, I drank too much alcohol."
        ),
        "g_n3_1" to GrammarFormBreakdown(
            pattern = "〜はずだ",
            formationFormula = "Verb plain form + はずだ\nい-Adj plain + はずだ\nな-Adj + な + はずだ\nNoun + の + はずだ",
            politenessRegister = "Standard / Objective assertion",
            nuanceNotes = "Expresses strong natural expectation based on objective evidence or logical deductions, not just a subjective guess.",
            commonMistakesToAvoid = "For negative expectations, either use ないはずだ or はずがない (the latter is far stronger: 'there is no way that...!').",
            realWorldExample = "彼は昨日連絡があったから、今日来るはずです。",
            realWorldReading = "かれはきのうれんらくがあったから、きょうくるはずです。",
            realWorldEnglish = "Since he contacted me yesterday, he is expected to come today."
        ),
        "g_n3_2" to GrammarFormBreakdown(
            pattern = "〜わけにはいかない",
            formationFormula = "Verb [Dict Form] + わけにはいかない (休むわけにはいかない)\nVerb [Negative ない-form] + わけにはいかない (行かないわけにはいかない = must go)",
            politenessRegister = "Formal / Serious conversational tone",
            nuanceNotes = "Social, moral, or psychological constraint prevents doing something, even if you technically have the physical ability.",
            commonMistakesToAvoid = "Don't confuse with できない (physical impossibility). わけにはいかない means 'I want to/can do it physically, but morally/socially I must not'.",
            realWorldExample = "大事な顧客との約束なので、遅刻するわけにはいかない。",
            realWorldReading = "だいじなこきゃくとのやくそくなので、ちこくするわけにはいかない。",
            realWorldEnglish = "Since it's an appointment with an important client, I cannot afford to be late."
        )
    )

    fun getMnemonicForKanji(kanji: String): KanjiMnemonicBreakdown? {
        return mnemonicsMap[kanji]
    }

    fun getCollocationsForWord(word: String): List<WordCollocation> {
        return collocationsMap.entries.firstOrNull { (key, _) -> word.contains(key) }?.value ?: emptyList()
    }

    fun getGrammarBreakdown(itemId: String): GrammarFormBreakdown? {
        return grammarBreakdownsMap[itemId]
    }
}
