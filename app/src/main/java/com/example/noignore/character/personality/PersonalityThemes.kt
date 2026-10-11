package com.example.noignore.character.personality

import androidx.compose.ui.graphics.Color

data class PersonalityTheme(
    val primaryColor: Color,
    val containerColor: Color,
    val tagText: String,
    val iconEmoji: String
)

object PersonalityThemes {

    fun forPersonality(personality: CharacterPersonality): PersonalityTheme {
        return when (personality) {
            CharacterPersonality.SENSEI -> PersonalityTheme(
                primaryColor = Color(0xFFD81B60),
                containerColor = Color(0x33D81B60),
                tagText = "JAPANESE SENSEI • 継続は力なり",
                iconEmoji = "🌸"
            )
            CharacterPersonality.SERGEANT -> PersonalityTheme(
                primaryColor = Color(0xFFC62828),
                containerColor = Color(0x33C62828),
                tagText = "STRICT & DIRECT",
                iconEmoji = "🪖"
            )
            CharacterPersonality.COACH -> PersonalityTheme(
                primaryColor = Color(0xFF2E7D32),
                containerColor = Color(0x332E7D32),
                tagText = "MOTIVATING & ENERGETIC",
                iconEmoji = "📣"
            )
            CharacterPersonality.SAGE -> PersonalityTheme(
                primaryColor = Color(0xFF1565C0),
                containerColor = Color(0x331565C0),
                tagText = "CALM & MEASURED",
                iconEmoji = "🧘"
            )
            CharacterPersonality.ANALYST -> PersonalityTheme(
                primaryColor = Color(0xFF6A1B9A),
                containerColor = Color(0x336A1B9A),
                tagText = "DATA & LOGIC",
                iconEmoji = "📊"
            )
            CharacterPersonality.JESTER -> PersonalityTheme(
                primaryColor = Color(0xFFEF6C00),
                containerColor = Color(0x33EF6C00),
                tagText = "SHARP & WITTY",
                iconEmoji = "🃏"
            )
        }
    }
}
