package com.example.noignore.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_history")
data class TaskHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskId: Int,
    val date: String, // ISO-8601 "yyyy-MM-dd"
    val status: String, // "DONE" or "MISSED"
    val taskTitle: String = "Directive",
    val confirmedAt: Long? = null
)
