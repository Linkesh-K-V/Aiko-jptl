#!/usr/bin/env python3
import json
import os
from collections import defaultdict

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
N5_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "jlpt", "n5")

# 1. Load Vocabulary
vocab_path = os.path.join(N5_DIR, "vocabulary.json")
vocabs = json.load(open(vocab_path, "r", encoding="utf-8"))

# Group by (japanese, reading)
word_map = defaultdict(list)
for v in vocabs:
    key = (v["japanese"].strip(), v["reading"].strip())
    word_map[key].append(v)

dup_map = {} # duplicate_id -> canonical_id
for k, v in word_map.items():
    if len(v) > 1:
        s = sorted(v, key=lambda x: int(x["id"].split("_")[-1]))
        canonical = s[0]
        duplicate = s[1]
        dup_map[duplicate["id"]] = canonical["id"]

print(f"Identified {len(dup_map)} duplicate vocabulary pairs.")

# Manual romaji mapping for legacy items that did not have duplicates
LEGACY_ROMAJI = {
    "n5_v_17": "ryoushin",
    "n5_v_18": "kyoudai",
    "n5_v_20": "oniisan",
    "n5_v_22": "oneesan",
    "n5_v_25": "kodomo",
    "n5_v_26": "otona",
    "n5_v_32": "mizu",
    "n5_v_35": "koohii",
    "n5_v_37": "ryouri",
    "n5_v_50": "getsuyoubi",
    "n5_v_51": "kinyoubi",
    "n5_v_52": "nichiyoubi",
    "n5_v_59": "yuubinkyoku",
    "n5_v_70": "benkyousuru",
    "n5_v_89": "ohayou gozaimasu",
    "n5_v_90": "konnichiwa",
    "n5_v_91": "konbanwa",
    "n5_v_92": "arigatou gozaimasu",
    "n5_v_93": "sumimasen",
    "n5_v_94": "onegaishimasu",
    "n5_v_95": "douitashimashite",
    "n5_v_96": "itadakimasu",
    "n5_v_97": "gochisousamadeshita"
}

# Merge duplicate metadata into canonical records
canonical_records = {}
duplicate_records = {}

for v in vocabs:
    vid = v["id"]
    if vid in dup_map:
        duplicate_records[vid] = v
    else:
        canonical_records[vid] = v

for dup_id, can_id in dup_map.items():
    can = canonical_records[can_id]
    dup = duplicate_records[dup_id]
    
    # 1. Romaji
    if not can.get("romaji"):
        if dup.get("romaji"):
            can["romaji"] = dup["romaji"]
        elif can_id in LEGACY_ROMAJI:
            can["romaji"] = LEGACY_ROMAJI[can_id]
            
    # 2. Related items merge
    can_rels = can.get("relatedItems", [])
    if isinstance(can_rels, str):
        can_rels = [r.strip() for r in can_rels.split(",") if r.strip()]
    dup_rels = dup.get("relatedItems", [])
    if isinstance(dup_rels, str):
        dup_rels = [r.strip() for r in dup_rels.split(",") if r.strip()]
        
    merged_rels = []
    for r in can_rels + dup_rels:
        mapped_r = dup_map.get(r, r)
        if mapped_r != can_id and mapped_r not in merged_rels:
            merged_rels.append(mapped_r)
    can["relatedItems"] = merged_rels
    
    # 3. Tags merge
    can_tags = can.get("tags", "")
    dup_tags = dup.get("tags", "")
    all_tags = set([t.strip() for t in (can_tags + "," + dup_tags).split(",") if t.strip()])
    all_tags.add("STATUS:SOURCE_VERIFIED")
    can["tags"] = ",".join(sorted(all_tags))
    
    # 4. Examples & POS
    if not can.get("exampleJapanese") and dup.get("exampleJapanese"):
        can["exampleJapanese"] = dup["exampleJapanese"]
        can["exampleReading"] = dup.get("exampleReading", "")
        can["exampleRomaji"] = dup.get("exampleRomaji", "")
        can["exampleEnglish"] = dup.get("exampleEnglish", "")
    if not can.get("partOfSpeech") and dup.get("partOfSpeech"):
        can["partOfSpeech"] = dup["partOfSpeech"]

# Fill remaining legacy romaji
for vid, can in canonical_records.items():
    if not can.get("romaji") and vid in LEGACY_ROMAJI:
        can["romaji"] = LEGACY_ROMAJI[vid]

# Fix missing relationships for isolated/under-linked items:
if "n5_v_17" in canonical_records: # 両親
    rels = canonical_records["n5_v_17"].get("relatedItems", [])
    for r in ["n5_k_60", "n5_v_19", "n5_v_23"]: # 親, 兄, 弟
        if r not in rels: rels.append(r)
    canonical_records["n5_v_17"]["relatedItems"] = rels

if "n5_v_18" in canonical_records: # 兄弟
    rels = canonical_records["n5_v_18"].get("relatedItems", [])
    for r in ["n5_k_72"]: # 兄
        if r not in rels: rels.append(r)
    canonical_records["n5_v_18"]["relatedItems"] = rels

if "n5_v_37" in canonical_records: # 料理
    rels = canonical_records["n5_v_37"].get("relatedItems", [])
    for r in ["n5_k_88"]: # 料
        if r not in rels: rels.append(r)
    canonical_records["n5_v_37"]["relatedItems"] = rels

if "n5_v_59" in canonical_records: # 郵便局
    rels = canonical_records["n5_v_59"].get("relatedItems", [])
    for r in ["n5_k_66", "n5_r_10"]:
        if r not in rels: rels.append(r)
    canonical_records["n5_v_59"]["relatedItems"] = rels

# Save deduplicated vocabulary
new_vocab_list = list(canonical_records.values())
print(f"New Vocabulary count: {len(new_vocab_list)} (was {len(vocabs)})")
with open(vocab_path, "w", encoding="utf-8") as f:
    json.dump(new_vocab_list, f, ensure_ascii=False, indent=2)

# Helper to canonicalize relationships across all files
def canonicalize_file_relationships(file_path):
    if not os.path.exists(file_path): return
    data = json.load(open(file_path, "r", encoding="utf-8"))
    modified = False
    for item in data:
        item_id = item["id"]
        rels = item.get("relatedItems", [])
        if isinstance(rels, str):
            rels = [r.strip() for r in rels.split(",") if r.strip()]
        new_rels = []
        for r in rels:
            mr = dup_map.get(r, r)
            if mr != item_id and mr not in new_rels:
                new_rels.append(mr)
        if new_rels != rels:
            item["relatedItems"] = new_rels
            modified = True
    if modified:
        with open(file_path, "w", encoding="utf-8") as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
        print(f"Canonicalized relationships in {os.path.basename(file_path)}")

for fname in ["kanji.json", "reading.json", "listening.json", "questions.json"]:
    canonicalize_file_relationships(os.path.join(N5_DIR, fname))

# Check and canonicalize other levels
for lvl in ["n4", "n3", "n2", "n1"]:
    lvl_dir = os.path.join(BASE_DIR, "app", "src", "main", "assets", "jlpt", lvl)
    if os.path.exists(lvl_dir):
        for fname in os.listdir(lvl_dir):
            if fname.endswith(".json"):
                canonicalize_file_relationships(os.path.join(lvl_dir, fname))

# 2. Fix Exam Question Duplicate: n5_q_22
questions_path = os.path.join(N5_DIR, "questions.json")
questions = json.load(open(questions_path, "r", encoding="utf-8"))
for q in questions:
    if q["id"] == "n5_q_22":
        q["japanese"] = "文法: 助詞「へ」"
        q["examQuestionType"] = "Contextual Grammar"
        q["examQuestionPrompt"] = "来週、京都＿＿旅行に行きます。"
        q["options"] = ["へ", "を", "から", "で"]
        q["meaning"] = "Next week, I am going on a trip to Kyoto. (Target particle: へ for direction of movement)."
        q["mnemonicOrNote"] = "Particle へ indicates direction of motion towards a destination."
        q["exampleJapanese"] = "来週、京都へ旅行に行きます。"
        q["exampleEnglish"] = "Next week, I am going on a trip to Kyoto."
        q["relatedItems"] = ["n5_g_03", "n5_v_03", "n5_v_188"]
        print("Updated n5_q_22 to test particle へ instead of duplicating と.")

with open(questions_path, "w", encoding="utf-8") as f:
    json.dump(questions, f, ensure_ascii=False, indent=2)

# 3. Populate Grammar Formation Rules (Structure) for all 45 items in n5/grammar.json
grammar_path = os.path.join(N5_DIR, "grammar.json")
grammars = json.load(open(grammar_path, "r", encoding="utf-8"))

STRUCTURE_MAP = {
    "n5_g_01": "Noun1 + は + Noun2 / Adjective + です",
    "n5_g_02": "Noun (Direct Object) + を + Transitive Verb",
    "n5_g_03": "Place + に / へ + Motion Verb (行く/来る/帰る) | Time + に + Action Verb",
    "n5_g_04": "Place + で + Action Verb | Noun (Tool/Transport) + で + Verb",
    "n5_g_05": "Verb [て-form] + ください",
    "n5_g_06": "Verb [て-form] + もいいですか",
    "n5_g_07": "Noun + も",
    "n5_g_08": "Noun1 + の + Noun2",
    "n5_g_09": "Noun (Inanimate) + があります | Noun (Animate) + がいます",
    "n5_g_10": "Time/Place 1 + から + Time/Place 2 + まで",
    "n5_g_11": "Verb stem + ましょう / ましょうか",
    "n5_g_12": "Verb stem + たいです",
    "n5_g_13": "Verb [て-form] + はいけません",
    "n5_g_14": "Verb [て-form] + います / いる",
    "n5_g_15": "Verb [ない-form] + でください",
    "n5_g_16": "Verb [ない-form drop い] + ければなりません",
    "n5_g_17": "Verb [Dictionary form] + ことができます",
    "n5_g_18": "Verb [た-form] + ことがあります",
    "n5_g_19": "Verb1 [た-form] + り + Verb2 [た-form] + り + します",
    "n5_g_20": "Verb [Dictionary form] + 前に | Verb [た-form] + 後で",
    "n5_g_21": "Sentence [Plain/Polite] + から | Sentence [Plain] + ので",
    "n5_g_22": "Noun1 + は + Noun2 + より + Adjective + です | Noun1 + のほうが + Noun2 + より + Adjective",
    "n5_g_23": "Category + の中で + Noun + が一番 + Adjective + です",
    "n5_g_24": "Noun + が + 好きです / 嫌いです",
    "n5_g_25": "Negative: い-Adj [drop い] + くないです | Past: い-Adj [drop い] + かったです | Past Neg: + くなかったです",
    "n5_g_26": "Negative: な-Adj + ではありません | Past: な-Adj + でした | Past Neg: + ではありませんでした",
    "n5_g_27": "Past: Verb stem + ました | Past Neg: Verb stem + ませんでした",
    "n5_g_28": "Verb1 [て-form] + Verb2 (Sequential actions)",
    "n5_g_29": "Group 1: u-ending | Group 2: -iru/-eru → stem + ru | Group 3: する / くる",
    "n5_g_30": "Group 1: a-stem + ない | Group 2: stem + ない | Group 3: しない / こない",
    "n5_g_31": "Group 1: te-form rules with -ta/-da | Group 2: stem + た | Group 3: した / きた",
    "n5_g_32": "Place + へ / に + Verb stem / Noun + に + 行く / 来る",
    "n5_g_33": "Noun1 + と + Noun2 (Listing) | Person + と + Verb (Together with)",
    "n5_g_34": "Noun1 + や + Noun2 + など",
    "n5_g_35": "まだ + Verb [て-form] + いません",
    "n5_g_36": "もう + Verb [た-form / ました]",
    "n5_g_37": "Verb stem + すぎます / すぎる | い-Adj [drop い] + すぎます | な-Adj stem + すぎます",
    "n5_g_38": "Verb [Dictionary form / ない-form] + つもりです",
    "n5_g_39": "Sentence [Plain form / Noun / な-Adj without だ] + でしょう",
    "n5_g_40": "Noun1 + と + Noun2 + とどちらが + Adjective + ですか",
    "n5_g_41": "Verb stem + 方 (かた)",
    "n5_g_42": "[Period/Duration] + に + [Frequency] 回 + Verb (e.g. 1週間に2回)",
    "n5_g_43": "Verb [Dictionary / た / ない-form] + 時 | い-Adj + 時 | Noun + の時",
    "n5_g_44": "Verb [て-form] + も | い-Adj [drop い] + くても | Noun / な-Adj + でも",
    "n5_g_45": "Verb stem + ながら + Verb2"
}

# Connect grammar items to their underlying prerequisite forms
PREREQ_GRAMMAR_MAP = {
    "n5_g_06": ["n5_g_05", "n5_g_28"],
    "n5_g_12": ["n5_g_27"],
    "n5_g_13": ["n5_g_05", "n5_g_28"],
    "n5_g_14": ["n5_g_28"],
    "n5_g_15": ["n5_g_30"],
    "n5_g_18": ["n5_g_31"],
    "n5_g_19": ["n5_g_31"]
}

for g in grammars:
    gid = g["id"]
    if gid in STRUCTURE_MAP:
        g["structure"] = STRUCTURE_MAP[gid]
    
    # Canonicalize existing relations
    rels = g.get("relatedItems", [])
    if isinstance(rels, str):
        rels = [r.strip() for r in rels.split(",") if r.strip()]
    canon_rels = []
    for r in rels:
        mr = dup_map.get(r, r)
        if mr != gid and mr not in canon_rels:
            canon_rels.append(mr)
    
    # Add prerequisite forms
    if gid in PREREQ_GRAMMAR_MAP:
        for pr in PREREQ_GRAMMAR_MAP[gid]:
            if pr not in canon_rels and pr != gid:
                canon_rels.append(pr)
    
    g["relatedItems"] = canon_rels

with open(grammar_path, "w", encoding="utf-8") as f:
    json.dump(grammars, f, ensure_ascii=False, indent=2)

print(f"Populated structures and updated relationships for all {len(grammars)} grammar items.")

print("Curriculum deduplication and grammar repair complete.")
