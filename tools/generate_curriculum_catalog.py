# Let's inspect the exact list of non-N5 planned words
import json

with open('tools/planned_words.json') as fp:
    words = json.load(fp)

print(f"Loaded {len(words)} planned non-N5 words")
