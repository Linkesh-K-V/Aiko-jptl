package com.example.noignore.ui.onboarding

import android.content.Context
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class BeginnerStartingLevel {
    ABSOLUTE_BEGINNER,
    SOME_KANA,
    ADVANCED_EXPLORER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeginnerOnboardingScreen(
    onFinishOnboarding: (startWithLesson1: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("aiko_jlpt_user_prefs", Context.MODE_PRIVATE)
    }

    var step by remember { mutableIntStateOf(0) }
    var selectedLevel by remember { mutableStateOf(BeginnerStartingLevel.ABSOLUTE_BEGINNER) }
    var selectedDailyMinutes by remember { mutableIntStateOf(15) }

    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    fun completeAndProceed() {
        prefs.edit()
            .putBoolean("onboarding_completed", true)
            .putString("learner_starting_level", selectedLevel.name)
            .putInt("daily_target_minutes", selectedDailyMinutes)
            .apply()

        // Synchronize lesson repository with selected learning track
        val lessonRepo = com.example.noignore.japanese.lesson.JapaneseLessonRepository(context)
        when (selectedLevel) {
            BeginnerStartingLevel.ABSOLUTE_BEGINNER -> {
                lessonRepo.resetLessonProgress()
            }
            BeginnerStartingLevel.SOME_KANA -> {
                lessonRepo.markLessonsCompleted((1..6).toList())
                lessonRepo.setCurrentLessonId(7)
            }
            BeginnerStartingLevel.ADVANCED_EXPLORER -> {
                lessonRepo.markLessonsCompleted((1..13).toList())
                lessonRepo.setCurrentLessonId(14)
            }
        }

        // Initialize persistent learner profile in Room
        coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val db = com.example.noignore.data.AppDatabase.getInstance(context)
                com.example.noignore.japanese.engine.LearnerModelEngine.initializeStartingProfile(
                    db = db,
                    startingLevel = selectedLevel,
                    dailyTargetMinutes = selectedDailyMinutes
                )
            } catch (e: Exception) {
                android.util.Log.e("Onboarding", "Error setting starting learner profile: ${e.message}")
            }
        }

        val startLesson1 = selectedLevel == BeginnerStartingLevel.ABSOLUTE_BEGINNER
        onFinishOnboarding(startLesson1)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Aiko JLPT Orientation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    if (step > 0) {
                        IconButton(onClick = { step-- }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Step")
                        }
                    }
                },
                actions = {
                    Text(
                        text = "Step ${step + 1} of 3",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Step Progress Bar
            LinearProgressIndicator(
                progress = { (step + 1) / 3f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboarding_step_transition"
            ) { targetStep ->
                when (targetStep) {
                    0 -> OnboardingWelcomeStep(onNext = { step = 1 })
                    1 -> OnboardingPathSelectionStep(
                        selectedLevel = selectedLevel,
                        onSelectLevel = { selectedLevel = it },
                        onNext = { step = 2 }
                    )
                    2 -> OnboardingDailyGoalStep(
                        selectedMinutes = selectedDailyMinutes,
                        onSelectMinutes = { selectedDailyMinutes = it },
                        startingLevel = selectedLevel,
                        onFinish = { completeAndProceed() }
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingWelcomeStep(
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Teacher Aiko Avatar Card
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "🌸", fontSize = 42.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome to Aiko JLPT!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Designed for zero-knowledge beginners. You can start with zero Japanese and follow a clear, friendly path all the way to JLPT fluency.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Three Pillars
        PillarCard(
            emoji = "🎌",
            title = "1. Kana First Approach",
            description = "Start with Hiragana & Katakana fundamentals with native audio, clear stroke order, and mnemonics before tackling kanji."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PillarCard(
            emoji = "🧠",
            title = "2. FSRS Spaced Repetition",
            description = "Smart mathematical scheduling reviews words and grammar right before memory decay, optimizing every minute of study."
        )

        Spacer(modifier = Modifier.height(12.dp))

        PillarCard(
            emoji = "📖",
            title = "3. Complete JLPT N5 Curriculum",
            description = "Structured progressive syllabus containing core vocabulary, grammar particles, reading passages, and listening audio."
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("onboarding_welcome_next_button"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Choose Your Starting Path", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
private fun OnboardingPathSelectionStep(
    selectedLevel: BeginnerStartingLevel,
    onSelectLevel: (BeginnerStartingLevel) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Where should we begin?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Be honest about your starting point — you can change or review any lesson at any time.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        LevelOptionCard(
            title = "🌱 Absolute Beginner (Zero Japanese)",
            tag = "RECOMMENDED FOR FIRST-TIMERS",
            description = "I do not know Hiragana yet. Teach me the 46 basic characters starting from Lesson 1: あ・い・う・え・お.",
            isSelected = selectedLevel == BeginnerStartingLevel.ABSOLUTE_BEGINNER,
            onClick = { onSelectLevel(BeginnerStartingLevel.ABSOLUTE_BEGINNER) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        LevelOptionCard(
            title = "⚡ I Know Kana (Ready for N5)",
            tag = "FAST-TRACK",
            description = "I can already read Hiragana and Katakana smoothly. Take me straight into N5 core vocabulary, kanji, and grammar.",
            isSelected = selectedLevel == BeginnerStartingLevel.SOME_KANA,
            onClick = { onSelectLevel(BeginnerStartingLevel.SOME_KANA) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        LevelOptionCard(
            title = "🎯 Custom JLPT Explorer",
            tag = "FULL ACCESS",
            description = "I want full access to all JLPT levels (N5 through N1), mock tests, and the comprehensive practice center.",
            isSelected = selectedLevel == BeginnerStartingLevel.ADVANCED_EXPLORER,
            onClick = { onSelectLevel(BeginnerStartingLevel.ADVANCED_EXPLORER) }
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("onboarding_path_next_button"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Continue to Daily Rhythm", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
private fun OnboardingDailyGoalStep(
    selectedMinutes: Int,
    onSelectMinutes: (Int) -> Unit,
    startingLevel: BeginnerStartingLevel,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set Your Daily Study Goal",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Consistency is the key to language acquisition. Even 5 minutes daily produces compounding results.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        DailyGoalCard(
            minutes = 5,
            title = "⚡ 5 Minutes / Day",
            subtitle = "Micro-sprint Habit",
            description = "Ideal for busy schedules. Quick daily flashcard reviews and habit preservation.",
            isSelected = selectedMinutes == 5,
            onClick = { onSelectMinutes(5) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        DailyGoalCard(
            minutes = 15,
            title = "🎯 15 Minutes / Day (Recommended)",
            subtitle = "Optimal Steady Growth",
            description = "Clear your daily FSRS review queue plus 1 new lesson unit or reading exercise.",
            isSelected = selectedMinutes == 15,
            onClick = { onSelectMinutes(15) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        DailyGoalCard(
            minutes = 30,
            title = "🔥 30 Minutes / Day",
            subtitle = "Accelerated Fluency",
            description = "In-depth kanji stroke practice, grammar drills, listening lessons, and speed quizzes.",
            isSelected = selectedMinutes == 30,
            onClick = { onSelectMinutes(30) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Motivational Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "🌸", fontSize = 24.sp)
                Column {
                    Text(
                        text = "Teacher Aiko's Promise:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "「継続は力なり (Continuity is power). I will be here every day to celebrate your progress!」",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("onboarding_finish_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            val buttonText = if (startingLevel == BeginnerStartingLevel.ABSOLUTE_BEGINNER) {
                "Start Lesson 1: Hiragana Basics ➔"
            } else {
                "Enter Aiko JLPT Dashboard ➔"
            }
            Text(buttonText, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun PillarCard(
    emoji: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun LevelOptionCard(
    title: String,
    tag: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("level_option_${title.take(10)}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun DailyGoalCard(
    minutes: Int,
    title: String,
    subtitle: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("daily_goal_${minutes}m"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(22.dp)
                        .padding(start = 8.dp)
                )
            }
        }
    }
}
