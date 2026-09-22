package com.example.noignore.ui.japanese

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.noignore.japanese.data.StudyMaterialEnrichmentRepository
import com.example.noignore.ui.japanese.study.KanjiMnemonicCard
import com.example.noignore.ui.japanese.study.WordCollocationsCard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.audio.JapaneseTtsManager
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.japanese.util.JapaneseRomajiHelper
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KanjiDeepStudyCard(
    item: JapaneseItem,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onReview: (Boolean) -> Unit,
    ttsManager: JapaneseTtsManager? = null
) {
    var showWritingPad by remember(item.id) { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFlip() },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Level badge, Stroke count, and Mastery indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "JLPT ${item.jlptLevel}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    if (item.strokeCount > 0) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${item.strokeCount} 画",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Mastery Status Badge
                Surface(
                    shape = CircleShape,
                    color = when (item.mastery) {
                        3 -> Color(0xFFE8F5E9)
                        1, 2 -> Color(0xFFFFF8E1)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    border = BorderStroke(
                        1.dp,
                        when (item.mastery) {
                            3 -> Color(0xFF81C784)
                            1, 2 -> Color(0xFFFFD54F)
                            else -> Color.Transparent
                        }
                    )
                ) {
                    Text(
                        text = when (item.mastery) {
                            3 -> "⭐ Mastered"
                            2 -> "📖 Familiar"
                            1 -> "🌱 Learning"
                            else -> "🆕 New"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (item.mastery) {
                            3 -> Color(0xFF2E7D32)
                            1, 2 -> Color(0xFFF57F17)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Large Kanji Display with 2x2 Calligraphy box & Speaker Icon
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier.size(130.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxSize(),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = item.japanese,
                            fontSize = 78.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                JapaneseSpeakerButton(
                    ttsManager = ttsManager,
                    textToSpeak = item.japanese,
                    size = 36.dp,
                    modifier = Modifier.padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Romaji Pronunciation Badge (Always visible for clarity!)
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Romaji: ${item.romaji}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Core Meaning
            Text(
                text = item.meaning,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            // Radical (部首) Breakdown
            if (item.radical.isNotBlank()) {
                val radRom = if (item.radicalRomaji.isNotBlank()) "(${item.radicalRomaji})" else ""
                val radMean = if (item.radicalMeaning.isNotBlank()) "— ${item.radicalMeaning}" else ""
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Radical (部首): ${item.radical} $radRom $radMean",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Toggle between Breakdown & Writing Practice Pad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (!showWritingPad) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier.clickable { showWritingPad = false }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (!showWritingPad) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Breakdown",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!showWritingPad) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (showWritingPad) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier.clickable { showWritingPad = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (showWritingPad) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "✍️ Practice Pad",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (showWritingPad) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (showWritingPad) {
                // Interactive Stroke Order & Finger Drawing Pad (Kanji Study mode)
                KanjiWritingPracticePad(
                    item = item,
                    ttsManager = ttsManager
                )
            } else if (!isFlipped) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "Tap Card to Reveal On'yomi, Kun'yomi & Compounds",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Detailed Breakdown (On'yomi with Romaji, Kun'yomi with Romaji, Compounds with Romaji)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // On'yomi & Kun'yomi Cards with Romaji
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // On'yomi (音読み)
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "音読み (ON)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.onyomi.ifBlank { "—" },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val onRom = if (item.onyomiRomaji.isNotBlank()) item.onyomiRomaji else JapaneseRomajiHelper.toRomaji(item.onyomi)
                                    if (onRom.isNotBlank()) {
                                        Text(
                                            text = onRom,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                if (item.onyomi.isNotBlank()) {
                                    JapaneseSpeakerButton(
                                        ttsManager = ttsManager,
                                        textToSpeak = item.onyomi,
                                        size = 28.dp
                                    )
                                }
                            }
                        }

                        // Kun'yomi (訓読み)
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "訓読み (KUN)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.kunyomi.ifBlank { item.reading },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val kunRom = if (item.kunyomiRomaji.isNotBlank()) item.kunyomiRomaji else JapaneseRomajiHelper.toRomaji(item.kunyomi.replace("・", "").replace("-", ""))
                                    if (kunRom.isNotBlank()) {
                                        Text(
                                            text = kunRom,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                val kunText = item.kunyomi.ifBlank { item.reading }
                                if (kunText.isNotBlank()) {
                                    JapaneseSpeakerButton(
                                        ttsManager = ttsManager,
                                        textToSpeak = kunText,
                                        size = 28.dp
                                    )
                                }
                            }
                        }
                    }

                    // Kanji Compounds (熟語 - Jukugo) with Romaji
                    if (item.compounds.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("📚", fontSize = 14.sp)
                                Text(
                                    text = "COMPOUND WORDS (熟語) WITH ROMAJI:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item.compounds.forEach { c ->
                                    val compRom = if (c.romaji.isNotBlank()) c.romaji else JapaneseRomajiHelper.toRomaji(c.reading)
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Column {
                                                Row(
                                                    verticalAlignment = Alignment.Bottom,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(
                                                        text = c.word,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = c.reading,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                                if (compRom.isNotBlank()) {
                                                    Text(
                                                        text = compRom,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.secondary,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                                Text(
                                                    text = c.meaning,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            JapaneseSpeakerButton(
                                                ttsManager = ttsManager,
                                                textToSpeak = c.word,
                                                size = 24.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Radical & Mnemonic Deconstruction (Wanikani / Heisig Style)
                    val mnemonicBreakdown = remember(item.japanese) {
                        StudyMaterialEnrichmentRepository.getMnemonicForKanji(item.japanese)
                    }
                    if (mnemonicBreakdown != null) {
                        KanjiMnemonicCard(
                            breakdown = mnemonicBreakdown,
                            ttsManager = ttsManager
                        )
                    } else if (item.mnemonicOrNote.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Lightbulb,
                                    contentDescription = "Mnemonic",
                                    tint = Color(0xFFFFA000),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = item.mnemonicOrNote,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Word Collocations & Pitch Accent
                    val collocations = remember(item.japanese) {
                        StudyMaterialEnrichmentRepository.getCollocationsForWord(item.japanese)
                    }
                    if (collocations.isNotEmpty()) {
                        WordCollocationsCard(
                            collocations = collocations,
                            ttsManager = ttsManager
                        )
                    }

                    // Example Sentence with Japanese, Romaji & English
                    if (item.exampleJapanese.isNotBlank()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "例文 (Example Sentence):",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                JapaneseSpeakerButton(
                                    ttsManager = ttsManager,
                                    textToSpeak = item.exampleJapanese,
                                    size = 26.dp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.exampleJapanese,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (item.exampleRomaji.isNotBlank()) {
                                Text(
                                    text = item.exampleRomaji,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = item.exampleEnglish,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // SRS Response Controls (Review Later vs Mastered with Kao coins)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = { onReview(false) },
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
        ) {
            Icon(Icons.Default.Close, contentDescription = "Hard")
            Spacer(modifier = Modifier.width(6.dp))
            Text("Review Later", fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = { onReview(true) },
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Check, contentDescription = "Mastered")
            Spacer(modifier = Modifier.width(6.dp))
            Text("Mastered! (+5🪙)", fontWeight = FontWeight.Bold)
        }
    }
}
