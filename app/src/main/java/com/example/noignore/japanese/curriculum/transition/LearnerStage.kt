package com.example.noignore.japanese.curriculum.transition

/**
 * Authoritative 7-Stage Learner Progression State Machine.
 * Governs the educational lifecycle from absolute zero-knowledge beginner
 * through N5 certification, transition bridge, and N4 curriculum completion.
 */
enum class LearnerStage(
    val levelCode: String,
    val displayName: String,
    val stageOrder: Int
) {
    N5_FOUNDATION("N5", "N5 Kana Foundation", 1),
    N5_ACTIVE("N5", "N5 Active Curriculum", 2),
    N5_COMPLETED("N5", "N5 Syllabus Completed", 3),
    N5_CERTIFIED("N5", "N5 Certified", 4),
    N4_BRIDGE("N4", "N5→N4 Transition Bridge", 5),
    N4_ACTIVE("N4", "N4 Active Curriculum", 6),
    N4_COMPLETED("N4", "N4 Syllabus Completed", 7);

    fun isN4Accessible(): Boolean = stageOrder >= N4_ACTIVE.stageOrder
    fun isN5Complete(): Boolean = stageOrder >= N5_COMPLETED.stageOrder
    fun isN5Certified(): Boolean = stageOrder >= N5_CERTIFIED.stageOrder
    fun isInBridge(): Boolean = this == N4_BRIDGE
}

data class TransitionResult(
    val isSuccess: Boolean,
    val fromStage: LearnerStage,
    val toStage: LearnerStage,
    val message: String,
    val missingRequirements: List<String> = emptyList()
)

data class N5CertificationReport(
    val isCertified: Boolean,
    val kanaScore: Float,
    val grammarScore: Float,
    val kanjiScore: Float,
    val diagnosticScore: Float,
    val missingRequirements: List<String>
)

data class N4BridgeReport(
    val isBridgeComplete: Boolean,
    val sessionsCompleted: Int,
    val requiredSessions: Int,
    val diagnosticScore: Float,
    val missingRequirements: List<String>
)
