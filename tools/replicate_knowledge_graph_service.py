import json, glob

items = []
for f in sorted(glob.glob('app/src/main/assets/jlpt/*/*.json')):
    with open(f) as fp:
        items.extend(json.load(fp))

itemMap = {it['id']: it for it in items}

connected = 0
isolated = 0

kanjiToVocab = 0
vocabToReading = 0
vocabToListening = 0
grammarToReading = 0
grammarToListening = 0
examToKnowledge = 0
vocabToGrammar = 0
kanaToVocab = 0

uniquePairs = set()
unclassified = []

for item in items:
    rel_raw = item.get('relatedItems', [])
    if isinstance(rel_raw, list):
        rel_ids = [r.strip() for r in rel_raw if r.strip()]
    elif isinstance(rel_raw, str):
        rel_ids = [r.strip() for r in rel_raw.split(',') if r.strip()]
    else:
        rel_ids = []

    if not rel_ids:
        isolated += 1
        continue
    connected += 1

    srcCat = item.get('category', '').upper()
    for targetId in rel_ids:
        target = itemMap.get(targetId)
        if not target: continue
        targetCat = target.get('category', '').upper()
        pair = (min(item['id'], target['id']), max(item['id'], target['id']))
        if pair not in uniquePairs:
            uniquePairs.add(pair)
            # Exactly matching Kotlin when block:
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
            else:
                unclassified.append((pair, srcCat, targetCat))

print("=== EXACT REPLICATION OF KNOWLEDGEGRAPHSERVICE ===")
print("Total items:", len(items))
print("Connected:", connected)
print("Isolated:", isolated)
print("Total edges:", len(uniquePairs))
print("kanjiToVocab:", kanjiToVocab)
print("vocabToReading:", vocabToReading)
print("vocabToListening:", vocabToListening)
print("grammarToReading:", grammarToReading)
print("grammarToListening:", grammarToListening)
print("examToKnowledge:", examToKnowledge)
print("vocabToGrammar:", vocabToGrammar)
print("kanaToVocab:", kanaToVocab)
print("Unclassified edges count:", len(unclassified))
