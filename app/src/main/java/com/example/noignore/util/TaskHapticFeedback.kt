package com.example.noignore.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Utility to deliver satisfying tactile haptic feedback for task interactions,
 * specifically providing positive reinforcement when tasks are marked complete.
 */
object TaskHapticFeedback {

    /**
     * Triggers a rich, satisfying double-pulse tactile feedback designed specifically
     * for positive reinforcement upon task completion:
     * 1. A light, crisp intro tap (35ms @ ~150 amplitude)
     * 2. Brief 45ms pause
     * 3. A decisive, rewarding confirmation pulse (65ms @ 255 full amplitude)
     *
     * Also triggers Compose UI haptic feedback for system-level synchronization.
     */
    fun performTaskCompleteHaptic(
        context: Context,
        composeHapticFeedback: HapticFeedback? = null
    ) {
        try {
            // Trigger Compose haptic feedback if provided
            composeHapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress)

            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (vibrator.hasAmplitudeControl()) {
                    // Crisp, highly satisfying dual-pulse tactile sensation
                    val timings = longArrayOf(0, 35, 45, 65)
                    val amplitudes = intArrayOf(0, 150, 0, 255)
                    val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                    vibrator.vibrate(effect)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Predefined heavy click for positive confirmation
                    val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                    vibrator.vibrate(effect)
                } else {
                    val timings = longArrayOf(0, 35, 45, 65)
                    val effect = VibrationEffect.createWaveform(timings, -1)
                    vibrator.vibrate(effect)
                }
            } else {
                @Suppress("DEPRECATION")
                val timings = longArrayOf(0, 35, 45, 65)
                @Suppress("DEPRECATION")
                vibrator.vibrate(timings, -1)
            }
        } catch (_: Exception) {
            // Gracefully ignore on devices without vibration motors or in strict silent mode
        }
    }

    /**
     * Light, snappy single-tap haptic for secondary or subtle interactions.
     */
    fun performLightTapHaptic(
        context: Context,
        composeHapticFeedback: HapticFeedback? = null
    ) {
        try {
            composeHapticFeedback?.performHapticFeedback(HapticFeedbackType.TextHandleMove)

            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                vibrator.vibrate(effect)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(25)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    /**
     * Celebratory multi-pulse haptic burst for major milestones (e.g., streak advances or completing all tasks).
     */
    fun performCelebrationHaptic(context: Context) {
        try {
            val vibrator = getVibrator(context) ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (vibrator.hasAmplitudeControl()) {
                    val timings = longArrayOf(0, 40, 40, 50, 40, 80)
                    val amplitudes = intArrayOf(0, 160, 0, 200, 0, 255)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    val timings = longArrayOf(0, 40, 40, 50, 40, 80)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                }
            } else {
                @Suppress("DEPRECATION")
                val timings = longArrayOf(0, 40, 40, 50, 40, 80)
                @Suppress("DEPRECATION")
                vibrator.vibrate(timings, -1)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    /**
     * Returns true if this device has a physical vibration actuator.
     */
    fun hasVibrator(context: Context): Boolean {
        return try {
            getVibrator(context)?.hasVibrator() == true
        } catch (_: Exception) {
            false
        }
    }

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}
