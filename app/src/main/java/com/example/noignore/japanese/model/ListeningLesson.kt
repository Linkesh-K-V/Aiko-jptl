package com.example.noignore.japanese.model

enum class ListeningExerciseType(val displayName: String, val icon: String) {
    COMPREHENSION("Meaning Comprehension", "🎧"),
    SCRIPT_RECOGNITION("Script & Kanji Match", "🈁"),
    PARTICLE_MATCH("Particle & Grammar", "⛩️"),
    DIALOGUE("Everyday Conversation", "💬")
}

data class ListeningExercise(
    val id: String,
    val jlptLevel: String, // "N5", "N4", "N3"
    val audioText: String, // The exact Japanese phrase spoken
    val type: ListeningExerciseType,
    val questionPrompt: String,
    val speakerRole: String = "Sensei Aiko",
    val options: List<String>,
    val correctOptionIndex: Int,
    val furiganaTranscript: String,
    val romaji: String,
    val englishTranslation: String,
    val explanation: String
)
