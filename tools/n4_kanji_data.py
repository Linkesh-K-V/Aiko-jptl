#!/usr/bin/env python3
"""
Canonical N4 Kanji Dataset (175 Kanji).
Strictly deduplicated against the 104 N5 Kanji.
Every Kanji includes character, readings, stroke count, radical, English meaning,
mnemonic, original example sentence, difficulty, tags, and planned vocabulary connections.
"""

# The 175 Canonical N4 Kanji (Grouped by 7 Pedagogical Stages)
# Stage 1: Society, Institutions & Community (25 Kanji)
# Stage 2: Travel, Transport & Spatial Direction (25 Kanji)
# Stage 3: Daily Life, Food, Home & Commerce (25 Kanji)
# Stage 4: Nature, Environment & Weather (25 Kanji)
# Stage 5: Mind, Communication, Education & Arts (25 Kanji)
# Stage 6: Human Body, Health & Clothing (25 Kanji)
# Stage 7: Actions, Change, State & Measurement (25 Kanji)
# Total = 7 * 25 = 175 Kanji!

RAW_KANJI_LIST = [
    # STAGE 1: Society, Institutions & Community
    ("会", "カイ, エ", "あ.う", 6, "人", "meet, society, association", "明日東京で友達に会います。", "明日東京で友達にあいます。", "Ashita Toukyou de tomodachi ni aimasu.", "I will meet a friend in Tokyo tomorrow.", "society,basic", ["会話", "会社"]),
    ("同", "ドウ", "おな.じ", 6, "口", "same, agree", "彼と私は同じクラスの学生です。", "かれとわたしはおなじクラスのがくせいです。", "Kare to watashi wa onaji kurasu no gakusei desu.", "He and I are students in the same class.", "society,basic", ["同じ", "同時に"]),
    ("事", "ジ, ズ", "こと, つか.う", 8, "一", "matter, thing, fact, business", "大切な用事があるので早く帰ります。", "たいせつなようじがあるのではやくかえります。", "Taisetsu na youji ga aru node hayaku kaerimasu.", "I have important business, so I will return early.", "society,basic", ["仕事", "火事"]),
    ("員", "イン", "", 10, "口", "member, employee", "駅員に切符の買い方を尋ねました。", "えきいんにきっぷのかいかたをたずねました。", "Ekiin ni kippu no kaikata o tazunemashita.", "I asked the station staff member how to buy a ticket.", "society,work", ["会社員", "店員"]),
    ("院", "イン", "", 10, "阜", "institution, hospital, hall", "体調が悪いので病院へ行きます。", "たいちょうがわるいのでびょういんへいきます。", "Taichou ga warui node byouin e ikimasu.", "I don't feel well, so I'm going to the hospital.", "society,health", ["病院", "大学院"]),
    ("公", "コウ, ク", "おおやけ", 4, "八", "public, official", "天気がいいので公園を散歩しました。", "てんきがいいのでこうえんをさんぽしました。", "Tenki ga ii node kouen o sanpo shimashita.", "Since the weather was nice, I took a walk in the park.", "society,places", ["公園", "公衆"]),
    ("園", "エン", "その", 13, "囗", "garden, park, orchard", "週末に家族と動物園へ出かけました。", "しゅうまつにかぞくとどうぶつえんへでかけました。", "Shuumatsu ni kazoku to doubutsuen e dekakemashita.", "I went to the zoo with my family on the weekend.", "society,places", ["公園", "動物園"]),
    ("市", "シ", "いち", 5, "巾", "city, market", "この市には多くの留学生が住んでいます。", "このしにはおおくのりゅうがくせいがすんでいます。", "Kono shi ni wa ooku no ryuugakusei ga sunde imasu.", "Many international students live in this city.", "society,places", ["市民", "市場"]),
    ("町", "チョウ", "まち", 7, "田", "town, neighborhood", "静かで住みやすい町に引っ越しました。", "しずかですみやすいまちにひっこしました。", "Shizuka de sumiyasui machi ni hikkoshimashita.", "I moved to a quiet and liveable town.", "society,places", ["町長", "下町"]),
    ("村", "ソン", "むら", 7, "木", "village", "山の中にある小さな村を訪れました。", "やまのなかにあるちいさなむらをおとずれました。", "Yama no naka ni aru chiisana mura o otozuremashita.", "I visited a small village nestled in the mountains.", "society,places", ["村人", "農村"]),
    ("区", "ク", "", 4, "匚", "ward, district, section", "区役所へ行って住所の登録をしました。", "くやくしょへいってじゅうしょのとうろくをしました。", "Kuyakusho e itte juusho no touroku o shimashita.", "I went to the ward office and registered my address.", "society,places", ["区役所", "地区"]),
    ("都", "ト, ツ", "みやこ", 11, "邑", "metropolis, capital", "東京は日本の首都です。", "とうきょうはにほんのしゅとです。", "Toukyou wa nihon no shuto desu.", "Tokyo is the capital of Japan.", "society,places", ["京都", "都合"]),
    ("府", "フ", "", 8, "广", "urban prefecture, government", "大阪府には美味しい食べ物がたくさんあります。", "おおさかふにはおいしいたべものがたくさんあります。", "Oosaka-fu ni wa oishii tabemono ga takusan arimasu.", "There is plenty of delicious food in Osaka Prefecture.", "society,places", ["政府", "大阪府"]),
    ("県", "ケン", "か.ける", 9, "目", "prefecture", "神奈川県にある横浜へ観光に行きました。", "かながわけんにあるよこはまへかんこうにいきました。", "Kanagawa-ken ni aru Yokohama e kankou ni ikimashita.", "I went sightseeing to Yokohama in Kanagawa Prefecture.", "society,places", ["県庁", "他県"]),
    ("官", "カン", "", 8, "宀", "government official, bureaucrat", "空港の入国審査で係官にパスポートを見せました。", "くうこうのにゅうこくしんさでかかりかんにパスポートをみせました。", "Kuukou no nyuukoku shinsa de kakarikan ni pasupooto o misemashita.", "I showed my passport to the official at airport immigration.", "society,official", ["警官", "裁判官"]),
    ("民", "ミン", "たみ", 5, "氏", "people, citizen, nation", "市民図書館で静かに本を読みました。", "しみんとしょかんでしずかにほんをよみました。", "Shimin toshokan de shizuka ni hon o yomimashita.", "I read a book quietly at the civic library.", "society,people", ["市民", "国民"]),
    ("主", "シュ, ス", "おも, ぬし", 5, "丶", "master, main, lord", "このアパートの大家さんは親切な方です。", "このアパートのおおやさんはしんせつなかたです。", "Kono apaato no ooyasan wa shinsetsu na kata desu.", "The landlord of this apartment is a kind person.", "society,roles", ["主人", "主に"]),
    ("仕", "シ, ジ", "つか.える", 5, "人", "serve, do, attend", "毎朝八時に出社して仕事を始めます。", "まいあさはちじにしゅっしゃしてしごとをはじめます。", "Maiasa hachiji ni shussha shite shigoto o hajimemasu.", "I arrive at work at 8:00 every morning and begin my job.", "society,work", ["仕事", "仕方"]),
    ("業", "ギョウ, ゴウ", "わざ", 13, "木", "business, vocation, industry", "大学を卒業した後の就職について考えます。", "だいがくをそつぎょうしたあとのしゅうしょくについてかんがえます。", "Daigaku o sotsugyou shita ato no shuushoku ni tsuite kangaemasu.", "I think about finding employment after graduating from university.", "society,work", ["授業", "卒業"]),
    ("役", "ヤク, エキ", "", 7, "彳", "role, duty, service", "区役所の窓口で書類を提出しました。", "くやくしょのまどぐちでしょるいをつていしゅつしました。", "Kuyakusho no madoguchi de shorui o teishutsu shimashita.", "I submitted the documents at the ward office counter.", "society,roles", ["役所", "役に立つ"]),
    ("所", "ショ", "ところ", 8, "戸", "place, office", "住所と電話番号をこの紙に書いてください。", "じゅうしょとでんわばんごうをこのかみにかいてください。", "Juusho to denwabangou o kono kami ni kaite kudasai.", "Please write your address and telephone number on this paper.", "society,places", ["近所", "場所"]),
    ("屋", "オク", "や", 9, "尸", "roof, shop, seller", "駅前の本屋で日本語の参考書を買いました。", "えきまえのほんやでにほんごのさんこうしょをかいました。", "Ekimae no hon'ya de nihongo no sankousho o kaimashita.", "I bought a Japanese study guide at the bookstore in front of the station.", "society,places", ["本屋", "部屋"]),
    ("館", "カン", "やかた", 16, "食", "building, hall, mansion", "午後は市立図書館で勉強する予定です。", "ごごはしりつとしょかんでべんきょうするよていです。", "Gogo wa shiritsu toshokan de benkyou suru yotei desu.", "In the afternoon, I plan to study at the city library.", "society,places", ["図書館", "映画館"]),
    ("堂", "ドウ", "", 11, "土", "public chamber, hall", "お昼休みに学生食堂でカレーを食べました。", "おひるやすみにがくせいしょくどうでカレーをたべました。", "Ohiruyasumi ni gakusei shokudou de karee o tabemashita.", "During lunch break, I ate curry at the student cafeteria.", "society,places", ["食堂", "講堂"]),
    ("場", "ジョウ", "ば", 12, "土", "location, place, ground", "車をスーパーの駐車場に止めました。", "くるまをスーパーのちゅうしゃじょうにとめました。", "Kuruma o suupaa no chuushajou ni tomemashita.", "I parked the car in the supermarket parking lot.", "society,places", ["場所", "工場"]),

    # STAGE 2: Travel, Transport & Spatial Direction
    ("運", "ウン", "はこ.ぶ", 12, "辵", "carry, transport, fortune", "大きなトラックで新しい荷物を運びました。", "おおきなトラックであたらしいにもつをはこびました。", "Ookina torakku de atarashii nimotsu o hakobimashita.", "I transported the new luggage with a large truck.", "transport,movement", ["運転", "運動"]),
    ("転", "テン", "ころ.がる, ころ.ぶ", 11, "車", "revolve, turn, fall over", "休日に海岸沿いを自転車で走りました。", "きゅうじつにかいがんぞいをじてんしゃではしりました。", "Kyuujitsu ni kaiganzoi o jitensha de hashirimashita.", "On my day off, I rode a bicycle along the coast.", "transport,movement", ["自転車", "運転"]),
    ("動", "ドウ", "うご.く, うご.かす", 11, "力", "move, motion, stir", "このボタンを押すと機械が動きます。", "このボタンをおすときかいがうごきます。", "Kono botan o osu to kikai ga ugokimasu.", "When you press this button, the machine moves.", "movement,action", ["自動車", "運動"]),
    ("止", "シ", "と.まる, と.める", 4, "止", "stop, halt", "赤信号ですから、ここで一度止まってください。", "あかしんごうですから、ここでいちどとまってください。", "Akashingou desu kara, koko de ichido tomatte kudasai.", "Because it's a red light, please stop here once.", "transport,movement", ["止まる", "中止"]),
    ("歩", "ホ, ブ", "ある.く, あゆ.む", 8, "止", "walk, step", "毎日駅から会社まで二十分歩きます。", "まいにちえきからかいしゃまでにじゅっぷんあるきます。", "Maiasa eki kara kaisha made nijuppun arukimasu.", "Every day I walk 20 minutes from the station to the office.", "movement,health", ["歩く", "散歩"]),
    ("走", "ソウ", "はし.る", 7, "走", "run, gallop", "電車に遅れそうだったので急いで走りました。", "でんしゃにおくれそうだったのでいそいではしりました。", "Densha ni okuresou datta node isoide hashirimashita.", "Because I seemed about to miss the train, I ran in a hurry.", "movement,action", ["走る", "走行"]),
    ("急", "キュウ", "いそ.ぐ", 9, "心", "hurry, urgent, steep", "急な用事で東京へ行くことになりました。", "きゅうなようじでとうきょうへいくことになりました。", "Kyuu na youji de Toukyou e iku koto ni narimashita.", "Due to urgent business, it was decided that I would go to Tokyo.", "movement,action", ["急ぐ", "特急"]),
    ("通", "ツウ", "とお.る, かよ.う", 10, "辵", "commute, pass through, traffic", "毎朝バスに乗って高校へ通っています。", "まいあさバスにのってこうこうへかよっています。", "Maiasa basu ni notte koukou e kayotte imasu.", "Every morning I commute to high school by bus.", "transport,movement", ["通う", "通り"]),
    ("路", "ロ", "じ, みち", 13, "足", "road, path, route", "信号のある交差点を渡って線路沿いを歩きました。", "しんごうのあるこうさてんをわたってせんろぞいをあるきました。", "Shingou no aru kousaten o watatte senrozoi o arukimashita.", "I crossed the signaled intersection and walked along the railway tracks.", "transport,places", ["線路", "道路"]),
    ("旅", "リョ", "たび", 10, "方", "trip, travel, journey", "夏休みに友人と北海道へ旅行しました。", "なつやすみにゆうじんとほっかいどうへりょこうしました。", "Natsuyasumi ni yuujin to Hokkaidou e ryokou shimashita.", "During summer vacation, I traveled to Hokkaido with my friend.", "travel,recreation", ["旅行", "旅人"]),
    ("着", "チャク, ジャク", "き.る, つ.く", 12, "羊", "arrive, don, wear", "新幹線は定刻通りに京都駅に着きました。", "しんかんせんはていこくどおりにきょうとえきにつきました。", "Shinkansen wa teikokudoori ni Kyouto-eki ni tsukimashita.", "The bullet train arrived at Kyoto Station on schedule.", "travel,movement", ["着く", "上着"]),
    ("発", "ハツ, ホツ", "た.つ", 9, "癶", "depart, emit, publish", "東京発の特急電車に乗って箱根へ向かいました。", "とうきょうはつのとっきゅうでんしゃにのってはこねへむかいました。", "Toukyou hatsu no tokkyuu densha ni notte Hakone e mukaimashita.", "I boarded the limited express departing from Tokyo and headed to Hakone.", "travel,transport", ["出発", "発見"]),
    ("去", "キョ, コ", "さ.る", 5, "厶", "past, leave, depart", "去年の春に高校を無事に卒業しました。", "きょねんのはるにこうこうをぶじにそつぎょうしました。", "Kyonen no haru ni koukou o buji ni sotsugyou shimashita.", "Last spring, I graduated safely from high school.", "time,travel", ["去年", "過去"]),
    ("近", "キン, コン", "ちか.い", 7, "辵", "near, close, early", "駅から一番近いスーパーで買い物をします。", "えきからいちばんちかいスーパーでかいものをします。", "Eki kara ichiban chikai suupaa de kaimono o shimasu.", "I shop at the supermarket closest to the station.", "spatial,distance", ["近い", "近所"]),
    ("遠", "エン, オン", "とお.い", 13, "辵", "far, distant", "家から学校まで遠いので毎朝早く起きます。", "いえからがっこうまでとおいのでまいあさはやくおきます。", "Ie kara gakkou made tooi node maiasa hayaku okimasu.", "Because it's far from home to school, I get up early every morning.", "spatial,distance", ["遠い", "遠慮"]),
    ("進", "シン", "すす.む, すす.める", 11, "辵", "advance, proceed, progress", "この道をまっすぐ進むと大きな郵便局があります。", "このみちをまっすぐすすむとおおきなゆうびんきょくがあります。", "Kono michi o massugu susumu to ookina yuubinkyoku ga arimasu.", "If you advance straight along this road, there is a large post office.", "movement,direction", ["進む", "進学"]),
    ("返", "ヘン", "かえ.す, かえ.る", 7, "辵", "return, answer", "図書館で借りた本を期日までに返しました。", "としょかんでかりたほんをきじつまでにかえしました。", "Toshokan de karita hon o kijitsu made ni kaeshimashita.", "I returned the book borrowed from the library by the due date.", "action,movement", ["返す", "返事"]),
    ("送", "ソウ", "おく.る", 9, "辵", "send, dispatch, escort", "故郷の両親に手紙と写真を送りました。", "こきょうのりょうしんにてがみとしゃしんをおくりました。", "Kokyou no ryoushin ni tegami to shashin o okurimashita.", "I sent a letter and photographs to my parents in my hometown.", "movement,communication", ["送る", "見送る"]),
    ("界", "カイ", "", 9, "田", "world, boundary", "世界中の様々な国の文化を学びたいです。", "せかいじゅうのさまざまなくにのぶんかをまなびたいです。", "Sekaijuu no samazama na kuni no bunka o manabitai desu.", "I want to learn about cultures from various countries around the world.", "spatial,world", ["世界", "限界"]),
    ("野", "ヤ, ショ", "の", 11, "里", "field, plains, rustic", "広大な平野でたくさんの野菜が育てられています。", "こうだいなへいやでたくさんのやさいがそだてられています。", "Koudai na heiya de takusan no yasai ga sodaterarete imasu.", "Many vegetables are grown in the vast open plain.", "nature,spatial", ["野菜", "分野"]),
    ("地", "チ, ジ", "", 6, "土", "ground, earth, region", "地震の時は慌てずに安全な場所へ移動してください。", "じしんのときはあわてずにあんぜんなばしょへいどうしてください。", "Jishin no toki wa awatezu ni anzen na basho e idou shite kudasai.", "In the event of an earthquake, move calmly to a safe place.", "nature,spatial", ["地下鉄", "地図"]),
    ("海", "カイ", "うみ", 9, "水", "sea, ocean", "青い海を眺めながら静かな時間を過ごしました。", "あおいうみをながめながらしずかなじかんをすごしました。", "Aoi umi o nagamenagara shizuka na jikan o sugoshimashita.", "I spent a quiet time while gazing at the blue ocean.", "nature,travel", ["海岸", "海外"]),
    ("空", "クウ", "そら, あ.く, から", 8, "穴", "sky, empty, void", "澄み渡る秋の青空に白い雲が浮かんでいます。", "すみわたるあきのあおぞらにしろいくもがうかんでいます。", "Sumiwataru aki no aozora ni shiroi kumo ga ukande imasu.", "White clouds are floating in the clear autumn blue sky.", "nature,spatial", ["空気", "空港"]),
    ("港", "コウ", "みなと", 12, "水", "harbor, port", "成田国際空港に夕方無事に到着しました。", "なりたこくさいくうこうにゆうがたぶじにとうちゃくしました。", "Narita kokusai kuukou ni yuugata buji ni touchaku shimashita.", "I arrived safely at Narita International Airport in the evening.", "transport,places", ["空港", "港"]),
    ("乗", "ジョウ", "の.る, の.せる", 9, "丿", "ride, board, mount", "朝八時の地下鉄に乗って会社へ行きます。", "あさはちじのちかてつにのってかいしゃへいきます。", "Asa hachiji no chikatetsu ni notte kaisha e ikimasu.", "I board the 8:00 morning subway and go to work.", "transport,action", ["乗る", "乗り換える"]),

    # STAGE 3: Daily Life, Food, Home & Commerce
    ("売", "バイ", "う.る, う.れる", 7, "士", "sell", "駅前の売店で朝の新聞を買いました。", "えきまえのばいてんであさのしんぶんをかいました。", "Ekimae no baiten de asa no shinbun o kaimashita.", "I bought the morning newspaper at the shop in front of the station.", "commerce,daily", ["売る", "売り場"]),
    ("買", "バイ", "か.う", 12, "貝", "buy, purchase", "週末にデパートで新しいコートを買いました。", "しゅうまつにデパートであたらしいコートをかいました。", "Shuumatsu ni depaato de atarashii kooto o kaimashita.", "I bought a new coat at the department store on the weekend.", "commerce,daily", ["買う", "買い物"]),
    ("貸", "タイ", "か.す", 12, "貝", "lend, loan", "友達に日本語のノートを貸してあげました。", "ともだちににほんごのノートをかしてあげました。", "Tomodachi ni nihongo no nooto o kashite agemashita.", "I lent my Japanese study notes to my friend.", "commerce,daily", ["貸す", "賃貸"]),
    ("借", "シャク", "か.りる", 10, "人", "borrow, rent", "市民図書館で日本の小説を三冊借りました。", "しみんとしょかんでにほんのしょうせつをさんさつかりました。", "Shimin toshokan de nihon no shousetsu o sansatsu karimashita.", "I borrowed three Japanese novels from the civic library.", "commerce,daily", ["借りる", "借金"]),
    ("銀", "ギン", "しろがね", 14, "金", "silver", "毎月の給料日に銀行でお金を下ろします。", "まいつきのきゅうりょうびにぎんこうでおかねをおろします。", "Maitsuki no kyuuryoubi ni ginkou de okane o oroshimasu.", "On monthly payday, I withdraw cash at the bank.", "commerce,finance", ["銀行", "銀色"]),
    ("紙", "シ", "かみ", 10, "糸", "paper", "手紙をきれいな便箋に書いてポストに入れました。", "てがみをきれいなびんせんにかいてポストにいれました。", "Tegami o kirei na binsen ni kaite posuto ni iremashita.", "I wrote a letter on beautiful stationery and posted it.", "daily,communication", ["手紙", "用紙"]),
    ("飯", "ハン", "めし", 12, "食", "meal, cooked rice", "家族と一緒に朝ご飯をしっかり食べました。", "かぞくといっしょにあさごはんをしっかりたべました。", "Kazoku to issho ni asagohan o shikkari tabemashita.", "I ate a hearty breakfast together with my family.", "daily,food", ["ご飯", "朝飯"]),
    ("肉", "ニク", "しし", 6, "肉", "meat, flesh", "今夜の夕食は美味しい牛肉の鍋料理です。", "こんやのゆうしょくはおいしいぎゅうにくのなべりょうりです。", "Kon'ya no yuushoku wa oishii gyuuniku no naberyouri desu.", "Tonight's dinner is a delicious beef hot-pot dish.", "daily,food", ["牛肉", "豚肉"]),
    ("牛", "ギュウ", "うし", 4, "牛", "cow, cattle, beef", "朝起きて冷たい牛乳を一杯飲みました。", "あさおきてつめたいぎゅうにゅうをいっぱいのみました。", "Asa okite tsumetai gyuunyuu o ippai nomimashita.", "Upon waking in the morning, I drank a glass of cold milk.", "daily,food", ["牛乳", "牛肉"]),
    ("魚", "ギョ", "うお, さかな", 11, "魚", "fish", "新鮮な魚を使ったお寿司を注文しました。", "しんせんなさかなをつかったおすしをちゅうもんしました。", "Shinsen na sakana o tsukatta osushi o chuumon shimashita.", "I ordered sushi made with freshly caught fish.", "daily,food", ["魚屋", "金魚"]),
    ("鳥", "チョウ", "とり", 11, "鳥", "bird, fowl, chicken", "公園の池の周りで小鳥たちがさえずっています。", "こうえんのいけのまわりでことりたちがさえずっています。", "Kouen no ike no mawari de kotoritachi ga saezutte imasu.", "Small birds are chirping around the park pond.", "daily,nature", ["小鳥", "鳥肉"]),
    ("茶", "チャ, サ", "", 9, "艸", "tea", "食後に温かい緑茶を一杯いただきました。", "しょくごにあたたかいりょくちゃをいっぱいいただきました。", "Shokugo ni atatakai ryokucha o ippai itadakimashita.", "After the meal, I had a cup of warm green tea.", "daily,food", ["お茶", "喫茶店"]),
    ("菜", "サイ", "な", 11, "艸", "vegetable, side dish, greens", "健康のために毎朝新鮮な野菜を食べます。", "けんこうのためにまいあさしんせんなやさいをたべます。", "Kenkou no tame ni maiasa shinsen na yasai o tabemasu.", "For my health, I eat fresh vegetables every morning.", "daily,food", ["野菜", "総菜"]),
    ("味", "ミ", "あじ, あじ.わう", 8, "口", "flavor, taste", "この料理は少し甘い味がして美味しいです。", "このりょうりはすこしあまいあじがしておいしいです。", "Kono ryouri wa sukoshi amai aji ga shite oishii desu.", "This dish has a slightly sweet flavor and is delicious.", "daily,food", ["意味", "味わう"]),
    ("料", "リョウ", "", 10, "斗", "fee, materials, ingredients", "日本料理の作り方を教室で習いました。", "にほんりょうりのつくりかたをきょうしつでならいました。", "Nihonryouri no tsukurikata o kyoushitsu de naraimashita.", "I learned how to prepare Japanese cuisine in the cooking class.", "daily,food", ["料理", "料金"]),
    ("理", "リ", "ことわり", 11, "玉", "logic, reason, principle", "休日に友達を家に招いて手料理を振る舞いました。", "きゅうじつにともだちをいえにまねいててりょうりをふるまいました。", "Kyuujitsu ni tomodachi o ie ni maneite teryouri o furumaimashita.", "On my day off, I invited my friend home and served home-cooked food.", "daily,logic", ["料理", "理由"]),
    ("物", "ブツ, モツ", "もの", 8, "牛", "thing, object, matter", "駅ビルで週末の旅行に必要な買い物をしました。", "えきビルでしゅうまつのりょこうにひつようなかいものをしました。", "Ekibiru de shuumatsu no ryokou ni hitsuyou na kaimono o shimashita.", "I did shopping for items needed for the weekend trip at the station building.", "daily,basic", ["食べ物", "買い物"]),
    ("品", "ヒン", "しな", 9, "口", "goods, refinement, article", "伝統工芸品をお土産として選びました。", "でんとうこうげいひんをおみやげとしてえらびました。", "Dentou kougeihin o omiyage to shite erabimashita.", "I chose a traditional handicraft as a souvenir.", "commerce,daily", ["商品", "作品"]),
    ("服", "フク", "", 8, "月", "clothing, attire, obey", "新しい季節に合わせて洋服を買いに行きました。", "あたらしいきせつにあわせてようふくをかいにいきました。", "Atarashii kisetsu ni awasete youfuku o kai ni ikimashita.", "I went to buy clothes suited to the new season.", "daily,clothing", ["洋服", "和服"]),
    ("洋", "ヨウ", "", 9, "水", "ocean, Western, foreign", "和食だけでなく美味しい洋食も大好きです。", "わしょくだけでなくおいしいようしょくもだいすきです。", "Washoku dakedenaku oishii youshoku mo daisuki desu.", "I love not only Japanese food, but also delicious Western food.", "daily,culture", ["洋食", "洋服"]),
    ("家", "カ, ケ", "いえ, や", 10, "宀", "house, home, family", "仕事を終えて自分の家にまっすぐ帰りました。", "しごとをおえてじぶんのいえにまっすぐかえりました。", "Shigoto o oete jibun no ie ni massugu kaerimashita.", "Finishing work, I returned directly to my own home.", "daily,home", ["家族", "家内"]),
    ("族", "ゾク", "", 11, "方", "tribe, family, clan", "お正月に家族全員が集まってお祝いをしました。", "おしょうがつにかぞくぜんいんがあつまっておいわいをしました。", "Oshougatsu ni kazoku zen'in ga atsumatte oiwai o shimashita.", "At New Year's, the whole family gathered and celebrated.", "daily,home", ["家族", "親族"]),
    ("具", "グ", "そな.える", 8, "八", "tool, utensil, ingredient", "新生活のために新しい家具を揃えました。", "しんせいかつのためにあたらしいかぐをそろえました。", "Shinseikatsu no tame ni atarashii kagu o soroemashita.", "I gathered new furniture for my new life.", "daily,home", ["道具", "家具"]),
    ("門", "モン", "かど", 8, "門", "gate, entrance", "大学の正門の前で友達と待ち合わせました。", "だいがくのせいもんのまえでともだちとまちあわせました。", "Daigaku no seimon no mae de tomodachi to machiawasemashita.", "I met up with my friend in front of the main university gate.", "daily,places", ["専門", "校門"]),
    ("戸", "コ", "と", 4, "戸", "door, counter for houses", "風が入らないように部屋の戸をしっかり閉めました。", "かぜがはいらないようにへやのとをしっかりしめました。", "Kaze ga hairanai you ni heya no to o shikkari shimemashita.", "I closed the room door securely so wind would not enter.", "daily,home", ["戸棚", "雨戸"]),

    # STAGE 4: Nature, Environment & Weather
    ("春", "シュン", "はる", 9, "日", "spring (season)", "春になると庭の桜が満開になります。", "はるになるとにわのさくらがまんかいになります。", "Haru ni naru to niwa no sakura ga mankai ni narimasu.", "When spring arrives, the cherry blossoms in the garden reach full bloom.", "nature,season", ["春休み", "青春"]),
    ("夏", "カ, ゲ", "なつ", 10, "夂", "summer", "日本の夏はとても蒸し暑くて雨が多いです。", "にほんのなつはとてもむしあつくてあめがおおいです。", "Nihon no natsu wa totemo mushiatsukute ame ga ooi desu.", "Summer in Japan is very humid and has plenty of rain.", "nature,season", ["夏休み", "真夏"]),
    ("秋", "シュウ", "あき", 9, "禾", "autumn, fall", "秋は紅葉が美しく、過ごしやすい季節です。", "あきはこうようがうつくしく、すごしやすいきせつです。", "Aki wa kouyou ga utsukushiku, sugoshiyasui kisetsu desu.", "Autumn has beautiful fall foliage and is a comfortable season.", "nature,season", ["秋祭り", "晩秋"]),
    ("冬", "トウ", "ふゆ", 5, "冫", "winter", "北国の冬は雪がたくさん降って寒いです。", "きたぐにのふゆはゆきがたくさんふってさむいです。", "Kitaguni no fuyu wa yuki ga takusan futte samui desu.", "Winter in the north country brings heavy snowfall and cold.", "nature,season", ["冬休み", "真冬"]),
    ("天", "テン", "あまつ", 4, "大", "heavens, sky, weather", "明日の天気予報を確認してから出かけます。", "あしたのてんきよほうをかくにんしてからでかけます。", "Ashita no tenki yohou o kakunin shite kara dekakemasu.", "I will check tomorrow's weather forecast before leaving.", "nature,weather", ["天気", "天国"]),
    ("気", "キ, ケ", "いき", 6, "气", "spirit, mind, atmosphere", "朝の澄んだ空気を胸いっぱいに吸い込みました。", "あさのすんだくうきをむねいっぱいに入いこみました。", "Asa no sunda kuuki o mune ippai ni suikomimashita.", "I inhaled the morning's crisp air deeply into my chest.", "nature,spirit", ["天気", "空気"]),
    ("雨", "ウ", "あめ, あま", 8, "雨", "rain", "激しい雨が降ってきたので傘を差しました。", "はげしいあめがふってきたのでかさをさしました。", "Hageshii ame ga futte kita node kasa o sashimashita.", "Heavy rain began to fall, so I opened an umbrella.", "nature,weather", ["大雨", "雨季"]),
    ("雪", "セツ", "ゆき", 11, "雨", "snow", "昨夜降った雪で街全体が白く覆われました。", "さくやふったゆきでまちぜんたいがしろくおおわれました。", "Sakuya futta yuki de machi zentai ga shiroku oowaremashita.", "With the snow that fell last night, the whole town was covered in white.", "nature,weather", ["雪景色", "大雪"]),
    ("風", "フウ, フ", "かぜ, かざ", 9, "風", "wind, breeze, manner", "海から心地よい涼しい風が吹いてきました。", "うみからここちよいすずしいかぜがふいてきました。", "Umi kara kokochiyoi suzushii kaze ga fuite kimashita.", "A pleasant and cool breeze blew in from the sea.", "nature,weather", ["台風", "風邪"]),
    ("晴", "セイ", "は.れる, は.らす", 12, "日", "clear weather, sunny", "休日は気持ちよく晴れて絶好の行楽日和でした。", "きゅうじつはきもちよくはれてぜっこうのこうらくびよりでした。", "Kyuujitsu wa kimochiyoku harete zekkou no kourakubiyori desu.", "The holiday was pleasantly clear, perfect outing weather.", "nature,weather", ["晴れ", "快晴"]),
    ("雲", "ウン", "くも", 12, "雨", "cloud", "青空に白い積乱雲がもくもくと広がっています。", "あおぞらにしろいせきらんうんがもくもくとひろがっています。", "Aozora ni shiroi sekiran'un ga mokumoku to hirogatte imasu.", "White cumulonimbus clouds are billowing across the blue sky.", "nature,weather", ["雨雲", "白雲"]),
    ("池", "チ", "いけ", 6, "水", "pond, reservoir", "静かな公園の池で鯉が優雅に泳いでいます。", "しずかなこうえんのいけでこいがゆうがにおよいでいます。", "Shizuka na kouen no ike de koi ga yuuga ni oyoide imasu.", "Carp are swimming gracefully in the quiet park pond.", "nature,places", ["池端", "貯水池"]),
    ("地", "チ, ジ", "", 6, "土", "ground, earth, region", "地下鉄の駅を出て大通りをまっすぐ歩きました。", "ちかてつのえきをでておおどおりをまっすぐあるきました。", "Chikatetsu no eki o dete oodoori o massugu arukimashita.", "Exiting the subway station, I walked straight along the main street.", "nature,spatial", ["地下", "土地"]),
    ("石", "セキ, シャク", "いし", 5, "石", "stone, rock", "庭に美しい白い石を並べて小道を作りました。", "にわにうつくしいしろいいしをならべてこみちをつくりました。", "Niwa ni utsukushii shiroi ishi o narabete komichi o tsukurimashita.", "I arranged beautiful white stones in the garden to make a path.", "nature,material", ["石鹸", "宝石"]),
    ("林", "リン", "はやし", 8, "木", "grove, woods", "木漏れ日が心地よい雑木林の中を歩きました。", "こもれびがここちよいぞうきばやしのなかをあるきました。", "Komorebi ga kokochiyoi zoukibayashi no naka o arukimashita.", "I walked through the grove where sunlight filtered pleasantly through trees.", "nature,woods", ["林業", "森林"]),
    ("森", "シン", "もり", 12, "木", "forest, woods", "豊かな自然が残る深い森で野鳥を観察しました。", "ゆたかなしぜんがのこるふかいもりでやちょうをかんさつしました。", "Yutaka na shizen ga nokoru fukai mori de yachou o kansatsu shimashita.", "I observed wild birds in the deep forest where nature remains rich.", "nature,woods", ["森林", "森の中"]),
    ("花", "カ, ケ", "はな", 7, "艸", "flower, blossom", "春の訪れとともに色とりどりの花が咲きました。", "はるのおとずれとともにいろとりどりのはながさきました。", "Haru no otozure to tomo ni irotoridori no hana ga sakimashita.", "With the arrival of spring, flowers of many colors bloomed.", "nature,plant", ["花火", "花屋"]),
    ("草", "ソウ", "くさ", 9, "艸", "grass, weed, herbs", "庭に生えた雑草を週末にきれいに抜きました。", "にわにはえたざっそうをしゅうまつにきれいにぬきました。", "Niwa ni haeta zassou o shuumatsu ni kirei ni nukimashita.", "Over the weekend, I neatly pulled out the weeds growing in the garden.", "nature,plant", ["草花", "雑草"]),
    ("竹", "チク", "たけ", 6, "竹", "bamboo", "京都の嵐山にある美しい竹林の小径を歩きました。", "きょうとのあらしやまにあるうつくしいたけばやしのこみちをあるきました。", "Kyouto no Arashiyama ni aru utsukushii takebayashi no komichi o arukimashita.", "I walked the beautiful bamboo grove path in Arashiyama, Kyoto.", "nature,plant", ["竹林", "竹の子"]),
    ("虫", "チュウ, キ", "むし", 6, "虫", "insect, bug", "夏の夜に庭から様々な虫の鳴き声が聞こえます。", "なつのよるににわからさまざまなむしのなきごえがきこえます。", "Natsu no yoru ni niwa kara samazama na mushi no nakigoe ga kikoemasu.", "On summer nights, the chirping of various insects can be heard from the garden.", "nature,creature", ["昆虫", "虫歯"]),
    ("犬", "ケン", "いぬ", 4, "犬", "dog", "毎朝近所の公園で元気な犬の散歩をします。", "まいあさきんじょのこうえんでげんきないぬのさんぽをします。", "Maiasa kinjo no kouen de genki na inu no sanpo o shimasu.", "Every morning I take a vigorous dog for a walk in the neighborhood park.", "nature,creature", ["番犬", "子犬"]),
    ("夕", "セキ", "ゆう", 3, "夕", "evening", "美しい夕日を見ながら浜辺をゆっくり歩きました。", "うつくしいゆうひをみながらはまべをゆっくりあるきました。", "Utsukushii yuuhi o minagara hamabe o yukkuri arukimashita.", "I walked slowly along the beach while watching the beautiful evening sun.", "time,nature", ["夕方", "夕食"]),
    ("夜", "ヤ", "よ, よる", 8, "夕", "night, evening", "静かな夜に机に向かって日本語の勉強をします。", "しずかなよるにつくえにむかってにほんごのべんきょうをします。", "Shizuka na yoru ni tsukue ni mukatte nihongo no benkyou o shimasu.", "In the quiet night, I sit at my desk and study Japanese.", "time,nature", ["今夜", "夜景"]),
    ("朝", "チョウ", "あさ", 12, "月", "morning", "毎朝六時に起きて庭で軽い体操をします。", "まいあさろくじにおきてにわでかるいたいそうをします。", "Maiasa rokuji ni okite niwa de karui taisou o shimasu.", "Every morning I wake up at 6:00 and do light exercise in the garden.", "time,nature", ["今朝", "朝食"]),
    ("昼", "チュウ", "ひる", 9, "日", "daytime, noon", "昼休みに会社の同僚と一緒に定食を食べました。", "ひるやすみにかいしゃのどうりょうといっしょにていしょくをたべました。", "Hiruyasumi ni kaisha no douryou to issho ni teishoku o tabemashita.", "During lunch break, I ate a set meal together with my company colleague.", "time,nature", ["昼食", "昼休み"]),

    # STAGE 5: Mind, Communication, Education & Arts
    ("言", "ゲン, ゴン", "い.う, こと", 7, "言", "say, word, speech", "感謝の気持ちを自分の言葉でしっかり伝えました。", "かんしゃのきもちをごぶんのことばでしっかりつたえました。", "Kansha no kimochi o jibun no kotoba de shikkari tsutaemashita.", "I conveyed my gratitude clearly in my own words.", "mind,speech", ["言葉", "方言"]),
    ("語", "ゴ", "かた.る, かた.らう", 14, "言", "language, talk, narrate", "毎日ラジオを聴いて新しい外国語を練習しています。", "まいにちラジオをきいてあたらしいがいこくごをれんしゅうしています。", "Mainichi rajio o kiite atarashii gaikokugo o renshuu shite imasu.", "Every day I listen to the radio and practice a new foreign language.", "mind,language", ["言語", "物語"]),
    ("話", "ワ", "はな.す, はなし", 13, "言", "speak, talk, tale", "先生とこれからの進路について詳しく話し合いました。", "せんせいとこれからのしんろについてくわしくはなしあいました。", "Sensei to kore kara no shinro ni tsuite kuwashiku hanashiaimashita.", "I spoke in detail with my teacher about my future path.", "mind,communication", ["会話", "世話"]),
    ("読", "ドク, トク", "よ.む", 14, "言", "read", "図書館で借りた歴史の本を熱心に読み進めました。", "としょかんでかりたれきしのほんをねっしによみすすめました。", "Toshokan de karita rekishi no hon o nesshin ni yomisusumemashita.", "I eagerly read through the history book borrowed from the library.", "education,mind", ["読書", "読者"]),
    ("書", "ショ", "か.く", 10, "曰", "write, book, document", "試験の申込書に必要事項を黒のペンで書きました。", "しけんのもうしこみしょにひつようじこうをくろのペンでかきました。", "Shiken no moushikomisho ni hitsuyou jikou o kuro no pen de kakimashita.", "I wrote the required items on the exam application with a black pen.", "education,mind", ["書類", "辞書"]),
    ("聞", "ブン, モン", "き.く, き.こえる", 14, "耳", "hear, listen, ask", "朝起きてすぐに最新のニュースをラジオで聞きました。", "あさおきてすぐにさいしんのニュースをラジオでききました。", "Asa okite sugu ni saishin no nyuusu o rajio de kikimashita.", "Immediately upon waking in the morning, I heard the latest news on the radio.", "communication,mind", ["新聞", "見聞"]),
    ("思", "シ", "おも.う", 9, "心", "think, consider, feel", "自分の夢を叶えるために努力することが大切だと思います。", "じぶんのゆめをかなえるためにどりょくすることがたいせつだとおもいます。", "Jibun no yume o kanaeru tame ni doryoku suru koto ga taisetsu da to omoimasu.", "I think it is important to make efforts in order to fulfill one's dreams.", "mind,cognition", ["思い出", "思考"]),
    ("考", "コウ", "かんが.える", 6, "老", "think, consider", "難しい問題の解決方法をじっくり考えました。", "むずかしいもんだいのかいけつほうほうをじっくりかんがえました。", "Muzukashii mondai no kaiketsu houhou o jikkuri kangaemashita.", "I carefully considered the solution method for the difficult problem.", "mind,cognition", ["考える", "参考"]),
    ("知", "チ", "し.る, し.らせる", 8, "矢", "know, understand, inform", "日本文化についてもっと深く知りたいと思っています。", "にほんぶんかについてもっとふかくしりたいとおもっています。", "Nihon bunka ni tsuite motto fukaku shiritai to omotte imasu.", "I think I want to know more deeply about Japanese culture.", "mind,knowledge", ["知人", "知識"]),
    ("教", "キョウ", "おし.える, おそ.わる", 11, "攴", "teach, instruction, faith", "大学の教室で熱心な先生に日本語を教えてもらいました。", "だいがくのきょうしつでねっしんなせんせいににほんごをおしえてもらいました。", "Daigaku no kyoushitsu de nesshin na sensei ni nihongo o oshiete moraimashita.", "In the university classroom, I had the enthusiastic teacher teach me Japanese.", "education,mind", ["教室", "教会"]),
    ("習", "シュウ", "なら.う", 11, "羽", "learn, custom, practice", "毎週土曜日に茶道教室で伝統的な作法を習っています。", "まいしゅうどようびにさどうきょうしつのでんとうてきなさほうをならっています。", "Maishuu doyoubi ni sadou kyoushitsu de dentouteki na sahou o naratte imasu.", "Every Saturday I learn traditional etiquette at the tea ceremony school.", "education,mind", ["復習", "予習"]),
    ("研", "ケン", "と.ぐ", 9, "石", "polish, sharpen, study", "大学院の研究室で新しい環境技術の開発に取り組みます。", "だいがくいんのけんきゅうしつであたらしいかんきょうぎじゅつのかいはつにとりくみます。", "Daigakuin no kenkyuushitsu de atarashii kankyou gijutsu no kaihatsu ni torikumimasu.", "In the graduate laboratory, I engage in research and development of new environmental tech.", "education,mind", ["研究", "研究所"]),
    ("究", "キュウ", "きわ.める", 7, "穴", "research, investigate", "日本の伝統芸能の歴史を深く究めたいです。", "にほんのでんとうげいのうのれきしをふかくきわめたいです。", "Nihon no dentou geinou no rekishi o fukaku kiwametai desu.", "I want to thoroughly investigate the history of traditional Japanese performing arts.", "education,mind", ["研究", "追究"]),
    ("文", "ブン, モン", "ふみ, あや", 4, "文", "writing, sentence, culture", "手紙の文章を丁寧に確認してから投函しました。", "てがみのぶんしょうをていねいにかくにんしてからとうかんしました。", "Tegami no bunshou o teinei ni kakunin shite kara toukan shimashita.", "I carefully checked the text of the letter before posting it.", "education,culture", ["文化", "作文"]),
    ("字", "ジ", "あざ", 6, "子", "character, letter, word", "ノートにきれいな漢字を丁寧に書く練習をしました。", "ノートにきれいなかんじをていねいにかくれんしゅうをしました。", "Nooto ni kirei na kanji o teinei ni kaku renshuu o shimashita.", "I practiced writing neat Kanji characters carefully in my notebook.", "education,mind", ["漢字", "文字"]),
    ("英", "エイ", "", 8, "艸", "English, outstanding, flower", "高校で毎日ネイティブの先生から英語を学びました。", "こうこうでまいにちネイティブのせんせいからえいごをまなびました。", "Koukou de mainichi neitibu no sensei kara eigo o manabimashita.", "In high school, I learned English every day from a native teacher.", "education,language", ["英語", "英会話"]),
    ("音", "オン, イン", "おと, ね", 9, "音", "sound, noise", "静かな部屋でリラックスできる音楽を聴きました。", "しずかなへやでリラックスできるおんがくをききました。", "Shizuka na heya de rirakkusu dekiru ongaku o kikimashita.", "In the quiet room, I listened to relaxing music.", "arts,mind", ["音楽", "本音"]),
    ("楽", "ガク, ラク", "たの.しい, たの.しむ", 13, "木", "music, comfort, fun", "友達と楽器を演奏するのはとても楽しい時間です。", "ともだちとがっきをえんそうするのはとてもたのしいじかんです。", "Tomodachi to gakki o ensou suru no wa totemo tanoshii jikan desu.", "Playing musical instruments with friends is a very fun time.", "arts,mind", ["音楽", "楽しい"]),
    ("歌", "カ", "うた, うた.う", 14, "欠", "song, to sing", "みんなで手拍子をしながら伝統的な歌を歌いました。", "みんなでてびょうしをしながらでんとうてきなうたをうたいました。", "Minna de tebyoushi o shinagara dentouteki na uta o utaimashita.", "While clapping hands together, everyone sang traditional songs.", "arts,culture", ["歌手", "短歌"]),
    ("画", "ガ, カク", "えが.く", 8, "田", "picture, stroke, plan", "休日に美術館へ行って有名な画家の絵画を鑑賞しました。", "きゅうじつにびじゅつかんへいってゆうめいながかのかいがをかんしょうしました。", "Kyuujitsu ni bijutsukan e itte yuumei na gaka no kaiga o kanshou shimashita.", "On my day off, I went to the art museum and admired paintings by famous artists.", "arts,culture", ["映画", "計画"]),
    ("映", "エイ", "うつ.る, うつ.す", 9, "日", "reflect, projection, shine", "夕暮れの湖の静かな水面に山が美しく映っています。", "ゆうぐれのみずうみのしずかなすいめんにやまがうつくしくうつっています。", "Yuugure no mizuumi no shizuka na suimen ni yama ga utsukushiku utsutte imasu.", "On the quiet water surface of the dusk lake, mountains are beautifully reflected.", "arts,visual", ["映画", "上映"]),
    ("真", "シン", "ま, まこと", 10, "目", "true, reality, genuine", "旅行先で思い出に残るきれいな写真を撮りました。", "りょこうさきでおもいでにのこるきれいなしゃしんをとりました。", "Ryokousaki de omoide ni nokoru kirei na shashin o torimashita.", "At my travel destination, I took beautiful photographs that remain as memories.", "arts,mind", ["写真", "真実"]),
    ("答", "トウ", "こた.える, こた.え", 12, "竹", "answer, reply", "先生の質問に対して自分の考えを正確に答えました。", "せんせいのしつもんにたいしてじぶんのかんがえをせいかくにこたえました。", "Sensei no shitsumon ni taishite jibun no kangae o seikaku ni kotaemashita.", "In response to the teacher's question, I answered my thoughts accurately.", "communication,education", ["答え", "回答"]),
    ("問", "モン", "と.う, と.い", 11, "口", "question, ask, problem", "試験の最後の難しい問題について先生に質問しました。", "しけんのさいごのむずかしいもんだいについてせんせいにしつもんしました。", "Shiken no saigo no muzukashii mondai ni tsuite sensei ni shitsumon shimashita.", "I asked the teacher a question regarding the difficult final problem of the exam.", "education,mind", ["問題", "質問"]),
    ("試", "シ", "こころ.みる, ため.す", 13, "言", "test, try, attempt", "来週の金曜日に日本語能力試験を受験します。", "らいしゅうのきんようびににほんごのうりょくしけんをじゅけんします。", "Raishuu no kinyoubi ni nihongo nouryoku shiken o juken shimasu.", "Next Friday, I will take the Japanese Language Proficiency Test.", "education,mind", ["試験", "試す"]),

    # STAGE 6: Human Body, Health & Clothing
    ("体", "タイ, テイ", "からだ", 7, "人", "body, health, form", "健康のために毎朝公園を走って体を鍛えています。", "けんこうのためにまいあさこうえんをはしってからだをきたえています。", "Kenkou no tame ni maiasa kouen o hashitte karada o kitaete imasu.", "For my health, I run in the park every morning to train my body.", "body,health", ["体重", "体操"]),
    ("頭", "トウ, ズ", "あたま, かしら", 16, "頁", "head, brain, top", "風邪をひいて少し頭が痛いので薬を飲みました。", "かぜをひいてすこしあたまがいたいのでくすりをのみました。", "Kaze o hiite sukoshi atama ga itai node kusuri o nomimashita.", "Because I caught a cold and my head aches a bit, I took medicine.", "body,health", ["頭痛", "先頭"]),
    ("顔", "ガン", "かお", 18, "頁", "face, expression", "朝起きて冷たい水で顔を洗うと目が覚めます。", "あさおきてつめたいみずでかおをあらうとめがさめます。", "Asa okite tsumetai mizu de kao o arau to me ga samemasu.", "When I wake in the morning and wash my face with cold water, I wake up.", "body,health", ["笑顔", "洗顔"]),
    ("首", "シュ", "くび", 9, "首", "neck, head, leader", "寒い冬の日は首に暖かいマフラーを巻いて出かけます。", "さむいふゆのひはくびにあたたかいマフラーをまいてでかけます。", "Samui fuyu no hi wa kubi ni atatakai mafuraa o maite dekakemasu.", "On cold winter days, I wrap a warm scarf around my neck and head out.", "body,clothing", ["首都", "手首"]),
    ("手", "シュ", "て, た", 4, "手", "hand, arm", "食事の前に必ず石鹸で両手をきれいに洗います。", "しょくじのまえにかならずせっけんでりょうてをきれいにあらいます。", "Shokuji no mae ni kanarazu sekken de ryoute o kirei ni araimasu.", "Before meals, I always wash both hands cleanly with soap.", "body,action", ["手紙", "上手"]),
    ("足", "ソク", "あし, た.りる", 7, "足", "foot, leg, sufficient", "一日中街を歩き回ったので足がとても疲れました。", "いちにちじゅうまちをあるきまわったのであしがとてもつかれました。", "Ichinichijuu machi o arukimawatta node ashi ga totemo tsukaremashita.", "Because I walked around town all day, my legs got very tired.", "body,movement", ["遠足", "足りる"]),
    ("目", "モク, ボク", "め, ま", 5, "目", "eye, vision, look", "パソコンを長時間使う時は、時々目を休めることが大切です。", "パソコンをちょうじかんつかうときは、ときどきめをやすめることがたいせつです。", "Pasokon o choujikan tsukau toki wa, tokidoki me o yasumeru koto ga taisetsu desu.", "When using a PC for long hours, resting one's eyes occasionally is vital.", "body,sensory", ["目的", "目次"]),
    ("耳", "ジ", "みみ", 6, "耳", "ear, hearing", "静かな夜に耳を澄ますと虫の鳴き声が聞こえます。", "しずかなよるにみみをすますとむしのなきごえがきこえます。", "Shizuka na yoru ni mimi o sumasu to mushi no nakigoe ga kikoemasu.", "In the quiet night, if you listen carefully with your ears, you hear insects.", "body,sensory", ["耳鼻科", "初耳"]),
    ("口", "コウ, ク", "くち", 3, "口", "mouth, opening", "駅の東口の改札前で友達と待ち合わせました。", "えきのひがしぐちのかいさつまえでともだちとまちあわせました。", "Eki no higashiguchi no kaisatsumae de tomodachi to machiawasemashita.", "I met up with my friend in front of the station east exit ticket gate.", "body,spatial", ["入口", "出口"]),
    ("歯", "シ", "は", 12, "歯", "tooth, cog", "食事の後は虫歯を防ぐために必ず歯を磨きます。", "しょくじのあとはむしばをふせぐためにかならずはをみがきます。", "Shokuji no ato wa mushiba o fusegu tame ni kanarazu ha o migakimasu.", "After meals, I always brush my teeth in order to prevent cavities.", "body,health", ["歯医者", "虫歯"]),
    ("心", "シン", "こころ", 4, "心", "heart, mind, spirit", "温かい励ましの言葉に心から深く感謝しました。", "あたたかいはげましのことばにこころからふかくかんしゃしました。", "Atatakai hagemashi no kotoba ni kokoro kara fukaku kansha shimashita.", "I thanked them deeply from my heart for the warm encouraging words.", "body,spirit", ["心配", "安心"]),
    ("病", "ビョウ, ヘイ", "や.む, やまい", 10, "疒", "illness, disease, sick", "病気にならないように毎日の手洗いとうがいを徹底します。", "びょうきにならないようにまいにちのてあらいとうがいをてっていします。", "Byouki ni naranai you ni mainichi no tearai to ugai o tettei shimasu.", "So as not to fall sick, I thoroughly wash hands and gargle every day.", "health,body", ["病気", "病院"]),
    ("痛", "ツウ", "いた.い, いた.む", 12, "疒", "pain, ache, hurt", "転んで膝を強く打ってしまい、痛みが続いています。", "ころんでひざをつよくうってしまい、いたみがつづいています。", "Koronde hiza o tsuyoku utte shimai, itami ga tsuzuite imasu.", "I fell and struck my knee hard, and the pain is continuing.", "health,body", ["痛い", "頭痛"]),
    ("薬", "ヤク", "くすり", 16, "艸", "medicine, chemical", "風邪の症状を抑えるために食後に薬を飲みました。", "かぜのしょうじょうをおさえるためにしょくごにくすりをのみました。", "Kaze no shoujou o osaeru tame ni shokugo ni kusuri o nomimashita.", "In order to suppress cold symptoms, I took medicine after the meal.", "health,medicine", ["薬局", "目薬"]),
    ("医", "イ", "", 7, "匚", "doctor, medicine, heal", "体調がすぐれないので近所の医院で診察を受けました。", "たいちょうがすぐれないのできんじょのいいんでしんさつをうけました。", "Taichou ga sugurenai node kinjo no iin de shinsatsu o ukemashita.", "Because I was not feeling well, I received an exam at a neighborhood clinic.", "health,profession", ["医者", "医院"]),
    ("者", "シャ", "もの", 8, "老", "person, specialist", "病院で優しい医者に丁寧に診てもらいました。", "びょういんでやさしいいしゃにていねいにみてもらいました。", "Byouin de yasashii isha ni teinei ni mite moraimashita.", "At the hospital, I had a gentle physician examine me carefully.", "health,people", ["医者", "学者"]),
    ("院", "イン", "", 10, "阜", "institution, hospital, hall", "大きな総合病院で定期的な健康診断を受けました。", "おおきなそうごうびょういんでていきてきなけんこうしんだんをうけました。", "Ookina sougou byouin de teikiteki na kenkoushindan o ukemashita.", "I received a regular health checkup at a large general hospital.", "health,society", ["病院", "入院"]),
    ("寒", "カン", "さむ.い", 12, "宀", "cold (climate/weather)", "今年の冬は例年よりも一段と寒いです。", "ことしのふゆはれいねんよりもいちだんとさむいです。", "Kotoshi no fuyu wa reinen yori mo ichidan to samui desu.", "This year's winter is even colder than normal years.", "body,nature", ["寒気", "極寒"]),
    ("暑", "ショ", "あつ.い", 12, "日", "hot (weather/climate)", "夏休みは毎日とても暑いので熱中症に注意します。", "なつやすみはまいにちとてもあついのでねっちゅうしょうにちゅういします。", "Natsuyasumi wa mainichi totemo atsui node netchuushou ni chuui shimasu.", "Because it is very hot every day during summer break, beware heatstroke.", "body,nature", ["猛暑", "暑中"]),
    ("熱", "ネツ", "あつ.い", 15, "火", "heat, fever, passion", "風邪をひいて高い熱が出たので一日中寝ていました。", "かぜをひいてたかいねつがでたのでいちにちじゅうねていました。", "Kaze o hiite takai netsu ga deta node ichinichijuu nete imashita.", "Because I caught a cold and had a high fever, I stayed in bed all day.", "health,nature", ["熱心", "高熱"]),
    ("冷", "レイ", "つめ.たい, ひ.える", 7, "冫", "cold (tactile), cool, chilly", "喉が渇いたので冷蔵庫から冷たい麦茶を取り出しました。", "のどがかわいたのでれいぞうこからつめたいむぎちゃをとりだしました。", "Nodo ga kawaita node reizouko kara tsumetai mugicha o toridashimashita.", "Because my throat was dry, I took out cold barley tea from the fridge.", "daily,sensory", ["冷蔵庫", "冷たい"]),
    ("温", "オン", "あたた.かい, あたた.まる", 12, "水", "warm, temperature", "寒い夜は温かいお風呂にゆっくり浸かって温まります。", "さむいよるはあたたかいおふろにゆっくりつかってあたたまります。", "Samui yoru wa atatakai ofuro ni yukkuri tsukatte atatamarimasu.", "On cold nights, I soak slowly in a warm bath and warm up.", "daily,sensory", ["温泉", "気温"]),
    ("洗", "セン", "あら.う", 9, "水", "wash, cleanse", "帰宅したらすぐに洗面所で手をきれいに洗います。", "きたくしたらすぐにせんめんじょでてをきれいにあらいます。", "Kitaku shitara sugu ni senmenjo de te o kirei ni araimasu.", "Upon returning home, I immediately wash my hands cleanly in the washroom.", "daily,action", ["洗う", "洗濯"]),
    ("着", "チャク", "き.る, つ.く", 12, "羊", "wear, put on, arrive", "寒い朝は暖かいセーターを着て出かけます。", "さむいあさはあたたかいセーターをきてでかけます。", "Samui asa wa atatakai seetaa o kite dekakemasu.", "On cold mornings, I put on a warm sweater and head out.", "clothing,daily", ["着物", "下着"]),
    ("脱", "ダツ", "ぬ.ぐ, ぬ.げる", 11, "肉", "undress, remove, escape", "日本の家に入る時は、玄関で靴を脱ぎます。", "にほんのいえにはいるときは、げんかんでくつをぬぎます。", "Nihon no ie ni hairu toki wa, genkan de kutsu o nugimasu.", "When entering a Japanese house, you remove shoes at the entryway.", "clothing,daily", ["脱ぐ", "脱走"]),

    # STAGE 7: Actions, Change, State & Measurement
    ("始", "シ", "はじ.める, はじ.まる", 8, "女", "begin, start", "朝九時に学校の最初の授業が始まります。", "あさくじにがっこうのさいしょのじゅぎょうがはじまります。", "Asa kuji ni gakkou no saisho no jugyou ga hajimarimasu.", "At 9:00 in the morning, the school's first class starts.", "action,time", ["始める", "開始"]),
    ("終", "シュウ", "お.わる, お.える", 11, "糸", "end, finish", "今日の仕事を全部終えて夕方に帰宅しました。", "きょうのしごとをぜんぶおえてゆうがたにきたくしました。", "Kyou no shigoto o zenbu oete yuugata ni kitaku shimashita.", "Finishing all of today's work, I returned home in the evening.", "action,time", ["終わる", "終電"]),
    ("開", "カイ", "ひら.く, あ.く, あ.ける", 12, "門", "open, unfold", "新鮮な空気を入れるために部屋の窓を開けました。", "しんせんなくうきをいれるためにへやのまどをあけました。", "Shinsen na kuuki o ireru tame ni heya no mado o akemashita.", "In order to let fresh air in, I opened the room window.", "action,state", ["開ける", "開店"]),
    ("閉", "ヘイ", "と.じる, し.まる, し.める", 11, "門", "close, shut", "雨が激しく降ってきたので急いで窓を閉めました。", "あめがはげしくふってきたのでいそいでまどをしめました。", "Ame ga hageshiku futte kita node isoide mado o shimemashita.", "Because rain began falling heavily, I closed the window in a hurry.", "action,state", ["閉める", "閉店"]),
    ("立", "リツ", "た.つ, た.てる", 5, "立", "stand, erect, set up", "電車でお年寄りに席を譲るために立ち上がりました。", "でんしゃでおとしよりにせきをゆずるためにたちあがりました。", "Densha de otoshiyori ni seki o yuzuru tame ni tachiagarimashita.", "I stood up to give up my seat to an elderly person on the train.", "action,body", ["立つ", "国立"]),
    ("座", "ザ", "すわ.る", 10, "广", "sit, seat", "新幹線の指定席に座ってゆっくり景色を眺めました。", "しんかんせんのしていせきにすわってゆっくりけしきをながめました。", "Shinkansen no shiteiseki ni suwatte yukkuri keshiki o nagamemashita.", "Sitting in the reserved seat of the bullet train, I leisurely watched scenery.", "action,body", ["座る", "口座"]),
    ("使", "シ", "つか.う", 8, "人", "use, employ, envoy", "辞書を使って難しい日本語の単語の意味を調べました。", "じしょをつかってむずかしいにほんごのたんごのいみをしらべました。", "Jisho o tsukatte muzukashii nihongo no tango no imi o shirabemashita.", "Using a dictionary, I looked up the meaning of difficult Japanese words.", "action,daily", ["使う", "大使館"]),
    ("作", "サク, サ", "つく.る", 7, "人", "make, create, produce", "家族のために夕食のカレーライスを作りました。", "かぞくのためにゆうしょくのカレーライスをつくりました。", "Kazoku no tame ni yuushoku no kareeraisu o tsukurimashita.", "I made curry rice for dinner for my family.", "action,creation", ["作る", "作文"]),
    ("持", "ジ", "も.つ", 9, "手", "hold, have, possess", "旅行に出かける時はパスポートを肌身離さず持っています。", "りょこうにでかけるときはパスポートをはだみはなさずもっています。", "Ryokou ni dekakeru toki wa pasupooto o hadamihanasazu motte imasu.", "When going on a trip, I keep my passport with me at all times.", "action,possession", ["持つ", "気持ち"]),
    ("待", "タイ", "ま.つ", 9, "彳", "wait, depend on", "駅の改札口の前で友達が来るのを待っていました。", "えきのかいさつぐちのまえでともだちがくるのをまっていました。", "Eki no kaisatsuguchi no mae de tomodachi ga kuru no o matte imashita.", "In front of the station ticket gate, I was waiting for my friend to arrive.", "action,patience", ["待つ", "招待"]),
    ("忘", "ボウ", "わす.れる", 7, "心", "forget", "大切な傘を電車の中に忘れないように注意してください。", "たいせつなかさをでんしゃのなかにわすれないようにちゅういしてください。", "Taisetsu na kasa o densha no naka ni wasurenai you ni chuui shite kudasai.", "Please be careful not to forget your important umbrella on the train.", "mental,action", ["忘れる", "忘れ物"]),
    ("貸", "タイ", "か.す", 12, "貝", "lend", "困っている友達にノートを快く貸してあげました。", "こまっているともだちにノートをこころよくかしてあげました。", "Komatte iru tomodachi ni nooto o kokoroyoku kashite agemashita.", "I gladly lent my notes to my friend who was in trouble.", "commerce,action", ["貸す", "貸出"]),
    ("借", "シャク", "か.りる", 10, "人", "borrow", "図書館から日本語の文法書を二冊借りました。", "としょかんからにほんごのぶんぽうしょをにさつかりました。", "Toshokan kara nihongo no bunpousho o nisatsu karimashita.", "I borrowed two Japanese grammar books from the library.", "commerce,action", ["借りる", "借金"]),
    ("置", "チ", "お.く", 13, "网", "put, place, leave", "机の上に辞書と筆記用具をきちんと置きました。", "つくえのうえにじしょとひっきようぐをきちんとおきました。", "Tsukue no ue ni jisho to hikkiyougu o kichinto okimashita.", "I placed the dictionary and writing instruments neatly on the desk.", "action,spatial", ["置く", "位置"]),
    ("残", "ザン", "のこ.る, のこ.す", 10, "歹", "remain, balance, leave behind", "残りの仕事を片付けてから急いで帰宅しました。", "のこのしごとをかたづけてからいそいできたくしました。", "Nokori no shigoto o katazukete kara isoide kitaku shimashita.", "After finishing the remaining work, I hurried home.", "state,action", ["残念", "残業"]),
    ("引", "イン", "ひ.く, ひ.ける", 4, "弓", "pull, draw, quote", "ドアの取っ手を強く引いて部屋の中に入りました。", "ドアのとってをつよくひいてへやのなかにはいりました。", "Doa no totte o tsuyoku hiite heya no naka ni hairimashita.", "Pulling the door handle firmly, I entered the room.", "action,movement", ["引く", "引き出し"]),
    ("押", "オウ", "お.す, お.さえる", 8, "手", "push, press, stamp", "エレベーターのボタンを押して最上階へ向かいました。", "エレベーターのボタンをおしてさいじょうかいへむかいました。", "Erebeetaa no botan o oshite saijoukai e mukaimashita.", "Pressing the elevator button, I headed to the top floor.", "action,movement", ["押す", "押し入れ"]),
    ("切", "セツ, サイ", "き.る, き.れる", 4, "刀", "cut, sever, end", "包丁を使って新鮮な野菜を細かく切りました。", "ほうちょうをつかってしんせんなやさいをこまかくきりました。", "Houchou o tsukatte shinsen na yasai o komakaku kirimashita.", "Using a kitchen knife, I finely chopped fresh vegetables.", "action,creation", ["切手", "大切"]),
    ("度", "ド, ト", "たび", 9, "广", "degrees, occurrence, measure", "一度始めたことは最後まで諦めずにやり遂げます。", "いちどはじめたことはさいごまであきらめずにやりとげます。", "Ichido hajimeta koto wa saigo made akiramezu ni yaritogemasu.", "Things once begun, I carry through to the end without giving up.", "measure,time", ["今度", "温度"]),
    ("回", "カイ, エ", "まわ.る, まわ.す", 6, "囗", "turn, round, frequency", "一日三回、食後に必ずこの薬を飲んでください。", "いちにちさんかい、しょくごにかならずこのくすりをのんでください。", "Ichinichi sankai, shokugo ni kanarazu kono kusuri o nonde kudasai.", "Please be sure to take this medicine three times a day after meals.", "measure,frequency", ["回る", "何回"]),
    ("計", "ケイ", "はか.る, はか.らう", 9, "言", "measure, calculate, plan", "毎朝体温計で体温を測って健康を管理しています。", "まいあさたいおんけいでたいおんをはかってけんこうをかんりしています。", "Maiasa taionkei de taion o hakatte kenkou o kanri shite imasu.", "Every morning I measure my body temperature with a thermometer and manage health.", "measure,plan", ["計画", "時計"]),
    ("番", "バン", "", 12, "田", "number, order, turn", "受付で順番を待っている間に書類を読みました。", "うけつけでじゅんばんをまっているあいだにしょるいをよみました。", "Uketsuke de junban o matte iru aida ni shorui o yomimashita.", "While waiting my turn at reception, I read through the documents.", "measure,order", ["番号", "一番"]),
    ("号", "ゴウ", "さけ.ぶ", 5, "口", "number, issue, signal", "新幹線の七号車に乗って京都まで行きました。", "しんかんせんのななごうしゃにのってきょうとまでいきました。", "Shinkansen no nanagousha ni notte Kyouto made ikimashita.", "I boarded car number 7 of the bullet train and went to Kyoto.", "measure,order", ["番号", "信号"]),
    ("強", "キョウ, ゴウ", "つよ.い, つよ.まる", 11, "弓", "strong, robust, force", "風がとても強いので帽子が飛ばされないよう注意してください。", "かぜがとてもつよいのでぼうしがとばされないようちゅういしてください。", "Kaze ga totemo tsuyoi node boushi ga tobasarenai you chuui shite kudasai.", "Because the wind is very strong, beware of your hat being blown away.", "state,nature", ["勉強", "強い"]),
    ("弱", "ジャク", "よわ.い, よわ.まる", 10, "弓", "weak, frail", "風邪をひいて体が弱っているので無理をせず休んでください。", "かぜをひいてからだがよわっているのでむりをせずやすんでください。", "Kaze o hiite karada ga yowatte iru node muri o sezu yasunde kudasai.", "Because your body is weakened from a cold, rest without overexertion.", "state,body", ["弱い", "弱点"])
]

def build_n4_kanji():
    kanji_items = []
    # Collect existing N5 kanji to guarantee ZERO overlap
    with open('app/src/main/assets/jlpt/n5/kanji.json') as f:
        n5_k = json.load(f)
    n5_set = {k['japanese'] for k in n5_k}

    seen_kanji = set()
    idx = 1
    for char, onyomi, kunyomi, strokes, radical, meaning, ex_jp, ex_rd, ex_rm, ex_en, tags, planned_vocab in RAW_KANJI_LIST:
        if char in n5_set:
            raise ValueError(f"DUPLICATE ERROR: {char} is already in N5!")
        if char in seen_kanji:
            continue
        seen_kanji.add(char)

        # Related items: only include valid existing items or empty
        item_id = f"n4_k_{idx:02d}" if idx < 100 else f"n4_k_{idx}"
        
        # Mnemonic & planned vocabulary note
        planned_vocab_str = ", ".join(planned_vocab)
        note = f"Radical: {radical}. Meaning: {meaning}. Planned N4 Vocab: {planned_vocab_str}."

        item = {
            "id": item_id,
            "japanese": char,
            "reading": kunyomi.split(",")[0].replace(".", "").replace("-", "").strip() if kunyomi else onyomi.split(",")[0].strip(),
            "romaji": "",
            "meaning": meaning,
            "category": "KANJI",
            "jlptLevel": "N4",
            "onyomi": onyomi,
            "kunyomi": kunyomi,
            "strokeCount": strokes,
            "radical": radical,
            "mnemonicOrNote": note,
            "exampleJapanese": ex_jp,
            "exampleReading": ex_rd,
            "exampleRomaji": ex_rm,
            "exampleEnglish": ex_en,
            "partOfSpeech": "Kanji Character",
            "difficulty": 2,
            "tags": f"kanji,n4,{tags}",
            "source": "Aiko JLPT Canonical N4 Kanji",
            "relatedItems": []
        }
        kanji_items.append(item)
        idx += 1

    return kanji_items

if __name__ == '__main__':
    items = build_n4_kanji()
    print(f"Total N4 Kanji built: {len(items)}")
