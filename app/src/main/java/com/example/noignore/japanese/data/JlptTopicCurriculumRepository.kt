package com.example.noignore.japanese.data

import android.content.Context
import android.content.SharedPreferences
import com.example.noignore.japanese.model.JlptExamLevel
import com.example.noignore.japanese.model.JlptTopicCategory
import com.example.noignore.japanese.model.JlptTopicItem
import com.example.noignore.japanese.model.JlptTopicUnit

class JlptTopicCurriculumRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("jlpt_topic_curriculum_prefs", Context.MODE_PRIVATE)

    fun isTopicUnderstood(topicId: String): Boolean {
        return prefs.getBoolean("topic_understood_$topicId", false)
    }

    fun setTopicUnderstood(topicId: String, understood: Boolean) {
        prefs.edit().putBoolean("topic_understood_$topicId", understood).apply()
    }

    fun toggleTopicUnderstood(topicId: String): Boolean {
        val newState = !isTopicUnderstood(topicId)
        setTopicUnderstood(topicId, newState)
        return newState
    }

    fun getUnderstoodCount(level: JlptExamLevel): Int {
        val topics = getTopicsForLevel(level)
        return topics.count { isTopicUnderstood(it.id) }
    }

    fun getTotalCount(level: JlptExamLevel): Int {
        return getTopicsForLevel(level).size
    }

    fun getTopicsForLevel(level: JlptExamLevel): List<JlptTopicUnit> {
        return allTopicUnits.filter { it.level == level }
    }

    fun getTopicById(id: String): JlptTopicUnit? {
        return allTopicUnits.find { it.id == id }
    }

    fun getTopicsByCategory(level: JlptExamLevel, category: JlptTopicCategory): List<JlptTopicUnit> {
        val levelTopics = getTopicsForLevel(level)
        return if (category == JlptTopicCategory.ALL) {
            levelTopics
        } else {
            levelTopics.filter { it.category == category }
        }
    }

    fun searchTopics(level: JlptExamLevel, query: String): List<JlptTopicUnit> {
        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) return getTopicsForLevel(level)
        return getTopicsForLevel(level).filter { unit ->
            unit.title.lowercase().contains(trimmed) ||
            unit.japaneseTitle.contains(trimmed) ||
            unit.overview.lowercase().contains(trimmed) ||
            unit.items.any { item ->
                item.japanese.contains(trimmed) ||
                item.reading.contains(trimmed) ||
                item.meaning.lowercase().contains(trimmed) ||
                item.romaji.lowercase().contains(trimmed)
            }
        }
    }

    // =========================================================================
    // COMPREHENSIVE CURRICULUM TOPICS CATALOG FOR ALL LEVELS (N5 - N1)
    // =========================================================================
    val allTopicUnits: List<JlptTopicUnit> = listOf(
        // =====================================================================
        // JLPT N5 TOPICS (FOUNDATIONS)
        // =====================================================================
        JlptTopicUnit(
            id = "n5_particles_core",
            level = JlptExamLevel.N5,
            category = JlptTopicCategory.GRAMMAR,
            title = "Core N5 Particles (助詞)",
            japaneseTitle = "基本の助詞 (は・が・を・に・で)",
            overview = "Particles are grammatical markers attached after nouns to specify their role: topic, subject, object, destination, or location.",
            senseiTip = "Teacher Aiko: Think of particles like glue! Master the difference between は (topic/spotlight) and が (subject identifier), and you will instantly pass N5!",
            items = listOf(
                JlptTopicItem(
                    id = "n5_p_wa",
                    japanese = "は",
                    reading = "わ",
                    romaji = "wa",
                    meaning = "Topic marker (As for X...)",
                    patternFormula = "[Noun] + は + [Comment/Predicate]",
                    explanation = "Written with the Hiragana 'ha' (は), but pronounced 'wa'. It sets the conversation topic or spotlight.",
                    exampleJapanese = "私は学生です。",
                    exampleReading = "わたしはがくせいです。",
                    exampleEnglish = "As for me, I am a student.",
                    mnemonicTip = "Remember: Written 'ha', pronounced 'wa'!"
                ),
                JlptTopicItem(
                    id = "n5_p_ga",
                    japanese = "が",
                    reading = "が",
                    romaji = "ga",
                    meaning = "Subject identifier / Specific focus",
                    patternFormula = "[Subject] + が + [Verb/Adjective]",
                    explanation = "Marks the specific actor of an action, or what is liked/wanted/capable (好き、欲しい、上手).",
                    exampleJapanese = "猫が好きです。",
                    exampleReading = "ねこがすきです。",
                    exampleEnglish = "I like cats (Cats are pleasing to me).",
                    mnemonicTip = "Use が with 好き (like), 嫌い (dislike), and 欲しい (want)!"
                ),
                JlptTopicItem(
                    id = "n5_p_wo",
                    japanese = "を",
                    reading = "お",
                    romaji = "o / wo",
                    meaning = "Direct Object marker",
                    patternFormula = "[Object] + を + [Transitive Verb]",
                    explanation = "Written with the special Hiragana 'wo' (を), pronounced 'o'. Points directly to what receives the action.",
                    exampleJapanese = "パンを食べます。",
                    exampleReading = "パンをたべます。",
                    exampleEnglish = "I eat bread.",
                    mnemonicTip = "Whatever you eat, drink, read, or buy takes を!"
                ),
                JlptTopicItem(
                    id = "n5_p_ni",
                    japanese = "に",
                    reading = "に",
                    romaji = "ni",
                    meaning = "Target time, location of existence, destination",
                    patternFormula = "[Time/Place] + に + [Verb]",
                    explanation = "Pinpoints a specific moment on the clock or calendar, a place where someone/something exists (います/あります), or where you are headed.",
                    exampleJapanese = "七時に起きます。",
                    exampleReading = "しちじにおきます。",
                    exampleEnglish = "I wake up at 7 o'clock.",
                    mnemonicTip = "Specific numerical times always take に!"
                ),
                JlptTopicItem(
                    id = "n5_p_de",
                    japanese = "で",
                    reading = "で",
                    romaji = "de",
                    meaning = "Location of active action / Means & tool",
                    patternFormula = "[Place/Tool] + で + [Action Verb]",
                    explanation = "Indicates WHERE an action takes place, or HOW/BY WHAT MEANS you do it (by bus, with chopsticks, in Japanese).",
                    exampleJapanese = "図書館で勉強します。",
                    exampleReading = "としょかんでべんきょうします。",
                    exampleEnglish = "I study at the library.",
                    mnemonicTip = "Location of living/existing is に; location of DOING is で!"
                ),
                JlptTopicItem(
                    id = "n5_p_e",
                    japanese = "へ",
                    reading = "え",
                    romaji = "e",
                    meaning = "Direction / Towards",
                    patternFormula = "[Destination] + へ + 行きます / 来ます / 帰ります",
                    explanation = "Written with 'he' (へ), pronounced 'e'. Emphasizes direction towards a destination.",
                    exampleJapanese = "日本へ行きます。",
                    exampleReading = "にほんへいきます。",
                    exampleEnglish = "I am going to Japan.",
                    mnemonicTip = "Pronounced 'e'! Followed by movement verbs."
                ),
                JlptTopicItem(
                    id = "n5_p_mo",
                    japanese = "も",
                    reading = "も",
                    romaji = "mo",
                    meaning = "Also / Too (Replaces は/が/を)",
                    patternFormula = "[Noun] + も + [Predicate]",
                    explanation = "Replaces は, が, or を to mean 'also' or 'neither'.",
                    exampleJapanese = "私も行きます。",
                    exampleReading = "わたしもいきます。",
                    exampleEnglish = "I will go too.",
                    mnemonicTip = "Replaces は! Never say '私はも'."
                ),
                JlptTopicItem(
                    id = "n5_p_to",
                    japanese = "と",
                    reading = "と",
                    romaji = "to",
                    meaning = "And (exhaustive list) / With someone",
                    patternFormula = "[Noun A] + と + [Noun B] / [Person] + と",
                    explanation = "Connects nouns in a complete list, or denotes doing an action together with a companion.",
                    exampleJapanese = "友達と話します。",
                    exampleReading = "ともだちとはなします。",
                    exampleEnglish = "I talk with a friend.",
                    mnemonicTip = "Connects nouns ONLY, not entire sentences!"
                )
            )
        ),

        JlptTopicUnit(
            id = "n5_verb_conjugations",
            level = JlptExamLevel.N5,
            category = JlptTopicCategory.GRAMMAR,
            title = "N5 Polite Verb Conjugations (ます形)",
            japaneseTitle = "動詞の活用 (ます・ません・ました・て形)",
            overview = "Verbs are the engine of Japanese sentences. In N5, polite speech (丁寧語 - ます style) is standard.",
            senseiTip = "Teacher Aiko: Once you know the verb stem, adding ます (present), ません (negative), ました (past), and ませんでした (past negative) gives you 4 sentences in 1 minute!",
            items = listOf(
                JlptTopicItem(
                    id = "n5_v_masu",
                    japanese = "〜ます",
                    reading = "ます",
                    romaji = "-masu",
                    meaning = "Polite Present / Future affirmative",
                    patternFormula = "[Verb Stem] + ます",
                    explanation = "Standard polite form for current habits or future scheduled actions.",
                    exampleJapanese = "毎日日本語を勉強します。",
                    exampleReading = "まいにちにほんごをべんきょうします。",
                    exampleEnglish = "I study Japanese every day / I will study Japanese.",
                    mnemonicTip = "Used for both today's habits and tomorrow's plans!"
                ),
                JlptTopicItem(
                    id = "n5_v_masen",
                    japanese = "〜ません",
                    reading = "ません",
                    romaji = "-masen",
                    meaning = "Polite Present / Future negative (Don't / Won't)",
                    patternFormula = "[Verb Stem] + ません",
                    explanation = "Polite way to say you don't do something or will not do it.",
                    exampleJapanese = "お酒を飲みません。",
                    exampleReading = "おさけをのみません。",
                    exampleEnglish = "I do not drink alcohol.",
                    mnemonicTip = "Replace ます with ません for negative!"
                ),
                JlptTopicItem(
                    id = "n5_v_mashita",
                    japanese = "〜ました",
                    reading = "ました",
                    romaji = "-mashita",
                    meaning = "Polite Past affirmative (Did)",
                    patternFormula = "[Verb Stem] + ました",
                    explanation = "Completed actions in the past.",
                    exampleJapanese = "昨日宿題をしました。",
                    exampleReading = "きのうしゅくだいをしました。",
                    exampleEnglish = "I did my homework yesterday.",
                    mnemonicTip = "Replace ます with ました for completed past!"
                ),
                JlptTopicItem(
                    id = "n5_v_te_form",
                    japanese = "〜てください",
                    reading = "てください",
                    romaji = "-te kudasai",
                    meaning = "Please do ~ (Polite request)",
                    patternFormula = "[Verb Te-Form] + ください",
                    explanation = "Used to politely ask someone to perform an action.",
                    exampleJapanese = "黒板を見てください。",
                    exampleReading = "こくばんをみてください。",
                    exampleEnglish = "Please look at the blackboard.",
                    mnemonicTip = "Te-form + ください = Polite request."
                ),
                JlptTopicItem(
                    id = "n5_v_te_iru",
                    japanese = "〜ています",
                    reading = "ています",
                    romaji = "-te imasu",
                    meaning = "Ongoing action (-ing) / Resulting state",
                    patternFormula = "[Verb Te-Form] + います",
                    explanation = "Expresses an action currently happening (eating, studying), or a state of being (living in Tokyo, being married).",
                    exampleJapanese = "今ご飯を食べています。",
                    exampleReading = "いまごはんをたべています。",
                    exampleEnglish = "I am eating a meal right now.",
                    mnemonicTip = "Ongoing present continuous!"
                ),
                JlptTopicItem(
                    id = "n5_v_tai",
                    japanese = "〜たいです",
                    reading = "たいです",
                    romaji = "-tai desu",
                    meaning = "Want to do ~ (Personal desire)",
                    patternFormula = "[Verb Stem] + たいです",
                    explanation = "Expresses the speaker's personal desire to do something.",
                    exampleJapanese = "日本へ行きたいです。",
                    exampleReading = "にほんへいきたいです。",
                    exampleEnglish = "I want to go to Japan.",
                    mnemonicTip = "Use only for your own desires, not for other people!"
                )
            )
        ),

        JlptTopicUnit(
            id = "n5_kanji_foundations",
            level = JlptExamLevel.N5,
            category = JlptTopicCategory.KANJI,
            title = "N5 Foundational Kanji (~100 Core Characters)",
            japaneseTitle = "N5の基礎漢字 (数字・自然・人間・方向)",
            overview = "N5 tests around 100 high-frequency pictograms and ideograms covering numbers, nature elements, people, and directions.",
            senseiTip = "Teacher Aiko: Look at kanji as pictures! 日 is the sun with a ray inside, 月 is the crescent moon, and 木 is a tree with roots and branches!",
            items = listOf(
                JlptTopicItem(
                    id = "n5_k_sun",
                    japanese = "日",
                    reading = "ひ / ニチ / ニ",
                    romaji = "hi / nichi / ni",
                    meaning = "Sun, Day, Japan",
                    explanation = "Depicts the sun with a sunspot or core ray. Used in calendar days, days of the week, and country names (日本).",
                    exampleJapanese = "日曜日は休みです。",
                    exampleReading = "にちようびはやすみです。",
                    exampleEnglish = "Sunday is a day off.",
                    mnemonicTip = "A window or frame viewing the shining sun!"
                ),
                JlptTopicItem(
                    id = "n5_k_moon",
                    japanese = "月",
                    reading = "つき / ゲツ / ガツ",
                    romaji = "tsuki / getsu / gatsu",
                    meaning = "Moon, Month",
                    explanation = "Depicts a crescent moon behind clouds. Used in months of the year (一月 = January) and Monday (月曜日).",
                    exampleJapanese = "今月は忙しいです。",
                    exampleReading = "こんげつはいそがしいです。",
                    exampleEnglish = "This month is busy.",
                    mnemonicTip = "Crescent moon shining in the night sky."
                ),
                JlptTopicItem(
                    id = "n5_k_tree",
                    japanese = "木",
                    reading = "き / モク",
                    romaji = "ki / moku",
                    meaning = "Tree, Wood",
                    explanation = "Shows trunk, branches spreading up, and roots spreading into the soil.",
                    exampleJapanese = "公園に大きな木があります。",
                    exampleReading = "こうえんにおおきなきがあります。",
                    exampleEnglish = "There is a large tree in the park.",
                    mnemonicTip = "A trunk with branches above and roots below."
                ),
                JlptTopicItem(
                    id = "n5_k_water",
                    japanese = "水",
                    reading = "みず / スイ",
                    romaji = "mizu / sui",
                    meaning = "Water",
                    explanation = "Rippling stream of water flowing between riverbanks.",
                    exampleJapanese = "冷たい水を飲みます。",
                    exampleReading = "つめたいみずをのみます。",
                    exampleEnglish = "I drink cold water.",
                    mnemonicTip = "Water flowing down a central stream."
                ),
                JlptTopicItem(
                    id = "n5_k_fire",
                    japanese = "火",
                    reading = "ひ / カ",
                    romaji = "hi / ka",
                    meaning = "Fire",
                    explanation = "Sparks dancing upwards from a central flame.",
                    exampleJapanese = "火曜日にテストがあります。",
                    exampleReading = "かようびにてすとがあります。",
                    exampleEnglish = "There is a test on Tuesday.",
                    mnemonicTip = "Sparks flying up from a campfire!"
                ),
                JlptTopicItem(
                    id = "n5_k_person",
                    japanese = "人",
                    reading = "ひと / ジン / ニン",
                    romaji = "hito / jin / nin",
                    meaning = "Person, Human, Nationality",
                    explanation = "Two legs of a human standing firm. Suffixed to countries for nationality (日本人 = Japanese person).",
                    exampleJapanese = "あの人は誰ですか。",
                    exampleReading = "あのひとはだれですか。",
                    exampleEnglish = "Who is that person?",
                    mnemonicTip = "Two legs walking forward!"
                ),
                JlptTopicItem(
                    id = "n5_k_mountain",
                    japanese = "山",
                    reading = "やま / サン",
                    romaji = "yama / san",
                    meaning = "Mountain",
                    explanation = "Three peaks of a mountain range rising together.",
                    exampleJapanese = "富士山はとても高いです。",
                    exampleReading = "ふじさんはとてもたかいです。",
                    exampleEnglish = "Mount Fuji is very tall.",
                    mnemonicTip = "Three mountain peaks side-by-side."
                ),
                JlptTopicItem(
                    id = "n5_k_river",
                    japanese = "川",
                    reading = "かわ / ガワ",
                    romaji = "kawa / gawa",
                    meaning = "River, Stream",
                    explanation = "Three vertical lines representing three streams of flowing water.",
                    exampleJapanese = "川で魚を釣ります。",
                    exampleReading = "かわでさかなをつります。",
                    exampleEnglish = "I catch fish at the river.",
                    mnemonicTip = "Water flowing smoothly down three river channels."
                )
            )
        ),

        JlptTopicUnit(
            id = "n5_adjectives",
            level = JlptExamLevel.N5,
            category = JlptTopicCategory.GRAMMAR,
            title = "N5 Adjectives (い-Adjectives & な-Adjectives)",
            japaneseTitle = "形容詞 (い形容詞・な形容詞の活用)",
            overview = "Japanese has two distinct adjective families with different conjugation rules for negative and past tense.",
            senseiTip = "Teacher Aiko: Notice how い-adjectives drop the 'い' and become 〜くない (negative) and 〜かった (past), while な-adjectives use じゃありません / でした just like nouns!",
            items = listOf(
                JlptTopicItem(
                    id = "n5_adj_i_present",
                    japanese = "〜いです / 〜くないです",
                    reading = "いです / くないです",
                    romaji = "-i desu / -kunai desu",
                    meaning = "i-Adjective Present (Positive / Negative)",
                    patternFormula = "[Drop い] + くないです",
                    explanation = "い-adjectives conjugate directly. To make negative, replace the final い with くない.",
                    exampleJapanese = "この本は高くないです。",
                    exampleReading = "このほんはたかくないです。",
                    exampleEnglish = "This book is not expensive.",
                    mnemonicTip = "高い (takai) -> 高くない (takakunai)!"
                ),
                JlptTopicItem(
                    id = "n5_adj_i_past",
                    japanese = "〜かったです / 〜くなかったです",
                    reading = "かったです / くなかったです",
                    romaji = "-katta desu / -kunakatta desu",
                    meaning = "i-Adjective Past (Was / Was not)",
                    patternFormula = "[Drop い] + かったです / くなかったです",
                    explanation = "To say something was past, replace い with かった.",
                    exampleJapanese = "昨日はとても寒かったです。",
                    exampleReading = "きのうはとてもさむかったです。",
                    exampleEnglish = "Yesterday was very cold.",
                    mnemonicTip = "寒い (samui) -> 寒かった (samukatta)!"
                ),
                JlptTopicItem(
                    id = "n5_adj_na",
                    japanese = "〜な [名詞] / 〜です / 〜じゃないです",
                    reading = "な [めいし]",
                    romaji = "-na [noun]",
                    meaning = "na-Adjective rules",
                    patternFormula = "[na-Adjective] + な + [Noun] / [na-Adjective] + です",
                    explanation = "Before a noun, attach な (e.g. 静かな町 = quiet town). At sentence end, conjugate like nouns (静かでした = was quiet).",
                    exampleJapanese = "京都は静かで綺麗な町です。",
                    exampleReading = "きょうとはしずかできれいなまちです。",
                    exampleEnglish = "Kyoto is a quiet and beautiful town.",
                    mnemonicTip = "Remember: きれい (kirei) and ゆうめい (yuumei) are な-adjectives despite ending in 'i' sound!"
                )
            )
        ),

        JlptTopicUnit(
            id = "n5_daily_vocab_themes",
            level = JlptExamLevel.N5,
            category = JlptTopicCategory.VOCABULARY,
            title = "N5 Essential Vocabulary Themes",
            japaneseTitle = "N5必須の生活単語 (挨拶・時間・場所・家族)",
            overview = "Core high-frequency words for daily survival: time indicators, family members, food, and common locations.",
            senseiTip = "Teacher Aiko: Learn vocabulary in thematic pairs: 今日 (today) - 明日 (tomorrow), 朝 (morning) - 夜 (night), 駅 (station) - 病院 (hospital)!",
            items = listOf(
                JlptTopicItem(
                    id = "n5_v_today",
                    japanese = "今日",
                    reading = "きょう",
                    romaji = "kyou",
                    meaning = "Today",
                    explanation = "Essential time word. Does not take particle に when used as an adverb.",
                    exampleJapanese = "今日はいい天気ですね。",
                    exampleReading = "きょうはいいてんきですね。",
                    exampleEnglish = "It is nice weather today, isn't it?",
                    mnemonicTip = "Combination of 'now' (今) and 'day' (日)."
                ),
                JlptTopicItem(
                    id = "n5_v_tomorrow",
                    japanese = "明日",
                    reading = "あした / みょうにち",
                    romaji = "ashita",
                    meaning = "Tomorrow",
                    explanation = "Bright (明) day (日). Very common in daily planning.",
                    exampleJapanese = "明日テストがあります。",
                    exampleReading = "あしたてすとがあります。",
                    exampleEnglish = "There is a test tomorrow.",
                    mnemonicTip = "Tomorrow is a bright (明) day (日)!"
                ),
                JlptTopicItem(
                    id = "n5_v_station",
                    japanese = "駅",
                    reading = "えき",
                    romaji = "eki",
                    meaning = "Train Station",
                    explanation = "Central transport hub in Japan. The heart of daily commuting.",
                    exampleJapanese = "駅の前で会いましょう。",
                    exampleReading = "えきのまえであいましょう。",
                    exampleEnglish = "Let's meet in front of the station.",
                    mnemonicTip = "Horse radical on the left: where horses were changed in old days!"
                ),
                JlptTopicItem(
                    id = "n5_v_meal",
                    japanese = "ご飯",
                    reading = "ごはん",
                    romaji = "gohan",
                    meaning = "Cooked rice / Meal in general",
                    explanation = "Polite prefix ご + 飯 (rice). Used for breakfast (朝ご飯), lunch (昼ご飯), and dinner (晩ご飯).",
                    exampleJapanese = "一緒にご飯を食べませんか。",
                    exampleReading = "いっしょにごはんをたべませんか。",
                    exampleEnglish = "Won't you eat a meal together with me?",
                    mnemonicTip = "Rice is synonymous with 'meal' in Japan!"
                )
            )
        ),

        JlptTopicUnit(
            id = "n5_practical_expressions",
            level = JlptExamLevel.N5,
            category = JlptTopicCategory.EXPRESSIONS,
            title = "N5 Practical Everyday Expressions",
            japaneseTitle = "日常のあいさつ・注文・道案内",
            overview = "Natural polite expressions used in Japanese restaurants, shops, and everyday encounters.",
            senseiTip = "Teacher Aiko: Memorize '〜をお願いします' (please give me ~) and '〜はどこですか' (where is ~). You can travel anywhere in Japan with these two phrases!",
            items = listOf(
                JlptTopicItem(
                    id = "n5_exp_onegai",
                    japanese = "これをお願いします",
                    reading = "これをおねがいします",
                    romaji = "kore o onegai shimasu",
                    meaning = "This one, please (Ordering/Requesting)",
                    patternFormula = "[Item] + を + お願いします",
                    explanation = "Universal phrase at restaurants, ticket counters, and retail stores.",
                    exampleJapanese = "メニューを見ながら、「これをお願いします」。",
                    exampleReading = "めにゅーをみながら、「これをおねがいします」。",
                    exampleEnglish = "Looking at the menu: 'This one, please.'",
                    mnemonicTip = "Point and say: Kore o onegai shimasu!"
                ),
                JlptTopicItem(
                    id = "n5_exp_doko",
                    japanese = "〜はどこですか",
                    reading = "〜はどこですか",
                    romaji = "... wa doko desu ka",
                    meaning = "Where is ~ ?",
                    patternFormula = "[Location/Place] + は + どこですか",
                    explanation = "Asking for the location of restrooms, train stations, hotels, or items.",
                    exampleJapanese = "すみません、お手洗いはどこですか。",
                    exampleReading = "すみません、おてあらいはどこですか。",
                    exampleEnglish = "Excuse me, where is the restroom?",
                    mnemonicTip = "Doko = Where!"
                ),
                JlptTopicItem(
                    id = "n5_exp_ikura",
                    japanese = "これはいくらですか",
                    reading = "これはいくらですか",
                    romaji = "kore wa ikura desu ka",
                    meaning = "How much is this?",
                    patternFormula = "[Item] + は + いくらですか",
                    explanation = "Essential shopping question to ask the price of an item.",
                    exampleJapanese = "このペンはいくらですか。百円です。",
                    exampleReading = "このぺんはいくらですか。ひゃくえんです。",
                    exampleEnglish = "How much is this pen? It is 100 yen.",
                    mnemonicTip = "Ikura = How much (price)!"
                )
            )
        ),

        // =====================================================================
        // JLPT N4 TOPICS (ELEMENTARY)
        // =====================================================================
        JlptTopicUnit(
            id = "n4_potential_form",
            level = JlptExamLevel.N4,
            category = JlptTopicCategory.GRAMMAR,
            title = "Potential Form (可能形: Can / Able to)",
            japaneseTitle = "可能形 (〜(ら)れる・話せる・食べられる)",
            overview = "Expresses physical ability or situational possibility (can speak Japanese, can eat spicy food).",
            senseiTip = "Teacher Aiko: In N4 potential form, the object marker を often shifts to が! e.g. 漢字が読めます (I can read kanji)!",
            items = listOf(
                JlptTopicItem(
                    id = "n4_pot_group1",
                    japanese = "話せる / 読める / 行ける",
                    reading = "はなせる / よめる / いける",
                    romaji = "hanaseru / yomeru / ikeru",
                    meaning = "Group 1 Verbs: Shift 'u' sound to 'e' + る",
                    patternFormula = "話す (u) -> 話せる (eru)",
                    explanation = "Group 1 Godan verbs change their final syllable from u-row to e-row and add る.",
                    exampleJapanese = "日本語で日常会話が話せます。",
                    exampleReading = "にほんごでにちじょうかいわがはなせます。",
                    exampleEnglish = "I can speak everyday conversation in Japanese.",
                    mnemonicTip = "Shift 'u' to 'e': 話す -> 話せる!"
                ),
                JlptTopicItem(
                    id = "n4_pot_group2",
                    japanese = "食べられる / 見られる",
                    reading = "たべられる / みられる",
                    romaji = "taberareru / mirareru",
                    meaning = "Group 2 Verbs: Drop る + られる",
                    patternFormula = "[Stem] + られる",
                    explanation = "Group 2 Ichidan verbs drop る and add られる (or casually spoken ら抜き: 食べれる).",
                    exampleJapanese = "納豆が食べられますか。",
                    exampleReading = "なっとうがたべられますか。",
                    exampleEnglish = "Can you eat natto?",
                    mnemonicTip = "Drop る, add られる!"
                )
            )
        ),

        JlptTopicUnit(
            id = "n4_giving_receiving",
            level = JlptExamLevel.N4,
            category = JlptTopicCategory.GRAMMAR,
            title = "Giving & Receiving (授受表現: あげる・くれる・もらう)",
            japaneseTitle = "やりもらい (あげる・くれる・もらう)",
            overview = "Japanese perspective in giving and receiving depends strictly on who gives to whom relative to the speaker.",
            senseiTip = "Teacher Aiko: くれる is someone giving to ME; あげる is ME giving to someone; もらう is RECEIVING from someone!",
            items = listOf(
                JlptTopicItem(
                    id = "n4_gr_ageru",
                    japanese = "あげる / 〜てあげる",
                    reading = "あげる / てあげる",
                    romaji = "ageru / -te ageru",
                    meaning = "Give / Do something as a favor for someone",
                    patternFormula = "[Giver] が [Receiver] に [Thing] を あげる",
                    explanation = "I or in-group member gives outward to someone else.",
                    exampleJapanese = "友達に誕生日プレゼントをあげました。",
                    exampleReading = "ともだちにたんじょうびぷれぜんとをあげました。",
                    exampleEnglish = "I gave a birthday present to my friend.",
                    mnemonicTip = "Giving outwards from me!"
                ),
                JlptTopicItem(
                    id = "n4_gr_kureru",
                    japanese = "くれる / 〜てくれる",
                    reading = "くれる / てくれる",
                    romaji = "kureru / -te kureru",
                    meaning = "Someone gives to me / Does a favor for me",
                    patternFormula = "[Someone] が 私に [Thing] を くれる",
                    explanation = "Someone moves a gift or favor towards me or my family.",
                    exampleJapanese = "田中さんが傘を貸してくれました。",
                    exampleReading = "たなかさんがかさをかしてくれました。",
                    exampleEnglish = "Tanaka-san kindly lent me an umbrella.",
                    mnemonicTip = "Giving inwards towards ME!"
                ),
                JlptTopicItem(
                    id = "n4_gr_morau",
                    japanese = "もらう / 〜てもらう",
                    reading = "もらう / てもらう",
                    romaji = "morau / -te morau",
                    meaning = "Receive / Have someone do something for me",
                    patternFormula = "[Receiver] は [Giver] に/から [Thing] を もらう",
                    explanation = "Receiving an item or receiving the benefit of an action.",
                    exampleJapanese = "先生に推薦状を書いてもらいました。",
                    exampleReading = "せんせいにすいせんじょうをかいてもらいました。",
                    exampleEnglish = "I had my teacher write a recommendation letter for me.",
                    mnemonicTip = "Subject is the receiver!"
                )
            )
        ),

        JlptTopicUnit(
            id = "n4_conditionals",
            level = JlptExamLevel.N4,
            category = JlptTopicCategory.GRAMMAR,
            title = "N4 Conditionals: たら vs ば vs なら vs と",
            japaneseTitle = "条件表現 (たら・ば・なら・と)",
            overview = "The four conditional structures in Japanese each express distinct nuances of hypothetical, natural result, or timing.",
            senseiTip = "Teacher Aiko: たら is the most versatile (if/when); と is for natural laws (turn right and there's the bank); なら is for advice!",
            items = listOf(
                JlptTopicItem(
                    id = "n4_cond_tara",
                    japanese = "〜たら",
                    reading = "たら",
                    romaji = "-tara",
                    meaning = "If / When (Past tense + ら)",
                    patternFormula = "[Verb Past た-Form] + ら",
                    explanation = "The safest universal conditional in Japanese. If condition A happens, then action B.",
                    exampleJapanese = "日本に着いたら、連絡してください。",
                    exampleReading = "にほんについたら、れんらくしてください。",
                    exampleEnglish = "When / If you arrive in Japan, please contact me.",
                    mnemonicTip = "Formed from past tense: 行った -> 行ったら!"
                ),
                JlptTopicItem(
                    id = "n4_cond_to",
                    japanese = "〜と",
                    reading = "と",
                    romaji = "-to",
                    meaning = "Natural consequence / Inevitable result (Whenever A, B happens)",
                    patternFormula = "[Verb Dictionary Form] + と",
                    explanation = "Used for natural laws, machinery, and directions. When you do A, B automatically occurs.",
                    exampleJapanese = "このボタンを押すと、お釣りが出ます。",
                    exampleReading = "このぼたんをおすと、おつりがでます。",
                    exampleEnglish = "Press this button, and the change comes out automatically.",
                    mnemonicTip = "Automatic cause-and-effect!"
                )
            )
        ),

        // =====================================================================
        // JLPT N3 TOPICS (INTERMEDIATE)
        // =====================================================================
        JlptTopicUnit(
            id = "n3_keigo_essentials",
            level = JlptExamLevel.N3,
            category = JlptTopicCategory.GRAMMAR,
            title = "N3 Respectful & Humble Keigo (敬語・謙譲語)",
            japaneseTitle = "敬語の体系 (尊敬語・謙譲語・丁寧語)",
            overview = "Honorific speech (尊敬語) elevates the other person; humble speech (謙譲語) lowers yourself to show deep respect.",
            senseiTip = "Teacher Aiko: Never use 尊敬語 for your own actions! If YOU go, say '参ります' (humble). If your TEACHER goes, say 'いらっしゃいます' (respectful)!",
            items = listOf(
                JlptTopicItem(
                    id = "n3_k_sonkeigo",
                    japanese = "いらっしゃる / おっしゃる / ご覧になる",
                    reading = "いらっしゃる / おっしゃる / ごらんになる",
                    romaji = "irassharu / ossharu / goran ni naru",
                    meaning = "Honorific verbs (Elevating the listener/superior)",
                    explanation = "Used exclusively for the actions of customers, teachers, bosses, and respected elders.",
                    exampleJapanese = "先生は何時にいらっしゃいますか。",
                    exampleReading = "せんせいはなんじにいらっしゃいますか。",
                    exampleEnglish = "What time will you (Teacher) arrive/be here?",
                    mnemonicTip = "Respectful verbs describe the actions of OTHERS."
                ),
                JlptTopicItem(
                    id = "n3_k_kenjougo",
                    japanese = "参る / 申す / いただく / 拝見する",
                    reading = "まいる / もうす / いただく / はいけんする",
                    romaji = "mairu / mousu / itadaku / haiken suru",
                    meaning = "Humble verbs (Lowering the speaker)",
                    explanation = "Used for actions done by yourself or your in-group to express humility.",
                    exampleJapanese = "明日十時にオフィスへ参ります。",
                    exampleReading = "あしたじゅうじにおふぃすへまいります。",
                    exampleEnglish = "I will (humbly) come to your office tomorrow at 10.",
                    mnemonicTip = "Humble verbs describe YOUR own actions."
                )
            )
        ),

        JlptTopicUnit(
            id = "n3_nuance_connectors",
            level = JlptExamLevel.N3,
            category = JlptTopicCategory.GRAMMAR,
            title = "N3 Nuance Connectors: わけ vs はず vs よう",
            japaneseTitle = "推量と理由 (わけ・はず・よう・そう)",
            overview = "Differentiates between logical necessity (わけ), expectations (はず), and appearance/similarity (よう).",
            senseiTip = "Teacher Aiko: 'はずです' means 'it should be / expected'; 'わけがない' means 'there is no logical way that's possible'!",
            items = listOf(
                JlptTopicItem(
                    id = "n3_c_wake",
                    japanese = "〜わけがない / 〜わけではない",
                    reading = "わけがない / わけではない",
                    romaji = "-wake ga nai / -wake dewa nai",
                    meaning = "No way that ~ / It doesn't mean that ~",
                    explanation = "わけ refers to underlying reason or logical sense.",
                    exampleJapanese = "彼がそんな嘘をつくわけがありません。",
                    exampleReading = "かれがそんなうそをつくわけがありません。",
                    exampleEnglish = "There is no way he would tell such a lie (logically impossible).",
                    mnemonicTip = "Wake = Reason or sense."
                ),
                JlptTopicItem(
                    id = "n3_c_hazu",
                    japanese = "〜はずです",
                    reading = "はずです",
                    romaji = "-hazu desu",
                    meaning = "Should be / Expected to be",
                    explanation = "Expresses strong confidence based on objective schedule or logic.",
                    exampleJapanese = "田中さんは今日来るはずです。",
                    exampleReading = "たなかさんはきょうくるはずです。",
                    exampleEnglish = "Tanaka-san is supposed/expected to come today.",
                    mnemonicTip = "Expectation based on scheduled facts!"
                )
            )
        ),

        // =====================================================================
        // JLPT N2 TOPICS (UPPER INTERMEDIATE)
        // =====================================================================
        JlptTopicUnit(
            id = "n2_compound_particles",
            level = JlptExamLevel.N2,
            category = JlptTopicCategory.GRAMMAR,
            title = "N2 Compound Particles (〜につれて・〜に関して・〜にわたって)",
            japaneseTitle = "複合格助詞 (〜につれて・〜にわたって・〜を通じて)",
            overview = "Formal written and news-level multi-word particles replacing simple particles in complex sentences.",
            senseiTip = "Teacher Aiko: Notice how 〜につれて means as one continuous change progresses, another occurs in proportion!",
            items = listOf(
                JlptTopicItem(
                    id = "n2_cp_tsurete",
                    japanese = "〜につれて",
                    reading = "につれて",
                    romaji = "-ni tsurete",
                    meaning = "As X changes, Y proportionally changes",
                    patternFormula = "[Verb Dictionary Form / Noun] + につれて",
                    explanation = "Indicates two concurrent continuous trends shifting together.",
                    exampleJapanese = "年を取るにつれて、健康の大切さが分かります。",
                    exampleReading = "としをとるにつれて、けんこうのたいせつさがわかります。",
                    exampleEnglish = "As one grows older, one realizes the value of good health.",
                    mnemonicTip = "Tsurete = moving along together in parallel!"
                ),
                JlptTopicItem(
                    id = "n2_cp_watatte",
                    japanese = "〜にわたって",
                    reading = "にわたって",
                    romaji = "-ni watatte",
                    meaning = "Spanning across / Over a whole period or range",
                    patternFormula = "[Period / Location / Scope] + にわたって",
                    explanation = "Emphasizes the wide scope or long duration of an event.",
                    exampleJapanese = "三日間にわたって国際会議が開催されました。",
                    exampleReading = "みっかかんにわたってこくさいかいぎがかいさいされました。",
                    exampleEnglish = "The international conference was held spanning over three full days.",
                    mnemonicTip = "Watatte comes from 渡る (to cross over a wide span)!"
                )
            )
        ),

        // =====================================================================
        // JLPT N1 TOPICS (ADVANCED FLUENCY)
        // =====================================================================
        JlptTopicUnit(
            id = "n1_four_char_idioms",
            level = JlptExamLevel.N1,
            category = JlptTopicCategory.VOCABULARY,
            title = "N1 Four-Character Idioms (四字熟語 - Yojijukugo)",
            japaneseTitle = "四字熟語 (一期一会・臨機応変・自業自得)",
            overview = "Proverbial four-kanji idioms encapsulating philosophical truths, historical allegories, and sophisticated life philosophies.",
            senseiTip = "Teacher Aiko: Yojijukugo appear frequently in N1 reading comprehension and editorials. They convey a complete essay's wisdom in just four characters!",
            items = listOf(
                JlptTopicItem(
                    id = "n1_y_ichigo",
                    japanese = "一期一会",
                    reading = "いちごいちえ",
                    romaji = "ichigo ichie",
                    meaning = "Once-in-a-lifetime encounter / Cherish every moment",
                    explanation = "Originated in the Japanese tea ceremony. Every meeting between people is unique and cannot be repeated.",
                    exampleJapanese = "人との出会いは一期一会だから、大切にしなければならない。",
                    exampleReading = "ひととのであいはいちごいちえだから、たいせつにしなければならない。",
                    exampleEnglish = "Since meeting others is a once-in-a-lifetime encounter, one must treasure it.",
                    mnemonicTip = "一期 (one lifetime) + 一会 (one meeting)!"
                ),
                JlptTopicItem(
                    id = "n1_y_rinki",
                    japanese = "臨機応変",
                    reading = "りんきおうへん",
                    romaji = "rinki ouhen",
                    meaning = "Adapting flexibly to changing circumstances",
                    explanation = "Acting according to the requirements of the situation rather than following rigid doctrine.",
                    exampleJapanese = "予期せぬ事態には、臨機応変な対応が求められる。",
                    exampleReading = "よきせぬじたいには、りんきおうへんなたいおうがもとめられる。",
                    exampleEnglish = "In unexpected situations, a flexible response suited to the occasion is required.",
                    mnemonicTip = "Responding (応) to the changing opportunity (機)!"
                ),
                JlptTopicItem(
                    id = "n1_y_shikou",
                    japanese = "試行錯誤",
                    reading = "しこうさくご",
                    romaji = "shikou sakugo",
                    meaning = "Trial and error",
                    explanation = "Repeatedly trying different methods and learning from mistakes until succeeding.",
                    exampleJapanese = "試行錯誤を繰り返して、ついに新薬の開発に成功した。",
                    exampleReading = "しこうさくごをくりかえして、ついにしんやくのかいはつにせいこうした。",
                    exampleEnglish = "Through repeated trial and error, they finally succeeded in developing the new medicine.",
                    mnemonicTip = "Try (試) and make mistakes (錯) to learn!"
                )
            )
        ),

        JlptTopicUnit(
            id = "n1_classical_literary_grammar",
            level = JlptExamLevel.N1,
            category = JlptTopicCategory.GRAMMAR,
            title = "N1 Classical & Literary Grammar Patterns",
            japaneseTitle = "最高峰の文法 (〜ずにはおかない・〜極まりない)",
            overview = "High-register literary grammar patterns derived from classical Japanese (文語), found in formal essays and academic critiques.",
            senseiTip = "Teacher Aiko: These patterns express intense inevitability or extreme degree. Master them to achieve top percentile N1 score!",
            items = listOf(
                JlptTopicItem(
                    id = "n1_g_okanai",
                    japanese = "〜ずにはおかない",
                    reading = "ずにはおかない",
                    romaji = "-zu ni wa okanai",
                    meaning = "Will certainly / Compelled to / Cannot help but cause",
                    patternFormula = "[Verb Negative Stem ず] + にはおかない",
                    explanation = "Expresses that an event will inevitably trigger a powerful emotional or physical outcome.",
                    exampleJapanese = "彼の迫真の演技は、観客を感動させずにはおかなかった。",
                    exampleReading = "かれのはくしんのえんぎは、かんきゃくをかんどうさせずにはおかなかった。",
                    exampleEnglish = "His true-to-life performance could not help but deeply move the audience.",
                    mnemonicTip = "Will not leave things alone without causing this result!"
                ),
                JlptTopicItem(
                    id = "n1_g_kiwamarunai",
                    japanese = "〜極まりない / 〜極まる",
                    reading = "きわまりない / きわまる",
                    romaji = "-kiwamarinai / -kiwamaru",
                    meaning = "Extremely / Boundlessly / Reaching the absolute limit",
                    patternFormula = "[na-Adjective Stem / Noun] + 極まりない",
                    explanation = "Formal literary expression describing an extreme condition (often negative, like danger or rudeness).",
                    exampleJapanese = "そのような無責任な態度は、遺憾極まりない。",
                    exampleReading = "そのようなむせきにんなたいどは、いかんきわまりない。",
                    exampleEnglish = "Such an irresponsible attitude is profoundly and utterly regrettable.",
                    mnemonicTip = "Reaching the absolute boundary or peak (極)!"
                )
            )
        )
    )
}
