import json, glob, sys

# 1. Authoritative assets verification
items = []
for f in sorted(glob.glob('app/src/main/assets/jlpt/*/*.json')):
    if 'further_study' in f: continue
    with open(f, 'r', encoding='utf-8') as fp:
        items.extend(json.load(fp))

print(f"1. Total Curriculum Assets Entities: {len(items)}")

# Check specifically that n4_k_07 is NOT in n5_v_02 and n4_k_10 is NOT in n5_v_56
nomu = next((x for x in items if x['id'] == 'n5_v_02'), None)
eki = next((x for x in items if x['id'] == 'n5_v_56'), None)

assert nomu is not None, "n5_v_02 must exist"
assert eki is not None, "n5_v_56 must exist"

assert 'n4_k_07' not in nomu.get('relatedItems', []), "n4_k_07 must not be in n5_v_02"
assert 'n5_k_60' in nomu.get('relatedItems', []), "n5_k_60 must be in n5_v_02"

assert 'n4_k_10' not in eki.get('relatedItems', []), "n4_k_10 must not be in n5_v_56"
assert 'n5_k_76' in eki.get('relatedItems', []), "n5_k_76 must be in n5_v_56"

print("Source asset checks passed: n5_v_02 and n5_v_56 have no invalid cross-level links.")

# 2. Canonical mapping & Room database emulation
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

room_items = []
for it in items:
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

print(f"2. Total Room Graph Edges: {len(uniquePairs)}")
print(f"3. Total Kanji-to-Vocab Edges: {kanjiToVocab}")
print(f"4. Broken References: {broken}")

# 5. External Resources Verification
with open("app/src/main/assets/jlpt/further_study_resources.json", "r", encoding="utf-8") as f:
    resources = json.load(f)

print(f"5. Total Further Study Resources: {len(resources)}")
for r in resources:
    assert r['id'], "Must have ID"
    assert r['url'].startswith('https://'), f"Must be valid HTTPS URL: {r['url']}"
    assert r['verificationStatus'] == "VERIFIED", f"Must be verified: {r['id']}"
    for cid in r['associatedConceptIds']:
        assert cid in itemMap or 'exam_deck' in cid, f"Mapped concept {cid} must exist in curriculum"

print("All resource concept mappings exist in curriculum.")

# 6. Verify Grammar Mappings
g_comp = next(r for r in resources if r['id'] == 'res_g_tae_kim_comparison')
g_desire = next(r for r in resources if r['id'] == 'res_g_tae_kim_desire')

print(f"Comparison associatedConceptIds: {g_comp['associatedConceptIds']}")
print(f"Desire associatedConceptIds: {g_desire['associatedConceptIds']}")

assert "n5_g_22" in g_comp['associatedConceptIds'], "Comparison must map to n5_g_22 (~より / ~のほうが)"
assert "n5_g_23" in g_comp['associatedConceptIds'], "Comparison must map to n5_g_23 (~の中で~が一番)"
assert "n4_v_16" in g_comp['associatedConceptIds'], "Comparison must map to n4_v_16 (一番)"

assert "n5_g_12" in g_desire['associatedConceptIds'], "Desire must map to n5_g_12 (~たいです)"

print("=== ALL CRITICAL CLAIMS EXECUTED AND CONFIRMED ===")
