import json, glob, sys

# 1. Authoritative Assets Loading
all_assets = []
levels = ["n5", "n4", "n3", "n2", "n1"]
categories = ["kanji", "vocabulary", "grammar", "questions", "reading", "listening", "kana"]

for lvl in levels:
    for cat in categories:
        path = f"app/src/main/assets/jlpt/{lvl}/{cat}.json"
        try:
            with open(path, "r", encoding="utf-8") as f:
                data = json.load(f)
                all_assets.extend(data)
        except FileNotFoundError:
            pass

print(f"1. Total Curriculum Entities in Assets: {len(all_assets)}")

# 2. Canonical mapping in CanonicalCurriculumIds.kt
# Genuine legacy aliases
legacy_aliases = {
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

# Emulate Room loading with CanonicalCurriculumIds.kt
room_items = []
for it in all_assets:
    rels = it.get("relatedItems", [])
    if isinstance(rels, str): rels = [x.strip() for x in rels.split(",") if x.strip()]
    canon_rels = [legacy_aliases.get(r, r) for r in rels]
    seen = set()
    distinct_rels = []
    for r in canon_rels:
        if r not in seen:
            seen.add(r)
            distinct_rels.append(r)
    it_copy = dict(it)
    it_copy["relatedItems"] = distinct_rels
    room_items.append(it_copy)

itemMap = {it["id"]: it for it in room_items}
uniquePairs = set()
kanjiToVocab = 0
broken_refs = []

for item in room_items:
    srcCat = item.get("category", "").upper()
    for tgtId in item["relatedItems"]:
        tgt = itemMap.get(tgtId)
        if not tgt:
            broken_refs.append((item["id"], tgtId))
            continue
        targetCat = tgt.get("category", "").upper()
        pair = (min(item["id"], tgt["id"]), max(item["id"], tgt["id"]))
        if pair not in uniquePairs:
            uniquePairs.add(pair)
            if (srcCat == "KANJI" and targetCat == "VOCAB") or (srcCat == "VOCAB" and targetCat == "KANJI"):
                kanjiToVocab += 1

print(f"2. Resulting Room Graph Unique Edges: {len(uniquePairs)}")
print(f"3. Resulting Kanji-to-Vocab Edges: {kanjiToVocab}")
print(f"4. Resulting Broken References: {len(broken_refs)}")

# 5. Verification of the 6 affected words in Room:
targets = ["n4_v_06", "n4_v_27", "n4_v_34", "n4_v_59", "n4_v_108", "n4_v_292"]
print("\n=== VERIFYING THE 6 AFFECTED WORDS IN ROOM ===")
for t_id in targets:
    it = itemMap[t_id]
    rels = it["relatedItems"]
    print(f"Word: {it['id']} ({it['japanese']}, {it['meaning']})")
    print(f"  Resolved relatedItems: {rels}")
    for r in rels:
        linked = itemMap[r]
        print(f"    -> linked to: {linked['id']} ({linked['japanese']}, {linked.get('meaning')}, category={linked.get('category')})")
        print(f"       Kanji '{linked['japanese']}' in '{it['japanese']}': {linked['japanese'] in it['japanese']}")

# 6. Verify 175 N4 Kanji
n4_kanji = [it for it in room_items if it.get("jlptLevel") == "N4" and it.get("category") == "KANJI"]
print(f"\nTotal N4 Kanji in Room: {len(n4_kanji)}")
print(f"Distinct N4 Kanji IDs: {len(set(k['id'] for k in n4_kanji))}")
print(f"Distinct N4 Kanji Characters: {len(set(k['japanese'] for k in n4_kanji))}")
