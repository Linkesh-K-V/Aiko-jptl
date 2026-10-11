package com.example.noignore.ui.japanese.study

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.audio.JapaneseTtsManager
import com.example.noignore.japanese.model.SurvivalRoleplayScenario
import com.example.noignore.ui.japanese.audio.JapaneseShadowingCard
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton

/**
 * Situational Survival Roleplay View.
 * Realistic conversational exchanges in convenience stores, izakayas, and train stations.
 * Includes native speed toggle (0.75x, 1.0x, 1.25x), cultural tips, and interactive voice shadowing.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SurvivalRoleplayView(
    scenarios: List<SurvivalRoleplayScenario>,
    ttsManager: JapaneseTtsManager?,
    modifier: Modifier = Modifier
) {
    var selectedScenarioIndex by remember { mutableStateOf(0) }
    val currentScenario = scenarios.getOrNull(selectedScenarioIndex) ?: scenarios.firstOrNull() ?: return
    var activeShadowingTurnIndex by remember { mutableStateOf<Int?>(null) }
    var speechSpeedSlow by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Scenario Selection Tabs
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            scenarios.forEachIndexed { idx, s ->
                val isSelected = idx == selectedScenarioIndex
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.clickable {
                        selectedScenarioIndex = idx
                        activeShadowingTurnIndex = null
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(s.categoryEmoji, fontSize = 16.sp)
                        Text(
                            text = s.title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Scenario Card Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${currentScenario.categoryEmoji} ${currentScenario.japaneseTitle}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = currentScenario.locationDescription,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Speed Toggle Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (speechSpeedSlow) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (speechSpeedSlow) Color(0xFFFFB74D) else Color.Transparent),
                        modifier = Modifier.clickable { speechSpeedSlow = !speechSpeedSlow }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Speed",
                                modifier = Modifier.size(14.dp),
                                tint = if (speechSpeedSlow) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (speechSpeedSlow) "0.75x Slow" else "1.0x Normal",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (speechSpeedSlow) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Key Survival Phrases
                if (currentScenario.survivalKeyPhrases.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "GOLDEN SURVIVAL PHRASES (必須フレーズ):",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        currentScenario.survivalKeyPhrases.forEach { phrase ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFF8E1),
                                border = BorderStroke(1.dp, Color(0xFFFFE082))
                            ) {
                                Text(
                                    text = phrase,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFBF360C),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dialogue Turns
        currentScenario.turns.forEachIndexed { turnIdx, turn ->
            val isLearner = turn.suggestedLearnerResponse
            val isShadowingActive = activeShadowingTurnIndex == turnIdx

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isLearner) Color(0xFFE3F2FD) else Color(0xFFF5F5F5)
                ),
                border = BorderStroke(
                    1.dp,
                    if (isLearner) Color(0xFF90CAF9) else Color(0xFFE0E0E0)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isLearner) Color(0xFF1976D2) else Color(0xFF616161)
                        ) {
                            Text(
                                text = turn.speakerRole,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isLearner) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isShadowingActive) Color(0xFFC8E6C9) else Color.White,
                                    border = BorderStroke(1.dp, Color(0xFF81C784)),
                                    modifier = Modifier.clickable {
                                        activeShadowingTurnIndex = if (isShadowingActive) null else turnIdx
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = "Shadow",
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (isShadowingActive) "Practicing" else "Shadow",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }
                            }

                            JapaneseSpeakerButton(
                                ttsManager = ttsManager,
                                textToSpeak = turn.japaneseText,
                                isSlow = speechSpeedSlow,
                                size = 32.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = turn.japaneseText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isLearner) Color(0xFF0D47A1) else Color(0xFF212121)
                    )
                    Text(
                        text = "${turn.readingText} (${turn.romajiText})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF546E7A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "“${turn.englishText}”",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF37474F)
                    )

                    // Cultural Nuance Tip
                    if (turn.culturalNuanceTip.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFF9C4),
                            border = BorderStroke(1.dp, Color(0xFFFFF59D)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFFF57F17),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = turn.culturalNuanceTip,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }
                    }

                    // Shadowing Card Inline
                    if (isShadowingActive) {
                        Spacer(modifier = Modifier.height(10.dp))
                        JapaneseShadowingCard(
                            targetText = turn.japaneseText,
                            targetReading = turn.readingText,
                            targetRomaji = turn.romajiText,
                            englishMeaning = turn.englishText,
                            ttsManager = ttsManager
                        )
                    }
                }
            }
        }
    }
}
