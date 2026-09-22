package com.example.noignore.ui.reminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noignore.data.preferences.StudyReminderPreferences
import com.example.noignore.reminder.AikoReminderTone
import com.example.noignore.reminder.TeacherAikoQuoteRepository
import com.example.noignore.reminder.TeacherAikoReminderManager
import com.example.noignore.reminder.TeacherAikoReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudyReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = StudyReminderPreferences(application)

    val isReminderEnabled: StateFlow<Boolean> = preferences.isReminderEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val reminderHour: StateFlow<Int> = preferences.reminderHour
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StudyReminderPreferences.DEFAULT_HOUR
        )

    val reminderMinute: StateFlow<Int> = preferences.reminderMinute
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StudyReminderPreferences.DEFAULT_MINUTE
        )

    val reminderTone: StateFlow<String> = preferences.reminderTone
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StudyReminderPreferences.DEFAULT_TONE
        )

    fun toggleReminder(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setReminderEnabled(enabled)
            val context = getApplication<Application>()
            if (enabled) {
                val hour = preferences.reminderHour.first()
                val minute = preferences.reminderMinute.first()
                TeacherAikoReminderScheduler.schedule(context, hour, minute)
            } else {
                TeacherAikoReminderScheduler.cancel(context)
            }
        }
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferences.setReminderTime(hour, minute)
            val isEnabled = preferences.isReminderEnabled.first()
            if (isEnabled) {
                TeacherAikoReminderScheduler.schedule(getApplication(), hour, minute)
            }
        }
    }

    fun updateReminderTone(tone: AikoReminderTone) {
        viewModelScope.launch {
            preferences.setReminderTone(tone.name)
        }
    }

    fun sendTestNotificationNow() {
        val tone = try {
            AikoReminderTone.valueOf(reminderTone.value)
        } catch (e: Exception) {
            AikoReminderTone.WARM_ENCOURAGING
        }
        val quote = TeacherAikoQuoteRepository.getQuoteForTone(tone)
        TeacherAikoReminderManager.sendReminderNotification(
            context = getApplication(),
            quote = quote,
            isTest = true
        )
    }
}
