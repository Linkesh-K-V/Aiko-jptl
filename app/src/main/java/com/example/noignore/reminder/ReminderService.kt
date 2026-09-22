package com.example.noignore.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.TaskHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.LocalDate

class ReminderService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val channelId = "noignore_reminder_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("NoIgnore")
            .setContentText("Discipline check active...")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        startForeground(1001, notification)

        if (intent == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        val taskId = intent.getIntExtra(ReminderScheduler.EXTRA_TASK_ID, -1)
        val isConfirm = intent.getBooleanExtra(ReminderScheduler.EXTRA_IS_CONFIRM, false)
        val isMissedCheck = intent.getBooleanExtra(ReminderScheduler.EXTRA_IS_MISSED_CHECK, false)
        val ringtoneUri = intent.getStringExtra(ReminderScheduler.EXTRA_RINGTONE_URI)

        if (isMissedCheck) {
            serviceScope.launch {
                try {
                    val db = AppDatabase.getInstance(applicationContext)
                    val today = LocalDate.now().toString()
                    val todayRecords = db.taskHistoryDao().getHistoryBetween(today, today)
                    val hasDone = todayRecords.any { it.taskId == taskId && it.status == "DONE" }

                    if (!hasDone) {
                        db.taskHistoryDao().insert(
                            TaskHistory(
                                taskId = taskId,
                                date = today,
                                status = "MISSED"
                            )
                        )
                    }
                    ReminderScheduler.cancel(applicationContext, taskId)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    stopSelf()
                }
            }
        } else {
            // Launch AlarmActivity with all extras
            val activityIntent = Intent(this, AlarmActivity::class.java).apply {
                // CRITICAL: use this.flags to avoid "Val cannot be reassigned" with method parameter 'flags'
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(ReminderScheduler.EXTRA_TASK_ID, taskId)
                putExtra(ReminderScheduler.EXTRA_IS_CONFIRM, isConfirm)
                putExtra(ReminderScheduler.EXTRA_IS_MISSED_CHECK, isMissedCheck)
                putExtra(ReminderScheduler.EXTRA_RINGTONE_URI, ringtoneUri)
            }
            startActivity(activityIntent)
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Task Alarms",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Strict discipline task notifications and missed checks"
                setSound(null, null)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
