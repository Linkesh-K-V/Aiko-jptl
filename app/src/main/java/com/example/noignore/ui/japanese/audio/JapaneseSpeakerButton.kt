package com.example.noignore.ui.japanese.audio

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.audio.JapaneseTtsManager

/**
 * Reusable Japanese Speaker Button with visual audio pulse effect.
 * Allows language learners to hear native pronunciation of words, readings, and sentences.
 */
@Composable
fun JapaneseSpeakerButton(
    ttsManager: JapaneseTtsManager?,
    textToSpeak: String,
    modifier: Modifier = Modifier,
    isSlow: Boolean = false,
    size: Dp = 38.dp,
    containerColor: Color? = null,
    iconColor: Color? = null,
    testTag: String = "speaker_button"
) {
    if (ttsManager == null || textToSpeak.isBlank()) return

    val isSpeaking by ttsManager.isSpeaking.collectAsState()
    val currentUtterance by ttsManager.currentUtteranceId.collectAsState()
    val isCurrentSpeaking = isSpeaking && currentUtterance?.contains(textToSpeak.take(10).hashCode().toString()) == true

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isCurrentSpeaking) 1.15f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speaker_pulse"
    )

    val bgColor by animateColorAsState(
        targetValue = when {
            isCurrentSpeaking -> MaterialTheme.colorScheme.primaryContainer
            containerColor != null -> containerColor
            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        },
        label = "bg_color"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isCurrentSpeaking -> MaterialTheme.colorScheme.primary
            iconColor != null -> iconColor
            else -> MaterialTheme.colorScheme.primary
        },
        label = "icon_color"
    )

    Surface(
        modifier = modifier
            .size(size)
            .scale(if (isCurrentSpeaking) pulseScale else 1f)
            .clip(CircleShape)
            .clickable {
                if (isCurrentSpeaking) {
                    ttsManager.stop()
                } else {
                    val uid = "jp_${textToSpeak.take(10).hashCode()}_${System.currentTimeMillis()}"
                    ttsManager.speak(textToSpeak, isSlow = isSlow, utteranceId = uid)
                }
            }
            .testTag(testTag),
        shape = CircleShape,
        color = bgColor,
        border = BorderStroke(
            1.dp,
            if (isCurrentSpeaking) MaterialTheme.colorScheme.primary else Color.Transparent
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size)
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = if (isSlow) "Listen slowly" else "Listen to pronunciation",
                tint = contentColor,
                modifier = Modifier.size(size * 0.58f)
            )
        }
    }
}

/**
 * Dual Listen Pill with both Normal (1.0x) and Slow (0.75x) pronunciation buttons.
 */
@Composable
fun JapaneseAudioControlPill(
    ttsManager: JapaneseTtsManager?,
    textToSpeak: String,
    modifier: Modifier = Modifier,
    label: String = "Listen"
) {
    if (ttsManager == null || textToSpeak.isBlank()) return

    val isSpeaking by ttsManager.isSpeaking.collectAsState()

    Surface(
        modifier = modifier.testTag("audio_control_pill"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Normal 1.0x Listen Button
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    val uid = "jp_norm_${textToSpeak.take(10).hashCode()}_${System.currentTimeMillis()}"
                    ttsManager.speak(textToSpeak, isSlow = false, utteranceId = uid)
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Normal audio",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$label (1.0x)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Slow 0.72x Listen Button
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.clickable {
                    val uid = "jp_slow_${textToSpeak.take(10).hashCode()}_${System.currentTimeMillis()}"
                    ttsManager.speak(textToSpeak, isSlow = true, utteranceId = uid)
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🐢", fontSize = 12.sp)
                    Text(
                        text = "Slow (0.7x)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
