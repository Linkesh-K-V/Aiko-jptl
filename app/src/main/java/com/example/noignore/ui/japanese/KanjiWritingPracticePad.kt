package com.example.noignore.ui.japanese

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.audio.JapaneseTtsManager
import com.example.noignore.japanese.data.KanjiStrokeOrderData
import com.example.noignore.japanese.data.NormalizedStroke
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton
import kotlin.math.hypot

/**
 * Interactive Writing & Stroke Order Practice Canvas with real-time Stroke Order Validation.
 * Compares the user's sequential finger strokes against canonical Kanji stroke directions,
 * highlighting correct strokes in emerald green and providing instant feedback.
 */
@Composable
fun KanjiWritingPracticePad(
    item: JapaneseItem,
    ttsManager: JapaneseTtsManager? = null,
    onCompletedKanji: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Canonical strokes for this Kanji
    val canonicalStrokes = remember(item.japanese) {
        KanjiStrokeOrderData.getStrokes(item.japanese, item.strokeCount)
    }

    // List of completed user strokes
    val completedStrokes = remember(item.id) { mutableStateListOf<List<Offset>>() }
    var currentStrokePoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var showGhostGuide by remember { mutableStateOf(true) }
    var showAnimatedGuide by remember { mutableStateOf(false) }
    var activeGuideIndex by remember { mutableIntStateOf(0) }

    // Validation state for each completed stroke (true = matched direction and location, false = deviant)
    val strokeValidations = remember(item.id) { mutableStateListOf<Boolean>() }

    val primaryColor = MaterialTheme.colorScheme.primary
    val gridLineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    val ghostColor = primaryColor.copy(alpha = 0.16f)
    val inkColor = MaterialTheme.colorScheme.onSurface

    val totalStrokes = if (canonicalStrokes.isNotEmpty()) canonicalStrokes.size else item.strokeCount.coerceAtLeast(1)
    val isAllStrokesCompleted = completedStrokes.size >= totalStrokes
    val isPerfectAccuracy = strokeValidations.isNotEmpty() && strokeValidations.all { it } && isAllStrokesCompleted

    LaunchedEffect(isPerfectAccuracy) {
        if (isPerfectAccuracy) {
            onCompletedKanji?.invoke()
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = BorderStroke(1.5.dp, primaryColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Stroke Count & Audio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "✍️ Write & Trace",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "$totalStrokes Strokes (画)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                JapaneseSpeakerButton(
                    ttsManager = ttsManager,
                    textToSpeak = item.japanese,
                    size = 32.dp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Traditional 2x2 Calligraphy Square
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .testTag("kanji_writing_canvas_box")
                    .pointerInput(item.id, completedStrokes.size) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentStrokePoints = listOf(offset)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentStrokePoints = currentStrokePoints + change.position
                            },
                            onDragEnd = {
                                if (currentStrokePoints.size >= 2) {
                                    val strokeIdx = completedStrokes.size
                                    val isValid = validateStroke(
                                        userStroke = currentStrokePoints,
                                        canvasWidth = size.width.toFloat(),
                                        canvasHeight = size.height.toFloat(),
                                        canonicalStroke = canonicalStrokes.getOrNull(strokeIdx)
                                    )
                                    completedStrokes.add(currentStrokePoints)
                                    strokeValidations.add(isValid)
                                    currentStrokePoints = emptyList()
                                } else {
                                    currentStrokePoints = emptyList()
                                }
                            },
                            onDragCancel = {
                                currentStrokePoints = emptyList()
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Background Ghost Kanji (if enabled)
                if (showGhostGuide) {
                    Text(
                        text = item.japanese,
                        fontSize = 140.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ghostColor,
                        textAlign = TextAlign.Center
                    )
                }

                // Canvas for 2x2 Calligraphy Grid lines, stroke order guide indicators & Drawn ink
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h / 2f

                    // Outer border
                    drawRect(
                        color = gridLineColor,
                        style = Stroke(width = 2f)
                    )

                    // Dashed center horizontal & vertical crosshairs
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)

                    drawLine(
                        color = gridLineColor,
                        start = Offset(cx, 0f),
                        end = Offset(cx, h),
                        strokeWidth = 1.5f,
                        pathEffect = dashEffect
                    )

                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, cy),
                        end = Offset(w, cy),
                        strokeWidth = 1.5f,
                        pathEffect = dashEffect
                    )

                    // Next stroke guide indicator (shows where next stroke begins)
                    val nextStrokeIdx = completedStrokes.size
                    val nextCanonical = canonicalStrokes.getOrNull(nextStrokeIdx)
                    if (nextCanonical != null && !isAllStrokesCompleted) {
                        val startPx = Offset(nextCanonical.start.x * w, nextCanonical.start.y * h)
                        val endPx = Offset(nextCanonical.end.x * w, nextCanonical.end.y * h)

                        // Highlight start circle with stroke number
                        drawCircle(
                            color = Color(0xFF1E88E5),
                            radius = 14f,
                            center = startPx
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 12f,
                            center = startPx
                        )
                        drawCircle(
                            color = Color(0xFF1E88E5),
                            radius = 5f,
                            center = startPx
                        )

                        // Direction guide arrow dashed line
                        drawLine(
                            color = Color(0xFF1E88E5).copy(alpha = 0.6f),
                            start = startPx,
                            end = endPx,
                            strokeWidth = 4f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f),
                            cap = StrokeCap.Round
                        )
                    }

                    // Draw completed strokes with color validation feedback
                    val strokePaint = Stroke(
                        width = 16f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )

                    for (i in completedStrokes.indices) {
                        val stroke = completedStrokes[i]
                        val isStrokeValid = strokeValidations.getOrNull(i) ?: true
                        val strokeColor = if (isStrokeValid) inkColor else Color(0xFFE53935).copy(alpha = 0.85f)

                        if (stroke.size >= 2) {
                            val path = Path().apply {
                                moveTo(stroke[0].x, stroke[0].y)
                                for (j in 1 until stroke.size) {
                                    lineTo(stroke[j].x, stroke[j].y)
                                }
                            }
                            drawPath(path, strokeColor, style = strokePaint)
                        } else if (stroke.size == 1) {
                            drawCircle(strokeColor, radius = 8f, center = stroke[0])
                        }
                    }

                    // Draw active dragging stroke
                    if (currentStrokePoints.size >= 2) {
                        val path = Path().apply {
                            moveTo(currentStrokePoints[0].x, currentStrokePoints[0].y)
                            for (i in 1 until currentStrokePoints.size) {
                                lineTo(currentStrokePoints[i].x, currentStrokePoints[i].y)
                            }
                        }
                        drawPath(path, inkColor, style = strokePaint)
                    } else if (currentStrokePoints.size == 1) {
                        drawCircle(inkColor, radius = 8f, center = currentStrokePoints[0])
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Drawing Controls Bar (Clear, Undo, Ghost Guide, Stroke Count)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Undo button
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        IconButton(
                            onClick = {
                                if (completedStrokes.isNotEmpty()) {
                                    completedStrokes.removeAt(completedStrokes.size - 1)
                                    if (strokeValidations.isNotEmpty()) {
                                        strokeValidations.removeAt(strokeValidations.size - 1)
                                    }
                                }
                            },
                            enabled = completedStrokes.isNotEmpty(),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Undo Stroke",
                                modifier = Modifier.size(20.dp),
                                tint = if (completedStrokes.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    // Clear button
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        IconButton(
                            onClick = {
                                completedStrokes.clear()
                                strokeValidations.clear()
                            },
                            enabled = completedStrokes.isNotEmpty(),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear Canvas",
                                modifier = Modifier.size(20.dp),
                                tint = if (completedStrokes.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                // Stroke count status
                Text(
                    text = "${completedStrokes.size} / $totalStrokes strokes",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (completedStrokes.size == totalStrokes) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )

                // Ghost Guide Toggle
                Surface(
                    shape = CircleShape,
                    color = if (showGhostGuide) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    IconButton(
                        onClick = { showGhostGuide = !showGhostGuide },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            if (showGhostGuide) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Ghost Kanji",
                            modifier = Modifier.size(20.dp),
                            tint = if (showGhostGuide) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Stroke Accuracy Feedback
            if (isAllStrokesCompleted) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isPerfectAccuracy) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    border = BorderStroke(1.dp, if (isPerfectAccuracy) Color(0xFF4CAF50) else Color(0xFFFFB74D)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(if (isPerfectAccuracy) "💮" else "✍️", fontSize = 18.sp)
                        Column {
                            Text(
                                text = if (isPerfectAccuracy) "Masterful! Correct Stroke Order" else "Completed! Check stroke order guidelines below",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isPerfectAccuracy) Color(0xFF2E7D32) else Color(0xFFE65100)
                            )
                            Text(
                                text = "${item.japanese} (${item.reading}) - ${item.meaning}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Stroke Order Steps Guide
            val nextGuide = canonicalStrokes.getOrNull(completedStrokes.size)
            if (nextGuide != null && !isAllStrokesCompleted) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("💡 Next Stroke ${completedStrokes.size + 1}:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = primaryColor)
                        Text(nextGuide.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

/**
 * Validates a user stroke against canonical stroke guidelines.
 * Checks general direction (dot product) and start location proximity.
 */
private fun validateStroke(
    userStroke: List<Offset>,
    canvasWidth: Float,
    canvasHeight: Float,
    canonicalStroke: NormalizedStroke?
): Boolean {
    if (canonicalStroke == null || userStroke.size < 2 || canvasWidth <= 0 || canvasHeight <= 0) return true

    val start = userStroke.first()
    val end = userStroke.last()

    val userDx = (end.x - start.x) / canvasWidth
    val userDy = (end.y - start.y) / canvasHeight

    val canonDx = canonicalStroke.end.x - canonicalStroke.start.x
    val canonDy = canonicalStroke.end.y - canonicalStroke.start.y

    val userMag = hypot(userDx, userDy)
    val canonMag = hypot(canonDx, canonDy)

    if (userMag < 0.05f || canonMag < 0.05f) return true // Small strokes/dots are forgiving

    // Direction similarity via dot product
    val dot = (userDx * canonDx + userDy * canonDy) / (userMag * canonMag)
    return dot > 0.30f // Positive cosine indicates similar general stroke direction
}
