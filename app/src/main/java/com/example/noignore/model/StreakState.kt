package com.example.noignore.model

enum class StreakState(val label: String, val emoji: String) {
    NONE("No streak", "💔"),
    ROOKIE("Rookie", "🔥"),
    CONSISTENT("Consistent", "🔥"),
    STRONG("Strong", "🔥🔥"),
    DISCIPLINED("Disciplined", "🔥🔥"),
    ELITE("Elite", "🔥🔥🔥"),
    LEGENDARY("Legendary", "🔥🔥🔥");

    companion object {
        fun from(streak: Int): StreakState {
            return when {
                streak <= 0 -> NONE
                streak in 1..2 -> ROOKIE
                streak in 3..6 -> CONSISTENT
                streak in 7..13 -> STRONG
                streak in 14..29 -> DISCIPLINED
                streak in 30..89 -> ELITE
                else -> LEGENDARY
            }
        }
    }
}
