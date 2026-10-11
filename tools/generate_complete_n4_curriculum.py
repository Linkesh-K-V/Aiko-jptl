import json, re

# Let's load the N4 kanji planned vocab to ensure 100% of the planned vocab from N4 kanji notes
# are systematically accounted for or integrated into canonical N4 vocabulary!

with open('app/src/main/assets/jlpt/n4/kanji.json', 'r', encoding='utf-8') as f:
    n4_kanji = json.load(f)

with open('app/src/main/assets/jlpt/n5/vocabulary.json', 'r', encoding='utf-8') as f:
    n5_vocab = json.load(f)

n5_words = {w['japanese'] for w in n5_vocab}

planned_by_kanji = {}
for k in n4_kanji:
    note = k.get('mnemonicOrNote', '')
    if 'Planned N4 Vocab:' in note:
        part = note.split('Planned N4 Vocab:')[1].strip()
        words = [w.strip().rstrip('.') for w in part.split(',') if w.strip()]
        planned_by_kanji[k['japanese']] = (k['id'], words)

print(f"Loaded planned vocab for {len(planned_by_kanji)} N4 kanji.")
