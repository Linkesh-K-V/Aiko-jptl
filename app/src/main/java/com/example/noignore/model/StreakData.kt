package com.example.noignore.model

data class StreakData(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalBreaks: Int = 0,
    val state: StreakState = StreakState.NONE
)
