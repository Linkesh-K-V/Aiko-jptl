package com.example.noignore.japanese.lesson

import android.content.Context
import android.content.SharedPreferences
import com.example.noignore.japanese.data.JapaneseDeckRepository
import com.example.noignore.japanese.data.JlptExamDeckData
import com.example.noignore.japanese.model.JapaneseCategory
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.japanese.model.KanjiCompound
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JapaneseLessonRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("japanese_curriculum_prefs", Context.MODE_PRIVATE)

    private val deckRepository = JapaneseDeckRepository(context)
    private val gson = Gson()

    companion object {
        private const val PREF_CURRENT_LESSON_ID = "current_lesson_id"
        private const val PREF_COMPLETED_LESSONS = "completed_lesson_ids"
        private const val PREF_EXTRA_ITEMS_PREFIX = "extra_failed_items_lesson_"
    }

    // All structured syllabus lessons
    val allLessons: List<Lesson> by lazy {
        listOf(
            Lesson(
                id = 1,
                lessonNumber = 1,
                title = "Greetings & Foundation Kanji",
                japaneseTitle = "第１課：あいさつと基本漢字",
                level = "JLPT N5",
                description = "Master essential Japanese greetings and your first fundamental kanji characters.",
                iconEmoji = "🌸",
                coreItems = listOf(
                    JapaneseItem(
                        id = "curric_1_1",
                        japanese = "日",
                        reading = "ひ / にち / び",
                        romaji = "hi / nichi / bi",
                        meaning = "Sun, Day",
                        category = JapaneseCategory.KANJI,
                        jlptLevel = "N5",
                        onyomi = "ニチ, ジツ",
                        kunyomi = "ひ, -び, -か",
                        strokeCount = 4,
                        radical = "日 (sun)",
                        mnemonicOrNote = "A window with sunlight pouring through.",
                        exampleJapanese = "今日はいい天気ですね。",
                        exampleReading = "きょうはいいてんきですね。",
                        exampleEnglish = "Today is great weather, isn't it?",
                        compounds = listOf(
                            KanjiCompound("日本", "にほん", "Japan"),
                            KanjiCompound("今日", "きょう", "Today")
                        ),
                        options = listOf("Sun, Day", "Moon, Month", "Fire", "Water")
                    ),
                    JapaneseItem(
                        id = "curric_1_2",
                        japanese = "月",
                        reading = "つき / げつ / がつ",
                        romaji = "tsuki / getsu / gatsu",
                        meaning = "Moon, Month",
                        category = JapaneseCategory.KANJI,
                        jlptLevel = "N5",
                        onyomi = "ゲツ, ガツ",
                        kunyomi = "つき",
                        strokeCount = 4,
                        radical = "月 (moon)",
                        mnemonicOrNote = "A crescent moon suspended in the night sky.",
                        exampleJapanese = "今月は日本語を毎日勉強します。",
                        exampleReading = "こんげつはにほんごをまいにちべんきょうします。",
                        exampleEnglish = "I will study Japanese every day this month.",
                        compounds = listOf(
                            KanjiCompound("月曜日", "げつようび", "Monday"),
                            KanjiCompound("今月", "こんげつ", "This month")
                        ),
                        options = listOf("Moon, Month", "Sun, Day", "Tree, Wood", "Gold, Money")
                    ),
                    JapaneseItem(
                        id = "curric_1_3",
                        japanese = "木",
                        reading = "き / もく",
                        romaji = "ki / moku",
                        meaning = "Tree, Wood",
                        category = JapaneseCategory.KANJI,
                        jlptLevel = "N5",
                        onyomi = "モク, ボク",
                        kunyomi = "き, こ-",
                        strokeCount = 4,
                        radical = "木 (tree)",
                        mnemonicOrNote = "A trunk with branches spreading upward and roots reaching down.",
                        exampleJapanese = "庭に大きな木があります。",
                        exampleReading = "にわにおおきなきがあります。",
                        exampleEnglish = "There is a large tree in the garden.",
                        compounds = listOf(
                            KanjiCompound("木曜日", "もくようび", "Thursday")
                        ),
                        options = listOf("Tree, Wood", "Book, Origin", "Soil", "Person")
                    ),
                    JapaneseItem(
                        id = "curric_1_4",
                        japanese = "おはようございます",
                        reading = "おはようございます",
                        romaji = "ohayou gozaimasu",
                        meaning = "Good morning (Polite)",
                        category = JapaneseCategory.VOCAB,
                        jlptLevel = "N5",
                        mnemonicOrNote = "From 'hayai' (early). Used in the morning until around 10:30 AM.",
                        exampleJapanese = "先生、おはようございます！",
                        exampleReading = "せんせい、おはようございます！",
                        exampleEnglish = "Good morning, teacher!",
                        options = listOf("Good morning (Polite)", "Good afternoon", "Good evening", "Goodbye")
                    ),
                    JapaneseItem(
                        id = "curric_1_5",
                        japanese = "ありがとう",
                        reading = "ありがとう",
                        romaji = "arigatou",
                        meaning = "Thank you",
                        category = JapaneseCategory.VOCAB,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Originally 'arigatai' (hard to exist / rare blessing).",
                        exampleJapanese = "いつもありがとうございます。",
                        exampleReading = "いつもありがとうございます。",
                        exampleEnglish = "Thank you as always.",
                        options = listOf("Thank you", "Excuse me", "You're welcome", "Please")
                    ),
                    JapaneseItem(
                        id = "curric_1_6",
                        japanese = "です",
                        reading = "です",
                        romaji = "desu",
                        meaning = "To be / Is / Am / Are (Polite Copula)",
                        category = JapaneseCategory.GRAMMAR,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Placed at the end of noun sentences: [Noun] です = It is [Noun].",
                        exampleJapanese = "私は学生です。",
                        exampleReading = "わたしはがくせいです。",
                        exampleEnglish = "I am a student.",
                        options = listOf("To be / Is / Am (Polite)", "To go", "To not be", "Past tense marker")
                    )
                )
            ),
            Lesson(
                id = 2,
                lessonNumber = 2,
                title = "Numbers, Nature & Action Verbs",
                japaneseTitle = "第２課：数と自然の漢字・動作動詞",
                level = "JLPT N5",
                description = "Learn essential elemental kanji (water, fire, earth) and first conversational verbs.",
                iconEmoji = "🌊",
                coreItems = listOf(
                    JapaneseItem(
                        id = "curric_2_1",
                        japanese = "水",
                        reading = "みず / スイ",
                        romaji = "mizu / sui",
                        meaning = "Water",
                        category = JapaneseCategory.KANJI,
                        jlptLevel = "N5",
                        onyomi = "スイ",
                        kunyomi = "みず",
                        strokeCount = 4,
                        radical = "水 (water)",
                        mnemonicOrNote = "A stream of flowing water with water drops splashing on both sides.",
                        exampleJapanese = "冷たい水を飲みます。",
                        exampleReading = "つめたいみずをのみます。",
                        exampleEnglish = "I drink cold water.",
                        compounds = listOf(
                            KanjiCompound("水曜日", "すいようび", "Wednesday")
                        ),
                        options = listOf("Water", "Fire", "Soil, Earth", "Tree")
                    ),
                    JapaneseItem(
                        id = "curric_2_2",
                        japanese = "火",
                        reading = "ひ / カ",
                        romaji = "hi / ka",
                        meaning = "Fire",
                        category = JapaneseCategory.KANJI,
                        jlptLevel = "N5",
                        onyomi = "カ",
                        kunyomi = "ひ, -び, ほ-",
                        strokeCount = 4,
                        radical = "火 (fire)",
                        mnemonicOrNote = "Sparks leaping out of a campfire.",
                        exampleJapanese = "火曜日に日本語のクラスがあります。",
                        exampleReading = "かようびににほんごのくらすがあります。",
                        exampleEnglish = "There is Japanese class on Tuesday.",
                        compounds = listOf(
                            KanjiCompound("火曜日", "かようび", "Tuesday")
                        ),
                        options = listOf("Fire", "Water", "Gold, Money", "Wood")
                    ),
                    JapaneseItem(
                        id = "curric_2_3",
                        japanese = "食べる",
                        reading = "たべる",
                        romaji = "taberu",
                        meaning = "To eat",
                        category = JapaneseCategory.VOCAB,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Ichidan verb: taberu -> tabemasu (polite form).",
                        exampleJapanese = "毎日朝ご飯を食べます。",
                        exampleReading = "まいにちあさごはんをたべます。",
                        exampleEnglish = "I eat breakfast every day.",
                        options = listOf("To eat", "To drink", "To read", "To buy")
                    ),
                    JapaneseItem(
                        id = "curric_2_4",
                        japanese = "飲む",
                        reading = "のむ",
                        romaji = "nomu",
                        meaning = "To drink",
                        category = JapaneseCategory.VOCAB,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Godan verb: nomu -> nomimasu.",
                        exampleJapanese = "温かいお茶を飲みます。",
                        exampleReading = "あたたかいおちゃをのみます。",
                        exampleEnglish = "I drink hot green tea.",
                        options = listOf("To drink", "To eat", "To speak", "To sleep")
                    ),
                    JapaneseItem(
                        id = "curric_2_5",
                        japanese = "行く",
                        reading = "いく / おこなう",
                        romaji = "iku",
                        meaning = "To go",
                        category = JapaneseCategory.VOCAB,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Crucial motion verb: iku -> ikimasu (polite).",
                        exampleJapanese = "明日学校へ行きます。",
                        exampleReading = "あしたがっこうへいきます。",
                        exampleEnglish = "I go to school tomorrow.",
                        options = listOf("To go", "To come", "To return", "To walk")
                    )
                )
            ),
            Lesson(
                id = 3,
                lessonNumber = 3,
                title = "Core Particles & Daily Life",
                japaneseTitle = "第３課：助詞と日常生活",
                level = "JLPT N5",
                description = "Grasp the building blocks of Japanese grammar: topic, object, and location particles.",
                iconEmoji = "⛩️",
                coreItems = listOf(
                    JapaneseItem(
                        id = "curric_3_1",
                        japanese = "は (Topic Particle)",
                        reading = "wa (pronounced wa)",
                        romaji = "wa",
                        meaning = "Topic Marker (As for...)",
                        category = JapaneseCategory.GRAMMAR,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Written as は (ha) but pronounced 'wa'. Marks the topic of the conversation.",
                        exampleJapanese = "これは私の本です。",
                        exampleReading = "これはわたしのほんです。",
                        exampleEnglish = "As for this, it is my book.",
                        options = listOf("Topic Marker (As for...)", "Direct Object Marker", "Location of Action", "Direction Particle")
                    ),
                    JapaneseItem(
                        id = "curric_3_2",
                        japanese = "を (Object Particle)",
                        reading = "wo (pronounced o)",
                        romaji = "o / wo",
                        meaning = "Direct Object Marker",
                        category = JapaneseCategory.GRAMMAR,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Attaches directly to the noun receiving the action: [Noun] を [Verb].",
                        exampleJapanese = "本を読みます。",
                        exampleReading = "ほんをよみます。",
                        exampleEnglish = "I read a book.",
                        options = listOf("Direct Object Marker", "Subject Marker", "Time Marker", "Question Marker")
                    ),
                    JapaneseItem(
                        id = "curric_3_3",
                        japanese = "今日",
                        reading = "きょう",
                        romaji = "kyou",
                        meaning = "Today",
                        category = JapaneseCategory.VOCAB,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Combines 今 (now) + 日 (day) -> today!",
                        exampleJapanese = "今日は忙しいです。",
                        exampleReading = "きょうはいそがしいです。",
                        exampleEnglish = "Today I am busy.",
                        options = listOf("Today", "Tomorrow", "Yesterday", "Every day")
                    ),
                    JapaneseItem(
                        id = "curric_3_4",
                        japanese = "明日",
                        reading = "あした / みょうにち",
                        romaji = "ashita",
                        meaning = "Tomorrow",
                        category = JapaneseCategory.VOCAB,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Bright (明) + day (日) = tomorrow!",
                        exampleJapanese = "明日は友達と会います。",
                        exampleReading = "あしたはともだちとあいます。",
                        exampleEnglish = "Tomorrow I will meet my friend.",
                        options = listOf("Tomorrow", "Today", "Yesterday", "Next week")
                    )
                )
            ),
            Lesson(
                id = 4,
                lessonNumber = 4,
                title = "Essential Questions & Places",
                japaneseTitle = "第４課：疑問詞と場所",
                level = "JLPT N5",
                description = "Learn how to ask questions with 何 (what), どこ (where), and talk about locations.",
                iconEmoji = "❓",
                coreItems = listOf(
                    JapaneseItem(
                        id = "curric_4_1",
                        japanese = "何",
                        reading = "なに / なん",
                        romaji = "nani / nan",
                        meaning = "What",
                        category = JapaneseCategory.KANJI,
                        jlptLevel = "N5",
                        mnemonicOrNote = "A person carrying a burden, asking 'What is this?!'",
                        exampleJapanese = "これは何ですか？",
                        exampleReading = "これはなんですか？",
                        exampleEnglish = "What is this?",
                        options = listOf("What", "Where", "Who", "When")
                    ),
                    JapaneseItem(
                        id = "curric_4_2",
                        japanese = "どこ",
                        reading = "どこ",
                        romaji = "doko",
                        meaning = "Where",
                        category = JapaneseCategory.VOCAB,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Question word for place / location.",
                        exampleJapanese = "駅はどこですか？",
                        exampleReading = "えきはどこですか？",
                        exampleEnglish = "Where is the station?",
                        options = listOf("Where", "What", "Why", "How")
                    ),
                    JapaneseItem(
                        id = "curric_4_3",
                        japanese = "駅",
                        reading = "えき",
                        romaji = "eki",
                        meaning = "Train Station",
                        category = JapaneseCategory.KANJI,
                        jlptLevel = "N5",
                        onyomi = "エキ",
                        strokeCount = 14,
                        radical = "馬 (horse)",
                        mnemonicOrNote = "A horse relay station where travelers stopped.",
                        exampleJapanese = "駅で待ち合わせをします。",
                        exampleReading = "えきでまちあわせをします。",
                        exampleEnglish = "We will meet at the station.",
                        options = listOf("Train Station", "Airport", "Library", "Hospital")
                    ),
                    JapaneseItem(
                        id = "curric_4_4",
                        japanese = "か (Question Particle)",
                        reading = "ka",
                        romaji = "ka",
                        meaning = "Question Particle (?)",
                        category = JapaneseCategory.GRAMMAR,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Acts like a spoken question mark at the end of a sentence.",
                        exampleJapanese = "日本語が分かりますか？",
                        exampleReading = "にほんごがわかりますか？",
                        exampleEnglish = "Do you understand Japanese?",
                        options = listOf("Question Particle (?)", "Exclamation Marker", "Connector", "Possessive Marker")
                    )
                )
            ),
            Lesson(
                id = 5,
                lessonNumber = 5,
                title = "Family, People & Pronouns",
                japaneseTitle = "第５課：人と家族",
                level = "JLPT N5",
                description = "Describe relationships, family members, and pronouns in polite Japanese.",
                iconEmoji = "👥",
                coreItems = listOf(
                    JapaneseItem(
                        id = "curric_5_1",
                        japanese = "人",
                        reading = "ひと / ジン / ニン",
                        romaji = "hito / jin / nin",
                        meaning = "Person, People",
                        category = JapaneseCategory.KANJI,
                        jlptLevel = "N5",
                        onyomi = "ジン, ニン",
                        kunyomi = "ひと",
                        strokeCount = 2,
                        radical = "人 (person)",
                        mnemonicOrNote = "Two legs walking forward together; a human being.",
                        exampleJapanese = "あの人は誰ですか？",
                        exampleReading = "あのひとはだれですか？",
                        exampleEnglish = "Who is that person?",
                        options = listOf("Person, People", "Tree", "Large", "Enter")
                    ),
                    JapaneseItem(
                        id = "curric_5_2",
                        japanese = "私",
                        reading = "わたし / わたくし",
                        romaji = "watashi",
                        meaning = "I / Me",
                        category = JapaneseCategory.VOCAB,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Standard first-person pronoun used in all polite contexts.",
                        exampleJapanese = "私は東京に住んでいます。",
                        exampleReading = "わたしはとうきょうにすんでいます。",
                        exampleEnglish = "I live in Tokyo.",
                        options = listOf("I / Me", "You", "He / Him", "They")
                    ),
                    JapaneseItem(
                        id = "curric_5_3",
                        japanese = "友だち",
                        reading = "ともだち",
                        romaji = "tomodachi",
                        meaning = "Friend",
                        category = JapaneseCategory.VOCAB,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Tomo (companion) + dachi (plural marker).",
                        exampleJapanese = "友だちと映画を見ました。",
                        exampleReading = "ともだちとえいがをみました。",
                        exampleEnglish = "I watched a movie with a friend.",
                        options = listOf("Friend", "Teacher", "Colleague", "Neighbor")
                    )
                )
            ),
            Lesson(
                id = 6,
                lessonNumber = 6,
                title = "Time, Hours & Clock Kanji",
                japaneseTitle = "第６課：時間と時計の漢字",
                level = "JLPT N5",
                description = "Master telling time, counting hours, minutes, and scheduling your day.",
                iconEmoji = "⏰",
                coreItems = listOf(
                    JapaneseItem(
                        id = "curric_6_1",
                        japanese = "時",
                        reading = "とき / ジ",
                        romaji = "toki / ji",
                        meaning = "Time, Hour, O'clock",
                        category = JapaneseCategory.KANJI,
                        jlptLevel = "N5",
                        onyomi = "ジ",
                        kunyomi = "とき, -どき",
                        strokeCount = 10,
                        radical = "日 (sun)",
                        mnemonicOrNote = "Sun (日) + temple (寺): Buddhist temples rang bells by the sun's time.",
                        exampleJapanese = "今は三時です。",
                        exampleReading = "いまはさんじです。",
                        exampleEnglish = "It is 3 o'clock now.",
                        options = listOf("Time, Hour, O'clock", "Minute, Part", "Year", "Day")
                    ),
                    JapaneseItem(
                        id = "curric_6_2",
                        japanese = "分",
                        reading = "ふん / ぷん / わ・かる",
                        romaji = "fun / pun / bun",
                        meaning = "Minute, Part, To understand",
                        category = JapaneseCategory.KANJI,
                        jlptLevel = "N5",
                        onyomi = "フン, ブン, ブ",
                        kunyomi = "わ・ける, わ・かる",
                        strokeCount = 4,
                        radical = "刀 (sword)",
                        mnemonicOrNote = "Eight (八) cut by a sword (刀) into parts/fractions of time.",
                        exampleJapanese = "十分休みましょう。",
                        exampleReading = "じゅっぷんやすみましょう。",
                        exampleEnglish = "Let's rest for 10 minutes.",
                        options = listOf("Minute, Part, To understand", "Hour", "Month", "Second")
                    ),
                    JapaneseItem(
                        id = "curric_6_3",
                        japanese = "今",
                        reading = "いま / コン",
                        romaji = "ima / kon",
                        meaning = "Now, Present",
                        category = JapaneseCategory.KANJI,
                        jlptLevel = "N5",
                        onyomi = "コン, キン",
                        kunyomi = "いま",
                        strokeCount = 4,
                        mnemonicOrNote = "A roof sheltering a person right this very moment.",
                        exampleJapanese = "今、何時ですか？",
                        exampleReading = "いま、なんじですか？",
                        exampleEnglish = "What time is it now?",
                        options = listOf("Now, Present", "Past", "Future", "Never")
                    )
                )
            ),
            Lesson(
                id = 7,
                lessonNumber = 7,
                title = "Food, Dining & Taste Adjectives",
                japaneseTitle = "第７課：食べ物と形容詞",
                level = "JLPT N5",
                description = "Order in restaurants, praise meals, and master Japanese i-adjectives.",
                iconEmoji = "🍱",
                coreItems = listOf(
                    JapaneseItem(
                        id = "curric_7_1",
                        japanese = "ご飯",
                        reading = "ごはん",
                        romaji = "gohan",
                        meaning = "Cooked Rice / Meal",
                        category = JapaneseCategory.VOCAB,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Polite prefix 'go' + han (rice/grain). Also means meal!",
                        exampleJapanese = "晩ご飯は何ですか？",
                        exampleReading = "ばんごはんはなんですか？",
                        exampleEnglish = "What is for dinner?",
                        options = listOf("Cooked Rice / Meal", "Bread", "Tea", "Soup")
                    ),
                    JapaneseItem(
                        id = "curric_7_2",
                        japanese = "美味しい",
                        reading = "おいしい",
                        romaji = "oishii",
                        meaning = "Delicious, Tasty",
                        category = JapaneseCategory.VOCAB,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Expresses enjoyable flavor: 'Totemo oishii desu! (Very delicious!)'",
                        exampleJapanese = "このラーメンはとても美味しいです。",
                        exampleReading = "このらーめんはとてもおいしいです。",
                        exampleEnglish = "This ramen is very delicious.",
                        options = listOf("Delicious, Tasty", "Spicy", "Cold", "Expensive")
                    ),
                    JapaneseItem(
                        id = "curric_7_3",
                        japanese = "お茶",
                        reading = "おちゃ",
                        romaji = "ocha",
                        meaning = "Green Tea",
                        category = JapaneseCategory.VOCAB,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Honorable prefix 'o' + cha (tea).",
                        exampleJapanese = "熱いお茶をください。",
                        exampleReading = "あついおちゃをください。",
                        exampleEnglish = "Hot green tea, please.",
                        options = listOf("Green Tea", "Coffee", "Water", "Milk")
                    )
                )
            ),
            Lesson(
                id = 8,
                lessonNumber = 8,
                title = "JLPT N5 Core Review & Fluency Mastery",
                japaneseTitle = "第８課：総復習と合格への道",
                level = "JLPT N5",
                description = "Synthesize all foundation knowledge, complete exam scenarios, and reach JLPT readiness.",
                iconEmoji = "👑",
                coreItems = listOf(
                    JapaneseItem(
                        id = "curric_8_1",
                        japanese = "日本",
                        reading = "にほん / にっぽん",
                        romaji = "nihon / nippon",
                        meaning = "Japan (Land of the Rising Sun)",
                        category = JapaneseCategory.KANJI,
                        jlptLevel = "N5",
                        mnemonicOrNote = "Sun (日) + Origin (本) = Origin of the Sun (Japan).",
                        exampleJapanese = "いつか日本へ旅行したいです。",
                        exampleReading = "いつかにほんへりょこうしたいです。",
                        exampleEnglish = "I want to travel to Japan someday.",
                        options = listOf("Japan", "China", "Tokyo", "Station")
                    ),
                    JapaneseItem(
                        id = "curric_8_2",
                        japanese = "東京",
                        reading = "とうきょう",
                        romaji = "toukyou",
                        meaning = "Tokyo (Eastern Capital)",
                        category = JapaneseCategory.VOCAB,
                        jlptLevel = "N5",
                        mnemonicOrNote = "East (東) + Capital (京) = Tokyo.",
                        exampleJapanese = "東京タワーを見に行きました。",
                        exampleReading = "とうきょうたわーをみにいきました。",
                        exampleEnglish = "I went to see Tokyo Tower.",
                        options = listOf("Tokyo", "Kyoto", "Osaka", "Hiroshima")
                    ),
                    JapaneseItem(
                        id = "curric_8_3",
                        japanese = "ください",
                        reading = "ください",
                        romaji = "kudasai",
                        meaning = "Please give me / Please do",
                        category = JapaneseCategory.GRAMMAR,
                        jlptLevel = "N5",
                        mnemonicOrNote = "[Noun] を ください = Please give me [Noun].",
                        exampleJapanese = "これをください。",
                        exampleReading = "これをください。",
                        exampleEnglish = "Please give me this one.",
                        options = listOf("Please give me / Please do", "Thank you", "Excuse me", "Goodbye")
                    )
                )
            )
        )
    }

    fun getCurrentLessonId(): Int {
        return prefs.getInt(PREF_CURRENT_LESSON_ID, 1)
    }

    fun setCurrentLessonId(lessonId: Int) {
        val clamped = lessonId.coerceIn(1, allLessons.size)
        prefs.edit().putInt(PREF_CURRENT_LESSON_ID, clamped).apply()
    }

    fun getCurrentLesson(): Lesson {
        val id = getCurrentLessonId()
        return allLessons.find { it.id == id } ?: allLessons.first()
    }

    fun getLesson(id: Int): Lesson? {
        return allLessons.find { it.id == id }
    }

    fun isLessonCompleted(lessonId: Int): Boolean {
        val completed = getCompletedLessonIds()
        return completed.contains(lessonId)
    }

    fun getCompletedLessonIds(): Set<Int> {
        val set = prefs.getStringSet(PREF_COMPLETED_LESSONS, emptySet()) ?: emptySet()
        return set.mapNotNull { it.toIntOrNull() }.toSet()
    }

    /**
     * Determines the previous lesson for the recap test.
     * If on Lesson > 1, returns Lesson (current - 1).
     * If on Lesson 1:
     * - If Lesson 1 has already been completed, returns Lesson 1 so user can review.
     * - If Lesson 1 is brand new, returns Lesson 1 as a foundational baseline test!
     */
    fun getPreviousLessonForRecap(): Lesson {
        val currentId = getCurrentLessonId()
        return if (currentId > 1) {
            getLesson(currentId - 1) ?: allLessons.first()
        } else {
            // Lesson 1 baseline recap
            allLessons.first()
        }
    }

    /**
     * Generates a rich set of 3 to 4 multiple-choice recap questions based on
     * the items in the previous lesson.
     */
    fun getRecapQuestions(lesson: Lesson): List<RecapQuestion> {
        val items = lesson.coreItems.shuffled().take(4.coerceAtMost(lesson.coreItems.size))
        val allOtherItems = allLessons.flatMap { it.coreItems }.filter { it.meaning.isNotBlank() }

        return items.mapIndexed { idx, item ->
            val correct = item.meaning
            val wrongCandidates = (item.options.filter { it != correct } + allOtherItems.map { it.meaning }.filter { it != correct }).distinct().shuffled().take(3)
            val combinedOptions = (wrongCandidates + correct).shuffled()
            val correctIdx = combinedOptions.indexOf(correct)

            val explanation = buildString {
                append("「${item.japanese}」 (${item.reading} • ${item.romaji}) means \"${item.meaning}\".")
                if (item.mnemonicOrNote.isNotBlank()) {
                    append(" 💡 Mnemonic: ${item.mnemonicOrNote}")
                }
                if (item.exampleJapanese.isNotBlank()) {
                    append(" Example: ${item.exampleJapanese} (${item.exampleEnglish})")
                }
            }

            RecapQuestion(
                id = "recap_q_${lesson.id}_${item.id}_$idx",
                targetItem = item,
                prompt = "What is the meaning / reading of 「${item.japanese}」?",
                options = combinedOptions,
                correctIndex = correctIdx,
                explanation = explanation
            )
        }
    }

    /**
     * On-the-spot reinforcement queue:
     * When a user fails a recap question, that item is added to the current lesson!
     */
    fun addExtraFailedItemToCurrentLesson(item: JapaneseItem) {
        val currentId = getCurrentLessonId()
        val currentExtra = getExtraItemsForCurrentLesson().toMutableList()
        if (currentExtra.none { it.id == item.id }) {
            currentExtra.add(item)
            val json = gson.toJson(currentExtra)
            prefs.edit().putString(PREF_EXTRA_ITEMS_PREFIX + currentId, json).apply()
        }
    }

    fun getExtraItemsForCurrentLesson(): List<JapaneseItem> {
        val currentId = getCurrentLessonId()
        val raw = prefs.getString(PREF_EXTRA_ITEMS_PREFIX + currentId, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<JapaneseItem>>() {}.type
            gson.fromJson<List<JapaneseItem>>(raw, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearExtraItemsForCurrentLesson() {
        val currentId = getCurrentLessonId()
        prefs.edit().remove(PREF_EXTRA_ITEMS_PREFIX + currentId).apply()
    }

    /**
     * Marks lesson as completed, increments currentLessonId, awards Kao coins & review stats!
     */
    fun completeLesson(lessonId: Int) {
        val currentCompleted = getCompletedLessonIds().toMutableSet()
        currentCompleted.add(lessonId)
        prefs.edit()
            .putStringSet(PREF_COMPLETED_LESSONS, currentCompleted.map { it.toString() }.toSet())
            .apply()

        // Clear extra failed items for this completed lesson
        prefs.edit().remove(PREF_EXTRA_ITEMS_PREFIX + lessonId).apply()

        // Advance to next lesson if current was completed
        if (lessonId == getCurrentLessonId() && lessonId < allLessons.size) {
            setCurrentLessonId(lessonId + 1)
        }

        // Award reward coins & record review counts
        deckRepository.addKaoCoins(20)
        val lesson = getLesson(lessonId)
        lesson?.coreItems?.forEach { item ->
            deckRepository.recordReview(item.id, true)
        }
    }
}
