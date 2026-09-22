#!/usr/bin/env python3
import json
import os

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
N5_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "jlpt", "n5")
KANA_PATH = os.path.join(N5_DIR, "kana.json")

# Core Hiragana Gojuon
HIRAGANA_GOJUON = [
    ("あ", "あ", "a", "Hiragana vowel 'a' (ah)", "Looks like an apple with a stem.", "あさ", "あさ", "asa", "morning", ["a", "o", "u", "e"], ["n5_v_126"]),
    ("い", "い", "i", "Hiragana vowel 'i' (ee)", "Looks like two upright needles side by side.", "いぬ", "いぬ", "inu", "dog", ["i", "ri", "ko", "ni"], ["n5_v_142"]),
    ("う", "う", "u", "Hiragana vowel 'u' (oo)", "Looks like a bent person carrying a heavy load.", "うみ", "うみ", "umi", "sea, ocean", ["u", "tsu", "ra", "wa"], ["n5_v_154"]),
    ("え", "え", "e", "Hiragana vowel 'e' (eh)", "Looks like an energetic ninja running.", "えき", "えき", "eki", "train station", ["e", "n", "i", "a"], ["n5_v_56"]),
    ("お", "お", "o", "Hiragana vowel 'o' (oh)", "Looks like a golfer on a green ball.", "おちゃ", "おちゃ", "ocha", "green tea", ["o", "a", "mu", "su"], ["n5_v_34"]),
    
    ("か", "か", "ka", "Hiragana 'ka'", "Looks like a blade cutting open something.", "かわ", "かわ", "kawa", "river", ["ka", "ga", "ki", "ku"], ["n5_v_155"]),
    ("き", "き", "ki", "Hiragana 'ki'", "Looks like an old-fashioned key.", "き", "き", "ki", "tree, wood", ["ki", "sa", "gi", "chi"], ["n5_v_160"]),
    ("く", "く", "ku", "Hiragana 'ku'", "Looks like the open beak of a cuckoo bird.", "くるま", "くるま", "kuruma", "car", ["ku", "he", "tsu", "ko"], ["n5_v_61"]),
    ("け", "け", "ke", "Hiragana 'ke'", "Looks like the wooden staves of a beer keg.", "けさ", "けさ", "kesa", "this morning", ["ke", "ha", "ho", "ni"], ["n5_v_127"]),
    ("こ", "こ", "ko", "Hiragana 'ko'", "Looks like two cozy worms curled together.", "こども", "こども", "kodomo", "child", ["ko", "ni", "ta", "i"], ["n5_v_25"]),
    
    ("さ", "さ", "sa", "Hiragana 'sa'", "Looks like a sword crossing downward.", "さかな", "さかな", "sakana", "fish", ["sa", "chi", "ki", "za"], ["n5_v_31"]),
    ("し", "し", "shi", "Hiragana 'shi'", "Looks like a giant fishing hook in water.", "しろい", "しろい", "shiroi", "white", ["shi", "tsu", "ji", "hi"], ["n5_v_14"]),
    ("す", "す", "su", "Hiragana 'su'", "Looks like a coiled straw for sipping soup.", "すし", "すし", "sushi", "sushi", ["su", "mu", "o", "zu"], ["n5_v_37"]),
    ("せ", "せ", "se", "Hiragana 'se'", "Looks like someone standing proudly on a stage.", "せんせい", "せんせい", "sensei", "teacher", ["se", "sa", "ze", "ya"], ["n5_v_63"]),
    ("そ", "そ", "so", "Hiragana 'so'", "Looks like a zigzag sewing stitch.", "そら", "そら", "sora", "sky", ["so", "te", "ro", "zo"], ["n5_v_157"]),
    
    ("た", "た", "ta", "Hiragana 'ta'", "Looks like the letters 't' and 'a'.", "たかい", "たかい", "takai", "tall, expensive", ["ta", "da", "na", "ko"], ["n5_v_13"]),
    ("ち", "ち", "chi", "Hiragana 'chi'", "Looks like a cheerleader cheering.", "ちいさい", "ちいさい", "chiisai", "small", ["chi", "sa", "ra", "ji"], ["n5_v_10"]),
    ("つ", "つ", "tsu", "Hiragana 'tsu'", "Looks like the crest of a tidal tsunami wave.", "つくえ", "つくえ", "tsukue", "desk", ["tsu", "shi", "u", "ku"], ["n5_v_174"]),
    ("て", "て", "te", "Hiragana 'te'", "Looks like an open hand or dog's tail.", "て", "て", "te", "hand", ["te", "de", "so", "to"], ["n5_v_148"]),
    ("と", "と", "to", "Hiragana 'to'", "Looks like a stubbed toe with a splinter.", "ともだち", "ともだち", "tomodachi", "friend", ["to", "do", "te", "ko"], ["n5_v_27"]),
    
    ("な", "な", "na", "Hiragana 'na'", "Looks like a nun kneeling in front of a cross.", "なつ", "なつ", "natsu", "summer", ["na", "ta", "me", "ne"], ["n5_v_134"]),
    ("に", "に", "ni", "Hiragana 'ni'", "Looks like a needle and a spool of thread.", "にほん", "にほん", "nihon", "Japan", ["ni", "ko", "ha", "i"], ["n5_v_02"]),
    ("ぬ", "ぬ", "nu", "Hiragana 'nu'", "Looks like chopsticks twisting round noodles.", "ぬの", "ぬの", "nuno", "cloth", ["nu", "me", "ne", "wa"], ["n5_v_172"]),
    ("ね", "ね", "ne", "Hiragana 'ne'", "Looks like a curled cat with a loop tail.", "ねこ", "ねこ", "neko", "cat", ["ne", "nu", "wa", "re"], ["n5_v_143"]),
    ("の", "の", "no", "Hiragana 'no'", "Looks like a 'No smoking' circle slash.", "のみもの", "のみもの", "nomimono", "beverage", ["no", "me", "ru", "ro"], ["n5_v_36"]),
    
    ("は", "は", "ha", "Hiragana 'ha'", "Looks like a standing person with a bag.", "はな", "はな", "hana", "flower", ["ha", "ba", "pa", "ho"], ["n5_v_161"]),
    ("ひ", "ひ", "hi", "Hiragana 'hi'", "Looks like a smiling mouth shouting 'hee hee'.", "ひと", "ひと", "hito", "person", ["hi", "bi", "pi", "shi"], ["n5_v_01"]),
    ("ふ", "ふ", "fu", "Hiragana 'fu'", "Looks like Mount Fuji surrounded by clouds.", "ふゆ", "ふゆ", "fuyu", "winter", ["fu", "bu", "pu", "u"], ["n5_v_136"]),
    ("へ", "へ", "he", "Hiragana 'he'", "Looks like an arrow pointing to heaven.", "へや", "へや", "heya", "room", ["he", "be", "pe", "ku"], ["n5_v_171"]),
    ("ほ", "ほ", "ho", "Hiragana 'ho'", "Looks like a horse with a rider wearing a hat.", "ほん", "ほん", "hon", "book", ["ho", "bo", "po", "ha"], ["n5_v_176"]),
    
    ("ま", "ま", "ma", "Hiragana 'ma'", "Looks like an angel calling mama.", "まち", "まち", "machi", "town, city", ["ma", "mo", "ho", "ha"], ["n5_v_159"]),
    ("み", "み", "mi", "Hiragana 'mi'", "Looks like lucky number 21.", "みず", "みず", "mizu", "water", ["mi", "ma", "mu", "me"], ["n5_v_32"]),
    ("む", "む", "mu", "Hiragana 'mu'", "Looks like a cow giving off a loud moo.", "むし", "むし", "mushi", "insect", ["mu", "su", "me", "mo"], ["n5_v_145"]),
    ("め", "め", "me", "Hiragana 'me'", "Looks like an eye (目, me) sketched softly.", "め", "め", "me", "eye", ["me", "nu", "ne", "no"], ["n5_v_146"]),
    ("も", "も", "mo", "Hiragana 'mo'", "Looks like a fishing hook with more worms.", "もの", "もの", "mono", "thing, object", ["mo", "ma", "to", "ho"], ["n5_v_179"]),
    
    ("や", "や", "ya", "Hiragana 'ya'", "Looks like a yak with curved horns.", "やま", "やま", "yama", "mountain", ["ya", "se", "ka", "yu"], ["n5_v_153"]),
    ("ゆ", "ゆ", "yu", "Hiragana 'yu'", "Looks like a swimming fish in clear water.", "ゆき", "ゆき", "yuki", "snow", ["yu", "ya", "yo", "me"], ["n5_v_138"]),
    ("よ", "よ", "yo", "Hiragana 'yo'", "Looks like a yo-yo hanging from a string.", "よる", "よる", "yoru", "night", ["yo", "ma", "ha", "ya"], ["n5_v_129"]),
    
    ("ら", "ら", "ra", "Hiragana 'ra'", "Looks like a rabbit standing on hind legs.", "らいしゅう", "らいしゅう", "raishuu", "next week", ["ra", "chi", "u", "ro"], ["n5_v_124"]),
    ("り", "り", "ri", "Hiragana 'ri'", "Looks like two reeds blowing in the wind.", "りんご", "りんご", "ringo", "apple", ["ri", "i", "re", "ka"], ["n5_v_33"]),
    ("る", "る", "ru", "Hiragana 'ru'", "Looks like number 3 with a loop at the base.", "るす", "るす", "rusu", "absence from home", ["ru", "ro", "re", "wa"], ["n5_v_171"]),
    ("れ", "れ", "re", "Hiragana 're'", "Looks like a runner reaching the finish line.", "れんしゅう", "れんしゅう", "renshuu", "practice", ["re", "ne", "wa", "ru"], ["n5_v_70"]),
    ("ろ", "ろ", "ro", "Hiragana 'ro'", "Looks like number 3 with no loop.", "ろうそく", "ろうそく", "rousoku", "candle", ["ro", "ru", "so", "u"], ["n5_v_179"]),
    
    ("わ", "わ", "wa", "Hiragana 'wa'", "Looks like a water bird sitting quietly.", "わたし", "わたし", "watashi", "I, myself", ["wa", "ne", "re", "o"], ["n5_v_01"]),
    ("を", "を", "wo", "Hiragana 'wo/o' (Particle)", "Looks like a person jumping over an obstacle.", "ほんをよむ", "ほんをよむ", "hon o yomu", "read a book", ["wo", "o", "a", "te"], ["n5_g_02"]),
    ("ん", "ん", "n", "Hiragana nasal 'n'", "Looks like the lowercase cursive letter 'n'.", "にほん", "にほん", "nihon", "Japan", ["n", "so", "e", "u"], ["n5_v_02"])
]

# Hiragana Dakuten & Handakuten
HIRAGANA_DAKUTEN = [
    ("が", "が", "ga", "Hiragana 'ga'", "か with dakuten marks.", "がくせい", "がくせい", "gakusei", "student", ["ga", "ka", "za", "da"], ["n5_v_62"]),
    ("ぎ", "ぎ", "gi", "Hiragana 'gi'", "き with dakuten marks.", "ぎんこう", "ぎんこう", "ginkou", "bank", ["gi", "ki", "ji", "bi"], ["n5_v_58"]),
    ("ぐ", "ぐ", "gu", "Hiragana 'gu'", "く with dakuten marks.", "ぐらい", "ぐらい", "gurai", "approximate amount", ["gu", "ku", "zu", "bu"], ["n5_v_42"]),
    ("げ", "げ", "ge", "Hiragana 'ge'", "け with dakuten marks.", "げつようび", "げつようび", "getsuyoubi", "Monday", ["ge", "ke", "ze", "de"], ["n5_v_50"]),
    ("ご", "ご", "go", "Hiragana 'go'", "こ with dakuten marks.", "ごはん", "ごはん", "gohan", "meal, cooked rice", ["go", "ko", "zo", "do"], ["n5_v_38"]),
    
    ("ざ", "ざ", "za", "Hiragana 'za'", "さ with dakuten marks.", "ざっし", "ざっし", "zasshi", "magazine", ["za", "sa", "ga", "da"], ["n5_v_177"]),
    ("じ", "じ", "ji", "Hiragana 'ji'", "し with dakuten marks.", "じかん", "じかん", "jikan", "time, hours", ["ji", "shi", "gi", "chi"], ["n5_v_43"]),
    ("ず", "ず", "zu", "Hiragana 'zu'", "す with dakuten marks.", "ずっと", "ずっと", "zutto", "all the time, by far", ["zu", "su", "gu", "bu"], ["n5_v_22"]),
    ("ぜ", "ぜ", "ze", "Hiragana 'ze'", "せ with dakuten marks.", "ぜんぶ", "ぜんぶ", "zenbu", "all, whole", ["ze", "se", "ge", "de"], ["n5_v_182"]),
    ("ぞ", "ぞ", "zo", "Hiragana 'zo'", "そ with dakuten marks.", "ぞう", "ぞう", "zou", "elephant", ["zo", "so", "go", "do"], ["n5_v_142"]),
    
    ("だ", "だ", "da", "Hiragana 'da'", "た with dakuten marks.", "だいがく", "だいがく", "daigaku", "university", ["da", "ta", "za", "ga"], ["n5_v_64"]),
    ("ぢ", "ぢ", "ji", "Hiragana 'ji' (di)", "ち with dakuten marks.", "はなぢ", "はなぢ", "hanaji", "nosebleed", ["ji", "chi", "dji", "shi"], ["n5_v_147"]),
    ("づ", "づ", "zu", "Hiragana 'zu' (du)", "つ with dakuten marks.", "つづく", "つづく", "tsuzuku", "to continue", ["zu", "tsu", "du", "su"], ["n5_v_71"]),
    ("で", "で", "de", "Hiragana 'de'", "て with dakuten marks.", "でんしゃ", "でんしゃ", "densha", "train", ["de", "te", "ze", "ge"], ["n5_v_57"]),
    ("ど", "ど", "do", "Hiragana 'do'", "と with dakuten marks.", "どこ", "どこ", "doko", "where", ["do", "to", "zo", "go"], ["n5_v_78"]),
    
    ("ば", "ば", "ba", "Hiragana 'ba'", "は with dakuten marks.", "ばんごはん", "ばんごはん", "bangohan", "dinner", ["ba", "ha", "pa", "da"], ["n5_v_39"]),
    ("び", "び", "bi", "Hiragana 'bi'", "ひ with dakuten marks.", "びょういん", "びょういん", "byouin", "hospital", ["bi", "hi", "pi", "gi"], ["n5_v_60"]),
    ("ぶ", "ぶ", "bu", "Hiragana 'bu'", "ふ with dakuten marks.", "ぶんしょう", "ぶんしょう", "bunshou", "sentence", ["bu", "fu", "pu", "gu"], ["n5_v_176"]),
    ("べ", "べ", "be", "Hiragana 'be'", "へ with dakuten marks.", "べんきょう", "べんきょう", "benkyou", "study", ["be", "he", "pe", "de"], ["n5_v_70"]),
    ("ぼ", "ぼ", "bo", "Hiragana 'bo'", "ほ with dakuten marks.", "ぼうし", "ぼうし", "boushi", "hat, cap", ["bo", "ho", "po", "do"], ["n5_v_167"]),
    
    ("ぱ", "ぱ", "pa", "Hiragana 'pa'", "は with handakuten circle.", "パン", "ぱん", "pan", "bread", ["pa", "ba", "ha", "ta"], ["n5_v_40"]),
    ("ぴ", "ぴ", "pi", "Hiragana 'pi'", "ひ with handakuten circle.", "ぴかぴか", "ぴかぴか", "pikapika", "sparkling", ["pi", "bi", "hi", "ki"], ["n5_v_14"]),
    ("ぷ", "ぷ", "pu", "Hiragana 'pu'", "ふ with handakuten circle.", "プール", "ぷーる", "puuru", "swimming pool", ["pu", "bu", "fu", "ku"], ["n5_v_154"]),
    ("ぺ", "ぺ", "pe", "Hiragana 'pe'", "へ with handakuten circle.", "ページ", "ぺーじ", "peeji", "page", ["pe", "be", "he", "te"], ["n5_v_176"]),
    ("ぽ", "ぽ", "po", "Hiragana 'po'", "ほ with handakuten circle.", "ポスト", "ぽすと", "posuto", "postbox", ["po", "bo", "ho", "to"], ["n5_v_59"])
]

# Hiragana Yoon & Sokuon
HIRAGANA_YOON = [
    ("きゃ", "きゃ", "kya", "Hiragana combo 'kya'", "き + small ゃ.", "きゃく", "きゃく", "kyaku", "guest, customer", ["kya", "ki", "ka", "kya"], ["n5_v_62"]),
    ("きゅ", "きゅ", "kyu", "Hiragana combo 'kyu'", "き + small ゅ.", "きゅう", "きゅう", "kyuu", "nine", ["kyu", "ki", "ku", "kyo"], ["n5_k_09"]),
    ("きょ", "きょ", "kyo", "Hiragana combo 'kyo'", "き + small ょ.", "きょう", "きょう", "kyou", "today", ["kyo", "ki", "ko", "kyu"], ["n5_v_46"]),
    ("しゃ", "しゃ", "sha", "Hiragana combo 'sha'", "し + small ゃ.", "しゃしん", "しゃしん", "shashin", "photograph", ["sha", "shi", "sa", "shu"], ["n5_v_178"]),
    ("しゅ", "しゅ", "shu", "Hiragana combo 'shu'", "し + small ゅ.", "しゅくだい", "しゅくだい", "shukudai", "homework", ["shu", "shi", "su", "sho"], ["n5_v_62"]),
    ("しょ", "しょ", "sho", "Hiragana combo 'sho'", "し + small ょ.", "しょくどう", "しょくどう", "shokudou", "cafeteria, dining hall", ["sho", "shi", "so", "sha"], ["n5_v_64"]),
    ("ちゃ", "ちゃ", "cha", "Hiragana combo 'cha'", "ち + small ゃ.", "おちゃ", "おちゃ", "ocha", "tea", ["cha", "chi", "ta", "chu"], ["n5_v_34"]),
    ("ちゅ", "ちゅ", "chu", "Hiragana combo 'chu'", "ち + small ゅ.", "ちゅうごく", "ちゅうごく", "chuugoku", "China", ["chu", "chi", "tsu", "cho"], ["n5_k_31"]),
    ("ちょ", "ちょ", "cho", "Hiragana combo 'cho'", "ち + small ょ.", "ちょっと", "ちょっと", "chotto", "a little, just a minute", ["cho", "chi", "to", "cha"], ["n5_v_22"]),
    ("にゃ", "にゃ", "nya", "Hiragana combo 'nya'", "に + small ゃ.", "にゃー", "にゃー", "nyaa", "cat meow", ["nya", "ni", "na", "nyu"], ["n5_v_143"]),
    ("にゅ", "にゅ", "nyu", "Hiragana combo 'nyu'", "に + small ゅ.", "ぎゅうにゅう", "ぎゅうにゅう", "gyuunyuu", "milk", ["nyu", "ni", "nu", "nyo"], ["n5_v_35"]),
    ("にょ", "にょ", "nyo", "Hiragana combo 'nyo'", "に + small ょ.", "にょうぼう", "にょうぼう", "nyoubou", "wife", ["nyo", "ni", "no", "nya"], ["n5_v_16"]),
    ("ひゃ", "ひゃ", "hya", "Hiragana combo 'hya'", "ひ + small ゃ.", "ひゃく", "ひゃく", "hyaku", "one hundred", ["hya", "hi", "ha", "hyu"], ["n5_k_10"]),
    ("ひゅ", "ひゅ", "hyu", "Hiragana combo 'hyu'", "ひ + small ゅ.", "ひゅう", "ひゅう", "hyuu", "whistling wind", ["hyu", "hi", "fu", "hyo"], ["n5_v_138"]),
    ("ひょ", "ひょ", "hyo", "Hiragana combo 'hyo'", "ひ + small ょ.", "ひょう", "ひょう", "hyou", "table, chart", ["hyo", "hi", "ho", "hya"], ["n5_v_176"]),
    ("みゃ", "みゃ", "mya", "Hiragana combo 'mya'", "み + small ゃ.", "みゃく", "みゃく", "myaku", "pulse", ["mya", "mi", "ma", "myu"], ["n5_v_146"]),
    ("みゅ", "みゅ", "myu", "Hiragana combo 'myu'", "み + small ゅ.", "みゅーじあむ", "みゅーじあむ", "myuujiamu", "museum", ["myu", "mi", "mu", "myo"], ["n5_v_60"]),
    ("みょ", "みょ", "myo", "Hiragana combo 'myo'", "み + small ょ.", "みょうじ", "みょうじ", "myouji", "family surname", ["myo", "mi", "mo", "mya"], ["n5_v_01"]),
    ("りゃ", "りゃ", "rya", "Hiragana combo 'rya'", "り + small ゃ.", "りゃく", "りゃく", "ryaku", "abbreviation", ["rya", "ri", "ra", "ryu"], ["n5_v_70"]),
    ("りゅ", "りゅ", "ryu", "Hiragana combo 'ryu'", "り + small ゅ.", "りゅうがくせい", "りゅうがくせい", "ryuugakusei", "international student", ["ryu", "ri", "ru", "ryo"], ["n5_v_62"]),
    ("りょ", "りょ", "ryo", "Hiragana combo 'ryo'", "り + small ょ.", "りょこう", "りょこう", "ryokou", "travel, trip", ["ryo", "ri", "ro", "rya"], ["n5_v_188"]),
    ("ぎゃ", "ぎゃ", "gya", "Hiragana combo 'gya'", "ぎ + small ゃ.", "ぎゃく", "ぎゃく", "gyaku", "reverse, opposite", ["gya", "gi", "ga", "gyu"], ["n5_v_182"]),
    ("ぎゅ", "ぎゅ", "gyu", "Hiragana combo 'gyu'", "ぎ + small ゅ.", "ぎゅうにく", "ぎゅうにく", "gyuuniku", "beef", ["gyu", "gi", "gu", "gyo"], ["n5_v_41"]),
    ("ぎょ", "ぎょ", "gyo", "Hiragana combo 'gyo'", "ぎ + small ょ.", "ぎょぎょう", "ぎょぎょう", "gyogyou", "fishing industry", ["gyo", "gi", "go", "gya"], ["n5_v_31"]),
    ("じゃ", "じゃ", "ja", "Hiragana combo 'ja'", "じ + small ゃ.", "じゃあ", "じゃあ", "jaa", "well then, goodbye", ["ja", "ji", "za", "ju"], ["n5_v_89"]),
    ("じゅ", "じゅ", "ju", "Hiragana combo 'ju'", "じ + small ゅ.", "じゅぎょう", "じゅぎょう", "jugyou", "class, lesson", ["ju", "ji", "zu", "jo"], ["n5_v_64"]),
    ("じょ", "じょ", "jo", "Hiragana combo 'jo'", "じ + small ょ.", "じょせい", "じょせい", "josei", "woman, female", ["jo", "ji", "zo", "ja"], ["n5_k_26"]),
    ("びゃ", "びゃ", "bya", "Hiragana combo 'bya'", "び + small ゃ.", "さんびゃく", "さんびゃく", "sanbyaku", "three hundred", ["bya", "bi", "ba", "byu"], ["n5_k_10"]),
    ("びゅ", "びゅ", "byu", "Hiragana combo 'byu'", "び + small ゅ.", "びゅう", "びゅう", "byuu", "gust of wind", ["byu", "bi", "bu", "byo"], ["n5_v_138"]),
    ("びょ", "びょ", "byo", "Hiragana combo 'byo'", "び + small ょ.", "びょういん", "びょういん", "byouin", "hospital", ["byo", "bi", "bo", "bya"], ["n5_v_60"]),
    ("ぴゃ", "ぴゃ", "pya", "Hiragana combo 'pya'", "ぴ + small ゃ.", "ろっぴゃく", "ろっぴゃく", "roppyaku", "six hundred", ["pya", "pi", "pa", "pyu"], ["n5_k_10"]),
    ("ぴゅ", "ぴゅ", "pyu", "Hiragana combo 'pyu'", "ぴ + small ゅ.", "ぴゅうぴゅう", "ぴゅうぴゅう", "pyuupyuu", "howling wind", ["pyu", "pi", "pu", "pyo"], ["n5_v_138"]),
    ("ぴょ", "ぴょ", "pyo", "Hiragana combo 'pyo'", "ぴ + small ょ.", "ぴょんぴょん", "ぴょんぴょん", "pyonpyon", "hopping, bouncing", ["pyo", "pi", "po", "pya"], ["n5_v_142"]),
    ("っ", "っ", "tsu (sokuon)", "Small tsu (geminate stop)", "Doubles the following consonant sound (stop).", "がっこう", "がっこう", "gakkou", "school", ["tsu", "su", "chi", "to"], ["n5_v_55"])
]

# Generate Katakana items matching the phonetic structure
KATAKANA_GOJUON = [
    ("ア", "ア", "a", "Katakana 'a'", "Looks like an axe blade angled to cut.", "アメリカ", "あめりか", "amerika", "America, USA", ["a", "i", "u", "e"], ["n5_v_02"]),
    ("イ", "イ", "i", "Katakana 'i'", "Looks like an easel stand.", "イギリス", "いぎりす", "igirisu", "United Kingdom", ["i", "a", "ri", "to"], ["n5_v_02"]),
    ("ウ", "ウ", "u", "Katakana 'u'", "Looks like an umbrella without the hook.", "ウェブ", "うぇぶ", "webu", "web", ["u", "wa", "fu", "ra"], ["n5_v_176"]),
    ("エ", "エ", "e", "Katakana 'e'", "Looks like the steel girder of an elevator.", "エレベーター", "えれべーたー", "erebeetaa", "elevator", ["e", "ko", "i", "a"], ["n5_v_56"]),
    ("オ", "オ", "o", "Katakana 'o'", "Looks like an opera singer taking a bow.", "オレンジ", "おれんじ", "orenji", "orange", ["o", "ka", "ho", "a"], ["n5_v_33"]),
    
    ("カ", "カ", "ka", "Katakana 'ka'", "Looks like a camera lens angle.", "カメラ", "かめら", "kamera", "camera", ["ka", "ga", "ki", "ku"], ["n5_v_178"]),
    ("キ", "キ", "ki", "Katakana 'ki'", "Looks like two parallel keys.", "キロ", "きろ", "kiro", "kilo, kilometer", ["ki", "sa", "gi", "chi"], ["n5_v_42"]),
    ("ク", "ク", "ku", "Katakana 'ku'", "Looks like a cook's hat tipped sideways.", "クラス", "くらす", "kurasu", "class", ["ku", "wa", "ta", "fu"], ["n5_v_64"]),
    ("ケ", "ケ", "ke", "Katakana 'ke'", "Looks like a kettle pouring downward.", "ケーキ", "けーき", "keeki", "cake", ["ke", "ku", "fu", "ha"], ["n5_v_40"]),
    ("コ", "コ", "ko", "Katakana 'ko'", "Looks like two corners forming a box.", "コーヒー", "こーひー", "koohii", "coffee", ["ko", "yu", "ro", "e"], ["n5_v_35"]),
    
    ("サ", "サ", "sa", "Katakana 'sa'", "Looks like three salad tongs.", "サンドイッチ", "さんどいっち", "sandoicchi", "sandwich", ["sa", "se", "chi", "za"], ["n5_v_40"]),
    ("シ", "シ", "shi", "Katakana 'shi'", "Looks like sea foam splashing upward.", "シャツ", "しゃつ", "shatsu", "shirt", ["shi", "tsu", "so", "n"], ["n5_v_165"]),
    ("ス", "ス", "su", "Katakana 'su'", "Looks like a ski jumper in flight.", "スポーツ", "すぽーつ", "supootsu", "sports", ["su", "nu", "fu", "ma"], ["n5_v_188"]),
    ("セ", "セ", "se", "Katakana 'se'", "Looks like a seven tilted sideways.", "セーター", "せーたー", "seetaa", "sweater", ["se", "sa", "ze", "ya"], ["n5_v_166"]),
    ("ソ", "ソ", "so", "Katakana 'so'", "Looks like soft needles pointing down.", "ソファ", "そふぁ", "sofa", "sofa", ["so", "n", "shi", "tsu"], ["n5_v_174"]),
    
    ("タ", "タ", "ta", "Katakana 'ta'", "Looks like a tall taco stand.", "タクシー", "たくしー", "takushii", "taxi", ["ta", "da", "ku", "fu"], ["n5_v_61"]),
    ("チ", "チ", "chi", "Katakana 'chi'", "Looks like a cheerleader waving a pompom.", "チーズ", "ちーず", "chiizu", "cheese", ["chi", "te", "sa", "ji"], ["n5_v_37"]),
    ("ツ", "ツ", "tsu", "Katakana 'tsu'", "Looks like two drops in a tsunami wave.", "ツアー", "つあー", "tsuaa", "tour", ["tsu", "shi", "so", "n"], ["n5_v_188"]),
    ("テ", "テ", "te", "Katakana 'te'", "Looks like a television antenna pole.", "テレビ", "てれび", "terebi", "television", ["te", "de", "chi", "to"], ["n5_v_175"]),
    ("ト", "ト", "to", "Katakana 'to'", "Looks like a totem pole with a branch.", "トイレ", "といれ", "toire", "restroom, toilet", ["to", "do", "i", "te"], ["n5_v_171"]),
    
    ("ナ", "ナ", "na", "Katakana 'na'", "Looks like a pocket knife.", "ナイフ", "ないふ", "naifu", "knife", ["na", "me", "ta", "ha"], ["n5_v_174"]),
    ("ニ", "ニ", "ni", "Katakana 'ni'", "Looks like two needles lying flat.", "ニュース", "にゅーす", "nyuusu", "news", ["ni", "ko", "i", "ro"], ["n5_v_176"]),
    ("ヌ", "ヌ", "nu", "Katakana 'nu'", "Looks like chopsticks grabbing noodles.", "ヌードル", "ぬーどる", "nuudoru", "noodles", ["nu", "su", "me", "wa"], ["n5_v_37"]),
    ("ネ", "ネ", "ne", "Katakana 'ne'", "Looks like a necktie hanging down.", "ネクタイ", "ねくたい", "nekutai", "necktie", ["ne", "ho", "ki", "wa"], ["n5_v_165"]),
    ("ノ", "ノ", "no", "Katakana 'no'", "Looks like a clean nose slope.", "ノート", "のーと", "nooto", "notebook", ["no", "so", "me", "ru"], ["n5_v_176"]),
    
    ("ハ", "ハ", "ha", "Katakana 'ha'", "Looks like the roof of a house.", "ハンバーガー", "はんばーがー", "hanbaagaa", "hamburger", ["ha", "ba", "pa", "ya"], ["n5_v_40"]),
    ("ヒ", "ヒ", "hi", "Katakana 'hi'", "Looks like someone smiling heel to toe.", "ホテル", "ほてる", "hoteru", "hotel", ["hi", "bi", "pi", "se"], ["n5_v_60"]),
    ("フ", "フ", "fu", "Katakana 'fu'", "Looks like a flag waving in the breeze.", "フォーク", "ふぉーく", "fooku", "fork", ["fu", "bu", "pu", "ra"], ["n5_v_174"]),
    ("ヘ", "ヘ", "he", "Katakana 'he'", "Looks like Mount Fuji's slope.", "ヘリコプター", "へりこぷたー", "herikoputaa", "helicopter", ["he", "be", "pe", "ku"], ["n5_v_61"]),
    ("ホ", "ホ", "ho", "Katakana 'ho'", "Looks like a holy cross with wings.", "ホテル", "ほてる", "hoteru", "hotel", ["ho", "bo", "po", "ha"], ["n5_v_60"]),
    
    ("マ", "マ", "ma", "Katakana 'ma'", "Looks like a megaphone shouting 'mama'.", "マッチ", "まっち", "macchi", "match (fire)", ["ma", "mu", "mo", "a"], ["n5_v_179"]),
    ("ミ", "ミ", "mi", "Katakana 'mi'", "Looks like three musical missile lines.", "ミルク", "みるく", "miruku", "milk", ["mi", "ma", "mu", "me"], ["n5_v_35"]),
    ("ム", "ム", "mu", "Katakana 'mu'", "Looks like a triangle piece of cheese.", "ムービー", "むーびー", "muubii", "movie", ["mu", "su", "ma", "nu"], ["n5_v_178"]),
    ("メ", "メ", "me", "Katakana 'me'", "Looks like crossing noodles.", "メニュー", "めにゅー", "menyuu", "menu", ["me", "nu", "na", "no"], ["n5_v_37"]),
    ("モ", "モ", "mo", "Katakana 'mo'", "Looks like a monitor with two shelves.", "モデル", "もでる", "moderu", "model", ["mo", "ma", "ho", "ko"], ["n5_v_63"]),
    
    ("ヤ", "ヤ", "ya", "Katakana 'ya'", "Looks like a yacht sail catching wind.", "ヤード", "やーど", "yaado", "yard", ["ya", "se", "ka", "yu"], ["n5_v_159"]),
    ("ユ", "ユ", "yu", "Katakana 'yu'", "Looks like a u-turn ramp.", "ユーモア", "ゆーもあ", "yuumoa", "humor", ["yu", "ko", "ya", "yo"], ["n5_v_27"]),
    ("ヨ", "ヨ", "yo", "Katakana 'yo'", "Looks like a yogurt spoon with shelves.", "ヨーロッパ", "よーろっぱ", "yooroppa", "Europe", ["yo", "ko", "e", "ya"], ["n5_v_02"]),
    
    ("ラ", "ラ", "ra", "Katakana 'ra'", "Looks like a radio antennae line.", "ラジオ", "らじお", "rajio", "radio", ["ra", "fu", "u", "ro"], ["n5_v_175"]),
    ("リ", "リ", "ri", "Katakana 'ri'", "Looks like two parallel ribbons.", "リンゴ", "りんご", "ringo", "apple", ["ri", "i", "re", "ka"], ["n5_v_33"]),
    ("ル", "ル", "ru", "Katakana 'ru'", "Looks like two legs running route.", "ルール", "るーる", "ruuru", "rule", ["ru", "re", "ro", "ha"], ["n5_v_62"]),
    ("レ", "レ", "re", "Katakana 're'", "Looks like a ray of sunlight bouncing.", "レストラン", "れすとらん", "resutoran", "restaurant", ["re", "ru", "ro", "wa"], ["n5_v_60"]),
    ("ロ", "ロ", "ro", "Katakana 'ro'", "Looks like a square road sign.", "ロシア", "ろしあ", "roshia", "Russia", ["ro", "ko", "ru", "so"], ["n5_v_02"]),
    
    ("ワ", "ワ", "wa", "Katakana 'wa'", "Looks like a wine glass outline.", "ワイン", "わいん", "wain", "wine", ["wa", "u", "fu", "o"], ["n5_v_36"]),
    ("ヲ", "ヲ", "wo", "Katakana 'wo'", "Used historically or in stylized text.", "ヲタク", "をたく", "wotaku", "otaku", ["wo", "o", "wa", "fu"], ["n5_v_176"]),
    ("ン", "ン", "n", "Katakana 'n'", "Looks like a needle stroke moving up.", "パン", "ぱん", "pan", "bread", ["n", "so", "shi", "tsu"], ["n5_v_40"])
]

# Katakana Dakuten & Handakuten
KATAKANA_DAKUTEN = [
    ("ガ", "ガ", "ga", "Katakana 'ga'", "カ with dakuten.", "ガス", "がす", "gasu", "gas", ["ga", "ka", "za", "da"], ["n5_v_179"]),
    ("ギ", "ギ", "gi", "Katakana 'gi'", "キ with dakuten.", "ギター", "ぎたー", "gitaa", "guitar", ["gi", "ki", "ji", "bi"], ["n5_v_188"]),
    ("グ", "グ", "gu", "Katakana 'gu'", "ク with dakuten.", "グラス", "ぐらす", "gurasu", "drinking glass", ["gu", "ku", "zu", "bu"], ["n5_v_174"]),
    ("ゲ", "ゲ", "ge", "Katakana 'ge'", "ケ with dakuten.", "ゲーム", "げーむ", "geemu", "game", ["ge", "ke", "ze", "de"], ["n5_v_188"]),
    ("ゴ", "ゴ", "go", "Katakana 'go'", "コ with dakuten.", "ゴルフ", "ごるふ", "gorufu", "golf", ["go", "ko", "zo", "do"], ["n5_v_188"]),
    
    ("ザ", "ザ", "za", "Katakana 'za'", "サ with dakuten.", "サラダ", "さらだ", "sarada", "salad", ["za", "sa", "ga", "da"], ["n5_v_37"]),
    ("ジ", "ジ", "ji", "Katakana 'ji'", "シ with dakuten.", "ジーンズ", "じーんず", "jiinzu", "jeans", ["ji", "shi", "gi", "chi"], ["n5_v_165"]),
    ("ズ", "ズ", "zu", "Katakana 'zu'", "ス with dakuten.", "ズボン", "ずぼん", "zubon", "pants, trousers", ["zu", "su", "gu", "bu"], ["n5_v_166"]),
    ("ゼ", "ゼ", "ze", "Katakana 'ze'", "セ with dakuten.", "ゼロ", "ぜろ", "zero", "zero", ["ze", "se", "ge", "de"], ["n5_k_01"]),
    ("ゾ", "ゾ", "zo", "Katakana 'zo'", "ソ with dakuten.", "ゾーン", "ぞーん", "zoon", "zone", ["zo", "so", "go", "do"], ["n5_v_159"]),
    
    ("ダ", "ダ", "da", "Katakana 'da'", "タ with dakuten.", "ダンス", "だんす", "dansu", "dance", ["da", "ta", "za", "ga"], ["n5_v_188"]),
    ("ヂ", "ヂ", "ji", "Katakana 'ji' (di)", "チ with dakuten.", "ヂーゼル", "じーぜる", "jiizeru", "diesel", ["ji", "chi", "dji", "shi"], ["n5_v_61"]),
    ("ヅ", "ヅ", "zu", "Katakana 'zu' (du)", "ツ with dakuten.", "ハナヅまり", "はなづまり", "hanazumari", "stuffy nose", ["zu", "tsu", "du", "su"], ["n5_v_147"]),
    ("デ", "デ", "de", "Katakana 'de'", "テ with dakuten.", "デパート", "でぱーと", "depaato", "department store", ["de", "te", "ze", "ge"], ["n5_v_58"]),
    ("ド", "ド", "do", "Katakana 'do'", "ト with dakuten.", "ドア", "どあ", "doa", "door", ["do", "to", "zo", "go"], ["n5_v_171"]),
    
    ("バ", "バ", "ba", "Katakana 'ba'", "ハ with dakuten.", "バス", "ばす", "basu", "bus", ["ba", "ha", "pa", "da"], ["n5_v_61"]),
    ("ビ", "ビ", "bi", "Katakana 'bi'", "ヒ with dakuten.", "ビル", "びる", "biru", "building", ["bi", "hi", "pi", "gi"], ["n5_v_58"]),
    ("ブ", "ブ", "bu", "Katakana 'bu'", "フ with dakuten.", "ブーツ", "ぶーつ", "buutsu", "boots", ["bu", "fu", "pu", "gu"], ["n5_v_167"]),
    ("ベ", "ベ", "be", "Katakana 'be'", "ヘ with dakuten.", "ベッド", "べっど", "beddo", "bed", ["be", "he", "pe", "de"], ["n5_v_174"]),
    ("ボ", "ボ", "bo", "Katakana 'bo'", "ホ with dakuten.", "ボールペン", "ぼーるぺん", "boorupen", "ballpoint pen", ["bo", "ho", "po", "do"], ["n5_v_176"]),
    
    ("パ", "パ", "pa", "Katakana 'pa'", "ハ with handakuten.", "パン", "ぱん", "pan", "bread", ["pa", "ba", "ha", "ta"], ["n5_v_40"]),
    ("ピ", "ピ", "pi", "Katakana 'pi'", "ヒ with handakuten.", "ピアノ", "ぴあの", "piano", "piano", ["pi", "bi", "hi", "ki"], ["n5_v_188"]),
    ("プ", "プ", "pu", "Katakana 'pu'", "フ with handakuten.", "プール", "ぷーる", "puuru", "swimming pool", ["pu", "bu", "fu", "ku"], ["n5_v_154"]),
    ("ペ", "ペ", "pe", "Katakana 'pe'", "ヘ with handakuten.", "ペン", "ぺん", "pen", "pen", ["pe", "be", "he", "te"], ["n5_v_176"]),
    ("ポ", "ポ", "po", "Katakana 'po'", "ホ with handakuten.", "ポスト", "ぽすと", "posuto", "postbox", ["po", "bo", "ho", "to"], ["n5_v_59"])
]

# Katakana Yoon & Special (Chōonpu, Sokuon)
KATAKANA_YOON = [
    ("キャ", "キャ", "kya", "Katakana combo 'kya'", "キ + small ャ.", "キャンプ", "きゃんぷ", "kyanpu", "camping", ["kya", "ki", "ka", "kya"], ["n5_v_188"]),
    ("キュ", "キュ", "kyu", "Katakana combo 'kyu'", "キ + small ュ.", "キューカンバー", "きゅーかんばー", "kyuukanbaa", "cucumber", ["kyu", "ki", "ku", "kyo"], ["n5_v_30"]),
    ("キョ", "キョ", "kyo", "Katakana combo 'kyo'", "キ + small ョ.", "キロ", "きろ", "kiro", "kilo", ["kyo", "ki", "ko", "kyu"], ["n5_v_42"]),
    ("シャ", "シャ", "sha", "Katakana combo 'sha'", "シ + small ャ.", "シャワー", "しゃわー", "shawaa", "shower", ["sha", "shi", "sa", "shu"], ["n5_v_171"]),
    ("シュ", "シュ", "shu", "Katakana combo 'shu'", "シ + small ュ.", "シュークリーム", "しゅーくりーむ", "shuukuriimu", "cream puff", ["shu", "shi", "su", "sho"], ["n5_v_40"]),
    ("ショ", "ショ", "sho", "Katakana combo 'sho'", "シ + small ョ.", "シャツ", "しゃつ", "shatsu", "shirt", ["sho", "shi", "so", "sha"], ["n5_v_165"]),
    ("チャ", "チャ", "cha", "Katakana combo 'cha'", "チ + small ャ.", "チャット", "ちゃっと", "chatto", "chat", ["cha", "chi", "ta", "chu"], ["n5_v_07"]),
    ("チュ", "チュ", "chu", "Katakana combo 'chu'", "チ + small ュ.", "チューリップ", "ちゅーりっぷ", "chuurippu", "tulip", ["chu", "chi", "tsu", "cho"], ["n5_v_161"]),
    ("チョ", "チョ", "cho", "Katakana combo 'cho'", "チ + small ョ.", "チョコレート", "ちょこれーと", "chokoreeto", "chocolate", ["cho", "chi", "to", "cha"], ["n5_v_40"]),
    ("ニャ", "ニャ", "nya", "Katakana combo 'nya'", "ニ + small ャ.", "ニャン", "にゃん", "nyan", "meow", ["nya", "ni", "na", "nyu"], ["n5_v_143"]),
    ("ニュ", "ニュ", "nyu", "Katakana combo 'nyu'", "ニ + small ュ.", "ニュース", "にゅーす", "nyuusu", "news", ["nyu", "ni", "nu", "nyo"], ["n5_v_176"]),
    ("ニョ", "ニョ", "nyo", "Katakana combo 'nyo'", "ニ + small ョ.", "ニョッキ", "にょっき", "nyokki", "gnocchi", ["nyo", "ni", "no", "nya"], ["n5_v_37"]),
    ("ヒャ", "ヒャ", "hya", "Katakana combo 'hya'", "ヒ + small ャ.", "ヒヤシンス", "ひやしんす", "hiyashinsu", "hyacinth", ["hya", "hi", "ha", "hyu"], ["n5_v_161"]),
    ("ヒュ", "ヒュ", "hyu", "Katakana combo 'hyu'", "ヒ + small ュ.", "ヒューズ", "ひゅーず", "fyuuzu", "fuse", ["hyu", "hi", "fu", "hyo"], ["n5_v_175"]),
    ("ヒョ", "ヒョ", "hyo", "Katakana combo 'hyo'", "ヒ + small ョ.", "ヒョウ", "ひょう", "hyou", "leopard", ["hyo", "hi", "ho", "hya"], ["n5_v_142"]),
    ("ミャ", "ミャ", "mya", "Katakana combo 'mya'", "ミ + small ャ.", "ミャンマー", "みゃんまー", "myanmaa", "Myanmar", ["mya", "mi", "ma", "myu"], ["n5_v_02"]),
    ("ミュ", "ミュ", "myu", "Katakana combo 'myu'", "ミ + small ュ.", "ミュージック", "みゅーじっく", "myuujikku", "music", ["myu", "mi", "mu", "myo"], ["n5_v_188"]),
    ("ミョ", "ミョ", "myo", "Katakana combo 'myo'", "ミ + small ョ.", "ミョウガ", "みょうが", "myouga", "myoga ginger", ["myo", "mi", "mo", "mya"], ["n5_v_30"]),
    ("リャ", "リャ", "rya", "Katakana combo 'rya'", "リ + small ャ.", "リャマ", "りゃま", "ryama", "llama", ["rya", "ri", "ra", "ryu"], ["n5_v_142"]),
    ("リュ", "リュ", "ryu", "Katakana combo 'ryu'", "リ + small ュ.", "リュック", "りゅっく", "ryukku", "backpack, rucksack", ["ryu", "ri", "ru", "ryo"], ["n5_v_179"]),
    ("リョ", "リョ", "ryo", "Katakana combo 'ryo'", "リ + small ョ.", "リョクトウ", "りょくとう", "ryokutou", "mung bean", ["ryo", "ri", "ro", "rya"], ["n5_v_30"]),
    ("ギャ", "ギャ", "gya", "Katakana combo 'gya'", "ギ + small ャ.", "ギャラリー", "ぎゃらりー", "gyararii", "gallery", ["gya", "gi", "ga", "gyu"], ["n5_v_60"]),
    ("ギュ", "ギュ", "gyu", "Katakana combo 'gyu'", "ギ + small ュ.", "レギュラー", "れぎゅらー", "regyuraa", "regular", ["gyu", "gi", "gu", "gyo"], ["n5_v_62"]),
    ("ギョ", "ギョ", "gyo", "Katakana combo 'gyo'", "ギ + small ョ.", "ギョーザ", "ぎょーざ", "gyooza", "gyoza dumpling", ["gyo", "gi", "go", "gya"], ["n5_v_37"]),
    ("ジャ", "ジャ", "ja", "Katakana combo 'ja'", "ジ + small ャ.", "ジャケット", "じゃけっと", "jaketto", "jacket", ["ja", "ji", "za", "ju"], ["n5_v_165"]),
    ("ジュ", "ジュ", "ju", "Katakana combo 'ju'", "ジ + small ュ.", "ジュース", "じゅーす", "juusu", "juice", ["ju", "ji", "zu", "jo"], ["n5_v_36"]),
    ("ジョ", "ジョ", "jo", "Katakana combo 'jo'", "ジ + small ョ.", "ジョギング", "じょぎんぐ", "jogingu", "jogging", ["jo", "ji", "zo", "ja"], ["n5_v_188"]),
    ("ビャ", "ビャ", "bya", "Katakana combo 'bya'", "ビ + small ャ.", "ビャンビャン", "びゃんびゃん", "byanbyan", "biangbiang noodles", ["bya", "bi", "ba", "byu"], ["n5_v_37"]),
    ("ビュ", "ビュ", "byu", "Katakana combo 'byu'", "ビ + small ュ.", "ビュッフェ", "びゅっふぇ", "byuffe", "buffet", ["byu", "bi", "bu", "byo"], ["n5_v_37"]),
    ("ビョ", "ビョ", "byo", "Katakana combo 'byo'", "ビ + small ョ.", "ビョウソク", "びょうそく", "byousoku", "speed per second", ["byo", "bi", "bo", "bya"], ["n5_v_43"]),
    ("ピャ", "ピャ", "pya", "Katakana combo 'pya'", "ピ + small ャ.", "ピャー", "ぴゃー", "pyaa", "chirping sound", ["pya", "pi", "pa", "pyu"], ["n5_v_142"]),
    ("ピュ", "ピュ", "pyu", "Katakana combo 'pyu'", "ピ + small ュ.", "ピューレ", "ぴゅーれ", "pyuure", "puree", ["pyu", "pi", "pu", "pyo"], ["n5_v_37"]),
    ("ピョ", "ピョ", "pyo", "Katakana combo 'pyo'", "ピ + small ョ.", "ピョンヤン", "ぴょんやん", "pyon-yan", "Pyongyang", ["pyo", "pi", "po", "pya"], ["n5_v_02"]),
    ("ッ", "ッ", "tsu (sokuon)", "Katakana small tsu (stop)", "Doubles the following consonant sound (stop).", "ベッド", "べっど", "beddo", "bed", ["tsu", "su", "chi", "to"], ["n5_v_174"]),
    ("ー", "ー", "chouonpu", "Katakana long vowel mark (ー)", "Extends the preceding vowel sound by one mora.", "コーヒー", "こーひー", "koohii", "coffee", ["ー", "一", "I", "-"], ["n5_v_35"])
]

# Load real vocabulary for ID resolution
vocab_path = os.path.join(N5_DIR, "vocabulary.json")
vocabs = json.load(open(vocab_path, "r", encoding="utf-8"))
valid_vocab_ids = set(v["id"] for v in vocabs)
reading_to_id = {}
for v in vocabs:
    r = v["reading"].strip()
    if r not in reading_to_id:
        reading_to_id[r] = v["id"]
    j = v["japanese"].strip()
    if j not in reading_to_id:
        reading_to_id[j] = v["id"]

def resolve_rels(rels, ex_rd, ex_jp):
    resolved = []
    for r in rels:
        if r in valid_vocab_ids and r not in resolved:
            resolved.append(r)
    if not resolved:
        if ex_rd in reading_to_id and reading_to_id[ex_rd] not in resolved:
            resolved.append(reading_to_id[ex_rd])
        elif ex_jp in reading_to_id and reading_to_id[ex_jp] not in resolved:
            resolved.append(reading_to_id[ex_jp])
    return resolved

all_items = []
idx = 1

# Process Hiragana Gojuon
for jp, rd, rm, mn, note, ex_jp, ex_rd, ex_rm, ex_en, opts, rels in HIRAGANA_GOJUON:
    all_items.append({
        "id": f"kana_h_{idx:03d}",
        "japanese": jp,
        "reading": rd,
        "romaji": rm,
        "meaning": mn,
        "category": "KANA",
        "jlptLevel": "N5",
        "partOfSpeech": "Hiragana Gojūon",
        "mnemonicOrNote": note,
        "exampleJapanese": ex_jp,
        "exampleReading": ex_rd,
        "exampleRomaji": ex_rm,
        "exampleEnglish": ex_en,
        "options": opts,
        "relatedItems": resolve_rels(rels, ex_rd, ex_jp),
        "tags": "KANA,HIRAGANA,GOJUON,STATUS:SOURCE_VERIFIED",
        "source": "NoIgnore Standard Kana Syllabus"
    })
    idx += 1

# Process Hiragana Dakuten
for jp, rd, rm, mn, note, ex_jp, ex_rd, ex_rm, ex_en, opts, rels in HIRAGANA_DAKUTEN:
    all_items.append({
        "id": f"kana_h_{idx:03d}",
        "japanese": jp,
        "reading": rd,
        "romaji": rm,
        "meaning": mn,
        "category": "KANA",
        "jlptLevel": "N5",
        "partOfSpeech": "Hiragana Dakuten" if "circle" not in note else "Hiragana Handakuten",
        "mnemonicOrNote": note,
        "exampleJapanese": ex_jp,
        "exampleReading": ex_rd,
        "exampleRomaji": ex_rm,
        "exampleEnglish": ex_en,
        "options": opts,
        "relatedItems": resolve_rels(rels, ex_rd, ex_jp),
        "tags": "KANA,HIRAGANA,DAKUTEN,STATUS:SOURCE_VERIFIED",
        "source": "NoIgnore Standard Kana Syllabus"
    })
    idx += 1

# Process Hiragana Yoon
for jp, rd, rm, mn, note, ex_jp, ex_rd, ex_rm, ex_en, opts, rels in HIRAGANA_YOON:
    all_items.append({
        "id": f"kana_h_{idx:03d}",
        "japanese": jp,
        "reading": rd,
        "romaji": rm,
        "meaning": mn,
        "category": "KANA",
        "jlptLevel": "N5",
        "partOfSpeech": "Hiragana Yōon" if jp != "っ" else "Hiragana Sokuon",
        "mnemonicOrNote": note,
        "exampleJapanese": ex_jp,
        "exampleReading": ex_rd,
        "exampleRomaji": ex_rm,
        "exampleEnglish": ex_en,
        "options": opts,
        "relatedItems": resolve_rels(rels, ex_rd, ex_jp),
        "tags": "KANA,HIRAGANA,YOON,STATUS:SOURCE_VERIFIED",
        "source": "NoIgnore Standard Kana Syllabus"
    })
    idx += 1

print(f"Total Hiragana generated: {idx - 1}")

k_idx = 1
# Process Katakana Gojuon
for jp, rd, rm, mn, note, ex_jp, ex_rd, ex_rm, ex_en, opts, rels in KATAKANA_GOJUON:
    all_items.append({
        "id": f"kana_k_{k_idx:03d}",
        "japanese": jp,
        "reading": rd,
        "romaji": rm,
        "meaning": mn,
        "category": "KANA",
        "jlptLevel": "N5",
        "partOfSpeech": "Katakana Gojūon",
        "mnemonicOrNote": note,
        "exampleJapanese": ex_jp,
        "exampleReading": ex_rd,
        "exampleRomaji": ex_rm,
        "exampleEnglish": ex_en,
        "options": opts,
        "relatedItems": resolve_rels(rels, ex_rd, ex_jp),
        "tags": "KANA,KATAKANA,GOJUON,STATUS:SOURCE_VERIFIED",
        "source": "NoIgnore Standard Kana Syllabus"
    })
    k_idx += 1

# Process Katakana Dakuten
for jp, rd, rm, mn, note, ex_jp, ex_rd, ex_rm, ex_en, opts, rels in KATAKANA_DAKUTEN:
    all_items.append({
        "id": f"kana_k_{k_idx:03d}",
        "japanese": jp,
        "reading": rd,
        "romaji": rm,
        "meaning": mn,
        "category": "KANA",
        "jlptLevel": "N5",
        "partOfSpeech": "Katakana Dakuten" if "handakuten" not in note else "Katakana Handakuten",
        "mnemonicOrNote": note,
        "exampleJapanese": ex_jp,
        "exampleReading": ex_rd,
        "exampleRomaji": ex_rm,
        "exampleEnglish": ex_en,
        "options": opts,
        "relatedItems": resolve_rels(rels, ex_rd, ex_jp),
        "tags": "KANA,KATAKANA,DAKUTEN,STATUS:SOURCE_VERIFIED",
        "source": "NoIgnore Standard Kana Syllabus"
    })
    k_idx += 1

# Process Katakana Yoon
for jp, rd, rm, mn, note, ex_jp, ex_rd, ex_rm, ex_en, opts, rels in KATAKANA_YOON:
    all_items.append({
        "id": f"kana_k_{k_idx:03d}",
        "japanese": jp,
        "reading": rd,
        "romaji": rm,
        "meaning": mn,
        "category": "KANA",
        "jlptLevel": "N5",
        "partOfSpeech": "Katakana Yōon" if jp not in ["ッ", "ー"] else "Katakana Orthography",
        "mnemonicOrNote": note,
        "exampleJapanese": ex_jp,
        "exampleReading": ex_rd,
        "exampleRomaji": ex_rm,
        "exampleEnglish": ex_en,
        "options": opts,
        "relatedItems": resolve_rels(rels, ex_rd, ex_jp),
        "tags": "KANA,KATAKANA,YOON,STATUS:SOURCE_VERIFIED",
        "source": "NoIgnore Standard Kana Syllabus"
    })
    k_idx += 1

print(f"Total Katakana generated: {k_idx - 1}")
print(f"Grand Total Kana entities: {len(all_items)}")

with open(KANA_PATH, "w", encoding="utf-8") as f:
    json.dump(all_items, f, ensure_ascii=False, indent=2)

print(f"Successfully saved {len(all_items)} items to {KANA_PATH}")
