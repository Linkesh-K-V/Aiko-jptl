package com.example.noignore.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/**
 * Scheduler for daily background storage hygiene and audio cache maintenance.
 * Ensures orphaned audio records are reliably purged without depending on UI process lifetime.
 */
object CacheMaintenanceScheduler {

    private const val REQUEST_CODE_CACHE_CLEAN = 60_001
    private const val CLEAN_INTERVAL_MS = 24L * 60 * 60 * 1000 // Every 24 hours

    fun schedulePeriodicMaintenance(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, CacheMaintenanceReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_CACHE_CLEAN,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAt = System.currentTimeMillis() + (6 * 60 * 60 * 1000L) // First run in 6 hours
        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC,
                triggerAt,
                CLEAN_INTERVAL_MS,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
