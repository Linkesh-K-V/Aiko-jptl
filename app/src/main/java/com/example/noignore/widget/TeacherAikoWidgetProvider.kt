package com.example.noignore.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.noignore.japanese.data.JapaneseDeckRepository

/**
 * AppWidgetProvider for Teacher Aiko's Japanese Home Screen Widget.
 * Displays daily streak, due review counts, and today's Kotowaza proverb,
 * with 1-tap quick launch into daily Renshuu study sessions.
 */
class TeacherAikoWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val repository = JapaneseDeckRepository(context)
            val kotowaza = repository.getKotowazaOfTheDay()
            val summary = repository.getSrsDeckSummary()
            val streakDays = repository.getStudyStreak()

            val views = RemoteViews(context.packageName, R.layout.widget_teacher_aiko)

            // Bind values
            views.setTextViewText(R.id.widget_kotowaza_kanji, kotowaza.kanji)
            views.setTextViewText(R.id.widget_kotowaza_english, "“${kotowaza.english}”")
            views.setTextViewText(R.id.widget_streak_badge, "🔥 $streakDays Day Streak")
            val dueTotal = summary.reviewCardsCount + summary.learningCardsCount
            views.setTextViewText(R.id.widget_due_count, "📚 ${dueTotal.coerceAtLeast(1)} Cards Due")

            // 1-Tap Launch Intent to open App straight into Renshuu practice
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("NAVIGATE_TO", "renshuu")
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            views.setOnClickPendingIntent(R.id.widget_practice_btn, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, TeacherAikoWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }
}
