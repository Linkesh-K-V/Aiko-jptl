package com.example.noignore.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object ReminderScheduler {
    const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
    const val EXTRA_IS_CONFIRM = "EXTRA_IS_CONFIRM"
    const val EXTRA_IS_MISSED_CHECK = "EXTRA_IS_MISSED_CHECK"
    const val EXTRA_RINGTONE_URI = "EXTRA_RINGTONE_URI"

    private const val REQUEST_CODE_MAIN_OFFSET = 0
    private const val REQUEST_CODE_CONFIRM_OFFSET = 10_000
    private const val REQUEST_CODE_MISSED_OFFSET = 20_000

    fun schedule(
        context: Context,
        taskId: Int,
        triggerAtMillis: Long,
        confirmDelayMinutes: Int,
        ringtoneUri: String? = null
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // Check exact alarm permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Cannot schedule exact alarms yet, will still attempt or fallback
            }
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        // 1. Main alarm at triggerAtMillis
        val mainIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_TASK_ID, taskId)
            putExtra(EXTRA_IS_CONFIRM, false)
            putExtra(EXTRA_IS_MISSED_CHECK, false)
            putExtra(EXTRA_RINGTONE_URI, ringtoneUri)
        }
        val mainPendingIntent = PendingIntent.getBroadcast(
            context,
            taskId + REQUEST_CODE_MAIN_OFFSET,
            mainIntent,
            flags
        )

        // 2. Confirm alarm at triggerAtMillis + confirmDelayMinutes * 60_000
        val confirmAtMillis = triggerAtMillis + (confirmDelayMinutes.toLong() * 60_000L)
        val confirmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_TASK_ID, taskId)
            putExtra(EXTRA_IS_CONFIRM, true)
            putExtra(EXTRA_IS_MISSED_CHECK, false)
            putExtra(EXTRA_RINGTONE_URI, ringtoneUri)
        }
        val confirmPendingIntent = PendingIntent.getBroadcast(
            context,
            taskId + REQUEST_CODE_CONFIRM_OFFSET,
            confirmIntent,
            flags
        )

        // 3. Missed check alarm at confirmAt + 60_000 (1 minute grace)
        val missedAtMillis = confirmAtMillis + 60_000L
        val missedIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_TASK_ID, taskId)
            putExtra(EXTRA_IS_CONFIRM, false)
            putExtra(EXTRA_IS_MISSED_CHECK, true)
            putExtra(EXTRA_RINGTONE_URI, ringtoneUri)
        }
        val missedPendingIntent = PendingIntent.getBroadcast(
            context,
            taskId + REQUEST_CODE_MISSED_OFFSET,
            missedIntent,
            flags
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            mainPendingIntent
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            confirmAtMillis,
            confirmPendingIntent
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            missedAtMillis,
            missedPendingIntent
        )
    }

    fun cancel(context: Context, taskId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val mainIntent = Intent(context, AlarmReceiver::class.java)
        val mainPendingIntent = PendingIntent.getBroadcast(
            context,
            taskId + REQUEST_CODE_MAIN_OFFSET,
            mainIntent,
            flags
        )
        alarmManager.cancel(mainPendingIntent)

        val confirmIntent = Intent(context, AlarmReceiver::class.java)
        val confirmPendingIntent = PendingIntent.getBroadcast(
            context,
            taskId + REQUEST_CODE_CONFIRM_OFFSET,
            confirmIntent,
            flags
        )
        alarmManager.cancel(confirmPendingIntent)

        val missedIntent = Intent(context, AlarmReceiver::class.java)
        val missedPendingIntent = PendingIntent.getBroadcast(
            context,
            taskId + REQUEST_CODE_MISSED_OFFSET,
            missedIntent,
            flags
        )
        alarmManager.cancel(missedPendingIntent)
    }
}
