package com.example.noignore.ui.achievements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noignore.data.Achievement
import com.example.noignore.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AchievementsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private val _unlockedCount = MutableStateFlow(0)
    val unlockedCount: StateFlow<Int> = _unlockedCount.asStateFlow()

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                ensureInitialAchievements()
                val list = db.achievementDao().getAllAchievementsOnce()
                _achievements.value = list
                _unlockedCount.value = list.count { it.unlocked }
                _totalCount.value = list.size
            }
        }
    }

    private suspend fun ensureInitialAchievements() {
        val existing = db.achievementDao().getAllAchievementsOnce()
        val existingIds = existing.map { it.id }.toSet()
        val missing = AppDatabase.INITIAL_ACHIEVEMENTS.filter { it.id !in existingIds }
        if (missing.isNotEmpty()) {
            db.achievementDao().insertAll(missing)
        }
    }

    suspend fun checkAndUnlock(streak: Int, totalCompleted: Int): List<Achievement> {
        return withContext(Dispatchers.IO) {
            ensureInitialAchievements()
            val list = db.achievementDao().getAllAchievementsOnce()
            val newlyUnlocked = mutableListOf<Achievement>()
            val now = System.currentTimeMillis()

            for (ach in list) {
                if (ach.unlocked) continue

                val shouldUnlock = when (ach.id) {
                    "aiko_approval_7" -> streak >= 7
                    "aiko_approval_30" -> streak >= 30
                    "first_step" -> totalCompleted >= 1
                    "streak_1" -> streak >= 1
                    "streak_3" -> streak >= 3
                    "streak_7" -> streak >= 7
                    "streak_14" -> streak >= 14
                    "streak_30" -> streak >= 30
                    "streak_90", "streak_100" -> streak >= 90
                    "tasks_5", "tasks_10" -> totalCompleted >= 5
                    "tasks_25" -> totalCompleted >= 25
                    "tasks_50" -> totalCompleted >= 50
                    "tasks_100" -> totalCompleted >= 100
                    else -> false
                }

                if (shouldUnlock) {
                    db.achievementDao().unlockAchievement(ach.id, now)
                    newlyUnlocked.add(ach.copy(unlocked = true, unlockedAt = now))
                }
            }

            if (newlyUnlocked.isNotEmpty()) {
                val updatedList = db.achievementDao().getAllAchievementsOnce()
                _achievements.value = updatedList
                _unlockedCount.value = updatedList.count { it.unlocked }
            }

            newlyUnlocked
        }
    }
}
