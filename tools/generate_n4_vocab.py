import json, re

# Load assets
with open('app/src/main/assets/jlpt/n5/vocabulary.json') as fp:
    n5_vocab = json.load(fp)
with open('app/src/main/assets/jlpt/n5/kanji.json') as fp:
    n5_kanji = json.load(fp)
with open('app/src/main/assets/jlpt/n4/kanji.json') as fp:
    n4_kanji = json.load(fp)
with open('app/src/main/assets/jlpt/n4/grammar.json') as fp:
    n4_grammar = json.load(fp)
with open('app/src/main/assets/jlpt/n4/vocabulary.json') as fp:
    legacy_n4_vocab = json.load(fp)

n5_words = {w['japanese'] for w in n5_vocab}
n5_readings = {w['reading'] for w in n5_vocab}

n5_k_map = {k['japanese']: k['id'] for k in n5_kanji}
n4_k_map = {k['japanese']: k['id'] for k in n4_kanji}
n4_g_map = {g['japanese']: g['id'] for g in n4_grammar}

print(f"N5 vocab: {len(n5_words)}, N5 kanji: {len(n5_k_map)}, N4 kanji: {len(n4_k_map)}, N4 grammar: {len(n4_g_map)}")
