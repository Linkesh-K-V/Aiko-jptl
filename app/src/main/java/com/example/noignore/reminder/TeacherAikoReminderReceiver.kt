package com.example.noignore.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.noignore.data.preferences.StudyReminderPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TeacherAikoReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val preferences = StudyReminderPreferences(context)
                val isEnabled = preferences.isReminderEnabled.first()
                if (isEnabled) {
                    val hour = preferences.reminderHour.first()
                    val minute = preferences.reminderMinute.first()
                    val toneName = preferences.reminderTone.first()
                    val tone = try {
                        AikoReminderTone.valueOf(toneName)
                    } catch (e: Exception) {
                        AikoReminderTone.WARM_ENCOURAGING
                    }

                    // 1. Send the friendly anti-procrastination notification
                    val quote = TeacherAikoQuoteRepository.getQuoteForTone(tone)
                    TeacherAikoReminderManager.sendReminderNotification(context, quote)

                    // 2. Schedule the next day's reminder at the same user-configured time
                    TeacherAikoReminderScheduler.scheduleNextDay(context, hour, minute)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
