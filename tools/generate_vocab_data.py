import json

# Let's verify how many planned words are covered by the 175 N4 kanji:
with open('app/src/main/assets/jlpt/n4/kanji.json') as fp:
    n4_k = json.load(fp)

kanji_planned = {}
for k in n4_k:
    note = k.get('mnemonicOrNote', '')
    if 'Planned N4 Vocab:' in note:
        part = note.split('Planned N4 Vocab:')[1].strip()
        words = [w.strip().rstrip('.') for w in part.split(',') if w.strip()]
        kanji_planned[k['japanese']] = (k['id'], words)

print("Planned kanji map size:", len(kanji_planned))
