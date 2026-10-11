package com.example.noignore.character.dialogue

import com.example.noignore.character.CharacterMood
import com.example.noignore.character.CharacterState
import com.example.noignore.character.DialogueTone
import com.example.noignore.character.ReactionType

object DialogueResolver {

    fun resolve(state: CharacterState): DialogueResult {
        if (state.reactionType == ReactionType.NONE || state.mood == CharacterMood.SILENT) {
            return DialogueResult(text = null, severity = DialogueSeverity.LIGHT, isBlocking = false)
        }

        return when {
            state.mood == CharacterMood.PROUD && state.tone == DialogueTone.RESPECTFUL -> {
                DialogueResult(
                    text = "You kept your discipline. That matters.",
                    severity = DialogueSeverity.LIGHT,
                    isBlocking = false
                )
            }
            state.mood == CharacterMood.PROUD -> {
                DialogueResult(
                    text = "Good. This is how habits are built.",
                    severity = DialogueSeverity.LIGHT,
                    isBlocking = false
                )
            }
            state.mood == CharacterMood.DISAPPOINTED -> {
                DialogueResult(
                    text = "You didn't follow through today. Don't ignore that.",
                    severity = DialogueSeverity.NORMAL,
                    isBlocking = false
                )
            }
            state.mood == CharacterMood.CONCERNED -> {
                DialogueResult(
                    text = "This pattern is slipping.",
                    severity = DialogueSeverity.HEAVY,
                    isBlocking = true
                )
            }
            state.mood == CharacterMood.CALM && state.tone == DialogueTone.RESPECTFUL -> {
                DialogueResult(
                    text = "Stay steady.",
                    severity = DialogueSeverity.LIGHT,
                    isBlocking = false
                )
            }
            else -> {
                DialogueResult(
                    text = "Keep going.",
                    severity = DialogueSeverity.LIGHT,
                    isBlocking = false
                )
            }
        }
    }
}
