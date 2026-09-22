package com.example.noignore.japanese.model

enum class JlptExamLevel(
    val code: String,
    val title: String,
    val japaneseTitle: String,
    val difficulty: String,
    val kanjiCount: Int,
    val vocabCount: Int,
    val grammarPoints: Int,
    val colorHex: Long,
    val targetDescription: String,
    val teacherAikoAdvice: String,
    val keyTopics: List<String>,
    val defaultPresetDirectives: List<String>
) {
    N5(
        code = "N5",
        title = "JLPT N5",
        japaneseTitle = "日本語能力試験 N5",
        difficulty = "Foundations",
        kanjiCount = 100,
        vocabCount = 800,
        grammarPoints = 80,
        colorHex = 0xFF2E7D32, // Forest Green
        targetDescription = "Master Hiragana, Katakana, ~100 core kanji, and essential everyday survival Japanese.",
        teacherAikoAdvice = "Konnichiwa! Teacher Aiko here. For N5, your golden key is mastering the fundamental particles (は, を, が, に, で) and saying verbs in their polite ます/ません forms aloud every day. 継続は力なり (Continuance is power)!",
        keyTopics = listOf("Kana & Dakuten", "Numbers & Counters", "Basic Particles (は/を/に/で)", "Verbs (ます/ません/ました)", "Adjectives (い/な)"),
        defaultPresetDirectives = listOf(
            "Review 10 N5 Foundational Kanji",
            "Master N5 Particles (は vs が drill)",
            "Practice Hiragana & Katakana Dakuten",
            "Drill 15 N5 Core Daily Vocabulary"
        )
    ),
    N4(
        code = "N4",
        title = "JLPT N4",
        japaneseTitle = "日本語能力試験 N4",
        difficulty = "Elementary",
        kanjiCount = 300,
        vocabCount = 1500,
        grammarPoints = 130,
        colorHex = 0xFF0277BD, // Indigo Blue
        targetDescription = "Understand everyday conversations spoken at slow/natural speed and basic reading materials.",
        teacherAikoAdvice = "Welcome to N4! Teacher Aiko recommends drilling the 'Te-form' until it becomes muscle memory. It connects actions, makes polite requests (~てください), and forms ongoing states (~ている)!",
        keyTopics = listOf("Te-Form Conjugation", "Potential Form (〜られる)", "Giving & Receiving (あげる/くれる/もらう)", "Volitional & Conditional (~たら/〜ば)", "Daily Routines"),
        defaultPresetDirectives = listOf(
            "Te-Form verb conjugation blitz",
            "Potential form (〜られる) sentences",
            "Drill Giving & Receiving (あげる/もらう)",
            "Review 20 N4 Elementary Words"
        )
    ),
    N3(
        code = "N3",
        title = "JLPT N3",
        japaneseTitle = "日本語能力試験 N3",
        difficulty = "Intermediate",
        kanjiCount = 650,
        vocabCount = 3750,
        grammarPoints = 200,
        colorHex = 0xFFE65100, // Rich Amber Orange
        targetDescription = "The bridge to fluency! Comprehend everyday situations and intermediate articles.",
        teacherAikoAdvice = "N3 is the most important bridge in Japanese! Teacher Aiko reminds you to master transitive vs. intransitive verb pairs (開ける vs 開く) and nuanced conjunctions like わけ vs はず.",
        keyTopics = listOf("Transitive / Intransitive Pairs", "Basic Keigo (Honorifics/Humble)", "Nuance Connectors (わけ/はず/よう)", "Compound Kanji (熟語)", "Short Reading Comprehension"),
        defaultPresetDirectives = listOf(
            "Transitive vs Intransitive verb pairs",
            "N3 Nuance connectors (わけ vs はず)",
            "Keigo introduction (敬語 & 謙譲語)",
            "15-minute N3 Reading Comprehension"
        )
    ),
    N2(
        code = "N2",
        title = "JLPT N2",
        japaneseTitle = "日本語能力試験 N2",
        difficulty = "Upper Intermediate",
        kanjiCount = 1000,
        vocabCount = 6000,
        grammarPoints = 220,
        colorHex = 0xFF6A1B9A, // Royal Purple
        targetDescription = "Business-level Japanese, news articles, commentary, and rapid native speech.",
        teacherAikoAdvice = "Gambatte! N2 opens professional doors in Japan. Teacher Aiko advises reading authentic Japanese editorials and practicing listening to announcements at 1.2x speed.",
        keyTopics = listOf("Business Keigo", "Formal Discourse Markers", "Compound Particles (〜につれて/〜にわたって)", "Newspaper & Editorial Reading", "Implicit Context Listening"),
        defaultPresetDirectives = listOf(
            "Compound particles (〜につれて/〜に関して)",
            "N2 Newspaper editorial reading",
            "Business Keigo email patterns",
            "N2 Fast-paced listening drill"
        )
    ),
    N1(
        code = "N1",
        title = "JLPT N1",
        japaneseTitle = "日本語能力試験 N1",
        difficulty = "Advanced Fluency",
        kanjiCount = 2000,
        vocabCount = 10000,
        grammarPoints = 250,
        colorHex = 0xFFB71C1C, // Deep Crimson
        targetDescription = "Native-level mastery: complex academic prose, literary expressions, and nuanced debate.",
        teacherAikoAdvice = "The pinnacle of Japanese proficiency! Teacher Aiko is so proud of your dedication. Focus on four-character idioms (四字熟語), classical nuances (〜ずにはいられない), and speed reading.",
        keyTopics = listOf("Four-character Idioms (四字熟語)", "Archaic & Literary Grammar", "Fine Nuance Synonyms", "Complex Logical Arguments", "High-speed Native Discourse"),
        defaultPresetDirectives = listOf(
            "Four-character idioms (四字熟語 blitz)",
            "Literary grammar (〜ずにはいられない)",
            "N1 Philosophical essay reading",
            "Advanced nuance & synonym comparison"
        )
    );

    companion object {
        fun fromCode(code: String): JlptExamLevel =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: N5
    }
}
