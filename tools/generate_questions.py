import json

with open("app/src/main/assets/jlpt/n5/questions.json") as f:
    existing = json.load(f)

print(f"Existing questions: {len(existing)}")

# Untested grammar:
# n5_g_34: 〜や〜など
# n5_g_27: 動詞の過去形
# n5_g_11: 〜ましょう / 〜ましょうか
# n5_g_06: 〜てもいいですか
# n5_g_14: 〜ている
# n5_g_35: 〜まだ〜ていません
# n5_g_44: 〜ても
# n5_g_36: 〜もう〜ました
# n5_g_26: な形容詞の活用
# n5_g_22: 〜より / 〜のほうが
# n5_g_40: 〜と〜とどちらが〜ですか
# n5_g_37: 〜すぎる
# n5_g_39: 〜でしょう
# n5_g_41: 〜方
# n5_g_42: 期間・回数の表現
# n5_g_43: 〜時
