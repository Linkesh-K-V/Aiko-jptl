import json

part10_data = [
    # Final 21 high-leverage canonical N4 items bringing unique items to exactly 575
    ("熱", "ねつ", "netsu", "fever, heat", "Noun", 1, "health,body", "風邪を引いて高い熱が出ました。", "かぜをひいてたかいねつがでました。", "Kaze o hiite takai netsu ga demashita.", "I caught a cold and developed a high fever.", []),
    ("喉", "のど", "nodo", "throat", "Noun", 1, "health,body", "乾燥して喉が痛いのでお茶を飲みます。", "かんそうしてのどがいたいのでおちゃをのみます。", "Kansou shite nodo ga itai node ocha o nomimasu.", "Because it is dry and my throat hurts, I drink tea.", []),
    ("喉が渇く", "のどがかわく", "nodo ga kawaku", "to get thirsty", "Expression", 1, "health,daily", "運動したあとは喉が渇きます。", "うんどうしたあとはのどがかわきます。", "Undou shita ato wa nodo ga kawakimasu.", "After exercising, I get thirsty.", []),
    ("お腹が空く", "おなかがすく", "onaka ga suku", "to get hungry", "Expression", 1, "food,daily", "たくさん歩いてお腹が空きました。", "たくさんあるいておなかがすきました。", "Takusan aruite onaka ga sukimashita.", "I walked a lot and got hungry.", []),
    ("お腹がいっぱい", "おなかがいっぱい", "onaka ga ippai", "to be full (stomach)", "Expression", 1, "food,daily", "美味しい料理を食べてお腹がいっぱいです。", "おいしいりょうりをたべておなかがいっぱいです。", "Oishii ryouri o tabete onaka ga ippai desu.", "I ate delicious food and my stomach is full.", []),
    ("お大事に", "おだいじに", "odaiji ni", "take care (to sick person)", "Expression", 1, "health,polite", "無理をしないでどうぞお大事になさってください。", "むりをしないでどうぞおだいじになさってください。", "Muri o shinaide douzo odaiji ni nasatte kudasai.", "Please do not overdo it and take good care of yourself.", []),
    ("おかげさま", "おかげさま", "okagesama", "thanks to you / under your shadow", "Expression", 2, "polite,culture", "おかげさまで家族全員元気に暮らしています。", "おかげさまでかぞくぜんいんげんきにくらししています。", "Okagesama de kazoku zen'in genki ni kurashite imasu.", "Thanks to your kindness, the whole family is living in good health.", []),
    ("よろしく", "よろしく", "yoroshiku", "best regards, please treat me well", "Expression", 1, "social,polite", "明日からの新しいプロジェクト、どうぞよろしくお願いします。", "あしたからのあたらしいプロジェクト、どうぞよろしくおねがいします。", "Ashita kara no atarashii purojekuto, douzo yoroshiku onegai shimasu.", "Regarding the new project starting tomorrow, please treat me well.", []),
    ("遠慮深げ", "えんりょぶかげ", "enryobukage", "shy-looking, reserved, modest manner", "Na-Adjective", 2, "personality,culture", "彼女は遠慮深げに小さくお辞儀をしました。", "かのじょはえんりょぶかげにちいさくおじぎをしました。", "Kanojo wa enryobukage ni chiisaku ojigi o shimashita.", "She bowed slightly in a reserved manner.", []),
    ("準備運動", "じゅんびうんどう", "junbi undou", "warm-up exercises, preparatory calisthenics", "Noun / Suru Verb", 2, "sports,health", "プールに入る前にしっかり準備運動をします。", "プールにはいるまえにしっかりじゅんびうんどうをします。", "Puuru ni hairu mae ni shikkari junbi undou o shimasu.", "I do warm-up exercises thoroughly before entering the pool.", []),
    ("約束手形", "やくそくてがた", "yakusoku tegata", "promissory note (commercial)", "Noun", 2, "business,money", "会社の取引で約束手形を受け取りました。", "かいしゃのとりひきでやくそくてがたをうけとりました。", "Kaisha no torihiki de yakusoku tegata o uketorimashita.", "I received a promissory note in company transactions.", []),
    ("連絡先", "れんらくさき", "renrakusaki", "contact address / info", "Noun", 2, "communication,social", "念のため名刺に連絡先を書き添えました。", "ねんのためめいしにれんらくさきをかきそえました。", "Nen no tame meishi ni renrakusaki o kakisoemashita.", "Just in case, I added contact info on the business card.", []),
    ("交通費", "こうつうひ", "koutsuuhi", "travel expenses, transportation cost", "Noun", 2, "money,transport", "会社から毎月の交通費が全額支給されます。", "かいしゃからまいつきのこうつうひがぜんがくしきゅうされます。", "Kaisha kara maitsuki no koutsuuhi ga zengaku shikyuu saremasu.", "Transportation costs are fully reimbursed monthly by the company.", []),
    ("交差点", "こうさてん", "kousaten", "crossing, intersection", "Noun", 1, "transport,city", "人通りの多い交差点で青信号を待ちます。", "ひとどおりのおおいこうさてんであおしんごうをまちます。", "Hitodoori no ooi kousaten de aoshingou o machimasu.", "I wait for the green light at a busy intersection.", []),
    ("光熱費", "こうねつひ", "kounetsuhi", "utility costs (electricity, gas, heating)", "Noun", 2, "money,daily", "節電を心がけて毎月の光熱費を抑えています。", "せつでんをこころがけてまいつきのこうねつひをおさえています。", "Setsuden o kokorogakete maitsuki no kounetsuhi o osaete imasu.", "I keep electricity saving in mind to hold down monthly utility costs.", []),
    ("食卓", "しょくたく", "shokutaku", "dining table", "Noun", 2, "home,food", "家族みんなで温かい夕食の食卓を囲みます。", "かぞくみんなであたたかいゆうしょくのしょくたくをかこみます。", "Kazoku minna de atatakai yuushoku no shokutaku o kakomimasu.", "The whole family surrounds the warm evening dining table together.", []),
    ("居間", "いま", "ima", "living room (Western/modern)", "Noun", 2, "home,daily", "夕食のあとに居間のソファでくつろぎます。", "ゆうしょくのあとにいまのソファでくつろぎます。", "Yuushoku no ato ni ima no sofa de kutsurogimasu.", "After dinner, I relax on the sofa in the living room.", []),
    ("台所用品", "だいどころようひん", "daidokoro youhin", "kitchenware, kitchen utensils", "Noun", 2, "home,shopping", "使いやすい台所用品をデパートで買い揃えました。", "つかいやすいだいどころようひんをデパートでかいそろえました。", "Tsukaiyasui daidokoro youhin o depaato de kaisoroemashita.", "I bought and gathered easy-to-use kitchenware at the department store.", []),
    ("お風呂場", "おふろば", "ofuroba", "bathroom (bathing room)", "Noun", 2, "home,daily", "お風呂場をきれいに洗って換気扇を回します。", "おふろばをきれいにあらってかんきせんをまわします。", "Ofuroba o kirei ni aratte kankisen o mawashimasu.", "I cleanly wash the bathroom and turn on the ventilation fan.", []),
    ("洗面所", "せんめんじょ", "senmenjo", "washroom, lavatory", "Noun", 2, "home,daily", "朝起きて洗面所の鏡で身だしなみを整えます。", "あさおきてせんめんじょのかがみでみだしなみをととのえます。", "Asa okite senmenjo no kagami de midashinami o totonoemasu.", "I wake up in the morning and tidy my appearance in the washroom mirror.", []),
    ("郵便受け", "ゆうびんうけ", "yuubin'uke", "postbox, letterbox, mailbox", "Noun", 2, "home,communication", "毎朝新聞を取りに郵便受けまで歩きます。", "まいあさしんぶんをとりにゆうびんうけまであるきます。", "Maiasa shinbun o tori ni yuubin'uke made arukimasu.", "I walk to the mailbox every morning to collect the newspaper.", []),
]

print("Part 10 count:", len(part10_data))
with open('tools/part10.json', 'w') as out:
    json.dump(part10_data, out, ensure_ascii=False, indent=2)
