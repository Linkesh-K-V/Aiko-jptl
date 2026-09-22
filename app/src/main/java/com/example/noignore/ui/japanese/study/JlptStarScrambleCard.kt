package com.example.noignore.ui.japanese.study

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.audio.JapaneseTtsManager
import com.example.noignore.japanese.model.JlptStarSentenceQuestion
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton

/**
 * Interactive JLPT Star Rearrangement View (並べ替え ★ 問題).
 * Users tap scrambled fragments to place them in the 4 blanks.
 * Once placed, users check which tile lands on the Star (★) position!
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JlptStarScrambleCard(
    question: JlptStarSentenceQuestion,
    ttsManager: JapaneseTtsManager?,
    onAnswerSubmitted: (isCorrect: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Current placed order of tiles (list of tile strings)
    val placedTiles = remember(question.id) { mutableStateListOf<String>() }
    // Available remaining tiles
    val availableTiles = remember(question.id) {
        mutableStateListOf<String>().apply { addAll(question.scrambledTiles) }
    }

    var isSubmitted by remember(question.id) { mutableStateOf(false) }
    var isCorrect by remember(question.id) { mutableStateOf(false) }

    val correctOrderStrings = remember(question.id) {
        question.correctOrderIndices.map { question.scrambledTiles[it] }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
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
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star Question",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "JLPT ★ Star Rearrangement",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "JLPT ${question.jlptLevel} • 並べ替え",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sentence Context Frame with 4 Slot Blanks
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Arrange the tiles so the sentence is grammatically correct:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Sentence with blank slots
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (question.sentencePrefix.isNotBlank()) {
                            Text(
                                text = question.sentencePrefix,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // 4 Slots
                        for (i in 0..3) {
                            val isStarSlot = i == question.starPositionIndex
                            val placedTile = placedTiles.getOrNull(i)

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when {
                                    placedTile != null -> MaterialTheme.colorScheme.primaryContainer
                                    isStarSlot -> Color(0xFFFFF8E1)
                                    else -> MaterialTheme.colorScheme.surface
                                },
                                border = BorderStroke(
                                    1.5.dp,
                                    if (isStarSlot) Color(0xFFFFB300) else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .clickable(enabled = placedTile != null && !isSubmitted) {
                                        // Tap placed tile to remove it back to pool
                                        if (placedTile != null) {
                                            placedTiles.remove(placedTile)
                                            availableTiles.add(placedTile)
                                        }
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (placedTile != null) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            if (isStarSlot) {
                                                Text("★", fontSize = 12.sp, color = Color(0xFFFF8F00))
                                            }
                                            Text(
                                                text = placedTile,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            if (isStarSlot) {
                                                Text("★ [${i + 1}]", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF8F00))
                                            } else {
                                                Text("[${i + 1}] ___", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (question.sentenceSuffix.isNotBlank()) {
                            Text(
                                text = question.sentenceSuffix,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Available Tiles Pool
            if (!isSubmitted) {
                Text(
                    text = "TAP TILES IN SEQUENCE TO FILL SLOTS 1 TO 4:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    availableTiles.forEach { tile ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable {
                                if (placedTiles.size < 4) {
                                    placedTiles.add(tile)
                                    availableTiles.remove(tile)
                                }
                            }
                        ) {
                            Text(
                                text = tile,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons (Submit & Reset)
            if (!isSubmitted) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            placedTiles.clear()
                            availableTiles.clear()
                            availableTiles.addAll(question.scrambledTiles)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = placedTiles.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset")
                    }

                    Button(
                        onClick = {
                            val userStarTile = placedTiles.getOrNull(question.starPositionIndex)
                            val expectedStarTile = question.correctTileAtStar
                            isCorrect = (placedTiles.toList() == correctOrderStrings) || (userStarTile == expectedStarTile)
                            isSubmitted = true
                            onAnswerSubmitted(isCorrect)
                        },
                        modifier = Modifier.weight(2f),
                        enabled = placedTiles.size == 4,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Check Answer", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Feedback & Full Sentence Explanation
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    border = BorderStroke(1.dp, if (isCorrect) Color(0xFFA5D6A7) else Color(0xFFFFCDD2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Clear,
                                    contentDescription = null,
                                    tint = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                                Text(
                                    text = if (isCorrect) "正解! Excellent Work!" else "惜しい! Almost!",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                    color = if (isCorrect) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                                )
                            }

                            JapaneseSpeakerButton(
                                ttsManager = ttsManager,
                                textToSpeak = question.fullSentenceJapanese,
                                size = 32.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Correct ★ Tile: 「${question.correctTileAtStar}」",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE65100)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Full Sentence: ${question.fullSentenceJapanese}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF263238)
                        )
                        Text(
                            text = question.fullSentenceReading,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF455A64)
                        )
                        Text(
                            text = "“${question.englishMeaning}”",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF37474F)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "💡 Grammar Rule: ${question.grammarExplanation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF00695C),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
