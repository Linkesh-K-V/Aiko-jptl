package com.example.noignore.ui.streak

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.TaskHistory
import com.example.noignore.model.StreakData
import com.example.noignore.model.StreakState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeParseException

class StreakViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val _streakData = MutableStateFlow(StreakData())
    val streakData: StateFlow<StreakData> = _streakData.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            refreshNow()
        }
    }

    suspend fun refreshNow(): StreakData {
        return withContext(Dispatchers.IO) {
            val history = db.taskHistoryDao().getAllHistory()
            val result = compute(history)
            _streakData.value = result
            result
        }
    }

    private fun compute(history: List<TaskHistory>): StreakData {
        if (history.isEmpty()) {
            return StreakData(0, 0, 0, StreakState.NONE)
        }

        // Group history by ISO date string
        val historyByDate = history.groupBy { it.date }

        val today = LocalDate.now()
        // Find earliest valid date
        var earliestDate = today
        for (dateStr in historyByDate.keys) {
            try {
                val parsed = LocalDate.parse(dateStr)
                if (parsed.isBefore(earliestDate)) {
                    earliestDate = parsed
                }
            } catch (e: DateTimeParseException) {
                // ignore malformed date
            }
        }

        var runningStreak = 0
        var longestStreak = 0
        var totalBreaks = 0

        var cursor = earliestDate
        while (!cursor.isAfter(today)) {
            val dateStr = cursor.toString()
            val records = historyByDate[dateStr]

            if (records == null) {
                // Rest day, no penalty
            } else {
                val hasMissed = records.any { it.status == "MISSED" }
                val hasDone = records.any { it.status == "DONE" }

                if (hasMissed) {
                    if (runningStreak > 0) {
                        totalBreaks++
                    }
                    runningStreak = 0
                } else if (hasDone) {
                    runningStreak++
                    if (runningStreak > longestStreak) {
                        longestStreak = runningStreak
                    }
                }
            }
            cursor = cursor.plusDays(1)
        }

        return StreakData(
            currentStreak = runningStreak,
            longestStreak = longestStreak,
            totalBreaks = totalBreaks,
            state = StreakState.from(runningStreak)
        )
    }
}
