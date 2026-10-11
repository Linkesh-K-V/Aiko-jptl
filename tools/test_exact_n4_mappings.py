import json

with open('app/src/main/assets/jlpt/n4/grammar.json') as fp:
    n4_g = json.load(fp)

print("n4_g_01 in N4 grammar:", n4_g[0]['japanese'])
print("n4_g_02 in N4 grammar:", n4_g[1]['japanese'])
print("n4_g_04 in N4 grammar:", n4_g[3]['japanese'])
