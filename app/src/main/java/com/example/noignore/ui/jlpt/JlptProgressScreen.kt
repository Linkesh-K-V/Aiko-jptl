package com.example.noignore.ui.jlpt

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.data.jlpt.JlptProgressEntity
import com.example.noignore.japanese.model.JlptExamLevel
import com.example.noignore.ui.reminder.DailyStudyReminderPreferenceCard
import com.example.noignore.ui.reminder.StudyReminderViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JlptProgressScreen(
    viewModel: JlptProgressViewModel,
    studyReminderViewModel: StudyReminderViewModel,
    onBack: () -> Unit,
    onNavigateToStudy: () -> Unit = {},
    onNavigateToSources: () -> Unit = {}
) {
    val context = LocalContext.current
    val targetExam by viewModel.targetExam.collectAsState()
    val selectedViewLevelCode by viewModel.selectedViewLevel.collectAsState()
    val currentLevelProgress by viewModel.currentLevelProgress.collectAsState()
    val allProgress by viewModel.allProgress.collectAsState()

    val currentTargetCode = targetExam?.targetLevel ?: "N5"
    val isViewingTarget = selectedViewLevelCode.equals(currentTargetCode, ignoreCase = true)

    val currentLevelEnum = remember(selectedViewLevelCode) {
        JlptExamLevel.fromCode(selectedViewLevelCode)
    }
    val levelColor = remember(currentLevelEnum) {
        Color(currentLevelEnum.colorHex)
    }

    // State for editing a specific category progress
    var editingCategory by remember { mutableStateOf<JlptProgressEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "JLPT Progress Tracker",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Room-Synced N5–N1 Exam Categories",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("jlpt_progress_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToSources,
                        modifier = Modifier.testTag("jlpt_progress_sources_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Curriculum Sources & Licenses",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color(currentLevelEnum.colorHex).copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Target: $currentTargetCode",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(currentLevelEnum.colorHex)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // 1. Level Toggle Selector (N5, N4, N3, N2, N1)
                Text(
                    text = "SELECT JLPT EXAM LEVEL TO VIEW & TRACK:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    JlptExamLevel.entries.forEach { lvl ->
                        val isSelected = selectedViewLevelCode == lvl.code
                        val isTarget = currentTargetCode == lvl.code
                        val chipColor = Color(lvl.colorHex)

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) chipColor else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) chipColor else if (isTarget) chipColor.copy(alpha = 0.5f) else Color.Transparent
                            ),
                            modifier = Modifier
                                .testTag("toggle_level_${lvl.code}")
                                .clickable {
                                    viewModel.selectViewLevel(lvl.code)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isTarget) {
                                    Text("⭐", fontSize = 12.sp)
                                }
                                Text(
                                    text = lvl.code,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "• ${lvl.difficulty.take(4)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 2. Active Target Toggle Bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isViewingTarget) {
                            levelColor.copy(alpha = 0.10f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        }
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isViewingTarget) levelColor.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isViewingTarget) "Active Goal Target" else "Viewing ${currentLevelEnum.code}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isViewingTarget) levelColor else MaterialTheme.colorScheme.onSurface
                                )
                                if (isViewingTarget) {
                                    Surface(
                                        shape = CircleShape,
                                        color = levelColor,
                                        modifier = Modifier.size(6.dp)
                                    ) {}
                                }
                            }
                            Text(
                                text = if (isViewingTarget) {
                                    "Your daily habits and study drills focus on ${currentLevelEnum.title}."
                                } else {
                                    "Toggle this level as your primary target exam."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (!isViewingTarget) {
                            Button(
                                onClick = {
                                    viewModel.setTargetExamLevel(selectedViewLevelCode)
                                    Toast.makeText(
                                        context,
                                        "Target set to ${currentLevelEnum.title} in Room!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = levelColor),
                                modifier = Modifier.testTag("set_target_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Set Target",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text("Set as Target", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = levelColor.copy(alpha = 0.2f),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Active Target",
                                        tint = levelColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Primary Goal",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = levelColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Hero Level Readiness Summary Card
            item {
                val totalItems = currentLevelProgress.sumOf { it.totalItems }.coerceAtLeast(1)
                val totalCompleted = currentLevelProgress.sumOf { it.completedItems }
                val overallPercent = ((totalCompleted.toFloat() / totalItems.toFloat()) * 100).toInt().coerceIn(0, 100)

                val animatedProgress by animateFloatAsState(
                    targetValue = overallPercent / 100f,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                    label = "overall_readiness_progress"
                )

                val readinessStatus = when {
                    overallPercent >= 85 -> "🏆 Exam Ready"
                    overallPercent >= 50 -> "🔥 Strong Mastery"
                    overallPercent >= 20 -> "⚡ Active Progress"
                    else -> "🌱 Foundations Phase"
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.5.dp, levelColor.copy(alpha = 0.35f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${currentLevelEnum.title} • ${currentLevelEnum.japaneseTitle}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Difficulty: ${currentLevelEnum.difficulty}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = levelColor
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = levelColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = readinessStatus,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = levelColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large Progress Display
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "$overallPercent%",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 34.sp,
                                    color = levelColor
                                )
                                Text(
                                    text = "$totalCompleted of $totalItems items logged",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Daily Goal Minutes quick adjuster
                            val dailyMinutes = targetExam?.dailyGoalMinutes ?: 30
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("⏱️", fontSize = 12.sp)
                                    Text(
                                        text = "$dailyMinutes min/day",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = levelColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Teacher Aiko's Strategy for this level
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("🌸", fontSize = 16.sp)
                                Column {
                                    Text(
                                        text = "Teacher Aiko's ${currentLevelEnum.code} Strategy:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = currentLevelEnum.teacherAikoAdvice,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Daily Study Reminder Preference (DataStore)
            item {
                DailyStudyReminderPreferenceCard(
                    viewModel = studyReminderViewModel,
                    accentColor = levelColor
                )
            }

            // 4. Category Breakdown Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currentLevelEnum.code} CATEGORIES (ROOM PERSISTENCE):",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${currentLevelProgress.size} categories",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 5. Individual Category Progress Cards
            items(currentLevelProgress, key = { "${it.level}_${it.category}" }) { itemProgress ->
                CategoryProgressCard(
                    progress = itemProgress,
                    accentColor = levelColor,
                    onIncrement = { delta ->
                        viewModel.incrementProgress(itemProgress.level, itemProgress.category, delta)
                        Toast.makeText(
                            context,
                            "+$delta to ${itemProgress.categoryTitle}!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onEdit = {
                        editingCategory = itemProgress
                    }
                )
            }

            // 6. Roadmap Comparison Across All JLPT Levels (N5 to N1)
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "JLPT FULL ROADMAP (N5 → N1):",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        JlptExamLevel.entries.forEach { lvl ->
                            val lvlItems = allProgress.filter { it.level.equals(lvl.code, ignoreCase = true) }
                            val total = lvlItems.sumOf { it.totalItems }.coerceAtLeast(1)
                            val completed = lvlItems.sumOf { it.completedItems }
                            val pct = ((completed.toFloat() / total.toFloat()) * 100).toInt().coerceIn(0, 100)
                            val isCurrentTarget = currentTargetCode == lvl.code
                            val lvlColor = Color(lvl.colorHex)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectViewLevel(lvl.code) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = lvlColor,
                                    modifier = Modifier.size(36.dp, 28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = lvl.code,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = lvl.difficulty,
                                                fontWeight = FontWeight.SemiBold,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            if (isCurrentTarget) {
                                                Text("⭐ Target", fontSize = 10.sp, color = lvlColor, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(
                                            text = "$pct%",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = lvlColor
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { pct / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = lvlColor,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Spacing
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Edit Category Count Dialog
    editingCategory?.let { categoryToEdit ->
        var inputCount by remember { mutableStateOf(categoryToEdit.completedItems.toString()) }
        var isError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { editingCategory = null },
            title = {
                Text(
                    text = "Update ${categoryToEdit.categoryTitle}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter your current completed count for ${categoryToEdit.level} (Total target: ${categoryToEdit.totalItems}):",
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = inputCount,
                        onValueChange = {
                            inputCount = it
                            isError = it.toIntOrNull() == null
                        },
                        label = { Text("Completed Items") },
                        isError = isError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_category_count_input")
                    )

                    if (isError) {
                        Text(
                            text = "Please enter a valid number between 0 and ${categoryToEdit.totalItems}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // Quick presets
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(10, 25, 50).forEach { presetDelta ->
                            OutlinedButton(
                                onClick = {
                                    val current = inputCount.toIntOrNull() ?: categoryToEdit.completedItems
                                    val updated = (current + presetDelta).coerceAtMost(categoryToEdit.totalItems)
                                    inputCount = updated.toString()
                                    isError = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+$presetDelta", fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = inputCount.toIntOrNull()
                        if (parsed != null && parsed in 0..categoryToEdit.totalItems) {
                            viewModel.updateCompletedCount(categoryToEdit.level, categoryToEdit.category, parsed)
                            Toast.makeText(context, "Saved to Room: $parsed items!", Toast.LENGTH_SHORT).show()
                            editingCategory = null
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier.testTag("save_category_count_button")
                ) {
                    Text("Save Progress")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCategory = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CategoryProgressCard(
    progress: JlptProgressEntity,
    accentColor: Color,
    onIncrement: (Int) -> Unit,
    onEdit: () -> Unit
) {
    val pct = if (progress.totalItems > 0) {
        ((progress.completedItems.toFloat() / progress.totalItems.toFloat()) * 100).toInt().coerceIn(0, 100)
    } else 0

    val animatedPct by animateFloatAsState(
        targetValue = pct / 100f,
        animationSpec = tween(durationMillis = 400),
        label = "category_bar_${progress.category}"
    )

    val icon = when (progress.category) {
        "KANJI" -> "🈁"
        "VOCABULARY" -> "📖"
        "GRAMMAR" -> "⛩️"
        "READING" -> "📜"
        "MOCK_EXAMS" -> "🎯"
        else -> "📚"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("category_card_${progress.category}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
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
                        color = accentColor.copy(alpha = 0.12f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = icon, fontSize = 16.sp)
                        }
                    }

                    Column {
                        Text(
                            text = progress.categoryTitle,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "${progress.completedItems} / ${progress.totalItems} items (${pct}%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("edit_button_${progress.category}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit count",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { animatedPct },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = accentColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick increment buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (progress.accuracyRate > 0) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "${progress.accuracyRate}% accuracy",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = { onIncrement(1) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("increment_1_${progress.category}")
                ) {
                    Text("+1", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = { onIncrement(5) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("increment_5_${progress.category}")
                ) {
                    Text("+5 Study", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
