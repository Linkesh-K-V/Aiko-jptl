package com.example.noignore.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey
    val id: String, // e.g. "streak_7"
    val title: String,
    val description: String,
    val icon: String, // emoji
    val requirement: Int,
    val category: String, // "STREAK" or "COMPLETION"
    val unlocked: Boolean = false,
    val unlockedAt: Long? = null
)
