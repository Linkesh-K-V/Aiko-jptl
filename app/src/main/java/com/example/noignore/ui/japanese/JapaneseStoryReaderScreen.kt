package com.example.noignore.ui.japanese

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.audio.JapaneseTtsManager
import com.example.noignore.japanese.data.JapaneseDeckRepository
import com.example.noignore.japanese.data.JapaneseStoryRepository
import com.example.noignore.japanese.model.JapaneseStory
import com.example.noignore.japanese.model.StorySentence
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Interactive Japanese Short Story Reader with:
 * - Line-by-line synchronized Text-to-Speech audio
 * - Furigana / Hiragana pronunciation guides toggle
 * - Romaji toggle
 * - English translation toggle
 * - Interactive vocabulary glossary
 * - Comprehension quiz rewarding Kao Coins
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JapaneseStoryReaderScreen(
    story: JapaneseStory,
    ttsManager: JapaneseTtsManager?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val storyRepo = remember { JapaneseStoryRepository(context) }
    val deckRepo = remember { JapaneseDeckRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    var showFurigana by remember { mutableStateOf(true) }
    var showRomaji by remember { mutableStateOf(false) }
    var showEnglish by remember { mutableStateOf(true) }
    var isSlowAudio by remember { mutableStateOf(false) }

    var activeSentenceIndex by remember { mutableIntStateOf(-1) }
    var isNarratingAll by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Story Text, 1: Vocabulary, 2: Quiz
    var quizAnswers = remember { mutableStateMapOf<String, Int>() }
    var isStoryMarkedCompleted by remember { mutableStateOf(storyRepo.isStoryCompleted(story.id)) }

    val isSpeaking by ttsManager?.isSpeaking?.collectAsState() ?: remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            ttsManager?.stop()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = story.emoji,
                                fontSize = 20.sp
                            )
                            Text(
                                text = story.title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Text(
                            text = "${story.titleEnglish} • JLPT ${story.jlptLevel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                    if (isStoryMarkedCompleted) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Read",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "READ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
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
        ) {
            // Tabs: Story Text | Vocabulary | Quiz
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("📖 Story Text", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("🈁 Key Words (${story.keyVocabulary.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("🎯 Quiz (${story.comprehensionQuestions.size})", fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Story Reader View
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Master Audio & Reading Controls Bar
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Narration Action Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            if (isNarratingAll) {
                                                ttsManager?.stop()
                                                isNarratingAll = false
                                                activeSentenceIndex = -1
                                            } else {
                                                isNarratingAll = true
                                                coroutineScope.launch {
                                                    for (i in story.sentences.indices) {
                                                        if (!isNarratingAll) break
                                                        activeSentenceIndex = i
                                                        val sentence = story.sentences[i]
                                                        ttsManager?.speak(sentence.japanese, isSlow = isSlowAudio)
                                                        // Estimate reading duration based on character count
                                                        val delayMs = (sentence.japanese.length * if (isSlowAudio) 320L else 220L) + 1200L
                                                        delay(delayMs)
                                                    }
                                                    isNarratingAll = false
                                                    activeSentenceIndex = -1
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isNarratingAll) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (isNarratingAll) Icons.Default.Stop else Icons.Default.PlayArrow,
                                            contentDescription = "Narration",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isNarratingAll) "Stop Audio" else "Read Whole Story 🔊",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    // Speed Toggle Pill
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSlowAudio) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, if (isSlowAudio) Color(0xFFFF9800) else MaterialTheme.colorScheme.outlineVariant),
                                        modifier = Modifier.clickable { isSlowAudio = !isSlowAudio }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(text = if (isSlowAudio) "🐢" else "⚡", fontSize = 12.sp)
                                            Text(
                                                text = if (isSlowAudio) "Slow (0.7x)" else "Normal (1.0x)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSlowAudio) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                // Display Toggles (Furigana, Romaji, English)
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    FilterChip(
                                        selected = showFurigana,
                                        onClick = { showFurigana = !showFurigana },
                                        label = { Text("💮 Furigana", fontSize = 11.sp) },
                                        shape = CircleShape
                                    )
                                    FilterChip(
                                        selected = showRomaji,
                                        onClick = { showRomaji = !showRomaji },
                                        label = { Text("🔤 Romaji", fontSize = 11.sp) },
                                        shape = CircleShape
                                    )
                                    FilterChip(
                                        selected = showEnglish,
                                        onClick = { showEnglish = !showEnglish },
                                        label = { Text("🌐 English", fontSize = 11.sp) },
                                        shape = CircleShape
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Story Sentence Cards
                        story.sentences.forEachIndexed { index, sentence ->
                            val isActive = activeSentenceIndex == index

                            StorySentenceCard(
                                sentence = sentence,
                                index = index,
                                isActive = isActive,
                                showFurigana = showFurigana,
                                showRomaji = showRomaji,
                                showEnglish = showEnglish,
                                isSlowAudio = isSlowAudio,
                                ttsManager = ttsManager,
                                onClick = {
                                    activeSentenceIndex = index
                                    ttsManager?.speak(sentence.japanese, isSlow = isSlowAudio)
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Next Step Banner (Go to Quiz)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTab = 2 },
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("🎯", fontSize = 24.sp)
                                    Column {
                                        Text(
                                            text = "Test Comprehension",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "Answer ${story.comprehensionQuestions.size} quick questions to earn Kao Coins!",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = "Quiz →",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                1 -> {
                    // Vocabulary Glossary Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "KEY VOCABULARY IN THIS STORY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        story.keyVocabulary.forEach { vocab ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.Bottom,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = vocab.word,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = vocab.reading,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = vocab.meaning,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Speaker Button for each word
                                    JapaneseSpeakerButton(
                                        ttsManager = ttsManager,
                                        textToSpeak = vocab.word,
                                        isSlow = isSlowAudio
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                2 -> {
                    // Story Comprehension Quiz Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "COMPREHENSION CHECK",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        story.comprehensionQuestions.forEachIndexed { qIdx, question ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp)
                                ) {
                                    Text(
                                        text = "Question ${qIdx + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = question.question,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    val answeredIndex = quizAnswers[question.id]
                                    val isAnswered = answeredIndex != null

                                    question.options.forEachIndexed { optIdx, optionText ->
                                        val isSelected = answeredIndex == optIdx
                                        val isCorrectChoice = optIdx == question.correctOptionIndex

                                        val optBg = when {
                                            !isAnswered -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            isCorrectChoice -> Color(0xFFE8F5E9)
                                            isSelected -> Color(0xFFFFEBEE)
                                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        }

                                        val optBorder = when {
                                            !isAnswered -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                            isCorrectChoice -> Color(0xFF4CAF50)
                                            isSelected -> Color(0xFFE53935)
                                            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                        }

                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable(enabled = !isAnswered) {
                                                    quizAnswers[question.id] = optIdx
                                                    val isCorrect = optIdx == question.correctOptionIndex
                                                    val coins = if (isCorrect) 5 else 2
                                                    deckRepo.addKaoCoins(coins)

                                                    // If all questions are answered, mark completed
                                                    if (quizAnswers.size == story.comprehensionQuestions.size) {
                                                        storyRepo.markStoryCompleted(story.id)
                                                        isStoryMarkedCompleted = true
                                                    }
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            color = optBg,
                                            border = BorderStroke(1.2.dp, optBorder)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = when {
                                                        !isAnswered -> MaterialTheme.colorScheme.surface
                                                        isCorrectChoice -> Color(0xFF4CAF50)
                                                        isSelected -> Color(0xFFE53935)
                                                        else -> MaterialTheme.colorScheme.surface
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = ('A' + optIdx).toString(),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isAnswered && (isCorrectChoice || isSelected)) Color.White else MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                }

                                                Text(
                                                    text = optionText,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }

                                    // Explanation Box
                                    if (isAnswered) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(
                                                    text = if (answeredIndex == question.correctOptionIndex) "✓ Correct! (+5 Kao Coins 🪙)" else "✗ Review Note (+2 Coins)",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (answeredIndex == question.correctOptionIndex) Color(0xFF2E7D32) else Color(0xFFC62828)
                                                )
                                                Text(
                                                    text = question.explanation,
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

/**
 * Individual story sentence card with audio playback and furigana/romaji/english toggles.
 */
@Composable
private fun StorySentenceCard(
    sentence: StorySentence,
    index: Int,
    isActive: Boolean,
    showFurigana: Boolean,
    showRomaji: Boolean,
    showEnglish: Boolean,
    isSlowAudio: Boolean,
    ttsManager: JapaneseTtsManager?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("story_sentence_${sentence.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            if (isActive) 2.dp else 1.dp,
            if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 3.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Sentence Number Badge
            Surface(
                shape = CircleShape,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(26.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${index + 1}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Main Content: Japanese, Furigana, Romaji, English
            Column(modifier = Modifier.weight(1f)) {
                // Furigana (reading guide above/inline)
                if (showFurigana && sentence.furigana.isNotBlank()) {
                    Text(
                        text = sentence.furigana,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Primary Japanese Text
                Text(
                    text = sentence.japanese,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp,
                    fontFamily = FontFamily.Serif
                )

                // Romaji guide
                if (showRomaji && sentence.romaji.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = sentence.romaji,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                // English contextual translation
                if (showEnglish && sentence.english.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = sentence.english,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }

            // Line-by-line Audio Speaker Button
            JapaneseSpeakerButton(
                ttsManager = ttsManager,
                textToSpeak = sentence.japanese,
                isSlow = isSlowAudio,
                size = 36.dp
            )
        }
    }
}
