import urllib.request, ssl

urls = [
    # Tae Kim accurate paths
    ("tae_kim_te_form_verbs", "https://guidetojapanese.org/learn/grammar/teform"),
    ("tae_kim_suru", "https://guidetojapanese.org/learn/grammar/suru"),
    ("tae_kim_giving_receiving", "https://guidetojapanese.org/learn/grammar/givingreceiving"),
    ("tae_kim_desire", "https://guidetojapanese.org/learn/grammar/desire"),
    ("tae_kim_potential_verbs", "https://guidetojapanese.org/learn/grammar/potential"),
    ("tae_kim_comparison", "https://guidetojapanese.org/learn/grammar/comparison"),
    
    # Jisho vocabulary & kanji
    ("jisho_hon", "https://jisho.org/word/%E6%9C%AC"),
    ("jisho_gohan", "https://jisho.org/word/%E3%81%94%E9%A3%AF"),
    ("jisho_kyoto", "https://jisho.org/word/%E4%BA%AC%E9%83%BD"),
    ("jisho_kaishain", "https://jisho.org/word/%E4%BC%9A%E7%社%E5%93%A1"), # check url encoding
    ("jisho_doubutsuen", "https://jisho.org/word/%E5%8B%95%E7%89%A9%E5%9C%92"),
    
    # University of Texas Japanese Grammar Guide
    ("utexas_particles", "https://www.laits.utexas.edu/japanese/joshu/"),
    
    # Tofugu Grammar guides (high quality stable blog)
    ("tofugu_hiragana", "https://www.tofugu.com/japanese/learn-hiragana/"),
    ("tofugu_katakana", "https://www.tofugu.com/japanese/learn-katakana/"),
    ("tofugu_te_form", "https://www.tofugu.com/japanese-grammar/te-form/"),
    ("tofugu_wa_vs_ga", "https://www.tofugu.com/japanese-grammar/wa-vs-ga/"),
    ("tofugu_kanji_radicals", "https://www.tofugu.com/japanese/kanji-radicals-mnemonic/"),
    ("tofugu_kosoado", "https://www.tofugu.com/japanese-grammar/kosoado/"),
    
    # Official JLPT Information Page (Japan Foundation / JEES)
    ("official_jlpt_summary", "https://www.jlpt.jp/e/about/levelsummary.html")
]

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

for tag, url in urls:
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'})
    try:
        with urllib.request.urlopen(req, timeout=8, context=ctx) as resp:
            print(f"{resp.status}: [{tag}] -> {url}")
    except Exception as e:
        print(f"ERR: [{tag}] -> {url} ({e})")
