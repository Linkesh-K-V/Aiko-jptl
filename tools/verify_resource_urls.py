import urllib.request, ssl, sys

# Reputable, stable, verified learning references
candidate_urls = [
    # Tae Kim's Japanese Grammar Guide (Creative Commons, extremely stable URL structure since 2004)
    ("tae_kim_particles_intro", "https://guidetojapanese.org/learn/grammar/particlesintro"),
    ("tae_kim_state_of_being", "https://guidetojapanese.org/learn/grammar/stateofbeing"),
    ("tae_kim_adjectives", "https://guidetojapanese.org/learn/grammar/adjectives"),
    ("tae_kim_verb_basics", "https://guidetojapanese.org/learn/grammar/verbs"),
    ("tae_kim_past_tense", "https://guidetojapanese.org/learn/grammar/pasttense"),
    ("tae_kim_particles_verbs", "https://guidetojapanese.org/learn/grammar/verbparticles"),
    ("tae_kim_potential", "https://guidetojapanese.org/learn/grammar/potential"),
    ("tae_kim_volitional", "https://guidetojapanese.org/learn/grammar/volitional"),
    ("tae_kim_conditionals", "https://guidetojapanese.org/learn/grammar/conditionals"),
    ("tae_kim_te_form", "https://guidetojapanese.org/learn/grammar/compound"),

    # Jisho.org (Authoritative open source Japanese dictionary based on JMdict/KANJIDIC)
    ("jisho_water", "https://jisho.org/word/%E6%B0%B4"),
    ("jisho_eat", "https://jisho.org/word/%E9%A3%9F%E3%81%B9%E3%82%8B"),
    ("jisho_read", "https://jisho.org/word/%E8%AA%AD%E3%82%80"),
    ("jisho_sun", "https://jisho.org/search/%E6%97%A5%20%23kanji"),
    ("jisho_person", "https://jisho.org/search/%E4%BA%BA%20%23kanji"),
    ("jisho_n4_tsugo", "https://jisho.org/word/%E9%83%BD%E5%90%88"),
    ("jisho_n4_junbi", "https://jisho.org/word/%E6%BA%96%E5%82%99"),

    # NHK World Japanese Lessons (Free public broadcast educational material)
    ("nhk_world_lessons", "https://www.nhk.or.jp/lesson/en/"),
    ("nhk_easy_news", "https://www3.nhk.or.jp/news/easy/"),

    # Kanji Alive (Open access educational web app supported by University of Chicago)
    ("kanji_alive_home", "https://kanjialive.com/"),

    # Maggie Sensei (Famous authentic conversational explanations)
    ("maggie_te_form", "https://maggiesensei.com/2010/03/08/request-%E3%80%9C%E3%81%A6%E3%81%8F%E3%81%A0%E3%81%95%E3%81%84-te-kudasai/"),

    # CosCom Japanese (Long-standing guide to Japanese pronunciation and kana)
    ("coscom_hiragana", "https://www.coscom.co.jp/hiragana-katakana/kanatable.html")
]

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

verified = []
failed = []

for tag, url in candidate_urls:
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'})
    try:
        with urllib.request.urlopen(req, timeout=8, context=ctx) as resp:
            if resp.status == 200:
                print(f"VERIFIED (200): [{tag}] -> {url}")
                verified.append((tag, url))
            else:
                print(f"FAILED ({resp.status}): [{tag}] -> {url}")
                failed.append((tag, url, f"Status {resp.status}"))
    except Exception as e:
        print(f"FAILED (Error): [{tag}] -> {url} ({e})")
        failed.append((tag, url, str(e)))

print(f"\nTotal candidate URLs: {len(candidate_urls)}")
print(f"Verified HTTP 200: {len(verified)}")
print(f"Failed: {len(failed)}")
