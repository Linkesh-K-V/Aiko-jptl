package com.example.noignore.ui.task

import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.japanese.lesson.JapaneseLessonRepository
import com.example.noignore.japanese.model.JapaneseCategory
import com.example.noignore.ui.japanese.PracticeMode
import com.example.noignore.ui.lesson.HomeLessonDashboardCard
import com.example.noignore.ui.study.StudyTopicDrawerContent
import kotlinx.coroutines.launch
import com.example.noignore.audio.JapaneseTtsManager
import com.example.noignore.character.CharacterViewModel
import com.example.noignore.data.Task
import com.example.noignore.japanese.data.JapaneseDeckRepository
import com.example.noignore.japanese.model.JlptExamLevel
import com.example.noignore.model.RepeatMode
import com.example.noignore.ui.achievements.AchievementsViewModel
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton
import com.example.noignore.ui.reminder.DailyStudyReminderPreferenceCard
import com.example.noignore.ui.reminder.StudyReminderViewModel
import com.example.noignore.ui.study.DailyConsistencyData
import com.example.noignore.ui.study.StudyConsistencyProgressBar
import com.example.noignore.ui.streak.StreakDisplay
import com.example.noignore.ui.streak.StreakViewModel
import com.example.noignore.util.TaskHapticFeedback
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    taskViewModel: TaskViewModel,
    streakViewModel: StreakViewModel,
    characterViewModel: CharacterViewModel,
    achievementsViewModel: AchievementsViewModel,
    studyReminderViewModel: StudyReminderViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToRenshuu: () -> Unit,
    onNavigateToJlptProgress: () -> Unit = {},
    onNavigateToListening: () -> Unit = {},
    onNavigateToReading: () -> Unit = {},
    onNavigateToStories: () -> Unit = {},
    onNavigateToKanjiStudy: () -> Unit = {},
    onNavigateToLessonStudy: (Int?) -> Unit = {},
    onNavigateToCategoryPractice: (JapaneseCategory) -> Unit = {},
    onNavigateToMode: (PracticeMode) -> Unit = {},
    onNavigateToJlptTopics: (String) -> Unit = {},
    onNavigateToBackup: () -> Unit = {}
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val streakData by streakViewModel.streakData.collectAsState()

    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val ttsManager = remember { JapaneseTtsManager(context) }
    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }
    val deckRepo = remember { JapaneseDeckRepository(context) }
    val lessonRepo = remember { JapaneseLessonRepository(context) }
    val todayKotowaza = remember { deckRepo.getKotowazaOfTheDay() }
    var cardsStudiedToday by remember { mutableIntStateOf(deckRepo.getCardsStudiedToday()) }
    var targetTasks by remember { mutableIntStateOf(3) }
    var targetLessons by remember { mutableIntStateOf(15) }
    val kaoCoins = remember(tasks, cardsStudiedToday) { deckRepo.getKaoCoins() }

    LaunchedEffect(tasks) {
        cardsStudiedToday = deckRepo.getCardsStudiedToday()
    }

    val completedTasksCount = remember(tasks) { tasks.count { it.completed } }
    val consistencyData = remember(completedTasksCount, targetTasks, cardsStudiedToday, targetLessons, streakData.currentStreak) {
        DailyConsistencyData(
            completedTasks = completedTasksCount,
            targetTasks = targetTasks,
            completedLessons = cardsStudiedToday,
            targetLessons = targetLessons,
            currentStreak = streakData.currentStreak
        )
    }

    var selectedExamLevel by remember { mutableStateOf(deckRepo.getSelectedExamLevel()) }
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(Unit) {
        val updatedStreak = streakViewModel.refreshNow()
        characterViewModel.refresh(updatedStreak.currentStreak)
        achievementsViewModel.checkAndUnlock(updatedStreak.currentStreak, tasks.count { it.completed })
        achievementsViewModel.refresh()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                StudyTopicDrawerContent(
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    },
                    onNavigateToLesson = { lessonId ->
                        lessonRepo.setCurrentLessonId(lessonId)
                        onNavigateToLessonStudy(lessonId)
                    },
                    onNavigateToCategoryPractice = { cat ->
                        onNavigateToCategoryPractice(cat)
                    },
                    onNavigateToMode = { mode ->
                        onNavigateToMode(mode)
                    },
                    onNavigateToJlptTopics = { levelCode ->
                        onNavigateToJlptTopics(levelCode)
                    },
                    onNavigateToBackup = {
                        onNavigateToBackup()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            var flowerScale by remember { mutableStateOf(1f) }
                            val animatedFlowerScale by animateFloatAsState(
                                targetValue = flowerScale,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                finishedListener = { flowerScale = 1f },
                                label = "flower_scale"
                            )

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .scale(animatedFlowerScale)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .clickable {
                                        flowerScale = 1.25f
                                        TaskHapticFeedback.performLightTapHaptic(context, hapticFeedback)
                                        scope.launch { drawerState.open() }
                                    }
                                    .testTag("top_bar_flower_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🌸",
                                    fontSize = 22.sp
                                )
                            }
                            Column {
                                Text(
                                    "Aiko JLPT • 愛子先生",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    "Target: ${selectedExamLevel.title} (${selectedExamLevel.difficulty})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                actions = {
                    // JLPT Progress Tracker Action
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        IconButton(
                            onClick = onNavigateToJlptProgress,
                            modifier = Modifier.testTag("nav_jlpt_progress_button")
                        ) {
                            Text(text = "📊", fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    // Renshuu Japanese Practice Action
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        IconButton(onClick = onNavigateToRenshuu) {
                            Text(text = "⛩️", fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    // AI Assistant Action
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        IconButton(onClick = onNavigateToAI) {
                            Text(text = "✨", fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    // Achievements Action
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        IconButton(onClick = onNavigateToAchievements) {
                            Text(text = "🏆", fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    // History Action
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        IconButton(onClick = onNavigateToHistory) {
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = "History",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    // 1-Click Backup & Restore Action
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        IconButton(
                            onClick = onNavigateToBackup,
                            modifier = Modifier.testTag("top_bar_backup_button")
                        ) {
                            Text(text = "💾", fontSize = 16.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Directive")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // 1. Streak Tracker
                StreakDisplay(streakData = streakData)
            }

            item {
                // 2. STUDY DASHBOARD: Quick access to the next lesson, prerequisite recap test & topics browser
                HomeLessonDashboardCard(
                    onStartNextLesson = {
                        onNavigateToLessonStudy(null)
                    },
                    onOpenTopicDrawer = {
                        scope.launch { drawerState.open() }
                    }
                )
            }

            item {
                // 3. Daily Study Consistency Visual Progress Bar Component
                StudyConsistencyProgressBar(
                    data = consistencyData,
                    onNavigateToStudy = onNavigateToRenshuu,
                    onQuickAddLesson = {
                        repeat(3) {
                            deckRepo.recordReview("quick_study_${System.currentTimeMillis()}_$it", true)
                        }
                        cardsStudiedToday = deckRepo.getCardsStudiedToday()
                        Toast.makeText(
                            context,
                            "🎌 +3 Cards Studied! Daily Consistency updated.",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onUpdateTargets = { newTasks, newLessons ->
                        targetTasks = newTasks
                        targetLessons = newLessons
                        Toast.makeText(
                            context,
                            "Daily study goals updated: $newTasks tasks, $newLessons cards",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }

            // === EXAM CATEGORIES & TEACHER AIKO'S EXAM PREP CENTER ===
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Category Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CHOOSE YOUR JLPT EXAM (試験区分)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Surface(
                            shape = CircleShape,
                            color = Color(selectedExamLevel.colorHex).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = selectedExamLevel.code,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(selectedExamLevel.colorHex),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Exam Category Tabs (N5, N4, N3, N2, N1)
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        JlptExamLevel.entries.forEach { level ->
                            val isSelected = selectedExamLevel == level
                            val levelColor = Color(level.colorHex)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) levelColor else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) BorderStroke(1.5.dp, levelColor) else null,
                                modifier = Modifier.clickable {
                                    selectedExamLevel = level
                                    deckRepo.setSelectedExamLevel(level)
                                    Toast.makeText(context, "Preparing for ${level.title} with Teacher Aiko!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = level.code,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "• ${level.difficulty}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Teacher Aiko's Exam Prep Center Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.5.dp, Color(selectedExamLevel.colorHex).copy(alpha = 0.4f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Teacher Aiko header
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
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = "🌸", fontSize = 20.sp)
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = "Teacher Aiko (愛子先生)",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Dedicated JLPT Mentor",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(selectedExamLevel.colorHex).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = selectedExamLevel.japaneseTitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(selectedExamLevel.colorHex),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Exam Targets Quick Stats
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "~${selectedExamLevel.kanjiCount}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(selectedExamLevel.colorHex)
                                    )
                                    Text("Kanji (漢字)", style = MaterialTheme.typography.labelSmall)
                                }
                                Box(modifier = Modifier.size(1.dp, 24.dp).background(MaterialTheme.colorScheme.outlineVariant))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "~${selectedExamLevel.vocabCount}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(selectedExamLevel.colorHex)
                                    )
                                    Text("Vocab (語彙)", style = MaterialTheme.typography.labelSmall)
                                }
                                Box(modifier = Modifier.size(1.dp, 24.dp).background(MaterialTheme.colorScheme.outlineVariant))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "~${selectedExamLevel.grammarPoints}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(selectedExamLevel.colorHex)
                                    )
                                    Text("Grammar (文法)", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Teacher Aiko's Advice Card
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("💡", fontSize = 16.sp)
                                    Column {
                                        Text(
                                            text = "Teacher Aiko's Strategy for ${selectedExamLevel.code}:",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = selectedExamLevel.teacherAikoAdvice,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Key Topics
                            Text(
                                text = "CORE ${selectedExamLevel.code} TOPICS:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                selectedExamLevel.keyTopics.forEach { topic ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = topic,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Explore All Topics in this Level
                            OutlinedButton(
                                onClick = {
                                    onNavigateToJlptTopics(selectedExamLevel.code)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("explore_level_topics_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.2.dp, Color(selectedExamLevel.colorHex).copy(alpha = 0.7f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(selectedExamLevel.colorHex)
                                )
                            ) {
                                Text(
                                    text = "🔍 Explore All ${selectedExamLevel.code} Topics & Explanations",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Launch Exam Prep Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onNavigateToRenshuu,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(selectedExamLevel.colorHex)
                                    )
                                ) {
                                    Text("📚 Practice ${selectedExamLevel.code}", fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = onNavigateToAI,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                ) {
                                    Text("✨ Ask Aiko")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = onNavigateToJlptProgress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("open_jlpt_tracker_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.2.dp, Color(selectedExamLevel.colorHex).copy(alpha = 0.6f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("📊", fontSize = 15.sp)
                                    Text(
                                        "Track ${selectedExamLevel.code} Categories & Progress (Room)",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(selectedExamLevel.colorHex)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Teacher Aiko's 1-Tap Exam Directives
                            Text(
                                text = "TEACHER AIKO'S RECOMMENDED STUDY DIRECTIVES:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                selectedExamLevel.defaultPresetDirectives.forEach { directiveTitle ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable {
                                            val timeMillis = System.currentTimeMillis() + (15 * 60 * 1000)
                                            taskViewModel.addTask(
                                                title = "[${selectedExamLevel.code}] $directiveTitle",
                                                timeMillis = timeMillis,
                                                confirmDelayMinutes = 5,
                                                repeatMode = RepeatMode.DAILY
                                            )
                                            Toast.makeText(context, "Added to Daily Plan: $directiveTitle", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Text(
                                            text = "+ $directiveTitle",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                DailyStudyReminderPreferenceCard(
                    viewModel = studyReminderViewModel,
                    accentColor = Color(selectedExamLevel.colorHex)
                )
            }

            item {
                // Kotowaza of the Day (Wisdom of Continuous Effort)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🌸", fontSize = 18.sp)
                            Text(
                                text = "TODAY'S KOTOWAZA (日本語のことわざ)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = todayKotowaza.kanji,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${todayKotowaza.hiragana} (${todayKotowaza.romaji})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            JapaneseSpeakerButton(
                                ttsManager = ttsManager,
                                textToSpeak = "${todayKotowaza.kanji}。${todayKotowaza.hiragana}",
                                size = 38.dp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "“${todayKotowaza.english}”",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = todayKotowaza.lesson,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            item {
                // Renshuu Japanese Study Hub Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("⛩️", fontSize = 20.sp)
                                Text(
                                    text = "RENSHUU JAPANESE PRACTICE",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.sp
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "🪙 $kaoCoins Coins",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Daily Study Target",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$cardsStudiedToday / 15 Cards",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (cardsStudiedToday / 15f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onNavigateToRenshuu,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("⛩️ Open Renshuu Hub (Kanji • JLPT Exam • SRS)", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Navigation row for Kanji, Listening, Reading, and Stories
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateToKanjiStudy,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("🈁 Kanji", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onNavigateToListening,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("🎧 Listen", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onNavigateToReading,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("📖 Read", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onNavigateToStories,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("📚 Stories", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "1-TAP STUDY DIRECTIVES (SCHEDULE WITH ALARM):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "🈁 ${selectedExamLevel.code} Kanji Study" to 15L,
                                "🎯 ${selectedExamLevel.code} Mock Exam" to 20L,
                                "📖 ${selectedExamLevel.code} Vocab Review" to 15L,
                                "⛩️ Particle & Grammar Drill" to 10L,
                                "⚡ 2-Min Micro Sprint" to 5L
                            ).forEach { (title, delay) ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable {
                                        val timeMillis = System.currentTimeMillis() + (delay * 60 * 1000)
                                        taskViewModel.addTask(
                                            title = title,
                                            timeMillis = timeMillis,
                                            confirmDelayMinutes = 5,
                                            repeatMode = RepeatMode.ONCE
                                        )
                                        Toast.makeText(context, "Scheduled: $title in $delay min!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text(
                                        text = "+ $title",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STUDY DIRECTIVES (学習計画)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${tasks.count { !it.completed }} Active",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (tasks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "⛩️", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No active study directives",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Discipline starts with scheduling your daily Japanese session. Tap + or quick study presets to lock in your study reminder.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onComplete = {
                            taskViewModel.markCompleted(task) {
                                scope.launch {
                                    val updated = streakViewModel.refreshNow()
                                    characterViewModel.refresh(updated.currentStreak)
                                    val totalDone = tasks.count { it.completed } + 1
                                    achievementsViewModel.checkAndUnlock(updated.currentStreak, totalDone)
                                    achievementsViewModel.refresh()
                                }
                            }
                        },
                        onClick = { onNavigateToEdit(task.id.toLong()) },
                        onDelete = { taskViewModel.deleteTask(task) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onComplete: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val timeFormat = SimpleDateFormat("EEE, hh:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(task.timeMillis))

    var isCelebrated by remember { mutableStateOf(false) }
    val checkmarkScale by animateFloatAsState(
        targetValue = if (isCelebrated) 1.25f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { isCelebrated = false },
        label = "task_complete_spring_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.completed) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (task.completed) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Checkbox / Complete button with satisfying haptic feedback & spring animation
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .scale(checkmarkScale)
                    .clip(CircleShape)
                    .background(
                        if (task.completed) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable {
                        if (!task.completed) {
                            isCelebrated = true
                            TaskHapticFeedback.performTaskCompleteHaptic(context, hapticFeedback)
                        } else {
                            TaskHapticFeedback.performLightTapHaptic(context, hapticFeedback)
                        }
                        onComplete()
                    }
                    .testTag("task_complete_button_${task.id}"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (task.completed) Icons.Default.Check else Icons.Outlined.CheckCircle,
                    contentDescription = if (task.completed) "Task completed" else "Mark task complete",
                    tint = if (task.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Task info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.completed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f) else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (task.repeatMode != RepeatMode.ONCE) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = task.repeatMode.name.lowercase().replaceFirstChar { it.uppercase() },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${task.confirmDelayMinutes}m verify",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
