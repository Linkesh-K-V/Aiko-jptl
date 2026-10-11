package com.example.noignore.japanese.data

import com.example.noignore.japanese.model.DialogueTurn
import com.example.noignore.japanese.model.JlptStarSentenceQuestion
import com.example.noignore.japanese.model.ParticleDrillQuestion
import com.example.noignore.japanese.model.SurvivalRoleplayScenario

/**
 * Authentic Japanese Study Materials:
 * 1. JLPT Sentence Rearrangement "Star" (並べ替え ★) Questions
 * 2. High-Yield Particle Cloze Drills (助詞バトル)
 * 3. Situational Survival Roleplay Modules (Convenience Store, Izakaya, Train station)
 */
object InteractivePracticeRepository {

    val starQuestions: List<JlptStarSentenceQuestion> = listOf(
        JlptStarSentenceQuestion(
            id = "star_n5_1",
            jlptLevel = "N5",
            sentencePrefix = "わたしは",
            sentenceSuffix = "行きます。",
            scrambledTiles = listOf("友達", "電車", "と", "で"),
            correctOrderIndices = listOf(0, 2, 1, 3), // 友達 [0] と [2] 電車 [1] で [3] -> 友達と電車で
            starPositionIndex = 2, // The 3rd blank is "電車"
            fullSentenceJapanese = "わたしは友達と電車で行きます。",
            fullSentenceReading = "わたしはともだちとでんしゃでいきます。",
            englishMeaning = "I am going by train with my friend.",
            grammarExplanation = "Structure: [Person] + と (together with) + [Means of Transport] + で (by means of) + 行きます。"
        ),
        JlptStarSentenceQuestion(
            id = "star_n5_2",
            jlptLevel = "N5",
            sentencePrefix = "図書館で",
            sentenceSuffix = "読みました。",
            scrambledTiles = listOf("本", "日本語", "の", "を"),
            correctOrderIndices = listOf(1, 2, 0, 3), // 日本語 [1] の [2] 本 [0] を [3]
            starPositionIndex = 2, // The 3rd blank is "本"
            fullSentenceJapanese = "図書館で日本語の本を読みました。",
            fullSentenceReading = "としょかんでにほんごのほんをよみました。",
            englishMeaning = "I read a Japanese book at the library.",
            grammarExplanation = "Noun modifier: 日本語 (Noun A) + の + 本 (Noun B) + を (object marker) + 読みました。"
        ),
        JlptStarSentenceQuestion(
            id = "star_n4_1",
            jlptLevel = "N4",
            sentencePrefix = "日本へ",
            sentenceSuffix = "思っています。",
            scrambledTiles = listOf("旅行", "に", "行こう", "と"),
            correctOrderIndices = listOf(0, 1, 2, 3), // 旅行 [0] に [1] 行こう [2] と [3]
            starPositionIndex = 2, // The 3rd blank is "行こう"
            fullSentenceJapanese = "日本へ旅行に行こうと思っています。",
            fullSentenceReading = "にほんへりょこうにいこうとおもっています。",
            englishMeaning = "I am thinking of going on a trip to Japan.",
            grammarExplanation = "Volitional form + と思っている: [Purpose Noun] + に行く (go to do) in volitional 行こう + と思っています (planning to)."
        ),
        JlptStarSentenceQuestion(
            id = "star_n4_2",
            jlptLevel = "N4",
            sentencePrefix = "薬を",
            sentenceSuffix = "寝てください。",
            scrambledTiles = listOf("飲んで", "から", "ゆっくり", "早く"),
            correctOrderIndices = listOf(0, 1, 3, 2), // 飲んで [0] から [1] 早く [3] ゆっくり [2] -> 薬を飲んでから早くゆっくり寝てください
            starPositionIndex = 1,
            fullSentenceJapanese = "薬を飲んでからゆっくり寝てください。",
            fullSentenceReading = "くすりをのんでからゆっくりねてください。",
            englishMeaning = "Please get plenty of rest after taking your medicine.",
            grammarExplanation = "Te-form + から (after doing X): 飲んで (te-form) + から (after) + adverb modifying 寝てください."
        ),
        JlptStarSentenceQuestion(
            id = "star_n3_1",
            jlptLevel = "N3",
            sentencePrefix = "大切な試験の",
            sentenceSuffix = "わけにはいかない。",
            scrambledTiles = listOf("前日", "に", "夜更かし", "をする"),
            correctOrderIndices = listOf(0, 1, 2, 3), // 前日 [0] に [1] 夜更かし [2] をする [3]
            starPositionIndex = 2, // 3rd blank is "夜更かし"
            fullSentenceJapanese = "大切な試験の前日に夜更かしをするわけにはいかない。",
            fullSentenceReading = "たいせつなしけんのぜんじつによふかしをするわけにはいかない。",
            englishMeaning = "I cannot afford to stay up late the night before an important exam.",
            grammarExplanation = "Grammar: [Verb Plain Dict Form] + わけにはいかない (cannot afford to / socially or morally impossible)."
        )
    )

    val particleDrills: List<ParticleDrillQuestion> = listOf(
        ParticleDrillQuestion(
            id = "p_1",
            jlptLevel = "N5",
            sentenceWithBlank = "図書館 [___] 本を借りました。",
            blankFuriganaSentence = "としょかん [___] ほんをかりました。",
            correctParticle = "で",
            options = listOf("で", "に", "を", "へ"),
            englishMeaning = "I borrowed a book at the library.",
            grammaticalReason = "で marks the location where an active event takes place (borrowing). に is for static existence or direction of entry."
        ),
        ParticleDrillQuestion(
            id = "p_2",
            jlptLevel = "N5",
            sentenceWithBlank = "机の上 [___] 猫がいます。",
            blankFuriganaSentence = "つくえのうえ [___] ねこがいます。",
            correctParticle = "に",
            options = listOf("に", "で", "を", "へ"),
            englishMeaning = "There is a cat on top of the desk.",
            grammaticalReason = "に marks the location of existence with verbs like います (animate) or あります (inanimate)."
        ),
        ParticleDrillQuestion(
            id = "p_3",
            jlptLevel = "N5",
            sentenceWithBlank = "毎朝7時 [___] 起きます。",
            blankFuriganaSentence = "まいあさしちじ [___] おきます。",
            correctParticle = "に",
            options = listOf("に", "で", "を", "は"),
            englishMeaning = "I wake up at 7:00 every morning.",
            grammaticalReason = "に marks specific numeric points in time (7時, 月曜日). Non-numeric relative time (今日, 明日) does not take に."
        ),
        ParticleDrillQuestion(
            id = "p_4",
            jlptLevel = "N4",
            sentenceWithBlank = "電車 [___] 降ります。",
            blankFuriganaSentence = "でんしゃ [___] おります。",
            correctParticle = "を",
            options = listOf("を", "から", "で", "に"),
            englishMeaning = "I get off the train.",
            grammaticalReason = "を marks the point of departure or separation when getting off transport (電車を降りる, 部屋を出る)."
        ),
        ParticleDrillQuestion(
            id = "p_5",
            jlptLevel = "N4",
            sentenceWithBlank = "電車 [___] 乗ります。",
            blankFuriganaSentence = "でんしゃ [___] のります。",
            correctParticle = "に",
            options = listOf("に", "で", "を", "へ"),
            englishMeaning = "I board / get on the train.",
            grammaticalReason = "に marks the target destination or vehicle you are entering/boarding (電車に乗る, バスに乗る)."
        ),
        ParticleDrillQuestion(
            id = "p_6",
            jlptLevel = "N4",
            sentenceWithBlank = "友達 [___] 会いました。",
            blankFuriganaSentence = "ともだち [___] あいました。",
            correctParticle = "に",
            options = listOf("に", "を", "で", "から"),
            englishMeaning = "I met my friend.",
            grammaticalReason = "The target person you meet with 会う takes the particle に (友達に会う), NOT を."
        )
    )

    val survivalScenarios: List<SurvivalRoleplayScenario> = listOf(
        SurvivalRoleplayScenario(
            id = "sc_combini",
            title = "Convenience Store Survival",
            japaneseTitle = "コンビニでお買い物",
            categoryEmoji = "🏪",
            locationDescription = "7-Eleven / Lawson / FamilyMart checkout counter",
            jlptLevel = "N5",
            survivalKeyPhrases = listOf("大丈夫です (Daijoubu desu - No thank you)", "お願いします (Onegai shimasu - Yes please)", "温めてください (Atatamete kudasai - Please heat it up)"),
            turns = listOf(
                DialogueTurn(
                    speakerRole = "店員 (Clerk)",
                    japaneseText = "いらっしゃいませ！お弁当温めますか？",
                    readingText = "いらっしゃいませ！おべんとうあたためますか？",
                    romajiText = "Irasshaimase! Obentou atatamemasu ka?",
                    englishText = "Welcome! Would you like your bento boxed meal warmed up in the microwave?",
                    culturalNuanceTip = "Convenience store staff automatically offer to microwave any prepared savory foods."
                ),
                DialogueTurn(
                    speakerRole = "客 (You)",
                    japaneseText = "はい、お願いします。",
                    readingText = "はい、おねがいします。",
                    romajiText = "Hai, onegai shimasu.",
                    englishText = "Yes, please!",
                    suggestedLearnerResponse = true
                ),
                DialogueTurn(
                    speakerRole = "店員 (Clerk)",
                    japaneseText = "袋はお付けしますか？",
                    readingText = "ふくろはおつけしますか？",
                    romajiText = "Fukuro wa otsuke shimasu ka?",
                    englishText = "Would you like a shopping bag? (Usually 3-5 yen extra in Japan).",
                    culturalNuanceTip = "Plastic bags cost a nominal fee in Japan. If you brought a bag or don't need one, say '大丈夫です' (Daijoubu desu)."
                ),
                DialogueTurn(
                    speakerRole = "客 (You)",
                    japaneseText = "あ、大丈夫です。レシートだけください。",
                    readingText = "あ、だいじょうぶです。れしーとだけください。",
                    romajiText = "A, daijoubu desu. Reshiito dake kudasai.",
                    englishText = "Ah, I'm fine without one. Just the receipt, please.",
                    suggestedLearnerResponse = true
                ),
                DialogueTurn(
                    speakerRole = "店員 (Clerk)",
                    japaneseText = "かしこまりました。750円になります。",
                    readingText = "かしこまりました。ななひゃくごじゅうえんになります。",
                    romajiText = "Kashikomarimashita. Nanahyaku gojuu-en ni narimasu.",
                    englishText = "Certainly. That will be 750 yen."
                )
            )
        ),
        SurvivalRoleplayScenario(
            id = "sc_izakaya",
            title = "Izakaya & Restaurant Ordering",
            japaneseTitle = "居酒屋で乾杯",
            categoryEmoji = "🍻",
            locationDescription = "Tokyo Izakaya / Japanese pub at night",
            jlptLevel = "N4",
            survivalKeyPhrases = listOf("とりあえず生で！ (Toriaezu nama de - Draft beer to start!)", "お会計お願いします (Okaikei onegai shimasu - Check please)", "すみません！ (Sumimasen - Excuse me!)"),
            turns = listOf(
                DialogueTurn(
                    speakerRole = "客 (You)",
                    japaneseText = "すみません！とりあえず生ビール二つお願いします！",
                    readingText = "すみません！とりあえずなまびーるふたつおねがいします！",
                    romajiText = "Sumimasen! Toriaezu nama biiru futatsu onegai shimasu!",
                    englishText = "Excuse me! Two draft beers to get us started, please!",
                    culturalNuanceTip = "'とりあえず生' (toriaezu nama) is Japan's most famous restaurant custom — ordering fresh beers immediately so drinks arrive quickly while you browse the menu.",
                    suggestedLearnerResponse = true
                ),
                DialogueTurn(
                    speakerRole = "店員 (Staff)",
                    japaneseText = "喜んで！生二つですね。お通しをお持ちしました。",
                    readingText = "よろこんで！なまふたつですね。おとおしをおもちしました。",
                    romajiText = "Yorokonde! Nama futatsu desu ne. Otooshi o omochi shimashita.",
                    englishText = "Gladly! Two draft beers. Here is your 'Otoushi' (table starter appetizer)."
                ),
                DialogueTurn(
                    speakerRole = "客 (You)",
                    japaneseText = "おすすめの焼き鳥は何ですか？",
                    readingText = "おすすめのやきとりはなんですか？",
                    romajiText = "Osusume no yakitori wa nan desu ka?",
                    englishText = "What grilled chicken skewers do you recommend?",
                    suggestedLearnerResponse = true
                ),
                DialogueTurn(
                    speakerRole = "店員 (Staff)",
                    japaneseText = "本日はつくねとねぎまが人気ですよ！",
                    readingText = "ほんじつはつくねとねぎまがにんきですよ！",
                    romajiText = "Honjitsu wa tsukune to negima ga ninki desu yo!",
                    englishText = "Today our minced meatball skewers and chicken-with-leek are very popular!"
                ),
                DialogueTurn(
                    speakerRole = "客 (You)",
                    japaneseText = "じゃあ、それをお願いします。ごちそうさまでした！お会計お願いします。",
                    readingText = "じゃあ、それをおねがいします。ごちそうさまでした！おかいけいおねがいします。",
                    romajiText = "Jaa, sore o onegai shimasu. Gochisousama deshita! Okaikei onegai shimasu.",
                    englishText = "Then we'll have those please. That was delicious! Could we have the bill please?",
                    suggestedLearnerResponse = true
                )
            )
        ),
        SurvivalRoleplayScenario(
            id = "sc_train",
            title = "Train & Station Navigation",
            japaneseTitle = "駅と電車の乗り換え",
            categoryEmoji = "🚆",
            locationDescription = "Shinjuku Station Yamanote Line platform",
            jlptLevel = "N4",
            survivalKeyPhrases = listOf("この電車は〜に止まりますか？ (Does this train stop at...?)", "何番線ですか？ (Which track platform is it?)", "降りるドアは右側です (The exit door is on the right)"),
            turns = listOf(
                DialogueTurn(
                    speakerRole = "客 (You)",
                    japaneseText = "すみません、東京駅に行きたいんですが、何番線ですか？",
                    readingText = "すみません、とうきょうえきにいきたいんですが、なんばんせんですか？",
                    romajiText = "Sumimasen, Toukyou eki ni ikitai n desu ga, nanban sen desu ka?",
                    englishText = "Excuse me, I'd like to go to Tokyo Station, which track number should I go to?",
                    suggestedLearnerResponse = true
                ),
                DialogueTurn(
                    speakerRole = "駅員 (Station Attendant)",
                    japaneseText = "1番線の山手線内回りにお乗りください。次の快速でも行けますよ。",
                    readingText = "いちばんせんのやまのてせんうちまわりにおのりください。つぎのかいそくでもいけますよ。",
                    romajiText = "Ichiban sen no Yamanote sen uchimawari ni onori kudasai. Tsugi no kaisoku demo ikemasu yo.",
                    englishText = "Please take track 1, Yamanote Line inner loop. The next rapid train also goes there.",
                    culturalNuanceTip = "Large hub stations in Japan use color coding and track numbers (番線 - bansen) prominently."
                ),
                DialogueTurn(
                    speakerRole = "アナウンス (Train Chime)",
                    japaneseText = "次は、東京、東京です。お出口は右側です。お忘れ物のないようご注意ください。",
                    readingText = "つぎは、とうきょう、とうきょうです。おでぐちはみぎがわです。おわすれもののないようごちゅういください。",
                    romajiText = "Tsugi wa, Toukyou, Toukyou desu. Odeguchi wa migigawa desu. O-wasuremono no nai you go-chuui kudasai.",
                    englishText = "Next stop is Tokyo, Tokyo. The exit doors are on the right side. Please check behind you for forgotten luggage."
                )
            )
        )
    )
}
