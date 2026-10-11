package com.example.noignore.reminder

data class AikoAntiProcrastinationQuote(
    val id: String,
    val japaneseHeading: String,
    val message: String,
    val proTip: String,
    val tone: AikoReminderTone
)

enum class AikoReminderTone(val displayName: String, val emoji: String) {
    WARM_ENCOURAGING("Warm & Gentle", "🌸"),
    SPIRITED_COACH("Spirited Coach", "⚡"),
    PROVERB_WISDOM("Proverb Wisdom", "🎌")
}

object TeacherAikoQuoteRepository {

    val allQuotes = listOf(
        // Warm & Gentle
        AikoAntiProcrastinationQuote(
            id = "warm_1",
            japaneseHeading = "🌸 一緒に頑張りましょう！ (Let's do our best together!)",
            message = "Sensei Aiko is here! Even 5 short minutes today will compound into effortless fluency. Don't worry about perfection—just show up!",
            proTip = "Try reviewing just 3 vocabulary cards right now. Action creates momentum!",
            tone = AikoReminderTone.WARM_ENCOURAGING
        ),
        AikoAntiProcrastinationQuote(
            id = "warm_2",
            japaneseHeading = "🍵 お茶を飲んで、一息つきましょう (Have tea & take a breath)",
            message = "Procrastination is often just mental fatigue. Grab a cup of green tea, take a deep breath, and let's conquer today's quick Japanese drill together.",
            proTip = "A low-pressure 3-minute listening exercise is the gentlest way to restart your study flow.",
            tone = AikoReminderTone.WARM_ENCOURAGING
        ),
        AikoAntiProcrastinationQuote(
            id = "warm_3",
            japaneseHeading = "🌱 一日一歩、着実に (Step by step, steadily)",
            message = "Every kanji you see and every audio sentence you listen to plants a seed. Don't let procrastination tell you it's too late—start now!",
            proTip = "You don't need a 2-hour study block. A 5-minute focused sprint beats hours of delayed good intentions.",
            tone = AikoReminderTone.WARM_ENCOURAGING
        ),
        AikoAntiProcrastinationQuote(
            id = "warm_4",
            japaneseHeading = "🌸 あなたなら絶対にできます (You can definitely do this)",
            message = "Teacher Aiko believes in your consistency! Don't let today slip into 'tomorrow'. A tiny victory today keeps your streak shining!",
            proTip = "Complete just one lesson recap quiz. You'll feel proud of yourself all evening!",
            tone = AikoReminderTone.WARM_ENCOURAGING
        ),

        // Spirited Coach
        AikoAntiProcrastinationQuote(
            id = "coach_1",
            japaneseHeading = "⚡ 先延ばしにサヨナラ！ (Say goodbye to procrastination!)",
            message = "Procrastination is the enemy of your JLPT dream! 5 minutes of study RIGHT NOW is infinitely better than an imaginary 2 hours tomorrow!",
            proTip = "Open the lesson card immediately—don't negotiate with hesitation!",
            tone = AikoReminderTone.SPIRITED_COACH
        ),
        AikoAntiProcrastinationQuote(
            id = "coach_2",
            japaneseHeading = "🔥 行動が不安を消す！ (Action dissolves anxiety!)",
            message = "Feeling overwhelmed by grammar? Waiting makes it harder. Jump into today's lesson—Sensei Aiko has broken everything into bite-sized steps!",
            proTip = "Count down: 3... 2... 1... Tap and start reviewing!",
            tone = AikoReminderTone.SPIRITED_COACH
        ),
        AikoAntiProcrastinationQuote(
            id = "coach_3",
            japaneseHeading = "🎯 今すぐ始めるのが最短の道 (Starting now is the fastest path)",
            message = "The secret to speaking Japanese isn't raw talent—it's refusing to let excuses win. Put 3 minutes on the clock and let's go!",
            proTip = "Do a rapid-fire flashcard run. Notice how the resistance vanishes once you start!",
            tone = AikoReminderTone.SPIRITED_COACH
        ),
        AikoAntiProcrastinationQuote(
            id = "coach_4",
            japaneseHeading = "🛡️ あなたのストリークを守れ！ (Defend your study streak!)",
            message = "You have worked hard to build your discipline. Don't let procrastination snap your unbroken chain of victories! Sensei is waiting!",
            proTip = "Even on busy days, 1 review point counts as keeping the promise to yourself.",
            tone = AikoReminderTone.SPIRITED_COACH
        ),

        // Proverb Wisdom
        AikoAntiProcrastinationQuote(
            id = "wisdom_1",
            japaneseHeading = "🎌 継続は力なり (Continuity is power)",
            message = "Ancient masters taught that small drops of water carve stone. Daily Japanese study, unbroken by hesitation, leads to unshakeable mastery.",
            proTip = "Consistency over intensity. Show up for Teacher Aiko today!",
            tone = AikoReminderTone.PROVERB_WISDOM
        ),
        AikoAntiProcrastinationQuote(
            id = "wisdom_2",
            japaneseHeading = "⛰️ 塵も積もれば山となる (Dust piled up forms a mountain)",
            message = "Every single vocabulary word you review today is a grain of sand building your mountain of Japanese fluency. Don't skip today!",
            proTip = "Reviewing 5 words a day is 1,825 words a year—more than enough to conquer JLPT N4!",
            tone = AikoReminderTone.PROVERB_WISDOM
        ),
        AikoAntiProcrastinationQuote(
            id = "wisdom_3",
            japaneseHeading = "✨ 七転び八起き (Fall seven times, stand up eight)",
            message = "Missed yesterday? It doesn't matter! Stand back up today with Sensei Aiko. True discipline is the willingness to begin again right now.",
            proTip = "Forgive yesterday's delays and focus purely on the next 5 minutes.",
            tone = AikoReminderTone.PROVERB_WISDOM
        ),
        AikoAntiProcrastinationQuote(
            id = "wisdom_4",
            japaneseHeading = "🎯 初志貫徹 (Carry out your original intention to the end)",
            message = "Remember why you decided to learn Japanese. Keep your original flame alive. Beat procrastination and take your daily step with Teacher Aiko.",
            proTip = "Visualize yourself effortlessly understanding Japanese anime and conversations.",
            tone = AikoReminderTone.PROVERB_WISDOM
        )
    )

    fun getQuoteForTone(tone: AikoReminderTone): AikoAntiProcrastinationQuote {
        val matches = allQuotes.filter { it.tone == tone }
        return if (matches.isNotEmpty()) {
            matches.random()
        } else {
            allQuotes.random()
        }
    }

    fun getRandomQuote(): AikoAntiProcrastinationQuote {
        return allQuotes.random()
    }
}
