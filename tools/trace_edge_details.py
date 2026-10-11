# Let's verify the before vs after edges for n4_k_04, n4_k_07, n4_k_10 and the 6 words
before_edges = {
    ('n4_v_06', 'n5_k_76'),  # 都合 <-> 駅 (SPURIOUS)
    ('n4_v_27', 'n5_k_76'),  # 京都 <-> 駅 (SPURIOUS)
    ('n4_v_59', 'n5_k_60'),  # 動物園 <-> 飲 (SPURIOUS)
    ('n4_v_108', 'n5_k_77'), # 店員 <-> 社 (SPURIOUS)
    ('n4_v_292', 'n5_k_76'), # 首都 <-> 駅 (SPURIOUS)
    # Also in before:
    # n4_v_34 had relatedItems ['n4_k_01', 'n5_k_77', 'n4_k_04' -> n5_k_77]
    # In before, n4_v_34's link to n4_k_04 was merged into existing n5_k_77!
}

after_edges = {
    ('n4_v_06', 'n4_k_10'),  # 都合 <-> 都 (VALID)
    ('n4_v_27', 'n4_k_10'),  # 京都 <-> 都 (VALID)
    ('n4_v_34', 'n4_k_04'),  # 会社員 <-> 員 (RESTORED / SEPARATED)
    ('n4_v_59', 'n4_k_07'),  # 動物園 <-> 園 (VALID)
    ('n4_v_108', 'n4_k_04'), # 店員 <-> 員 (VALID)
    ('n4_v_292', 'n4_k_10'), # 首都 <-> 都 (VALID)
    # Plus cross-level references from n5_v_02 and n5_v_56 in raw JSON:
    ('n4_k_07', 'n5_v_02'),  # 園 <-> 飲む
    ('n4_k_10', 'n5_v_56'),  # 都 <-> 駅
}

print("5 SPURIOUS REMOVED EDGES:")
print("1. n4_v_06 (都合) <-> n5_k_76 (駅)")
print("2. n4_v_27 (京都) <-> n5_k_76 (駅)")
print("3. n4_v_59 (動物園) <-> n5_k_60 (飲)")
print("4. n4_v_108 (店員) <-> n5_k_77 (社)")
print("5. n4_v_292 (首都) <-> n5_k_76 (駅)")

print("\n3 RESTORED / ADDED EDGES TO THE GRAPH UNIQUE EDGE SET:")
print("1. n4_v_292 (首都) <-> n4_k_10 (都) [Restored unique link between 首都 and 都]")
print("2. n4_k_07 (園) <-> n5_v_02 (飲む) [Uncanonicalized raw link]")
print("3. n4_k_10 (都) <-> n5_v_56 (駅) [Uncanonicalized raw link]")
print("Note: ('n4_v_06', 'n4_k_10') and ('n4_v_27', 'n4_k_10') already existed from n4_k_10's own outgoing relatedItems ['n4_v_27', 'n4_v_06'], so they were already in the undirected set.")
print("Note: ('n4_v_34', 'n4_k_04'), ('n4_v_59', 'n4_k_07'), ('n4_v_108', 'n4_k_04') already existed from the kanji's own outgoing relatedItems, so they were already counted in the undirected set.")
print("Net edge difference: -5 removed + 3 added = -2 net edges (from 2334 to 2332).")
