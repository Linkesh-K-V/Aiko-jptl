package com.example.noignore.character.personality

object PersonalityDialogueAdapter {

    fun adapt(base: String?, personality: CharacterPersonality): String? {
        if (base == null) return null

        return when (personality) {
            CharacterPersonality.SENSEI -> "継続は力なり (Continuance is power)! $base"
            CharacterPersonality.SERGEANT -> base
            CharacterPersonality.COACH -> "You've got this. $base"
            CharacterPersonality.SAGE -> "Remember: discipline is built quietly. $base"
            CharacterPersonality.ANALYST -> "Data indicates inconsistency. $base"
            CharacterPersonality.JESTER -> "Well… look who showed up. $base"
        }
    }
}
