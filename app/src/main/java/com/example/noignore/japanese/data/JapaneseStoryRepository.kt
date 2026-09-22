package com.example.noignore.japanese.data

import android.content.Context
import android.content.SharedPreferences
import com.example.noignore.japanese.model.JapaneseStory
import com.example.noignore.japanese.model.ListeningExercise
import com.example.noignore.japanese.model.ListeningExerciseType
import com.example.noignore.japanese.model.ReadingLesson
import com.example.noignore.japanese.model.ReadingPassageSentence
import com.example.noignore.japanese.model.StoryQuizQuestion
import com.example.noignore.japanese.model.StorySentence
import com.example.noignore.japanese.model.StoryVocab

/**
 * Repository providing graded Japanese short stories, listening drills, and reading lessons.
 */
class JapaneseStoryRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("japanese_reading_listening_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val PREF_COMPLETED_STORY_PREFIX = "completed_story_"
        private const val PREF_COMPLETED_LISTENING_PREFIX = "completed_listening_"
        private const val PREF_COMPLETED_READING_PREFIX = "completed_reading_"
    }

    // ==========================================
    // 1. GRADED SHORT STORIES FOR READING PRACTICE
    // ==========================================
    val stories: List<JapaneseStory> = listOf(
        JapaneseStory(
            id = "story_aiko_tea",
            title = "愛子先生と朝のお茶",
            titleEnglish = "Sensei Aiko & The Morning Tea",
            jlptLevel = "N5",
            emoji = "🍵",
            summary = "A peaceful morning routine with Sensei Aiko, enjoying green tea and mindfulness before daily training.",
            sentences = listOf(
                StorySentence(
                    id = 1,
                    japanese = "愛子先生は毎朝六時に起きます。",
                    furigana = "あいこせんせいは まいあさ ろくじに おきます。",
                    romaji = "Aiko sensei wa maiasa rokuji ni okimasu.",
                    english = "Sensei Aiko wakes up at six o'clock every morning."
                ),
                StorySentence(
                    id = 2,
                    japanese = "部屋の窓を開けて、静かな朝の空気を吸います。",
                    furigana = "へやの まどを あけて、しずかな あさの くうきを すいます。",
                    romaji = "Heya no mado o akete, shizuka na asa no kuuki o suimasu.",
                    english = "She opens the room window and breathes in the calm morning air."
                ),
                StorySentence(
                    id = 3,
                    japanese = "庭では、小さい小鳥が楽しそうに歌っています。",
                    furigana = "にわでは、ちいさい ことりが たのしそうに うたっています。",
                    romaji = "Niwa de wa, chiisai kotori ga tanoshisou ni utatteimasu.",
                    english = "In the garden, little birds are singing cheerfully."
                ),
                StorySentence(
                    id = 4,
                    japanese = "お湯を沸かして、温かい緑茶を丁寧に淹れます。",
                    furigana = "おゆを わかして、あたたかい りょくちゃを ていねいに いれます。",
                    romaji = "Oyu o wakashite, atatakai ryokucha o teinei ni iremasu.",
                    english = "She boils hot water and carefully brews warm green tea."
                ),
                StorySentence(
                    id = 5,
                    japanese = "「今日も一日、一歩ずつ進みましょう」と笑顔で言いました。",
                    furigana = "「きょうも いちにち、いっぽずつ すすみましょう」と えがおで いいました。",
                    romaji = "\"Kyou mo ichinichi, ippo zutsu susumimashou\" to egao de iimashita.",
                    english = "\"Let us move forward one step at a time today,\" she said with a smile."
                )
            ),
            keyVocabulary = listOf(
                StoryVocab("毎朝", "まいあさ", "Every morning"),
                StoryVocab("静か", "しずか", "Quiet / Calm"),
                StoryVocab("小鳥", "ことり", "Little bird"),
                StoryVocab("緑茶", "りょくちゃ", "Green tea"),
                StoryVocab("一歩ずつ", "いっぽずつ", "Step by step")
            ),
            comprehensionQuestions = listOf(
                StoryQuizQuestion(
                    id = "q_tea_1",
                    question = "愛子先生は何時に起きますか？ (What time does Sensei Aiko wake up?)",
                    options = listOf("六時 (6:00 AM)", "七時 (7:00 AM)", "五時 (5:00 AM)", "八時 (8:00 AM)"),
                    correctOptionIndex = 0,
                    explanation = "The first sentence states: 『愛子先生は毎朝六時に起きます』(Sensei Aiko wakes up at 6:00 every morning)."
                ),
                StoryQuizQuestion(
                    id = "q_tea_2",
                    question = "愛子先生は何を淹れましたか？ (What did Sensei Aiko brew?)",
                    options = listOf("温かい緑茶 (Warm green tea)", "冷たいコーヒー (Cold coffee)", "紅茶 (Black tea)", "水 (Water)"),
                    correctOptionIndex = 0,
                    explanation = "Sentence 4 states: 『温かい緑茶を丁寧に淹れます』(She carefully brews warm green tea)."
                )
            )
        ),
        JapaneseStory(
            id = "story_black_cat",
            title = "京都の路地と不思議な黒猫",
            titleEnglish = "The Kyoto Alley & The Mysterious Cat",
            jlptLevel = "N5",
            emoji = "🐈‍⬛",
            summary = "Follow a clever black cat through ancient stone streets in Kyoto to discover a secret tranquil shrine.",
            sentences = listOf(
                StorySentence(
                    id = 1,
                    japanese = "午後の京都はとても静かでした。",
                    furigana = "ごごの きょうとは とても しずかでした。",
                    romaji = "Gogo no Kyouto wa totemo shizuka deshita.",
                    english = "Kyoto in the afternoon was very quiet."
                ),
                StorySentence(
                    id = 2,
                    japanese = "古い石の道を歩いていると、一匹の黒猫に出会いました。",
                    furigana = "ふるい いしの みちを あるいていると、いっぴきの くろねこに であいました。",
                    romaji = "Furui ishi no michi o aruiteiru to, ippiki no kuro neko ni deaimashita.",
                    english = "While walking along the old stone path, I encountered a single black cat."
                ),
                StorySentence(
                    id = 3,
                    japanese = "黒猫は一度こちらを見て、「にゃー」と鳴いて走り出しました。",
                    furigana = "くろねこは いちど こちらを みて、「にゃー」と ないて はしりだしました。",
                    romaji = "Kuro neko wa ichido kochira o mite, \"nyaa\" to naite hashiridashimashita.",
                    english = "The black cat looked at me once, meowed, and started running."
                ),
                StorySentence(
                    id = 4,
                    japanese = "細い路地を追いかけると、赤い鳥居と美しい庭がありました。",
                    furigana = "ほそい ろじを おいかけると、あかい とりいと うつくしい にわが ありました。",
                    romaji = "Hosoi roji o oikakeru to, akai torii to utsukushii niwa ga arimashita.",
                    english = "Following it into a narrow alley, there was a red torii gate and a beautiful garden."
                ),
                StorySentence(
                    id = 5,
                    japanese = "猫は苔の上で丸くなって、気持ちよさそうに眠りました。",
                    furigana = "ねこは こけの うえで まるくなって、きもちよさそうに ねむりました。",
                    romaji = "Neko wa koke no ue de marukunatte, kimochiyosasou ni nemurimashita.",
                    english = "The cat curled up on the green moss and fell asleep peacefully."
                )
            ),
            keyVocabulary = listOf(
                StoryVocab("路地", "ろじ", "Alley / Lane"),
                StoryVocab("石の道", "いしのみち", "Stone path"),
                StoryVocab("鳥居", "とりい", "Shinto shrine gate"),
                StoryVocab("苔", "こけ", "Moss"),
                StoryVocab("丸くなる", "まるくなる", "To curl up into a ball")
            ),
            comprehensionQuestions = listOf(
                StoryQuizQuestion(
                    id = "q_cat_1",
                    question = "主人公は誰に出会いましたか？ (Who did the protagonist meet?)",
                    options = listOf("一匹の黒猫 (A black cat)", "白い犬 (A white dog)", "愛子先生 (Sensei Aiko)", "観光客 (A tourist)"),
                    correctOptionIndex = 0,
                    explanation = "The story mentions: 『一匹の黒猫に出会いました』(I met a black cat)."
                ),
                StoryQuizQuestion(
                    id = "q_cat_2",
                    question = "路地の先に何がありましたか？ (What was at the end of the alley?)",
                    options = listOf("赤い鳥居と美しい庭 (A red torii gate and beautiful garden)", "大きな駅 (A large train station)", "猫のカフェ (A cat cafe)", "古いお寺の門 (An old temple gate)"),
                    correctOptionIndex = 0,
                    explanation = "Sentence 4 describes: 『赤い鳥居と美しい庭がありました』(There was a red torii gate and a beautiful garden)."
                )
            )
        ),
        JapaneseStory(
            id = "story_onigiri",
            title = "美味しい三角おにぎり",
            titleEnglish = "The Delicious Triangle Onigiri",
            jlptLevel = "N5",
            emoji = "🍙",
            summary = "Learn the loving tradition and technique of making triangular rice balls with warm rice and crispy seaweed.",
            sentences = listOf(
                StorySentence(
                    id = 1,
                    japanese = "今日はお昼ご飯におにぎりを作ります。",
                    furigana = "きょうは おひるごはんに おにぎりを つくります。",
                    romaji = "Kyou wa ohirugohan ni onigiri o tsukurimasu.",
                    english = "Today I am making onigiri (rice balls) for lunch."
                ),
                StorySentence(
                    id = 2,
                    japanese = "まず、炊きたての温かいご飯を用意します。",
                    furigana = "まず、たきたての あたたかい ごはんを よういします。",
                    romaji = "Mazu, takitate no atatakai gohan o youishimasu.",
                    english = "First, prepare freshly cooked, warm steamed rice."
                ),
                StorySentence(
                    id = 3,
                    japanese = "手に少し塩をつけて、ご飯の真ん中に焼き鮭を入れます。",
                    furigana = "てに すこし しおを つけて、ごはんの まんなかに やきざけを いれます。",
                    romaji = "Te ni sukoshi shio o tsukete, gohan no mannaka ni yakizake o iremasu.",
                    english = "Put a little salt on your hands and place grilled salmon into the center of the rice."
                ),
                StorySentence(
                    id = 4,
                    japanese = "両手で優しく三角の形に握ります。",
                    furigana = "りょうてで やさしく さんかくの かたちに にぎります。",
                    romaji = "Ryoute de yasashiku sankaku no katachi ni nigirimasu.",
                    english = "Gently shape it into a triangle using both hands."
                ),
                StorySentence(
                    id = 5,
                    japanese = "最後にパリパリの黒い海苔を巻いて、完成です！とても美味しいです。",
                    furigana = "さいごに パリパリの くろい のりを まいて、かんせいです！とても おいしいです。",
                    romaji = "Saigo ni paripari no kuroi nori o maite, kansei desu! Totemo oishii desu.",
                    english = "Finally, wrap it with crisp black seaweed, and it's complete! It is delicious."
                )
            ),
            keyVocabulary = listOf(
                StoryVocab("炊きたて", "たきたて", "Freshly cooked (rice)"),
                StoryVocab("焼き鮭", "やきざけ", "Grilled salmon"),
                StoryVocab("三角", "さんかく", "Triangle"),
                StoryVocab("握る", "にぎる", "To shape / clasp with hands"),
                StoryVocab("海苔", "のり", "Edible seaweed")
            ),
            comprehensionQuestions = listOf(
                StoryQuizQuestion(
                    id = "q_onigiri_1",
                    question = "おにぎりの中央に何を入れましたか？ (What was placed in the middle of the onigiri?)",
                    options = listOf("焼き鮭 (Grilled salmon)", "梅干し (Pickled plum)", "ツナマヨ (Tuna mayo)", "牛肉 (Beef)"),
                    correctOptionIndex = 0,
                    explanation = "Sentence 3 says: 『ご飯の真ん中に焼き鮭を入れます』(Put grilled salmon in the middle of the rice)."
                ),
                StoryQuizQuestion(
                    id = "q_onigiri_2",
                    question = "おにぎりはどんな形に握りましたか？ (What shape was the onigiri made into?)",
                    options = listOf("三角の形 (Triangle shape)", "丸い形 (Round shape)", "四角い形 (Square shape)", "星の形 (Star shape)"),
                    correctOptionIndex = 0,
                    explanation = "Sentence 4 states: 『両手で優しく三角の形に握ります』(Gently shape into a triangle)."
                )
            )
        ),
        JapaneseStory(
            id = "story_autumn_festival",
            title = "秋の紅葉と祭り太鼓",
            titleEnglish = "Autumn Leaves & Festival Drums",
            jlptLevel = "N4",
            emoji = "🍁",
            summary = "An atmospheric evening festival in Nara during peak autumn maple colors, accompanied by reverberating taiko drums.",
            sentences = listOf(
                StorySentence(
                    id = 1,
                    japanese = "十月の奈良の夜は、涼しい風が吹いていました。",
                    furigana = "じゅうがつの ならの よるは、すずしい かぜが ふいていました。",
                    romaji = "Juu-gatsu no Nara no yoru wa, suzushii kaze ga fuiteimashita.",
                    english = "On an October night in Nara, a cool breeze was blowing."
                ),
                StorySentence(
                    id = 2,
                    japanese = "寺の周りのもみじは真っ赤に染まり、提灯の光に照らされています。",
                    furigana = "てらの まわりの もみじは まっかに そまり、ちょうちんの ひかりに てらされています。",
                    romaji = "Tera no mawari no momiji wa makka ni somari, chouchin no hikari ni terasareteimasu.",
                    english = "The maple leaves around the temple were dyed deep crimson, illuminated by lantern light."
                ),
                StorySentence(
                    id = 3,
                    japanese = "遠くからドンドンと力強い太鼓の音が響いてきました。",
                    furigana = "とおくから ドンドンと ちからづよい たいこの おとが ひびいてきました。",
                    romaji = "Tooku kara don don to chikaradzuyoi taiko no oto ga hibiitekimashita.",
                    english = "From afar, the powerful 'don-don' boom of taiko drums resonated."
                ),
                StorySentence(
                    id = 4,
                    japanese = "屋台で温かいみたらし団子を買い、友達と一緒に食べました。",
                    furigana = "やたいで あたたかい みたらしだんごを かい、ともだちと いっしょに たべました。",
                    romaji = "Yatai de atatakai mitarashi dango o kai, tomodachi to issho ni tabemashita.",
                    english = "I bought warm mitarashi dango at a festival stall and ate them together with my friend."
                ),
                StorySentence(
                    id = 5,
                    japanese = "日本の秋の美しさを心から感じた特別な夜でした。",
                    furigana = "にほんの あきの うつくしさを こころから かんじた とくべつな よるでした。",
                    romaji = "Nihon no aki no utsukushisa o kokoro kara kanjita tokubetsu na yoru deshita.",
                    english = "It was a special night where I truly felt the beauty of Japanese autumn."
                )
            ),
            keyVocabulary = listOf(
                StoryVocab("もみじ", "もみじ", "Japanese maple / Autumn leaves"),
                StoryVocab("提灯", "ちょうちん", "Paper lantern"),
                StoryVocab("太鼓", "たいこ", "Japanese taiko drum"),
                StoryVocab("屋台", "やたい", "Festival food stall"),
                StoryVocab("みたらし団子", "みたらしだんご", "Sweet soy sauce rice dumplings")
            ),
            comprehensionQuestions = listOf(
                StoryQuizQuestion(
                    id = "q_festival_1",
                    question = "もみじは何の光に照らされていましたか？ (By what light were the maple leaves illuminated?)",
                    options = listOf("提灯の光 (Lantern light)", "月の光 (Moonlight)", "車のライト (Car headlights)", "花火 (Fireworks)"),
                    correctOptionIndex = 0,
                    explanation = "Sentence 2 states: 『提灯の光に照らされています』(Illuminated by the light of lanterns)."
                ),
                StoryQuizQuestion(
                    id = "q_festival_2",
                    question = "屋台で何を買って食べましたか？ (What was bought and eaten at the stall?)",
                    options = listOf("温かいみたらし団子 (Warm mitarashi dumplings)", "たこ焼き (Takoyaki)", "りんご飴 (Candy apple)", "焼きそば (Yakisoba)"),
                    correctOptionIndex = 0,
                    explanation = "Sentence 4 states: 『屋台で温かいみたらし団子を買い』(Bought warm mitarashi dango at a stall)."
                )
            )
        ),
        JapaneseStory(
            id = "story_momotaro",
            title = "桃太郎の冒険",
            titleEnglish = "Momotaro's Adventure",
            jlptLevel = "N4",
            emoji = "🍑",
            summary = "A classic Japanese folktale of courage, friendship, and sharing kibi dango with faithful companions.",
            sentences = listOf(
                StorySentence(
                    id = 1,
                    japanese = "昔々、ある所におじいさんとおばあさんが住んでいました。",
                    furigana = "むかしむかし、ある ところに おじいさんと おばあさんが すんでいました。",
                    romaji = "Mukashi mukashi, aru tokoro ni ojiisan to obaasan ga sundeimashita.",
                    english = "Once upon a time, an old man and an old woman lived in a certain place."
                ),
                StorySentence(
                    id = 2,
                    japanese = "川から流れてきた大きな桃から、元気な男の子が生まれました。",
                    furigana = "かわから ながれてきた おおきな ももから、げんきな おとこのこが うまれました。",
                    romaji = "Kawa kara nagarete kita ookina momo kara, genki na otokonoko ga umaremashita.",
                    english = "From a giant peach that floated down the river, a energetic baby boy was born."
                ),
                StorySentence(
                    id = 3,
                    japanese = "二人はその子を「桃太郎」と名付け、大切に育てました。",
                    furigana = "ふたりは そのこを 「ももたろう」と なづけ、たいせつに そだてました。",
                    romaji = "Futari wa sono ko o \"Momotarou\" to nadzuke, taisetsu ni sodatemashita.",
                    english = "The couple named the child 'Momotaro' and raised him with deep care."
                ),
                StorySentence(
                    id = 4,
                    japanese = "桃太郎は日本一の黍団子を持ち、犬、猿、キジを仲間にしました。",
                    furigana = "ももたろうは にほんいちの きびだんごを もち、いぬ、さる、きじを なかまに しました。",
                    romaji = "Momotarou wa nihon-ichi no kibi dango o mochi, inu, saru, kiji o nakama ni shimashita.",
                    english = "Momotaro took Japan's finest millet dumplings and befriended a dog, monkey, and pheasant."
                ),
                StorySentence(
                    id = 5,
                    japanese = "仲間と力を合わせて鬼ヶ島へ向かい、村の平和を取り戻しました。",
                    furigana = "なかまと ちからを あわせて おにがしまへ むかい、むらの へいわを とりもどしました。",
                    romaji = "Nakama to chikara o awasete Onigashima e mukai, mura no heiwa o torimodoshimashita.",
                    english = "Joining forces with his companions, he headed to Ogre Island and restored peace to the village."
                )
            ),
            keyVocabulary = listOf(
                StoryVocab("昔々", "むかしむかし", "Once upon a time"),
                StoryVocab("桃", "もも", "Peach"),
                StoryVocab("黍団子", "きびだんご", "Millet dumplings"),
                StoryVocab("仲間", "なかま", "Companions / Comrades"),
                StoryVocab("平和", "へいわ", "Peace")
            ),
            comprehensionQuestions = listOf(
                StoryQuizQuestion(
                    id = "q_momo_1",
                    question = "桃太郎は何から生まれましたか？ (From what was Momotaro born?)",
                    options = listOf("川から流れてきた大きな桃 (A big peach floating down the river)", "竹の中から (Inside bamboo)", "山の上から (From a mountaintop)", "海の波から (From ocean waves)"),
                    correctOptionIndex = 0,
                    explanation = "Sentence 2 explains: 『川から流れてきた大きな桃から、元気な男の子が生まれました』(Born from a giant peach floating down the river)."
                ),
                StoryQuizQuestion(
                    id = "q_momo_2",
                    question = "桃太郎の仲間になった動物はどれですか？ (Which animals became Momotaro's companions?)",
                    options = listOf("犬、猿、キジ (Dog, monkey, pheasant)", "猫、熊、鳥 (Cat, bear, bird)", "虎、馬、鹿 (Tiger, horse, deer)", "兎、亀、狐 (Rabbit, turtle, fox)"),
                    correctOptionIndex = 0,
                    explanation = "Sentence 4 notes: 『犬、猿、キジを仲間にしました』(He made friends with a dog, a monkey, and a pheasant)."
                )
            )
        ),
        JapaneseStory(
            id = "story_bookstore",
            title = "雨の日の古本屋",
            titleEnglish = "The Rainy Day Antique Bookstore",
            jlptLevel = "N4",
            emoji = "📚",
            summary = "Seeking shelter from rain in Tokyo's Jinbocho book district, surrounded by the aromatic scent of aged paper and wisdom.",
            sentences = listOf(
                StorySentence(
                    id = 1,
                    japanese = "神保町の駅を出ると、突然激しい雨が降り始めました。",
                    furigana = "じんぼうちょうの えきを でると、とつぜん はげしい あめが ふりはじめました。",
                    romaji = "Jinbouchou no eki o deru to, totsuzen hageshii ame ga furihajimemashita.",
                    english = "As I exited Jimbocho Station, heavy rain suddenly started to fall."
                ),
                StorySentence(
                    id = 2,
                    japanese = "傘を持っていなかったので、近くの古い木造の古本屋に駆け込みました。",
                    furigana = "かさを もっていなかったので、ちかくの ふるい もくぞうの ふるほんやに かけこみました。",
                    romaji = "Kasa o motteinakatta node, chikaku no furui mokuzou no furuhonya ni kakekomimashita.",
                    english = "Not having an umbrella, I dashed into a nearby antique wooden bookstore."
                ),
                StorySentence(
                    id = 3,
                    japanese = "店内には古い紙の香りと、心地よいジャズの音楽が漂っていました。",
                    furigana = "てんないには ふるい かみの かおりと、ここちよい ジャズの おんがくが ただよっていました。",
                    romaji = "Tennai ni wa furui kami no kaori to, kokochiyoi jazu no ongaku ga tadayotteimashita.",
                    english = "Inside the shop, the scent of aged paper and pleasant jazz music drifted through the air."
                ),
                StorySentence(
                    id = 4,
                    japanese = "棚の奥で、日本の伝統的な浮世絵についての本を見つけました。",
                    furigana = "たなの おくで、にほんの でんとうてきな うきよえについての ほんを みつけました。",
                    romaji = "Tana no oku de, Nihon no dentouteki na ukiyoe ni tsuite no hon o mitsukemashita.",
                    english = "Deep on a shelf, I discovered a book about traditional Japanese ukiyo-e woodblock prints."
                ),
                StorySentence(
                    id = 5,
                    japanese = "雨宿りの時間が、忘れられない素晴らしい宝探しになりました。",
                    furigana = "あまやどりの じかんが、わすれられない すばらしい たからさがしに なりました。",
                    romaji = "Amayadori no jikan ga, wasurerarenai subarashii takarasagashi ni narimashita.",
                    english = "My time sheltering from the rain turned into an unforgettable and wonderful treasure hunt."
                )
            ),
            keyVocabulary = listOf(
                StoryVocab("古本屋", "ふるほんや", "Used / Antique bookstore"),
                StoryVocab("雨宿り", "あまやどり", "Sheltering from rain"),
                StoryVocab("浮世絵", "うきよえ", "Ukiyo-e woodblock prints"),
                StoryVocab("香り", "かおり", "Aroma / Fragrance"),
                StoryVocab("宝探し", "たからさがし", "Treasure hunt")
            ),
            comprehensionQuestions = listOf(
                StoryQuizQuestion(
                    id = "q_book_1",
                    question = "なぜ古本屋に入りましたか？ (Why did the person enter the bookstore?)",
                    options = listOf("突然雨が降ってきたから (Because it suddenly started raining)", "本を売りたかったから (Wanted to sell books)", "友達を待っていたから (Waiting for a friend)", "コーヒーを飲むため (To drink coffee)"),
                    correctOptionIndex = 0,
                    explanation = "Sentence 2 explains: 『傘を持っていなかったので、近くの...古本屋に駆け込みました』(Had no umbrella so dashed into the bookstore)."
                ),
                StoryQuizQuestion(
                    id = "q_book_2",
                    question = "棚の奥でどんな本を見つけましたか？ (What kind of book was found deep on the shelf?)",
                    options = listOf("浮世絵についての本 (A book about ukiyo-e)", "料理の本 (A cooking recipe book)", "旅行ガイドブック (A travel guidebook)", "漫画の本 (A manga book)"),
                    correctOptionIndex = 0,
                    explanation = "Sentence 4 states: 『伝統的な浮世絵についての本を見つけました』(Found a book about traditional ukiyo-e)."
                )
            )
        ),
        JapaneseStory(
            id = "story_craftsman_pottery",
            title = "伝統工芸と陶芸家の心",
            titleEnglish = "Traditional Craft & The Potter's Spirit",
            jlptLevel = "N3",
            emoji = "🏺",
            summary = "In the ancient kiln town of Mashiko, an apprentice discovers that shaping clay reflects the state of one's own mind.",
            sentences = listOf(
                StorySentence(
                    id = 1,
                    japanese = "栃木県の益子町は、古くから陶芸の街として全国に知られています。",
                    furigana = "とちぎけんの ましこまちは、ふるくから とうげいの まちとして ぜんこくに しられています。",
                    romaji = "Tochigi-ken no Mashiko-machi wa, furuku kara tougei no machi toshite zenkoku ni shirareteimasu.",
                    english = "Mashiko Town in Tochigi Prefecture has been known nationwide as a pottery town since ancient times."
                ),
                StorySentence(
                    id = 2,
                    japanese = "見習い職人の健太は、師匠の繊細な手の動きを息を呑んで見つめました。",
                    furigana = "みならいしょくにんの けんたは、ししょうの せんさいな ての うごきを いきをのんで みつめました。",
                    romaji = "Minarai shokunin no Kenta wa, shishou no sensai na te no ugoki o iki o nonde mitsumemashita.",
                    english = "Kenta, an apprentice craftsman, watched his master's delicate hand movements with bated breath."
                ),
                StorySentence(
                    id = 3,
                    japanese = "「土と対話しなさい。焦る気持ちは器の歪みとなって現れる」と師匠は静かに諭しました。",
                    furigana = "「つちと たいわしなさい。あせる きもちは うつわの ゆがみとなって あらわれる」と ししょうは しずかに さとしました。",
                    romaji = "\"Tsuchi to taiwashinasai. Aseru kimochi wa utsuwa no yugami to natte arawareru\" to shishou wa shizuka ni satoshimashita.",
                    english = "\"Converse with the clay. An impatient mind will appear as a warp in the vessel,\" the master calmly advised."
                ),
                StorySentence(
                    id = 4,
                    japanese = "失敗を恐れず何度もろくろを回すうちに、健太の手は土の温もりを感じ始めました。",
                    furigana = "しっぱいを おそれず なんども ろくろを まわすうちに、けんたの ては つちの ぬくもりを かんじはじめました。",
                    romaji = "Shippai o osorezu nando mo rokuro o mawasu uchi ni, Kenta no te wa tsuchi no nukumori o kanjihajimemashita.",
                    english = "As he spun the potter's wheel again and again without fearing failure, Kenta's hands began to feel the warmth of the clay."
                ),
                StorySentence(
                    id = 5,
                    japanese = "夕暮れ時、窯の前に並んだ湯呑みは、どれも素朴で温かい輝きを放っていました。",
                    furigana = "ゆうぐれどき、かまの まえに ならんだ ゆのみは、どれも そぼくで あたたかい かがやきを はなっていました。",
                    romaji = "Yuuguredoki, kama no mae ni naranda yunomi wa, dore mo soboku de atatakai kagayaki o hanatteimashita.",
                    english = "At dusk, the tea cups lined up before the kiln all emitted a simple and heartwarming radiance."
                )
            ),
            keyVocabulary = listOf(
                StoryVocab("陶芸", "とうげい", "Ceramics / Pottery"),
                StoryVocab("見習い職人", "みならいしょくにん", "Apprentice artisan"),
                StoryVocab("師匠", "ししょう", "Master / Teacher"),
                StoryVocab("ろくろ", "ろくろ", "Potter's wheel"),
                StoryVocab("素朴", "そぼく", "Simple / Rustic / Artless")
            ),
            comprehensionQuestions = listOf(
                StoryQuizQuestion(
                    id = "q_pottery_1",
                    question = "師匠は焦る気持ちについて何と言いましたか？ (What did the master say about an impatient mind?)",
                    options = listOf(
                        "器の歪みとなって現れる (Appears as a warp in the vessel)",
                        "土が乾いてしまう (The clay dries out)",
                        "ろくろが壊れてしまう (The potter's wheel breaks)",
                        "窯の火が消えてしまう (The kiln fire goes out)"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Sentence 3 states: 『焦る気持ちは器の歪みとなって現れる』(An impatient mind appears as a warp in the vessel)."
                ),
                StoryQuizQuestion(
                    id = "q_pottery_2",
                    question = "窯の前に並んだ湯呑みはどんな輝きを放っていましたか？ (What kind of radiance did the teacups emit?)",
                    options = listOf(
                        "素朴で温かい輝き (A rustic, warm radiance)",
                        "派手で金色の輝き (A flashy, golden radiance)",
                        "冷たく鋭い光 (A cold, sharp light)",
                        "暗くて見えなかった (Too dark to see)"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Sentence 5 states: 『どれも素朴で温かい輝きを放っていました』(All emitted a simple and warm radiance)."
                )
            )
        ),
        JapaneseStory(
            id = "story_ai_future",
            title = "人工知能と人間の共生社会",
            titleEnglish = "AI & Human Symbiotic Society",
            jlptLevel = "N2",
            emoji = "🤖",
            summary = "An insightful exploration into how autonomous technology and human empathy collaborate to build a compassionate society.",
            sentences = listOf(
                StorySentence(
                    id = 1,
                    japanese = "急速に進化を遂げる人工知能は、現代社会のあらゆる側面に劇的な変革をもたらしています。",
                    furigana = "きゅうそくに しんかを とげる じんこうちのうは、げんだいしゃかいの あらゆる そくめんに げきてきな へんかくを もたらしています。",
                    romaji = "Kyuusoku ni shinka o togeru jinkouchinou wa, gendai shakai no arayuru sokumen ni gekiteki na henkaku o motarashiteimasu.",
                    english = "Rapidly evolving artificial intelligence is bringing dramatic transformation to every aspect of modern society."
                ),
                StorySentence(
                    id = 2,
                    japanese = "膨大なデータの分析や予測において、機械の処理能力は人間のそれを遥かに凌駕しています。",
                    furigana = "ぼうだいな データの ぶんせきや よそくにおいて、きかいの しょりのうりょくは にんげんの それを はるかに りょうがしています。",
                    romaji = "Boudai na deeta no bunseki ya yosoku ni oite, kikai no shorinouryoku wa ningen no sore o haruka ni ryougashiteimasu.",
                    english = "In the analysis and forecasting of vast data, machine processing capacity far surpasses that of humans."
                ),
                StorySentence(
                    id = 3,
                    japanese = "しかしながら、他者の痛みに寄り添う共感や、倫理的な判断力は人間固有の領域と言えます。",
                    furigana = "しかしながら、たしゃの いたみに よりそう きょうかんや、りんりてきな はんだんりょくは にんげんこゆうの りょういきと いえます。",
                    romaji = "Shikashinagara, tasha no itami ni yorisou kyoukan ya, rinriteki na handanryoku wa ningen koyuu no ryouiki to iemasu.",
                    english = "Nevertheless, empathy that connects with others' pain and ethical judgment remain distinctly human domains."
                ),
                StorySentence(
                    id = 4,
                    japanese = "テクノロジーを単なる代替手段としてではなく、人間の可能性を拡張する協働者として捉えるべきです。",
                    furigana = "テクノロジーを たんなる だいたいしゅだんとしてではなく、にんげんの かのうせいを かくちょうする きょうどうしゃとして とらえるべきです。",
                    romaji = "Tekunorojii o tannaru daitaishudan toshite dewa naku, ningen no kanousei o kakuchou suru kyoudousha toshite toraeru beki desu.",
                    english = "We must perceive technology not merely as a substitute, but as a collaborator that expands human potential."
                ),
                StorySentence(
                    id = 5,
                    japanese = "両者の調和的な共生こそが、より温もりある未来を拓く鍵となるに違いありません。",
                    furigana = "りょうしゃの ちょうわてきな きょうせいこそが、より ぬくもりある みらいを ひらく かぎとなるに ちがいありません。",
                    romaji = "Ryousha no chouwateki na kyousei koso ga, yori nukumori aru mirai o hiraku kagi to naru ni chigai arimasen.",
                    english = "A harmonious coexistence between both will undoubtedly be the key that unlocks a warmer future."
                )
            ),
            keyVocabulary = listOf(
                StoryVocab("劇的変革", "げきてきへんかく", "Dramatic transformation"),
                StoryVocab("凌駕する", "りょうがする", "To surpass / Outperform"),
                StoryVocab("共感", "きょうかん", "Empathy / Sympathy"),
                StoryVocab("協働者", "きょうどうしゃ", "Collaborator / Partner"),
                StoryVocab("調和的共生", "ちょうわてききょうせい", "Harmonious coexistence")
            ),
            comprehensionQuestions = listOf(
                StoryQuizQuestion(
                    id = "q_ai_1",
                    question = "本文において、人間固有の領域とされているものは何ですか？ (What is cited as uniquely human?)",
                    options = listOf(
                        "共感や倫理的判断力 (Empathy and ethical judgment)",
                        "膨大なデータの計算速度 (Calculation speed of vast data)",
                        "天候の正確な予測 (Accurate weather forecasting)",
                        "夜間の無人警備 (Unmanned night surveillance)"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Sentence 3 points out: 『他者の痛みに寄り添う共感や、倫理的な判断力は人間固有の領域』."
                ),
                StoryQuizQuestion(
                    id = "q_ai_2",
                    question = "筆者はテクノロジーをどのように捉えるべきだと述べていますか？ (How does the author believe technology should be viewed?)",
                    options = listOf(
                        "人間の可能性を拡張する協働者 (A collaborator expanding human potential)",
                        "人間をすべて置き換える代替品 (A replacement for all humans)",
                        "危険で直ちに禁止すべきもの (Something dangerous that should be banned)",
                        "経済的利益のみを追求する道具 (A tool solely pursuing economic profit)"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Sentence 4 states: 『人間の可能性を拡張する協働者として捉えるべきです』."
                )
            )
        ),
        JapaneseStory(
            id = "story_wabi_sabi",
            title = "侘び寂びと日本的美意識の深淵",
            titleEnglish = "Wabi-Sabi & The Depths of Japanese Aesthetics",
            jlptLevel = "N1",
            emoji = "🌸",
            summary = "A philosophical reflection on the quintessential Japanese aesthetic philosophy: discovering profound beauty in impermanence and imperfection.",
            sentences = listOf(
                StorySentence(
                    id = 1,
                    japanese = "日本文化の根底に通底する「侘び寂び」の概念は、単なる質素さへの賛美にとどまりません。",
                    furigana = "にほんぶんかの こんていに つうていする 「わびさび」の がいねんは、たんなる しっそさへの さんびに とどまりません。",
                    romaji = "Nihon bunka no kontei ni tsuutei suru \"Wabi-Sabi\" no gainen wa, tannaru shissosa e no sanbi ni todomarimasen.",
                    english = "The concept of 'Wabi-Sabi,' running through the foundations of Japanese culture, does not stop at mere admiration of simplicity."
                ),
                StorySentence(
                    id = 2,
                    japanese = "それは万物流転の無常観を受容し、不完全さや経年変化の中に宿る幽玄な美を見出す境地です。",
                    furigana = "それは ばんぶつるてんの むじょうかんを じゅようし、ふかんぜんさや けいねんへんかの なかに やどる ゆうげんな びを みいだす きょうちです。",
                    romaji = "Sore wa banbutsuruten no mujoukan o juyou shi, fukanzen-sa ya keinenhenka no naka ni yadoru yuugen na bi o miidasu kyouchi desu.",
                    english = "It is a mental realm that embraces the impermanence of all things, discovering profound, subtle beauty within imperfection and the weathering of time."
                ),
                StorySentence(
                    id = 3,
                    japanese = "満開の桜のみならず、散りゆく花びらや苔むした庭石に心を寄せる感性こそ、その証左と言えます。",
                    furigana = "まんかいの さくらのみならず、ちりゆく はなびらや こけむした にわいしに こころを よせる かんせいこそ、その しょうさと いえます。",
                    romaji = "Mankai no sakura nominarazu, chiriyuku hanabira ya kokemushita niwaishi ni kokoro o yoseru kansei koso, sono shousa to iemasu.",
                    english = "The sensitivity of being moved not only by cherries in full bloom, but also by scattering petals and moss-covered garden stones, is proof of this."
                ),
                StorySentence(
                    id = 4,
                    japanese = "欠落や余白をあえて残すことで、鑑賞者の想像力を喚起し、静謐な精神の対話を促すのです。",
                    furigana = "けつらくや よはくを あえて のこすことで、かんしょうしゃの そうぞうりょくを かんきし、せいひつな せいしんの たいわを うながすのです。",
                    romaji = "Ketsuraku ya yohaku o aete nokosu koto de, kanshousha no souzouryoku o kanki shi, seihitsu na seishin no taiwa o unagasu no desu.",
                    english = "By deliberately leaving absences and empty margins, it awakens the observer's imagination and fosters a tranquil communion of spirit."
                ),
                StorySentence(
                    id = 5,
                    japanese = "刹那的な現代において、この古き美意識は私たちが内省を取り戻すための指針となり得ます。",
                    furigana = "せつなてきな げんだいにおいて、この ふるき びいしきは わたしたちが ないせいを とりもどすための ししんと なりえます。",
                    romaji = "Setsunateki na gendai ni oite, kono furuki biishiki wa watashitachi ga naisei o torimodosu tame no shishin to nariemasu.",
                    english = "In our ephemeral modern era, this ancient aesthetic consciousness can serve as a compass for regaining inner contemplation."
                )
            ),
            keyVocabulary = listOf(
                StoryVocab("通底する", "つうていする", "To run through / Underlie"),
                StoryVocab("無常観", "むじょうかん", "Sense of impermanence"),
                StoryVocab("幽玄", "ゆうげん", "Subtle grace / Profound mystery"),
                StoryVocab("静謐", "せいひつ", "Tranquility / Serenity"),
                StoryVocab("内省", "ないせい", "Introspection / Self-reflection")
            ),
            comprehensionQuestions = listOf(
                StoryQuizQuestion(
                    id = "q_wabi_1",
                    question = "「侘び寂び」が見出す美はどのようなものの中に宿ると述べていますか？ (Where does Wabi-Sabi discover beauty?)",
                    options = listOf(
                        "不完全さや経年変化の中 (Within imperfection and the passage of time)",
                        "左右対称で完全無欠な造形 (In perfectly symmetrical and flawless forms)",
                        "高価で豪華絢爛な装飾 (In expensive and lavish ornamentation)",
                        "永遠に劣化しない人工素材 (In synthetic materials that never degrade)"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Sentence 2 indicates: 『不完全さや経年変化の中に宿る幽玄な美を見出す』."
                ),
                StoryQuizQuestion(
                    id = "q_wabi_2",
                    question = "あえて余白を残すことの意図は何ですか？ (What is the purpose of deliberately leaving empty space?)",
                    options = listOf(
                        "鑑賞者の想像力を喚起するため (To awaken the observer's imagination)",
                        "制作費用を節約するため (To save on production costs)",
                        "技術が未熟であったため (Because the artisan lacked technical skill)",
                        "時間を短縮するため (To shorten the required time)"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Sentence 4 states: 『欠落や余白をあえて残すことで、鑑賞者の想像力を喚起し』."
                )
            )
        )
    )

    // ==========================================
    // 2. LISTENING LESSON DRILLS & EXERCISES
    // ==========================================
    val listeningExercises: List<ListeningExercise> = listOf(
        ListeningExercise(
            id = "listen_1",
            jlptLevel = "N5",
            audioText = "明日の朝、駅の前で会いましょう。",
            type = ListeningExerciseType.COMPREHENSION,
            questionPrompt = "Listen to the spoken audio. What did the speaker propose?",
            speakerRole = "Ken",
            options = listOf(
                "Let's meet in front of the train station tomorrow morning.",
                "I bought a train ticket yesterday afternoon.",
                "The train station will close early tomorrow.",
                "Let's meet at the cafe tonight."
            ),
            correctOptionIndex = 0,
            furiganaTranscript = "あしたの あさ、えきの まえで あいましょう。",
            romaji = "Ashita no asa, eki no mae de aimashou.",
            englishTranslation = "Let's meet in front of the train station tomorrow morning.",
            explanation = "『明日の朝』(tomorrow morning) + 『駅の前で』(in front of the station) + 『会いましょう』(let's meet)."
        ),
        ListeningExercise(
            id = "listen_2",
            jlptLevel = "N5",
            audioText = "すみません、この漢字の読み方を教えてください。",
            type = ListeningExerciseType.SCRIPT_RECOGNITION,
            questionPrompt = "Listen carefully. Which written sentence matches what was spoken?",
            speakerRole = "Sensei Aiko",
            options = listOf(
                "すみません、この漢字の読み方を教えてください。",
                "すみません、この本を貸してください。",
                "すみません、この電車の時間を教えてください。",
                "すみません、この部屋の窓を開けてください。"
            ),
            correctOptionIndex = 0,
            furiganaTranscript = "すみません、この かんじの よみかたを おしえて ください。",
            romaji = "Sumimasen, kono kanji no yomikata o oshiete kudasai.",
            englishTranslation = "Excuse me, please teach me how to read this kanji.",
            explanation = "The speaker said 『この漢字の読み方』(the reading of this kanji) and 『教えてください』(please teach me)."
        ),
        ListeningExercise(
            id = "listen_3",
            jlptLevel = "N5",
            audioText = "図書館で静かに本を読みます。",
            type = ListeningExerciseType.PARTICLE_MATCH,
            questionPrompt = "Listen to the audio. Which particle was spoken after 図書館 (library)?",
            speakerRole = "Sensei Aiko",
            options = listOf(
                "で (Action location: 図書館で)",
                "に (Existence / Destination)",
                "へ (Direction)",
                "を (Direct object)"
            ),
            correctOptionIndex = 0,
            furiganaTranscript = "としょかんで しずかに ほんを よみます。",
            romaji = "Toshokan de shizuka ni hon o yomimasu.",
            englishTranslation = "I read books quietly in the library.",
            explanation = "The particle 『で』 indicates the location where an action takes place (reading at the library)."
        ),
        ListeningExercise(
            id = "listen_4",
            jlptLevel = "N5",
            audioText = "今日は雨が降っていますから、傘を持っていきます。",
            type = ListeningExerciseType.COMPREHENSION,
            questionPrompt = "Listen to the sentence. Why is the speaker taking an umbrella?",
            speakerRole = "Yuki",
            options = listOf(
                "Because it is raining today.",
                "Because it is very hot and sunny.",
                "Because they lost their old umbrella.",
                "Because they are going to buy a new umbrella."
            ),
            correctOptionIndex = 0,
            furiganaTranscript = "きょうは あめが ふっていますから、かさを もっていきます。",
            romaji = "Kyou wa ame ga futteimasu kara, kasa o motteikimasu.",
            englishTranslation = "Because it is raining today, I will bring an umbrella.",
            explanation = "『雨が降っていますから』: 〜から means 'because' (Because it's raining, I'll take an umbrella)."
        ),
        ListeningExercise(
            id = "listen_5",
            jlptLevel = "N4",
            audioText = "週末は家で映画を見たり、日本語を勉強したりしました。",
            type = ListeningExerciseType.COMPREHENSION,
            questionPrompt = "Listen to the spoken sentence. What did the speaker do over the weekend?",
            speakerRole = "Ken",
            options = listOf(
                "Watched movies and studied Japanese at home.",
                "Went to the cinema and met Japanese friends.",
                "Cleaned the house and read a book.",
                "Took an exam at the language school."
            ),
            correctOptionIndex = 0,
            furiganaTranscript = "しゅうまつは いえで えいがを みたり、にほんごを べんきょうしたり しました。",
            romaji = "Shuumatsu wa ie de eiga o mitari, nihongo o benkyoushitari shimashita.",
            englishTranslation = "Over the weekend, I did things like watching movies and studying Japanese at home.",
            explanation = "The grammar pattern 『〜たり〜たりする』 lists non-exhaustive activities: watching movies and studying Japanese."
        ),
        ListeningExercise(
            id = "listen_6",
            jlptLevel = "N4",
            audioText = "この部屋に入ってもいいですか。どうぞ、お入りください。",
            type = ListeningExerciseType.DIALOGUE,
            questionPrompt = "Listen to the short dialogue. Did the second speaker give permission?",
            speakerRole = "Conversation",
            options = listOf(
                "Yes, gave permission ('Please come in').",
                "No, said the room is currently occupied.",
                "No, asked the person to wait 10 minutes.",
                "Yes, but asked them to take off their shoes first."
            ),
            correctOptionIndex = 0,
            furiganaTranscript = "この へやに はいっても いいですか。どうぞ、おはいりください。",
            romaji = "Kono heya ni haitte mo ii desu ka. Douzo, ohairi kudasai.",
            englishTranslation = "\"May I enter this room?\" \"Yes please, do come in.\"",
            explanation = "『〜てもいいですか』 asks for permission; 『どうぞ、お入りください』 politely grants permission."
        ),
        ListeningExercise(
            id = "listen_7",
            jlptLevel = "N4",
            audioText = "電車が遅れたので、授業に遅刻してしまいました。",
            type = ListeningExerciseType.COMPREHENSION,
            questionPrompt = "Listen carefully. What problem occurred?",
            speakerRole = "Student",
            options = listOf(
                "Arrived late to class because the train was delayed.",
                "Missed the last train home after class.",
                "Forgot their homework on the morning train.",
                "Class was cancelled due to heavy train traffic."
            ),
            correctOptionIndex = 0,
            furiganaTranscript = "でんしゃが おくれたので、じゅぎょうに ちこくして しまいました。",
            romaji = "Densha ga okureta node, jugyou ni chikokushite shimaimashita.",
            englishTranslation = "Because the train was delayed, I unfortunately ended up being late for class.",
            explanation = "『電車が遅れたので』(Because the train was late) + 『遅刻してしまいました』(I regrettably ended up late)."
        ),
        ListeningExercise(
            id = "listen_8",
            jlptLevel = "N3",
            audioText = "毎日コツコツ続けることが、合格への一番の近道です。",
            type = ListeningExerciseType.COMPREHENSION,
            questionPrompt = "Listen to Sensei Aiko's advice. What is the shortest shortcut to passing?",
            speakerRole = "Sensei Aiko",
            options = listOf(
                "Continuing steadily day by day (コツコツ).",
                "Studying 10 hours on the weekend only.",
                "Memorizing all dictionary words in one month.",
                "Hiring an expensive private tutor."
            ),
            correctOptionIndex = 0,
            furiganaTranscript = "まいにち コツコツ つづける ことが、ごうかくへの いちばんの ちかみちです。",
            romaji = "Mainichi kotsukotsu tsuzukeru koto ga, goukaku e no ichiban no chikamichi desu.",
            englishTranslation = "Continuing steadily day by day is the best shortcut to passing.",
            explanation = "『コツコツ続ける』 means to persevere steadily/diligently; 『近道』 means a shortcut."
        ),
        ListeningExercise(
            id = "listen_9",
            jlptLevel = "N3",
            audioText = "恐れ入りますが、明日の打ち合わせを午後3時に変更していただけないでしょうか。",
            type = ListeningExerciseType.COMPREHENSION,
            questionPrompt = "Listen to this business voicemail. What is the speaker requesting?",
            speakerRole = "Yamada (Client)",
            options = listOf(
                "Rescheduling tomorrow's meeting to 3:00 PM.",
                "Cancelling tomorrow's meeting entirely.",
                "Moving tomorrow's meeting to next Monday morning.",
                "Requesting meeting documents to be mailed."
            ),
            correctOptionIndex = 0,
            furiganaTranscript = "おそれいりますが、あしたの うちあわせを ごご さんじに へんこうして いただけないでしょうか。",
            romaji = "Osoreirimasu ga, ashita no uchiawase o gogo san-ji ni henkoushite itadakenai deshou ka.",
            englishTranslation = "Excuse me, but would it be possible to reschedule tomorrow's meeting to 3:00 PM?",
            explanation = "『恐れ入りますが』 polite preamble + 『午後3時に変更していただけないでしょうか』 (could you please change it to 3 PM)."
        ),
        ListeningExercise(
            id = "listen_10",
            jlptLevel = "N2",
            audioText = "本年度の業績向上に伴い、来月より全従業員を対象とした特別研修を実施する運びとなりました。",
            type = ListeningExerciseType.COMPREHENSION,
            questionPrompt = "Listen to the company president's internal announcement. What was decided?",
            speakerRole = "Director Sato",
            options = listOf(
                "Conducting special training for all employees starting next month due to improved performance.",
                "Reducing work hours for new employees starting next quarter.",
                "Cancelling year-end employee bonuses due to market downturn.",
                "Hiring external consultants to restructure the accounting department."
            ),
            correctOptionIndex = 0,
            furiganaTranscript = "ほんねんどの ぎょうせきこうじょうに ともない、らいげつより ぜんじゅうぎょういんを たいしょうとした とくべつけんしゅうを じっしする はこびと なりました。",
            romaji = "Honnendo no gyouseki koujou ni tomonai, raigetsu yori zenjuugyouin o taishou to shita tokubetsu kenshuu o jisshi suru hakobi to narimashita.",
            englishTranslation = "In accompanying our performance improvement this fiscal year, it has been arranged to conduct special training for all employees starting next month.",
            explanation = "『〜に伴い』 (accompanying/due to) + 『特別研修を実施する運びとなりました』 (it has been arranged to implement special training)."
        ),
        ListeningExercise(
            id = "listen_11",
            jlptLevel = "N1",
            audioText = "目先の利害にとらわれることなく、将来世代に対する倫理的責務を果たすことこそが肝要です。",
            type = ListeningExerciseType.COMPREHENSION,
            questionPrompt = "Listen to the keynote speaker's argument. What does the speaker emphasize as essential?",
            speakerRole = "Prof. Watanabe",
            options = listOf(
                "Fulfilling ethical obligations to future generations without being blinded by short-term interests.",
                "Maximizing quarterly profit margins before expanding overseas operations.",
                "Preserving traditional manufacturing techniques without adopting automated digital systems.",
                "Lowering taxation rates for young families in rural prefectures."
            ),
            correctOptionIndex = 0,
            furiganaTranscript = "めさきの りがいに とらわれることなく、しょうらいせだいに たいする りんりてきせきむを はたすことこそが かんようです。",
            romaji = "Mesaki no rigai ni torawareru koto naku, shourai sedai ni taisuru rinriteki sekimu o hatasu koto koso ga kanyou desu.",
            englishTranslation = "Without being trapped by short-term interests, fulfilling our ethical obligations toward future generations is what is truly essential.",
            explanation = "『目先の利害』 (immediate/short-term interests) + 『とらわれることなく』 (without being caught up in) + 『倫理的責務を果たすことこそが肝要』 (fulfilling ethical responsibility is paramount)."
        )
    )

    // ==========================================
    // 3. READING COMPREHENSION LESSONS
    // ==========================================
    val readingLessons: List<ReadingLesson> = listOf(
        ReadingLesson(
            id = "reading_diary_1",
            title = "私の週末の日記",
            titleEnglish = "My Weekend Diary",
            jlptLevel = "N5",
            genre = "Daily Diary",
            emoji = "📝",
            grammarNote = "Past tense verbs (-ました, -ませんでした) and connecting clauses with 〜て.",
            sentences = listOf(
                ReadingPassageSentence(
                    japanese = "土曜日の朝、天気が良かったので公園へ散歩に行きました。",
                    furigana = "どようびの あさ、てんきが よかったので こうえんへ さんぽに いきました。",
                    romaji = "Doyoubi no asa, tenki ga yokatta node kouen e sanpo ni ikimashita.",
                    english = "On Saturday morning, because the weather was pleasant, I went for a walk in the park."
                ),
                ReadingPassageSentence(
                    japanese = "公園にはたくさんの人がいて、子供たちが楽しそうに遊んでいました。",
                    furigana = "こうえんには たくさんの ひとが いて、こどもたちが たのしそうに あそんでいました。",
                    romaji = "Kouen ni wa takusan no hito ga ite, kodomotachi ga tanoshisou ni asondeimashita.",
                    english = "There were many people in the park, and children were playing happily."
                ),
                ReadingPassageSentence(
                    japanese = "昼は近くのカフェで美味しいサンドイッチを食べました。",
                    furigana = "ひるは ちかくの カフェで おいしい サンドイッチを たべました。",
                    romaji = "Hiru wa chikaku no kafe de oishii sandoicchi o tabemashita.",
                    english = "At noon, I ate a delicious sandwich at a nearby cafe."
                ),
                ReadingPassageSentence(
                    japanese = "日曜日は家で日本語の漢字を練習しました。とても充実した週末でした。",
                    furigana = "にちようびは いえで にほんごの かんじを れんしゅうしました。とても じゅうじつした しゅうまつでした。",
                    romaji = "Nichiyoubi wa ie de nihongo no kanji o renshuushimashita. Totemo juujitsu shita shuumatsu deshita.",
                    english = "On Sunday, I practiced Japanese kanji at home. It was a very fulfilling weekend."
                )
            ),
            keyVocabulary = listOf(
                StoryVocab("散歩", "さんぽ", "A walk / Stroll"),
                StoryVocab("遊ぶ", "あそぶ", "To play"),
                StoryVocab("充実", "じゅうじつ", "Fulfilling / Substantial")
            ),
            questions = listOf(
                StoryQuizQuestion(
                    id = "rq_1_1",
                    question = "土曜日の朝、筆者はどこへ行きましたか？ (Where did the writer go on Saturday morning?)",
                    options = listOf("公園へ散歩 (Walk in the park)", "図書館 (Library)", "学校 (School)", "デパート (Department store)"),
                    correctOptionIndex = 0,
                    explanation = "First sentence: 『公園へ散歩に行きました』(Went for a walk in the park)."
                ),
                StoryQuizQuestion(
                    id = "rq_1_2",
                    question = "日曜日に何をしましたか？ (What was done on Sunday?)",
                    options = listOf("家で漢字を練習した (Practiced kanji at home)", "映画を見に行った (Went to see a movie)", "友達と買い物した (Shopped with friends)", "一日中寝ていた (Slept all day)"),
                    correctOptionIndex = 0,
                    explanation = "Last sentence: 『日曜日は家で日本語の漢字を練習しました』(On Sunday practiced Japanese kanji at home)."
                )
            )
        ),
        ReadingLesson(
            id = "reading_notice_station",
            title = "駅の案内とお知らせ",
            titleEnglish = "Station Announcement & Notice",
            jlptLevel = "N4",
            genre = "Notice / Sign",
            emoji = "🚉",
            grammarNote = "Polite request form: 〜てください, caution forms: 〜にご注意ください.",
            sentences = listOf(
                ReadingPassageSentence(
                    japanese = "利用者の皆様へ：ホームでは黄色い点字ブロックの内側でお待ちください。",
                    furigana = "りようしゃの みなさまへ：ホームでは きいろい てんじブロックの うちがわで おまちください。",
                    romaji = "Riyousha no minasama e: Houmu de wa kiiroi tenji burokku no uchigawa de omachi kudasai.",
                    english = "To all passengers: Please wait inside the yellow tactile paving blocks on the platform."
                ),
                ReadingPassageSentence(
                    japanese = "歩きながらのスマートフォンの操作は大変危険ですので、おやめください。",
                    furigana = "あるきながらの スマートフォンの そうさは たいへん きけんですので、おやめください。",
                    romaji = "Arukinagara no sumaatofon no sousa wa taihen kiken desu node, oyame kudasai.",
                    english = "Operating your smartphone while walking is very dangerous, so please refrain from doing so."
                ),
                ReadingPassageSentence(
                    japanese = "落とし物をされたお客様は、改札口の駅員までお申し出ください。",
                    furigana = "おとしものを された おきゃくさまは、かいさつぐちの えきいんまで おもうしでください。",
                    romaji = "Otoshimono o sareta okyakusama wa, kaisatsuguchi no ekiin made omoushide kudasai.",
                    english = "Passengers who have lost an item, please speak to the station staff at the ticket gates."
                )
            ),
            keyVocabulary = listOf(
                StoryVocab("黄色い点字ブロック", "きいろいてんじブロック", "Yellow tactile paving"),
                StoryVocab("内側", "うちがわ", "Inside / Inner side"),
                StoryVocab("危険", "きけん", "Danger / Dangerous"),
                StoryVocab("改札口", "かいさつぐち", "Ticket gate / Turnstile")
            ),
            questions = listOf(
                StoryQuizQuestion(
                    id = "rq_2_1",
                    question = "ホームではどこで電車を待ちますか？ (Where should you wait on the platform?)",
                    options = listOf(
                        "黄色いブロックの内側 (Inside the yellow blocks)",
                        "ホームの端 (Edge of the platform)",
                        "階段のすぐ下 (Right below the stairs)",
                        "自動販売機の前 (In front of the vending machine)"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Notice states: 『黄色い点字ブロックの内側でお待ちください』(Wait inside the yellow tactile blocks)."
                )
            )
        ),
        ReadingLesson(
            id = "reading_business_email",
            title = "業務連絡：新企画の打ち合わせ日程",
            titleEnglish = "Business Email: New Project Meeting Schedule",
            jlptLevel = "N3",
            genre = "Business Email",
            emoji = "📧",
            grammarNote = "Keigo expressions (お疲れ様です、〜につきまして、ご都合はいかがでしょうか).",
            sentences = listOf(
                ReadingPassageSentence(
                    japanese = "社員の皆様、日々の業務お疲れ様です。来期の新企画に関する全体打ち合わせを以下の日程で開催いたします。",
                    furigana = "しゃいんの みなさま、ひびの ぎょうむ おつかれさまです。らいきの しんきかくに かんする ぜんたい うちあわせを いかの にっていにて かいさいいたします。",
                    romaji = "Shain no minasama, hibi no gyoumu otsukaresama desu. Raiki no shin-kikaku ni kansuru zentai uchiawase o ika no nittei nite kaisai itashimasu.",
                    english = "To all employees, thank you for your daily dedication. The general meeting regarding next quarter's new project will be held on the following schedule."
                ),
                ReadingPassageSentence(
                    japanese = "日時：来週水曜日 午後2時〜3時30分 / 場所：第3会議室（オンライン参加も可能）",
                    furigana = "にちじ：らいしゅう すいようび ごご にじから さんじさんじっぷん / ばしょ：だいさん かいぎしつ（オンラインさんかも かのう）",
                    romaji = "Nichiji: Raishuu suiyoubi gogo ni-ji kara san-ji sanjippun / Basho: Dai-san kaigishitsu (onrain sanka mo kanou)",
                    english = "Date & Time: Next Wednesday 2:00 PM - 3:30 PM / Location: Conference Room 3 (Online participation also available)"
                ),
                ReadingPassageSentence(
                    japanese = "事前に共有フォルダー内の企画草案に目を通し、ご意見をご準備くださいますようお願いいたします。",
                    furigana = "じぜんに きょうゆうフォルダーないの きかくそうあんに めをとおし、ごいけんを ごじゅんびくださいますよう おねがいいたします。",
                    romaji = "Jizen ni kyouyuu forudaanai no kikaku souan ni me o tooshi, go-iken o go-junbi kudasaimasu you onegai itashimasu.",
                    english = "Please review the draft proposal in the shared folder in advance and prepare your feedback."
                )
            ),
            keyVocabulary = listOf(
                StoryVocab("打ち合わせ", "うちあわせ", "Business meeting / Consultation"),
                StoryVocab("草案", "そうあん", "Draft / Initial plan"),
                StoryVocab("目を通す", "めをとおす", "To look over / Scan through")
            ),
            questions = listOf(
                StoryQuizQuestion(
                    id = "rq_bus_1",
                    question = "打ち合わせの前に社員がしておくべきことは何ですか？ (What should employees do before the meeting?)",
                    options = listOf(
                        "企画草案に目を通しておく (Review the draft proposal in advance)",
                        "全員が第3会議室に集合する (Gather everyone in Room 3 immediately)",
                        "新しいフォルダーを作成する (Create a new folder)",
                        "プレゼンテーション動画を撮影する (Film a presentation video)"
                    ),
                    correctOptionIndex = 0,
                    explanation = "The email states: 『事前に共有フォルダー内の企画草案に目を通し、ご意見をご準備くださいますよう』."
                )
            )
        ),
        ReadingLesson(
            id = "reading_essay_nature",
            title = "都市における緑地の役割",
            titleEnglish = "The Role of Green Spaces in Urban Environments",
            jlptLevel = "N2",
            genre = "Environmental Essay",
            emoji = "🌿",
            grammarNote = "Formal explanatory prose: 〜に他ならない, 〜を通じて, 〜が求められている.",
            sentences = listOf(
                ReadingPassageSentence(
                    japanese = "近年、過密化が進む大都市において、公園や屋上庭園などの緑地空間の重要性が再認識されています。",
                    furigana = "きんねん、かみつかが すすむ だいとしにおいて、こうえんや おくじょうていえんなどの りょくちくうかんの じゅうようせいが さいにんしきされています。",
                    romaji = "Kinnen, kamitsuka ga susumu daitoshi ni oite, kouen ya okujou teien nado no ryokuchi kuukan no juuyousei ga saininshiki sareteimasu.",
                    english = "In recent years, the importance of green spaces such as parks and rooftop gardens is being reaffirmed in densely populated metropolises."
                ),
                ReadingPassageSentence(
                    japanese = "緑地は都市のヒートアイランド現象を緩和するだけでなく、市民の心理的ストレスを軽減するオアシスとしても機能しています。",
                    furigana = "りょくちは としの ヒートアイランドげんしょうを かんわするだけでなく、しみんの しんりてきストレスを けいげんする オアシスとしても きのうしています。",
                    romaji = "Ryokuchi wa toshi no hiitoairando genshou o kanwa suru dakedenaku, shimin no shinriteki sutoresu o keigen suru oashisu toshite mo kinoushiteimasu.",
                    english = "Green spaces not only alleviate the urban heat island effect, but also function as an oasis easing psychological stress for citizens."
                ),
                ReadingPassageSentence(
                    japanese = "持続可能な都市設計のためには、経済合理性のみならず、自然との共生を見据えた中長期的な政策立案が求められているのです。",
                    furigana = "じぞくかのうな としせっけいの ためには、けいざいごうりせいのみならず、しぜんとの きょうせいを みすえた ちゅうちょうきてきな せいさくりつあんが もとめられているのです。",
                    romaji = "Jizokukanou na toshi sekkei no tame ni wa, keizaigourisei nominarazu, shizen to no kyousei o misueta chuuchoukiteki na seisakurituan ga motomerareteiru no desu.",
                    english = "For sustainable urban design, medium-to-long-term policy planning that looks toward coexistence with nature is demanded, not merely economic rationality."
                )
            ),
            keyVocabulary = listOf(
                StoryVocab("過密化", "かみつか", "Overcrowding / High density"),
                StoryVocab("緩和", "かんわ", "Relief / Mitigation"),
                StoryVocab("経済合理性", "けいざいごうりせい", "Economic rationality"),
                StoryVocab("政策立案", "せいさくりつあん", "Policy formulation")
            ),
            questions = listOf(
                StoryQuizQuestion(
                    id = "rq_nature_1",
                    question = "筆者が持続可能な都市設計に不可欠だと主張している視点は何ですか？ (What perspective does the author deem essential?)",
                    options = listOf(
                        "自然との共生を見据えた中長期的な政策 (Mid-to-long-term policies looking toward coexistence with nature)",
                        "短期的な経済利益の最大化 (Maximizing short-term economic profit)",
                        "高層ビルの建設規制緩和 (Deregulation of skyscraper construction)",
                        "自家用車の利用促進 (Promoting private vehicle usage)"
                    ),
                    correctOptionIndex = 0,
                    explanation = "The concluding sentence emphasizes: 『経済合理性のみならず、自然との共生を見据えた中長期的な政策立案が求められている』."
                )
            )
        ),
        ReadingLesson(
            id = "reading_critique_culture",
            title = "情報過多の時代における思索の深さ",
            titleEnglish = "Depth of Contemplation in the Age of Information Overload",
            jlptLevel = "N1",
            genre = "Philosophical Critique",
            emoji = "📜",
            grammarNote = "Advanced discourse markers: 〜を余儀なくされる, 〜に他ならない, 〜と言っても過言ではない.",
            sentences = listOf(
                ReadingPassageSentence(
                    japanese = "デジタル技術の飛躍的発展は、瞬時に莫大な情報へアクセスすることを可能ならしめた反面、知の断片化という副産物を生み出しました。",
                    furigana = "デジタルぎじゅつの ひやくてきはってんは、しゅんじに ばくだいな じょうほうへ アクセスすることをかのうならしめた はんめん、ちの だんぺんかという ふくさんぶつを うみだしました。",
                    romaji = "Dejitaru gijutsu no hiyakuteki hatten wa, shunji ni bakudai na jouhou e akusesu suru koto o kanounarashimeta hanmen, chi no danpenka to iu fukusanbutsu o umidashimashita.",
                    english = "While the leap of digital technology made instantaneous access to immense information possible, on the other hand it produced the byproduct of knowledge fragmentation."
                ),
                ReadingPassageSentence(
                    japanese = "短時間で消費される即時的な情報に追従するあまり、物事の本質を深く沈思黙考する契機が失われつつあることは否定できません。",
                    furigana = "たんじかんで しょうひされる そくじてきな じょうほうに ついじゅうするあまり、ものごとの ほんしつを ふかく ちんしもっこうする けいきが うしなわれつつあることは ひていできません。",
                    romaji = "Tanjikan de shouhisareru sokujiteki na jouhou ni tsuijuu suru amari, monogoto no honshitsu o fukaku chinshimokkou suru keiki ga ushinawaretsutsu aru koto wa hiteidekimasen.",
                    english = "In continually chasing after immediate information consumed in short intervals, it cannot be denied that opportunities for deep, silent contemplation of the essence of things are being lost."
                ),
                ReadingPassageSentence(
                    japanese = "溢れる雑音から距離を置き、孤独の中で自らの思索を深めることこそが、真の独創性を涵養する土壌となるのではないでしょうか。",
                    furigana = "あふれる ざつおんから きょりをおき、こどくの なかで みずからの しさくを ふかめることこそが、しんの どくそうせいを かんようする どじょうとなるのではないでしょうか。",
                    romaji = "Afureru zatsuon kara kyori o oki, kodoku no naka de mizukara no shisaku o fukameru koto koso ga, shin no dokusousei o kanyou suru dojou to naru no dewa nai deshou ka.",
                    english = "Stepping back from overwhelming noise and deepening one's thoughts in solitude may well be the fertile soil that cultivates true originality."
                )
            ),
            keyVocabulary = listOf(
                StoryVocab("断片化", "だんぺんか", "Fragmentation"),
                StoryVocab("沈思黙考", "ちんしもっこう", "Deep silent contemplation / Pondering"),
                StoryVocab("涵養する", "かんようする", "To cultivate / Foster gradually"),
                StoryVocab("独創性", "どくそうせい", "Originality / Creative uniqueness")
            ),
            questions = listOf(
                StoryQuizQuestion(
                    id = "rq_crit_1",
                    question = "筆者は真の独創性を育むために何が土壌になると考えていますか？ (What does the author believe cultivates true originality?)",
                    options = listOf(
                        "溢れる雑音から離れ、孤独の中で思索を深めること (Distancing from noise and deepening thoughts in solitude)",
                        "最新のSNSトレンドを常時監視し続けること (Constantly monitoring the latest SNS trends)",
                        "可能な限り多くの短い記事を素早く読むこと (Reading as many short articles as quickly as possible)",
                        "あらゆる情報機器を完全に廃棄すること (Completely discarding all electronic devices)"
                    ),
                    correctOptionIndex = 0,
                    explanation = "The passage concludes: 『溢れる雑音から距離を置き、孤独の中で自らの思索を深めることこそが、真の独創性を涵養する土壌となる』."
                )
            )
        )
    )

    // ==========================================
    // PERSISTENCE HELPERS
    // ==========================================
    fun isStoryCompleted(storyId: String): Boolean {
        return prefs.getBoolean(PREF_COMPLETED_STORY_PREFIX + storyId, false)
    }

    fun markStoryCompleted(storyId: String) {
        prefs.edit().putBoolean(PREF_COMPLETED_STORY_PREFIX + storyId, true).apply()
    }

    fun getCompletedStoriesCount(): Int {
        return stories.count { isStoryCompleted(it.id) }
    }

    fun isListeningExerciseCompleted(exerciseId: String): Boolean {
        return prefs.getBoolean(PREF_COMPLETED_LISTENING_PREFIX + exerciseId, false)
    }

    fun markListeningExerciseCompleted(exerciseId: String) {
        prefs.edit().putBoolean(PREF_COMPLETED_LISTENING_PREFIX + exerciseId, true).apply()
    }

    fun getCompletedListeningCount(): Int {
        return listeningExercises.count { isListeningExerciseCompleted(it.id) }
    }

    fun isReadingLessonCompleted(lessonId: String): Boolean {
        return prefs.getBoolean(PREF_COMPLETED_READING_PREFIX + lessonId, false)
    }

    fun markReadingLessonCompleted(lessonId: String) {
        prefs.edit().putBoolean(PREF_COMPLETED_READING_PREFIX + lessonId, true).apply()
    }

    fun getCompletedReadingCount(): Int {
        return readingLessons.count { isReadingLessonCompleted(it.id) }
    }
}
