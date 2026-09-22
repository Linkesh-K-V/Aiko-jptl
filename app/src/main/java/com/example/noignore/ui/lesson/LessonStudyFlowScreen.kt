package com.example.noignore.ui.lesson

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.audio.JapaneseTtsManager
import com.example.noignore.japanese.lesson.JapaneseLessonRepository
import com.example.noignore.japanese.lesson.Lesson
import com.example.noignore.japanese.lesson.RecapQuestion
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton
import com.example.noignore.ui.japanese.audio.JapaneseAudioControlPill
import com.example.noignore.util.TaskHapticFeedback

enum class LessonFlowStep {
    RECAP_QUIZ,
    RECAP_SUMMARY,
    ACTIVE_LESSON_STUDY,
    LESSON_COMPLETE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonStudyFlowScreen(
    onBack: () -> Unit,
    onLessonCompleted: () -> Unit
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val repo = remember { JapaneseLessonRepository(context) }
    val ttsManager = remember { JapaneseTtsManager(context) }

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    val currentLesson = remember { repo.getCurrentLesson() }
    val previousLesson = remember { repo.getPreviousLessonForRecap() }
    val recapQuestions = remember { repo.getRecapQuestions(previousLesson) }

    var flowStep by remember { mutableStateOf(LessonFlowStep.RECAP_QUIZ) }

    // Recap State
    var currentRecapIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isQuestionSubmitted by remember { mutableStateOf(false) }
    var isCurrentCorrect by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    var failedItemsList by remember { mutableStateOf(listOf<JapaneseItem>()) }

    // Active Study State
    var activeStudyCards by remember { mutableStateOf(listOf<JapaneseItem>()) }
    var currentCardIndex by remember { mutableIntStateOf(0) }
    var isCardFlipped by remember { mutableStateOf(false) }

    val currentRecapQuestion: RecapQuestion? = recapQuestions.getOrNull(currentRecapIndex)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when (flowStep) {
                                LessonFlowStep.RECAP_QUIZ -> "🌸 Recap Test: Lesson ${previousLesson.lessonNumber}"
                                LessonFlowStep.RECAP_SUMMARY -> "🎌 Recap Results"
                                LessonFlowStep.ACTIVE_LESSON_STUDY -> "Lesson ${currentLesson.lessonNumber}: ${currentLesson.title}"
                                LessonFlowStep.LESSON_COMPLETE -> "👑 Lesson Completed"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (flowStep) {
                                LessonFlowStep.RECAP_QUIZ -> "Testing prerequisite recall before next lesson"
                                LessonFlowStep.RECAP_SUMMARY -> "Preparing your study queue"
                                LessonFlowStep.ACTIVE_LESSON_STUDY -> "Card ${currentCardIndex + 1} of ${activeStudyCards.size}"
                                LessonFlowStep.LESSON_COMPLETE -> "Great job on your progress!"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("lesson_study_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (flowStep) {
                LessonFlowStep.RECAP_QUIZ -> {
                    if (currentRecapQuestion != null) {
                        RecapQuizView(
                            question = currentRecapQuestion,
                            questionIndex = currentRecapIndex,
                            totalQuestions = recapQuestions.size,
                            selectedOption = selectedOptionIndex,
                            isSubmitted = isQuestionSubmitted,
                            isCorrect = isCurrentCorrect,
                            ttsManager = ttsManager,
                            onOptionSelected = { idx ->
                                if (!isQuestionSubmitted) {
                                    selectedOptionIndex = idx
                                    isQuestionSubmitted = true
                                    val correct = idx == currentRecapQuestion.correctIndex
                                    isCurrentCorrect = correct

                                    if (correct) {
                                        TaskHapticFeedback.performTaskCompleteHaptic(context, hapticFeedback)
                                        correctCount++
                                    } else {
                                        TaskHapticFeedback.performLightTapHaptic(context, hapticFeedback)
                                        // On-the-spot: Add failed item into the current lesson's queue!
                                        repo.addExtraFailedItemToCurrentLesson(currentRecapQuestion.targetItem)
                                        failedItemsList = failedItemsList + currentRecapQuestion.targetItem
                                    }
                                }
                            },
                            onContinueNext = {
                                if (currentRecapIndex + 1 < recapQuestions.size) {
                                    currentRecapIndex++
                                    selectedOptionIndex = null
                                    isQuestionSubmitted = false
                                    isCurrentCorrect = false
                                } else {
                                    // Move to recap summary
                                    flowStep = LessonFlowStep.RECAP_SUMMARY
                                }
                            }
                        )
                    } else {
                        flowStep = LessonFlowStep.RECAP_SUMMARY
                    }
                }

                LessonFlowStep.RECAP_SUMMARY -> {
                    RecapSummaryView(
                        totalQuestions = recapQuestions.size,
                        correctCount = correctCount,
                        failedItems = failedItemsList,
                        currentLesson = currentLesson,
                        ttsManager = ttsManager,
                        onStartNextLesson = {
                            // Merge current lesson items with any failed items added from recap
                            val extraItems = repo.getExtraItemsForCurrentLesson()
                            val combined = (currentLesson.coreItems + extraItems).distinctBy { it.id }
                            activeStudyCards = combined
                            currentCardIndex = 0
                            isCardFlipped = false
                            flowStep = LessonFlowStep.ACTIVE_LESSON_STUDY
                        }
                    )
                }

                LessonFlowStep.ACTIVE_LESSON_STUDY -> {
                    if (activeStudyCards.isNotEmpty() && currentCardIndex in activeStudyCards.indices) {
                        val card = activeStudyCards[currentCardIndex]
                        val isExtraReinforced = failedItemsList.any { it.id == card.id }

                        ActiveLessonCardView(
                            card = card,
                            cardIndex = currentCardIndex,
                            totalCards = activeStudyCards.size,
                            isFlipped = isCardFlipped,
                            isExtraReinforced = isExtraReinforced,
                            ttsManager = ttsManager,
                            onFlip = {
                                isCardFlipped = !isCardFlipped
                                TaskHapticFeedback.performLightTapHaptic(context, hapticFeedback)
                            },
                            onNextCard = {
                                if (currentCardIndex + 1 < activeStudyCards.size) {
                                    currentCardIndex++
                                    isCardFlipped = false
                                } else {
                                    // Lesson Complete!
                                    repo.completeLesson(currentLesson.id)
                                    TaskHapticFeedback.performCelebrationHaptic(context)
                                    flowStep = LessonFlowStep.LESSON_COMPLETE
                                }
                            }
                        )
                    }
                }

                LessonFlowStep.LESSON_COMPLETE -> {
                    LessonCompleteView(
                        lesson = currentLesson,
                        ttsManager = ttsManager,
                        onFinish = onLessonCompleted
                    )
                }
            }
        }
    }
}

@Composable
private fun RecapQuizView(
    question: RecapQuestion,
    questionIndex: Int,
    totalQuestions: Int,
    selectedOption: Int?,
    isSubmitted: Boolean,
    isCorrect: Boolean,
    ttsManager: JapaneseTtsManager,
    onOptionSelected: (Int) -> Unit,
    onContinueNext: () -> Unit
) {
    val progress = (questionIndex + 1).toFloat() / totalQuestions.toFloat()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress bar & label
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Recap Question ${questionIndex + 1} of $totalQuestions",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }

        // Japanese Prompt Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "PREREQUISITE RECAP",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Japanese prompt text with TTS speaker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = question.targetItem.japanese,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    JapaneseSpeakerButton(
                        textToSpeak = question.targetItem.reading.ifBlank { question.targetItem.japanese },
                        ttsManager = ttsManager,
                        modifier = Modifier.size(38.dp)
                    )
                }

                if (question.targetItem.reading.isNotBlank() && question.targetItem.reading != question.targetItem.japanese) {
                    Text(
                        text = question.targetItem.reading,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = question.prompt,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Options List
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            question.options.forEachIndexed { index, option ->
                val isSelected = selectedOption == index
                val isAnswerCorrect = index == question.correctIndex

                val containerColor = when {
                    !isSubmitted -> MaterialTheme.colorScheme.surface
                    isAnswerCorrect -> Color(0xFF2E7D32).copy(alpha = 0.15f)
                    isSelected && !isAnswerCorrect -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                    else -> MaterialTheme.colorScheme.surface
                }

                val borderColor = when {
                    !isSubmitted -> if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    isAnswerCorrect -> Color(0xFF2E7D32)
                    isSelected && !isAnswerCorrect -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = !isSubmitted) { onOptionSelected(index) }
                        .testTag("recap_option_$index"),
                    shape = RoundedCornerShape(16.dp),
                    color = containerColor,
                    border = BorderStroke(if (isSelected || (isSubmitted && isAnswerCorrect)) 2.dp else 1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected || (isSubmitted && isAnswerCorrect)) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        if (isSubmitted) {
                            if (isAnswerCorrect) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Correct",
                                    tint = Color(0xFF2E7D32)
                                )
                            } else if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Incorrect",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        // On-the-spot Teaching Card (Surfaced immediately when failed)
        AnimatedVisibility(
            visible = isSubmitted && !isCorrect,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("on_the_spot_teaching_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Teach on the spot",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "On-The-Spot Teaching • 愛子先生の解説",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Don't worry! Let's master it right now on the spot:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "➕ Added to Current Lesson queue for reinforcement!",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Key Breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        JapaneseSpeakerButton(
                            textToSpeak = question.targetItem.reading.ifBlank { question.targetItem.japanese },
                            ttsManager = ttsManager,
                            modifier = Modifier.size(34.dp),
                            testTag = "speaker_recap_teaching_${question.targetItem.id}"
                        )
                    }

                    // Example Sentence if available
                    if (question.targetItem.exampleJapanese.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = question.targetItem.exampleJapanese,
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = question.targetItem.exampleEnglish,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                JapaneseSpeakerButton(
                                    textToSpeak = question.targetItem.exampleReading.ifBlank { question.targetItem.exampleJapanese },
                                    ttsManager = ttsManager,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action button to continue
        if (isSubmitted) {
            Button(
                onClick = onContinueNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("recap_next_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (isCorrect) "Great! Next Question ➔" else "Understood! Continue Recap ➔",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun RecapSummaryView(
    totalQuestions: Int,
    correctCount: Int,
    failedItems: List<JapaneseItem>,
    currentLesson: Lesson,
    ttsManager: JapaneseTtsManager,
    onStartNextLesson: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (failedItems.isEmpty()) "🌟" else "🎌",
                fontSize = 40.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Recap Test Complete!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Prerequisite recall score: $correctCount / $totalQuestions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (failedItems.isNotEmpty()) {
                    Text(
                        text = "📝 ${failedItems.size} item(s) missed during recap were added to your current lesson queue (listen to pronunciation below):",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    failedItems.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "•", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Column {
                                        Text(
                                            text = "「${item.japanese}」 (${item.reading})",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = item.meaning,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                JapaneseSpeakerButton(
                                    textToSpeak = item.reading.ifBlank { item.japanese },
                                    ttsManager = ttsManager,
                                    modifier = Modifier.size(32.dp),
                                    testTag = "speaker_failed_recap_${item.id}"
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "✨ Flawless recall! You fully mastered previous concepts and are ready to tackle new material.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onStartNextLesson,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("start_current_lesson_button"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "🚀 Begin Lesson ${currentLesson.lessonNumber}: ${currentLesson.title}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ActiveLessonCardView(
    card: JapaneseItem,
    cardIndex: Int,
    totalCards: Int,
    isFlipped: Boolean,
    isExtraReinforced: Boolean,
    ttsManager: JapaneseTtsManager,
    onFlip: () -> Unit,
    onNextCard: () -> Unit
) {
    val progress = (cardIndex + 1).toFloat() / totalCards.toFloat()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress header
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Lesson Item ${cardIndex + 1} of $totalCards",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }

        // On-the-spot reinforcement badge if added from recap
        if (isExtraReinforced) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔁 On-The-Spot Recap Reinforcement",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Study Flashcard
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .clickable { onFlip() }
                .testTag("active_study_flashcard"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.5.dp,
                if (isExtraReinforced) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Category & Audio Pronunciation Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "${card.category.displayName} • ${card.jlptLevel}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    JapaneseAudioControlPill(
                        ttsManager = ttsManager,
                        textToSpeak = card.reading.ifBlank { card.japanese },
                        label = "Listen"
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Japanese Character / Word / Grammar (Clickable to speak!)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            ttsManager.speak(card.reading.ifBlank { card.japanese })
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = card.japanese,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔊",
                        fontSize = 20.sp
                    )
                }

                if (!isFlipped) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Tap to reveal reading, meaning & mnemonics",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    // Back of Card Content
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = card.reading,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        JapaneseSpeakerButton(
                            textToSpeak = card.reading.ifBlank { card.japanese },
                            ttsManager = ttsManager,
                            size = 32.dp,
                            testTag = "speaker_reading"
                        )
                    }

                    Text(
                        text = card.romaji,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = card.meaning,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }

                    // Mnemonic note
                    if (card.mnemonicOrNote.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💡 ${card.mnemonicOrNote}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Compound Words (with individual audio speaker buttons)
                    if (card.compounds.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "COMPOUND VOCABULARY • 熟語",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                            card.compounds.forEach { compound ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = compound.word,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = compound.reading,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            Text(
                                                text = compound.meaning,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        JapaneseSpeakerButton(
                                            ttsManager = ttsManager,
                                            textToSpeak = compound.reading.ifBlank { compound.word },
                                            size = 32.dp,
                                            testTag = "speaker_compound_${compound.word}"
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Example Sentence (with Normal and Slow audio pronunciation)
                    if (card.exampleJapanese.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "EXAMPLE IN CONTEXT • 例文",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        JapaneseSpeakerButton(
                                            textToSpeak = card.exampleReading.ifBlank { card.exampleJapanese },
                                            ttsManager = ttsManager,
                                            isSlow = true,
                                            size = 32.dp,
                                            testTag = "speaker_card_example_slow"
                                        )
                                        JapaneseSpeakerButton(
                                            textToSpeak = card.exampleReading.ifBlank { card.exampleJapanese },
                                            ttsManager = ttsManager,
                                            isSlow = false,
                                            size = 32.dp,
                                            testTag = "speaker_card_example_normal"
                                        )
                                    }
                                }
                                Text(
                                    text = card.exampleJapanese,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (card.exampleReading.isNotBlank() && card.exampleReading != card.exampleJapanese) {
                                    Text(
                                        text = card.exampleReading,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = card.exampleEnglish,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Next / Card Rating Buttons
        Button(
            onClick = onNextCard,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("lesson_card_next_button"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (cardIndex + 1 < totalCards) "Next Card ➔" else "Complete Lesson 🏆",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun LessonCompleteView(
    lesson: Lesson,
    ttsManager: JapaneseTtsManager,
    onFinish: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "👑", fontSize = 46.sp)
        }

        Text(
            text = "Lesson ${lesson.lessonNumber} Mastered!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = lesson.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Reward Earned", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "+20 Kao Coins 🪙", fontWeight = FontWeight.Bold, color = Color(0xFFD39E00))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Status", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Next Lesson Unlocked 🔓", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
        }

        // Vocabulary & Grammar Audio Pronunciation Review
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🎧", fontSize = 20.sp)
                    Column {
                        Text(
                            text = "Lesson Pronunciation Recap",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Listen to native pronunciation for all points in this lesson",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                lesson.coreItems.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                    ) {
                                        Text(
                                            text = item.category.displayName,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = item.japanese,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "(${item.reading})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    JapaneseSpeakerButton(
                                        textToSpeak = item.reading.ifBlank { item.japanese },
                                        ttsManager = ttsManager,
                                        isSlow = true,
                                        size = 32.dp,
                                        testTag = "speaker_lesson_complete_${item.id}_slow"
                                    )
                                    JapaneseSpeakerButton(
                                        textToSpeak = item.reading.ifBlank { item.japanese },
                                        ttsManager = ttsManager,
                                        isSlow = false,
                                        size = 32.dp,
                                        testTag = "speaker_lesson_complete_${item.id}_normal"
                                    )
                                }
                            }

                            Text(
                                text = item.meaning,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (item.exampleJapanese.isNotBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "例: ${item.exampleJapanese}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                        modifier = Modifier.weight(1f)
                                    )
                                    JapaneseSpeakerButton(
                                        textToSpeak = item.exampleReading.ifBlank { item.exampleJapanese },
                                        ttsManager = ttsManager,
                                        size = 28.dp,
                                        testTag = "speaker_lesson_complete_example_${item.id}"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("lesson_complete_dashboard_button"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Back to Home Dashboard",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
