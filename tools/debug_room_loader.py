import json, re

# Check JlptAssetContentLoader.kt lines 60-120
with open('app/src/main/java/com/example/noignore/japanese/curriculum/JlptAssetContentLoader.kt') as fp:
    txt = fp.read()
    print("Loader file length:", len(txt))
