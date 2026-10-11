package com.example.noignore.japanese.curriculum.transition

import android.content.Context
import android.content.SharedPreferences
import com.example.noignore.data.learner.LearnerProfileEntity

/**
 * Manages formal stage transitions across the N5 and N4 curriculum boundaries.
 * Prevents premature or illegal stage skipping (e.g. N5_FOUNDATION -> N4_ACTIVE).
 */
object LearnerTransitionManager {

    private const val PREFS_NAME = "aiko_jlpt_learner_transition"
    private const val KEY_CURRENT_STAGE = "key_current_learner_stage"
    private const val KEY_BRIDGE_SESSIONS = "key_bridge_sessions_completed"
    private const val KEY_BRIDGE_DIAGNOSTIC_SCORE = "key_bridge_diagnostic_score"
    private const val KEY_N5_DIAGNOSTIC_SCORE = "key_n5_diagnostic_score"
    private const val KEY_N5_CERTIFIED_AT = "key_n5_certified_at_ms"

    // Aiko Certification Thresholds (defined in Phase 18A specification)
    const val THRESHOLD_N5_KANA = 0.90f
    const val THRESHOLD_N5_GRAMMAR = 0.85f
    const val THRESHOLD_N5_KANJI = 0.80f
    const val THRESHOLD_N5_DIAGNOSTIC = 0.80f

    // Bridge Completion Thresholds
    const val MIN_BRIDGE_SESSIONS = 3
    const val THRESHOLD_BRIDGE_DIAGNOSTIC = 0.80f

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getLearnerStage(context: Context): LearnerStage {
        val prefs = getPrefs(context)
        val stageName = prefs.getString(KEY_CURRENT_STAGE, null)
        if (stageName != null) {
            try {
                return LearnerStage.valueOf(stageName)
            } catch (_: Exception) {
                // fallback below
            }
        }

        // Initialize based on user preferences if not set
        val userPrefs = context.getSharedPreferences("aiko_jlpt_user_prefs", Context.MODE_PRIVATE)
        val startingLevel = userPrefs.getString("learner_starting_level", "ABSOLUTE_BEGINNER")
        val defaultStage = when (startingLevel) {
            "SOME_KANA" -> LearnerStage.N5_ACTIVE
            "ADVANCED_EXPLORER" -> LearnerStage.N5_COMPLETED
            else -> LearnerStage.N5_FOUNDATION
        }
        setLearnerStage(context, defaultStage)
        return defaultStage
    }

    fun setLearnerStage(context: Context, stage: LearnerStage) {
        getPrefs(context).edit().putString(KEY_CURRENT_STAGE, stage.name).apply()
    }

    fun getBridgeSessionsCompleted(context: Context): Int {
        return getPrefs(context).getInt(KEY_BRIDGE_SESSIONS, 0)
    }

    fun recordBridgeSession(context: Context) {
        val current = getBridgeSessionsCompleted(context)
        getPrefs(context).edit().putInt(KEY_BRIDGE_SESSIONS, current + 1).apply()
    }

    fun setBridgeDiagnosticScore(context: Context, score: Float) {
        getPrefs(context).edit().putFloat(KEY_BRIDGE_DIAGNOSTIC_SCORE, score).apply()
    }

    fun getBridgeDiagnosticScore(context: Context): Float {
        return getPrefs(context).getFloat(KEY_BRIDGE_DIAGNOSTIC_SCORE, 0f)
    }

    fun setN5DiagnosticScore(context: Context, score: Float) {
        getPrefs(context).edit().putFloat(KEY_N5_DIAGNOSTIC_SCORE, score).apply()
    }

    fun getN5DiagnosticScore(context: Context): Float {
        return getPrefs(context).getFloat(KEY_N5_DIAGNOSTIC_SCORE, 0f)
    }

    /**
     * Evaluates whether the learner meets all N5 certification benchmarks.
     */
    fun evaluateN5Certification(
        profile: LearnerProfileEntity,
        diagnosticScore: Float
    ): N5CertificationReport {
        val missing = mutableListOf<String>()

        if (profile.kanaMasteryScore < THRESHOLD_N5_KANA) {
            missing.add("Kana mastery (${(profile.kanaMasteryScore * 100).toInt()}%) below required ${(THRESHOLD_N5_KANA * 100).toInt()}%")
        }
        if (profile.grammarMasteryScore < THRESHOLD_N5_GRAMMAR) {
            missing.add("N5 grammar mastery (${(profile.grammarMasteryScore * 100).toInt()}%) below required ${(THRESHOLD_N5_GRAMMAR * 100).toInt()}%")
        }
        if (profile.kanjiMasteryScore < THRESHOLD_N5_KANJI) {
            missing.add("N5 kanji mastery (${(profile.kanjiMasteryScore * 100).toInt()}%) below required ${(THRESHOLD_N5_KANJI * 100).toInt()}%")
        }
        if (diagnosticScore < THRESHOLD_N5_DIAGNOSTIC) {
            missing.add("N5 comprehensive diagnostic score (${(diagnosticScore * 100).toInt()}%) below required ${(THRESHOLD_N5_DIAGNOSTIC * 100).toInt()}%")
        }

        return N5CertificationReport(
            isCertified = missing.isEmpty(),
            kanaScore = profile.kanaMasteryScore,
            grammarScore = profile.grammarMasteryScore,
            kanjiScore = profile.kanjiMasteryScore,
            diagnosticScore = diagnosticScore,
            missingRequirements = missing
        )
    }

    /**
     * Evaluates whether N4 Bridge requirements are satisfied.
     */
    fun evaluateN4Bridge(
        sessionsCompleted: Int,
        diagnosticScore: Float
    ): N4BridgeReport {
        val missing = mutableListOf<String>()

        if (sessionsCompleted < MIN_BRIDGE_SESSIONS) {
            missing.add("Completed bridge sessions ($sessionsCompleted) below required $MIN_BRIDGE_SESSIONS")
        }
        if (diagnosticScore < THRESHOLD_BRIDGE_DIAGNOSTIC) {
            missing.add("Bridge transition diagnostic score (${(diagnosticScore * 100).toInt()}%) below required ${(THRESHOLD_BRIDGE_DIAGNOSTIC * 100).toInt()}%")
        }

        return N4BridgeReport(
            isBridgeComplete = missing.isEmpty(),
            sessionsCompleted = sessionsCompleted,
            requiredSessions = MIN_BRIDGE_SESSIONS,
            diagnosticScore = diagnosticScore,
            missingRequirements = missing
        )
    }

    /**
     * Pure validation logic for stage transitions without side effects.
     */
    fun evaluateTransition(
        fromStage: LearnerStage,
        toStage: LearnerStage,
        profile: LearnerProfileEntity,
        bridgeSessionsCompleted: Int = 0,
        bridgeDiagnosticScore: Float = 0f,
        n5DiagnosticScore: Float = 0f
    ): TransitionResult {
        // No-op transition
        if (fromStage == toStage) {
            return TransitionResult(true, fromStage, toStage, "Already in stage ${toStage.name}")
        }

        // Backward / Refresher transitions are always legal
        if (toStage.stageOrder < fromStage.stageOrder) {
            return TransitionResult(true, fromStage, toStage, "Regressed/refreshed to earlier stage ${toStage.name}")
        }

        // Disallow skipping stages forward
        if (toStage.stageOrder > fromStage.stageOrder + 1) {
            return TransitionResult(
                isSuccess = false,
                fromStage = fromStage,
                toStage = toStage,
                message = "Illegal transition: cannot jump from ${fromStage.name} directly to ${toStage.name}. Must complete intermediate stages.",
                missingRequirements = listOf("Sequential progression required through intermediate stage")
            )
        }

        // Specific legal step-by-step transition checks
        return when (toStage) {
            LearnerStage.N5_ACTIVE -> {
                if (profile.kanaMasteryScore >= 0.50f) {
                    TransitionResult(true, fromStage, toStage, "Kana foundation unlocked N5_ACTIVE")
                } else {
                    TransitionResult(
                        false, fromStage, toStage,
                        "Kana mastery (${(profile.kanaMasteryScore * 100).toInt()}%) must reach 50% to advance to N5_ACTIVE",
                        listOf("Kana mastery >= 0.50")
                    )
                }
            }

            LearnerStage.N5_COMPLETED -> {
                if (profile.kanaMasteryScore >= 0.70f && profile.vocabMasteryScore >= 0.50f && profile.grammarMasteryScore >= 0.50f) {
                    TransitionResult(true, fromStage, toStage, "N5 curriculum syllabus completed")
                } else {
                    TransitionResult(
                        false, fromStage, toStage,
                        "N5 core curriculum competencies not fully satisfied to complete N5 syllabus",
                        listOf("Complete all core N5 syllabus lessons")
                    )
                }
            }

            LearnerStage.N5_CERTIFIED -> {
                val certReport = evaluateN5Certification(profile, n5DiagnosticScore)
                if (certReport.isCertified) {
                    TransitionResult(true, fromStage, toStage, "N5 certification achieved")
                } else {
                    TransitionResult(
                        false, fromStage, toStage,
                        "N5 certification criteria not met",
                        certReport.missingRequirements
                    )
                }
            }

            LearnerStage.N4_BRIDGE -> {
                if (fromStage.isN5Certified()) {
                    TransitionResult(true, fromStage, toStage, "Entered N4_BRIDGE for transition preparation")
                } else {
                    TransitionResult(
                        false, fromStage, toStage,
                        "Cannot enter N4_BRIDGE without N5 certification",
                        listOf("Attain N5_CERTIFIED stage first")
                    )
                }
            }

            LearnerStage.N4_ACTIVE -> {
                if (fromStage != LearnerStage.N4_BRIDGE) {
                    TransitionResult(
                        false, fromStage, toStage,
                        "Cannot enter N4_ACTIVE directly. Must pass through N4_BRIDGE",
                        listOf("Complete N4_BRIDGE stage")
                    )
                } else {
                    val bridgeReport = evaluateN4Bridge(bridgeSessionsCompleted, bridgeDiagnosticScore)
                    if (bridgeReport.isBridgeComplete) {
                        TransitionResult(true, fromStage, toStage, "N4_BRIDGE successfully completed. Entering N4_ACTIVE")
                    } else {
                        TransitionResult(
                            false, fromStage, toStage,
                            "N4_BRIDGE requirements not yet satisfied",
                            bridgeReport.missingRequirements
                        )
                    }
                }
            }

            LearnerStage.N4_COMPLETED -> {
                if (fromStage == LearnerStage.N4_ACTIVE && profile.grammarMasteryScore >= 0.80f && profile.vocabMasteryScore >= 0.80f) {
                    TransitionResult(true, fromStage, toStage, "N4 syllabus completed")
                } else {
                    TransitionResult(
                        false, fromStage, toStage,
                        "N4 active mastery incomplete",
                        listOf("Complete N4 curriculum requirements")
                    )
                }
            }

            else -> TransitionResult(false, fromStage, toStage, "Unknown transition target")
        }
    }

    /**
     * Executes transition with persistence if valid.
     */
    fun attemptTransition(
        context: Context,
        targetStage: LearnerStage,
        profile: LearnerProfileEntity,
        diagnosticScore: Float = 0f
    ): TransitionResult {
        val currentStage = getLearnerStage(context)
        val bridgeSessions = getBridgeSessionsCompleted(context)
        val bridgeScore = getBridgeDiagnosticScore(context)

        val result = evaluateTransition(
            fromStage = currentStage,
            toStage = targetStage,
            profile = profile,
            bridgeSessionsCompleted = bridgeSessions,
            bridgeDiagnosticScore = bridgeScore,
            n5DiagnosticScore = diagnosticScore
        )

        if (result.isSuccess) {
            setLearnerStage(context, targetStage)
            if (targetStage == LearnerStage.N5_CERTIFIED) {
                getPrefs(context).edit().putLong(KEY_N5_CERTIFIED_AT, System.currentTimeMillis()).apply()
            }
        }

        return result
    }
}
