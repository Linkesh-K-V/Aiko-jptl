package com.example.noignore.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.Calendar

private val Context.userMetricsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_study_metrics")

data class UserStudyMetrics(
    val kaoCoins: Int = 0,
    val cardsStudiedToday: Int = 0,
    val totalReviews: Int = 0,
    val studyStreakDays: Int = 1,
    val lastStudyDate: String = ""
)

/**
 * Modern Jetpack DataStore repository for critical user metrics:
 * Coins, daily study counts, streaks, and total reviews.
 * Replaces legacy SharedPreferences with transactional, non-blocking asynchronous state.
 */
class UserMetricsDataStore(private val context: Context) {

    private object PreferencesKeys {
        val KAO_COINS = intPreferencesKey("kao_coins")
        val CARDS_STUDIED_TODAY = intPreferencesKey("cards_studied_today")
        val TOTAL_REVIEWS = intPreferencesKey("total_reviews")
        val STUDY_STREAK_DAYS = intPreferencesKey("study_streak_days")
        val LAST_STUDY_DATE = stringPreferencesKey("last_study_date")
    }

    val metricsFlow: Flow<UserStudyMetrics> = context.userMetricsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val todayStr = getTodayDateString()
            val storedDate = preferences[PreferencesKeys.LAST_STUDY_DATE] ?: ""
            val cardsToday = if (storedDate == todayStr) {
                preferences[PreferencesKeys.CARDS_STUDIED_TODAY] ?: 0
            } else {
                0
            }

            UserStudyMetrics(
                kaoCoins = preferences[PreferencesKeys.KAO_COINS] ?: 0,
                cardsStudiedToday = cardsToday,
                totalReviews = preferences[PreferencesKeys.TOTAL_REVIEWS] ?: 0,
                studyStreakDays = maxOf(1, preferences[PreferencesKeys.STUDY_STREAK_DAYS] ?: 1),
                lastStudyDate = storedDate
            )
        }

    suspend fun recordReview(coinsEarned: Int) {
        val todayStr = getTodayDateString()
        context.userMetricsDataStore.edit { preferences ->
            val currentCoins = preferences[PreferencesKeys.KAO_COINS] ?: 0
            val totalRev = preferences[PreferencesKeys.TOTAL_REVIEWS] ?: 0
            val lastDate = preferences[PreferencesKeys.LAST_STUDY_DATE] ?: ""
            val currentCardsToday = if (lastDate == todayStr) {
                preferences[PreferencesKeys.CARDS_STUDIED_TODAY] ?: 0
            } else {
                0
            }

            preferences[PreferencesKeys.KAO_COINS] = maxOf(0, currentCoins + coinsEarned)
            preferences[PreferencesKeys.TOTAL_REVIEWS] = totalRev + 1
            preferences[PreferencesKeys.CARDS_STUDIED_TODAY] = currentCardsToday + 1
            preferences[PreferencesKeys.LAST_STUDY_DATE] = todayStr
        }
    }

    suspend fun setKaoCoins(coins: Int) {
        context.userMetricsDataStore.edit { preferences ->
            preferences[PreferencesKeys.KAO_COINS] = maxOf(0, coins)
        }
    }

    suspend fun updateStreak(streak: Int) {
        context.userMetricsDataStore.edit { preferences ->
            preferences[PreferencesKeys.STUDY_STREAK_DAYS] = maxOf(1, streak)
        }
    }

    suspend fun resetCardsTodayIfNewDay() {
        val todayStr = getTodayDateString()
        context.userMetricsDataStore.edit { preferences ->
            val lastDate = preferences[PreferencesKeys.LAST_STUDY_DATE] ?: ""
            if (lastDate != todayStr) {
                preferences[PreferencesKeys.LAST_STUDY_DATE] = todayStr
                preferences[PreferencesKeys.CARDS_STUDIED_TODAY] = 0
            }
        }
    }

    private fun getTodayDateString(): String {
        val cal = Calendar.getInstance()
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
    }
}
