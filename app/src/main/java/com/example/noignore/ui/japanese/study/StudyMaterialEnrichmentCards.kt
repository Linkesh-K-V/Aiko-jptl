package com.example.noignore.ui.japanese.study

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
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
import com.example.noignore.japanese.model.GrammarFormBreakdown
import com.example.noignore.japanese.model.KanjiMnemonicBreakdown
import com.example.noignore.japanese.model.WordCollocation
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton

/**
 * Wanikani / Heisig-style Mnemonic & Radical Deconstruction Card.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KanjiMnemonicCard(
    breakdown: KanjiMnemonicBreakdown,
    ttsManager: JapaneseTtsManager?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1) // Warm golden parchment tone
        ),
        border = BorderStroke(1.5.dp, Color(0xFFFFD54F))
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
                    Text("🧩", fontSize = 16.sp)
                    Text(
                        text = "Radical & Mnemonic Deconstruction",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE65100)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFFECB3)
                ) {
                    Text(
                        text = "Wanikani Method",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBF360C),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Radical Components Chips
            Text(
                text = "FOUNDATIONAL RADICALS:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF795548)
            )
            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                breakdown.components.forEach { comp ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFFFE082))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = comp.radical,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFE65100)
                            )
                            Column {
                                Text(
                                    text = comp.meaning,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3E2723)
                                )
                                Text(
                                    text = "${comp.name} • ${comp.position}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = Color(0xFF8D6E63)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mnemonic Story Block
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White.copy(alpha = 0.8f),
                border = BorderStroke(1.dp, Color(0xFFFFE082).copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("💡", fontSize = 13.sp)
                        Text(
                            text = "MEMORY STORY (覚え方):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFE65100)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = breakdown.mnemonicStory,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF37474F),
                        lineHeight = 18.sp
                    )
                }
            }

            // Confusable Lookalikes Warning
            if (breakdown.confusableLookalikes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFEBEE),
                    border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Confusable",
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "WATCH OUT FOR LOOKALIKES:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFC62828)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            breakdown.confusableLookalikes.forEach { lookalike ->
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFEF9A9A))
                                ) {
                                    Text(
                                        text = lookalike,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB71C1C),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        if (breakdown.confusableNote.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = breakdown.confusableNote,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = Color(0xFFB71C1C)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Natural Lexical Collocations & Pitch Accent Card.
 */
@Composable
fun WordCollocationsCard(
    collocations: List<WordCollocation>,
    ttsManager: JapaneseTtsManager?,
    modifier: Modifier = Modifier
) {
    if (collocations.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9) // Fresh green accent
        ),
        border = BorderStroke(1.5.dp, Color(0xFFA5D6A7))
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
                    Text("🌿", fontSize = 16.sp)
                    Text(
                        text = "Native Collocations & Pitch Accents",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF2E7D32)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFC8E6C9)
                ) {
                    Text(
                        text = "Natural Phrasing",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                collocations.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFC8E6C9)),
                        modifier = Modifier.fillMaxWidth()
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
                                    text = item.phrase,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = "${item.reading} (${item.romaji})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF388E3C)
                                )
                                Text(
                                    text = item.englishMeaning,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF455A64)
                                )
                                if (item.pitchAccentPattern.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "🎵 Pitch: ${item.pitchAccentPattern}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF00796B)
                                    )
                                }
                            }

                            JapaneseSpeakerButton(
                                ttsManager = ttsManager,
                                textToSpeak = item.phrase,
                                size = 32.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Bunpro-Style Grammar Anatomy Card:
 * Explicit formation formula, politeness register, nuance difference, and common traps.
 */
@Composable
fun BunproGrammarAnatomyCard(
    breakdown: GrammarFormBreakdown,
    ttsManager: JapaneseTtsManager?,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEDE7F6) // Elegant lavender tone
        ),
        border = BorderStroke(1.5.dp, Color(0xFFD1C4E9))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("⛩️", fontSize = 16.sp)
                    Text(
                        text = "Bunpro Grammar Breakdown",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF512DA8)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFD1C4E9)
                ) {
                    Text(
                        text = breakdown.politenessRegister,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF311B92),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    // Formation Formula Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFB39DDB)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "📐 FORMATION FORMULA (接続):",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF512DA8)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = breakdown.formationFormula,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = Color(0xFF263238),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Nuance Notes
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFB39DDB)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Nuance",
                                    tint = Color(0xFF5E35B1),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "NUANCE & CONTEXT:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF5E35B1)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = breakdown.nuanceNotes,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF37474F),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Common Pitfalls to Avoid
                    if (breakdown.commonMistakesToAvoid.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFF3E0),
                            border = BorderStroke(1.dp, Color(0xFFFFCC80)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warning",
                                        tint = Color(0xFFE65100),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "COMMON TRAPS & PITFALLS:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFE65100)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = breakdown.commonMistakesToAvoid,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF4E342E),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Real-World Example
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFB39DDB)),
                        modifier = Modifier.fillMaxWidth()
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
                                    text = "NATURAL EXAMPLE:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF512DA8)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = breakdown.realWorldExample,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF263238)
                                )
                                Text(
                                    text = breakdown.realWorldReading,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF5E35B1)
                                )
                                Text(
                                    text = breakdown.realWorldEnglish,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF546E7A)
                                )
                            }

                            JapaneseSpeakerButton(
                                ttsManager = ttsManager,
                                textToSpeak = breakdown.realWorldExample,
                                size = 32.dp
                            )
                        }
                    }
                }
            }
        }
    }
}
