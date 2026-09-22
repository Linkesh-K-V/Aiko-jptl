package com.example.noignore.ui.japanese

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.audio.JapaneseTtsManager
import com.example.noignore.japanese.model.AnkiRating
import com.example.noignore.japanese.model.JapaneseCategory
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.japanese.model.JapaneseStory
import com.example.noignore.japanese.model.SrsCardData
import com.example.noignore.japanese.model.SrsDeckSummary
import com.example.noignore.japanese.data.InteractivePracticeRepository
import com.example.noignore.japanese.data.StudyMaterialEnrichmentRepository
import com.example.noignore.ui.japanese.audio.JapaneseShadowingCard
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton
import com.example.noignore.ui.japanese.srs.AnkiSrsRatingBar
import com.example.noignore.ui.japanese.srs.EbbinghausMemoryCurveView
import com.example.noignore.ui.japanese.study.BunproGrammarAnatomyCard
import com.example.noignore.ui.japanese.study.JlptStarScrambleCard
import com.example.noignore.ui.japanese.study.KanjiMnemonicCard
import com.example.noignore.ui.japanese.study.LeechRemediationCard
import com.example.noignore.ui.japanese.study.ParticleDrillCard
import com.example.noignore.ui.japanese.study.SurvivalRoleplayView
import com.example.noignore.ui.japanese.study.WordCollocationsCard
import com.example.noignore.ui.japanese.study.ConjugationDrillCard
import com.example.noignore.ui.japanese.study.DailyMissionCard
import com.example.noignore.ui.japanese.study.PitchAccentVisualizer
import com.example.noignore.ui.japanese.study.FuriganaText
import com.example.noignore.japanese.engine.DailyStudyPlanner
import com.example.noignore.japanese.engine.DailyMissionPlan

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RenshuuPracticeScreen(
    viewModel: RenshuuPracticeViewModel,
    aiViewModel: com.example.noignore.ui.ai.AIAssistantViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val ttsManager = remember { JapaneseTtsManager(context) }
    var selectedStory by remember { mutableStateOf<JapaneseStory?>(null) }
    var showGroundingDialog by remember { mutableStateOf(false) }
    var showLeechRemediationDialog by remember { mutableStateOf(false) }
    var activeStarQuestionIndex by remember { mutableStateOf(0) }
    var activeParticleQuestionIndex by remember { mutableStateOf(0) }

    val jlptGroundingResult by aiViewModel.jlptGroundingResult.collectAsState()
    val aiIsLoading by aiViewModel.isLoading.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    val cards by viewModel.cards.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val isFlipped by viewModel.isFlipped.collectAsState()
    val practiceMode by viewModel.practiceMode.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedJlptLevel by viewModel.selectedJlptLevel.collectAsState()
    val quizState by viewModel.quizState.collectAsState()
    val kaoCoins by viewModel.kaoCoins.collectAsState()
    val cardsStudiedToday by viewModel.cardsStudiedToday.collectAsState()
    val sessionCompleted by viewModel.sessionCompleted.collectAsState()
    val sessionCoinsEarned by viewModel.sessionCoinsEarned.collectAsState()

    // Anki Spaced Repetition State
    val srsDeckSummary by viewModel.srsDeckSummary.collectAsState()
    val onlyDueCards by viewModel.onlyDueCards.collectAsState()
    val relearningCount by viewModel.relearningCount.collectAsState()
    val currentCardSrs by viewModel.currentCardSrs.collectAsState()
    val furiganaMode by viewModel.furiganaMode.collectAsState()

    // Handle dedicated full-screen practice sub-modes
    if (practiceMode == PracticeMode.LISTENING_LESSON) {
        ListeningLessonScreen(
            ttsManager = ttsManager,
            onBack = { viewModel.setMode(PracticeMode.FLASHCARDS) }
        )
        return
    }

    if (practiceMode == PracticeMode.READING_LESSON) {
        ReadingLessonScreen(
            ttsManager = ttsManager,
            onBack = { viewModel.setMode(PracticeMode.FLASHCARDS) }
        )
        return
    }

    if (practiceMode == PracticeMode.KANJI_EXPLORER) {
        KanjiStudyExplorerScreen(
            kanjiList = viewModel.getAllKanjiList(),
            kaoCoins = kaoCoins,
            ttsManager = ttsManager,
            onUpdateMastery = { id, mastery -> viewModel.setItemMastery(id, mastery) },
            onBack = { viewModel.setMode(PracticeMode.FLASHCARDS) }
        )
        return
    }

    if (practiceMode == PracticeMode.STORIES) {
        val currentStory = selectedStory
        if (currentStory != null) {
            JapaneseStoryReaderScreen(
                story = currentStory,
                ttsManager = ttsManager,
                onBack = { selectedStory = null }
            )
        } else {
            JapaneseStoriesCatalogScreen(
                onSelectStory = { selectedStory = it },
                onBack = { viewModel.setMode(PracticeMode.FLASHCARDS) }
            )
        }
        return
    }

    val currentCard = if (cards.isNotEmpty() && currentIndex < cards.size) cards[currentIndex] else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Renshuu Study",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "日本語",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                navigationIcon = {
                    Surface(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(38.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    // Furigana Mode Toggle Button
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.clickable { viewModel.cycleFuriganaMode() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = when (furiganaMode) {
                                    "ALWAYS_SHOW" -> "あ [Show]"
                                    "ON_TAP" -> "あ [Tap]"
                                    else -> "あ [Hide]"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    // Kao coins badge
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFF8E1),
                        border = BorderStroke(1.dp, Color(0xFFFFD54F))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "🪙", fontSize = 14.sp)
                            Text(
                                text = "$kaoCoins",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Daily Progress Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🎯", fontSize = 18.sp)
                        Column {
                            Text(
                                text = "Today's Study Target",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$cardsStudiedToday / 15 Cards Reviewed",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        modifier = Modifier.clickable { viewModel.startTwoMinuteSprint() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("⚡", fontSize = 12.sp)
                            Text(
                                text = "2-Min Sprint",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Selector
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Triple(PracticeMode.DAILY_MISSION, "⚡ Daily Mission", Color(0xFF00695C)),
                    Triple(PracticeMode.CONJUGATION_DRILL, "🔄 Conjugation", Color(0xFF6A1B9A)),
                    Triple(PracticeMode.LEECH_REMEDY, "🐛 Leech Remedy", Color(0xFFC62828)),
                    Triple(PracticeMode.FLASHCARDS, "📇 Flashcards", MaterialTheme.colorScheme.primary),
                    Triple(PracticeMode.JLPT_STAR_QUESTIONS, "★ JLPT Star (並べ替え)", Color(0xFFF57C00)),
                    Triple(PracticeMode.PARTICLE_BATTLE, "⚡ Particle Battle (助詞)", Color(0xFF0288D1)),
                    Triple(PracticeMode.SURVIVAL_ROLEPLAY, "🎭 Situational Roleplay", Color(0xFF388E3C)),
                    Triple(PracticeMode.EXAM_PAPER_STUDY, "📝 Exam Paper (Anki)", Color(0xFFB71C1C)),
                    Triple(PracticeMode.JLPT_MOCK_EXAM, "🎯 JLPT Exam", Color(0xFFD32F2F)),
                    Triple(PracticeMode.KANJI_EXPLORER, "🈁 Kanji Grid", Color(0xFFE65100)),
                    Triple(PracticeMode.KANJI_LAB, "✍️ Kanji Lab", MaterialTheme.colorScheme.secondary),
                    Triple(PracticeMode.LISTENING_LESSON, "🎧 Listening", Color(0xFF00897B)),
                    Triple(PracticeMode.READING_LESSON, "📖 Reading", Color(0xFF1E88E5)),
                    Triple(PracticeMode.STORIES, "📚 Stories", Color(0xFF8E24AA)),
                    Triple(PracticeMode.SPEED_QUIZ, "⚡ Speed Quiz", MaterialTheme.colorScheme.tertiary)
                ).forEach { (mode, label, activeColor) ->
                    val isActive = practiceMode == mode
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isActive) activeColor else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { viewModel.setMode(mode) }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Anki Spaced Repetition (SRS) Cognitive Deck Tracker
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🧠", fontSize = 16.sp)
                            Text(
                                text = "Anki SRS Memory State",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Filter Due Cards Only Toggle
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (onlyDueCards) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { viewModel.toggleOnlyDueCards() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (onlyDueCards) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Active",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Text(
                                    text = if (onlyDueCards) "SRS Due Only (Active)" else "Filter Due Cards",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (onlyDueCards) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // New Count
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE3F2FD)
                        ) {
                            Text(
                                text = "🌱 ${srsDeckSummary.newCardsCount} New",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        // Learning Count
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFF3E0)
                        ) {
                            Text(
                                text = "⚡ ${srsDeckSummary.learningCardsCount} Learning",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        // Review Count
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "🧠 ${srsDeckSummary.reviewCardsCount} Due",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        // Retention Rate
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "${(srsDeckSummary.avgRetentionRate * 100).toInt()}% Retained",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        // Leech Alert & Remediation Trigger
                        val leechCards = remember(srsDeckSummary) { viewModel.getLeechCards() }
                        if (leechCards.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFEBEE),
                                border = BorderStroke(1.dp, Color(0xFFEF5350)),
                                modifier = Modifier.clickable { showLeechRemediationDialog = true }
                            ) {
                                Text(
                                    text = "⚠️ ${leechCards.size} Leech",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC62828),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    if (relearningCount > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFEBEE),
                            border = BorderStroke(1.dp, Color(0xFFE57373)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🔄", fontSize = 11.sp)
                                Text(
                                    text = "Active Relearn Queue: $relearningCount card(s) queued for second-pass memory consolidation",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // JLPT Level & Category Filter Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // JLPT Level Selector (ALL, N5, N4, N3, N2, N1)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("ALL", "N5", "N4", "N3", "N2", "N1").forEach { lvl ->
                        val isSelected = selectedJlptLevel == lvl
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { viewModel.setJlptLevel(lvl) }
                        ) {
                            Text(
                                text = lvl,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "${cards.size} Items Available",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // JLPT Exam Official Benchmark & Grounded Syllabus Bar
            if (practiceMode == PracticeMode.JLPT_MOCK_EXAM) {
                Spacer(modifier = Modifier.height(10.dp))
                JlptExamContextBanner(
                    selectedLevel = selectedJlptLevel ?: "ALL",
                    onExploreSyllabus = { query ->
                        aiViewModel.requestJlptSearch(query, selectedJlptLevel)
                        showGroundingDialog = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Chips (Only show if not in dedicated Kanji Lab or JLPT Exam)
            if (practiceMode == PracticeMode.FLASHCARDS || practiceMode == PracticeMode.SPEED_QUIZ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { viewModel.setCategory(null) },
                        label = { Text("All") },
                        shape = CircleShape
                    )
                    JapaneseCategory.values().forEach { cat ->
                        val selected = selectedCategory == cat
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.setCategory(cat) },
                            label = { Text("${cat.emoji} ${cat.displayName}") },
                            shape = CircleShape
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Dedicated Practice Modules: JLPT Star Questions, Particle Battle, and Situational Roleplay
            if (practiceMode == PracticeMode.JLPT_STAR_QUESTIONS) {
                val starQuestions = remember(selectedJlptLevel) {
                    if (selectedJlptLevel == null || selectedJlptLevel == "ALL") {
                        InteractivePracticeRepository.starQuestions
                    } else {
                        InteractivePracticeRepository.starQuestions.filter { it.jlptLevel.equals(selectedJlptLevel, ignoreCase = true) }
                            .ifEmpty { InteractivePracticeRepository.starQuestions }
                    }
                }
                val currentStarQ = starQuestions.getOrNull(activeStarQuestionIndex % starQuestions.size) ?: starQuestions.first()

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "QUESTION ${(activeStarQuestionIndex % starQuestions.size) + 1} OF ${starQuestions.size}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFF57C00),
                            letterSpacing = 1.sp
                        )
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFF3E0)
                        ) {
                            Text(
                                text = "★ JLPT ${currentStarQ.jlptLevel} 並べ替え",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    JlptStarScrambleCard(
                        question = currentStarQ,
                        ttsManager = ttsManager,
                        onAnswerSubmitted = { isCorrect ->
                            if (isCorrect) {
                                viewModel.recordDrillSuccess(5)
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                activeStarQuestionIndex = (activeStarQuestionIndex + 1) % starQuestions.size
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00))
                        ) {
                            Text("Next Star Question ★", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (practiceMode == PracticeMode.PARTICLE_BATTLE) {
                val particleQuestions = remember(selectedJlptLevel) {
                    if (selectedJlptLevel == null || selectedJlptLevel == "ALL") {
                        InteractivePracticeRepository.particleDrills
                    } else {
                        InteractivePracticeRepository.particleDrills.filter { it.jlptLevel.equals(selectedJlptLevel, ignoreCase = true) }
                            .ifEmpty { InteractivePracticeRepository.particleDrills }
                    }
                }
                val currentParticleQ = particleQuestions.getOrNull(activeParticleQuestionIndex % particleQuestions.size) ?: particleQuestions.first()

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DRILL ${(activeParticleQuestionIndex % particleQuestions.size) + 1} OF ${particleQuestions.size}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0288D1),
                            letterSpacing = 1.sp
                        )
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE1F5FE)
                        ) {
                            Text(
                                text = "助詞 JLPT ${currentParticleQ.jlptLevel}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0288D1),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    ParticleDrillCard(
                        question = currentParticleQ,
                        ttsManager = ttsManager,
                        onAnswered = { isCorrect ->
                            if (isCorrect) {
                                viewModel.recordDrillSuccess(5)
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                activeParticleQuestionIndex = (activeParticleQuestionIndex + 1) % particleQuestions.size
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                        ) {
                            Text("Next Particle Battle ⚡", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (practiceMode == PracticeMode.SURVIVAL_ROLEPLAY) {
                val scenarios = remember(selectedJlptLevel) {
                    if (selectedJlptLevel == null || selectedJlptLevel == "ALL") {
                        InteractivePracticeRepository.survivalScenarios
                    } else {
                        InteractivePracticeRepository.survivalScenarios.filter { it.jlptLevel.equals(selectedJlptLevel, ignoreCase = true) }
                            .ifEmpty { InteractivePracticeRepository.survivalScenarios }
                    }
                }
                SurvivalRoleplayView(
                    scenarios = scenarios,
                    ttsManager = ttsManager
                )
            } else if (practiceMode == PracticeMode.DAILY_MISSION) {
                var isEmergency by remember { mutableStateOf(false) }
                val missionPlan = remember(isEmergency, selectedJlptLevel, srsDeckSummary.reviewCardsCount) {
                    DailyStudyPlanner.createDefaultPlan(
                        cardsDueCount = srsDeckSummary.reviewCardsCount,
                        curriculumLevel = selectedJlptLevel ?: "N5",
                        isEmergencyMode = isEmergency
                    )
                }
                DailyMissionCard(
                    missionPlan = missionPlan,
                    onToggleEmergencyMode = { emergency ->
                        isEmergency = emergency
                    },
                    onStartStudy = {
                        viewModel.setMode(PracticeMode.FLASHCARDS)
                    }
                )
            } else if (sessionCompleted) {
                // Celebration & Summary Card
                SessionCompletedCard(
                    coinsEarned = sessionCoinsEarned,
                    totalStudied = cards.size,
                    quizState = quizState,
                    onRestart = { viewModel.restartSession() },
                    onBack = onBack
                )
            } else if (currentCard != null) {
                // Card Counter & Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUESTION / CARD ${currentIndex + 1} OF ${cards.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "${currentCard.category.emoji} ${currentCard.category.displayName} • JLPT ${currentCard.jlptLevel}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / cards.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (practiceMode) {
                    PracticeMode.FLASHCARDS -> {
                        FlashcardView(
                            item = currentCard,
                            isFlipped = isFlipped,
                            srsData = currentCardSrs,
                            furiganaMode = furiganaMode,
                            onFlip = { viewModel.flipCard() },
                            onRateSrs = { rating -> viewModel.ankiReviewCard(rating) },
                            ttsManager = ttsManager,
                            repository = viewModel.repository
                        )
                    }
                    PracticeMode.EXAM_PAPER_STUDY -> {
                        ExamPaperStudyCard(
                            item = currentCard,
                            questionNumber = currentIndex + 1,
                            totalQuestions = cards.size,
                            srsData = currentCardSrs,
                            onRateRecall = { rating -> viewModel.ankiReviewCard(rating) },
                            onAskSenseiGrounded = {
                                val selectedIdx = quizState.selectedOptionIndex
                                val userAns = if (selectedIdx != null && selectedIdx in currentCard.options.indices) {
                                    currentCard.options[selectedIdx]
                                } else null
                                aiViewModel.requestJlptQuestionGroundedExplanation(
                                    questionPrompt = currentCard.examQuestionPrompt.ifBlank { currentCard.japanese },
                                    targetItem = currentCard.japanese,
                                    jlptLevel = currentCard.jlptLevel,
                                    questionType = currentCard.examQuestionType,
                                    correctAnswer = currentCard.options.firstOrNull() ?: currentCard.meaning,
                                    userAnswer = userAns,
                                    isUserCorrect = if (quizState.isSubmitted) quizState.isCorrect else null,
                                    distractors = currentCard.options.drop(1)
                                )
                                showGroundingDialog = true
                            },
                            ttsManager = ttsManager
                        )
                    }
                    PracticeMode.KANJI_LAB -> {
                        KanjiDeepStudyCard(
                            item = currentCard,
                            isFlipped = isFlipped,
                            onFlip = { viewModel.flipCard() },
                            onReview = { isMastered -> viewModel.reviewFlashcard(isMastered) },
                            ttsManager = ttsManager
                        )
                    }
                    PracticeMode.JLPT_MOCK_EXAM -> {
                        JlptExamCard(
                            item = currentCard,
                            questionNumber = currentIndex + 1,
                            totalQuestions = cards.size,
                            quizState = quizState,
                            onSelectOption = { optionIndex, isCorrect ->
                                viewModel.selectQuizOption(optionIndex, isCorrect)
                            },
                            onNext = { viewModel.nextQuizQuestion() },
                            onAskSenseiGrounded = {
                                val selectedIdx = quizState.selectedOptionIndex
                                val userAns = if (selectedIdx != null && selectedIdx in currentCard.options.indices) {
                                    currentCard.options[selectedIdx]
                                } else null
                                aiViewModel.requestJlptQuestionGroundedExplanation(
                                    questionPrompt = currentCard.examQuestionPrompt.ifBlank { currentCard.japanese },
                                    targetItem = currentCard.japanese,
                                    jlptLevel = currentCard.jlptLevel,
                                    questionType = currentCard.examQuestionType,
                                    correctAnswer = currentCard.options.firstOrNull() ?: currentCard.meaning,
                                    userAnswer = userAns,
                                    isUserCorrect = if (quizState.isSubmitted) quizState.isCorrect else null,
                                    distractors = currentCard.options.drop(1)
                                )
                                showGroundingDialog = true
                            },
                            srsData = currentCardSrs,
                            onRateSrs = { rating -> viewModel.ankiReviewCard(rating) },
                            ttsManager = ttsManager
                        )
                    }
                    PracticeMode.CONJUGATION_DRILL -> {
                        ConjugationDrillCard(
                            item = currentCard,
                            ttsManager = ttsManager,
                            onNext = { viewModel.nextQuizQuestion() }
                        )
                    }
                    PracticeMode.LEECH_REMEDY -> {
                        val srs = currentCardSrs ?: viewModel.getSrsForCard(currentCard.id)
                        LeechRemediationCard(
                            item = currentCard,
                            srsData = srs,
                            ttsManager = ttsManager,
                            onDrillCard = { viewModel.nextQuizQuestion() }
                        )
                    }
                    PracticeMode.SPEED_QUIZ -> {
                        SpeedQuizView(
                            item = currentCard,
                            quizState = quizState,
                            onSelectOption = { viewModel.selectQuizOption(it) },
                            onNext = { viewModel.nextQuizQuestion() },
                            ttsManager = ttsManager
                        )
                    }
                    else -> {}
                }
            } else {
                Text(
                    text = "No cards available in this category.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showGroundingDialog) {
        JlptSearchGroundingDialog(
            result = jlptGroundingResult,
            isLoading = aiIsLoading,
            title = "JLPT Sensei Grounded Intelligence",
            subtitle = "Google Search Grounded • JLPT ${selectedJlptLevel ?: "N5-N1"}",
            onDismiss = {
                showGroundingDialog = false
                aiViewModel.clearJlptGrounding()
            },
            onSendQuery = { query ->
                aiViewModel.requestJlptSearch(query, selectedJlptLevel)
            }
        )
    }

    if (showLeechRemediationDialog) {
        val leechCards = remember(srsDeckSummary) { viewModel.getLeechCards() }
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showLeechRemediationDialog = false }
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
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
                            Text("⚠️", fontSize = 22.sp)
                            Column {
                                Text(
                                    text = "Leech Remediation Lab",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${leechCards.size} difficult cards with 3+ lapses",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFC62828)
                                )
                            }
                        }
                        IconButton(onClick = { showLeechRemediationDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (leechCards.isEmpty()) {
                        Text(
                            text = "🎉 No leeches currently! All studied cards have high recall ease.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "A 'Leech' is a card you've repeatedly forgotten (lapsed 3+ times). Teacher Aiko deconstructs the cognitive conflict with mnemonics, pitch accents, and native collocations:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        leechCards.forEach { (item, srs) ->
                            LeechRemediationCard(
                                item = item,
                                srsData = srs,
                                ttsManager = ttsManager,
                                onDrillCard = {
                                    showLeechRemediationDialog = false
                                    viewModel.setMode(PracticeMode.FLASHCARDS)
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JlptExamContextBanner(
    selectedLevel: String,
    onExploreSyllabus: (String) -> Unit
) {
    val (levelTitle, passCriteria, description) = when (selectedLevel.uppercase()) {
        "N5" -> Triple(
            "JLPT N5 (Basic Foundation)",
            "Pass: 80 / 180 • Section Min: 19 / 60",
            "Covers basic hiragana, katakana, ~100 foundational kanji, and ~800 daily survival words. Must understand typical classroom & daily life Japanese spoken slowly."
        )
        "N4" -> Triple(
            "JLPT N4 (Elementary Fluency)",
            "Pass: 90 / 180 • Section Min: 19 / 60",
            "Covers basic daily conversations, ~300 kanji, ~1,500 vocabulary words, conditional forms (たら/ば/なら), and ability to read simple passages written in basic vocabulary."
        )
        "N3" -> Triple(
            "JLPT N3 (Intermediate Bridge)",
            "Pass: 95 / 180 • Section Min: 19 / 60",
            "The crucial bridge level. ~650 kanji, ~3,700 vocabulary words. Able to understand everyday conversations spoken at near-natural speed and comprehend newspaper headlines."
        )
        "N2" -> Triple(
            "JLPT N2 (Pre-Advanced / Business)",
            "Pass: 90 / 180 • Section Min: 19 / 60",
            "General business & university proficiency. ~1,000 kanji, ~6,000 vocabulary words. Able to comprehend magazine articles, editorials, and nuanced workplace communications."
        )
        "N1" -> Triple(
            "JLPT N1 (Advanced Native)",
            "Pass: 100 / 180 • Section Min: 19 / 60",
            "Highest standard. ~2,000+ kanji, 10,000+ vocabulary words, academic discourse, literary nuance, four-character idioms (四字熟語), and complex spoken lectures."
        )
        else -> Triple(
            "JLPT Comprehensive Multi-Level (N5-N1)",
            "Sectional Minimum Rule Applies to All Tiers (≥19/60)",
            "Full spectrum examination mode. Practice across Kanji reading, vocabulary usage, star sentence ordering, and deep reading comprehension."
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f))
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
                    text = "🎯 $levelTitle",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        text = passCriteria,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                lineHeight = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = {
                        onExploreSyllabus("Official JLPT $selectedLevel exam guidelines, scoring criteria, and test syllabus")
                    },
                    shape = CircleShape,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = "Search JLPT syllabus",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Official JLPT Intel (Grounded)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashcardView(
    item: JapaneseItem,
    isFlipped: Boolean,
    srsData: SrsCardData?,
    furiganaMode: String = "ALWAYS_SHOW",
    onFlip: () -> Unit,
    onRateSrs: (AnkiRating) -> Unit,
    ttsManager: JapaneseTtsManager? = null,
    repository: com.example.noignore.japanese.data.JapaneseDeckRepository? = null
) {
    var revealedFuriganaOnTap by remember(item.id) { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember(item.id) { Animatable(0f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationX = offsetX.value
                rotationZ = offsetX.value / 40f
            }
            .pointerInput(item.id, isFlipped) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            val dragDistance = offsetX.value
                            if (isFlipped && dragDistance < -160f) {
                                // Swiped Left -> AGAIN (Need Review)
                                offsetX.animateTo(-600f, spring())
                                onRateSrs(AnkiRating.AGAIN)
                                offsetX.snapTo(0f)
                            } else if (isFlipped && dragDistance > 160f) {
                                // Swiped Right -> GOOD (Recalled Well)
                                offsetX.animateTo(600f, spring())
                                onRateSrs(AnkiRating.GOOD)
                                offsetX.snapTo(0f)
                            } else {
                                offsetX.animateTo(0f, spring())
                            }
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch { offsetX.animateTo(0f, spring()) }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        coroutineScope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount)
                        }
                    }
                )
            }
            .clickable { onFlip() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Front: Optional Furigana reading above Kanji (based on furiganaMode)
            val showFrontFurigana = when (furiganaMode) {
                "ALWAYS_SHOW" -> true
                "ON_TAP" -> revealedFuriganaOnTap
                else -> false // NEVER_SHOW
            }

            if (showFrontFurigana && item.reading.isNotBlank()) {
                Text(
                    text = item.reading,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            } else if (furiganaMode == "ON_TAP" && !revealedFuriganaOnTap) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clickable { revealedFuriganaOnTap = true }
                        .padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "👁️ Tap to peek furigana",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Front: Japanese Characters & Speaker Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = item.japanese,
                    fontSize = if (item.japanese.length <= 2) 68.sp else 36.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                JapaneseSpeakerButton(
                    ttsManager = ttsManager,
                    textToSpeak = item.japanese,
                    size = 38.dp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedContent(
                targetState = isFlipped,
                transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(180)) },
                label = "flip"
            ) { flipped ->
                if (flipped) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Furigana / Reading with Audio
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = item.reading,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                            JapaneseSpeakerButton(
                                ttsManager = ttsManager,
                                textToSpeak = item.reading,
                                size = 32.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Romaji
                        Text(
                            text = item.romaji,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // English Meaning
                        Text(
                            text = item.meaning,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )

                        if (item.category == JapaneseCategory.VOCAB && item.reading.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            PitchAccentVisualizer(
                                word = item.japanese,
                                reading = item.reading
                            )
                        }

                        if (item.mnemonicOrNote.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Lightbulb,
                                        contentDescription = "Mnemonic",
                                        tint = Color(0xFFFFA000),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = item.mnemonicOrNote,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Deep Study Enrichment: Wanikani Mnemonic
                        val kanjiMnemonic = remember(item.japanese) {
                            StudyMaterialEnrichmentRepository.getMnemonicForKanji(item.japanese)
                        }
                        if (kanjiMnemonic != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            KanjiMnemonicCard(breakdown = kanjiMnemonic, ttsManager = ttsManager)
                        }

                        // Deep Study Enrichment: Bunpro Grammar Anatomy
                        val grammarBreakdown = remember(item.id) {
                            StudyMaterialEnrichmentRepository.getGrammarBreakdown(item.id)
                        }
                        if (grammarBreakdown != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            BunproGrammarAnatomyCard(breakdown = grammarBreakdown, ttsManager = ttsManager)
                        }

                        // Deep Study Enrichment: Word Collocations & Pitch Accent
                        val collocations = remember(item.japanese) {
                            StudyMaterialEnrichmentRepository.getCollocationsForWord(item.japanese)
                        }
                        if (collocations.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            WordCollocationsCard(collocations = collocations, ttsManager = ttsManager)
                        }

                        if (srsData != null && srsData.isLeech) {
                            Spacer(modifier = Modifier.height(12.dp))
                            LeechRemediationCard(
                                item = item,
                                srsData = srsData,
                                ttsManager = ttsManager,
                                onDrillCard = { /* Card is currently being reviewed */ }
                            )
                        }

                        if (item.exampleJapanese.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            com.example.noignore.ui.japanese.study.MorphologicalSentenceMiningCard(
                                sentence = item.exampleJapanese,
                                englishTranslation = item.exampleEnglish,
                                romaji = item.exampleRomaji,
                                repository = repository,
                                ttsManager = ttsManager
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        JapaneseShadowingCard(
                            targetText = item.japanese,
                            targetReading = item.reading,
                            targetRomaji = item.romaji,
                            englishMeaning = item.meaning,
                            ttsManager = ttsManager
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        EbbinghausMemoryCurveView(srsData = srsData)
                    }
                } else {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "Tap Card or Button to Reveal Meaning & Reading",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Anki SRS Response Controls
    if (isFlipped) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnkiSrsRatingBar(
                srsData = srsData,
                onRate = onRateSrs,
                title = "Grade Your Memory Recall (FSRS v4.5)"
            )
            // Gesture Ergonomics Hint
            Row(
                modifier = Modifier.padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👈 Swipe Left: Again",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = Color(0xFFC62828),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Swipe Right: Good 👉",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    } else {
        Button(
            onClick = onFlip,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🧠", fontSize = 16.sp)
                Text("Reveal Meaning & Grade Recall ➔", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SpeedQuizView(
    item: JapaneseItem,
    quizState: QuizState,
    onSelectOption: (Int) -> Unit,
    onNext: () -> Unit,
    ttsManager: JapaneseTtsManager? = null
) {
    // Generate 4 randomized options (with index 0 as correct)
    val displayOptions = remember(item.id) {
        if (item.options.isNotEmpty()) {
            item.options
        } else {
            listOf(item.meaning, "Water", "To study", "Person")
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (quizState.currentStreak > 1) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFFE082),
                    border = BorderStroke(1.dp, Color(0xFFFFA000))
                ) {
                    Text(
                        text = "🔥 STREAK: ${quizState.currentStreak} COMBO!",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE65100),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = "What is the meaning / reading of:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = item.japanese,
                    fontSize = if (item.japanese.length <= 2) 54.sp else 32.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                JapaneseSpeakerButton(
                    ttsManager = ttsManager,
                    textToSpeak = item.japanese,
                    size = 36.dp
                )
            }

            if (item.reading.isNotBlank() && item.category == JapaneseCategory.KANJI) {
                Text(
                    text = "(${item.reading})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4 Choices
            displayOptions.forEachIndexed { index, option ->
                val isSelected = quizState.selectedOptionIndex == index
                val isCorrectAnswer = index == 0

                val (btnColor, textColor, borderColor) = when {
                    !quizState.isSubmitted -> {
                        Triple(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.onSurface,
                            MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    isCorrectAnswer -> {
                        Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), Color(0xFF4CAF50))
                    }
                    isSelected && !quizState.isCorrect -> {
                        Triple(Color(0xFFFFEBEE), Color(0xFFC62828), Color(0xFFE57373))
                    }
                    else -> {
                        Triple(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable(enabled = !quizState.isSubmitted) { onSelectOption(index) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = btnColor),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected || (quizState.isSubmitted && isCorrectAnswer)) FontWeight.Bold else FontWeight.Normal,
                            color = textColor
                        )

                        if (quizState.isSubmitted) {
                            if (isCorrectAnswer) {
                                Text("✨ 正解 (Correct!)", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 12.sp)
                            } else if (isSelected) {
                                Text("❌", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            if (quizState.isSubmitted) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Next Question ➔", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SessionCompletedCard(
    coinsEarned: Int,
    totalStudied: Int,
    quizState: QuizState,
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎉", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "お疲れ様でした！",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Otsukaresama deshita! Great Japanese study session!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🪙 +$coinsEarned", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFE65100))
                        Text(text = "Kao Coins", style = MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📖 $totalStudied", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        Text(text = "Cards Reviewed", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Restart")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Study Another Round", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Return to Daily Directives", fontWeight = FontWeight.Bold)
            }
        }
    }
}
