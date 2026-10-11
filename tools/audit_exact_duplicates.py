import json

with open('app/src/main/assets/jlpt/n5/vocabulary.json', 'r', encoding='utf-8') as f5:
    n5 = json.load(f5)

with open('app/src/main/assets/jlpt/n4/vocabulary.json', 'r', encoding='utf-8') as f4:
    n4 = json.load(f4)

# 1. Exact Japanese string
exact_matches = []
n5_map = {item['japanese']: item for item in n5}
for item in n4:
    if item['japanese'] in n5_map:
        exact_matches.append((item, n5_map[item['japanese']]))

print(f"1. Exact Japanese Headword Overlap: {len(exact_matches)}")

# 2. Kana normalization
def hira(text):
    res = []
    for c in text:
        code = ord(c)
        if 0x30A1 <= code <= 0x30F6:
            res.append(chr(code - 0x60))
        else:
            res.append(c)
    return "".join(res)

# 3. Same Reading Overlap (Homophones / potential duplicates)
reading_overlap = []
n5_reading_map = {}
for item in n5:
    r = hira(item['reading'])
    n5_reading_map.setdefault(r, []).append(item)

for item in n4:
    r = hira(item['reading'])
    if r in n5_reading_map:
        for match in n5_reading_map[r]:
            reading_overlap.append((item, match))

print(f"2. Homophone / Same Reading Overlap: {len(reading_overlap)} pairs")
for n4_item, n5_item in reading_overlap:
    print(f"Pair: N4 ID={n4_item['id']} '{n4_item['japanese']}' ({n4_item['reading']}) -> {n4_item['meaning']}")
    print(f"  vs: N5 ID={n5_item['id']} '{n5_item['japanese']}' ({n5_item['reading']}) -> {n5_item['meaning']}")
    # Compare kanji and meanings
    print(f"  Semantic analysis: N4 kanji={n4_item['japanese']} vs N5 kanji={n5_item['japanese']}")
    print("-" * 60)
