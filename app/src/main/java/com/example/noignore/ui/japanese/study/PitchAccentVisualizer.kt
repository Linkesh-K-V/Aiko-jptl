package com.example.noignore.ui.japanese.study

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
import com.example.noignore.japanese.engine.PitchAccentEngine
import com.example.noignore.japanese.engine.PitchPattern

@Composable
fun PitchAccentVisualizer(
    word: String,
    reading: String,
    modifier: Modifier = Modifier
) {
    val info = PitchAccentEngine.analyzePitchAccent(word, reading)
    val morae = PitchAccentEngine.splitIntoMorae(reading)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tokyo Pitch Accent: ${info.pattern.labelJp}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = CircleShape,
                    color = when (info.pattern) {
                        PitchPattern.HEIBAN -> Color(0xFFE8F5E9)
                        PitchPattern.ATAMADAKA -> Color(0xFFFFEBEE)
                        PitchPattern.NAKADAKA -> Color(0xFFE3F2FD)
                        PitchPattern.ODAKA -> Color(0xFFFFF3E0)
                    }
                ) {
                    Text(
                        text = info.pattern.labelEn,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (info.pattern) {
                            PitchPattern.HEIBAN -> Color(0xFF2E7D32)
                            PitchPattern.ATAMADAKA -> Color(0xFFC62828)
                            PitchPattern.NAKADAKA -> Color(0xFF1565C0)
                            PitchPattern.ODAKA -> Color(0xFFE65100)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visual High-Low Mora Step Graph
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                morae.forEachIndexed { index, mora ->
                    val isHigh = if (index < info.pitchGraph.size) info.pitchGraph[index] else false

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(36.dp)
                    ) {
                        // High/Low dot and bar indicator
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .fillMaxWidth(),
                            contentAlignment = if (isHigh) Alignment.TopCenter else Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        color = if (isHigh) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                        shape = CircleShape
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Mora kana
                        Text(
                            text = mora,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Tone label (H/L)
                        Text(
                            text = if (isHigh) "高 (H)" else "低 (L)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isHigh) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = info.pattern.ruleDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
