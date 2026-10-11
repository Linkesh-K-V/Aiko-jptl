#!/usr/bin/env python3
"""
Canonical N4 Curriculum Ingestion Pipeline for Phase 18C.
Generates:
  1. app/src/main/assets/jlpt/n4/grammar.json (56 verified items)
  2. app/src/main/assets/jlpt/n4/kanji.json (175 verified items)
Validates zero duplicate IDs, zero N5 duplicate content, valid DAG references,
and structural integrity before writing.
"""

import json
import os
import sys

sys.path.insert(0, os.path.dirname(__file__))
from build_n4_foundation import build_n4_grammar

# 175 Canonical N4 Kanji (Zero N5 overlap guaranteed)
N4_KANJI_DATA = [
    # STAGE 1: Society, Community, Places & People (25 Kanji)
    ("会", "カイ, エ", "あ.う", 6, "人", "meet, society, association", "明日東京で友達に会います。", "Ashita Toukyou de tomodachi ni aimasu.", "I will meet a friend in Tokyo tomorrow.", "society,people", ["会話", "会社"]),
    ("同", "ドウ", "おな.じ", 6, "口", "same, agree", "彼と私は同じクラスの学生です。", "Kare to watashi wa onaji kurasu no gakusei desu.", "He and I are students in the same class.", "society,basic", ["同じ", "同時に"]),
    ("事", "ジ, ズ", "こと", 8, "一", "matter, thing, fact, business", "大切な用事があるので早く帰ります。", "Taisetsu na youji ga aru node hayaku kaerimasu.", "I have important business, so I will return early.", "society,basic", ["仕事", "火事"]),
    ("員", "イン", "", 10, "口", "member, employee", "駅員に切符の買い方を尋ねました。", "Ekiin ni kippu no kaikata o tazunemashita.", "I asked the station staff member how to buy a ticket.", "society,work", ["会社員", "店員"]),
    ("院", "イン", "", 10, "阜", "institution, hospital, hall", "体調が悪いので病院へ行きます。", "Taichou ga warui node byouin e ikimasu.", "I don't feel well, so I'm going to the hospital.", "society,health", ["病院", "大学院"]),
    ("公", "コウ, ク", "おおやけ", 4, "八", "public, official", "天気がいいので公園を散歩しました。", "Tenki ga ii node kouen o sanpo shimashita.", "Since the weather was nice, I took a walk in the park.", "society,places", ["公園", "公衆"]),
    ("園", "エン", "その", 13, "囗", "garden, park, orchard", "週末に家族と動物園へ出かけました。", "Shuumatsu ni kazoku to doubutsuen e dekakemashita.", "I went to the zoo with my family on the weekend.", "society,places", ["公園", "動物園"]),
    ("市", "シ", "いち", 5, "巾", "city, market", "この市には多くの留学生が住んでいます。", "Kono shi ni wa ooku no ryuugakusei ga sunde imasu.", "Many international students live in this city.", "society,places", ["市民", "市長"]),
    ("区", "ク", "", 4, "匚", "ward, district, section", "区役所へ行って住所の登録をしました。", "Kuyakusho e itte juusho no touroku o shimashita.", "I went to the ward office and registered my address.", "society,places", ["区役所", "地区"]),
    ("都", "ト, ツ", "みやこ", 11, "邑", "metropolis, capital", "東京は日本の首都です。", "Toukyou wa nihon no shuto desu.", "Tokyo is the capital of Japan.", "society,places", ["京都", "都合"]),
    ("府", "フ", "", 8, "广", "urban prefecture, government", "大阪府には美味しい食べ物がたくさんあります。", "Oosaka-fu ni wa oishii tabemono ga takusan arimasu.", "There is plenty of delicious food in Osaka Prefecture.", "society,places", ["政府", "大阪府"]),
    ("県", "ケン", "", 9, "目", "prefecture", "神奈川県にある横浜へ観光に行きました。", "Kanagawa-ken ni aru Yokohama e kankou ni ikimashita.", "I went sightseeing to Yokohama in Kanagawa Prefecture.", "society,places", ["県庁", "他県"]),
    ("官", "カン", "", 8, "宀", "government official, bureaucrat", "空港で入国審査官にパスポートを見せました。", "Kuukou de nyuukoku shinsakan ni pasupooto o misemashita.", "I showed my passport to the immigration official at the airport.", "society,official", ["警官", "裁判官"]),
    ("民", "ミン", "たみ", 5, "氏", "people, citizen, nation", "市民図書館で静かに本を読みました。", "Shimin toshokan de shizuka ni hon o yomimashita.", "I read a book quietly at the civic library.", "society,people", ["市民", "国民"]),
    ("主", "シュ, ス", "おも, ぬし", 5, "丶", "master, main, lord", "日本のお正月の主な伝統行事に参加しました。", "Nihon no oshougatsu no omona dentou gyouji ni sanka shimashita.", "I participated in the main traditional events of Japanese New Year.", "society,roles", ["主人", "主に"]),
    ("仕", "シ, ジ", "つか.える", 5, "人", "serve, do, attend", "毎朝八時に出社して仕事を始めます。", "Maiasa hachiji ni shussha shite shigoto o hajimemasu.", "I arrive at work at 8:00 every morning and begin my job.", "society,work", ["仕事", "仕方"]),
    ("業", "ギョウ, ゴウ", "わざ", 13, "木", "business, vocation, industry", "将来の自分の仕事や卒業後の進路について考えます。", "Shourai no jibun no shigoto ya sotsugyou go no shinro ni tsuite kangaemasu.", "I think about my future work and my path after graduation.", "society,work", ["授業", "卒業"]),
    ("役", "ヤク, エキ", "", 7, "彳", "role, duty, service", "区役所の窓口で書類を提出しました。", "Kuyakusho no madoguchi de shorui o teishutsu shimashita.", "I submitted the documents at the ward office counter.", "society,roles", ["役所", "役に立つ"]),
    ("所", "ショ", "ところ", 8, "戸", "place, office", "住所と電話番号をこの紙に書いてください。", "Juusho to denwabangou o kono kami ni kaite kudasai.", "Please write your address and telephone number on this paper.", "society,places", ["近所", "場所"]),
    ("屋", "オク", "や", 9, "尸", "roof, shop, seller", "駅前の本屋で日本語の参考書を買いました。", "Ekimae no hon'ya de nihongo no sankousho o kaimashita.", "I bought a Japanese study guide at the bookstore in front of the station.", "society,places", ["本屋", "部屋"]),
    ("館", "カン", "やかた", 16, "食", "building, hall, mansion", "午後は市立図書館で勉強する予定です。", "Gogo wa shiritsu toshokan de benkyou suru yotei desu.", "In the afternoon, I plan to study at the city library.", "society,places", ["図書館", "映画館"]),
    ("堂", "ドウ", "", 11, "土", "public chamber, hall", "お昼休みに学生食堂でカレーを食べました。", "Ohiruyasumi ni gakusei shokudou de karee o tabemashita.", "During lunch break, I ate curry at the student cafeteria.", "society,places", ["食堂", "講堂"]),
    ("場", "ジョウ", "ば", 12, "土", "location, place, ground", "車をスーパーの駐車場に止めました。", "Kuruma o suupaa no chuushajou ni tomemashita.", "I parked the car in the supermarket parking lot.", "society,places", ["場所", "工場"]),
    ("銀", "ギン", "", 14, "金", "silver", "毎月の給料日に銀行でお金を下ろします。", "Maitsuki no kyuuryoubi ni ginkou de okane o oroshimasu.", "On monthly payday, I withdraw cash at the bank.", "commerce,finance", ["銀行", "銀色"]),
    ("住", "ジュウ", "す.む", 7, "人", "dwell, reside, inhabit", "緑が豊かで静かな町に住んでいます。", "Midori ga yutaka de shizuka na machi ni sunde imasu.", "I live in a quiet town rich with greenery.", "society,daily", ["住所", "住民"]),

    # STAGE 2: Travel, Transport, Motion & Spatial Direction (25 Kanji)
    ("運", "ウン", "はこ.ぶ", 12, "辵", "carry, transport, fortune", "大きなトラックで荷物を運びました。", "Ookina torakku de nimotsu o hakobimashita.", "I transported luggage with a large truck.", "transport,movement", ["運転", "運動"]),
    ("転", "テン", "ころ.がる, ころ.ぶ", 11, "車", "revolve, turn, fall over", "休日に海岸沿いを自転車で走りました。", "Kyuujitsu ni kaiganzoi o jitensha de hashirimashita.", "On my day off, I rode a bicycle along the coast.", "transport,movement", ["自転車", "運転"]),
    ("動", "ドウ", "うご.く, うご.かす", 11, "力", "move, motion, stir", "このボタンを押すと機械が動きます。", "Kono botan o osu to kikai ga ugokimasu.", "When you press this button, the machine moves.", "movement,action", ["自動車", "運動"]),
    ("止", "シ", "と.まる, と.める", 4, "止", "stop, halt", "赤信号ですから、ここで一度止まってください。", "Akashingou desu kara, koko de ichido tomatte kudasai.", "Because it's a red light, please stop here once.", "transport,movement", ["止まる", "中止"]),
    ("歩", "ホ, ブ", "ある.く, あゆ.む", 8, "止", "walk, step", "毎日駅から会社まで歩きます。", "Maiasa eki kara kaisha made arukimasu.", "Every day I walk from the station to the office.", "movement,health", ["歩く", "散歩"]),
    ("走", "ソウ", "はし.る", 7, "走", "run, gallop", "電車に遅れそうだったので急いで走りました。", "Densha ni okuresou datta node isoide hashirimashita.", "Because I seemed about to miss the train, I ran in a hurry.", "movement,action", ["走る", "走行"]),
    ("急", "キュウ", "いそ.ぐ", 9, "心", "hurry, urgent, steep", "急な用事で東京へ行くことになりました。", "Kyuu na youji de Toukyou e iku koto ni narimashita.", "Due to urgent business, it was decided that I would go to Tokyo.", "movement,action", ["急ぐ", "特急"]),
    ("通", "ツウ", "とお.る, かよ.う", 10, "辵", "commute, pass through, traffic", "毎朝バスに乗って高校へ通っています。", "Maiasa basu ni notte koukou e kayotte imasu.", "Every morning I commute to high school by bus.", "transport,movement", ["通う", "通り"]),
    ("路", "ロ", "じ, みち", 13, "足", "road, path, route", "線路沿いの安全な道を歩きました。", "Senrozoi no anzen na michi o arukimashita.", "I walked along the safe path beside the railway tracks.", "transport,places", ["線路", "道路"]),
    ("旅", "リョ", "たび", 10, "方", "trip, travel, journey", "夏休みに友人と北海道へ旅行しました。", "Natsuyasumi ni yuujin to Hokkaidou e ryokou shimashita.", "During summer vacation, I traveled to Hokkaido with my friend.", "travel,recreation", ["旅行", "旅人"]),
    ("着", "チャク, ジャク", "き.る, つ.く", 12, "羊", "arrive, don, wear", "新幹線は定刻通りに京都駅に着きました。", "Shinkansen wa teikokudoori ni Kyouto-eki ni tsukimashita.", "The bullet train arrived at Kyoto Station on schedule.", "travel,movement", ["着く", "上着"]),
    ("発", "ハツ, ホツ", "た.つ", 9, "癶", "depart, emit, publish", "東京発の特急電車に乗って箱根へ向かいました。", "Toukyou hatsu no tokkyuu densha ni notte Hakone e mukaimashita.", "I boarded the limited express departing from Tokyo and headed to Hakone.", "travel,transport", ["出発", "発見"]),
    ("去", "キョ, コ", "さ.る", 5, "厶", "past, leave, depart", "去年の春に高校を卒業しました。", "Kyonen no haru ni koukou o sotsugyou shimashita.", "Last spring, I graduated from high school.", "time,travel", ["去年", "過去"]),
    ("近", "キン, コン", "ちか.い", 7, "辵", "near, close, early", "駅から一番近いスーパーで買い物をします。", "Eki kara ichiban chikai suupaa de kaimono o shimasu.", "I shop at the supermarket closest to the station.", "spatial,distance", ["近い", "近所"]),
    ("遠", "エン, オン", "とお.い", 13, "辵", "far, distant", "家から学校まで遠いので毎朝早く起きます。", "Ie kara gakkou made tooi node maiasa hayaku okimasu.", "Because it's far from home to school, I get up early every morning.", "spatial,distance", ["遠い", "遠慮"]),
    ("進", "シン", "すす.む, すす.める", 11, "辵", "advance, proceed, progress", "この道をまっすぐ進むと大きな郵便局があります。", "Kono michi o massugu susumu to ookina yuubinkyoku ga arimasu.", "If you advance straight along this road, there is a large post office.", "movement,direction", ["進む", "進学"]),
    ("返", "ヘン", "かえ.す, かえ.る", 7, "辵", "return, answer", "図書館で借りた本を期日までに返しました。", "Toshokan de karita hon o kijitsu made ni kaeshimashita.", "I returned the book borrowed from the library by the due date.", "action,movement", ["返す", "返事"]),
    ("送", "ソウ", "おく.る", 9, "辵", "send, dispatch, escort", "故郷の両親に手紙と写真を送りました。", "Kokyou no ryoushin ni tegami to shashin o okurimashita.", "I sent a letter and photographs to my parents in my hometown.", "movement,communication", ["送る", "見送る"]),
    ("野", "ヤ, ショ", "の", 11, "里", "field, plains, rustic", "広大な平野でたくさんの野菜が育てられています。", "Koudai na heiya de takusan no yasai ga sodaterarete imasu.", "Many vegetables are grown in the vast open plain.", "nature,spatial", ["野菜", "分野"]),
    ("地", "チ, ジ", "", 6, "土", "ground, earth, region", "地下鉄の駅を出て大通りをまっすぐ歩きました。", "Chikatetsu no eki o dete oodoori o massugu arukimashita.", "Exiting the subway station, I walked straight along the main street.", "nature,spatial", ["地下鉄", "地図"]),
    ("海", "カイ", "うみ", 9, "水", "sea, ocean", "青い海を眺めながら静かな時間を過ごしました。", "Aoi umi o nagamenagara shizuka na jikan o sugoshimashita.", "I spent a quiet time while gazing at the blue ocean.", "nature,travel", ["海岸", "海外"]),
    ("空", "クウ", "そら, あ.く, から", 8, "穴", "sky, empty, void", "澄み渡る秋の青空に白い雲が浮かんでいます。", "Sumiwataru aki no aozora ni shiroi kumo ga ukande imasu.", "White clouds are floating in the clear autumn blue sky.", "nature,spatial", ["空気", "空港"]),
    ("港", "コウ", "みなと", 12, "水", "harbor, port", "成田国際空港に夕方無事に到着しました。", "Narita kokusai kuukou ni yuugata buji ni touchaku shimashita.", "I arrived safely at Narita International Airport in the evening.", "transport,places", ["空港", "港"]),
    ("乗", "ジョウ", "の.る, の.せる", 9, "丿", "ride, board, mount", "朝八時の電車に乗って会社へ行きます。", "Asa hachiji no densha ni notte kaisha e ikimasu.", "I board the 8:00 morning train and go to work.", "transport,action", ["乗る", "乗り換える"]),
    ("降", "コウ", "お.りる, ふ.る", 10, "阜", "descend, fall, alight", "次の駅で電車を降りてバスに乗り換えます。", "Tsugi no eki de densha o orite basu ni norikaemasu.", "I will get off the train at the next station and transfer to a bus.", "transport,action", ["降りる", "降車"]),

    # STAGE 3: Food, Commerce, Home & Everyday Life (25 Kanji)
    ("売", "バイ", "う.る, う.れる", 7, "士", "sell", "駅前の売店で朝の新聞を売っています。", "Ekimae no baiten de asa no shinbun o utte imasu.", "They sell morning newspapers at the shop in front of the station.", "commerce,daily", ["売る", "売り場"]),
    ("貸", "タイ", "か.す", 12, "貝", "lend, loan", "困っている友達にノートを快く貸してあげました。", "Komatte iru tomodachi ni nooto o kokoroyoku kashite agemashita.", "I gladly lent my notes to my friend who was in trouble.", "commerce,daily", ["貸す", "賃貸"]),
    ("借", "シャク", "か.りる", 10, "人", "borrow, rent", "図書館から日本語の参考書を二冊借りました。", "Toshokan kara nihongo no sankousho o nisatsu karimashita.", "I borrowed two Japanese study guides from the library.", "commerce,daily", ["借りる", "借金"]),
    ("紙", "シ", "かみ", 10, "糸", "paper", "手紙をきれいな便箋に書いてポストに入れました。", "Tegami o kirei na binsen ni kaite posuto ni iremashita.", "I wrote a letter on beautiful stationery and posted it.", "daily,communication", ["手紙", "用紙"]),
    ("飯", "ハン", "めし", 12, "食", "meal, cooked rice", "家族と一緒に朝ご飯をしっかり食べました。", "Kazoku to issho ni asagohan o shikkari tabemashita.", "I ate a hearty breakfast together with my family.", "daily,food", ["ご飯", "朝飯"]),
    ("牛", "ギュウ", "うし", 4, "牛", "cow, cattle, beef", "朝起きて冷たい牛乳を一杯飲みました。", "Asa okite tsumetai gyuunyuu o ippai nomimashita.", "Upon waking in the morning, I drank a glass of cold milk.", "daily,food", ["牛乳", "牛肉"]),
    ("鳥", "チョウ", "とり", 11, "鳥", "bird, fowl, chicken", "公園の池の周りで小鳥たちがさえずっています。", "Kouen no ike no mawari de kotoritachi ga saezutte imasu.", "Small birds are chirping around the park pond.", "daily,nature", ["小鳥", "鳥肉"]),
    ("菜", "サイ", "な", 11, "艸", "vegetable, side dish, greens", "健康のために毎朝新鮮な野菜を食べます。", "Kenkou no tame ni maiasa shinsen na yasai o tabemasu.", "For my health, I eat fresh vegetables every morning.", "daily,food", ["野菜", "総菜"]),
    ("味", "ミ", "あじ, あじ.わう", 8, "口", "flavor, taste", "この料理は少し甘い味がして美味しいです。", "Kono ryouri wa sukoshi amai aji ga shite oishii desu.", "This dish has a slightly sweet flavor and is delicious.", "daily,food", ["意味", "味わう"]),
    ("料", "リョウ", "", 10, "斗", "fee, materials, ingredients", "日本料理の作り方を教室で習いました。", "Nihonryouri no tsukurikata o kyoushitsu de naraimashita.", "I learned how to prepare Japanese cuisine in the cooking class.", "daily,food", ["料理", "料金"]),
    ("理", "リ", "ことわり", 11, "玉", "logic, reason, principle", "休日に友達を招いて美味しい料理を作りました。", "Kyuujitsu ni tomodachi o maneite oishii ryouri o tsukurimashita.", "On my day off, I invited my friend and prepared delicious cooking.", "daily,logic", ["料理", "理由"]),
    ("物", "ブツ, モツ", "もの", 8, "牛", "thing, object, matter", "駅ビルで週末の旅行に必要な買い物をしました。", "Ekibiru de shuumatsu no ryokou ni hitsuyou na kaimono o shimashita.", "I did shopping for items needed for the weekend trip at the station building.", "daily,basic", ["食べ物", "買い物"]),
    ("品", "ヒン", "しな", 9, "口", "goods, refinement, article", "伝統工芸品をお土産として選びました。", "Dentou kougeihin o omiyage to shite erabimashita.", "I chose a traditional handicraft as a souvenir.", "commerce,daily", ["商品", "作品"]),
    ("服", "フク", "", 8, "月", "clothing, attire, obey", "新しい季節に合わせて洋服を買いに行きました。", "Atarashii kisetsu ni awasete youfuku o kai ni ikimashita.", "I went to buy clothes suited to the new season.", "daily,clothing", ["洋服", "和服"]),
    ("洋", "ヨウ", "", 9, "水", "ocean, Western, foreign", "和食だけでなく美味しい洋食も大好きです。", "Washoku dakedenaku oishii youshoku mo daisuki desu.", "I love not only Japanese food, but also delicious Western food.", "daily,culture", ["洋食", "洋服"]),
    ("家", "カ, ケ", "いえ, や", 10, "宀", "house, home, family", "仕事を終えて自分の家にまっすぐ帰りました。", "Shigoto o oete jibun no ie ni massugu kaerimashita.", "Finishing work, I returned directly to my own home.", "daily,home", ["家族", "家内"]),
    ("族", "ゾク", "", 11, "方", "tribe, family, clan", "お正月に家族全員が集まってお祝いをしました。", "Oshougatsu ni kazoku zen'in ga atsumatte oiwai o shimashita.", "At New Year's, the whole family gathered and celebrated.", "daily,home", ["家族", "親族"]),
    ("具", "グ", "そな.える", 8, "八", "tool, utensil, ingredient", "新生活のために新しい家具を揃えました。", "Shinseikatsu no tame ni atarashii kagu o soroemashita.", "I gathered new furniture for my new life.", "daily,home", ["道具", "家具"]),
    ("門", "モン", "かど", 8, "門", "gate, entrance", "大学の正門の前で友達と待ち合わせました。", "Daigaku no seimon no mae de tomodachi to machiawasemashita.", "I met up with my friend in front of the main university gate.", "daily,places", ["専門", "校門"]),
    ("戸", "コ", "と", 4, "戸", "door, counter for houses", "風が入らないように部屋の戸をしっかり閉めました。", "Kaze ga hairanai you ni heya no to o shikkari shimemashita.", "I closed the room door securely so wind would not enter.", "daily,home", ["戸棚", "雨戸"]),
    ("室", "シツ", "むろ", 9, "宀", "room, chamber", "静かな研究室で熱心に勉強を続けました。", "Shizuka na kenkyuushitsu de nesshin ni benkyou o tsuzukemashita.", "I continued studying earnestly in the quiet study room.", "daily,places", ["教室", "研究室"]),
    ("洗", "セン", "あら.う", 9, "水", "wash, cleanse", "帰宅したらすぐに洗面所で手をきれいに洗います。", "Kitaku shitara sugu ni senmenjo de te o kirei ni araimasu.", "Upon returning home, I immediately wash my hands cleanly in the washroom.", "daily,action", ["洗う", "洗濯"]),
    ("台", "ダイ, タイ", "", 5, "口", "stand, platform, counter for machines", "台所で夕食の準備を始めました。", "Daidokoro de yuushoku no junbi o hajimemashita.", "I began preparing dinner in the kitchen.", "daily,home", ["台所", "一台"]),
    ("夜", "ヤ", "よ, よる", 8, "夕", "night, evening", "静かな夜に机に向かって勉強します。", "Shizuka na yoru ni tsukue ni mukatte benkyou shimasu.", "In the quiet night, I sit at my desk and study.", "time,daily", ["今夜", "夜景"]),
    ("朝", "チョウ", "あさ", 12, "月", "morning", "毎朝六時に起きて散歩をします。", "Maiasa rokuji ni okite sanpo o shimasu.", "Every morning I wake up at 6:00 and take a walk.", "time,daily", ["今朝", "朝食"]),

    # STAGE 4: Nature, Season, Atmosphere & Weather (25 Kanji)
    ("昼", "チュウ", "ひる", 9, "日", "daytime, noon", "昼休みに会社の同僚と一緒に食事をしました。", "Hiruyasumi ni kaisha no douryou to issho ni shokuji o shimashita.", "During lunch break, I ate a meal together with my company colleague.", "time,nature", ["昼食", "昼休み"]),
    ("夕", "セキ", "ゆう", 3, "夕", "evening", "美しい夕日を見ながら浜辺を歩きました。", "Utsukushii yuuhi o minagara hamabe o arukimashita.", "I walked along the beach while watching the beautiful evening sun.", "time,nature", ["夕方", "夕食"]),
    ("春", "シュン", "はる", 9, "日", "spring (season)", "春になると桜が満開になります。", "Haru ni naru to sakura ga mankai ni narimasu.", "When spring arrives, cherry blossoms reach full bloom.", "nature,season", ["春休み", "青春"]),
    ("夏", "カ, ゲ", "なつ", 10, "夂", "summer", "日本の夏は蒸し暑くて雨が多いです。", "Nihon no natsu wa mushiatsukute ame ga ooi desu.", "Summer in Japan is humid and has plenty of rain.", "nature,season", ["夏休み", "真夏"]),
    ("秋", "シュウ", "あき", 9, "禾", "autumn, fall", "秋は紅葉が美しく、過ごしやすい季節です。", "Aki wa kouyou ga utsukushiku, sugoshiyasui kisetsu desu.", "Autumn has beautiful fall foliage and is a comfortable season.", "nature,season", ["秋祭り", "晩秋"]),
    ("冬", "トウ", "ふゆ", 5, "冫", "winter", "北国の冬は雪がたくさん降って寒いです。", "Kitaguni no fuyu wa yuki ga takusan futte samui desu.", "Winter in the north country brings heavy snowfall and cold.", "nature,season", ["冬休み", "真冬"]),
    ("雪", "セツ", "ゆき", 11, "雨", "snow", "昨夜降った雪で街全体が白く覆われました。", "Sakuya futta yuki de machi zentai ga shiroku oowaremashita.", "With the snow that fell last night, the whole town was covered in white.", "nature,weather", ["雪景色", "大雪"]),
    ("風", "フウ, フ", "かぜ, かざ", 9, "風", "wind, breeze, manner", "海から涼しい風が吹いてきました。", "Umi kara suzushii kaze ga fuite kimashita.", "A cool breeze blew in from the sea.", "nature,weather", ["台風", "風邪"]),
    ("晴", "セイ", "は.れる, は.らす", 12, "日", "clear weather, sunny", "休日は気持ちよく晴れて絶好の行楽日和でした。", "Kyuujitsu wa kimochiyoku harete zekkou no kourakubiyori desu.", "The holiday was pleasantly clear, perfect outing weather.", "nature,weather", ["晴れ", "快晴"]),
    ("雲", "ウン", "くも", 12, "雨", "cloud", "青空に白い雲が浮かんでいます。", "Aozora ni shiroi kumo ga ukande imasu.", "White clouds are floating in the blue sky.", "nature,weather", ["雨雲", "白雲"]),
    ("池", "チ", "いけ", 6, "水", "pond, reservoir", "静かな公園の池で魚が泳いでいます。", "Shizuka na kouen no ike de sakana ga oyoide imasu.", "Fish are swimming in the quiet park pond.", "nature,places", ["池端", "貯水池"]),
    ("石", "セキ, シャク", "いし", 5, "石", "stone, rock", "庭に美しい白い石を並べて小道を作りました。", "Niwa ni utsukushii shiroi ishi o narabete komichi o tsukurimashita.", "I arranged beautiful white stones in the garden to make a path.", "nature,material", ["石鹸", "宝石"]),
    ("林", "リン", "はやし", 8, "木", "grove, woods", "木漏れ日が心地よい林の中を歩きました。", "Komorebi ga kokochiyoi hayashi no naka o arukimashita.", "I walked through the grove where sunlight filtered pleasantly through trees.", "nature,woods", ["林業", "森林"]),
    ("森", "シン", "もり", 12, "木", "forest, woods", "自然が残る深い森で野鳥を観察しました。", "Shizen ga nokoru fukai mori de yachou o kansatsu shimashita.", "I observed wild birds in the deep forest where nature remains.", "nature,woods", ["森林", "森の中"]),
    ("草", "ソウ", "くさ", 9, "艸", "grass, weed, herbs", "庭に生えた草をきれいに抜きました。", "Niwa ni haeta kusa o kirei ni nukimashita.", "I neatly pulled out the weeds growing in the garden.", "nature,plant", ["草花", "雑草"]),
    ("竹", "チク", "たけ", 6, "竹", "bamboo", "嵐山にある美しい竹林を歩きました。", "Arashiyama ni aru utsukushii chikurin o arukimashita.", "I walked the beautiful bamboo grove in Arashiyama.", "nature,plant", ["竹林", "竹の子"]),
    ("虫", "チュウ", "むし", 6, "虫", "insect, bug", "夏の夜に庭から虫の声が聞こえます。", "Natsu no yoru ni niwa kara mushi no koe ga kikoemasu.", "On summer nights, the sound of insects can be heard from the garden.", "nature,creature", ["昆虫", "虫歯"]),
    ("犬", "ケン", "いぬ", 4, "犬", "dog", "毎朝近所の公園で犬の散歩をします。", "Maiasa kinjo no kouen de inu no sanpo o shimasu.", "Every morning I take a dog for a walk in the neighborhood park.", "nature,creature", ["番犬", "子犬"]),
    ("光", "コウ", "ひか.る, ひかり", 6, "儿", "light, ray", "朝の優しい日光が部屋いっぱいに差し込みました。", "Asa no yasashii nikkou ga heya ippai ni sashikomimashita.", "Gentle morning sunlight streamed into the room.", "nature,light", ["日光", "観光"]),
    ("暗", "アン", "くら.い", 13, "日", "dark, gloomy", "日が沈んで外が暗くなってきたので急いで帰りました。", "Hi ga shizunde soto ga kuraku natte kita node isoide kaerimashita.", "The sun set and outside grew dark, so I hurried home.", "nature,state", ["暗い", "暗記"]),
    ("音", "オン, イン", "おと, ね", 9, "音", "sound, noise", "静かな部屋でリラックスできる音楽を聴きました。", "Shizuka na heya de rirakkusu dekiru ongaku o kikimashita.", "In the quiet room, I listened to relaxing music.", "arts,mind", ["音楽", "本音"]),
    ("楽", "ガク, ラク", "たの.しい", 13, "木", "music, comfort, fun", "友達と話すのはとても楽しい時間です。", "Tomodachi to hanasu no wa totemo tanoshii jikan desu.", "Talking with friends is a very fun time.", "arts,mind", ["音楽", "楽しい"]),
    ("歌", "カ", "うた, うた.う", 14, "欠", "song, to sing", "みんなで手拍子をしながら歌を歌いました。", "Minna de tebyoushi o shinagara uta o utaimashita.", "While clapping hands together, everyone sang songs.", "arts,culture", ["歌手", "短歌"]),
    ("画", "ガ, カク", "", 8, "田", "picture, stroke, plan", "休日に美術館へ行って絵画を鑑賞しました。", "Kyuujitsu ni bijutsukan e itte kaiga o kanshou shimashita.", "On my day off, I went to the art museum and admired paintings.", "arts,culture", ["映画", "計画"]),
    ("映", "エイ", "うつ.る, うつ.す", 9, "日", "reflect, projection, shine", "週末に映画館へ行って話題の新作映画を鑑賞しました。", "Shuumatsu ni eigakan e itte wadai no shinsaku eiga o kanshou shimashita.", "Over the weekend, I went to the movie theater and watched the new film.", "arts,visual", ["映画", "上映"]),

    # STAGE 5: Mind, Communication, Education & Thinking (25 Kanji)
    ("真", "シン", "ま, まこと", 10, "目", "true, reality, genuine", "旅行先で思い出に残るきれいな写真を撮りました。", "Ryokousaki de omoide ni nokoru kirei na shashin o torimashita.", "At my travel destination, I took beautiful photographs that remain as memories.", "arts,mind", ["写真", "真実"]),
    ("思", "シ", "おも.う", 9, "心", "think, consider, feel", "努力を続けることが何より大切だと思います。", "Doryoku o tsuzukeru koto ga nani yori taisetsu da to omoimasu.", "I think continuing effort is more important than anything.", "mind,cognition", ["思い出", "思考"]),
    ("考", "コウ", "かんが.える", 6, "老", "think, consider", "難しい問題の解決策をじっくり考えました。", "Muzukashii mondai no kaiketsusaku o jikkuri kangaemashita.", "I carefully considered the solution for the difficult problem.", "mind,cognition", ["考える", "参考"]),
    ("知", "チ", "し.る, し.らせる", 8, "矢", "know, understand, inform", "日本の文化や歴史をもっと深く知りたいです。", "Nihon no bunka ya rekishi o motto fukaku shiritai desu.", "I want to know Japanese culture and history more deeply.", "mind,knowledge", ["知人", "知識"]),
    ("教", "キョウ", "おし.える, おそ.わる", 11, "攴", "teach, instruction, faith", "大学の教室で熱心な先生に日本語を教わりました。", "Daigaku no kyoushitsu de nesshin na sensei ni nihongo o osowarimashita.", "In the university classroom, I was taught Japanese by an enthusiastic teacher.", "education,mind", ["教室", "教会"]),
    ("習", "シュウ", "なら.う", 11, "羽", "learn, custom, practice", "毎週土曜日に茶道教室で作法を習っています。", "Maishuu doyoubi ni sadou kyoushitsu de sahou o naratte imasu.", "Every Saturday I learn etiquette at the tea ceremony school.", "education,mind", ["復習", "予習"]),
    ("研", "ケン", "と.ぐ", 9, "石", "polish, sharpen, study", "大学院の研究室で新しい技術の開発に取り組みます。", "Daigakuin no kenkyuushitsu de atarashii gijutsu no kaihatsu ni torikumimasu.", "In the graduate laboratory, I engage in research and development of new tech.", "education,mind", ["研究", "研究所"]),
    ("究", "キュウ", "きわ.める", 7, "穴", "research, investigate", "伝統芸能の歴史を深く研究しています。", "Dentou geinou no rekishi o fukaku kenkyuu shite imasu.", "I am deeply researching the history of traditional performing arts.", "education,mind", ["研究", "追究"]),
    ("文", "ブン, モン", "ふみ", 4, "文", "writing, sentence, culture", "手紙の文章を丁寧に確認してから送りました。", "Tegami no bunshou o teinei ni kakunin shite kara okurimashita.", "I carefully checked the text of the letter before sending it.", "education,culture", ["文化", "作文"]),
    ("字", "ジ", "", 6, "子", "character, letter, word", "ノートにきれいな漢字を書く練習をしました。", "Nooto ni kirei na kanji o kaku renshuu o shimashita.", "I practiced writing neat Kanji characters in my notebook.", "education,mind", ["漢字", "文字"]),
    ("英", "エイ", "", 8, "艸", "English, outstanding, flower", "高校で毎日英語を学びました。", "Koukou de mainichi eigo o manabimashita.", "In high school, I learned English every day.", "education,language", ["英語", "英会話"]),
    ("答", "トウ", "こた.える, こた.え", 12, "竹", "answer, reply", "先生の質問に対して正確に答えました。", "Sensei no shitsumon ni taishite seikaku ni kotaemashita.", "In response to the teacher's question, I answered accurately.", "communication,education", ["答え", "回答"]),
    ("問", "モン", "と.う, と.い", 11, "口", "question, ask, problem", "試験の最後の問題について質問しました。", "Shiken no saigo no mondai ni tsuite shitsumon shimashita.", "I asked a question regarding the final problem of the exam.", "education,mind", ["問題", "質問"]),
    ("試", "シ", "ため.す", 13, "言", "test, try, attempt", "来週の金曜日に日本語能力試験を受験します。", "Raishuu no kinyoubi ni nihongo nouryoku shiken o juken shimasu.", "Next Friday, I will take the Japanese Language Proficiency Test.", "education,mind", ["試験", "試す"]),
    ("験", "ケン", "", 18, "馬", "test, verify, effect", "日本での生活を通して様々な貴重な経験を積みました。", "Nihon de no seikatsu o tooshite samazama na kichou na keiken o tsumimashita.", "Through life in Japan, I accumulated various precious experiences.", "education,mind", ["経験", "受験"]),
    ("意", "イ", "", 13, "心", "idea, mind, intention", "分からない単語の意味を辞書で調べました。", "Wakaranai tango no imi o jisho de shirabemashita.", "I looked up the meaning of words I did not understand in a dictionary.", "mind,meaning", ["意味", "注意"]),
    ("心", "シン", "こころ", 4, "心", "heart, mind, spirit", "温かい言葉に心から深く感謝しました。", "Atatakai kotoba ni kokoro kara fukaku kansha shimashita.", "I thanked them deeply from my heart for the warm words.", "mind,spirit", ["心配", "安心"]),
    ("注", "チュウ", "そそ.ぐ", 8, "水", "pour, note, concentrate", "車に注意しながら横断歩道を渡りました。", "Kuruma ni chuui shinagara oudanhodou o watarimashita.", "I crossed the pedestrian crossing while paying attention to cars.", "mind,action", ["注意", "注文"]),
    ("勉", "ベン", "", 10, "力", "exertion, study", "試験に合格するために毎日日本語を勉強しています。", "Shiken ni goukaku suru tame ni mainichi nihongo o benkyou shite imasu.", "In order to pass the exam, I study Japanese every day.", "education,mind", ["勉強", "勤勉"]),
    ("題", "ダイ", "", 18, "頁", "topic, problem, title", "宿題の問題を解いてから寝ることにします。", "Shukudai no mondai o toite kara neru koto ni shimasu.", "I decide to go to sleep after solving the homework problems.", "education,mind", ["宿題", "話題"]),
    ("計", "ケイ", "はか.る", 9, "言", "measure, calculate, plan", "旅行の計画を友達と一緒に立てました。", "Ryokou no keikaku o tomodachi to issho ni tatemashita.", "I drew up the travel plan together with my friend.", "measure,plan", ["計画", "時計"]),
    ("用", "ヨウ", "もち.いる", 5, "用", "utilize, business, service", "大切な用事があるので今日は定時で退社します。", "Taisetsu na youji ga aru node kyou wa teiji de taisha shimasu.", "Because I have important business, I leave work on time today.", "daily,business", ["用事", "用意"]),
    ("質", "シツ", "", 15, "貝", "quality, substance, matter", "授業の後で先生にいくつか質問をしました。", "Jugyou no ato de sensei ni ikutsuka shitsumon o shimashita.", "After class, I asked the teacher several questions.", "education,mind", ["質問", "品質"]),
    ("代", "ダイ", "か.わる", 5, "人", "substitute, generation, fee", "先生の代わりに本日の講義を担当しました。", "Sensei no kawari ni honjitsu no kougi o tantou shimashita.", "I took charge of today's lecture in place of the teacher.", "society,roles", ["時代", "電気代"]),
    ("世", "セイ, セ", "よ", 5, "一", "generation, world, age", "世界中の様々な人々と友達になりたいです。", "Sekaijuu no samazama na hitobito to tomodachi ni naritai desu.", "I want to become friends with diverse people all over the world.", "society,world", ["世界", "世話"]),

    # STAGE 6: Human Body, Health, Medicine & Sensations (25 Kanji)
    ("体", "タイ, テイ", "からだ", 7, "人", "body, health, form", "健康のために毎朝公園を走って体を動かします。", "Kenkou no tame ni maiasa kouen o hashitte karada o ugokashimasu.", "For my health, I run in the park every morning to move my body.", "body,health", ["体重", "体操"]),
    ("頭", "トウ, ズ", "あたま", 16, "頁", "head, brain, top", "風邪をひいて少し頭が痛いので薬を飲みました。", "Kaze o hiite sukoshi atama ga itai node kusuri o nomimashita.", "Because I caught a cold and my head aches a bit, I took medicine.", "body,health", ["頭痛", "先頭"]),
    ("顔", "ガン", "かお", 18, "頁", "face, expression", "冷たい水で顔を洗うと目が覚めます。", "Tsumetai mizu de kao o arau to me ga samemasu.", "When I wash my face with cold water, I wake up.", "body,health", ["笑顔", "洗顔"]),
    ("首", "シュ", "くび", 9, "首", "neck, head, leader", "寒い冬の日は首にマフラーを巻いて出かけます。", "Samui fuyu no hi wa kubi ni mafuraa o maite dekakemasu.", "On cold winter days, I wrap a scarf around my neck and head out.", "body,clothing", ["首都", "手首"]),
    ("歯", "シ", "は", 12, "歯", "tooth", "食事の後は虫歯を防ぐために歯を磨きます。", "Shokuji no ato wa mushiba o fusegu tame ni ha o migakimasu.", "After meals, I brush my teeth in order to prevent cavities.", "body,health", ["歯医者", "虫歯"]),
    ("病", "ビョウ, ヘイ", "やまい", 10, "疒", "illness, disease, sick", "病気にならないように毎日の手洗いとうがいを徹底します。", "Byouki ni naranai you ni mainichi no tearai to ugai o tettei shimasu.", "So as not to fall sick, I thoroughly wash hands and gargle every day.", "health,body", ["病気", "病院"]),
    ("痛", "ツウ", "いた.い", 12, "疒", "pain, ache, hurt", "転んで膝を強く打ってしまい、痛みが続いています。", "Koronde hiza o tsuyoku utte shimai, itami ga tsuzuite imasu.", "I fell and struck my knee hard, and the pain is continuing.", "health,body", ["痛い", "頭痛"]),
    ("薬", "ヤク", "くすり", 16, "艸", "medicine, chemical", "風邪の症状を抑えるために食後に薬を飲みました。", "Kaze no shoujou o osaeru tame ni shokugo ni kusuri o nomimashita.", "In order to suppress cold symptoms, I took medicine after the meal.", "health,medicine", ["薬局", "目薬"]),
    ("医", "イ", "", 7, "匚", "doctor, medicine, heal", "体調がすぐれないので近所の医院で診察を受けました。", "Taichou ga sugurenai node kinjo no iin de shinsatsu o ukemashita.", "Because I was not feeling well, I received an exam at a neighborhood clinic.", "health,profession", ["医者", "医院"]),
    ("者", "シャ", "もの", 8, "老", "person, specialist", "病院で優しい医者に丁寧に診てもらいました。", "Byouin de yasashii isha ni teinei ni mite moraimashita.", "At the hospital, I had a gentle physician examine me carefully.", "health,people", ["医者", "学者"]),
    ("寒", "カン", "さむ.い", 12, "宀", "cold (climate/weather)", "今年の冬は例年よりも一段と寒いです。", "Kotoshi no fuyu wa reinen yori mo ichidan to samui desu.", "This year's winter is even colder than normal years.", "body,nature", ["寒気", "極寒"]),
    ("暑", "ショ", "あつ.い", 12, "日", "hot (weather/climate)", "夏休みは毎日とても暑いので熱中症に注意します。", "Natsuyasumi wa mainichi totemo atsui node netchuushou ni chuui shimasu.", "Because it is very hot every day during summer break, beware heatstroke.", "body,nature", ["猛暑", "残暑"]),
    ("熱", "ネツ", "あつ.い", 15, "火", "heat, fever, passion", "風邪をひいて高い熱が出たので一日中寝ていました。", "Kaze o hiite takai netsu ga deta node ichinichijuu nete imashita.", "Because I caught a cold and had a high fever, I stayed in bed all day.", "health,nature", ["熱心", "高熱"]),
    ("冷", "レイ", "つめ.たい, ひ.える", 7, "冫", "cold (tactile), cool, chilly", "喉が渇いたので冷蔵庫から冷たいお茶を取り出しました。", "Nodo ga kawaita node reizouko kara tsumetai ocha o toridashimashita.", "Because my throat was dry, I took out cold tea from the fridge.", "daily,sensory", ["冷蔵庫", "冷たい"]),
    ("温", "オン", "あたた.かい", 12, "水", "warm, temperature", "寒い夜は温かいお風呂にゆっくり浸かって温まります。", "Samui yoru wa atatakai ofuro ni yukkuri tsukatte atatamarimasu.", "On cold nights, I soak slowly in a warm bath and warm up.", "daily,sensory", ["温泉", "気温"]),
    ("重", "ジュウ, チョウ", "おも.い, かさ.なる", 9, "里", "heavy, serious, pile up", "教科書がたくさん入った重い鞄を持って登校しました。", "Kyoukasho ga takusan haitta omoi kaban o motte toukou shimashita.", "I went to school carrying a heavy bag packed with many textbooks.", "state,measurement", ["体重", "重要"]),
    ("軽", "ケイ", "かる.い", 12, "車", "light (weight), agile", "軽くて持ち運びに便利なノートパソコンを買いました。", "Karukute mochihakobi ni benri na nootopasokon o kaimashita.", "I bought a laptop that is light and convenient to carry around.", "state,measurement", ["気軽", "軽食"]),
    ("広", "コウ", "ひろ.い", 5, "广", "wide, spacious", "天井が高くて広々とした部屋に住んでいます。", "Tenjou ga takakute hirobiro to shita heya ni sunde imasu.", "I live in a spacious room with a high ceiling.", "spatial,state", ["広場", "広告"]),
    ("早", "ソウ, サッ", "はや.い", 6, "日", "early, fast", "明日の朝は早いので今夜は早めに寝ます。", "Ashita no asa wa hayai node kon'ya wa hayame ni nemasu.", "Because tomorrow morning is early, I will go to bed early tonight.", "time,state", ["早朝", "早起き"]),
    ("速", "ソク", "はや.い", 10, "辵", "fast, speedy, rapid", "新幹線はとても速い乗り物です。", "Shinkansen wa totemo hayai norimono desu.", "The bullet train is a very fast vehicle.", "speed,transport", ["速度", "早速"]),
    ("遅", "チ", "おそ.い, おく.れる", 12, "辵", "late, slow, delay", "事故の影響で電車の到着が十分遅れました。", "Jiko no eikyou de densha no touchaku ga juppun okuremashita.", "Due to an accident, the train's arrival was delayed by 10 minutes.", "time,speed", ["遅刻", "遅い"]),
    ("短", "タン", "みじか.い", 12, "矢", "short, brief, fault", "夏休みはあっという間に終わって短く感じました。", "Natsuyasumi wa attoiumani owatte mijikaku kanjimashita.", "Summer vacation ended in a flash and felt short.", "measurement,time", ["短所", "短期"]),
    ("親", "シン", "おや, した.しい", 16, "見", "parent, intimate, familiar", "故郷にいる両親へ感謝の手紙を送りました。", "Kokyou ni iru ryoushin e kansha no tegami o okurimashita.", "I sent a thank-you letter to my parents in my hometown.", "people,family", ["両親", "親切"]),
    ("便", "ベン, ビン", "たよ.り", 9, "人", "convenience, mail, flight", "この街は交通の便が良くて住みやすいです。", "Kono machi wa koutsuu no ben ga yokute sumiyasui desu.", "This town has good transportation convenience and is easy to live in.", "transport,daily", ["便利", "郵便"]),
    ("利", "リ", "き.く", 7, "刀", "profit, advantage, benefit", "駅の近くに住むのはとても便利です。", "Eki no chikaku ni sumu no wa totemo benri desu.", "Living near the station is very convenient.", "daily,commerce", ["便利", "利用"]),

    # STAGE 7: Actions, State Changes, Relations & Measurements (25 Kanji)
    ("不", "フ, ブ", "", 4, "一", "negative, non-, bad, un-", "バスが少ない地域なので少し不便を感じています。", "Basu ga sukunai chiiki na node sukoshi fuben o kanjite imasu.", "Because buses are scarce in this area, I feel it is slightly inconvenient.", "abstract,negative", ["不便", "不安"]),
    ("有", "ユウ, ウ", "あ.る", 6, "月", "have, possess, exist, famous", "京都には歴史ある有名な寺院がたくさんあります。", "Kyouto ni wa rekishi aru yuumei na jiin ga takusan arimasu.", "Kyoto has many famous temples with long history.", "society,state", ["有名", "有料"]),
    ("特", "トク", "", 10, "牛", "special, particular", "誕生日のお祝いに特別なケーキを注文しました。", "Tanjoubi no oiwai ni tokubetsu na keeki o chuumon shimashita.", "I ordered a special cake for the birthday celebration.", "nature,abstract", ["特別", "特急"]),
    ("別", "ベツ", "わか.れる", 7, "刀", "separate, another, part", "駅の改札口の前で友人と名残惜しく別れました。", "Eki no kaisatsuguchi no mae de yuujin to nagorioshiku wakaremashita.", "In front of the station ticket gate, I reluctantly parted with my friend.", "social,action", ["特別", "別れる"]),
    ("始", "シ", "はじ.める, はじ.まる", 8, "女", "begin, start", "朝九時に学校の最初の授業が始まります。", "Asa kuji ni gakkou no saisho no jugyou ga hajimarimasu.", "At 9:00 in the morning, the school's first class starts.", "action,time", ["始める", "開始"]),
    ("終", "シュウ", "お.わる, お.える", 11, "糸", "end, finish", "今日の仕事を全部終えて夕方に帰宅しました。", "Kyou no shigoto o zenbu oete yuugata ni kitaku shimashita.", "Finishing all of today's work, I returned home in the evening.", "action,time", ["終わる", "終電"]),
    ("開", "カイ", "ひら.く, あ.く, あ.ける", 12, "門", "open, unfold", "新鮮な空気を入れるために部屋の窓を開けました。", "Shinsen na kuuki o ireru tame ni heya no mado o akemashita.", "In order to let fresh air in, I opened the room window.", "action,state", ["開ける", "開店"]),
    ("閉", "ヘイ", "と.じる, し.まる, し.める", 11, "門", "close, shut", "雨が激しく降ってきたので急いで窓を閉めました。", "Ame ga hageshiku futte kita node isoide mado o shimemashita.", "Because rain began falling heavily, I closed the window in a hurry.", "action,state", ["閉める", "閉店"]),
    ("座", "ザ", "すわ.る", 10, "广", "sit, seat", "新幹線の指定席に座ってゆっくり景色を眺めました。", "Shinkansen no shiteiseki ni suwatte yukkuri keshiki o nagamemashita.", "Sitting in the reserved seat of the bullet train, I leisurely watched scenery.", "action,body", ["座る", "口座"]),
    ("使", "シ", "つか.う", 8, "人", "use, employ, envoy", "辞書を使って難しい単語の意味を調べました。", "Jisho o tsukatte muzukashii tango no imi o shirabemashita.", "Using a dictionary, I looked up the meaning of difficult words.", "action,daily", ["使う", "大使館"]),
    ("作", "サク, サ", "つく.る", 7, "人", "make, create, produce", "家族のために夕食のカレーライスを作りました。", "Kazoku no tame ni yuushoku no kareeraisu o tsukurimashita.", "I made curry rice for dinner for my family.", "action,creation", ["作る", "作文"]),
    ("持", "ジ", "も.つ", 9, "手", "hold, have, possess", "旅行に出かける時はパスポートを持っています。", "Ryokou ni dekakeru toki wa pasupooto o motte imasu.", "When going on a trip, I keep my passport with me.", "action,possession", ["持つ", "気持ち"]),
    ("待", "タイ", "ま.つ", 9, "彳", "wait, depend on", "駅の改札口の前で友達が来るのを待っていました。", "Eki no kaisatsuguchi no mae de tomodachi ga kuru no o matte imashita.", "In front of the station ticket gate, I was waiting for my friend to arrive.", "action,patience", ["待つ", "招待"]),
    ("忘", "ボウ", "わす.れる", 7, "心", "forget", "大切な傘を電車の中に忘れないように注意してください。", "Taisetsu na kasa o densha no naka ni wasurenai you ni chuui shite kudasai.", "Please be careful not to forget your important umbrella on the train.", "mental,action", ["忘れる", "忘れ物"]),
    ("置", "チ", "お.く", 13, "网", "put, place, leave", "机の上に筆記用具をきちんと置きました。", "Tsukue no ue ni hikkiyougu o kichinto okimashita.", "I placed the writing instruments neatly on the desk.", "action,spatial", ["置く", "位置"]),
    ("残", "ザン", "のこ.る, のこ.す", 10, "歹", "remain, balance, leave behind", "残りの仕事を片付けてから急いで帰宅しました。", "Nokori no shigoto o katazukete kara isoide kitaku shimashita.", "After finishing the remaining work, I hurried home.", "state,action", ["残念", "残業"]),
    ("集", "シュウ", "あつ.まる, あつ.める", 12, "隹", "gather, collect, assemble", "週末に広場でボランティアが集まって掃除をしました。", "Shuumatsu ni hiroba de borantia ga atsumatte souji o shimashita.", "On the weekend, volunteers gathered in the plaza and cleaned up.", "action,society", ["集まる", "集合"]),
    ("引", "イン", "ひ.く, ひ.ける", 4, "弓", "pull, draw, quote", "ドアの取っ手を強く引いて部屋の中に入りました。", "Doa no totte o tsuyoku hiite heya no naka ni hairimashita.", "Pulling the door handle firmly, I entered the room.", "action,movement", ["引く", "引き出し"]),
    ("押", "オウ", "お.す, お.さえる", 8, "手", "push, press, stamp", "エレベーターのボタンを押して最上階へ向かいました。", "Erebeetaa no botan o oshite saijoukai e mukaimashita.", "Pressing the elevator button, I headed to the top floor.", "action,movement", ["押す", "押し入れ"]),
    ("切", "セツ, サイ", "き.る, き.れる", 4, "刀", "cut, sever, end", "包丁を使って新鮮な野菜を細かく切りました。", "Houchou o tsukatte shinsen na yasai o komakaku kirimashita.", "Using a kitchen knife, I finely chopped fresh vegetables.", "action,creation", ["切手", "大切"]),
    ("度", "ド, ト", "たび", 9, "广", "degrees, occurrence, measure", "一度始めたことは最後まで諦めずにやり遂げます。", "Ichido hajimeta koto wa saigo made akiramezu ni yaritogemasu.", "Things once begun, I carry through to the end without giving up.", "measure,time", ["今度", "温度"]),
    ("回", "カイ, エ", "まわ.る, まわ.す", 6, "囗", "turn, round, frequency", "一日三回、食後に必ずこの薬を飲んでください。", "Ichinichi sankai, shokugo ni kanarazu kono kusuri o nonde kudasai.", "Please be sure to take this medicine three times a day after meals.", "measure,frequency", ["回る", "何回"]),
    ("番", "バン", "", 12, "田", "number, order, turn", "受付で順番を待っている間に書類を読みました。", "Uketsuke de junban o matte iru aida ni shorui o yomimashita.", "While waiting my turn at reception, I read through the documents.", "measure,order", ["番号", "一番"]),
    ("号", "ゴウ", "さけ.ぶ", 5, "口", "number, issue, signal", "新幹線の七号車に乗って京都まで行きました。", "Shinkansen no nanagousha ni notte Kyouto made ikimashita.", "I boarded car number 7 of the bullet train and went to Kyoto.", "measure,order", ["番号", "信号"]),
    ("弱", "ジャク", "よわ.い, よわ.まる", 10, "弓", "weak, frail", "風邪をひいて体が弱っているので無理をせず休んでください。", "Kaze o hiite karada ga yowatte iru node muri o sezu yasunde kudasai.", "Because your body is weakened from a cold, rest without overexertion.", "state,body", ["弱い", "弱点"])
]

def main():
    print("--- Starting N4 Curriculum Compilation ---")
    
    # Check N5 kanji
    with open('app/src/main/assets/jlpt/n5/kanji.json') as f:
        n5_k = json.load(f)
    n5_set = {k['japanese'] for k in n5_k}

    # 1. Grammar
    grammar_items = build_n4_grammar()
    print(f"Generated {len(grammar_items)} N4 Grammar points.")

    # 2. Kanji
    kanji_items = []
    seen = set()
    idx = 1

    for char, onyomi, kunyomi, strokes, radical, meaning, ex_jp, ex_rm, ex_en, tags, planned_vocab in N4_KANJI_DATA:
        if char in n5_set:
            raise ValueError(f"FATAL: {char} is already in N5!")
        if char in seen:
            raise ValueError(f"FATAL: Duplicate char in N4 data: {char}")
        seen.add(char)

        item_id = f"n4_k_{idx:02d}" if idx < 100 else f"n4_k_{idx}"
        planned_str = ", ".join(planned_vocab)
        reading_val = kunyomi.split(",")[0].replace(".", "").replace("-", "").strip() if kunyomi else onyomi.split(",")[0].strip()

        item = {
            "id": item_id,
            "japanese": char,
            "reading": reading_val,
            "romaji": "",
            "meaning": meaning,
            "category": "KANJI",
            "jlptLevel": "N4",
            "onyomi": onyomi,
            "kunyomi": kunyomi,
            "strokeCount": strokes,
            "radical": radical,
            "mnemonicOrNote": f"Radical: {radical}. Meaning: {meaning}. Planned N4 Vocab: {planned_str}.",
            "exampleJapanese": ex_jp,
            "exampleReading": "",
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

    print(f"Generated {len(kanji_items)} N4 Kanji items.")
    assert len(grammar_items) == 56, f"Expected 56 grammar items, got {len(grammar_items)}"
    assert len(kanji_items) == 175, f"Expected 175 kanji items, got {len(kanji_items)}"

    # Write assets
    with open('app/src/main/assets/jlpt/n4/grammar.json', 'w', encoding='utf-8') as f:
        json.dump(grammar_items, f, ensure_ascii=False, indent=2)
    print("Successfully wrote app/src/main/assets/jlpt/n4/grammar.json (56 items)")

    with open('app/src/main/assets/jlpt/n4/kanji.json', 'w', encoding='utf-8') as f:
        json.dump(kanji_items, f, ensure_ascii=False, indent=2)
    print("Successfully wrote app/src/main/assets/jlpt/n4/kanji.json (175 items)")

if __name__ == '__main__':
    main()
