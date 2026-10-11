package com.example.noignore.ui.study

enum class ConsistencyTier(
    val title: String,
    val japaneseTitle: String,
    val emoji: String,
    val badgeColorHex: Long
) {
    NOT_STARTED(
        title = "Fresh Start",
        japaneseTitle = "準備開始",
        emoji = "🌱",
        badgeColorHex = 0xFF78909C
    ),
    STARTING(
        title = "Warming Up",
        japaneseTitle = "助走中",
        emoji = "⚡",
        badgeColorHex = 0xFF42A5F5
    ),
    STEADY(
        title = "Steady Focus",
        japaneseTitle = "継続中",
        emoji = "🔥",
        badgeColorHex = 0xFFFF7043
    ),
    ADVANCED(
        title = "High Discipline",
        japaneseTitle = "あと少し",
        emoji = "✨",
        badgeColorHex = 0xFFAB47BC
    ),
    MASTERY(
        title = "Daily Mastery",
        japaneseTitle = "完全達成",
        emoji = "👑",
        badgeColorHex = 0xFF2E7D32
    );

    companion object {
        fun fromProgress(progress: Float): ConsistencyTier {
            val pct = (progress * 100).toInt()
            return when {
                pct >= 100 -> MASTERY
                pct >= 75 -> ADVANCED
                pct >= 40 -> STEADY
                pct >= 1 -> STARTING
                else -> NOT_STARTED
            }
        }
    }
}

data class DailyConsistencyData(
    val completedTasks: Int = 0,
    val targetTasks: Int = 3,
    val completedLessons: Int = 0,
    val targetLessons: Int = 15,
    val currentStreak: Int = 0
) {
    val taskProgress: Float
        get() = if (targetTasks > 0) (completedTasks.toFloat() / targetTasks).coerceIn(0f, 1f) else 0f

    val lessonProgress: Float
        get() = if (targetLessons > 0) (completedLessons.toFloat() / targetLessons).coerceIn(0f, 1f) else 0f

    val overallProgress: Float
        get() {
            // Balanced weighting: 50% task directives + 50% lesson cards
            // If user has zero target tasks, base on lessons, and vice versa
            return when {
                targetTasks > 0 && targetLessons > 0 -> {
                    ((taskProgress * 0.5f) + (lessonProgress * 0.5f)).coerceIn(0f, 1f)
                }
                targetTasks > 0 -> taskProgress
                targetLessons > 0 -> lessonProgress
                else -> 0f
            }
        }

    val percentage: Int
        get() = (overallProgress * 100).toInt().coerceIn(0, 100)

    val tier: ConsistencyTier
        get() = ConsistencyTier.fromProgress(overallProgress)

    val aikoFeedback: String
        get() = when (tier) {
            ConsistencyTier.NOT_STARTED ->
                "Kon'nichiwa! Even 5 minutes today protects your streak. Complete 1 directive or study 5 flashcards to ignite today's consistency! 🌸"

            ConsistencyTier.STARTING ->
                "Great start! ⚡ You've taken the first step. Keep the momentum going with another study task or quick kanji review!"

            ConsistencyTier.STEADY ->
                "Steady discipline! 🔥 Over halfway there. Remember: 継続は力なり (Continuance is power). You're building solid neural memory."

            ConsistencyTier.ADVANCED ->
                "Superb focus! ✨ At $percentage% daily consistency, you're just a tiny sprint away from 100% perfection. Finish strong!"

            ConsistencyTier.MASTERY ->
                "Subarashii! 👑 100% Daily Mastery reached! You conquered today's directives and lessons. Teacher Aiko is deeply impressed!"
        }

    fun isMilestonePassed(milestonePercentage: Int): Boolean {
        return percentage >= milestonePercentage
    }
}
