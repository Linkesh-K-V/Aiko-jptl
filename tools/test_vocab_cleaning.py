import json

with open('app/src/main/assets/jlpt/n5/vocabulary.json') as fp:
    n5_v = json.load(fp)

# In n5_v_02 (飲む):
# relatedItems has ['n4_k_07', 'n5_k_60']
# In n5_v_56 (駅):
# relatedItems has ['n4_g_10', 'n4_k_10', 'n5_k_76', 'n5_l_02', 'n5_r_03', 'n5_k_66']

print("Notice that n5_v_02 already contains the canonical N5 kanji 'n5_k_60' (飲)!")
print("And n5_v_56 already contains the canonical N5 kanji 'n5_k_76' (駅)!")
