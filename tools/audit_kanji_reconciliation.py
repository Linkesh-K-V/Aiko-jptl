import json, glob

with open('app/src/main/assets/jlpt/n4/kanji.json') as fp:
    n4_kanji = json.load(fp)

with open('app/src/main/assets/jlpt/n5/kanji.json') as fp:
    n5_kanji = json.load(fp)

with open('app/src/main/assets/jlpt/n4/vocabulary.json') as fp:
    n4_vocab = json.load(fp)

with open('app/src/main/assets/jlpt/n5/vocabulary.json') as fp:
    n5_vocab = json.load(fp)

all_vocab_map = {v['id']: v for v in n4_vocab + n5_vocab}
all_kanji_map = {k['id']: k for k in n4_kanji + n5_kanji}

# Check n4_k_04, n4_k_07, n4_k_10
print("=== INSPECTING n4_k_04, n4_k_07, n4_k_10 in n4_kanji ===")
for k_id in ['n4_k_04', 'n4_k_07', 'n4_k_10']:
    found = [k for k in n4_kanji if k['id'] == k_id]
    if found:
        print(f"Found in n4/kanji.json: {found[0]['id']} -> {found[0]['japanese']}")
    else:
        print(f"NOT in n4/kanji.json: {k_id}")

print("\n=== N4 KANJI ID DISTRIBUTION ===")
print(f"Total N4 kanji: {len(n4_kanji)}")
all_n4_k_ids = [k['id'] for k in n4_kanji]
print("Min ID:", all_n4_k_ids[0], "Max ID:", all_n4_k_ids[-1])
print("Distinct Kanji characters in N4:", len(set(k['japanese'] for k in n4_kanji)))

# Check outgoing links of all 175 N4 kanji
print("\n=== OUTGOING LINKS FOR 175 N4 KANJI ===")
outgoing_links_count = 0
unmatched_chars = []
for k in n4_kanji:
    char = k['japanese']
    rels = k.get('relatedItems', [])
    outgoing_links_count += len(rels)
    for r in rels:
        v = all_vocab_map.get(r)
        if v:
            if char not in v['japanese']:
                unmatched_chars.append((k['id'], char, r, v['japanese']))
        else:
            print(f"Missing target vocab: {r} from {k['id']}")

print(f"Total outgoing links from 175 N4 Kanji: {outgoing_links_count}")
print(f"Unmatched Kanji characters in target vocabulary: {len(unmatched_chars)}")
if unmatched_chars:
    print("Unmatched:", unmatched_chars)
