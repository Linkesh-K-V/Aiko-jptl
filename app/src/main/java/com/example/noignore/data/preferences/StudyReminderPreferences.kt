package com.example.noignore.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.studyReminderDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "study_reminder_preferences"
)

class StudyReminderPreferences(private val context: Context) {

    companion object {
        val KEY_DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_study_reminders_enabled")
        val KEY_REMINDER_HOUR = intPreferencesKey("daily_study_reminder_hour")
        val KEY_REMINDER_MINUTE = intPreferencesKey("daily_study_reminder_minute")
        val KEY_REMINDER_TONE = stringPreferencesKey("daily_study_reminder_tone")

        const val DEFAULT_HOUR = 20 // 8:00 PM
        const val DEFAULT_MINUTE = 0
        const val DEFAULT_TONE = "WARM_ENCOURAGING"
    }

    val isReminderEnabled: Flow<Boolean> = context.studyReminderDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_DAILY_REMINDER_ENABLED] ?: false
        }

    val reminderHour: Flow<Int> = context.studyReminderDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_REMINDER_HOUR] ?: DEFAULT_HOUR
        }

    val reminderMinute: Flow<Int> = context.studyReminderDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_REMINDER_MINUTE] ?: DEFAULT_MINUTE
        }

    val reminderTone: Flow<String> = context.studyReminderDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_REMINDER_TONE] ?: DEFAULT_TONE
        }

    suspend fun setReminderEnabled(enabled: Boolean) {
        context.studyReminderDataStore.edit { preferences ->
            preferences[KEY_DAILY_REMINDER_ENABLED] = enabled
        }
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        context.studyReminderDataStore.edit { preferences ->
            preferences[KEY_REMINDER_HOUR] = hour.coerceIn(0, 23)
            preferences[KEY_REMINDER_MINUTE] = minute.coerceIn(0, 59)
        }
    }

    suspend fun setReminderTone(tone: String) {
        context.studyReminderDataStore.edit { preferences ->
            preferences[KEY_REMINDER_TONE] = tone
        }
    }
}
