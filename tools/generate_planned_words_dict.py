import json

with open('app/src/main/assets/jlpt/n4/kanji.json') as fp:
    n4_kanji = json.load(fp)

with open('app/src/main/assets/jlpt/n5/vocabulary.json') as fp:
    n5_vocab = json.load(fp)

n5_words = {w['japanese'] for w in n5_vocab}

all_planned = {}
for k in n4_kanji:
    note = k.get('mnemonicOrNote', '')
    if 'Planned N4 Vocab:' in note:
        part = note.split('Planned N4 Vocab:')[1].strip()
        words = [w.strip().rstrip('.') for w in part.split(',') if w.strip()]
        for w in words:
            if w not in n5_words:
                if w not in all_planned:
                    all_planned[w] = []
                all_planned[w].append(k['japanese'])

print(f"Total non-N5 planned words to incorporate: {len(all_planned)}")
with open('tools/planned_words.json', 'w') as out:
    json.dump(all_planned, out, ensure_ascii=False, indent=2)
print("Saved to tools/planned_words.json")
