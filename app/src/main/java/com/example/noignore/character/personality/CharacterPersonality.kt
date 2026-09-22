package com.example.noignore.character.personality

enum class CharacterPersonality(val displayName: String, val unlockCriteria: String) {
    SENSEI("Teacher Aiko (愛子先生)", "Default - Unlocked"),
    SERGEANT("Sergeant Discipline", "Default - Unlocked"),
    COACH("Coach Motivation", "Requires 7-day streak"),
    SAGE("Master Sage", "Requires 100-day streak"),
    ANALYST("Dr. Analytics", "Requires 30-day streak"),
    JESTER("Court Jester", "Requires 50 tasks completed")
}
