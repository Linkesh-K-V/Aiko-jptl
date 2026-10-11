package com.example.noignore.ui.japanese.study

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
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
import com.example.noignore.japanese.model.ParticleDrillQuestion
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton

/**
 * Fast-Paced Particle Battle Drill (助詞クイズ).
 * Tests tricky particle contrasts (は vs が, に vs で, を vs に).
 */
@Composable
fun ParticleDrillCard(
    question: ParticleDrillQuestion,
    ttsManager: JapaneseTtsManager?,
    onAnswered: (isCorrect: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedParticle by remember(question.id) { mutableStateOf<String?>(null) }
    val isSubmitted = selectedParticle != null
    val isCorrect = selectedParticle == question.correctParticle

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.5.dp, Color(0xFF0288D1).copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
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
                    Text("⚡", fontSize = 16.sp)
                    Text(
                        text = "Particle Battle (助詞クイズ)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0277BD)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE1F5FE)
                ) {
                    Text(
                        text = "JLPT ${question.jlptLevel}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF01579B),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sentence Card with Blank
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF1F8E9),
                border = BorderStroke(1.dp, Color(0xFFDCEDC8)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = question.sentenceWithBlank.replace("[___]", if (isSubmitted) " [ ${question.correctParticle} ] " else " [ ___ ] "),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1B5E20)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.blankFuriganaSentence.replace("[___]", if (isSubmitted) " [ ${question.correctParticle} ] " else " [ ___ ] "),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF33691E)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "“${question.englishMeaning}”",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF558B2F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Particle Options
            Text(
                text = "CHOOSE THE CORRECT PARTICLE:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0277BD)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                question.options.forEach { p ->
                    val isChosen = selectedParticle == p
                    val isTheCorrectOne = p == question.correctParticle

                    val backgroundColor = when {
                        !isSubmitted -> MaterialTheme.colorScheme.surfaceVariant
                        isTheCorrectOne -> Color(0xFFC8E6C9)
                        isChosen -> Color(0xFFFFCDD2)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }

                    val borderColor = when {
                        !isSubmitted -> MaterialTheme.colorScheme.outline
                        isTheCorrectOne -> Color(0xFF4CAF50)
                        isChosen -> Color(0xFFE53935)
                        else -> Color.Transparent
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = !isSubmitted) {
                                selectedParticle = p
                                onAnswered(p == question.correctParticle)
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = backgroundColor,
                        border = BorderStroke(1.5.dp, borderColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = p,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = when {
                                    !isSubmitted -> MaterialTheme.colorScheme.onSurfaceVariant
                                    isTheCorrectOne -> Color(0xFF1B5E20)
                                    isChosen -> Color(0xFFB71C1C)
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                }
                            )
                        }
                    }
                }
            }

            // Reason and Grammar Explanation
            if (isSubmitted) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    border = BorderStroke(1.dp, if (isCorrect) Color(0xFFA5D6A7) else Color(0xFFFFCDD2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Clear,
                                    contentDescription = null,
                                    tint = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isCorrect) "正解! Spot on!" else "Incorrect — particle 「${question.correctParticle}」 is needed",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = question.grammaticalReason,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF37474F),
                                lineHeight = 16.sp
                            )
                        }

                        val fullText = question.sentenceWithBlank.replace("[___]", question.correctParticle)
                        JapaneseSpeakerButton(
                            ttsManager = ttsManager,
                            textToSpeak = fullText,
                            size = 32.dp
                        )
                    }
                }
            }
        }
    }
}
