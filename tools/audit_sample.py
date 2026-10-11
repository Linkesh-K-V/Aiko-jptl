import json, random

with open('app/src/main/assets/jlpt/n4/vocabulary.json', 'r', encoding='utf-8') as f:
    vocab = json.load(f)

# Deterministic sample by categories/types
pos_buckets = {}
for v in vocab:
    pos = v.get('partOfSpeech', '')
    pos_buckets.setdefault(pos, []).append(v)

sample = []
for pos, items in sorted(pos_buckets.items()):
    sample.append(items[0])
    if len(items) > 10:
        sample.append(items[len(items) // 2])

# Add specific linguistic categories
# 1. Kana-accessible (zero kanji)
kana_only = [v for v in vocab if not any('\u4e00' <= c <= '\u9faf' for c in v['japanese'])]
sample.append(kana_only[0])
sample.append(kana_only[len(kana_only) // 2])

# 2. Compound / heavy kanji (>= 3 kanji)
heavy_kanji = [v for v in vocab if sum(1 for c in v['japanese'] if '\u4e00' <= c <= '\u9faf') >= 2]
sample.append(heavy_kanji[0])
sample.append(heavy_kanji[len(heavy_kanji) // 2])

# Deduplicate by ID
seen_ids = set()
unique_sample = []
for s in sample:
    if s['id'] not in seen_ids:
        seen_ids.add(s['id'])
        unique_sample.append(s)

print(f"Total representative sample count: {len(unique_sample)}")
for s in unique_sample[:25]:
    print(f"ID: {s['id']} | Japanese: {s['japanese']} | Reading: {s['reading']} | POS: {s['partOfSpeech']} | Meaning: {s['meaning']} | Ex: {s['exampleJapanese'][:30]}...")
