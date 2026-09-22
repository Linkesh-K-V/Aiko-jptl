package com.example.noignore.ui.study

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.japanese.lesson.JapaneseLessonRepository
import com.example.noignore.japanese.model.JapaneseCategory
import com.example.noignore.japanese.model.JlptExamLevel
import com.example.noignore.ui.japanese.PracticeMode

@Composable
fun StudyTopicDrawerContent(
    onCloseDrawer: () -> Unit,
    onNavigateToLesson: (Int) -> Unit,
    onNavigateToCategoryPractice: (JapaneseCategory) -> Unit,
    onNavigateToMode: (PracticeMode) -> Unit,
    onNavigateToJlptTopics: (String) -> Unit = {},
    onNavigateToBackup: () -> Unit = {}
) {
    val context = LocalContext.current
    val repo = remember { JapaneseLessonRepository(context) }
    val lessons = remember { repo.allLessons }
    val currentLessonId = remember { repo.getCurrentLessonId() }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
            .verticalScroll(scrollState)
            .testTag("study_topic_drawer_content")
    ) {
        // Drawer Header with Flower Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🌸", fontSize = 22.sp)
                }
                Column {
                    Text(
                        text = "Study Topics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "愛子先生の学習ライブラリ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(
                onClick = onCloseDrawer,
                modifier = Modifier.testTag("close_topic_drawer_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Topics Drawer"
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // HERO FEATURE: BROWSE BY JLPT LEVEL TOPICS (N5 to N1)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    onCloseDrawer()
                    onNavigateToJlptTopics("N5")
                }
                .testTag("drawer_jlpt_topics_banner"),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp)
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
                        Text("🏆", fontSize = 18.sp)
                        Text(
                            text = "JLPT Topics by Level",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Browse what topics & formulas are in N5, N4, N3, N2, N1 with plain explanations",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Quick Level Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    JlptExamLevel.entries.forEach { lvl ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(lvl.colorHex).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(lvl.colorHex).copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onCloseDrawer()
                                    onNavigateToJlptTopics(lvl.code)
                                }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lvl.code,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(lvl.colorHex)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 1: CURRICULUM LESSONS (1 to 8)
        DrawerSectionHeader(title = "CURRICULUM LESSONS", subtitle = "Structured path with recap tests")

        lessons.forEach { lesson ->
            val isCurrent = lesson.id == currentLessonId
            val isCompleted = repo.isLessonCompleted(lesson.id)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        onCloseDrawer()
                        onNavigateToLesson(lesson.id)
                    }
                    .testTag("topic_lesson_${lesson.id}"),
                shape = RoundedCornerShape(14.dp),
                color = when {
                    isCurrent -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    else -> MaterialTheme.colorScheme.surface
                },
                border = BorderStroke(
                    1.dp,
                    if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = lesson.iconEmoji, fontSize = 20.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Lesson ${lesson.lessonNumber}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (isCurrent) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = "CURRENT",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            } else if (isCompleted) {
                                Text(
                                    text = "✓ DONE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                        Text(
                            text = lesson.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 2: TOPICS BY SUBJECT
        DrawerSectionHeader(title = "TOPICS BY SUBJECT", subtitle = "Direct subject focus")

        TopicItemRow(
            emoji = "🈸",
            title = "Kanji by Level (漢字)",
            subtitle = "N5 to N1 characters, radicals & stroke order",
            testTag = "topic_item_kanji",
            onClick = {
                onCloseDrawer()
                onNavigateToCategoryPractice(JapaneseCategory.KANJI)
            }
        )

        TopicItemRow(
            emoji = "📖",
            title = "Vocabulary Themes (語彙)",
            subtitle = "Greetings, food, travel, time & daily life",
            testTag = "topic_item_vocab",
            onClick = {
                onCloseDrawer()
                onNavigateToCategoryPractice(JapaneseCategory.VOCAB)
            }
        )

        TopicItemRow(
            emoji = "⛩️",
            title = "Grammar Patterns (文法)",
            subtitle = "Particles, verb conjugations & sentences",
            testTag = "topic_item_grammar",
            onClick = {
                onCloseDrawer()
                onNavigateToCategoryPractice(JapaneseCategory.GRAMMAR)
            }
        )

        TopicItemRow(
            emoji = "⚡",
            title = "Daily Mission & 5-Min Sprint",
            subtitle = "Targeted SRS reviews & streak safeguard",
            testTag = "topic_item_daily_mission",
            onClick = {
                onCloseDrawer()
                onNavigateToMode(PracticeMode.DAILY_MISSION)
            }
        )

        TopicItemRow(
            emoji = "🔄",
            title = "Conjugation Trainer (動詞・形容詞活用)",
            subtitle = "Godan, Ichidan & irregular inflection drills",
            testTag = "topic_item_conjugation",
            onClick = {
                onCloseDrawer()
                onNavigateToMode(PracticeMode.CONJUGATION_DRILL)
            }
        )

        TopicItemRow(
            emoji = "🐛",
            title = "Leech Remedy (弱点克服)",
            subtitle = "Target cards with 3+ memory lapses",
            testTag = "topic_item_leech",
            onClick = {
                onCloseDrawer()
                onNavigateToMode(PracticeMode.LEECH_REMEDY)
            }
        )

        TopicItemRow(
            emoji = "🎧",
            title = "Listening Lessons (聴解)",
            subtitle = "Conversations & realistic audio drills",
            testTag = "topic_item_listening",
            onClick = {
                onCloseDrawer()
                onNavigateToMode(PracticeMode.LISTENING_LESSON)
            }
        )

        TopicItemRow(
            emoji = "📚",
            title = "Stories & Graded Reading (読解)",
            subtitle = "Folktales and reading comprehension",
            testTag = "topic_item_reading",
            onClick = {
                onCloseDrawer()
                onNavigateToMode(PracticeMode.STORIES)
            }
        )

        TopicItemRow(
            emoji = "🎯",
            title = "JLPT Exam Blitz (模擬試験)",
            subtitle = "Timed mock exam test drills",
            testTag = "topic_item_exam",
            onClick = {
                onCloseDrawer()
                onNavigateToMode(PracticeMode.JLPT_MOCK_EXAM)
            }
        )

        TopicItemRow(
            emoji = "🔄",
            title = "SRS Spaced Repetition Review (復習)",
            subtitle = "Human memory retention review queue",
            testTag = "topic_item_srs",
            onClick = {
                onCloseDrawer()
                onNavigateToMode(PracticeMode.FLASHCARDS)
            }
        )

        TopicItemRow(
            emoji = "💾",
            title = "Backup & Data Integrity (バックアップ)",
            subtitle = "1-Click JSON export & restore of SRS states",
            testTag = "topic_item_backup",
            onClick = {
                onCloseDrawer()
                onNavigateToBackup()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DrawerSectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TopicItemRow(
    emoji: String,
    title: String,
    subtitle: String,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 18.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
