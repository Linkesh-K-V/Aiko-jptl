import json, re

# Load all 10 parts and assemble into exactly 575 unique canonical entries
all_raw = []
for p in range(1, 11):
    with open(f'tools/part{p}.json', 'r', encoding='utf-8') as f:
        all_raw.extend(json.load(f))

seen = set()
unique_raw = []
for item in all_raw:
    jp = item[0]
    if jp not in seen:
        seen.add(jp)
        unique_raw.append(item)

print(f"Loaded {len(unique_raw)} unique vocabulary entries.")

# Load N4 Kanji and N5 Kanji to build bidirectional graph relations
with open('app/src/main/assets/jlpt/n4/kanji.json', 'r', encoding='utf-8') as f:
    n4_kanji = json.load(f)
with open('app/src/main/assets/jlpt/n5/kanji.json', 'r', encoding='utf-8') as f:
    n5_kanji = json.load(f)
with open('app/src/main/assets/jlpt/n4/grammar.json', 'r', encoding='utf-8') as f:
    n4_grammar = json.load(f)
with open('app/src/main/assets/jlpt/n5/vocabulary.json', 'r', encoding='utf-8') as f:
    n5_vocab = json.load(f)

n5_words = {w['japanese'] for w in n5_vocab}
n4_k_map = {k['japanese']: k['id'] for k in n4_kanji}
n5_k_map = {k['japanese']: k['id'] for k in n5_kanji}

# Check N5 duplicate protection
n5_overlap = [w[0] for w in unique_raw if w[0] in n5_words]
print(f"N5 overlap count: {len(n5_overlap)}")
if n5_overlap:
    print(f"WARNING - N5 duplicates detected: {n5_overlap}")

# Build N4 vocabulary records with IDs n4_v_01 to n4_v_575
# Ensure legacy n4_v_01 - n4_v_15 keep their canonical IDs
canonical_vocab = []

for idx, item in enumerate(unique_raw, start=1):
    v_id = f"n4_v_{idx:02d}" if idx <= 99 else f"n4_v_{idx}"
    jp, reading, romaji, meaning, pos, diff, tags, exJp, exReading, exRomaji, exEn, extraRel = item

    # Extract kanji in this word
    word_kanji = [c for c in jp if '\u4e00' <= c <= '\u9faf']
    related_ids = list(extraRel)

    for ch in word_kanji:
        if ch in n4_k_map and n4_k_map[ch] not in related_ids:
            related_ids.append(n4_k_map[ch])
        elif ch in n5_k_map and n5_k_map[ch] not in related_ids:
            related_ids.append(n5_k_map[ch])

    # If verb and ends in ことができる etc., connect grammar
    # Add mnemonic note if kanji are present
    n4_contained = [ch for ch in word_kanji if ch in n4_k_map]
    n5_contained = [ch for ch in word_kanji if ch in n5_k_map]
    beyond_contained = [ch for ch in word_kanji if ch not in n4_k_map and ch not in n5_k_map]

    note_parts = []
    if n4_contained:
        note_parts.append(f"Contains N4 Kanji: {', '.join(n4_contained)}")
    if n5_contained:
        note_parts.append(f"Contains N5 Kanji: {', '.join(n5_contained)}")
    if beyond_contained:
        note_parts.append(f"Kana-First / Advanced Kanji: {', '.join(beyond_contained)}")
    if not note_parts:
        note_parts.append("Kana-First Foundation: zero Kanji dependency")

    note = " | ".join(note_parts)

    record = {
        "id": v_id,
        "japanese": jp,
        "reading": reading,
        "romaji": romaji,
        "meaning": meaning,
        "category": "VOCAB",
        "jlptLevel": "N4",
        "partOfSpeech": pos,
        "difficulty": diff,
        "tags": tags,
        "mnemonicOrNote": note,
        "exampleJapanese": exJp,
        "exampleReading": exReading,
        "exampleRomaji": exRomaji,
        "exampleEnglish": exEn,
        "source": "JLPT N4 Vocabulary Foundation",
        "relatedItems": related_ids
    }
    canonical_vocab.append(record)

print(f"Generated {len(canonical_vocab)} canonical N4 vocabulary records.")

# Save directly to n4/vocabulary.json
with open('app/src/main/assets/jlpt/n4/vocabulary.json', 'w', encoding='utf-8') as f:
    json.dump(canonical_vocab, f, ensure_ascii=False, indent=2)

print("Saved to app/src/main/assets/jlpt/n4/vocabulary.json")
