package com.example.noignore.character.personality

object PersonalityResolver {

    fun resolveUnlocked(streak: Int, totalCompleted: Int): List<CharacterPersonality> {
        val list = mutableListOf(CharacterPersonality.SENSEI, CharacterPersonality.SERGEANT)
        if (streak >= 7) {
            list.add(CharacterPersonality.COACH)
        }
        if (streak >= 30) {
            list.add(CharacterPersonality.ANALYST)
        }
        if (streak >= 100) {
            list.add(CharacterPersonality.SAGE)
        }
        if (totalCompleted >= 50) {
            list.add(CharacterPersonality.JESTER)
        }
        return list
    }

    fun resolveActive(
        unlocked: List<CharacterPersonality>,
        selected: CharacterPersonality
    ): CharacterPersonality {
        return if (unlocked.contains(selected)) {
            selected
        } else {
            CharacterPersonality.SERGEANT
        }
    }
}
