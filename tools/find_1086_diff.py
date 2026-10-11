import json, glob

items = []
for f in sorted(glob.glob('app/src/main/assets/jlpt/*/*.json')):
    with open(f) as fp:
        items.extend(json.load(fp))

itemMap = {it['id']: it for it in items}
uniquePairs = set()
kv_edges = set()

for it in items:
    rels = it.get('relatedItems', [])
    if isinstance(rels, str):
        rels = [x.strip() for x in rels.split(',') if x.strip()]
    srcCat = it.get('category', '').upper()
    for tgt in rels:
        if tgt not in itemMap: continue
        target = itemMap[tgt]
        targetCat = target.get('category', '').upper()
        pair = (min(it['id'], target['id']), max(it['id'], target['id']))
        if pair not in uniquePairs:
            uniquePairs.add(pair)
            if (srcCat == 'KANJI' and targetCat == 'VOCAB') or (srcCat == 'VOCAB' and targetCat == 'KANJI'):
                kv_edges.add(pair)

print(f"Number of kv_edges in Python: {len(kv_edges)}")

# Now check: are there ANY other items where category is KANJI or VOCAB?
for it in items:
    if 'KANJI' in it['id'] and it['category'] != 'KANJI':
        print(f"Kanji ID with different category: {it['id']} -> {it['category']}")
    if '_v_' in it['id'] and it['category'] != 'VOCAB':
        print(f"Vocab ID with different category: {it['id']} -> {it['category']}")
