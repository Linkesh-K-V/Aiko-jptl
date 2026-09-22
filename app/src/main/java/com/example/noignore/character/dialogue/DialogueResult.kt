package com.example.noignore.character.dialogue

data class DialogueResult(
    val text: String?,
    val severity: DialogueSeverity = DialogueSeverity.LIGHT,
    val isBlocking: Boolean = false
)
