import json, re

# Load assets
with open('app/src/main/assets/jlpt/n5/vocabulary.json', 'r', encoding='utf-8') as f:
    n5_vocab = json.load(f)
with open('app/src/main/assets/jlpt/n5/kanji.json', 'r', encoding='utf-8') as f:
    n5_kanji = json.load(f)
with open('app/src/main/assets/jlpt/n4/kanji.json', 'r', encoding='utf-8') as f:
    n4_kanji = json.load(f)
with open('app/src/main/assets/jlpt/n4/grammar.json', 'r', encoding='utf-8') as f:
    n4_grammar = json.load(f)

n5_words = {w['japanese'] for w in n5_vocab}
n5_readings = {w['reading'] for w in n5_vocab}
n5_k_map = {k['japanese']: k['id'] for k in n5_kanji}
n4_k_map = {k['japanese']: k['id'] for k in n4_kanji}
n4_g_map = {g['japanese']: g['id'] for g in n4_grammar}

print(f"Setup verified. N5 words={len(n5_words)}, N4 kanji={len(n4_k_map)}")
