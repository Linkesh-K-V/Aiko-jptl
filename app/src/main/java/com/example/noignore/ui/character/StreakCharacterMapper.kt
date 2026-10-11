package com.example.noignore.ui.character

import com.example.noignore.model.StreakState

object StreakCharacterMapper {

    fun map(streakState: StreakState): CharacterState {
        return when (streakState) {
            StreakState.NONE -> CharacterState.SHAME
            StreakState.ROOKIE -> CharacterState.NEUTRAL
            StreakState.CONSISTENT -> CharacterState.HAPPY
            StreakState.STRONG -> CharacterState.HAPPY
            StreakState.DISCIPLINED -> CharacterState.PROUD
            StreakState.ELITE -> CharacterState.PROUD
            StreakState.LEGENDARY -> CharacterState.PROUD
        }
    }
}
