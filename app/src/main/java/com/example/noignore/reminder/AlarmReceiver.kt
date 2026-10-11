package com.example.noignore.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra(ReminderScheduler.EXTRA_TASK_ID, -1)
        val isConfirm = intent.getBooleanExtra(ReminderScheduler.EXTRA_IS_CONFIRM, false)
        val isMissedCheck = intent.getBooleanExtra(ReminderScheduler.EXTRA_IS_MISSED_CHECK, false)
        val ringtoneUri = intent.getStringExtra(ReminderScheduler.EXTRA_RINGTONE_URI)

        val serviceIntent = Intent(context, ReminderService::class.java).apply {
            putExtra(ReminderScheduler.EXTRA_TASK_ID, taskId)
            putExtra(ReminderScheduler.EXTRA_IS_CONFIRM, isConfirm)
            putExtra(ReminderScheduler.EXTRA_IS_MISSED_CHECK, isMissedCheck)
            putExtra(ReminderScheduler.EXTRA_RINGTONE_URI, ringtoneUri)
        }

        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
