import json, glob

items = []
for f in sorted(glob.glob('app/src/main/assets/jlpt/*/*.json')):
    with open(f) as fp: items.extend(json.load(fp))

itemMap = {it['id']: it for it in items}

# Let's inspect the exact items where relatedItems contains n4_k_04, n4_k_07, or n4_k_10
affected = []
for it in items:
    rels = it.get('relatedItems', [])
    if isinstance(rels, str): rels = [x.strip() for x in rels.split(',') if x.strip()]
    for r in rels:
        if r in ['n4_k_04', 'n4_k_07', 'n4_k_10']:
            affected.append((it['id'], it['japanese'], it['category'], r))

print("=== ITEMS REFERENCING n4_k_04, n4_k_07, n4_k_10 ===")
for a in affected:
    print(f"Item {a[0]} ({a[1]}, {a[2]}) references {a[3]}")
    # In n4_kanji:
    k_n4 = itemMap.get(a[3])
    print(f"  In n4/kanji.json, {a[3]} is: {k_n4['japanese']} ({k_n4['meaning']})")
    # In CanonicalCurriculumIds:
    target_canon = {'n4_k_04': 'n5_k_77', 'n4_k_07': 'n5_k_60', 'n4_k_10': 'n5_k_76'}[a[3]]
    k_canon = itemMap.get(target_canon)
    print(f"  Canonical mapping replaces with {target_canon}: {k_canon['japanese']} ({k_canon['meaning']})")
    # Does the word actually contain the Kanji?
    word = a[1]
    n4_char = k_n4['japanese']
    canon_char = k_canon['japanese']
    print(f"  Does word '{word}' contain N4 kanji '{n4_char}'? {n4_char in word}")
    print(f"  Does word '{word}' contain Canon kanji '{canon_char}'? {canon_char in word}")
    print("-" * 50)
