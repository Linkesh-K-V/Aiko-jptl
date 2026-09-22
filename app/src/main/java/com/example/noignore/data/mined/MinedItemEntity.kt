package com.example.noignore.data.mined

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Persistent Room Entity for user-mined vocabulary and grammar items.
 * Guaranteed to survive application restarts, background process termination,
 * and database migrations.
 */
@Entity(
    tableName = "mined_items",
    indices = [
        Index(value = ["japanese"]),
        Index(value = ["jlptLevel"]),
        Index(value = ["createdAtMs"])
    ]
)
data class MinedItemEntity(
    @PrimaryKey
    val id: String,
    val japanese: String,
    val reading: String,
    val romaji: String = "",
    val meaning: String,
    val category: String = "VOCAB",
    val jlptLevel: String = "Custom",
    val sourceSentence: String = "",
    val sourceEnglish: String = "",
    val userNotes: String = "",
    val tags: String = "",
    val createdAtMs: Long = System.currentTimeMillis(),
    val updatedAtMs: Long = System.currentTimeMillis()
)
