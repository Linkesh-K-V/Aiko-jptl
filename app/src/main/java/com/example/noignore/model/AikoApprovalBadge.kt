package com.example.noignore.model

import androidx.compose.ui.graphics.Color

/**
 * Represents the official 'Aiko's Approval' badges awarded by Sensei Aiko
 * for maintaining consistency in study streaks (7-day and 30-day milestones).
 */
enum class AikoApprovalBadge(
    val id: String,
    val title: String,
    val japaneseTitle: String,
    val requiredStreak: Int,
    val emoji: String,
    val hankoSeal: String,
    val rankTitle: String,
    val shortSummary: String,
    val fullDescription: String,
    val senseiQuote: String,
    val primaryColorHex: Long,
    val accentColorHex: Long
) {
    SEVEN_DAY(
        id = "aiko_approval_7",
        title = "Aiko's 7-Day Approval",
        japaneseTitle = "愛子の承認 (7日間)",
        requiredStreak = 7,
        emoji = "💮",
        hankoSeal = "承認",
        rankTitle = "Disciplined Apprentice",
        shortSummary = "7-Day Consistent Streak",
        fullDescription = "Awarded by Sensei Aiko for completing 7 consecutive days of Japanese study without breaking discipline.",
        senseiQuote = "“Subarashii! Seven consecutive days of focused study proves your consistency. You have earned my official approval seal. Keep moving forward with pride!”",
        primaryColorHex = 0xFFD81B60, // Sakura crimson
        accentColorHex = 0xFFFCE4EC
    ),
    THIRTY_DAY(
        id = "aiko_approval_30",
        title = "Aiko's 30-Day Master Approval",
        japaneseTitle = "愛子の特認 (30日間)",
        requiredStreak = 30,
        emoji = "👑",
        hankoSeal = "特認",
        rankTitle = "Master Scholar",
        shortSummary = "30-Day Ironclad Streak",
        fullDescription = "Conferred by Sensei Aiko for achieving an unbroken 30-day streak of daily Japanese devotion. Unbreakable iron will.",
        senseiQuote = "“Extraordinary! A full month of unbroken discipline reflects true warrior spirit (大和魂). You possess my highest recognition and master endorsement!”",
        primaryColorHex = 0xFFE65100, // Deep imperial gold amber
        accentColorHex = 0xFFFFF8E1
    );

    fun isUnlocked(currentStreak: Int): Boolean = currentStreak >= requiredStreak

    fun progress(currentStreak: Int): Float =
        (currentStreak.toFloat() / requiredStreak.toFloat()).coerceIn(0f, 1f)

    fun daysRemaining(currentStreak: Int): Int =
        (requiredStreak - currentStreak).coerceAtLeast(0)

    val primaryColor: Color get() = Color(primaryColorHex)
    val accentColor: Color get() = Color(accentColorHex)

    companion object {
        fun fromId(id: String): AikoApprovalBadge? = entries.firstOrNull { it.id == id }
    }
}
