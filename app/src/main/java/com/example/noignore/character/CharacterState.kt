package com.example.noignore.character

data class CharacterState(
    val level: CharacterLevel = CharacterLevel.BEGINNER,
    val mood: CharacterMood = CharacterMood.CALM,
    val tone: DialogueTone = DialogueTone.STRICT,
    val reactionType: ReactionType = ReactionType.SHORT
)
