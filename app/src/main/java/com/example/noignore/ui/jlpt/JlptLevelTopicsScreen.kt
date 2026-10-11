package com.example.noignore.ui.jlpt

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.audio.JapaneseTtsManager
import com.example.noignore.japanese.data.JlptTopicCurriculumRepository
import com.example.noignore.japanese.model.JlptExamLevel
import com.example.noignore.japanese.model.JlptTopicCategory
import com.example.noignore.japanese.model.JlptTopicItem
import com.example.noignore.japanese.model.JlptTopicUnit
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton
import com.example.noignore.util.TaskHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JlptLevelTopicsScreen(
    initialLevelCode: String = "N5",
    onBack: () -> Unit,
    onPracticeTopic: (JlptTopicUnit) -> Unit = {}
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val ttsManager = remember { JapaneseTtsManager(context) }
    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    val repository = remember { JlptTopicCurriculumRepository(context) }
    var selectedLevel by remember {
        mutableStateOf(JlptExamLevel.fromCode(initialLevelCode))
    }
    var selectedCategory by remember { mutableStateOf(JlptTopicCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    // Track understood state reactively
    var refreshTrigger by remember { mutableStateOf(0) }
    val understoodCount = remember(selectedLevel, refreshTrigger) {
        repository.getUnderstoodCount(selectedLevel)
    }
    val totalCount = remember(selectedLevel, refreshTrigger) {
        repository.getTotalCount(selectedLevel)
    }

    // Filter topics by level, category, and search query
    val topicUnits = remember(selectedLevel, selectedCategory, searchQuery, refreshTrigger) {
        val baseList = if (searchQuery.isNotBlank()) {
            repository.searchTopics(selectedLevel, searchQuery)
        } else {
            repository.getTopicsByCategory(selectedLevel, selectedCategory)
        }
        if (selectedCategory != JlptTopicCategory.ALL && searchQuery.isNotBlank()) {
            baseList.filter { it.category == selectedCategory }
        } else {
            baseList
        }
    }

    val levelAccentColor = Color(selectedLevel.colorHex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "JLPT ${selectedLevel.code} Topics",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = levelAccentColor.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, levelAccentColor.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = selectedLevel.difficulty,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = levelAccentColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Curriculum, Formulas & Explanations (試験項目)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            TaskHapticFeedback.performLightTapHaptic(context, hapticFeedback)
                            onBack()
                        },
                        modifier = Modifier.testTag("jlpt_topics_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. JLPT LEVEL SELECTOR TABS (N5, N4, N3, N2, N1)
            item {
                Spacer(modifier = Modifier.height(4.dp))
                LevelSelectorPills(
                    selectedLevel = selectedLevel,
                    onSelectLevel = { level ->
                        TaskHapticFeedback.performLightTapHaptic(context, hapticFeedback)
                        selectedLevel = level
                    }
                )
            }

            // 2. TEACHER AIKO'S LEVEL OVERVIEW & PROGRESS CARD
            item {
                LevelOverviewCard(
                    level = selectedLevel,
                    understoodCount = understoodCount,
                    totalCount = totalCount,
                    accentColor = levelAccentColor
                )
            }

            // 3. SEARCH & CATEGORY FILTER BAR
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("topic_search_field"),
                        placeholder = {
                            Text("Search ${selectedLevel.code} topics, particles, kanji...")
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = levelAccentColor,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    // Category Filter Chips
                    CategoryFilterChipsRow(
                        selectedCategory = selectedCategory,
                        accentColor = levelAccentColor,
                        onSelectCategory = { cat ->
                            TaskHapticFeedback.performLightTapHaptic(context, hapticFeedback)
                            selectedCategory = cat
                        }
                    )
                }
            }

            // 4. TOPIC LIST HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOPICS IN ${selectedLevel.code} (${topicUnits.size} FOUND)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = levelAccentColor,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Tap cards to read & listen",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 5. LIST OF DETAILED TOPIC UNITS
            if (topicUnits.isEmpty()) {
                item {
                    EmptyTopicState(searchQuery = searchQuery, level = selectedLevel)
                }
            } else {
                items(topicUnits, key = { it.id }) { unit ->
                    val isUnderstood = remember(unit.id, refreshTrigger) {
                        repository.isTopicUnderstood(unit.id)
                    }

                    TopicUnitCard(
                        unit = unit,
                        isUnderstood = isUnderstood,
                        accentColor = levelAccentColor,
                        ttsManager = ttsManager,
                        onToggleUnderstood = {
                            val state = repository.toggleTopicUnderstood(unit.id)
                            refreshTrigger++
                            if (state) {
                                TaskHapticFeedback.performCelebrationHaptic(context)
                                Toast.makeText(context, "🌟 Topic marked as Understood!", Toast.LENGTH_SHORT).show()
                            } else {
                                TaskHapticFeedback.performLightTapHaptic(context, hapticFeedback)
                            }
                        },
                        onPracticeTopic = {
                            TaskHapticFeedback.performLightTapHaptic(context, hapticFeedback)
                            onPracticeTopic(unit)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

/**
 * Segmented level selector pill row for N5, N4, N3, N2, N1.
 */
@Composable
private fun LevelSelectorPills(
    selectedLevel: JlptExamLevel,
    onSelectLevel: (JlptExamLevel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        JlptExamLevel.entries.forEach { level ->
            val isSelected = level == selectedLevel
            val levelColor = Color(level.colorHex)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSelected) levelColor else Color.Transparent
                    )
                    .clickable { onSelectLevel(level) }
                    .padding(vertical = 10.dp)
                    .testTag("level_tab_${level.code}"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = level.code,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = level.difficulty.take(4),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Overview card for the selected JLPT level with Teacher Aiko's strategic tips and progress.
 */
@Composable
private fun LevelOverviewCard(
    level: JlptExamLevel,
    understoodCount: Int,
    totalCount: Int,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Teacher Aiko Avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🌸", fontSize = 24.sp)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = level.japaneseTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(
                        text = level.targetDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatBadge(label = "Kanji", value = "~${level.kanjiCount}", color = accentColor)
                StatBadge(label = "Vocab", value = "~${level.vocabCount}", color = accentColor)
                StatBadge(label = "Grammar", value = "~${level.grammarPoints}", color = accentColor)
                StatBadge(label = "Topics", value = "$totalCount", color = accentColor)
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Teacher Aiko's Advice Callout
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = accentColor.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("💡", fontSize = 18.sp)
                    Column {
                        Text(
                            text = "Teacher Aiko's Study Strategy:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = level.teacherAikoAdvice,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mastery Progress
            val progress = if (totalCount > 0) understoodCount.toFloat() / totalCount else 0f
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Level Topic Understanding",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$understoodCount / $totalCount topics (${(progress * 100).toInt()}%)",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = accentColor,
                trackColor = accentColor.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
private fun StatBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Filter chips for topic categories.
 */
@Composable
private fun CategoryFilterChipsRow(
    selectedCategory: JlptTopicCategory,
    accentColor: Color,
    onSelectCategory: (JlptTopicCategory) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(JlptTopicCategory.entries) { cat ->
            val isSelected = cat == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = { onSelectCategory(cat) },
                label = {
                    Text("${cat.emoji} ${cat.label}")
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = accentColor.copy(alpha = 0.15f),
                    selectedLabelColor = accentColor
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    selectedBorderColor = accentColor,
                    borderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }
    }
}

/**
 * Detailed expandable card for a topic unit.
 */
@Composable
private fun TopicUnitCard(
    unit: JlptTopicUnit,
    isUnderstood: Boolean,
    accentColor: Color,
    ttsManager: JapaneseTtsManager?,
    onToggleUnderstood: () -> Unit,
    onPracticeTopic: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("topic_unit_card_${unit.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.5.dp,
            if (isUnderstood) accentColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row with Category, Title, Understood Badge, and Expand/Collapse
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = accentColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${unit.category.emoji} ${unit.category.label}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        if (isUnderstood) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Understood",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Understood",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = unit.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = unit.japaneseTitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = accentColor
                    )
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Overview explanation
            Text(
                text = unit.overview,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Sensei Tip
            if (unit.senseiTip.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accentColor.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌸", fontSize = 16.sp)
                        Text(
                            text = unit.senseiTip,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Expandable Items Section
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    Text(
                        text = "CORE ITEMS IN THIS TOPIC (${unit.items.size}):",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        letterSpacing = 0.5.sp
                    )

                    unit.items.forEach { item ->
                        TopicItemCard(
                            item = item,
                            accentColor = accentColor,
                            ttsManager = ttsManager
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bottom Action Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onToggleUnderstood,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("toggle_understood_button_${unit.id}"),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                1.2.dp,
                                if (isUnderstood) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isUnderstood) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = if (isUnderstood) Icons.Default.Check else Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isUnderstood) "Understood!" else "Mark Understood",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onPracticeTopic,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("practice_topic_button_${unit.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor
                            )
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Practice Topic", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Discrete study item card with large Japanese characters, reading, Romaji,
 * English meaning, formula, explanation, and native TTS audio.
 */
@Composable
private fun TopicItemCard(
    item: JlptTopicItem,
    accentColor: Color,
    ttsManager: JapaneseTtsManager?
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("topic_item_${item.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Main Character & Audio Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.japanese,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (item.reading.isNotBlank() && item.reading != item.japanese) {
                            Text(
                                text = "[ ${item.reading} ]",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = accentColor
                            )
                        }
                    }
                    if (item.romaji.isNotBlank()) {
                        Text(
                            text = item.romaji,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                JapaneseSpeakerButton(
                    textToSpeak = item.reading.ifBlank { item.japanese },
                    ttsManager = ttsManager,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Meaning
            Text(
                text = item.meaning,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Pattern Formula Callout (for grammar/verbs/particles)
            if (item.patternFormula.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accentColor.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("📐", fontSize = 14.sp)
                        Text(
                            text = item.patternFormula,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }
            }

            // Explanation
            if (item.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.explanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Example Sentence with pronunciation audio
            if (item.exampleJapanese.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.exampleJapanese,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (item.exampleReading.isNotBlank()) {
                                Text(
                                    text = item.exampleReading,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = accentColor
                                )
                            }
                            Text(
                                text = item.exampleEnglish,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        JapaneseSpeakerButton(
                            textToSpeak = item.exampleReading.ifBlank { item.exampleJapanese },
                            ttsManager = ttsManager,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }

            // Mnemonic Tip
            if (item.mnemonicTip.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "💡 Tip: ${item.mnemonicTip}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun EmptyTopicState(searchQuery: String, level: JlptExamLevel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🔍", fontSize = 36.sp)
            Text(
                text = "No topics matched \"$searchQuery\"",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Try searching for a different keyword or switch to another category in ${level.code}.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
