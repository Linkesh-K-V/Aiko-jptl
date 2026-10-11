package com.example.noignore.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.noignore.model.RepeatMode

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val timeMillis: Long,
    val repeatMode: RepeatMode = RepeatMode.ONCE,
    val repeatDays: String = "",
    val confirmDelayMinutes: Int = 5,
    val ringtoneUri: String? = null,
    val hour: Int = 9,
    val minute: Int = 0,
    val completed: Boolean = false
)
