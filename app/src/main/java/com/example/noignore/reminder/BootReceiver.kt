package com.example.noignore.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.preferences.StudyReminderPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getInstance(context)
                    val tasks = db.taskDao().getAllTasksOnce()
                    val now = System.currentTimeMillis()

                    for (task in tasks) {
                        if (!task.completed && task.timeMillis > now) {
                            ReminderScheduler.schedule(
                                context = context,
                                taskId = task.id,
                                triggerAtMillis = task.timeMillis,
                                confirmDelayMinutes = task.confirmDelayMinutes,
                                ringtoneUri = task.ringtoneUri
                            )
                        }
                    }

                    // Reschedule Teacher Aiko daily anti-procrastination reminder if enabled
                    val studyPrefs = StudyReminderPreferences(context)
                    if (studyPrefs.isReminderEnabled.first()) {
                        val hour = studyPrefs.reminderHour.first()
                        val minute = studyPrefs.reminderMinute.first()
                        TeacherAikoReminderScheduler.schedule(context, hour, minute)
                    }

                    // Schedule periodic cache and storage hygiene maintenance
                    CacheMaintenanceScheduler.schedulePeriodicMaintenance(context)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
