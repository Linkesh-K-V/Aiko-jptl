import json, glob

items = []
for f in sorted(glob.glob('app/src/main/assets/jlpt/*/*.json')):
    with open(f) as fp: items.extend(json.load(fp))

current_map = {
    "k_1": "n5_k_15", "k_2": "n5_k_16", "k_3": "n5_k_18", "k_4": "n5_k_17", "k_5": "n5_k_19",
    "k_6": "n5_k_20", "k_7": "n5_k_21", "k_8": "n5_k_27", "k_9": "n5_k_23", "k_10": "n5_k_28",
    "k_11": "n5_k_29", "k_12": "n5_k_30", "k_13": "n5_k_59", "k_14": "n5_k_60", "k_15": "n5_k_57",
    "v_3": "n5_v_41", "v_4": "n5_v_15", "v_6": "n5_v_133", "curric_1_1": "n5_k_15", "curric_1_2": "n5_k_16",
    "curric_1_3": "n5_k_19", "curric_1_4": "n5_v_89", "curric_2_1": "n5_k_18", "curric_2_2": "n5_k_17",
    "curric_2_3": "n5_v_01", "curric_2_4": "n5_v_02", "curric_2_5": "n5_v_03", "curric_3_3": "n5_v_38",
    "curric_3_4": "n5_v_39", "curric_4_1": "n5_k_84", "curric_4_2": "n5_v_265", "curric_4_3": "n5_k_76",
    "curric_5_1": "n5_k_23", "curric_6_1": "n5_k_80", "curric_6_2": "n5_k_81", "curric_6_3": "n5_k_83",
    "curric_7_1": "n5_v_27", "curric_7_3": "n5_v_33", "n5_v_212": "n5_v_03", "n4_g_01": "n5_g_18",
    "n4_g_02": "n5_g_37", "n4_g_04": "n5_g_37", "n4_k_04": "n5_k_77", "n4_k_07": "n5_k_60",
    "n4_k_10": "n5_k_76"
}
corrected_map = dict(current_map)
del corrected_map["n4_k_04"]
del corrected_map["n4_k_07"]
del corrected_map["n4_k_10"]

def get_edges(cmap):
    room_items = []
    for it in items:
        rels = it.get('relatedItems', [])
        if isinstance(rels, str): rels = [x.strip() for x in rels.split(',') if x.strip()]
        canon_rels = [cmap.get(r, r) for r in rels]
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
    edges = set()
    for item in room_items:
        for tgtId in item['relatedItems']:
            if tgtId in itemMap:
                edges.add((min(item['id'], tgtId), max(item['id'], tgtId)))
    return edges

e_curr = get_edges(current_map)
e_corr = get_edges(corrected_map)

print("Edges in current but NOT in corrected:", e_curr - e_corr)
print("Edges in corrected but NOT in current:", e_corr - e_curr)
