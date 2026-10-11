import urllib.request, ssl, json

resources = [
    # 1. N5 Grammar: Particles は / が / を / に / で
    {
        "id": "res_g_tae_kim_particles_intro",
        "title": "Introduction to Japanese Particles (は, も, が)",
        "provider": "Tae Kim's Guide to Japanese",
        "url": "https://guidetojapanese.org/learn/grammar/particlesintro",
        "associatedConceptIds": ["n5_g_01", "n5_g_02", "n5_g_03"],
        "jlptLevel": "N5",
        "resourceType": "GRAMMAR_EXPLANATION",
        "language": "en",
        "description": "Comprehensive explanation of topic marker は, inclusive particle も, and subject identifier が with clear contrastive examples.",
        "reasonForRecommendation": "Clear linguistic distinction between topic and grammatical subject, which is essential for N5 learners.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against guidetojapanese.org"
    },
    {
        "id": "res_g_tae_kim_verb_particles",
        "title": "Particles Used with Verbs (を, に, へ, で)",
        "provider": "Tae Kim's Guide to Japanese",
        "url": "https://guidetojapanese.org/learn/grammar/verbparticles",
        "associatedConceptIds": ["n5_g_04", "n5_g_05", "n5_g_06"],
        "jlptLevel": "N5",
        "resourceType": "GRAMMAR_EXPLANATION",
        "language": "en",
        "description": "Explains direct object marker を, target/destination に, direction へ, and location of action で.",
        "reasonForRecommendation": "Directly reinforces Aiko N5 particle lessons with structural syntax diagrams.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against guidetojapanese.org"
    },
    {
        "id": "res_g_tofugu_kosoado",
        "title": "Japanese Demonstratives: The Ko-So-A-Do System",
        "provider": "Tofugu",
        "url": "https://www.tofugu.com/japanese-grammar/kosoado/",
        "associatedConceptIds": ["n5_v_332", "n5_v_333", "n5_v_334", "n5_v_335", "n5_v_336", "n5_v_340"],
        "jlptLevel": "N5",
        "resourceType": "GRAMMAR_EXPLANATION",
        "language": "en",
        "description": "Detailed visual guide to demonstrative pronouns and adjectives (これ, それ, あれ, どれ, この, その, あの, どの).",
        "reasonForRecommendation": "Explains the physical and psychological distance framework of Japanese spatial demonstratives.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against tofugu.com"
    },
    {
        "id": "res_g_tae_kim_te_form",
        "title": "The て-form and Compound Sentences",
        "provider": "Tae Kim's Guide to Japanese",
        "url": "https://guidetojapanese.org/learn/grammar/teform",
        "associatedConceptIds": ["n5_g_12", "n5_g_13", "n5_g_14", "n5_g_15", "n5_g_16"],
        "jlptLevel": "N5",
        "resourceType": "GRAMMAR_EXPLANATION",
        "language": "en",
        "description": "Complete breakdown of verb conjugation into the te-form and its chaining functions.",
        "reasonForRecommendation": "The te-form is the foundational verb bridge for continuous action, permissions, and prohibitions.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against guidetojapanese.org"
    },
    {
        "id": "res_g_tae_kim_adjectives",
        "title": "Adjectives (い-adjectives and な-adjectives)",
        "provider": "Tae Kim's Guide to Japanese",
        "url": "https://guidetojapanese.org/learn/grammar/adjectives",
        "associatedConceptIds": ["n5_g_07", "n5_g_08", "n5_g_09"],
        "jlptLevel": "N5",
        "resourceType": "GRAMMAR_EXPLANATION",
        "language": "en",
        "description": "Covers conjugation, negation, and past tense for both true adjectives and adjectival nouns.",
        "reasonForRecommendation": "Crucial foundation for modifying nouns and forming descriptive predicates.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against guidetojapanese.org"
    },

    # 2. N4 Grammar Concepts
    {
        "id": "res_g_tae_kim_potential",
        "title": "Potential Form: Ability and Possibility (可能形)",
        "provider": "Tae Kim's Guide to Japanese",
        "url": "https://guidetojapanese.org/learn/grammar/potential",
        "associatedConceptIds": ["n4_g_01"],
        "jlptLevel": "N4",
        "resourceType": "GRAMMAR_EXPLANATION",
        "language": "en",
        "description": "Rules for converting Godan, Ichidan, and irregular verbs into the potential form, including particle が usage.",
        "reasonForRecommendation": "Canonical grammar reference for Aiko N4 potential form lesson (n4_g_01).",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against guidetojapanese.org"
    },
    {
        "id": "res_g_tae_kim_conditionals",
        "title": "Conditionals (〜たら, 〜ば, 〜なら, 〜と)",
        "provider": "Tae Kim's Guide to Japanese",
        "url": "https://guidetojapanese.org/learn/grammar/conditionals",
        "associatedConceptIds": ["n4_g_07", "n4_g_08", "n4_g_09"],
        "jlptLevel": "N4",
        "resourceType": "GRAMMAR_EXPLANATION",
        "language": "en",
        "description": "Clear nuance comparison between the four primary Japanese conditional forms.",
        "reasonForRecommendation": "Essential N4 grammar clarification to prevent common learner confusion between たら and ば.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against guidetojapanese.org"
    },
    {
        "id": "res_g_tae_kim_comparison",
        "title": "Comparison and Superlatives (より, ほど, の方が)",
        "provider": "Tae Kim's Guide to Japanese",
        "url": "https://guidetojapanese.org/learn/grammar/comparison",
        "associatedConceptIds": ["n4_g_15", "n4_g_16", "n4_v_16"],
        "jlptLevel": "N4",
        "resourceType": "GRAMMAR_EXPLANATION",
        "language": "en",
        "description": "Syntactic structures for comparative statements (AよりBの方が) and superlative expressions (一番).",
        "reasonForRecommendation": "Directly supports Aiko N4 comparative grammar points and vocabulary like 一番.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against guidetojapanese.org"
    },
    {
        "id": "res_g_tae_kim_desire",
        "title": "Desire and Suggestions (〜たい, 〜たがる, 〜ほしい)",
        "provider": "Tae Kim's Guide to Japanese",
        "url": "https://guidetojapanese.org/learn/grammar/desire",
        "associatedConceptIds": ["n5_g_18", "n4_g_12"],
        "jlptLevel": "N4",
        "resourceType": "GRAMMAR_EXPLANATION",
        "language": "en",
        "description": "Forming expressions of personal desire (〜たい) and expressing third-person observations (〜たがる).",
        "reasonForRecommendation": "Clarifies the key cultural and grammatical rule regarding third-person perspective in Japanese.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against guidetojapanese.org"
    },

    # 3. Kanji & Vocabulary Resources
    {
        "id": "res_k_kanji_alive",
        "title": "Kanji Alive: Web-based Kanji Study Application",
        "provider": "University of Chicago Kanji Alive Project",
        "url": "https://kanjialive.com/",
        "associatedConceptIds": ["n5_k_01", "n5_k_15", "n5_k_18", "n4_k_01", "n4_k_10"],
        "jlptLevel": "N5",
        "resourceType": "KANJI_REFERENCE",
        "language": "en",
        "description": "Comprehensive stroke order animations, radical breakdowns, mnemonics, and audio examples for 1,235 kanji.",
        "reasonForRecommendation": "Outstanding companion for Aiko's Kanji Writing Practice Pad and radical deconstruction cards.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against kanjialive.com"
    },
    {
        "id": "res_v_jisho_main",
        "title": "Jisho.org: Powerful Japanese-English Dictionary",
        "provider": "Jisho.org (Kim Ahlström / JMdict)",
        "url": "https://jisho.org/word/%E6%B0%B4",
        "associatedConceptIds": ["n5_v_32", "n5_k_18"],
        "jlptLevel": "N5",
        "resourceType": "VOCABULARY_REFERENCE",
        "language": "en",
        "description": "Electronic dictionary interface displaying definitions, compound words, pitch accent, and stroke orders.",
        "reasonForRecommendation": "Standard reference tool for deep vocabulary and compound character exploration.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against jisho.org"
    },
    {
        "id": "res_v_jisho_n4_tsugo",
        "title": "Jisho Entry for 都合 (つごう - Circumstances / Convenience)",
        "provider": "Jisho.org",
        "url": "https://jisho.org/word/%E9%83%BD%E5%90%88",
        "associatedConceptIds": ["n4_v_06", "n4_k_10"],
        "jlptLevel": "N4",
        "resourceType": "VOCABULARY_REFERENCE",
        "language": "en",
        "description": "Detailed entry for essential N4 scheduling noun 都合, showcasing collocations like 都合がいい and 都合が悪い.",
        "reasonForRecommendation": "Deepens usage understanding for N4 interpersonal communication.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against jisho.org"
    },
    {
        "id": "res_k_jisho_capital",
        "title": "Jisho Kanji Entry: 都 (Capital / Metropolis)",
        "provider": "Jisho.org",
        "url": "https://jisho.org/search/%E9%83%BD%20%23kanji",
        "associatedConceptIds": ["n4_k_10", "n4_v_27", "n4_v_292"],
        "jlptLevel": "N4",
        "resourceType": "KANJI_REFERENCE",
        "language": "en",
        "description": "Kanji radical breakdown, on/kun readings (ト, ツ, みやこ), and stroke order for N4 kanji 都.",
        "reasonForRecommendation": "Reinforces DEF-19-02 canonicalization for 都 (n4_k_10) and its compound vocabulary.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against jisho.org"
    },

    # 4. Kana & Reading Practice
    {
        "id": "res_kana_tofugu_hira",
        "title": "Learn Hiragana: The Ultimate Guide",
        "provider": "Tofugu",
        "url": "https://www.tofugu.com/japanese/learn-hiragana/",
        "associatedConceptIds": ["kana_h_001", "kana_h_002", "kana_h_003"],
        "jlptLevel": "N5",
        "resourceType": "EXERCISES_AND_QUIZZES",
        "language": "en",
        "description": "Visual mnemonic system and quiz generator for mastering the 46 basic hiragana characters.",
        "reasonForRecommendation": "Perfect companion to Aiko's Tier A Kana-First curriculum foundation.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against tofugu.com"
    },
    {
        "id": "res_kana_tofugu_kata",
        "title": "Learn Katakana: The Ultimate Guide",
        "provider": "Tofugu",
        "url": "https://www.tofugu.com/japanese/learn-katakana/",
        "associatedConceptIds": ["kana_k_001", "kana_k_002", "kana_k_003"],
        "jlptLevel": "N5",
        "resourceType": "EXERCISES_AND_QUIZZES",
        "language": "en",
        "description": "Comprehensive visual mnemonics and practice exercises for all katakana characters.",
        "reasonForRecommendation": "Supports foreign loanword recognition and Aiko katakana drills.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against tofugu.com"
    },
    {
        "id": "res_r_nhk_easy",
        "title": "NHK News Web Easy",
        "provider": "NHK (Japan Broadcasting Corporation)",
        "url": "https://www3.nhk.or.jp/news/easy/",
        "associatedConceptIds": ["n4_r_01", "n4_r_02", "n5_r_01"],
        "jlptLevel": "N4",
        "resourceType": "READING_PRACTICE",
        "language": "ja",
        "description": "Real Japanese daily news rewritten in simplified grammar with ruby furigana and native voiceover audio.",
        "reasonForRecommendation": "The gold standard for extensive reading practice bridging N4 learners into authentic Japanese prose.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against nhk.or.jp"
    },
    {
        "id": "res_l_nhk_world_lessons",
        "title": "Easy Japanese Audio Dialogues (Easy Japanese Series)",
        "provider": "NHK World-Japan",
        "url": "https://www.nhk.or.jp/lesson/en/",
        "associatedConceptIds": ["n5_l_01", "n5_l_02", "n4_l_01"],
        "jlptLevel": "N5",
        "resourceType": "LISTENING_PRACTICE",
        "language": "en",
        "description": "Original audio dramas with situational dialogues, downloadable audio scripts, and grammar points.",
        "reasonForRecommendation": "Authentic conversational listening practice for everyday Japanese situations.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against nhk.or.jp"
    },

    # 5. Formal JLPT Syllabus Reference
    {
        "id": "res_jlpt_official_levels",
        "title": "Official JLPT Level Summary & Can-Do Descriptors",
        "provider": "Japan Foundation & Japan Educational Exchanges and Services",
        "url": "https://www.jlpt.jp/e/about/levelsummary.html",
        "associatedConceptIds": ["n5_exam_deck", "n4_exam_deck"],
        "jlptLevel": "N5",
        "resourceType": "GENERAL_REVIEW",
        "language": "en",
        "description": "Official overview of language competency and proficiency standards tested at levels N5 through N1.",
        "reasonForRecommendation": "Authoritative standard so learners understand exactly what real JLPT certifications test.",
        "verificationStatus": "VERIFIED",
        "lastVerifiedDate": "2026-10-10",
        "provenance": "HTTP 200 validated live against jlpt.jp"
    }
]

# Verify all URLs again live
ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

all_ok = True
for r in resources:
    req = urllib.request.Request(r["url"], headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'})
    try:
        with urllib.request.urlopen(req, timeout=8, context=ctx) as resp:
            if resp.status != 200:
                print(f"FAILED status {resp.status}: {r['url']}")
                all_ok = False
            else:
                print(f"OK (200): {r['id']} -> {r['url']}")
    except Exception as e:
        print(f"FAILED error: {r['url']} -> {e}")
        all_ok = False

if all_ok:
    print(f"\nALL {len(resources)} RESOURCES ARE 100% VERIFIED LIVE!")
    with open("app/src/main/assets/jlpt/further_study_resources.json", "w", encoding="utf-8") as fp:
        json.dump(resources, fp, indent=2, ensure_ascii=False)
    print("Saved to app/src/main/assets/jlpt/further_study_resources.json")
else:
    sys.exit(1)
