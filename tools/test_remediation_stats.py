import json, glob

items = []
for f in sorted(glob.glob('app/src/main/assets/jlpt/*/*.json')):
    with open(f) as fp:
        items.extend(json.load(fp))

print(f"Total items in assets: {len(items)}")
# Check if any item has category mismatch or different case
for it in items:
    cat = it.get('category', '')
    if cat not in ['KANJI', 'VOCAB', 'GRAMMAR', 'READING', 'LISTENING', 'JLPT_EXAM', 'KANA']:
        print(f"Non-standard category: {it['id']} -> {cat}")
