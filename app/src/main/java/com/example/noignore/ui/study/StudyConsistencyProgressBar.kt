package com.example.noignore.ui.study

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun StudyConsistencyProgressBar(
    data: DailyConsistencyData,
    modifier: Modifier = Modifier,
    onNavigateToStudy: () -> Unit = {},
    onQuickAddLesson: () -> Unit = {},
    onUpdateTargets: (targetTasks: Int, targetLessons: Int) -> Unit = { _, _ -> }
) {
    var showTargetDialog by remember { mutableStateOf(false) }
    var showCelebrationDialog by remember { mutableStateOf(false) }
    var hasTriggeredCelebration by remember { mutableStateOf(false) }

    LaunchedEffect(data.percentage) {
        if (data.percentage >= 100 && !hasTriggeredCelebration) {
            hasTriggeredCelebration = true
            showCelebrationDialog = true
        } else if (data.percentage < 100) {
            hasTriggeredCelebration = false
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = data.overallProgress,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessLow
        ),
        label = "overall_study_progress"
    )

    val tierColor = Color(data.tier.badgeColorHex)
    val animatedTierColor by animateColorAsState(
        targetValue = tierColor,
        animationSpec = tween(400),
        label = "tier_color_anim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_study_progress_bar_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = animatedTierColor.copy(alpha = 0.45f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Row: Title, Tier Badge & Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = animatedTierColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = data.tier.emoji,
                                fontSize = 22.sp
                            )
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "DAILY CONSISTENCY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.1.sp
                            )
                            Text(
                                text = "毎日の習慣",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Normal
                            )
                        }
                        Text(
                            text = "${data.tier.title} (${data.tier.japaneseTitle})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = animatedTierColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, animatedTierColor.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "${(animatedProgress * 100).roundToInt()}%",
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("daily_consistency_percentage_text"),
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleSmall,
                            color = animatedTierColor
                        )
                    }

                    IconButton(
                        onClick = { showTargetDialog = true },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("study_targets_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = "Adjust Daily Study Targets",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Visual Gradient Progress Bar with Milestones
            MainVisualProgressBar(
                progress = animatedProgress,
                tierColor = animatedTierColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Milestone Checkpoints Row
            MilestoneMarkersRow(
                progress = animatedProgress,
                accentColor = animatedTierColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dual Sub-Progress Bars: Tasks vs Lessons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Study Tasks / Directives Sub-Bar
                SubProgressMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Directives",
                    subtitle = "${data.completedTasks}/${data.targetTasks} Done",
                    percentage = (data.taskProgress * 100).toInt(),
                    progress = data.taskProgress,
                    barColor = Color(0xFF303F9F),
                    icon = "📋",
                    testTag = "tasks_progress_subcard"
                )

                // 2. Study Lessons / Flashcards Sub-Bar
                SubProgressMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Lessons",
                    subtitle = "${data.completedLessons}/${data.targetLessons} Cards",
                    percentage = (data.lessonProgress * 100).toInt(),
                    progress = data.lessonProgress,
                    barColor = Color(0xFFC2185B),
                    icon = "⛩️",
                    testTag = "lessons_progress_subcard"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Teacher Aiko's Consistency Feedback Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = animatedTierColor.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, animatedTierColor.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("aiko_consistency_feedback_banner")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🌸", fontSize = 16.sp)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Sensei Aiko's Feedback",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (data.currentStreak > 0) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFF9800).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "🔥 ${data.currentStreak}d Streak",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = data.aikoFeedback,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 100% Daily Goal Lottie Celebration Banner
            AnimatedVisibility(
                visible = data.percentage >= 100,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    CelebrationGoalBanner(
                        onReplayCelebration = {
                            showCelebrationDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // Action Buttons: Quick Lesson (+3) & Open Hub
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onQuickAddLesson,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_study_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, animatedTierColor.copy(alpha = 0.5f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("⚡", fontSize = 14.sp)
                        Text(
                            text = "Quick Study (+3)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = animatedTierColor
                        )
                    }
                }

                Button(
                    onClick = onNavigateToStudy,
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("practice_now_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = animatedTierColor
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("⛩️", fontSize = 14.sp)
                        Text(
                            text = "Study Lessons",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    if (showTargetDialog) {
        DailyTargetSettingsDialog(
            currentTargetTasks = data.targetTasks,
            currentTargetLessons = data.targetLessons,
            onDismiss = { showTargetDialog = false },
            onConfirm = { newTasks, newLessons ->
                onUpdateTargets(newTasks, newLessons)
                showTargetDialog = false
            }
        )
    }

    if (showCelebrationDialog) {
        Goal100CelebrationDialog(
            data = data,
            onDismiss = { showCelebrationDialog = false },
            onContinue = { showCelebrationDialog = false }
        )
    }
}

/**
 * Main Visual Progress Bar featuring layered gradients, rounded pill caps,
 * and illuminated milestone markers.
 */
@Composable
private fun MainVisualProgressBar(
    progress: Float,
    tierColor: Color,
    modifier: Modifier = Modifier
) {
    val gradientColors = remember(tierColor) {
        listOf(
            Color(0xFFC2185B), // Sakura Pink
            Color(0xFFFF5252), // Coral Flame
            Color(0xFFFFB300), // Golden Momentum
            tierColor          // Current Tier Accent
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .border(
                BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                CircleShape
            )
            .testTag("daily_study_progress_track")
    ) {
        val totalWidth = maxWidth

        // Animated Fill
        Box(
            modifier = Modifier
                .width(totalWidth * progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(gradientColors)
                )
                .testTag("daily_study_progress_fill")
        )

        // Subtle Shimmer / Milestone Ticks on Track
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(0.25f, 0.50f, 0.75f, 1.0f).forEach { mark ->
                val isReached = progress >= mark
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isReached) Color.White.copy(alpha = 0.9f)
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
                        )
                )
            }
        }
    }
}

/**
 * Visual Milestone Markers showing four stages of daily consistency:
 * 25% (🌱 Start), 50% (⚡ Steady), 75% (🔥 Momentum), 100% (👑 Mastery)
 */
@Composable
private fun MilestoneMarkersRow(
    progress: Float,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val milestones = listOf(
        Triple(25, "🌱", "Start"),
        Triple(50, "⚡", "Halfway"),
        Triple(75, "🔥", "Focus"),
        Triple(100, "👑", "Mastery")
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        milestones.forEach { (pct, emoji, label) ->
            val isPassed = (progress * 100) >= pct
            val markerColor by animateColorAsState(
                targetValue = if (isPassed) accentColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                label = "milestone_color_$pct"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isPassed) accentColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(
                        1.dp,
                        if (isPassed) accentColor else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isPassed && pct == 100) {
                            Text(text = "👑", fontSize = 12.sp)
                        } else if (isPassed) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "$label Reached",
                                tint = accentColor,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Text(text = emoji, fontSize = 10.sp)
                        }
                    }
                }

                Text(
                    text = "$pct%",
                    fontSize = 10.sp,
                    fontWeight = if (isPassed) FontWeight.Bold else FontWeight.Normal,
                    color = markerColor
                )

                Text(
                    text = label,
                    fontSize = 9.sp,
                    color = if (isPassed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Sub-Progress metric card for Task Directives and Lesson Flashcards
 */
@Composable
private fun SubProgressMetricCard(
    title: String,
    subtitle: String,
    percentage: Int,
    progress: Float,
    barColor: Color,
    icon: String,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessLow
        ),
        label = "sub_metric_progress"
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier.testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = icon, fontSize = 14.sp)
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = barColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = barColor,
                trackColor = barColor.copy(alpha = 0.15f)
            )
        }
    }
}

/**
 * Settings Dialog allowing user to configure their daily targets
 * (e.g. 1-10 tasks, 5-50 lessons).
 */
@Composable
private fun DailyTargetSettingsDialog(
    currentTargetTasks: Int,
    currentTargetLessons: Int,
    onDismiss: () -> Unit,
    onConfirm: (targetTasks: Int, targetLessons: Int) -> Unit
) {
    var selectedTasks by remember { mutableFloatStateOf(currentTargetTasks.toFloat()) }
    var selectedLessons by remember { mutableFloatStateOf(currentTargetLessons.toFloat()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "⚙️", fontSize = 20.sp)
                Text(
                    text = "Daily Consistency Targets",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Adjust your daily study goals. The visual consistency bar updates dynamically based on these targets.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 1. Target Tasks Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Directives Target:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedTasks.roundToInt()} Tasks",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = selectedTasks,
                        onValueChange = { selectedTasks = it },
                        valueRange = 1f..10f,
                        steps = 8
                    )
                }

                // 2. Target Lessons Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Lesson Cards Target:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedLessons.roundToInt()} Cards",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = selectedLessons,
                        onValueChange = { selectedLessons = it },
                        valueRange = 5f..50f,
                        steps = 8
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedTasks.roundToInt(), selectedLessons.roundToInt())
                }
            ) {
                Text("Save Targets")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Celebratory 100% banner embedded in the progress bar card when daily goal is reached,
 * featuring a continuous mini celebration Lottie animation.
 */
@Composable
fun CelebrationGoalBanner(
    onReplayCelebration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.celebration_goal))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFF8E1),
        border = BorderStroke(1.5.dp, Color(0xFFFFB300)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("celebration_goal_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("celebration_lottie_mini"),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "100% GOAL ACHIEVED!",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "完全達成",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF57C00)
                        )
                    }
                    Text(
                        text = "Congratulations! Daily consistency unlocked.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = Color(0xFF5D4037)
                    )
                }
            }

            Button(
                onClick = onReplayCelebration,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8F00)
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("celebrate_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🎉", fontSize = 14.sp)
                    Text(
                        text = "Celebrate",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Celebration Dialog featuring Lottie Animation celebrating 100% goal completion.
 */
@Composable
fun Goal100CelebrationDialog(
    data: DailyConsistencyData,
    onDismiss: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentAnimationIndex by remember { mutableIntStateOf(0) }
    // Alternate between celebration particles and Aiko's proud animation
    val currentRawRes = if (currentAnimationIndex % 2 == 0) R.raw.celebration_goal else R.raw.char_proud
    val dynamicComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(currentRawRes))
    val dynamicProgress by animateLottieCompositionAsState(
        composition = dynamicComposition,
        iterations = LottieConstants.IterateForever
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("celebration_dialog"),
        shape = RoundedCornerShape(24.dp),
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFFD54F).copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, Color(0xFFFFB300)),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "👑", fontSize = 14.sp)
                        Text(
                            text = "DAILY MASTERY UNLOCKED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE65100)
                        )
                    }
                }

                // Center Lottie Animation
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .testTag("celebration_lottie_animation"),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = dynamicComposition,
                        progress = { dynamicProgress },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Congratulatory Titles
                Text(
                    text = "100% Daily Goal Complete!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Text(
                    text = "完全達成 • Excellent Work!",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC2185B),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stats summary chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "📋 Tasks", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${data.completedTasks}/${data.targetTasks}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "⛩️ Cards", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${data.completedLessons}/${data.targetLessons}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC2185B)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🔥 Streak", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${data.currentStreak}d",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sensei Aiko celebratory remark
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9),
                    border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🌸", fontSize = 20.sp)
                        Text(
                            text = "“Subarashii! Continuous effort is the secret to fluency. I am proud of your dedication today!”",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1B5E20),
                            lineHeight = 16.sp,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("celebration_continue_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Text(
                    text = "Subarashii! (Awesome)",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = { currentAnimationIndex++ },
                    modifier = Modifier.testTag("replay_celebration_button")
                ) {
                    Text(
                        text = if (currentAnimationIndex % 2 == 0) "Switch to Sensei Aiko 🥋" else "Switch to Confetti Burst 🎉",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}
