package com.example.noignore.ui.japanese.study

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.audio.JapaneseTtsManager
import com.example.noignore.japanese.data.StudyMaterialEnrichmentRepository
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.japanese.model.SrsCardData
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton

/**
 * Anki Leech Remediation Card.
 * Triggered whenever a card suffers 3+ lapses ("Again" clicks).
 * Teacher Aiko intervenes with mnemonics, confusable lookalike contrasts,
 * and collocations so the student resolves the cognitive roadblock.
 */
@Composable
fun LeechRemediationCard(
    item: JapaneseItem,
    srsData: SrsCardData,
    ttsManager: JapaneseTtsManager?,
    onDrillCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mnemonic = StudyMaterialEnrichmentRepository.getMnemonicForKanji(item.japanese)
    val collocations = StudyMaterialEnrichmentRepository.getCollocationsForWord(item.japanese)
    val grammar = StudyMaterialEnrichmentRepository.getGrammarBreakdown(item.id)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1) // Alert amber background
        ),
        border = BorderStroke(2.dp, Color(0xFFFFB300))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                    Text("🐛", fontSize = 18.sp)
                    Text(
                        text = "Anki Leech Detected!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE65100)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFFCC80)
                ) {
                    Text(
                        text = "${srsData.lapses} Memory Lapses",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFBF360C),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Teacher Aiko noticed you missed this card ${srsData.lapses} times in spaced repetition. Let's rebuild your cognitive hook so you never forget it again!",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF5D4037)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // The problematic item
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFFFE082)),
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
                        Text(
                            text = item.japanese,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "${item.reading} (${item.romaji})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8D6E63)
                        )
                        Text(
                            text = item.meaning,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF37474F)
                        )
                    }

                    JapaneseSpeakerButton(
                        ttsManager = ttsManager,
                        textToSpeak = item.japanese,
                        size = 36.dp
                    )
                }
            }

            // Mnemonic breakdown if available
            if (mnemonic != null) {
                Spacer(modifier = Modifier.height(10.dp))
                KanjiMnemonicCard(
                    breakdown = mnemonic,
                    ttsManager = ttsManager
                )
            } else if (grammar != null) {
                Spacer(modifier = Modifier.height(10.dp))
                BunproGrammarAnatomyCard(
                    breakdown = grammar,
                    ttsManager = ttsManager
                )
            } else if (collocations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                WordCollocationsCard(
                    collocations = collocations,
                    ttsManager = ttsManager
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onDrillCard,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE65100)
                )
            ) {
                Text("Study This Leech Now", fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}
