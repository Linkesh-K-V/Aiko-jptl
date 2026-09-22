package com.example.noignore.ui.jlpt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.jlpt.JlptExamTargetEntity
import com.example.noignore.data.jlpt.JlptProgressEntity
import com.example.noignore.data.jlpt.JlptProgressRepository
import com.example.noignore.japanese.data.JapaneseDeckRepository
import com.example.noignore.japanese.model.JlptExamLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JlptProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = JlptProgressRepository(db.jlptProgressDao())
    private val deckRepo = JapaneseDeckRepository(application)

    private val _selectedViewLevel = MutableStateFlow("N5")
    val selectedViewLevel: StateFlow<String> = _selectedViewLevel.asStateFlow()

    val targetExam: StateFlow<JlptExamTargetEntity?> = repository.targetExam
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allProgress: StateFlow<List<JlptProgressEntity>> = repository.allProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered progress for the currently inspected level
    val currentLevelProgress: StateFlow<List<JlptProgressEntity>> = combine(
        allProgress,
        _selectedViewLevel
    ) { progressList, levelCode ->
        progressList.filter { it.level.equals(levelCode, ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            repository.initializeDefaultsIfEmpty()
            // Sync with deckRepo's preference if present
            val savedLevel = deckRepo.getSelectedExamLevel().code
            _selectedViewLevel.value = savedLevel
            repository.setTargetExamLevel(savedLevel)
        }
    }

    fun selectViewLevel(levelCode: String) {
        _selectedViewLevel.value = levelCode
    }

    fun setTargetExamLevel(levelCode: String) {
        viewModelScope.launch {
            repository.setTargetExamLevel(levelCode)
            val examLevel = JlptExamLevel.fromCode(levelCode)
            deckRepo.setSelectedExamLevel(examLevel)
            _selectedViewLevel.value = levelCode
        }
    }

    fun incrementProgress(levelCode: String, category: String, delta: Int = 1) {
        viewModelScope.launch {
            repository.incrementCompletedCount(levelCode, category, delta)
        }
    }

    fun updateCompletedCount(levelCode: String, category: String, newCount: Int) {
        viewModelScope.launch {
            repository.updateCompletedCount(levelCode, category, newCount)
        }
    }

    fun updateDailyGoalMinutes(minutes: Int) {
        viewModelScope.launch {
            val currentTarget = targetExam.value ?: JlptExamTargetEntity()
            repository.updateTargetExam(
                currentTarget.copy(
                    dailyGoalMinutes = minutes,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
