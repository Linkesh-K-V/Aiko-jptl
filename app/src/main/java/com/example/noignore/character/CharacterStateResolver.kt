package com.example.noignore.character

object CharacterStateResolver {

    fun resolve(
        currentStreak: Int,
        missesLast7Days: Int,
        didMissToday: Boolean,
        justUnlockedAchievement: Boolean = false
    ): CharacterState {
        val level = when {
            currentStreak >= 100 -> CharacterLevel.LEGEND
            currentStreak >= 30 -> CharacterLevel.HARDENED
            currentStreak >= 7 -> CharacterLevel.DISCIPLINED
            currentStreak >= 3 -> CharacterLevel.CONSISTENT
            else -> CharacterLevel.BEGINNER
        }

        val mood = when {
            justUnlockedAchievement -> CharacterMood.PROUD
            didMissToday -> CharacterMood.DISAPPOINTED
            missesLast7Days >= 3 -> CharacterMood.SILENT
            missesLast7Days >= 2 -> CharacterMood.CONCERNED
            else -> CharacterMood.CALM
        }

        val tone = when (mood) {
            CharacterMood.PROUD -> if (level.levelNumber >= 4) DialogueTone.RESPECTFUL else DialogueTone.SUPPORTIVE
            CharacterMood.DISAPPOINTED -> DialogueTone.NEUTRAL
            CharacterMood.CONCERNED -> DialogueTone.STRICT
            CharacterMood.SILENT -> DialogueTone.COLD
            CharacterMood.CALM -> if (level.levelNumber >= 4) DialogueTone.RESPECTFUL else DialogueTone.STRICT
        }

        val reactionType = when {
            justUnlockedAchievement -> ReactionType.SPECIAL
            mood == CharacterMood.SILENT -> ReactionType.NONE
            mood == CharacterMood.PROUD -> ReactionType.FULL
            else -> ReactionType.SHORT
        }

        return CharacterState(
            level = level,
            mood = mood,
            tone = tone,
            reactionType = reactionType
        )
    }
}
