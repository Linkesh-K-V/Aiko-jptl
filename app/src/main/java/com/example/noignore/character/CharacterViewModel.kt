package com.example.noignore.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noignore.character.dialogue.DialogueResolver
import com.example.noignore.character.dialogue.DialogueResult
import com.example.noignore.character.personality.CharacterPersonality
import com.example.noignore.character.personality.PersonalityDialogueAdapter
import com.example.noignore.character.personality.PersonalityResolver
import com.example.noignore.character.personality.PersonalityTheme
import com.example.noignore.character.personality.PersonalityThemes
import com.example.noignore.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class CharacterViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    private val _characterState = MutableStateFlow(CharacterState())
    val characterState: StateFlow<CharacterState> = _characterState.asStateFlow()

    private val _dialogue = MutableStateFlow(DialogueResult(text = "Keep going."))
    val dialogue: StateFlow<DialogueResult> = _dialogue.asStateFlow()

    private val _selectedPersonality = MutableStateFlow(CharacterPersonality.SENSEI)
    val selectedPersonality: StateFlow<CharacterPersonality> = _selectedPersonality.asStateFlow()

    private val _unlockedPersonalities = MutableStateFlow(listOf(CharacterPersonality.SENSEI, CharacterPersonality.SERGEANT))
    val unlockedPersonalities: StateFlow<List<CharacterPersonality>> = _unlockedPersonalities.asStateFlow()

    private val _personalityTheme = MutableStateFlow(PersonalityThemes.forPersonality(CharacterPersonality.SENSEI))
    val personalityTheme: StateFlow<PersonalityTheme> = _personalityTheme.asStateFlow()

    private var lastKnownStreak = 0

    fun selectPersonality(personality: CharacterPersonality) {
        _selectedPersonality.value = personality
        refresh(lastKnownStreak)
    }

    fun refresh(currentStreak: Int, justUnlockedAchievement: Boolean = false) {
        lastKnownStreak = currentStreak
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val today = LocalDate.now().toString()
                val sevenDaysAgo = LocalDate.now().minusDays(7).toString()

                val missesLast7 = db.taskHistoryDao().countMissesBetween(sevenDaysAgo, today)
                val missesToday = db.taskHistoryDao().countMissedOnDate(today)
                val totalCompleted = db.taskHistoryDao().countTotalDone()

                applyState(
                    currentStreak = currentStreak,
                    missesLast7 = missesLast7,
                    didMissToday = missesToday > 0,
                    totalCompleted = totalCompleted,
                    justUnlockedAchievement = justUnlockedAchievement
                )
            }
        }
    }

    private fun applyState(
        currentStreak: Int,
        missesLast7: Int,
        didMissToday: Boolean,
        totalCompleted: Int,
        justUnlockedAchievement: Boolean
    ) {
        val resolvedState = CharacterStateResolver.resolve(
            currentStreak = currentStreak,
            missesLast7Days = missesLast7,
            didMissToday = didMissToday,
            justUnlockedAchievement = justUnlockedAchievement
        )

        val baseDialogue = DialogueResolver.resolve(resolvedState)
        val unlocked = PersonalityResolver.resolveUnlocked(currentStreak, totalCompleted)
        val activePersonality = PersonalityResolver.resolveActive(unlocked, _selectedPersonality.value)

        val adaptedText = PersonalityDialogueAdapter.adapt(baseDialogue.text, activePersonality)
        val adaptedDialogue = baseDialogue.copy(text = adaptedText)
        val theme = PersonalityThemes.forPersonality(activePersonality)

        _characterState.value = resolvedState
        _dialogue.value = adaptedDialogue
        _unlockedPersonalities.value = unlocked
        _personalityTheme.value = theme
    }
}
