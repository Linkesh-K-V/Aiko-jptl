package com.example.noignore.japanese.data

import android.content.Context
import android.content.SharedPreferences
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.srs.FlashcardSrsDao
import com.example.noignore.data.srs.FlashcardSrsEntity
import com.example.noignore.data.srs.FlashcardSrsRepository
import com.example.noignore.japanese.model.AnkiRating
import com.example.noignore.japanese.model.JapaneseCategory
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.japanese.model.Kotowaza
import com.example.noignore.japanese.model.SrsCardData
import com.example.noignore.japanese.model.SrsCardState
import com.example.noignore.japanese.model.SrsDeckSummary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class JapaneseDeckRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("renshuu_japanese_prefs", Context.MODE_PRIVATE)

    val database: AppDatabase = AppDatabase.getInstance(context)
    val srsDao: FlashcardSrsDao = database.flashcardSrsDao()
    val srsRepository: FlashcardSrsRepository = FlashcardSrsRepository(srsDao)
    val userMetricsDataStore: com.example.noignore.data.datastore.UserMetricsDataStore =
        com.example.noignore.data.datastore.UserMetricsDataStore(context)

    init {
        migrateSharedPreferencesToRoomIfNeeded()
        // Sync metrics and populate Room curriculum & mined cards asynchronously on startup
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userMetricsDataStore.setKaoCoins(getKaoCoins())
                userMetricsDataStore.updateStreak(getStudyStreak())
                userMetricsDataStore.resetCardsTodayIfNewDay()
            } catch (_: Exception) {}
            try {
                com.example.noignore.japanese.curriculum.JlptAssetContentLoader.loadAllCurriculumIntoRoom(context)
                loadMinedCardsFromDb()
                loadRoomCurriculumIntoMemory()
            } catch (e: Exception) {
                android.util.Log.e("JapaneseDeckRepo", "Error syncing Room content: ${e.message}")
            }
            // Run audio cache cleanup in background to keep storage footprint minimal
            com.example.noignore.util.AudioCacheCleaner.cleanStaleAudioCache(context)
        }
    }

    private fun migrateSharedPreferencesToRoomIfNeeded() {
        if (!prefs.getBoolean("srs_migrated_to_room_v1", false)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val allItems = getAllItems()
                    val entitiesToInsert = mutableListOf<FlashcardSrsEntity>()
                    for (item in allItems) {
                        val rep = prefs.getInt(PREF_SRS_REP_PREFIX + item.id, -1)
                        if (rep >= 0) {
                            val interval = prefs.getInt(PREF_SRS_INTERVAL_PREFIX + item.id, 0)
                            val ease = prefs.getFloat(PREF_SRS_EASE_PREFIX + item.id, 2.50f)
                            val due = prefs.getLong(PREF_SRS_DUE_PREFIX + item.id, 0L)
                            val last = prefs.getLong(PREF_SRS_LAST_PREFIX + item.id, 0L)
                            val lapses = prefs.getInt(PREF_SRS_LAPSES_PREFIX + item.id, 0)
                            val state = prefs.getString(PREF_SRS_STATE_PREFIX + item.id, "NEW") ?: "NEW"

                            entitiesToInsert.add(
                                FlashcardSrsEntity(
                                    cardId = item.id,
                                    category = item.category.name,
                                    jlptLevel = item.jlptLevel,
                                    repetition = rep,
                                    intervalDays = interval,
                                    easeFactor = ease,
                                    dueDateMs = due,
                                    lastReviewedMs = last,
                                    lapses = lapses,
                                    state = state,
                                    totalReviews = maxOf(rep, 1)
                                )
                            )
                        }
                    }
                    if (entitiesToInsert.isNotEmpty()) {
                        srsDao.insertAll(entitiesToInsert)
                    }
                    prefs.edit().putBoolean("srs_migrated_to_room_v1", true).apply()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        private const val PREF_KAO_COINS = "renshuu_kao_coins"
        private const val PREF_CARDS_TODAY = "renshuu_cards_today"
        private const val PREF_LAST_STUDY_DATE = "renshuu_last_study_date"
        private const val PREF_TOTAL_REVIEWS = "renshuu_total_reviews"
        private const val PREF_MASTERY_PREFIX = "renshuu_mastery_"

        // Anki Spaced Repetition Constants
        private const val PREF_SRS_REP_PREFIX = "srs_rep_"
        private const val PREF_SRS_INTERVAL_PREFIX = "srs_interval_"
        private const val PREF_SRS_EASE_PREFIX = "srs_ease_"
        private const val PREF_SRS_DUE_PREFIX = "srs_due_"
        private const val PREF_SRS_LAST_PREFIX = "srs_last_"
        private const val PREF_SRS_LAPSES_PREFIX = "srs_lapses_"
        private const val PREF_SRS_STATE_PREFIX = "srs_state_"
    }

    val kotowazaList: List<Kotowaza> = listOf(
        Kotowaza(
            kanji = "継続は力なり",
            hiragana = "けいぞくはちからなり",
            romaji = "Keizoku wa chikara nari",
            english = "Continuance is power / Persistence pays off",
            lesson = "Even 5 minutes of Japanese every single day creates unbreakable momentum. Consistency trumps sporadic intensity."
        ),
        Kotowaza(
            kanji = "七転び八起き",
            hiragana = "ななころびやおき",
            romaji = "Nana korobi ya oki",
            english = "Fall seven times, stand up eight",
            lesson = "Forgot a kanji? Struggling with particles? Failing today is just part of the journey. Stand back up and review one card."
        ),
        Kotowaza(
            kanji = "千里の道も一歩から",
            hiragana = "せんりのみちもいっぽから",
            romaji = "Senri no michi mo ippo kara",
            english = "A journey of a thousand miles begins with a single step",
            lesson = "Mastering 2,000 kanji seems impossible until you master just 3 today. Take that single step."
        ),
        Kotowaza(
            kanji = "塵も積もれば山となる",
            hiragana = "ちりもつもればやまとなる",
            romaji = "Chiri mo tsumoreba yama to naru",
            english = "Even dust, if piled up, becomes a mountain",
            lesson = "5 new vocabulary words each day equals 1,825 words in one year. Small daily drops fill the ocean."
        ),
        Kotowaza(
            kanji = "初志貫徹",
            hiragana = "しょしかんてつ",
            romaji = "Shoshi kantetsu",
            english = "Carrying out one's original intention to the very end",
            lesson = "Remember why you decided to learn Japanese. Keep that fire burning when procrastination knocks."
        ),
        Kotowaza(
            kanji = "日進月歩",
            hiragana = "にっしんげっぽ",
            romaji = "Nisshin geppo",
            english = "Steady, continuous progress day by day",
            lesson = "You don't need giant leaps. Trust the spaced repetition process and show up daily."
        ),
        Kotowaza(
            kanji = "習うより慣れよ",
            hiragana = "ならうよりなれよ",
            romaji = "Narau yori nare yo",
            english = "Practice makes perfect / Experience over theory",
            lesson = "Don't just read about grammar rules. Use them, say them out loud, and test yourself on flashcards."
        )
    )

    private val masterDeck: List<JapaneseItem> = listOf(
        // === KANJI (JLPT N5) ===
        JapaneseItem(
            id = "k_1",
            japanese = "日",
            reading = "ひ / にち / び",
            romaji = "hi / nichi / bi",
            meaning = "Sun, Day",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "A window letting the sunlight in; day.",
            exampleJapanese = "今日はいい天気です。",
            exampleEnglish = "Today is nice weather.",
            options = listOf("Sun, Day", "Moon, Month", "Fire", "Water")
        ),
        JapaneseItem(
            id = "k_2",
            japanese = "月",
            reading = "つき / げつ / がつ",
            romaji = "tsuki / getsu / gatsu",
            meaning = "Moon, Month",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "Crescent moon with clouds passing over it.",
            exampleJapanese = "今月は日本語を毎日勉強します。",
            exampleEnglish = "I will study Japanese every day this month.",
            options = listOf("Moon, Month", "Sun, Day", "Tree, Wood", "Gold, Money")
        ),
        JapaneseItem(
            id = "k_3",
            japanese = "水",
            reading = "みず / スイ",
            romaji = "mizu / sui",
            meaning = "Water",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "Water droplets splashing from a central river current.",
            exampleJapanese = "水を一杯飲みます。",
            exampleEnglish = "I will drink a glass of water.",
            options = listOf("Water", "Fire", "Soil, Earth", "Tree")
        ),
        JapaneseItem(
            id = "k_4",
            japanese = "火",
            reading = "ひ / カ",
            romaji = "hi / ka",
            meaning = "Fire",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "A person jumping with hands flailing while a flame sparks.",
            exampleJapanese = "火曜日は日本語のレッスンがあります。",
            exampleEnglish = "I have a Japanese lesson on Tuesday.",
            options = listOf("Fire", "Water", "Gold", "Wood")
        ),
        JapaneseItem(
            id = "k_5",
            japanese = "木",
            reading = "き / モク / ボク",
            romaji = "ki / moku",
            meaning = "Tree, Wood",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "A trunk with branches spreading above and roots digging below.",
            exampleJapanese = "庭に大きな木があります。",
            exampleEnglish = "There is a big tree in the garden.",
            options = listOf("Tree, Wood", "Book, Origin", "Soil", "Person")
        ),
        JapaneseItem(
            id = "k_6",
            japanese = "金",
            reading = "かね / キン",
            romaji = "kane / kin",
            meaning = "Gold, Money",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "Gold nuggets buried deep under the roof of the earth.",
            exampleJapanese = "お金を大切に使います。",
            exampleEnglish = "I spend money carefully.",
            options = listOf("Gold, Money", "Fire", "Water", "Earth")
        ),
        JapaneseItem(
            id = "k_7",
            japanese = "土",
            reading = "つち / ド",
            romaji = "tsuchi / do",
            meaning = "Soil, Earth, Ground",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "A plant shoot sprouting out from the fertile soil.",
            exampleJapanese = "土曜日は休みです。",
            exampleEnglish = "Saturday is my day off.",
            options = listOf("Soil, Earth", "Tree", "King", "Person")
        ),
        JapaneseItem(
            id = "k_8",
            japanese = "本",
            reading = "ほん / ボン / ポン",
            romaji = "hon",
            meaning = "Book, Origin",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "A tree with a horizontal cut showing its root origin: paper/books!",
            exampleJapanese = "日本語の本を読みます。",
            exampleEnglish = "I read a Japanese book.",
            options = listOf("Book, Origin", "Tree", "Body", "Rest")
        ),
        JapaneseItem(
            id = "k_9",
            japanese = "人",
            reading = "ひと / ジン / ニン",
            romaji = "hito / jin / nin",
            meaning = "Person, Human",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "Two legs of a human walking forward.",
            exampleJapanese = "あの人は親切な先生です。",
            exampleEnglish = "That person is a kind teacher.",
            options = listOf("Person, Human", "Enter", "Big", "Small")
        ),
        JapaneseItem(
            id = "k_10",
            japanese = "学",
            reading = "まな・ぶ / ガク",
            romaji = "mana-bu / gaku",
            meaning = "Study, Learning",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "A child (子) studying under a school roof.",
            exampleJapanese = "大学で日本語を学んでいます。",
            exampleEnglish = "I am studying Japanese at university.",
            options = listOf("Study, Learning", "School", "Student", "Teacher")
        ),
        JapaneseItem(
            id = "k_11",
            japanese = "生",
            reading = "い・きる / う・まれる / セイ",
            romaji = "i-kiru / sei",
            meaning = "Life, Birth, Student",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "A plant emerging alive from the earth.",
            exampleJapanese = "私は学生です。",
            exampleEnglish = "I am a student.",
            options = listOf("Life, Birth, Student", "Study", "Teacher", "Before")
        ),
        JapaneseItem(
            id = "k_12",
            japanese = "先",
            reading = "さき / セン",
            romaji = "saki / sen",
            meaning = "Ahead, Previous, Former",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "Legs running ahead of everyone else.",
            exampleJapanese = "先生、質問があります。",
            exampleEnglish = "Teacher, I have a question.",
            options = listOf("Ahead, Previous", "Student", "Life", "Origin")
        ),
        JapaneseItem(
            id = "k_13",
            japanese = "食",
            reading = "た・べる / ショク",
            romaji = "ta-beru / shoku",
            meaning = "Eat, Food",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "A person under a roof sitting down with good food.",
            exampleJapanese = "朝ごはんを食べましたか？",
            exampleEnglish = "Did you eat breakfast?",
            options = listOf("Eat, Food", "Drink", "Speak", "Look")
        ),
        JapaneseItem(
            id = "k_14",
            japanese = "飲",
            reading = "の・む / イン",
            romaji = "no-mu / in",
            meaning = "Drink",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "Food radical plus yawning person opening mouth to drink.",
            exampleJapanese = "緑茶を飲みます。",
            exampleEnglish = "I drink green tea.",
            options = listOf("Drink", "Eat", "Study", "Listen")
        ),
        JapaneseItem(
            id = "k_15",
            japanese = "行",
            reading = "い・く / おこな・う / コウ",
            romaji = "i-ku / kou",
            meaning = "Go, Act, Conduct",
            category = JapaneseCategory.KANJI,
            jlptLevel = "N5",
            mnemonicOrNote = "Crossroads where travelers go and walk.",
            exampleJapanese = "来週日本に行きます。",
            exampleEnglish = "I am going to Japan next week.",
            options = listOf("Go, Act", "Come", "Return", "Wait")
        ),

        // === VOCABULARY (JLPT N5) ===
        JapaneseItem(
            id = "v_1",
            japanese = "勉強",
            reading = "べんきょう",
            romaji = "benkyou",
            meaning = "Study, Diligence",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5",
            mnemonicOrNote = "Formed by 勉 (effort/exertion) and 強 (strong). Study makes you strong!",
            exampleJapanese = "毎日30分日本語を勉強します。",
            exampleEnglish = "I study Japanese for 30 minutes every day.",
            options = listOf("Study, Diligence", "Practice", "Rest, Vacation", "Work, Labor")
        ),
        JapaneseItem(
            id = "v_2",
            japanese = "継続",
            reading = "けいぞく",
            romaji = "keizoku",
            meaning = "Continuation, Persistence",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5",
            mnemonicOrNote = "Key word from 継続は力なり (Continuance is power).",
            exampleJapanese = "継続することが一番大切です。",
            exampleEnglish = "Continuing is the most important thing.",
            options = listOf("Continuation, Persistence", "Beginning", "Finish, End", "Memory")
        ),
        JapaneseItem(
            id = "v_3",
            japanese = "毎日",
            reading = "まいにち",
            romaji = "mainichi",
            meaning = "Every day",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5",
            mnemonicOrNote = "毎 (every) + 日 (day).",
            exampleJapanese = "毎日単語を復習します。",
            exampleEnglish = "I review words every day.",
            options = listOf("Every day", "Every week", "Every month", "Yesterday")
        ),
        JapaneseItem(
            id = "v_4",
            japanese = "友達",
            reading = "ともだち",
            romaji = "tomodachi",
            meaning = "Friend",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5",
            mnemonicOrNote = "友 (friend) + 達 (plural suffix).",
            exampleJapanese = "友達と日本語で話しました。",
            exampleEnglish = "I spoke in Japanese with a friend.",
            options = listOf("Friend", "Family", "Teacher", "Colleague")
        ),
        JapaneseItem(
            id = "v_5",
            japanese = "頑張る",
            reading = "がんばる",
            romaji = "ganbaru",
            meaning = "To do one's best, Persevere",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5",
            mnemonicOrNote = "The ultimate Japanese motivational verb: Ganbatte!",
            exampleJapanese = "今日も一日頑張ろう！",
            exampleEnglish = "Let's do our best today too!",
            options = listOf("To do one's best, Persevere", "To rest", "To sleep", "To forget")
        ),
        JapaneseItem(
            id = "v_6",
            japanese = "時間",
            reading = "じかん",
            romaji = "jikan",
            meaning = "Time, Hour",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5",
            mnemonicOrNote = "時 (time) + 間 (interval/space).",
            exampleJapanese = "勉強する時間を作りましょう。",
            exampleEnglish = "Let's make time to study.",
            options = listOf("Time, Hour", "Place", "Date", "Calendar")
        ),
        JapaneseItem(
            id = "v_7",
            japanese = "約束",
            reading = "やくそく",
            romaji = "yakusoku",
            meaning = "Promise, Commitment",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5",
            mnemonicOrNote = "A promise made to yourself to stay consistent.",
            exampleJapanese = "自分との約束を守ります。",
            exampleEnglish = "I keep the promise made to myself.",
            options = listOf("Promise, Commitment", "Habit", "Schedule", "Excuse")
        ),
        JapaneseItem(
            id = "v_8",
            japanese = "覚える",
            reading = "おぼえる",
            romaji = "oboeru",
            meaning = "To memorize, Remember",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5",
            mnemonicOrNote = "Crucial for vocabulary and kanji acquisition.",
            exampleJapanese = "新しい漢字を5つ覚えました。",
            exampleEnglish = "I memorized 5 new kanji.",
            options = listOf("To memorize, Remember", "To forget", "To write", "To read")
        ),
        JapaneseItem(
            id = "v_9",
            japanese = "始める",
            reading = "はじめる",
            romaji = "hajimeru",
            meaning = "To begin, To start",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5",
            mnemonicOrNote = "Overcome friction by simply beginning for 2 minutes.",
            exampleJapanese = "今すぐ学習を始めましょう。",
            exampleEnglish = "Let's start studying right now.",
            options = listOf("To begin, To start", "To finish", "To pause", "To think")
        ),
        JapaneseItem(
            id = "v_10",
            japanese = "練習",
            reading = "れんしゅう",
            romaji = "renshuu",
            meaning = "Practice, Drill",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N5",
            mnemonicOrNote = "The namesake of Renshuu! Practice makes mastery.",
            exampleJapanese = "発音の練習をします。",
            exampleEnglish = "I will practice pronunciation.",
            options = listOf("Practice, Drill", "Test, Exam", "Lesson", "Homework")
        ),

        // === GRAMMAR PARTICLES & PATTERNS (JLPT N5) ===
        JapaneseItem(
            id = "g_1",
            japanese = "は (wa)",
            reading = "は (pronouned わ/wa)",
            romaji = "wa",
            meaning = "Topic Marker (As for...)",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N5",
            mnemonicOrNote = "Written with 'ha' but pronounced 'wa'. Sets the stage/topic of conversation.",
            exampleJapanese = "私は毎日勉強します。",
            exampleEnglish = "As for me, I study every day.",
            options = listOf("Topic Marker", "Object Marker", "Location Marker", "Direction Marker")
        ),
        JapaneseItem(
            id = "g_2",
            japanese = "を (o/wo)",
            reading = "を (pronounced お/o)",
            romaji = "o",
            meaning = "Direct Object Marker",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N5",
            mnemonicOrNote = "Points directly at the noun receiving the action (e.g. 本を読む = read a book).",
            exampleJapanese = "日本語を勉強します。",
            exampleEnglish = "I study Japanese.",
            options = listOf("Direct Object Marker", "Topic Marker", "Subject Marker", "Possessive Marker")
        ),
        JapaneseItem(
            id = "g_3",
            japanese = "が (ga)",
            reading = "が",
            romaji = "ga",
            meaning = "Subject / Identifier Marker",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N5",
            mnemonicOrNote = "Marks specific subject, also used with adjectives (好き、上手) and existence (あります/います).",
            exampleJapanese = "日本語が好きです。",
            exampleEnglish = "I like Japanese.",
            options = listOf("Subject Marker", "Topic Marker", "Object Marker", "Time Marker")
        ),
        JapaneseItem(
            id = "g_4",
            japanese = "に (ni)",
            reading = "に",
            romaji = "ni",
            meaning = "Target / Specific Time / Destination",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N5",
            mnemonicOrNote = "Points like a pinpoint arrow at a target, specific clock time (7時に), or destination (日本に行く).",
            exampleJapanese = "7時に起きます。",
            exampleEnglish = "I wake up at 7 o'clock.",
            options = listOf("Time/Destination Marker", "Topic Marker", "Direct Object Marker", "Subject Marker")
        ),
        JapaneseItem(
            id = "g_5",
            japanese = "で (de)",
            reading = "で",
            romaji = "de",
            meaning = "Location of Action / By Means Of",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N5",
            mnemonicOrNote = "Marks where dynamic actions happen (図書館で勉強する) or instrument/tool used (バスで行く).",
            exampleJapanese = "机で勉強します。",
            exampleEnglish = "I study at my desk.",
            options = listOf("Location of Action / By Means Of", "Destination", "Topic", "Possession")
        ),
        JapaneseItem(
            id = "g_6",
            japanese = "〜てください",
            reading = "〜てください",
            romaji = "~te kudasai",
            meaning = "Please do ~ (Polite Request)",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N5",
            mnemonicOrNote = "Formed by Te-form of verb + ください. Used for polite requests and directions.",
            exampleJapanese = "この文を読んでください。",
            exampleEnglish = "Please read this sentence.",
            options = listOf("Please do ~", "I want to do ~", "You must do ~", "May I do ~?")
        ),

        // === KANA ESSENTIALS ===
        JapaneseItem(
            id = "kana_1",
            japanese = "あ (a) / い (i) / う (u) / え (e) / お (o)",
            reading = "あ・い・う・え・お",
            romaji = "a, i, u, e, o",
            meaning = "Vowel Row (The foundation of all Japanese syllables)",
            category = JapaneseCategory.KANA,
            jlptLevel = "N5",
            mnemonicOrNote = "All 46 Hiragana characters originate from combinations with these 5 core vowels.",
            exampleJapanese = "あいうえおを練習しましょう。",
            exampleEnglish = "Let's practice a-i-u-e-o.",
            options = listOf("Vowel Row", "K-row", "S-row", "T-row")
        ),
        JapaneseItem(
            id = "kana_2",
            japanese = "か (ka) / き (ki) / く (ku) / ke (ke) / こ (ko)",
            reading = "か・き・く・け・こ",
            romaji = "ka, ki, ku, ke, ko",
            meaning = "K-Row Syllables (Adds Dakuten to become Ga, Gi, Gu, Ge, Go)",
            category = JapaneseCategory.KANA,
            jlptLevel = "N5",
            mnemonicOrNote = "With ten-ten (゛): が, ぎ, ぐ, げ, ご.",
            exampleJapanese = "傘 (かさ) を持って行きます。",
            exampleEnglish = "I take an umbrella.",
            options = listOf("K-Row Syllables", "S-row", "N-row", "H-row")
        ),

        // === N4 VOCABULARY & GRAMMAR ===
        JapaneseItem(
            id = "v_n4_1",
            japanese = "案内",
            reading = "あんない",
            romaji = "annai",
            meaning = "Guidance, Information, Leading the way",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N4",
            mnemonicOrNote = "案 (plan) + 内 (inside). Guiding someone with an internal plan.",
            exampleJapanese = "街をご案内します。",
            exampleEnglish = "I will show you around the town.",
            options = listOf("Guidance, Information", "Invitation", "Safety", "Meeting")
        ),
        JapaneseItem(
            id = "v_n4_2",
            japanese = "複雑",
            reading = "ふくざつ",
            romaji = "fukuzatsu",
            meaning = "Complicated, Complex",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N4",
            mnemonicOrNote = "複 (duplicate/double) + 雑 (mixed). Multi-layered and complicated.",
            exampleJapanese = "この機械の使い方は複雑です。",
            exampleEnglish = "The usage of this machine is complicated.",
            options = listOf("Complicated, Complex", "Simple", "Dangerous", "Convenient")
        ),
        JapaneseItem(
            id = "v_n4_3",
            japanese = "遠慮",
            reading = "えんりょ",
            romaji = "enryo",
            meaning = "Hesitation, Restraint, Holding back",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N4",
            mnemonicOrNote = "遠 (distant) + 慮 (thought). Considering others from afar by exercising restraint.",
            exampleJapanese = "遠慮しないでたくさん食べてください。",
            exampleEnglish = "Please eat plenty without holding back.",
            options = listOf("Restraint, Holding back", "Politeness", "Preparation", "Confidence")
        ),
        JapaneseItem(
            id = "v_n4_4",
            japanese = "都合",
            reading = "つごう",
            romaji = "tsugou",
            meaning = "Convenience, Circumstances, Schedule",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N4",
            mnemonicOrNote = "都 (capital) + 合 (match). How conditions match your personal convenience.",
            exampleJapanese = "明日の午後、ご都合はいかがですか。",
            exampleEnglish = "How is your schedule tomorrow afternoon?",
            options = listOf("Convenience, Schedule", "Mood", "Weather", "Location")
        ),
        JapaneseItem(
            id = "v_n4_5",
            japanese = "故障",
            reading = "こしょう",
            romaji = "koshou",
            meaning = "Breakdown, Out of order",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N4",
            mnemonicOrNote = "故 (happenstance) + 障 (obstacle). A malfunction causing a breakdown.",
            exampleJapanese = "エレベーターが故障しています。",
            exampleEnglish = "The elevator is out of order.",
            options = listOf("Breakdown, Malfunction", "Repair", "Operation", "Inspection")
        ),
        JapaneseItem(
            id = "v_n4_6",
            japanese = "予定",
            reading = "よてい",
            romaji = "yotei",
            meaning = "Plan, Schedule, Arrangement",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N4",
            mnemonicOrNote = "予 (in advance) + 定 (decide). Something decided in advance.",
            exampleJapanese = "来週京都へ旅行する予定です。",
            exampleEnglish = "I plan to travel to Kyoto next week.",
            options = listOf("Plan, Schedule", "Memory", "Promise", "Habit")
        ),
        JapaneseItem(
            id = "g_n4_1",
            japanese = "〜られる (Potential)",
            reading = "〜られる / 〜れる",
            romaji = "~rareru",
            meaning = "Can do ~, Able to do ~",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N4",
            mnemonicOrNote = "Ichidan verbs replace る with られる (食べられる). Godan verbs change u to e + る (話せる).",
            exampleJapanese = "日本語の新聞が少し読めます。",
            exampleEnglish = "I can read a little of a Japanese newspaper.",
            options = listOf("Can do ~", "Must do ~", "Want to do ~", "Try to do ~")
        ),
        JapaneseItem(
            id = "g_n4_2",
            japanese = "〜てはいけない",
            reading = "〜てはいけない",
            romaji = "~te wa ikenai",
            meaning = "Must not do ~, Prohibition",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N4",
            mnemonicOrNote = "Te-form + はいけない. Expresses strict prohibition or rules.",
            exampleJapanese = "ここでタバコを吸ってはいけません。",
            exampleEnglish = "You must not smoke here.",
            options = listOf("Must not do ~", "May do ~", "Must do ~", "Good at ~")
        ),
        JapaneseItem(
            id = "g_n4_3",
            japanese = "〜たことがある",
            reading = "〜たことがある",
            romaji = "~ta koto ga aru",
            meaning = "Have experience of ~ing",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N4",
            mnemonicOrNote = "Past plain form (た-form) + ことがある. Talking about past life experiences.",
            exampleJapanese = "日本へ行ったことがありますか。",
            exampleEnglish = "Have you ever been to Japan?",
            options = listOf("Have experience of ~", "About to do ~", "Decided to do ~", "Planning to do ~")
        ),
        JapaneseItem(
            id = "g_n4_4",
            japanese = "〜すぎる",
            reading = "〜すぎる",
            romaji = "~sugiru",
            meaning = "Too much, Excessively",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N4",
            mnemonicOrNote = "Verb stem / Adj stem + すぎる. Exceeding a moderate limit.",
            exampleJapanese = "甘いお菓子を食べすぎました。",
            exampleEnglish = "I ate too many sweet treats.",
            options = listOf("Too much, Excessively", "Not enough", "Just right", "Difficult to")
        ),

        // === N3 VOCABULARY & GRAMMAR ===
        JapaneseItem(
            id = "v_n3_1",
            japanese = "役に立つ",
            reading = "やくにたつ",
            romaji = "yaku ni tatsu",
            meaning = "To be helpful, Useful",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N3",
            mnemonicOrNote = "役 (role/service) + 立つ (to stand). To stand in service = to be useful.",
            exampleJapanese = "このアプリは日本語の試験にとても役に立ちます。",
            exampleEnglish = "This app is very useful for Japanese exams.",
            options = listOf("To be helpful, Useful", "To stand out", "To pass an exam", "To remember")
        ),
        JapaneseItem(
            id = "v_n3_2",
            japanese = "影響",
            reading = "えいきょう",
            romaji = "eikyou",
            meaning = "Influence, Effect",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N3",
            mnemonicOrNote = "影 (shadow) + 響 (echo). A sound and shadow leaving their mark.",
            exampleJapanese = "台風の影響で新幹線が運転を見合わせた。",
            exampleEnglish = "Bullet train services were suspended due to the influence of the typhoon.",
            options = listOf("Influence, Effect", "Danger", "Assistance", "Result")
        ),
        JapaneseItem(
            id = "v_n3_3",
            japanese = "解決",
            reading = "かいけつ",
            romaji = "kaiketsu",
            meaning = "Solution, Resolution, Settlement",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N3",
            mnemonicOrNote = "解 (untie) + 決 (decide). Untying tangled difficulties to reach a decision.",
            exampleJapanese = "双方でじっくり話し合って問題を解決した。",
            exampleEnglish = "We settled the problem by discussing it thoroughly together.",
            options = listOf("Solution, Resolution", "Suspension", "Discussion", "Investigation")
        ),
        JapaneseItem(
            id = "v_n3_4",
            japanese = "傾向",
            reading = "けいこう",
            romaji = "keikou",
            meaning = "Tendency, Trend, Inclination",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N3",
            mnemonicOrNote = "傾 (tilt/incline) + 向 (direction). Leaning in a distinct direction over time.",
            exampleJapanese = "近年は若者の自炊が増加する傾向にある。",
            exampleEnglish = "In recent years there has been a trend of increasing home-cooking among youths.",
            options = listOf("Tendency, Trend", "Comparison", "Interest", "Contrast")
        ),
        JapaneseItem(
            id = "v_n3_5",
            japanese = "緊張",
            reading = "きんちょう",
            romaji = "kinchou",
            meaning = "Nervousness, Tension, Strain",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N3",
            mnemonicOrNote = "緊 (tight) + 張 (stretched). Strings pulled taut before a big moment.",
            exampleJapanese = "面接の直前はとても緊張しました。",
            exampleEnglish = "I was extremely nervous right before the interview.",
            options = listOf("Nervousness, Tension", "Excitement", "Disappointment", "Exhaustion")
        ),
        JapaneseItem(
            id = "g_n3_1",
            japanese = "〜はずだ",
            reading = "〜はずだ",
            romaji = "~hazu da",
            meaning = "Should be ~, Expected to be ~",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N3",
            mnemonicOrNote = "Expresses strong natural expectation based on objective evidence.",
            exampleJapanese = "田中さんは今日来るはずです。",
            exampleEnglish = "Tanaka-san is expected to come today.",
            options = listOf("Expected to be ~", "Must not be ~", "Might be ~", "Used to be ~")
        ),
        JapaneseItem(
            id = "g_n3_2",
            japanese = "〜わけにはいかない",
            reading = "〜わけにはいかない",
            romaji = "~wake ni wa ikanai",
            meaning = "Cannot afford to ~, Inadmissible",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N3",
            mnemonicOrNote = "Moral, social, or psychological constraint preventing an action.",
            exampleJapanese = "大事な顧客との会議なので、休むわけにはいかない。",
            exampleEnglish = "Since it's a meeting with an important client, I cannot afford to take the day off.",
            options = listOf("Cannot afford to ~", "Naturally will ~", "Must have ~", "Easy to ~")
        ),
        JapaneseItem(
            id = "g_n3_3",
            japanese = "〜たとたん",
            reading = "〜たとたん",
            romaji = "~ta totan",
            meaning = "Just as ~, The moment that ~",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N3",
            mnemonicOrNote = "Past verb + とたん (に). Emphasizes an unexpected immediate consequence.",
            exampleJapanese = "窓を開けたとたん、涼しい風が吹き込んできた。",
            exampleEnglish = "The moment I opened the window, a refreshing breeze blew in.",
            options = listOf("The moment that ~", "Before doing ~", "While waiting for ~", "Instead of ~")
        ),
        JapaneseItem(
            id = "g_n3_4",
            japanese = "〜わりに",
            reading = "〜わりに",
            romaji = "~warini",
            meaning = "Considering ~, In spite of ~",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N3",
            mnemonicOrNote = "Noun + の / Verb plain + わりに. Highlights an unexpected gap compared to standard expectation.",
            exampleJapanese = "彼はあまり勉強しなかったわりに、高得点を取った。",
            exampleEnglish = "Considering he didn't study much, he achieved a high score.",
            options = listOf("Considering ~, In spite of", "Because of ~", "On behalf of ~", "Compared to ~")
        ),

        // === N2 VOCABULARY & GRAMMAR ===
        JapaneseItem(
            id = "v_n2_1",
            japanese = "把握",
            reading = "はあく",
            romaji = "haaku",
            meaning = "Grasp, Comprehend, Understand fully",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N2",
            mnemonicOrNote = "Holding and gripping the whole situation in your hands.",
            exampleJapanese = "現状を正確に把握することが肝要です。",
            exampleEnglish = "It is essential to grasp the current situation accurately.",
            options = listOf("Grasp, Comprehend", "Overlook", "Hesitate", "Construct")
        ),
        JapaneseItem(
            id = "v_n2_2",
            japanese = "徹底",
            reading = "てってい",
            romaji = "tettei",
            meaning = "Thoroughness, Completeness, Enforcement",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N2",
            mnemonicOrNote = "徹 (pierce through) + 底 (bottom). Penetrating right down to the deepest root.",
            exampleJapanese = "全社員に対する情報管理の徹底を図る。",
            exampleEnglish = "We will ensure thorough information management across all employees.",
            options = listOf("Thoroughness, Enforcement", "Superficiality", "Postponement", "Abandonment")
        ),
        JapaneseItem(
            id = "v_n2_3",
            japanese = "迅速",
            reading = "じんそく",
            romaji = "jinsoku",
            meaning = "Prompt, Swift, Rapid",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N2",
            mnemonicOrNote = "迅 (fast) + 速 (speed). Lightning-quick agility in business and action.",
            exampleJapanese = "予期せぬトラブルにも迅速に対処できた。",
            exampleEnglish = "We were able to respond promptly even to unexpected trouble.",
            options = listOf("Prompt, Swift", "Cautious", "Gradual", "Indifferent")
        ),
        JapaneseItem(
            id = "v_n2_4",
            japanese = "妥協",
            reading = "だきょう",
            romaji = "dakyou",
            meaning = "Compromise, Giving in",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N2",
            mnemonicOrNote = "妥 (appropriate) + 協 (cooperate). Meeting halfway through negotiation.",
            exampleJapanese = "製品の品質に関しては一切妥協しない。",
            exampleEnglish = "We make no compromises whatsoever when it comes to product quality.",
            options = listOf("Compromise, Giving in", "Resistance", "Conflict", "Ambition")
        ),
        JapaneseItem(
            id = "v_n2_5",
            japanese = "契機",
            reading = "けいき",
            romaji = "keiki",
            meaning = "Opportunity, Turning point, Catalyst",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N2",
            mnemonicOrNote = "契 (pledge) + 機 (machine/chance). A catalytic turning point in life or history.",
            exampleJapanese = "海外留学を契機として、異文化共生への関心が深まった。",
            exampleEnglish = "Studying abroad served as a catalyst that deepened my interest in multicultural coexistence.",
            options = listOf("Turning point, Opportunity", "Crisis", "Dilemma", "Routine")
        ),
        JapaneseItem(
            id = "g_n2_1",
            japanese = "〜にわたって",
            reading = "〜にわたって",
            romaji = "~ni watatte",
            meaning = "Spanning across ~, Throughout (time/space)",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N2",
            mnemonicOrNote = "Watatte comes from 渡る (to cross). Spanning across a multi-year or wide geographic range.",
            exampleJapanese = "3日間にわたって試験が実施されました。",
            exampleEnglish = "The exam was conducted over a span of 3 days.",
            options = listOf("Spanning across ~", "Compared to ~", "Due to ~", "Instead of ~")
        ),
        JapaneseItem(
            id = "g_n2_2",
            japanese = "〜に相違ない",
            reading = "〜にそういない",
            romaji = "~ni soui nai",
            meaning = "Without doubt ~, Must certainly be ~",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N2",
            mnemonicOrNote = "相違 (difference/discrepancy) + ない. Formal conviction: 'there is zero discrepancy that...'",
            exampleJapanese = "この決定的な証拠から見て、彼が主犯に相違ない。",
            exampleEnglish = "Judging from this decisive evidence, he must certainly be the prime culprit.",
            options = listOf("Without doubt, Certainly", "Unlikely to be", "Might possibly be", "Cannot be")
        ),
        JapaneseItem(
            id = "g_n2_3",
            japanese = "〜ざるを得ない",
            reading = "〜ざるをえない",
            romaji = "~zaru o enai",
            meaning = "Cannot help but ~, Compelled to ~",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N2",
            mnemonicOrNote = "Verb [Nai-stem] + ざるを得ない (する -> せざるを得ない). Unavoidable necessity despite reluctance.",
            exampleJapanese = "予算の制約上、計画の一部を縮小せざるを得ない。",
            exampleEnglish = "Given budget constraints, we cannot help but scale down part of the plan.",
            options = listOf("Cannot help but ~, Compelled to", "Forbidden from", "Voluntarily doing", "Able to avoid")
        ),

        // === N1 VOCABULARY & GRAMMAR ===
        JapaneseItem(
            id = "v_n1_1",
            japanese = "一期一会",
            reading = "いちごいちえ",
            romaji = "ichigo ichie",
            meaning = "Once-in-a-lifetime encounter, Cherish every meeting",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N1",
            mnemonicOrNote = "Classic tea ceremony idiom: treat every encounter as unique, because it will never happen again.",
            exampleJapanese = "人との出会いを一期一会として大切にする。",
            exampleEnglish = "Treasure encounters with others as once-in-a-lifetime moments.",
            options = listOf("Once-in-a-lifetime encounter", "Constant change", "Deep meditation", "Self-study")
        ),
        JapaneseItem(
            id = "v_n1_2",
            japanese = "乖離",
            reading = "かいり",
            romaji = "kairi",
            meaning = "Estrangement, Divergence, Disconnect",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N1",
            mnemonicOrNote = "乖 (go against) + 離 (separate). A sharp rift opening between ideals and reality.",
            exampleJapanese = "掲げた理想と厳しい現実の乖離に深く苦悩する。",
            exampleEnglish = "Suffering deeply from the disconnect between championed ideals and harsh reality.",
            options = listOf("Divergence, Disconnect", "Harmony", "Assimilation", "Convergence")
        ),
        JapaneseItem(
            id = "v_n1_3",
            japanese = "彷彿",
            reading = "ほうふつ",
            romaji = "houfutsu",
            meaning = "Evoking, Reminiscent of, Calling to mind",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N1",
            mnemonicOrNote = "彷彿とさせる. Vividly bringing a historical or artistic masterpiece back before the eyes.",
            exampleJapanese = "往年の名優を彷彿とさせる圧倒的な名演を見せた。",
            exampleEnglish = "Gave a breathtaking performance evoking the legendary actors of yesteryear.",
            options = listOf("Evoking, Reminiscent of", "Exaggerating", "Imitating poorly", "Rejecting")
        ),
        JapaneseItem(
            id = "v_n1_4",
            japanese = "席巻",
            reading = "せっけん",
            romaji = "sekken",
            meaning = "Sweeping over, Dominating, Taking by storm",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N1",
            mnemonicOrNote = "Rolling up a woven straw mat (席) and conquering the whole field in a swoop.",
            exampleJapanese = "新開発のAI技術が一瞬にして世界の市場を席巻した。",
            exampleEnglish = "The newly developed AI technology swept the global market in the blink of an eye.",
            options = listOf("Dominating, Sweeping over", "Stagnating", "Withdrawing from", "Surrendering")
        ),
        JapaneseItem(
            id = "v_n1_5",
            japanese = "辟易",
            reading = "へきえき",
            romaji = "hekieki",
            meaning = "Fed up with, Weary of, Dumbfounded",
            category = JapaneseCategory.VOCAB,
            jlptLevel = "N1",
            mnemonicOrNote = "Shrinking back in exhaustion or exasperation from an overwhelming nuisance.",
            exampleJapanese = "繰り返される相手の身勝手な言い訳には心底辟易した。",
            exampleEnglish = "I was thoroughly fed up with the other party's repeated selfish excuses.",
            options = listOf("Fed up with, Weary of", "Enthusiastic about", "Sympathetic to", "Interested in")
        ),
        JapaneseItem(
            id = "g_n1_1",
            japanese = "〜を余儀なくされる",
            reading = "〜をよぎなくされる",
            romaji = "~o yoginaku sareru",
            meaning = "To be forced to ~, To have no choice but to ~",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N1",
            mnemonicOrNote = "Used in formal writing/news when external circumstances leave no other option.",
            exampleJapanese = "計画の変更を余儀なくされた。",
            exampleEnglish = "We were forced to make changes to the plan.",
            options = listOf("To be forced to ~", "To easily achieve ~", "To wish for ~", "To decide voluntarily")
        ),
        JapaneseItem(
            id = "g_n1_2",
            japanese = "〜極まりない",
            reading = "〜きわまりない",
            romaji = "~kiwamarinai",
            meaning = "In the extreme, Utterly (negative/grave)",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N1",
            mnemonicOrNote = "極まる (reach extremity) + ない. Extremely severe or deplorable condition.",
            exampleJapanese = "公の場であのような無責任極まりない放言をするとは許しがたい。",
            exampleEnglish = "Making such an utterly irresponsible outburst in a public forum is unforgivable.",
            options = listOf("In the extreme, Utterly", "Not very much", "Rather moderate", "Acceptable")
        ),
        JapaneseItem(
            id = "g_n1_3",
            japanese = "〜はおろか",
            reading = "〜はおろか",
            romaji = "~wa oroka",
            meaning = "Let alone ~, Not to mention ~",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N1",
            mnemonicOrNote = "A はおろか B も... (Let alone A, even B cannot be done).",
            exampleJapanese = "彼は漢字はおろか、基本的なひらがなさえ読めない。",
            exampleEnglish = "He cannot even read basic hiragana, let alone kanji.",
            options = listOf("Let alone ~, Not to mention", "Because of ~", "Just like ~", "Thanks to ~")
        ),
        JapaneseItem(
            id = "g_n1_4",
            japanese = "〜たるもの",
            reading = "〜たるもの",
            romaji = "~tarumono",
            meaning = "As someone who is ~, In the position of ~",
            category = JapaneseCategory.GRAMMAR,
            jlptLevel = "N1",
            mnemonicOrNote = "Used for professions or roles with high duty: 'One who holds the esteemed title of...'",
            exampleJapanese = "国の指導者たるものは、常に大局観を持たねばならない。",
            exampleEnglish = "One who is a leader of a nation must always maintain a grand perspective.",
            options = listOf("As someone who is ~", "Unlike ~", "In place of ~", "Regardless of ~")
        )
    )

    fun getSelectedExamLevel(): com.example.noignore.japanese.model.JlptExamLevel {
        val code = prefs.getString("renshuu_selected_exam_level", "N5") ?: "N5"
        return com.example.noignore.japanese.model.JlptExamLevel.fromCode(code)
    }

    fun setSelectedExamLevel(level: com.example.noignore.japanese.model.JlptExamLevel) {
        prefs.edit().putString("renshuu_selected_exam_level", level.code).apply()
    }

    fun getKotowazaOfTheDay(): Kotowaza {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear - 1) % kotowazaList.size
        return kotowazaList[index]
    }

    private val customItems = mutableListOf<JapaneseItem>()
    private val roomCurriculumItems = mutableListOf<JapaneseItem>()

    private suspend fun loadMinedCardsFromDb() {
        try {
            val mined = database.minedItemDao().getAllMinedItems()
            synchronized(customItems) {
                for (m in mined) {
                    if (customItems.none { it.id == m.id || it.japanese == m.japanese }) {
                        customItems.add(
                            JapaneseItem(
                                id = m.id,
                                japanese = m.japanese,
                                reading = m.reading,
                                romaji = m.romaji,
                                meaning = m.meaning,
                                category = try { JapaneseCategory.valueOf(m.category) } catch (_: Exception) { JapaneseCategory.VOCAB },
                                jlptLevel = m.jlptLevel,
                                exampleJapanese = m.sourceSentence,
                                exampleEnglish = m.sourceEnglish,
                                mnemonicOrNote = m.userNotes
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("JapaneseDeckRepo", "Error loading mined cards: ${e.message}")
        }
    }

    private suspend fun loadRoomCurriculumIntoMemory() {
        try {
            val items = database.jlptContentDao().getAllContent()
            if (items.isNotEmpty()) {
                synchronized(roomCurriculumItems) {
                    roomCurriculumItems.clear()
                    roomCurriculumItems.addAll(items.map { it.toJapaneseItem() })
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("JapaneseDeckRepo", "Error loading room curriculum: ${e.message}")
        }
    }

    fun addCustomItem(item: JapaneseItem) {
        synchronized(customItems) {
            if (customItems.none { it.id == item.id || it.japanese == item.japanese }) {
                customItems.add(item)
            }
        }

        // Persist permanently in Room SQLite MinedItemDao and Spaced Repetition queue
        CoroutineScope(Dispatchers.IO).launch {
            try {
                database.minedItemDao().insert(
                    com.example.noignore.data.mined.MinedItemEntity(
                        id = item.id,
                        japanese = item.japanese,
                        reading = item.reading,
                        romaji = item.romaji,
                        meaning = item.meaning,
                        category = item.category.name,
                        jlptLevel = item.jlptLevel,
                        sourceSentence = item.exampleJapanese,
                        sourceEnglish = item.exampleEnglish,
                        userNotes = item.mnemonicOrNote,
                        tags = "mined"
                    )
                )

                val existingSrs = database.flashcardSrsDao().getCardSrs(item.id)
                if (existingSrs == null) {
                    database.flashcardSrsDao().insertOrUpdate(
                        com.example.noignore.data.srs.FlashcardSrsEntity(
                            cardId = item.id,
                            category = item.category.name,
                            jlptLevel = item.jlptLevel,
                            repetition = 0,
                            intervalDays = 0,
                            easeFactor = 2.50f,
                            dueDateMs = System.currentTimeMillis(),
                            lastReviewedMs = 0L,
                            lapses = 0,
                            state = "NEW",
                            totalReviews = 0
                        )
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("JapaneseDeckRepo", "Failed to persist mined card: ${e.message}")
            }
        }
    }

    fun getAllItems(): List<JapaneseItem> {
        val todayStr = getTodayString()
        checkResetCardsToday(todayStr)

        val baseList = if (roomCurriculumItems.isNotEmpty()) {
            roomCurriculumItems
        } else {
            JlptExamDeckData.kanjiList + masterDeck.filter { m -> JlptExamDeckData.kanjiList.none { it.id == m.id } } + JlptExamDeckData.jlptExamQuestions + JlptExamDeckExtended.extendedExamQuestions
        }

        val fullDeck = synchronized(customItems) {
            val unaddedCustom = customItems.filter { c -> baseList.none { it.id == c.id } }
            baseList + unaddedCustom
        }

        return fullDeck.map { enrichItem(it) }
    }

    private fun enrichItem(item: JapaneseItem): JapaneseItem {
        val onyomiRom = if (item.onyomiRomaji.isNotBlank()) item.onyomiRomaji else com.example.noignore.japanese.util.JapaneseRomajiHelper.toRomaji(item.onyomi)
        val kunyomiRom = if (item.kunyomiRomaji.isNotBlank()) item.kunyomiRomaji else com.example.noignore.japanese.util.JapaneseRomajiHelper.toRomaji(item.kunyomi.replace("・", "").replace("-", ""))
        val radRom = if (item.radicalRomaji.isNotBlank()) item.radicalRomaji else com.example.noignore.japanese.util.JapaneseRomajiHelper.toRomaji(item.radical.substringBefore(" "))
        val exRom = if (item.exampleRomaji.isNotBlank()) item.exampleRomaji else com.example.noignore.japanese.util.JapaneseRomajiHelper.toRomaji(item.exampleReading.ifBlank { item.exampleJapanese })
        val enrichedCompounds = item.compounds.map { c ->
            if (c.romaji.isNotBlank()) c else c.copy(romaji = com.example.noignore.japanese.util.JapaneseRomajiHelper.toRomaji(c.reading))
        }
        val mastery = prefs.getInt(PREF_MASTERY_PREFIX + item.id, 0)
        return item.copy(
            onyomiRomaji = onyomiRom,
            kunyomiRomaji = kunyomiRom,
            radicalRomaji = radRom,
            exampleRomaji = exRom,
            compounds = enrichedCompounds,
            mastery = mastery
        )
    }

    fun setMastery(itemId: String, newMastery: Int) {
        val clamped = newMastery.coerceIn(0, 3)
        prefs.edit().putInt(PREF_MASTERY_PREFIX + itemId, clamped).apply()
        if (clamped == 3) {
            addKaoCoins(5)
        }
    }

    fun getItemsByCategory(category: JapaneseCategory): List<JapaneseItem> {
        return getAllItems().filter { it.category == category }
    }

    fun getItemsByJlptLevel(level: String): List<JapaneseItem> {
        return getAllItems().filter { it.jlptLevel.equals(level, ignoreCase = true) }
    }

    fun getKanjiDeck(level: String? = null): List<JapaneseItem> {
        val kanji = getAllItems().filter { it.category == JapaneseCategory.KANJI }
        return if (level != null && level != "ALL") {
            kanji.filter { it.jlptLevel.equals(level, ignoreCase = true) }
        } else {
            kanji
        }
    }

    fun getJlptExamDeck(level: String? = null): List<JapaneseItem> {
        val questions = getAllItems().filter { it.category == JapaneseCategory.JLPT_EXAM }
        return if (level != null && level != "ALL") {
            questions.filter { it.jlptLevel.equals(level, ignoreCase = true) }
        } else {
            questions
        }
    }

    fun getKaoCoins(): Int {
        return prefs.getInt(PREF_KAO_COINS, 25) // Start with 25 Kao coins welcome bonus!
    }

    fun addKaoCoins(amount: Int): Int {
        val current = getKaoCoins()
        val updated = current + amount
        prefs.edit().putInt(PREF_KAO_COINS, updated).apply()
        return updated
    }

    fun getCardsStudiedToday(): Int {
        val todayStr = getTodayString()
        checkResetCardsToday(todayStr)
        return prefs.getInt(PREF_CARDS_TODAY, 0)
    }

    fun getTotalReviews(): Int {
        return prefs.getInt(PREF_TOTAL_REVIEWS, 0)
    }

    fun getSrsCardData(itemId: String): SrsCardData {
        val rep = prefs.getInt(PREF_SRS_REP_PREFIX + itemId, 0)
        val interval = prefs.getInt(PREF_SRS_INTERVAL_PREFIX + itemId, 0)
        val ease = prefs.getFloat(PREF_SRS_EASE_PREFIX + itemId, 2.50f)
        val due = prefs.getLong(PREF_SRS_DUE_PREFIX + itemId, 0L)
        val last = prefs.getLong(PREF_SRS_LAST_PREFIX + itemId, 0L)
        val lapses = prefs.getInt(PREF_SRS_LAPSES_PREFIX + itemId, 0)
        val stateStr = prefs.getString(PREF_SRS_STATE_PREFIX + itemId, null)
        val state = if (stateStr != null) {
            try {
                SrsCardState.valueOf(stateStr)
            } catch (e: Exception) {
                if (rep > 0) SrsCardState.REVIEW else SrsCardState.NEW
            }
        } else {
            if (rep > 0) SrsCardState.REVIEW else SrsCardState.NEW
        }

        return SrsCardData(
            itemId = itemId,
            repetition = rep,
            intervalDays = interval,
            easeFactor = ease,
            dueDateMs = due,
            lastReviewedMs = last,
            lapses = lapses,
            state = state
        )
    }

    fun recordAnkiReview(itemId: String, rating: AnkiRating, category: String = "", jlptLevel: String = ""): SrsCardData {
        val todayStr = getTodayString()
        checkResetCardsToday(todayStr)

        val currentSrs = getSrsCardData(itemId)
        val updatedSrs = currentSrs.processRating(rating)

        val currentCardsToday = prefs.getInt(PREF_CARDS_TODAY, 0) + 1
        val totalReviews = prefs.getInt(PREF_TOTAL_REVIEWS, 0) + 1

        val currentMastery = prefs.getInt(PREF_MASTERY_PREFIX + itemId, 0)
        val newMastery = when (rating) {
            AnkiRating.AGAIN -> 0
            AnkiRating.HARD -> maxOf(1, currentMastery)
            AnkiRating.GOOD -> minOf(3, currentMastery + 1)
            AnkiRating.EASY -> 3
        }

        val coinsEarned = when (rating) {
            AnkiRating.AGAIN -> 1
            AnkiRating.HARD -> 3
            AnkiRating.GOOD -> 5
            AnkiRating.EASY -> 8
        }
        val currentCoins = getKaoCoins()

        // Persist FSRS schedule to Room Database & metrics to Jetpack DataStore asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            val entity = FlashcardSrsEntity(
                cardId = itemId,
                category = category,
                jlptLevel = jlptLevel,
                repetition = updatedSrs.repetition,
                intervalDays = updatedSrs.intervalDays,
                easeFactor = updatedSrs.easeFactor,
                dueDateMs = updatedSrs.dueDateMs,
                lastReviewedMs = updatedSrs.lastReviewedMs,
                lapses = updatedSrs.lapses,
                state = updatedSrs.state.name,
                totalReviews = totalReviews,
                lastRating = rating.name
            )
            srsDao.insertOrUpdate(entity)
            try {
                userMetricsDataStore.recordReview(coinsEarned)
            } catch (_: Exception) {}
        }

        val updatedCoins = currentCoins + coinsEarned
        prefs.edit()
            .putInt(PREF_CARDS_TODAY, currentCardsToday)
            .putInt(PREF_TOTAL_REVIEWS, totalReviews)
            .putInt(PREF_MASTERY_PREFIX + itemId, newMastery)
            .putInt(PREF_KAO_COINS, updatedCoins)
            .putInt(PREF_SRS_REP_PREFIX + itemId, updatedSrs.repetition)
            .putInt(PREF_SRS_INTERVAL_PREFIX + itemId, updatedSrs.intervalDays)
            .putFloat(PREF_SRS_EASE_PREFIX + itemId, updatedSrs.easeFactor)
            .putLong(PREF_SRS_DUE_PREFIX + itemId, updatedSrs.dueDateMs)
            .putLong(PREF_SRS_LAST_PREFIX + itemId, updatedSrs.lastReviewedMs)
            .putInt(PREF_SRS_LAPSES_PREFIX + itemId, updatedSrs.lapses)
            .putString(PREF_SRS_STATE_PREFIX + itemId, updatedSrs.state.name)
            .apply()

        try {
            com.example.noignore.widget.TeacherAikoWidgetProvider.updateAllWidgets(context)
        } catch (_: Exception) {}

        return updatedSrs
    }

    fun recordReview(itemId: String, isCorrect: Boolean) {
        recordAnkiReview(itemId, if (isCorrect) AnkiRating.GOOD else AnkiRating.AGAIN)
    }

    fun isCardDue(itemId: String): Boolean {
        val srs = getSrsCardData(itemId)
        val now = System.currentTimeMillis()
        return srs.repetition == 0 || (srs.dueDateMs != 0L && srs.dueDateMs <= now)
    }

    /**
     * Surfaces cards scheduled by the Spaced Repetition System at optimal intervals:
     * 1. Priority: LEARNING state (recent misses requiring immediate reinforcement).
     * 2. Overdue/due review cards sorted by urgency (earliest dueDateMs first).
     * 3. New cards awaiting initial study.
     * Cards scheduled in the future (dueDateMs > now) are held back to preserve the forgetting curve.
     */
    fun getDueCards(category: JapaneseCategory? = null, level: String? = null): List<JapaneseItem> {
        val all = if (category != null) getItemsByCategory(category) else getAllItems()
        val filtered = if (level != null && level != "ALL") {
            all.filter { it.jlptLevel.equals(level, ignoreCase = true) }
        } else {
            all
        }
        val now = System.currentTimeMillis()
        val dueCards = mutableListOf<Pair<JapaneseItem, SrsCardData>>()

        for (item in filtered) {
            val srs = getSrsCardData(item.id)
            if (srs.state == SrsCardState.NEW || srs.dueDateMs <= now) {
                dueCards.add(item to srs)
            }
        }

        return dueCards.sortedWith(
            compareBy<Pair<JapaneseItem, SrsCardData>> { (_, srs) ->
                when (srs.state) {
                    SrsCardState.LEARNING -> 0
                    SrsCardState.REVIEW, SrsCardState.MASTERED -> 1
                    SrsCardState.NEW -> 2
                }
            }.thenBy { (_, srs) -> srs.dueDateMs }
        ).map { it.first }
    }

    fun getSrsDeckSummary(level: String? = null): SrsDeckSummary {
        val all = if (level != null && level != "ALL") {
            getAllItems().filter { it.jlptLevel.equals(level, ignoreCase = true) }
        } else {
            getAllItems()
        }
        val now = System.currentTimeMillis()
        var newCount = 0
        var learningCount = 0
        var reviewCount = 0
        var totalRetentionSum = 0f
        var reviewedCount = 0

        for (item in all) {
            val srs = getSrsCardData(item.id)
            when (srs.state) {
                SrsCardState.NEW -> newCount++
                SrsCardState.LEARNING -> learningCount++
                SrsCardState.REVIEW, SrsCardState.MASTERED -> {
                    if (srs.dueDateMs <= now) {
                        reviewCount++
                    }
                    totalRetentionSum += srs.calculateMemoryRetention(now)
                    reviewedCount++
                }
            }
        }

        val avgRetention = if (reviewedCount > 0) (totalRetentionSum / reviewedCount) else 0.85f

        return SrsDeckSummary(
            newCardsCount = newCount,
            learningCardsCount = learningCount,
            reviewCardsCount = reviewCount,
            totalStudiedToday = getCardsStudiedToday(),
            avgRetentionRate = avgRetention
        )
    }

    fun getStudyStreak(): Int {
        val count = prefs.getInt("renshuu_study_streak_days", 1)
        return maxOf(1, count)
    }

    /**
     * Identifies Anki "Leech" cards: items with 3 or more lapses (struggled repeatedly with Again).
     * These require special mnemonic breakdown and focused remediation.
     */
    fun getLeechCards(): List<Pair<JapaneseItem, SrsCardData>> {
        val all = getAllItems()
        val leeches = mutableListOf<Pair<JapaneseItem, SrsCardData>>()
        for (item in all) {
            val srs = getSrsCardData(item.id)
            if (srs.lapses >= 3) {
                leeches.add(item to srs)
            }
        }
        return leeches.sortedByDescending { it.second.lapses }
    }

    fun setKaoCoins(amount: Int) {
        val safeAmount = maxOf(0, amount)
        prefs.edit().putInt(PREF_KAO_COINS, safeAmount).apply()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userMetricsDataStore.setKaoCoins(safeAmount)
            } catch (_: Exception) {}
        }
    }

    fun setCardsStudiedToday(count: Int) {
        val todayStr = getTodayString()
        prefs.edit()
            .putString(PREF_LAST_STUDY_DATE, todayStr)
            .putInt(PREF_CARDS_TODAY, maxOf(0, count))
            .apply()
    }

    private fun checkResetCardsToday(todayStr: String) {
        val lastDate = prefs.getString(PREF_LAST_STUDY_DATE, "")
        if (lastDate != todayStr) {
            prefs.edit()
                .putString(PREF_LAST_STUDY_DATE, todayStr)
                .putInt(PREF_CARDS_TODAY, 0)
                .apply()
        }
    }

    private fun getTodayString(): String {
        val cal = Calendar.getInstance()
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
    }

    /**
     * Local zero-latency sentence mining: tokenizes Japanese sentences and matches against repository vocabulary.
     */
    fun parseSentenceWithMining(sentence: String): List<com.example.noignore.japanese.util.ParsedToken> {
        return com.example.noignore.japanese.util.JapaneseSentenceTokenizer.tokenize(sentence, getAllItems())
    }
}
