import json, os

categories_in_room = {}

# Replicate JlptAssetContentLoader:
category_files = [
    ("n5", "kana", "KANA"),
    ("n5", "kanji", "KANJI"),
    ("n5", "vocabulary", "VOCAB"),
    ("n5", "grammar", "GRAMMAR"),
    ("n5", "reading", "READING"),
    ("n5", "listening", "LISTENING"),
    ("n5", "questions", "JLPT_EXAM"),
    ("n4", "kanji", "KANJI"),
    ("n4", "vocabulary", "VOCAB"),
    ("n4", "grammar", "GRAMMAR"),
    ("n4", "reading", "READING"),
    ("n4", "listening", "LISTENING"),
    ("n4", "questions", "JLPT_EXAM"),
    ("n3", "kanji", "KANJI"),
    ("n3", "vocabulary", "VOCAB"),
    ("n3", "grammar", "GRAMMAR"),
    ("n3", "reading", "READING"),
    ("n3", "listening", "LISTENING"),
    ("n3", "questions", "JLPT_EXAM"),
    ("n2", "kanji", "KANJI"),
    ("n2", "vocabulary", "VOCAB"),
    ("n2", "grammar", "GRAMMAR"),
    ("n2", "reading", "READING"),
    ("n2", "listening", "LISTENING"),
    ("n2", "questions", "JLPT_EXAM"),
    ("n1", "kanji", "KANJI"),
    ("n1", "vocabulary", "VOCAB"),
    ("n1", "grammar", "GRAMMAR"),
    ("n1", "reading", "READING"),
    ("n1", "listening", "LISTENING"),
    ("n1", "questions", "JLPT_EXAM"),
]

items = []
for level, filename, default_cat in category_files:
    path = f"app/src/main/assets/jlpt/{level}/{filename}.json"
    if os.path.exists(path):
        with open(path, "r", encoding="utf-8") as f:
            data = json.load(f)
            for it in data:
                cat = it.get("category", default_cat)
                # In JlptAssetContentLoader: val category = obj.optString("category", defaultCategory)
                # if obj has "category": "...", it uses that!
                it_copy = dict(it)
                it_copy["category"] = cat
                items.append(it_copy)

itemMap = {it["id"]: it for it in items}
uniquePairs = set()
kanjiToVocab = 0

for it in items:
    rels = it.get("relatedItems", [])
    if isinstance(rels, str):
        rels = [x.strip() for x in rels.split(",") if x.strip()]
    srcCat = it.get("category", "").upper()
    for tgtId in rels:
        tgt = itemMap.get(tgtId)
        if not tgt: continue
        targetCat = tgt.get("category", "").upper()
        pair = (min(it["id"], tgt["id"]), max(it["id"], tgt["id"]))
        if pair not in uniquePairs:
            uniquePairs.add(pair)
            if (srcCat == "KANJI" and targetCat == "VOCAB") or (srcCat == "VOCAB" and targetCat == "KANJI"):
                kanjiToVocab += 1

print(f"When loaded into Room: total edges = {len(uniquePairs)}, kanjiToVocab = {kanjiToVocab}")
