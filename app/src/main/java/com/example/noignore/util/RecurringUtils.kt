package com.example.noignore.util

import com.example.noignore.model.RepeatMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object RecurringUtils {

    fun nextOccurrence(
        repeatMode: RepeatMode,
        hour: Int,
        minute: Int,
        repeatDays: String = ""
    ): Long {
        val zone = ZoneId.systemDefault()
        val now = LocalDateTime.now(zone)
        var targetDateTime = LocalDateTime.of(LocalDate.now(zone), LocalTime.of(hour, minute, 0))

        when (repeatMode) {
            RepeatMode.ONCE, RepeatMode.DAILY -> {
                if (targetDateTime.isBefore(now) || targetDateTime.isEqual(now)) {
                    targetDateTime = targetDateTime.plusDays(1)
                }
                return targetDateTime.atZone(zone).toInstant().toEpochMilli()
            }
            RepeatMode.WEEKLY_CUSTOM -> {
                val selectedDays = repeatDays.split(",")
                    .map { it.trim().uppercase() }
                    .filter { it.isNotEmpty() }
                    .toSet()

                if (selectedDays.isEmpty()) {
                    if (targetDateTime.isBefore(now) || targetDateTime.isEqual(now)) {
                        targetDateTime = targetDateTime.plusDays(1)
                    }
                    return targetDateTime.atZone(zone).toInstant().toEpochMilli()
                }

                // Check starting from today (if time hasn't passed) or tomorrow up to 8 days forward
                val startOffset = if (targetDateTime.isAfter(now)) 0L else 1L
                for (i in startOffset..8L) {
                    val candidate = targetDateTime.plusDays(i)
                    val dayCode = mapDayOfWeekToCode(candidate.dayOfWeek)
                    if (selectedDays.contains(dayCode)) {
                        return candidate.atZone(zone).toInstant().toEpochMilli()
                    }
                }

                // Fallback
                return targetDateTime.plusDays(1).atZone(zone).toInstant().toEpochMilli()
            }
        }
    }

    private fun mapDayOfWeekToCode(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "MON"
            DayOfWeek.TUESDAY -> "TUE"
            DayOfWeek.WEDNESDAY -> "WED"
            DayOfWeek.THURSDAY -> "THU"
            DayOfWeek.FRIDAY -> "FRI"
            DayOfWeek.SATURDAY -> "SAT"
            DayOfWeek.SUNDAY -> "SUN"
        }
    }
}
