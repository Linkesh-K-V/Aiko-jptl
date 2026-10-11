import json

# Replacements:
# '不便' -> '不親切' (ふしんせつ, fushinsetsu, unkind, unfriendly, Na-Adjective, 2, "character,adjectives", "困っている人に不親切な態度をとってはいけません。", "こまっているひとにふしんせつなたいどをとってはいけません。", "Komatte iru hito ni fushinsetsu na taido o totte wa ikemasen.", "You must not adopt an unkind attitude toward people in trouble.", [])
# '親切' -> '不親切' (wait, let's use two distinct words!)
# 1. '不親切' (unkind)
# 2. '便利さ' (べんりさ, benrisa, convenience, degree of usefulness, Noun, 2, "abstract,evaluation", "都会の生活はその便利さが魅力です。", "とかいのせいかつはそのべんりさがみりょくです。", "Tokai no seikatsu wa sono benrisa ga miryoku desu.", "The appeal of city life lies in its convenience.", [])
# 3. 'まだしも' (まだしも, madashimo, rather, better (comparative), Adverb, 2, "adverbs,comparison", "小雨ならまだしも、大雨のときは外出を控えます。", "こさめならまだしも、おおあめのときはがいしゅつをひかえます。", "Kosame nara madashimo, ooame no toki wa gaishutsu o hikaemasu.", "A drizzle is one thing, but in heavy rain I refrain from going out.", [])
# 4. 'もうすぐ' (もうすぐ, mousugu, soon, before long, Adverb, 2, "time,adverbs", "もうすぐ待ちに待った桜の季節が始まります。", "もうすぐまちにまったさくらのきせつがはじまります。", "Mousugu machinimatta sakura no kisetsu ga hajimarimasu.", "Soon the long-awaited cherry blossom season will begin.", [])

replacements = {
    '不便': ("便利さ", "べんりさ", "benrisa", "convenience, usefulness", "Noun", 2, "abstract,evaluation", "都会の生活はその便利さが魅力です。", "とかいのせいかつはそのべんりさがみりょくです。", "Tokai no seikatsu wa sono benrisa ga miryoku desu.", "The appeal of city life lies in its convenience.", []),
    '親切': ("不親切", "ふしんせつ", "fushinsetsu", "unkind, unfriendly", "Na-Adjective", 2, "character,adjectives", "困っている人に不親切な態度をとってはいけません。", "こまっているひとにふしんせつなたいどをとってはいけません。", "Komatte iru hito ni fushinsetsu na taido o totte wa ikemasen.", "You must not adopt an unkind attitude toward people in trouble.", []),
    'まだ': ("まだしも", "まだしも", "madashimo, rather, better", "rather, somewhat acceptable", "Adverb", 2, "adverbs,comparison", "小雨ならまだしも、大雨のときは外出を控えます。", "こさめならまだしも、おおあめのときはがいしゅつをひかえます。", "Kosame nara madashimo, ooame no toki wa gaishutsu o hikaemasu.", "A drizzle is one thing, but in heavy rain I refrain from going out.", []),
    'もう': ("もうすぐ", "もうすぐ", "mousugu", "soon, before long", "Adverb", 2, "time,adverbs", "もうすぐ待ちに待った桜の季節が始まります。", "もうすぐまちにまったさくらのきせつがはじまります。", "Mousugu machinimatta sakura no kisetsu ga hajimarimasu.", "Soon the long-awaited cherry blossom season will begin.", [])
}

# Update part files where these appeared
for p in range(1, 11):
    with open(f'tools/part{p}.json', 'r', encoding='utf-8') as f:
        data = json.load(f)
    new_data = []
    for item in data:
        if item[0] in replacements:
            new_data.append(replacements[item[0]])
        else:
            new_data.append(item)
    with open(f'tools/part{p}.json', 'w', encoding='utf-8') as f:
        json.dump(new_data, f, ensure_ascii=False, indent=2)

print("Updated part files with non-duplicate replacements.")
