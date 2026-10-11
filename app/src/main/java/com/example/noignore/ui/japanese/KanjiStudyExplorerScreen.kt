package com.example.noignore.ui.japanese

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.audio.JapaneseTtsManager
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton

/**
 * Authentic Kanji Study Grid Explorer inspired by the Kanji Study app and Renshuu.
 * Allows learners to explore all Kanji in an interactive grid, filter by JLPT level & mastery,
 * search by English/Romaji/Kana, view stroke order, and launch interactive finger drawing canvas.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun KanjiStudyExplorerScreen(
    kanjiList: List<JapaneseItem>,
    kaoCoins: Int,
    ttsManager: JapaneseTtsManager? = null,
    onUpdateMastery: (String, Int) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedJlpt by remember { mutableStateOf("ALL") }
    var selectedMasteryFilter by remember { mutableStateOf<Int?>(null) } // null = All, 0 = New, 1/2 = Learning, 3 = Mastered
    var selectedKanjiForDetail by remember { mutableStateOf<JapaneseItem?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Filter kanji
    val filteredKanji = kanjiList.filter { item ->
        val matchesJlpt = selectedJlpt == "ALL" || item.jlptLevel.equals(selectedJlpt, ignoreCase = true)
        val matchesMastery = when (selectedMasteryFilter) {
            null -> true
            0 -> item.mastery == 0
            1 -> item.mastery in 1..2
            3 -> item.mastery == 3
            else -> true
        }
        val q = searchQuery.trim().lowercase()
        val matchesSearch = q.isEmpty() ||
                item.japanese.contains(q, ignoreCase = true) ||
                item.meaning.lowercase().contains(q) ||
                item.romaji.lowercase().contains(q) ||
                item.reading.contains(q, ignoreCase = true) ||
                item.onyomi.contains(q, ignoreCase = true) ||
                item.kunyomi.contains(q, ignoreCase = true)

        matchesJlpt && matchesMastery && matchesSearch
    }

    // Stats
    val currentLevelPool = if (selectedJlpt == "ALL") kanjiList else kanjiList.filter { it.jlptLevel.equals(selectedJlpt, ignoreCase = true) }
    val totalInPool = currentLevelPool.size
    val masteredInPool = currentLevelPool.count { it.mastery == 3 }
    val progressFraction = if (totalInPool > 0) masteredInPool.toFloat() / totalInPool.toFloat() else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Kanji Study",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "漢字",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
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
                .padding(horizontal = 16.dp)
        ) {
            // Live Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by Romaji, Meaning, Kana, or Kanji...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // JLPT Level Chips (ALL, N5, N4, N3, N2, N1)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("ALL", "N5", "N4", "N3", "N2", "N1").forEach { lvl ->
                    val isSelected = selectedJlpt == lvl
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { selectedJlpt = lvl }
                    ) {
                        Text(
                            text = lvl,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mastery Filter Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Pair(null, "All Kanji (${currentLevelPool.size})"),
                    Pair(0, "🌱 New"),
                    Pair(1, "📖 Learning"),
                    Pair(3, "⭐ Mastered")
                ).forEach { (filterVal, label) ->
                    FilterChip(
                        selected = selectedMasteryFilter == filterVal,
                        onClick = { selectedMasteryFilter = filterVal },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        shape = CircleShape
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$selectedJlpt Kanji Mastery",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$masteredInPool / $totalInPool (${(progressFraction * 100).toInt()}%)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = Color(0xFF2E7D32),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grid of Kanji Tiles
            if (filteredKanji.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Kanji found matching filters.\nTry searching by Romaji or English meaning.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredKanji, key = { it.id }) { item ->
                        KanjiGridTile(
                            item = item,
                            onClick = { selectedKanjiForDetail = item },
                            ttsManager = ttsManager
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Kanji Deep Study & Writing Practice Canvas
    selectedKanjiForDetail?.let { activeKanji ->
        ModalBottomSheet(
            onDismissRequest = { selectedKanjiForDetail = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kanji Deep Study",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { selectedKanjiForDetail = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                KanjiDeepStudyCard(
                    item = activeKanji,
                    isFlipped = true, // Always flipped in detail sheet so user immediately sees all readings and romaji
                    onFlip = {},
                    onReview = { isMastered ->
                        val newMastery = if (isMastered) 3 else 1
                        onUpdateMastery(activeKanji.id, newMastery)
                        selectedKanjiForDetail = activeKanji.copy(mastery = newMastery)
                    },
                    ttsManager = ttsManager
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Individual Kanji Tile in the Kanji Study Grid.
 * Displays character, Romaji reading, English meaning, stroke count, and mastery border.
 */
@Composable
fun KanjiGridTile(
    item: JapaneseItem,
    onClick: () -> Unit,
    ttsManager: JapaneseTtsManager? = null
) {
    val borderColor = when (item.mastery) {
        3 -> Color(0xFF66BB6A) // Green for mastered
        1, 2 -> Color(0xFFFFCA28) // Amber for learning
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Row: Stroke count & Mastery Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.strokeCount}画",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (item.mastery == 3) {
                    Text(text = "⭐", fontSize = 11.sp)
                } else if (item.mastery in 1..2) {
                    Text(text = "🌱", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Big Kanji Glyph
            Text(
                text = item.japanese,
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Romaji reading
            Text(
                text = item.romaji,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // English meaning
            Text(
                text = item.meaning,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
