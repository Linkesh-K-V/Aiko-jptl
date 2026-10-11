package com.example.noignore.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.noignore.character.CharacterMood
import com.example.noignore.character.CharacterState as LogicCharacterState
import com.example.noignore.character.dialogue.DialogueResult
import com.example.noignore.character.personality.CharacterPersonality
import com.example.noignore.character.personality.PersonalityTheme
import com.example.noignore.model.StreakState

@Composable
fun CharacterComposable(
    logicState: LogicCharacterState,
    dialogue: DialogueResult,
    streakCount: Int = 0,
    streakState: StreakState = StreakState.NONE,
    personality: CharacterPersonality = CharacterPersonality.SENSEI,
    personalityTheme: PersonalityTheme? = null,
    onPersonalityClick: () -> Unit = {},
    onStudyClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AoiSenseiFullAnimeCharacter(
        mood = logicState.mood,
        streakCount = streakCount,
        level = logicState.level,
        initialDialogue = dialogue.text,
        personality = personality,
        personalityTheme = personalityTheme,
        onPersonalityClick = onPersonalityClick,
        onStudyClick = onStudyClick,
        modifier = modifier
    )
}

