import json, glob

items = []
for f in sorted(glob.glob('app/src/main/assets/jlpt/*/*.json')):
    with open(f) as fp:
        items.extend(json.load(fp))

# Corrected mapping:
# 1. Genuine legacy aliases (k_*, v_*, curric_*, n5_v_212) remain 100% intact!
# 2. n4_k_04, n4_k_07, n4_k_10 removed from duplicate map!
# 3. In app/src/main/assets/jlpt/n5/vocabulary.json:
#    - n5_v_02 (飲む) had relatedItems ['n4_k_07', 'n5_k_60']. Without n4_k_07, it keeps 'n5_k_60' (飲).
#    - n5_v_56 (駅) had relatedItems ['n4_g_10', 'n4_k_10', 'n5_k_76', ...]. Without n4_k_10, it keeps 'n5_k_76' (駅).
# 4. In app/src/main/assets/jlpt/n4/vocabulary.json:
#    - n4_v_06 (都合) links to n4_k_10 (都) -> Now resolves to n4_k_10 (都)!
#    - n4_v_27 (京都) links to n4_k_10 (都) -> Now resolves to n4_k_10 (都)!
#    - n4_v_34 (会社員) links to n4_k_04 (員) -> Now resolves to n4_k_04 (員)!
#    - n4_v_59 (動物園) links to n4_k_07 (園) -> Now resolves to n4_k_07 (園)!
#    - n4_v_108 (店員) links to n4_k_04 (員) -> Now resolves to n4_k_04 (員)!
#    - n4_v_292 (首都) links to n4_k_10 (都) -> Now resolves to n4_k_10 (都)!

base_map = {
    "k_1": "n5_k_15", "k_2": "n5_k_16", "k_3": "n5_k_18", "k_4": "n5_k_17", "k_5": "n5_k_19",
    "k_6": "n5_k_20", "k_7": "n5_k_21", "k_8": "n5_k_27", "k_9": "n5_k_23", "k_10": "n5_k_28",
    "k_11": "n5_k_29", "k_12": "n5_k_30", "k_13": "n5_k_59", "k_14": "n5_k_60", "k_15": "n5_k_57",
    "v_3": "n5_v_41", "v_4": "n5_v_15", "v_6": "n5_v_133", "curric_1_1": "n5_k_15", "curric_1_2": "n5_k_16",
    "curric_1_3": "n5_k_19", "curric_1_4": "n5_v_89", "curric_2_1": "n5_k_18", "curric_2_2": "n5_k_17",
    "curric_2_3": "n5_v_01", "curric_2_4": "n5_v_02", "curric_2_5": "n5_v_03", "curric_3_3": "n5_v_38",
    "curric_3_4": "n5_v_39", "curric_4_1": "n5_k_84", "curric_4_2": "n5_v_265", "curric_4_3": "n5_k_76",
    "curric_5_1": "n5_k_23", "curric_6_1": "n5_k_80", "curric_6_2": "n5_k_81", "curric_6_3": "n5_k_83",
    "curric_7_1": "n5_v_27", "curric_7_3": "n5_v_33", "n5_v_212": "n5_v_03", "n4_g_01": "n5_g_18",
    "n4_g_02": "n5_g_37", "n4_g_04": "n5_g_37"
}

# Clean n5_v_02 and n5_v_56: remove the accidental n4_k_07 and n4_k_10
cleaned_items = []
for it in items:
    it_copy = dict(it)
    if it['id'] == 'n5_v_02':
        it_copy['relatedItems'] = ['n5_k_60']
    elif it['id'] == 'n5_v_56':
        it_copy['relatedItems'] = ['n4_g_10', 'n5_k_76', 'n5_l_02', 'n5_r_03', 'n5_k_66']
    cleaned_items.append(it_copy)

# Let's compute graph statistics with cleaned items and corrected map:
room_items = []
for it in cleaned_items:
    rels = it.get('relatedItems', [])
    if isinstance(rels, str): rels = [x.strip() for x in rels.split(',') if x.strip()]
    canon_rels = [base_map.get(r, r) for r in rels]
    seen = set()
    distinct_rels = []
    for r in canon_rels:
        if r not in seen:
            seen.add(r)
            distinct_rels.append(r)
    it_copy = dict(it)
    it_copy['relatedItems'] = distinct_rels
    room_items.append(it_copy)

itemMap = {it['id']: it for it in room_items}
uniquePairs = set()
kanjiToVocab = 0
vocabToReading = 0
vocabToListening = 0
grammarToReading = 0
grammarToListening = 0
examToKnowledge = 0
vocabToGrammar = 0
kanaToVocab = 0
broken = 0

for item in room_items:
    srcCat = item.get('category', '').upper()
    for tgtId in item['relatedItems']:
        tgt = itemMap.get(tgtId)
        if not tgt:
            broken += 1
            continue
        targetCat = tgt.get('category', '').upper()
        pair = (min(item['id'], tgt['id']), max(item['id'], tgt['id']))
        if pair not in uniquePairs:
            uniquePairs.add(pair)
            if (srcCat == "KANJI" and targetCat == "VOCAB") or (srcCat == "VOCAB" and targetCat == "KANJI"):
                kanjiToVocab += 1
            elif (srcCat == "VOCAB" and targetCat == "READING") or (srcCat == "READING" and targetCat == "VOCAB"):
                vocabToReading += 1
            elif (srcCat == "VOCAB" and targetCat == "LISTENING") or (srcCat == "LISTENING" and targetCat == "VOCAB"):
                vocabToListening += 1
            elif (srcCat == "GRAMMAR" and targetCat == "READING") or (srcCat == "READING" and targetCat == "GRAMMAR"):
                grammarToReading += 1
            elif (srcCat == "GRAMMAR" and targetCat == "LISTENING") or (srcCat == "LISTENING" and targetCat == "GRAMMAR"):
                grammarToListening += 1
            elif srcCat == "JLPT_EXAM" or targetCat == "JLPT_EXAM":
                examToKnowledge += 1
            elif (srcCat == "VOCAB" and targetCat == "GRAMMAR") or (srcCat == "GRAMMAR" and targetCat == "VOCAB"):
                vocabToGrammar += 1
            elif (srcCat == "KANA" and targetCat == "VOCAB") or (srcCat == "VOCAB" and targetCat == "KANA"):
                kanaToVocab += 1

print("Total unique edges:", len(uniquePairs))
print("kanjiToVocab:", kanjiToVocab)
print("vocabToReading:", vocabToReading)
print("vocabToListening:", vocabToListening)
print("grammarToReading:", grammarToReading)
print("grammarToListening:", grammarToListening)
print("examToKnowledge:", examToKnowledge)
print("vocabToGrammar:", vocabToGrammar)
print("kanaToVocab:", kanaToVocab)
print("broken_refs:", broken)
