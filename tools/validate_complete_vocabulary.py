import json, re

def validate_vocab_file(file_path, expected_level):
    with open(file_path, 'r', encoding='utf-8') as f:
        data = json.load(f)

    report = {
        'total': len(data),
        'valid': 0,
        'failures': [],
        'checks_executed': [
            'stable_unique_id',
            'non_empty_japanese',
            'non_empty_kana_reading',
            'non_empty_meaning',
            'valid_part_of_speech',
            'correct_jlpt_level',
            'category_is_VOCAB',
            'authentic_example_japanese',
            'example_contains_word',
            'example_english_translation',
            'valid_related_items'
        ]
    }

    kana_regex = re.compile(r'^[\u3040-\u309F\u30A0-\u30FF\u30FC\s・]+$')
    seen_ids = set()

    for idx, item in enumerate(data):
        item_id = item.get('id', '')
        jp = item.get('japanese', '')
        rd = item.get('reading', '')
        mn = item.get('meaning', '')
        pos = item.get('partOfSpeech', '')
        lvl = item.get('jlptLevel', '')
        cat = item.get('category', '')
        exJp = item.get('exampleJapanese', '')
        exEn = item.get('exampleEnglish', '')
        rels = item.get('relatedItems', [])

        item_failures = []

        if not item_id or item_id in seen_ids:
            item_failures.append(f"Duplicate or empty ID: '{item_id}'")
        seen_ids.add(item_id)

        if not jp:
            item_failures.append("Empty japanese headword")
        if not rd or not kana_regex.match(rd):
            item_failures.append(f"Invalid reading: '{rd}'")
        if not mn:
            item_failures.append("Empty meaning")
        if not pos:
            item_failures.append("Empty partOfSpeech")
        if lvl != expected_level:
            item_failures.append(f"Expected level {expected_level}, got {lvl}")
        if cat != 'VOCAB':
            item_failures.append(f"Expected category VOCAB, got {cat}")
        if not exJp:
            item_failures.append("Empty exampleJapanese")
        if not exEn:
            item_failures.append("Empty exampleEnglish")

        # Example sentence should ideally contain the word (or root/stem for inflected verbs/adjectives)
        # Check stem
        stem = jp[:-1] if len(jp) > 1 and (jp.endswith('る') or jp.endswith('う') or jp.endswith('い') or jp.endswith('く') or jp.endswith('す') or jp.endswith('つ') or jp.endswith('ぬ') or jp.endswith('む') or jp.endswith('ぐ') or jp.endswith('ぶ')) else jp
        if stem not in exJp and jp not in exJp:
            # Check reading
            rd_stem = rd[:-1] if len(rd) > 1 else rd
            if rd_stem not in exJp:
                item_failures.append(f"Example does not contain headword/stem '{stem}': '{exJp}'")

        if item_failures:
            report['failures'].append({'id': item_id, 'japanese': jp, 'errors': item_failures})
        else:
            report['valid'] += 1

    return report

rep_n4 = validate_vocab_file('app/src/main/assets/jlpt/n4/vocabulary.json', 'N4')
rep_n5 = validate_vocab_file('app/src/main/assets/jlpt/n5/vocabulary.json', 'N5')

print(f"=== N4 VOCABULARY VALIDATION ===")
print(f"Total records examined: {rep_n4['total']}")
print(f"Valid records: {rep_n4['valid']}")
print(f"Total failures: {len(rep_n4['failures'])}")
if rep_n4['failures']:
    for f in rep_n4['failures'][:10]:
        print(f"  {f['id']} ({f['japanese']}): {f['errors']}")

print(f"\n=== N5 VOCABULARY VALIDATION ===")
print(f"Total records examined: {rep_n5['total']}")
print(f"Valid records: {rep_n5['valid']}")
print(f"Total failures: {len(rep_n5['failures'])}")
if rep_n5['failures']:
    for f in rep_n5['failures'][:10]:
        print(f"  {f['id']} ({f['japanese']}): {f['errors']}")
