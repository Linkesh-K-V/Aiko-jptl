import urllib.request, ssl, json, sys

with open("app/src/main/assets/jlpt/further_study_resources.json", "r", encoding="utf-8") as f:
    resources = json.load(f)

print(f"Total Resources: {len(resources)}")

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

results = []
for res in resources:
    url = res["url"]
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'})
    try:
        with urllib.request.urlopen(req, timeout=10, context=ctx) as resp:
            final_url = resp.geturl()
            status = resp.status
            content = resp.read(2048).decode('utf-8', errors='ignore')
            results.append({
                "id": res["id"],
                "url": url,
                "final_url": final_url,
                "status": status,
                "redirected": (url != final_url),
                "title": res["title"],
                "provider": res["provider"],
                "relevance_snippet": content[:150].replace('\n', ' ')
            })
    except Exception as e:
        results.append({
            "id": res["id"],
            "url": url,
            "status": f"ERROR: {e}",
            "redirected": False,
            "title": res["title"],
            "provider": res["provider"]
        })

print(f"{'ID':<30} | {'STATUS':<8} | {'REDIRECT':<8} | {'FINAL DESTINATION'}")
print("-" * 100)
for r in results:
    stat = str(r['status'])
    redir = "YES" if r.get('redirected') else "NO"
    dest = r.get('final_url', r.get('url'))
    print(f"{r['id']:<30} | {stat:<8} | {redir:<8} | {dest}")
