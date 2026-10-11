package com.example.noignore.ui.japanese.audio

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.noignore.audio.JapaneseSpeechRecognizer
import com.example.noignore.audio.JapaneseTtsManager
import com.example.noignore.audio.ShadowingSpeechState

/**
 * Pronunciation Shadowing Practice Card.
 * Allows Japanese learners to listen to native audio, tap the microphone to shadow speak,
 * and receive live pronunciation accuracy scoring against target phrase/readings.
 */
@Composable
fun JapaneseShadowingCard(
    targetText: String,
    targetReading: String = "",
    targetRomaji: String = "",
    englishMeaning: String = "",
    ttsManager: JapaneseTtsManager? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val speechRecognizer = remember { JapaneseSpeechRecognizer(context) }
    val speechState by speechRecognizer.state.collectAsState()
    val isListening by speechRecognizer.isListening.collectAsState()

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            val compareTarget = targetReading.ifBlank { targetText }
            speechRecognizer.startListening(compareTarget)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer.stopListening()
        }
    }

    // Microphone pulsation animation while listening
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    val activeColor = if (isListening) Color(0xFFE53935) else MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.2.dp, activeColor.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🎙️", fontSize = 16.sp)
                    Text(
                        text = "PRONUNCIATION SHADOWING",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = if (isListening) "Listening..." else "Speak & Match",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Target Japanese Phrase & Audio Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = targetText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (targetReading.isNotBlank() && targetReading != targetText) {
                        Text(
                            text = targetReading,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (targetRomaji.isNotBlank()) {
                        Text(
                            text = targetRomaji,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Reference Audio Playback
                JapaneseSpeakerButton(
                    ttsManager = ttsManager,
                    textToSpeak = targetText,
                    size = 40.dp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Shadowing Mic Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isListening) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(
                        2.dp,
                        if (isListening) Color(0xFFE53935) else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(56.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (isListening) {
                                speechRecognizer.stopListening()
                            } else {
                                if (hasMicPermission) {
                                    val compareTarget = targetReading.ifBlank { targetText }
                                    speechRecognizer.startListening(compareTarget)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        modifier = Modifier.testTag("shadowing_mic_button")
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (isListening) "Stop Listening" else "Start Shadowing Speech",
                            tint = if (isListening) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isListening) "Speak now in Japanese (話してください)..." else "Tap mic to shadow this pronunciation",
                style = MaterialTheme.typography.bodySmall,
                color = if (isListening) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isListening) FontWeight.Bold else FontWeight.Normal
            )

            // Feedback Display
            when (val st = speechState) {
                is ShadowingSpeechState.Success -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    val isGoodMatch = st.similarityPercent >= 70
                    val gradeColor = if (st.similarityPercent >= 85) Color(0xFF2E7D32)
                    else if (st.similarityPercent >= 60) Color(0xFFF57F17)
                    else Color(0xFFD32F2F)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = gradeColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, gradeColor.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isGoodMatch) "🎉 Excellent Accent!" else "💪 Keep practicing!",
                                    fontWeight = FontWeight.Bold,
                                    color = gradeColor,
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = "${st.similarityPercent}% Match",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = gradeColor,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Heard: “${st.recognizedText}”",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                is ShadowingSpeechState.Error -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = st.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {}
            }
        }
    }
}
