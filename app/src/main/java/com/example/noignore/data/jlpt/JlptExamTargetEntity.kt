package com.example.noignore.data.jlpt

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jlpt_exam_target")
data class JlptExamTargetEntity(
    @PrimaryKey val id: Int = 1,
    val targetLevel: String = "N5",
    val targetExamDateMillis: Long = 0L,
    val dailyGoalMinutes: Int = 30,
    val customGoalNotes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
