package com.example.noignore.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity

object TeacherAikoReminderManager {

    const val CHANNEL_ID = "channel_teacher_aiko_reminders"
    const val CHANNEL_NAME = "Teacher Aiko's Anti-Procrastination Reminders"
    const val CHANNEL_DESCRIPTION = "Daily friendly motivational study nudges from Teacher Aiko to beat procrastination."
    const val NOTIFICATION_ID = 2001
    const val TEST_NOTIFICATION_ID = 2002

    const val EXTRA_FROM_AIKO_REMINDER = "EXTRA_FROM_AIKO_REMINDER"
    const val EXTRA_TARGET_SCREEN = "EXTRA_TARGET_SCREEN"

    /**
     * Creates the notification channel on Android 8.0+ (Oreo).
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = Color.parseColor("#E91E63") // Warm Japanese Sakura pink
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Sends a friendly anti-procrastination notification from Teacher Aiko.
     */
    fun sendReminderNotification(
        context: Context,
        quote: AikoAntiProcrastinationQuote = TeacherAikoQuoteRepository.getRandomQuote(),
        isTest: Boolean = false
    ) {
        createNotificationChannel(context)

        // Main Tap Intent: opens app and directs user to study
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_FROM_AIKO_REMINDER, true)
            putExtra(EXTRA_TARGET_SCREEN, "lesson_study")
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val requestCode = if (isTest) 101 else 100
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            openIntent,
            pendingIntentFlags
        )

        // Quick Drill action button
        val drillIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_FROM_AIKO_REMINDER, true)
            putExtra(EXTRA_TARGET_SCREEN, "renshuu")
        }
        val drillPendingIntent = PendingIntent.getActivity(
            context,
            requestCode + 1,
            drillIntent,
            pendingIntentFlags
        )

        val title = if (isTest) {
            "🌸 Teacher Aiko (愛子先生) • Test Nudge"
        } else {
            "🌸 Teacher Aiko (愛子先生)"
        }

        val bigText = buildString {
            appendLine(quote.japaneseHeading)
            appendLine()
            appendLine(quote.message)
            appendLine()
            append("💡 Teacher's Tip: ${quote.proTip}")
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(quote.message)
            .setSubText("Anti-Procrastination Reminder")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bigText)
                    .setBigContentTitle(title)
                    .setSummaryText("${quote.tone.emoji} ${quote.tone.displayName}")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.ic_media_play,
                "📖 Study Lesson",
                contentPendingIntent
            )
            .addAction(
                android.R.drawable.ic_dialog_info,
                "⚡ Quick Drill",
                drillPendingIntent
            )
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(0, 250, 150, 250))
            .setColor(0xFFE91E63.toInt()) // Sakura accent

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            val notificationId = if (isTest) TEST_NOTIFICATION_ID else NOTIFICATION_ID
            notificationManager.notify(notificationId, notificationBuilder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
