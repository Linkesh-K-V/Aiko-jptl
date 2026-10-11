package com.example.noignore.ui.japanese.srs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.japanese.model.SrsCardData
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun EbbinghausMemoryCurveView(
    srsData: SrsCardData?,
    modifier: Modifier = Modifier
) {
    val cardData = srsData ?: SrsCardData(itemId = "")
    val retention = cardData.calculateMemoryRetention()
    val retentionPct = (retention * 100).roundToInt()
    val stability = max(1.0, cardData.intervalDays.toDouble() * (cardData.easeFactor / 2.5))

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                    Text("🧠", fontSize = 18.sp)
                    Column {
                        Text(
                            text = "Human Brain Memory Consolidation",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "FSRS-4.5 DSR Model & Synaptic Consolidation",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = when {
                        retentionPct >= 85 -> Color(0xFFE8F5E9)
                        retentionPct >= 65 -> Color(0xFFFFF3E0)
                        else -> Color(0xFFFFEBEE)
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            retentionPct >= 85 -> Color(0xFF4CAF50)
                            retentionPct >= 65 -> Color(0xFFFF9800)
                            else -> Color(0xFFE57373)
                        }
                    )
                ) {
                    Text(
                        text = "$retentionPct% Retrievability",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = when {
                            retentionPct >= 85 -> Color(0xFF2E7D32)
                            retentionPct >= 65 -> Color(0xFFE65100)
                            else -> Color(0xFFC62828)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Curve Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            ) {
                val w = size.width
                val h = size.height

                // Base grid / axes
                drawLine(
                    color = Color.Gray.copy(alpha = 0.25f),
                    start = Offset(0f, h),
                    end = Offset(w, h),
                    strokeWidth = 2f
                )
                drawLine(
                    color = Color.Gray.copy(alpha = 0.25f),
                    start = Offset(0f, 0f),
                    end = Offset(0f, h),
                    strokeWidth = 2f
                )

                // 100% and 50% dotted guideline
                val midY = h * 0.5f
                drawLine(
                    color = Color.Gray.copy(alpha = 0.18f),
                    start = Offset(0f, midY),
                    end = Offset(w, midY),
                    strokeWidth = 1f
                )

                // Draw exponential forgetting curve: R(t) = e^(-t / S)
                val curvePath = Path()
                val steps = 50
                for (i in 0..steps) {
                    val tFraction = i.toFloat() / steps.toFloat() // 0 to 1 representing time
                    val tDays = tFraction * 7.0 // span 7 days
                    val r = exp(-tDays / stability).coerceIn(0.1, 1.0).toFloat()
                    val x = tFraction * w
                    val y = h - (r * (h - 8f))

                    if (i == 0) {
                        curvePath.moveTo(x, y)
                    } else {
                        curvePath.lineTo(x, y)
                    }
                }

                drawPath(
                    path = curvePath,
                    color = Color(0xFF3F51B5),
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                )

                // Draw Current Retention Marker
                val currentMarkerY = h - (retention * (h - 8f))
                val markerX = (w * 0.15f).coerceAtMost(w * 0.5f)
                drawCircle(
                    color = Color(0xFFE91E63),
                    radius = 5.dp.toPx(),
                    center = Offset(markerX, currentMarkerY)
                )
            }

            // Explanatory Neuro-Science Strip
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrainMetricItem(label = "Memory Stability", value = "${String.format("%.1f", stability)} days")
                    BrainMetricItem(label = "Ease Factor", value = "${String.format("%.2f", cardData.easeFactor)}")
                    BrainMetricItem(label = "Repetitions", value = "${cardData.repetition}x")
                    BrainMetricItem(label = "Synapse Tier", value = cardData.state.displayName)
                }
            }

            Text(
                text = "⚡ Cognitive Mechanism: Human working memory fades exponentially without spaced testing. Tapping 'Again' resets short-term memory, while 'Good' and 'Easy' flatten the decay curve to cement knowledge in long-term neocortex storage.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun BrainMetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
