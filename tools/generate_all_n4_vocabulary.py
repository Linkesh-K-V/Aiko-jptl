import json, re

# Comprehensive canonical N4 vocabulary catalog across 21 semantic families
# Carefully curated to cover:
# 1. 175 N4 kanji Planned N4 vocab
# 2. 15 legacy N4 vocabulary (promoted)
# 3. Essential N4 verbs, adjectives, nouns, adverbs, expressions
# 4. Strict N5 deduplication

raw_entries = [
    # -------------------------------------------------------------
    # FAMILY 1: DAILY LIFE, HOME & ROUTINE
    # -------------------------------------------------------------
    ("生活", "せいかつ", "seikatsu", "daily life, living", "Noun / Suru Verb", 2, "daily,life", "日本の生活に慣れました。", "にほんのせいかつになれました。", "Nihon no seikatsu ni naremashita.", "I have gotten used to living in Japan.", "JLPT N4 Vocabulary Foundation", []),
    ("世話", "せわ", "sewa", "care, looking after", "Noun / Suru Verb", 2, "daily,family", "犬の世話を毎日します。", "いぬのせわをまいにちします。", "Inu no sewa o mainichi shimasu.", "I take care of the dog every day.", "JLPT N4 Vocabulary Foundation", []),
    ("準備", "じゅんび", "junbi", "preparation, arrangements", "Noun / Suru Verb", 2, "daily,work", "試験の準備を始めました。", "しけんのじゅんびをはじめました。", "Shiken no junbi o hajimemashita.", "I started preparing for the exam.", "JLPT N4 Vocabulary Foundation", []),
    ("習慣", "しゅうかん", "shuukan", "habit, custom", "Noun", 2, "daily,culture", "早起きの習慣を身につけます。", "はやおきのしゅうかんをみにつけます。", "Hayaoki no shuukan o mi ni tsukemasu.", "I acquire the habit of waking up early.", "JLPT N4 Vocabulary Foundation", ["n4_r_01"]),
    ("都合", "つごう", "tsugou", "convenience, availability", "Noun", 2, "daily,schedule", "明日の都合はいかがですか。", "あすのつごうはいかがですか。", "Asu no tsugou wa ikaga desu ka.", "How is your availability tomorrow?", "JLPT N4 Vocabulary Foundation", ["n5_l_03"]),
    ("用事", "ようじ", "youji", "errand, business to attend to", "Noun", 2, "daily,schedule", "午後から用事があります。", "ごごからようじがあります。", "Gogo kara youji ga arimasu.", "I have errands to run in the afternoon.", "JLPT N4 Vocabulary Foundation", []),
    ("片付ける", "かたづける", "katadzukeru", "to tidy up, clean up", "Ichidan Verb", 2, "daily,verbs,home", "部屋をきれいに片付けました。", "へやをきれいにかたづけました。", "Heya o kirei ni katadzukemashita.", "I tidied up the room thoroughly.", "JLPT N4 Vocabulary Foundation", []),
    ("掃除", "そうじ", "souji", "cleaning", "Noun / Suru Verb", 2, "daily,home", "毎朝部屋の掃除をします。", "まいあさへやのそうじをします。", "Maiasa heya no souji o shimasu.", "I clean the room every morning.", "JLPT N4 Vocabulary Foundation", []),
    ("洗濯", "せんたく", "sentaku", "laundry, washing clothes", "Noun / Suru Verb", 2, "daily,home", "天気がいいので洗濯をします。", "てんきがいいのでせんたくをします。", "Tenki ga ii node sentaku o shimasu.", "The weather is good, so I will do the laundry.", "JLPT N4 Vocabulary Foundation", []),
    ("燃える", "もえる", "moeru", "to burn", "Ichidan Verb", 2, "daily,nature", "燃えるゴミの日は火曜日です。", "もえるゴミのひはかようびです。", "Moeru gomi no hi wa kayoubi desu.", "Burnable trash day is Tuesday.", "JLPT N4 Vocabulary Foundation", []),
    ("ゴミ", "ゴミ", "gomi", "trash, garbage", "Noun", 1, "daily,home", "ゴミをゴミ箱に捨ててください。", "ゴミをごみばこにすててください。", "Gomi o gomibako ni sutete kudasai.", "Please throw the trash into the trash can.", "JLPT N4 Vocabulary Foundation", []),
    ("捨てる", "すてる", "suteru", "to throw away, discard", "Ichidan Verb", 2, "daily,verbs", "古い雑誌を捨てました。", "ふるいざっしをすてました。", "Furui zasshi o sutemashita.", "I threw away the old magazines.", "JLPT N4 Vocabulary Foundation", []),
    ("拾う", "ひろう", "hirou", "to pick up, gather", "Godan Verb", 2, "daily,verbs", "道で鍵を拾いました。", "みちでかぎをひろいました。", "Michi de kagi o hiroimashita.", "I picked up a key on the street.", "JLPT N4 Vocabulary Foundation", []),
    ("引っ越す", "ひっこす", "hikkosu", "to move house", "Godan Verb", 2, "daily,verbs,home", "来月新しいアパートへ引っ越します。", "らいげつあたらしいアパートへひっこします。", "Raigetsu atarashii apaato e hikkoshimasu.", "I will move to a new apartment next month.", "JLPT N4 Vocabulary Foundation", []),
    ("近所", "きんじょ", "kinjo", "neighborhood", "Noun", 2, "daily,community", "近所の人に挨拶しました。", "きんじょのひとにあいさつしました。", "Kinjo no hito ni aisatsu shimashita.", "I greeted a neighbor.", "JLPT N4 Vocabulary Foundation", []),
    ("玄関", "げんかん", "genkan", "entranceway, front door", "Noun", 2, "daily,home", "玄関で靴を脱ぎます。", "げんかんでくつをぬぎます。", "Genkan de kutsu o nugimasu.", "Take off your shoes at the entrance.", "JLPT N4 Vocabulary Foundation", []),
    ("階段", "かいだん", "kaidan", "stairs, staircase", "Noun", 2, "daily,home", "階段を急いで上がりました。", "かいだんをいそいであがりました。", "Kaidan o isoide agarimashita.", "I hurriedly went up the stairs.", "JLPT N4 Vocabulary Foundation", []),
    ("屋上", "おくじょう", "okujou", "rooftop", "Noun", 2, "daily,building", "屋上から街が見えます。", "おくじょうからまちがみえます。", "Okujou kara machi ga miemasu.", "You can see the town from the rooftop.", "JLPT N4 Vocabulary Foundation", []),
    ("押入れ", "おしいれ", "oshiire", "Japanese-style closet", "Noun", 2, "daily,home", "布団を押入れにしまいます。", "ふとんをおしいれにしまいます。", "Futon o oshiire ni shimaimasu.", "I put the futon away in the closet.", "JLPT N4 Vocabulary Foundation", []),
    ("畳", "たたみ", "tatami", "tatami mat", "Noun", 2, "daily,culture,home", "和室には畳が敷いてあります。", "わしつにはたたみがしいてあります。", "Washitsu ni wa tatami ga shiite arimasu.", "Tatami mats are laid out in the Japanese-style room.", "JLPT N4 Vocabulary Foundation", []),
    ("和室", "わしつ", "washitsu", "Japanese-style room", "Noun", 2, "daily,home", "旅館の和室に泊まりました。", "りょかんのわしつにとまりました。", "Ryokan no washitsu ni tomarimashita.", "I stayed in a Japanese-style room at an inn.", "JLPT N4 Vocabulary Foundation", []),
    ("洋室", "ようしつ", "youshitsu", "Western-style room", "Noun", 2, "daily,home", "ホテルは洋室を予約しました。", "ホテルはようしつをよやくしました。", "Hoteru wa youshitsu o yoyaku shimashita.", "I booked a Western-style room at the hotel.", "JLPT N4 Vocabulary Foundation", []),
    ("水道", "すいどう", "suidou", "water service, tap water", "Noun", 2, "daily,utility", "水道の水を止めました。", "すいどうのみずをとめました。", "Suidou no mizu o tomemashita.", "I turned off the tap water.", "JLPT N4 Vocabulary Foundation", []),
    ("ガス", "ガス", "gasu", "gas", "Noun", 1, "daily,utility", "料理の後、ガスの元栓を閉めます。", "りょうりのあと、ガスのえもとせんをしめます。", "Ryouri no ato, gasu no motosen o shimemasu.", "After cooking, I turn off the gas valve.", "JLPT N4 Vocabulary Foundation", []),
    ("電気代", "でんきだい", "denkidai", "electricity fee/bill", "Noun", 2, "daily,money", "先月の電気代が高かったです。", "せんげつのでんきだいが高かったです。", "Sengetsu no denkidai ga takakatta desu.", "Last month's electric bill was high.", "JLPT N4 Vocabulary Foundation", []),
    ("留守", "るす", "rusu", "absence from home", "Noun", 2, "daily,home", "留守の間に荷物が届きました。", "るすのあいだににもつがとどきました。", "Rusu no aida ni nimotsu ga todokimashita.", "A package arrived while I was away.", "JLPT N4 Vocabulary Foundation", []),
    ("家具", "かぐ", "kagu", "furniture", "Noun", 2, "daily,home", "新しい部屋に家具を置きます。", "あたらしいへやにかぐをおきます。", "Atarashii heya ni kagu o okimasu.", "I will place furniture in the new room.", "JLPT N4 Vocabulary Foundation", []),
    ("道具", "どうぐ", "dougu", "tool, instrument", "Noun", 2, "daily,work", "修理のための道具を揃えます。", "しゅうりのためのどうぐをそろえます。", "Shuuri no tame no dougu o soroemasu.", "I prepare the tools for repair.", "JLPT N4 Vocabulary Foundation", []),
    ("石鹸", "せっけん", "sekken", "soap", "Noun", 2, "daily,hygiene", "石鹸で手をきれいに洗います。", "せっけんでてをきれいにあらいます。", "Sekken de te o kirei ni araimasu.", "Wash your hands thoroughly with soap.", "JLPT N4 Vocabulary Foundation", []),
    ("タオル", "タオル", "taoru", "towel", "Noun", 1, "daily,hygiene", "顔を洗ってタオルで拭きます。", "かおをあらってタオルでふきます。", "Kao o aratte taoru de fukimasu.", "Wash your face and wipe with a towel.", "JLPT N4 Vocabulary Foundation", []),
    ("鏡", "かがみ", "kagami", "mirror", "Noun", 2, "daily,home", "鏡を見て髪を整えます。", "かがみをみてかみを整えます。", "Kagami o mite kami o totonoemasu.", "Look in the mirror and arrange your hair.", "JLPT N4 Vocabulary Foundation", []),
    ("鍵", "かぎ", "kagi", "key, lock", "Noun", 1, "daily,security", "出かける前に鍵をかけます。", "でかけるまえにかぎをかけます。", "Dekakeru mae ni kagi o kakemasu.", "Lock the door before leaving.", "JLPT N4 Vocabulary Foundation", []),
    ("人形", "にんぎょう", "ningyou", "doll, figure", "Noun", 2, "daily,culture", "棚にかわいい人形が飾ってあります。", "たなにかわいいにんぎょうがかざってあります。", "Tana ni kawaii ningyou ga kazatte arimasu.", "A cute doll is displayed on the shelf.", "JLPT N4 Vocabulary Foundation", []),
    ("引き出し", "ひきだし", "hikidashi", "drawer", "Noun", 2, "daily,home", "パスポートを引き出しに入れます。", "パスポートをひきだしにいれます。", "Pasupooto o hikidashi ni iremasu.", "Put the passport into the drawer.", "JLPT N4 Vocabulary Foundation", []),
    ("棚", "たな", "tana", "shelf", "Noun", 2, "daily,home", "本を棚に並べました。", "ほんをたなに並べました。", "Hon o tana ni narabemashita.", "I arranged the books on the shelf.", "JLPT N4 Vocabulary Foundation", []),
    ("カーテン", "カーテン", "kaaten", "curtain", "Noun", 1, "daily,home", "朝カーテンを開けて日光を入れます。", "あさカーテンをあけてにっこうをいれます。", "Asa kaaten o akete nikkou o iremasu.", "In the morning I open the curtains to let in sunlight.", "JLPT N4 Vocabulary Foundation", []),
    ("布団", "ふとん", "futon", "futon, Japanese bedding", "Noun", 1, "daily,home", "夜布団を敷いて寝ます。", "よるふとんをしいてねます。", "Yoru futon o shiite nemasu.", "At night I lay out the futon and sleep.", "JLPT N4 Vocabulary Foundation", []),
    ("シーツ", "シーツ", "shiitsu", "bed sheet", "Noun", 1, "daily,home", "週末にシーツを洗います。", "しゅうまつにシーツをあらいます。", "Shuumatsu ni shiitsu o araimasu.", "I wash the bed sheets on weekends.", "JLPT N4 Vocabulary Foundation", []),
    ("毛布", "もうふ", "moufu", "blanket", "Noun", 2, "daily,home", "寒いので毛布をもう一枚使います。", "さむいのでもうふをもういちまいつかいます。", "Samui node moufu o mou ichimai tsukaimasu.", "Because it is cold, I use another blanket.", "JLPT N4 Vocabulary Foundation", []),
    ("暖房", "だんぼう", "danbou", "heating, heater", "Noun / Suru Verb", 2, "daily,home", "部屋が寒いので暖房をつけます。", "へやがさむいのでだんぼうをつけます。", "Heya ga samui node danbou o tsukemasu.", "The room is cold, so I turn on the heating.", "JLPT N4 Vocabulary Foundation", []),
    ("冷房", "れいぼう", "reibou", "air conditioning, cooling", "Noun / Suru Verb", 2, "daily,home", "夏は冷房を入れます。", "なつはれいぼうをいれます。", "Natsu wa reibou o iremasu.", "In summer I turn on the air conditioner.", "JLPT N4 Vocabulary Foundation", []),
]

print(f"Sample raw count: {len(raw_entries)}")
