#!/usr/bin/env python3
import json
import glob
import re

def analyze():
    items = []
    items_by_id = {}
    for f in sorted(glob.glob("app/src/main/assets/jlpt/*/*.json")):
        d = json.load(open(f, "r", encoding="utf-8"))
        for it in d:
            it["_file"] = f
            items.append(it)
            items_by_id[it["id"]] = it

    kanji_items = [it for it in items if it["category"] == "KANJI"]
    vocab_items = [it for it in items if it["category"] == "VOCAB"]
    grammar_items = [it for it in items if it["category"] == "GRAMMAR"]
    reading_items = [it for it in items if it["category"] == "READING"]
    listening_items = [it for it in items if it["category"] == "LISTENING"]
    exam_items = [it for it in items if it["category"] == "JLPT_EXAM"]

    print(f"Items: Kanji={len(kanji_items)}, Vocab={len(vocab_items)}, Grammar={len(grammar_items)}, Reading={len(reading_items)}, Listening={len(listening_items)}, Exam={len(exam_items)}")

    # 1. Kanji in Vocab
    kanji_vocab_edges = []
    for k in kanji_items:
        char = k["japanese"].strip()
        for v in vocab_items:
            v_jp = v["japanese"].strip()
            if char in v_jp:
                kanji_vocab_edges.append((k["id"], v["id"], f"{char} in {v_jp}"))

    print(f"1. Kanji -> Vocab edges: {len(kanji_vocab_edges)}")

    # 2. Vocab in Reading
    vocab_reading_edges = []
    for r in reading_items:
        passage = r["japanese"]
        for v in vocab_items:
            v_jp = v["japanese"].strip()
            if len(v_jp) >= 2 or (len(v_jp) == 1 and "\u4e00" <= v_jp <= "\u9fff"):
                if v_jp in passage:
                    vocab_reading_edges.append((v["id"], r["id"], f"{v_jp} in reading {r['id']}"))

    print(f"2. Vocab -> Reading edges: {len(vocab_reading_edges)}")

    # 3. Grammar in Reading
    grammar_reading_edges = []
    for r in reading_items:
        passage = r["japanese"]
        for g in grammar_items:
            g_clean = g["japanese"].strip().replace("〜", "").replace("~", "").strip()
            if len(g_clean) >= 2 and g_clean in passage:
                grammar_reading_edges.append((g["id"], r["id"], f"{g_clean} in reading {r['id']}"))

    print(f"3. Grammar -> Reading edges: {len(grammar_reading_edges)}")

    # 4. Vocab in Listening
    vocab_listening_edges = []
    for l in listening_items:
        dialogue = l["japanese"]
        for v in vocab_items:
            v_jp = v["japanese"].strip()
            if len(v_jp) >= 2 or (len(v_jp) == 1 and "\u4e00" <= v_jp <= "\u9fff"):
                if v_jp in dialogue:
                    vocab_listening_edges.append((v["id"], l["id"], f"{v_jp} in listening {l['id']}"))

    print(f"4. Vocab -> Listening edges: {len(vocab_listening_edges)}")

    # 5. Grammar in Listening
    grammar_listening_edges = []
    for l in listening_items:
        dialogue = l["japanese"]
        for g in grammar_items:
            g_clean = g["japanese"].strip().replace("〜", "").replace("~", "").strip()
            if len(g_clean) >= 2 and g_clean in dialogue:
                grammar_listening_edges.append((g["id"], l["id"], f"{g_clean} in listening {l['id']}"))

    print(f"5. Grammar -> Listening edges: {len(grammar_listening_edges)}")

    # 6. Exam question -> Knowledge item
    exam_knowledge_edges = []
    for q in exam_items:
        q_jp = q["japanese"]
        prompt = q.get("examQuestionPrompt", "")
        opts = q.get("options", [])
        
        # Check grammar
        for g in grammar_items:
            g_raw = g["japanese"].strip()
            g_clean = g_raw.replace("〜", "").replace("~", "").strip()
            # Match if tested explicitly in options or prompt
            matched = False
            for opt in opts:
                if (len(g_clean) >= 2 and g_clean in opt) or (opt in g_clean and len(opt) >= 3):
                    exam_knowledge_edges.append((q["id"], g["id"], f"Exam tests grammar pattern {g_raw}"))
                    matched = True
                    break
            if not matched and (g_clean in q_jp or g_clean in prompt):
                if len(g_clean) >= 3:
                    exam_knowledge_edges.append((q["id"], g["id"], f"Exam prompt contains grammar {g_raw}"))
        
        # Check vocab
        for v in vocab_items:
            v_jp = v["japanese"].strip()
            # If tested in options
            matched = False
            for opt in opts:
                if opt == v_jp:
                    exam_knowledge_edges.append((q["id"], v["id"], f"Exam option tests vocab {v_jp}"))
                    matched = True
                    break
                elif len(v_jp) >= 2 and (opt.startswith(v_jp) or v_jp.startswith(opt)):
                    # Conjugation match
                    exam_knowledge_edges.append((q["id"], v["id"], f"Exam option tests conjugated vocab {v_jp}"))
                    matched = True
                    break
            if not matched and len(v_jp) >= 2 and v_jp in q_jp:
                exam_knowledge_edges.append((q["id"], v["id"], f"Exam question sentence uses vocab {v_jp}"))

    print(f"6. Exam -> Knowledge edges: {len(exam_knowledge_edges)}")

    # 7. Vocab in Grammar example sentences / Grammar in Vocab example sentences
    vocab_grammar_edges = []
    for g in grammar_items:
        ex = g.get("exampleJapanese", "")
        for v in vocab_items:
            v_jp = v["japanese"].strip()
            if len(v_jp) >= 2 and v_jp in ex:
                vocab_grammar_edges.append((v["id"], g["id"], f"{v_jp} in grammar example of {g['id']}"))

    print(f"7. Vocab in Grammar examples: {len(vocab_grammar_edges)}")

if __name__ == "__main__":
    analyze()
