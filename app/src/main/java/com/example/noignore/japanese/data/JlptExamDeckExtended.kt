package com.example.noignore.japanese.data

import com.example.noignore.japanese.model.JapaneseCategory
import com.example.noignore.japanese.model.JapaneseItem

/**
 * Extended JLPT Examination Comprehensive Question Bank covering all JLPT levels (N5 -> N1)
 * and all authentic exam sections:
 * - Language Knowledge: Kanji Reading (漢字読み), Orthography (表記), Contextual Vocabulary (文脈規定),
 *   Paraphrasing (言い換え類義), Usage in Context (用法)
 * - Grammar: Form Selection (文法形式の判断), Sentence Composition (文の組み立て ★)
 * - Reading Comprehension: Information Retrieval / Notices (短文・情報検索読解)
 * - Listening & Pragmatics: Instant Response / Conversational Turn-Taking (聴解・即時応答)
 */
object JlptExamDeckExtended {

    val extendedExamQuestions: List<JapaneseItem> = listOf(
        // =========================================================================
        // === JLPT N5 EXTENDED EXAM QUESTIONS (Novice / Absolute Beginner) ===
        // Contexts: Daily routine, classroom, counters, time, directions, shopping
        // =========================================================================
        JapaneseItem(
            id = "exam_ext_n5_1",
            japanese = "傘",
            reading = "かさ",
            romaji = "kasa",
            meaning = "Umbrella",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N5",
            examQuestionType = "Kanji Reading (漢字読み)",
            examQuestionPrompt = "雨が降っていますから、【傘】をさしてください。",
            mnemonicOrNote = "JLPT N5 漢字読み: 傘 = かさ. Common exam traps: くつ (shoes), かばん (bag), ぼうし (hat).",
            options = listOf("かさ", "くつ", "かばん", "ぼうし")
        ),
        JapaneseItem(
            id = "exam_ext_n5_2",
            japanese = "枚",
            reading = "まい",
            romaji = "mai",
            meaning = "Counter for flat thin objects (sheets, shirts, paper)",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N5",
            examQuestionType = "Contextual Vocabulary & Counters (助数詞)",
            examQuestionPrompt = "切手を五（　　）買いました。手紙をアメリカへ送ります。",
            mnemonicOrNote = "Counters (助数詞): Stamps (切手) and paper are flat objects counted with 枚 (まい). 台 (だい) is for machines/cars; 本 (ほん) is for cylindrical objects; 冊 (さつ) is for bound books.",
            options = listOf("まい", "だい", "ほん", "さつ")
        ),
        JapaneseItem(
            id = "exam_ext_n5_3",
            japanese = "喫茶店",
            reading = "きっさてん",
            romaji = "kissaten",
            meaning = "Coffee shop / Café",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N5",
            examQuestionType = "Contextual Vocabulary (文脈規定)",
            examQuestionPrompt = "駅の前の（　　）でコーヒーを飲みながら友達を待ちました。",
            mnemonicOrNote = "JLPT N5 文脈規定: Drinking coffee implies 喫茶店 (きっさてん / café). 郵便局 (post office), 交番 (police box), 図書館 (library) do not fit the context of serving coffee.",
            options = listOf("きっさてん", "ゆうびんきょく", "こうばん", "としょかん")
        ),
        JapaneseItem(
            id = "exam_ext_n5_4",
            japanese = "助詞「で」",
            reading = "じょしで",
            romaji = "joshi de",
            meaning = "Particle 'de' indicating means, method or vehicle",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N5",
            examQuestionType = "Grammar: Particles in Context (助詞形式)",
            examQuestionPrompt = "毎朝、学校まで地下鉄（　　）行きます。約20分かかります。",
            mnemonicOrNote = "Particle で denotes means/transportation (地下鉄で行く = go by subway). に is for arrival destination/time; を is for direct object; から is starting point.",
            options = listOf("で", "に", "を", "から")
        ),
        JapaneseItem(
            id = "exam_ext_n5_5",
            japanese = "図書館の利用案内",
            reading = "としょかんのりようあんない",
            romaji = "toshokan no riyou annai",
            meaning = "Library Notice & Information Retrieval",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N5",
            examQuestionType = "Reading Comprehension / Information Notice (短文・情報検索)",
            examQuestionPrompt = "【市立図書館からのお知らせ】\n・開館時間：午前9:00〜午後7:00\n・休館日：毎週月曜日（月曜日が祝日の場合は火曜日が休みになります）\n・本の貸出：1人5冊まで、2週間借りることができます。\n質問：この図書館で本を借りたいとき、正しいものはどれですか。",
            mnemonicOrNote = "Reading Notice: The notice states: 1人5冊まで、2週間 (Up to 5 books per person for 2 weeks). Option 1 matches exactly.",
            options = listOf(
                "1人5冊まで、2週間借りることができる。",
                "毎週火曜日は必ず休みである。",
                "夜8時まで開いている。",
                "本は何冊でも好きなだけ借りられる。"
            )
        ),
        JapaneseItem(
            id = "exam_ext_n5_6",
            japanese = "買い物でのあいさつ",
            reading = "かいものでのあいさつ",
            romaji = "kaimono de no aisatsu",
            meaning = "Instant Response at Store / Pragmatics",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N5",
            examQuestionType = "Listening Response & Pragmatics (聴解・即時応答)",
            examQuestionPrompt = "店員：「いらっしゃいませ！何名様ですか。」\n客：「（　　）」\n最も自然な返答を選びなさい。",
            mnemonicOrNote = "When store staff ask '何名様ですか' (How many people?), the natural answer is the number of people: '二人です' (Two of us).",
            options = listOf("二人です。", "いただきます。", "ごちそうさまでした。", "いいえ、けっこうです。")
        ),
        JapaneseItem(
            id = "exam_ext_n5_7",
            japanese = "週末の映画",
            reading = "しゅうまつのえいが",
            romaji = "shuumatsu no eiga",
            meaning = "Sentence Composition with Star Clause",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N5",
            examQuestionType = "Sentence Composition (文の組み立て ★)",
            examQuestionPrompt = "週末は [ 1. 映画を ] [ 2. 友達と ] [ ★ 3. 見に ] [ 4. 行きました ]。\n正しい文を作るとき、★に入るものはどれですか。",
            mnemonicOrNote = "★ Star Question: Correct natural syntax: 週末は [友達と(2)] [映画を(1)] [★見に(3)] [行きました(4)]. Purpose of going: [Verb Stem + に行く]. Star position is 見に.",
            options = listOf("見に", "映画を", "友達と", "行きました")
        ),

        // =========================================================================
        // === JLPT N4 EXTENDED EXAM QUESTIONS (Elementary / Upper Beginner) ===
        // Contexts: Transitive/intransitive, keigo basics, directions, notices, giving/receiving
        // =========================================================================
        JapaneseItem(
            id = "exam_ext_n4_1",
            japanese = "届く",
            reading = "とどく",
            romaji = "todoku",
            meaning = "To arrive / To be delivered (Intransitive)",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N4",
            examQuestionType = "Kanji Reading (漢字読み)",
            examQuestionPrompt = "昨日インターネットで注文した荷物が、先ほど【届き】ました。",
            mnemonicOrNote = "JLPT N4 漢字読み: 届く = とどく. Intransitive verb: 荷物が届く (package arrived). Trap: とどける (transitive: deliver).",
            options = listOf("とどき", "うごき", "おどろき", "つづき")
        ),
        JapaneseItem(
            id = "exam_ext_n4_2",
            japanese = "閉める / 閉まる",
            reading = "しめる / しまる",
            romaji = "shimeru / shimaru",
            meaning = "Transitive vs Intransitive Pairs",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N4",
            examQuestionType = "Grammar: Transitive & Intransitive Distinction (自他動詞)",
            examQuestionPrompt = "風が強くて、窓が突然（　　）ましたから、びっくりしました。",
            mnemonicOrNote = "Subject takes が without an agent: 窓が閉まりました (intransitive 閉まる). If an agent closed it: 窓を閉めました (transitive 閉める).",
            options = listOf("閉まり", "閉め", "開け", "止め")
        ),
        JapaneseItem(
            id = "exam_ext_n4_3",
            japanese = "いただく",
            reading = "いただく",
            romaji = "itadaku",
            meaning = "Humble form of もらう (Receive from superior)",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N4",
            examQuestionType = "Honorific & Giving/Receiving (敬語・授受表現)",
            examQuestionPrompt = "田中先生に、日本文化についての素晴らしいご本を（　　）。",
            mnemonicOrNote = "Receiving something from a teacher/superior uses humble verb いただきました (humble form of もらいました). くださる is used when teacher gives (先生がくださった).",
            options = listOf("いただきました", "あげました", "やりました", "くれました")
        ),
        JapaneseItem(
            id = "exam_ext_n4_4",
            japanese = "〜てしまう",
            reading = "〜てしまう",
            romaji = "te shimau",
            meaning = "Regretful completion / Inadvertent mistake",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N4",
            examQuestionType = "Grammar: Aspect & Nuance (文法形式の判断)",
            examQuestionPrompt = "急いで家を出たので、大切な財布を部屋に（　　）しまいました。",
            mnemonicOrNote = "〜てしまう expresses unfortunate completion or regret. 忘れてしまいました = inadvertently forgot.",
            options = listOf("忘れて", "忘れよう", "忘れたら", "忘れると")
        ),
        JapaneseItem(
            id = "exam_ext_n4_5",
            japanese = "アパートのゴミ収集ルール",
            reading = "あぱーとのごみしゅうしゅうるーる",
            romaji = "apaato no gomi shuushuu ruuru",
            meaning = "Apartment Garbage Disposal Notice",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N4",
            examQuestionType = "Reading Comprehension / Daily Life Notice (読解・生活情報)",
            examQuestionPrompt = "【さくらハイツのゴミ出しについて】\n・燃えるゴミ：火曜日・金曜日の朝8:30までに出してください。\n・ビン・缶・ペットボトル：水曜日のみ回収します。\n・粗大ゴミ（家具・電化製品）：管理会社への事前電話予約が必要です。連絡なしでゴミ置き場に出すことは禁止されています。\n質問：古い電子レンジを捨てたい住人は、まず何をしなければなりませんか。",
            mnemonicOrNote = "Notice clearly states: 粗大ゴミ（家具・電化製品）は管理会社への事前電話予約が必要 (Appliance disposal requires prior phone reservation with management).",
            options = listOf(
                "管理会社へ事前に電話で連絡して予約する。",
                "火曜日の朝8:30までにゴミ置き場に置いておく。",
                "水曜日に缶と一緒にそのまま出す。",
                "事前の連絡をせずに、いつでも自由に置いてよい。"
            )
        ),
        JapaneseItem(
            id = "exam_ext_n4_6",
            japanese = "駅での乗り換え案内",
            reading = "えきでののりかえあんない",
            romaji = "eki de no norikae annai",
            meaning = "Station Transfer Instant Response",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N4",
            examQuestionType = "Listening Response & Pragmatics (聴解・即時応答)",
            examQuestionPrompt = "観光客：「すみません、空港行きの急行電車はどこから乗ればいいですか。」\n駅員：「（　　）」\n最も適切な駅員の返答を選びなさい。",
            mnemonicOrNote = "Directions response: The station staff points out the platform: '3番ホームでお待ちください' (Please wait on platform 3).",
            options = listOf(
                "3番ホームでお待ちください。",
                "飛行機の切符を見せてください。",
                "いいえ、乗りたくありません。",
                "空港はとても広くて便利ですよ。"
            )
        ),
        JapaneseItem(
            id = "exam_ext_n4_7",
            japanese = "健康のための散歩",
            reading = "けんこうのためのさんぽ",
            romaji = "kenkou no tame no sanpo",
            meaning = "Sentence Syntax with Star Clause",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N4",
            examQuestionType = "Sentence Composition (文の組み立て ★)",
            examQuestionPrompt = "健康の [ 1. 散歩する ] [ 2. 毎朝30分 ] [ ★ 3. ように ] [ 4. ために ] しています。\n正しい文を作るとき、★に入るものはどれですか。",
            mnemonicOrNote = "★ Star Question: Correct syntax: 健康の [ために(4)] [毎朝30分(2)] [散歩する(1)] [★ように(3)] しています。(In order to stay healthy, I make an effort to walk 30 mins every morning). Star is ように.",
            options = listOf("ように", "ために", "毎朝30分", "散歩する")
        ),

        // =========================================================================
        // === JLPT N3 EXTENDED EXAM QUESTIONS (Intermediate - The Bridge) ===
        // Contexts: Business interactions, reporting, concessions, phrasal verbs, idioms
        // =========================================================================
        JapaneseItem(
            id = "exam_ext_n3_1",
            japanese = "承知",
            reading = "しょうち",
            romaji = "shouchi",
            meaning = "Consent, Acknowledgment (Business 'Understood')",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N3",
            examQuestionType = "Kanji Reading (漢字読み)",
            examQuestionPrompt = "部長からのご指示の件、確かに【承知】いたしました。",
            mnemonicOrNote = "JLPT N3 漢字読み: 承知 = しょうち. 承 (しょう) + 知 (ち). Standard business acknowledgment: 承知いたしました (I have understood / acknowledged).",
            options = listOf("しょうち", "じょうち", "しょうじ", "ちょうち")
        ),
        JapaneseItem(
            id = "exam_ext_n3_2",
            japanese = "引き受ける",
            reading = "ひきうける",
            romaji = "hikiukeru",
            meaning = "To take on / undertake a task or responsibility",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N3",
            examQuestionType = "Compound Verbs (複合動詞)",
            examQuestionPrompt = "急な依頼で大変恐縮ですが、このプロジェクトのリーダーを（　　）いただけませんか。",
            mnemonicOrNote = "JLPT N3 複合動詞: 仕事や責任を引き受ける (take on / undertake work). 引き出す (withdraw/pull out), 引き止める (detain/stop), 引き返す (turn back).",
            options = listOf("引き受けて", "引き出して", "引き止めて", "引き返して")
        ),
        JapaneseItem(
            id = "exam_ext_n3_3",
            japanese = "〜わけにはいかない",
            reading = "〜わけにはいかない",
            romaji = "wake ni wa ikanai",
            meaning = "Cannot do because of moral/social responsibility",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N3",
            examQuestionType = "Grammar: Modality & Obligation (文法形式の判断)",
            examQuestionPrompt = "明日は大切な採用最終面接があるから、風邪気味でも休む（　　）。",
            mnemonicOrNote = "〜わけにはいかない expresses impossibility due to social conscience or moral duty: 'I simply cannot take the day off because of the crucial final interview'.",
            options = listOf("わけにはいかない", "にすぎない", "ほかない", "きりがない")
        ),
        JapaneseItem(
            id = "exam_ext_n3_4",
            japanese = "〜わりに(は)",
            reading = "〜わりに(は)",
            romaji = "wari ni (wa)",
            meaning = "Considering / In spite of expectations based on standard",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N3",
            examQuestionType = "Grammar: Contrast & Evaluation (文法形式の判断)",
            examQuestionPrompt = "このレストランは、値段が手頃な（　　）、料理のボリュームと味が非常に素晴らしい。",
            mnemonicOrNote = "〜わりに(は) indicates an outcome surprising compared to standard expectations (considering how affordable it is, the food is remarkably delicious).",
            options = listOf("わりに", "とおりに", "ついでに", "かわりに")
        ),
        JapaneseItem(
            id = "exam_ext_n3_5",
            japanese = "社内テレワーク制度の見直し",
            reading = "しゃないてれわーくせいどのみなおし",
            romaji = "shanai terewaaku seido no minaoshi",
            meaning = "Business Communication Memo Reading",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N3",
            examQuestionType = "Reading Comprehension / Business Memo (読解・社内連絡)",
            examQuestionPrompt = "【全社員向け社内通知：在宅勤務ガイドラインの改定について】\nこれまで週3回まで認められていた在宅勤務ですが、部署間の対面コミュニケーションおよび新入社員のOJT研修円滑化のため、来月1日より『原則週2回まで』に変更となります。\nただし、育児や介護など特定の事情を有する社員については、所属長の事前承認を得ることで特例申請が可能です。\n質問：この通知の内容と合致しているものはどれですか。",
            mnemonicOrNote = "Notice content: Under normal rules, telework is reduced to 2 times a week from next month, but employees with childcare/nursing circumstances can apply for exceptions with supervisor approval.",
            options = listOf(
                "来月からは原則として週2回までの在宅勤務となるが、育児等の事情があれば特例が認められる。",
                "来月以降、全社員の在宅勤務は一切禁止され、全員出社が義務付けられる。",
                "新入社員のみが週3回以上の在宅勤務を認められる。",
                "事前の承認がなくても、育児中の社員は好きな日数だけ在宅勤務できる。"
            )
        ),
        JapaneseItem(
            id = "exam_ext_n3_6",
            japanese = "ビジネス電話の取り次ぎ",
            reading = "びじねすでんわのとりつぎ",
            romaji = "bijinesu denwa no toritsugi",
            meaning = "Business Telephone Protocol Pragmatics",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N3",
            examQuestionType = "Listening Response & Pragmatics (聴解・即時応答)",
            examQuestionPrompt = "取引先：「ABC商事の佐藤と申します。営業部の高橋様はいらっしゃいますでしょうか。」\n社員：「（高橋は現在外出中）（　　）」\n最も適切なビジネスマナーの返答を選びなさい。",
            mnemonicOrNote = "Business keigo for absent colleague: 'あいにく高橋は外出しております。戻り次第、こちらからお電話させましょうか。' (Do not use honorific 様 for in-group member Takahashi).",
            options = listOf(
                "あいにく高橋は席を外しております。戻り次第、こちらからお電話させましょうか。",
                "高橋様は今お出かけになっていますから、後で電話してください。",
                "高橋さんは今忙しいので、自分で明日もう一度かけてください。",
                "佐藤様のおっしゃる通り、高橋はいません。"
            )
        ),
        JapaneseItem(
            id = "exam_ext_n3_7",
            japanese = "失敗を乗り越える力",
            reading = "しっぱいをのりこえるちから",
            romaji = "shippai o norikoeru chikara",
            meaning = "Sentence Composition with Star Clause",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N3",
            examQuestionType = "Sentence Composition (文の組み立て ★)",
            examQuestionPrompt = "一度の失敗で [ 1. 諦めてしまう ] [ 2. ことなく ] [ ★ 3. 挑戦を ] [ 4. 続ける姿勢こそが ] 成功につながる。\n正しい文を作るとき、★に入るものはどれですか。",
            mnemonicOrNote = "★ Star Question: 一度の失敗で [諦めてしまう(1)] [ことなく(2)] [★挑戦を(3)] [続ける姿勢こそが(4)] 成功につながる。(Without giving up after a single failure, it is the attitude of persevering with challenges that leads to success). Star is 挑戦を.",
            options = listOf("挑戦を", "諦めてしまう", "ことなく", "続ける姿勢こそが")
        ),

        // =========================================================================
        // === JLPT N2 EXTENDED EXAM QUESTIONS (Upper Intermediate / Pre-Advanced) ===
        // Contexts: Advanced corporate negotiations, social editorials, nuanced grammar
        // =========================================================================
        JapaneseItem(
            id = "exam_ext_n2_1",
            japanese = "顕著",
            reading = "けんちょ",
            romaji = "kencho",
            meaning = "Remarkable, Prominent, Conspicuous",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N2",
            examQuestionType = "Kanji Reading (漢字読み)",
            examQuestionPrompt = "近年の少子高齢化に伴い、地方都市における労働力不足が【顕著】になってきた。",
            mnemonicOrNote = "JLPT N2 漢字読み: 顕著 = けんちょ. Trap readings: げんちょ, けんしゃ, けんじょ. 顕著な例 (a prominent example).",
            options = listOf("けんちょ", "げんちょ", "けんしゃ", "けんじょ")
        ),
        JapaneseItem(
            id = "exam_ext_n2_2",
            japanese = "〜を余儀なくされる",
            reading = "〜をよぎなくされる",
            romaji = "o yogi naku sareru",
            meaning = "To be forced / compelled to do something unavoidable",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N2",
            examQuestionType = "Grammar: Unavoidable Circumstance (文法形式の判断)",
            examQuestionPrompt = "大型台風の直撃により送電線が寸断され、住民は数日間の避難所生活を（　　）。",
            mnemonicOrNote = "〜を余儀なくされる means to be forced into an unwelcome action due to external unavoidable disaster/circumstance: '避難所生活を余儀なくされた'.",
            options = listOf("余儀なくされた", "ものともしなかった", "皮切りにした", "極まりなかった")
        ),
        JapaneseItem(
            id = "exam_ext_n2_3",
            japanese = "〜ざるを得ない",
            reading = "〜ざるをえない",
            romaji = "zaru o enai",
            meaning = "Cannot help but do / Have no choice but to do",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N2",
            examQuestionType = "Grammar: Inevitable Conclusion (文法形式の判断)",
            examQuestionPrompt = "これだけの客観的証拠を突きつけられては、自らの過ちを認め（　　）。",
            mnemonicOrNote = "〜ざるを得ない (Verb nai-stem + ざるを得ない, する -> せざるを得ない): 'have no choice but to acknowledge'. 認めざるを得ない.",
            options = listOf("ざるを得ない", "っこない", "かねない", "きれない")
        ),
        JapaneseItem(
            id = "exam_ext_n2_4",
            japanese = "目処が立つ",
            reading = "めどがたつ",
            romaji = "medo ga tatsu",
            meaning = "Prospect of resolution / Target in sight",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N2",
            examQuestionType = "Idiomatic Expression & Paraphrase (慣用句・言い換え)",
            examQuestionPrompt = "新工場の建設資材の調達について、ようやく【目処が立った】。",
            mnemonicOrNote = "慣用句: 目処が立つ (めどがたつ) = 見通しがつく (a viable outlook/prospect has emerged).",
            options = listOf(
                "今後の見通しや解決のめどがついた",
                "予算が大幅にオーバーして中断した",
                "関係者全員の合意が得られず決裂した",
                "計画を白紙に戻して最初からやり直した"
            )
        ),
        JapaneseItem(
            id = "exam_ext_n2_5",
            japanese = "都市景観と地域経済の論考",
            reading = "としけいかんとちいきけいざいのろんこう",
            romaji = "toshi keikan to chiiki keizai no ronkou",
            meaning = "Social Commentary Editorial Passage",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N2",
            examQuestionType = "Reading Comprehension / Editorial Critique (読解・論説文)",
            examQuestionPrompt = "【評論：歴史的景観の保存と商業主義の摩擦】\n「古い街並みを保存しようとする動きは、単なる懐古趣味にとどまらない。その土地固有の歴史と文化が息づく空間は、住民のアイデンティティを涵養すると同時に、長期的には代替不可能な観光資源としての経済的価値をも内包している。目先の商業的利便性のみを優先して高層マンションを乱立させることは、地域の真の価値を不可逆的に破壊する行為に等しい。」\n質問：筆者の主張として最も合致するものはどれですか。",
            mnemonicOrNote = "Reading Editorial: The author asserts that preserving historical streets fosters resident identity and preserves long-term irreplaceable economic and cultural value, which should not be sacrificed for short-term commercial convenience.",
            options = listOf(
                "歴史的街並みの保存は住民の精神的支柱であり、長期的な地域価値を守るために商業開発より優先されるべきだ。",
                "観光客誘致のためには、歴史的建造物をすべて解体して最新のショッピングモールを建設すべきだ。",
                "街並みの保存は単なる懐古趣味にすぎず、経済的利益には一切寄与しない。",
                "高層マンションの乱立こそが、地域住民のアイデンティティを最も高める最善策である。"
            )
        ),
        JapaneseItem(
            id = "exam_ext_n2_6",
            japanese = "役員会議での慎重意見の表明",
            reading = "やくいんかいぎでのしんちょういけんのひょうめい",
            romaji = "yakuin kaigi de no shinchou iken no hyoumei",
            meaning = "Executive Boardroom Pragmatic Response",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N2",
            examQuestionType = "Listening Response & Pragmatics (聴解・即時応答)",
            examQuestionPrompt = "専務：「今回の海外進出プロジェクトですが、現地の政情不安も鑑み、一度立ち止まって再考すべきではないでしょうか。」\n社長：「（　　）」\n専務の慎重論に理解を示しつつ結論を保留する返答として、最も適切なものを選びなさい。",
            mnemonicOrNote = "Pragmatic response acknowledging the concern while deferring final decision: '確かにおっしゃる通り懸念は残りますね。各リスク要因を洗い出した上で、来週改めて判断しましょう。'",
            options = listOf(
                "確かにおっしゃる通り懸念は残りますね。各リスク要因を洗い出した上で、来週改めて判断しましょう。",
                "そんな弱腰な意見など聞く耳を持たないので、明日にでも全員契約を結んでください。",
                "政情不安などまったく関係ないので、すぐに荷物をまとめて出発しなさい。",
                "私は社長ですから、専務が何を言おうと勝手にやってください。"
            )
        ),
        JapaneseItem(
            id = "exam_ext_n2_7",
            japanese = "技術革新と倫理的責任",
            reading = "ぎじゅつかくしんとりんりてきせきにん",
            romaji = "gijutsu kakushin to rinriteki sekinin",
            meaning = "Sentence Composition with Star Clause",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N2",
            examQuestionType = "Sentence Composition (文の組み立て ★)",
            examQuestionPrompt = "人工知能の発展は、利便性の向上 [ 1. もたらす ] [ 2. 倫理的な ] [ ★ 3. のみならず ] [ 4. 課題をも ] 提起している。\n正しい文を作るとき、★に入るものはどれですか。",
            mnemonicOrNote = "★ Star Question: 利便性の向上 [のみならず(3)] [倫理的な(2)] [★課題をも(4)] [もたらす(1)] ではなく、自然な構文: 利便性の向上を [もたらす(1)] [★のみならず(3)] [倫理的な(2)] [課題をも(4)] 提起している。 Star is のみならず.",
            options = listOf("のみならず", "もたらす", "倫理的な", "課題をも")
        ),

        // =========================================================================
        // === JLPT N1 EXTENDED EXAM QUESTIONS (Advanced Native Proficiency) ===
        // Contexts: Deep philosophical critique, legal/academic discourse, classical four-character idioms
        // =========================================================================
        JapaneseItem(
            id = "exam_ext_n1_1",
            japanese = "蹂躙",
            reading = "じゅうりん",
            romaji = "juurin",
            meaning = "Infringement, Trampling upon rights or dignity",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N1",
            examQuestionType = "Kanji Reading (漢字読み)",
            examQuestionPrompt = "いかなる大義名分があろうとも、基本的人権が【蹂躙】される事態は断じて容認できない。",
            mnemonicOrNote = "JLPT N1 漢字読み: 蹂躙 = じゅうりん (trampling underfoot, violation of rights/sanctity). Traps: しゅうりん, じゅうれん, とうりん.",
            options = listOf("じゅうりん", "しゅうりん", "じゅうれん", "とうりん")
        ),
        JapaneseItem(
            id = "exam_ext_n1_2",
            japanese = "〜であれ〜であれ",
            reading = "〜であれ〜であれ",
            romaji = "de are ... de are",
            meaning = "Whether it be A or B / In either case without exception",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N1",
            examQuestionType = "Grammar: Universal Concession (文法形式の判断)",
            examQuestionPrompt = "理由が何（　　）、法を犯して他者に損害を与えた責任は免れない。",
            mnemonicOrNote = "〜であれ expresses 'regardless of what it may be': 理由が何であれ (whatever the reason may be).",
            options = listOf("であれ", "につけ", "がてら", "まみれ")
        ),
        JapaneseItem(
            id = "exam_ext_n1_3",
            japanese = "〜を禁じ得ない",
            reading = "〜をきんじえない",
            romaji = "o kinji enai",
            meaning = "Cannot suppress or contain an intense spontaneous emotion",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N1",
            examQuestionType = "Grammar: Spontaneous Emotion (文法形式の判断)",
            examQuestionPrompt = "国家の安全保障を担う要職にある者の度重なる失言と無責任な対応に、国民は強い憤り（　　）。",
            mnemonicOrNote = "〜を禁じ得ない expresses an overwhelming emotion that cannot be held back: 憤りを禁じ得ない (cannot suppress righteous indignation).",
            options = listOf("を禁じ得ない", "をものともしない", "にたえない", "をおいてほかにない")
        ),
        JapaneseItem(
            id = "exam_ext_n1_4",
            japanese = "臨機応変",
            reading = "りんきおうへん",
            romaji = "rinkiouhen",
            meaning = "Adapting flexibly to changing circumstances",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N1",
            examQuestionType = "Four-Character Idiom & Paraphrase (四字熟語・類義)",
            examQuestionPrompt = "予期せぬトラブルが多発する現場では、マニュアルに盲従するのではなく、【臨機応変】な判断が求められる。",
            mnemonicOrNote = "四字熟語: 臨機応変 (りんきおうへん) = その場の状況の変化に応じて、適切かつ柔軟に対処すること (acting flexibly in accordance with changing situations).",
            options = listOf(
                "状況の変化に応じて柔軟かつ適切に対処すること",
                "過去の前例と規則を何があっても絶対厳守すること",
                "自分の利益だけを最大化するために他人を出し抜くこと",
                "他人の指示を待ってからしか行動を起こさないこと"
            )
        ),
        JapaneseItem(
            id = "exam_ext_n1_5",
            japanese = "科学認識論と客観性の神話",
            reading = "かがくにんしきろんときゃっかんせいのおごり",
            romaji = "kagaku ninshikiron to kyakkansei no ogori",
            meaning = "Epistemological Discourse Reading Comprehension",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N1",
            examQuestionType = "Academic / Philosophical Discourse (読解・学術論説)",
            examQuestionPrompt = "【科学哲学論考：観察の理論負荷性と客観性の再審】\n「近代科学は『主観を排した純粋客観的な観察事実の集積』を標榜してきた。しかし、科学史が雄弁に物語るように、いかなる観察データもそれを解釈する枠組み（理論パラダイム）から完全に自由ではありえない。観察者は自らが依って立つ言語体系や理論的偏見という色眼鏡を通してしか世界を眼差すことができないのである。したがって、真の科学的精神とは、自らの知の枠組みが持つ歴史的・社会的な相対性を自覚し、常に反証に対して開かれている謙虚さにこそ宿る。」\n質問：筆者が述べる「真の科学的精神」の要諦として、最も適切なものはどれですか。",
            mnemonicOrNote = "Academic Discourse: The author argues that no observation is free of theoretical bias; true scientific spirit lies in being humble about the relative nature of one's own interpretive frameworks and staying open to refutation.",
            options = listOf(
                "観察データには理論的枠組みが不可避に伴うことを自覚し、自らの仮説の相対性と反証可能性に開かれていること",
                "自らの観察結果こそが唯一絶対の真理であると確信し、批判的な他者の反証意見を断固として排除すること",
                "理論的仮説を一切立てず、コンピュータによる無作為な数値収集のみを信仰すること",
                "過去のパラダイムをすべて否定し、公的機関が定めた基準のみを科学的事実として受容すること"
            )
        ),
        JapaneseItem(
            id = "exam_ext_n1_6",
            japanese = "国際外交シンポジウムでの質疑応答",
            reading = "こくさいがいこうしんぽじうむでのしつぎおうとう",
            romaji = "kokusai gaikou shinpojiumu de no shitsugi outou",
            meaning = "Diplomatic Pragmatic Turn-Taking",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N1",
            examQuestionType = "Listening Response & Pragmatics (聴解・即時応答)",
            examQuestionPrompt = "司会者：「パネリストの先生方、多国間協調体制の崩壊が懸念される中、各主権国家がナショナリズムに回帰する力学をどのように制御すべきとお考えでしょうか。」\n国際政治学者：「（　　）」\n学術的かつ論理的に本質を突く返答として、最も適切なものを選びなさい。",
            mnemonicOrNote = "Academic dialogue response: '短絡的な利害対立を越え、共有可能な規範の再構築に向けた多層的な対話チャンネルを維持することに尽きます。'",
            options = listOf(
                "短絡的な利害対立を越え、共有可能な規範の再構築に向けた多層的な対話チャンネルを維持することに尽きます。",
                "そんな難しい問題は誰にも分かりませんから、各自勝手に行動すればよろしいのではないでしょうか。",
                "国家間の協調など最初から無意味ですから、軍事力を行使して他国を従属させるべきです。",
                "司会者の方の質問が長すぎて疲れたので、休憩に入りましょう。"
            )
        ),
        JapaneseItem(
            id = "exam_ext_n1_7",
            japanese = "言語と存在の深層",
            reading = "げんごのそんざいのしんそう",
            romaji = "gengo to sonzai no shinsou",
            meaning = "Sentence Composition with Star Clause",
            category = JapaneseCategory.JLPT_EXAM,
            jlptLevel = "N1",
            examQuestionType = "Sentence Composition (文の組み立て ★)",
            examQuestionPrompt = "言語とは [ 1. 単なる意思疎通の ] [ 2. 道具にとどまらず ] [ ★ 3. 我々の思考そのものを ] [ 4. 規定する枠組み ] にほかならない。\n正しい文を作るとき、★に入るものはどれですか。",
            mnemonicOrNote = "★ Star Question: 言語とは [単なる意思疎通の(1)] [道具にとどまらず(2)] [★我々の思考そのものを(3)] [規定する枠組み(4)] にほかならない。(Language is nothing less than a framework that conditions our very thinking, rather than being a mere tool for communication). Star is 我々の思考そのものを.",
            options = listOf("我々の思考そのものを", "単なる意思疎通の", "道具にとどまらず", "規定する枠組み")
        )
    )
}
