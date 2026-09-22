package com.example.noignore.ui.japanese.study

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
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Check
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
import com.example.noignore.japanese.data.JapaneseDeckRepository
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.japanese.util.ParsedToken
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton

/**
 * Interactive Morphological Sentence Mining Component.
 *
 * Breaks down any Japanese sentence into selectable grammatical and lexical tokens.
 * Users can tap tokens to inspect readings, definitions, part-of-speech tags,
 * and seamlessly save new vocabulary into their local FSRS review deck.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MorphologicalSentenceMiningCard(
    sentence: String,
    englishTranslation: String,
    romaji: String = "",
    repository: JapaneseDeckRepository? = null,
    ttsManager: JapaneseTtsManager? = null,
    modifier: Modifier = Modifier
) {
    if (sentence.isBlank()) return

    val tokens: List<ParsedToken> = remember(sentence) {
        repository?.parseSentenceWithMining(sentence)
            ?: com.example.noignore.japanese.util.JapaneseSentenceTokenizer.tokenize(sentence)
    }

    var selectedToken by remember(sentence) { mutableStateOf<ParsedToken?>(null) }
    var minedItems by remember(sentence) { mutableStateOf(setOf<String>()) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "例文 (Sentence Mining):",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Tap word to mine",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (ttsManager != null) {
                    JapaneseSpeakerButton(
                        ttsManager = ttsManager,
                        textToSpeak = sentence,
                        size = 28.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tokenized Interactive Japanese Flow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tokens.forEach { token ->
                    val isSelected = selectedToken == token
                    val hasDictMatch = token.matchedItem != null
                    val isPunctuation = token.pos == "PUNCTUATION"

                    if (isPunctuation) {
                        Text(
                            text = token.surface,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 1.dp, vertical = 4.dp)
                        )
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                hasDictMatch -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                token.pos == "PARTICLE" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                else -> Color.Transparent
                            },
                            border = if (isSelected) {
                                BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            } else if (hasDictMatch) {
                                BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                            } else null,
                            modifier = Modifier
                                .clickable {
                                    selectedToken = if (selectedToken == token) null else token
                                }
                        ) {
                            Text(
                                text = token.surface,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (hasDictMatch) FontWeight.Bold else FontWeight.Medium,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.onPrimary
                                    hasDictMatch -> MaterialTheme.colorScheme.onPrimaryContainer
                                    token.pos == "PARTICLE" -> MaterialTheme.colorScheme.outline
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Optional Romaji
            if (romaji.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = romaji,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            // English meaning
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = englishTranslation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Token Inspection & Mining Drawer
            selectedToken?.let { token ->
                Spacer(modifier = Modifier.height(10.dp))
                TokenMiningDetailStrip(
                    token = token,
                    isMined = minedItems.contains(token.surface),
                    onMineItem = {
                        val itemToSave = token.matchedItem ?: JapaneseItem(
                            id = "mined_${System.currentTimeMillis()}",
                            japanese = token.surface,
                            reading = token.reading,
                            romaji = "",
                            meaning = "(Mined from sentence)",
                            category = com.example.noignore.japanese.model.JapaneseCategory.VOCAB,
                            jlptLevel = "Custom"
                        )
                        repository?.addCustomItem(itemToSave)
                        minedItems = minedItems + token.surface
                    }
                )
            }
        }
    }
}

@Composable
private fun TokenMiningDetailStrip(
    token: ParsedToken,
    isMined: Boolean,
    onMineItem: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = token.surface,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (token.reading.isNotBlank()) {
                        Text(
                            text = "【${token.reading}】",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = token.pos,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }

                val meaning = token.matchedItem?.meaning ?: "Segmented Japanese Morpheme"
                Text(
                    text = meaning,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isMined) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(
                    1.dp,
                    if (isMined) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.clickable(enabled = !isMined) { onMineItem() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isMined) Icons.Outlined.Check else Icons.Outlined.BookmarkAdd,
                        contentDescription = if (isMined) "Saved to Deck" else "Mine to Flashcards",
                        tint = if (isMined) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isMined) "In Deck" else "+ Mine",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isMined) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
