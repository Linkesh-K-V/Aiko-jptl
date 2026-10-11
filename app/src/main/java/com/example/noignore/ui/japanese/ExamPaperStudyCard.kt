package com.example.noignore.ui.japanese

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.audio.JapaneseTtsManager
import com.example.noignore.japanese.model.AnkiRating
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.japanese.model.SrsCardData
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton
import com.example.noignore.ui.japanese.srs.AnkiSrsRatingBar
import com.example.noignore.ui.japanese.srs.EbbinghausMemoryCurveView

@Composable
fun ExamPaperStudyCard(
    item: JapaneseItem,
    questionNumber: Int,
    totalQuestions: Int,
    srsData: SrsCardData?,
    onRateRecall: (AnkiRating) -> Unit,
    onAskSenseiGrounded: (() -> Unit)? = null,
    ttsManager: JapaneseTtsManager? = null,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val paperBg = if (isDark) Color(0xFF1E2024) else Color(0xFFFAF8F2)
    val paperBorder = if (isDark) Color(0xFF373B41) else Color(0xFFE2DDD1)
    val paperInk = if (isDark) Color(0xFFECEFF4) else Color(0xFF1F2328)
    val stampRed = Color(0xFFC62828)

    // Option state: In JapaneseItem, options[0] is the authentic correct answer
    val (displayOptions, correctOptionIndex) = remember(item.id) {
        if (item.options.isNotEmpty()) {
            val original = item.options
            val correctAnswer = original[0]
            val shuffled = original.shuffled()
            shuffled to shuffled.indexOf(correctAnswer)
        } else {
            listOf("Option 1", "Option 2", "Option 3", "Option 4") to 0
        }
    }

    var selectedChoiceIndex by remember(item.id) { mutableStateOf<Int?>(null) }
    var isAnswerRevealed by remember(item.id) { mutableStateOf(false) }

    val sectionInstruction = remember(item.examQuestionType) {
        when {
            item.examQuestionType.contains("読み") ->
                "【問題１】＿＿の言葉の読み方として最もよいものを、１・２・３・４から一つ選びなさい。"
            item.examQuestionType.contains("表記") ->
                "【問題２】＿＿の言葉を漢字で書くとき、最もよいものを、１・２・３・４から一つ選びなさい。"
            item.examQuestionType.contains("文脈") || item.examQuestionType.contains("文法") ->
                "【問題３】（　）に入る最もよいものを、１・２・３・４から一つ選びなさい。"
            item.examQuestionType.contains("★") || item.examQuestionType.contains("組み立て") ->
                "【問題４】次の文の★に入る最もよいものを、１・２・３・４から一つ選びなさい。"
            item.examQuestionType.contains("読解") ->
                "【問題５】次の文章を読んで、後の問いに対する答えとして最もよいものを、１・２・３・４から一つ選びなさい。"
            else ->
                "【問題】次の問いに対する答えとして最もよいものを、１・２・３・４から一つ選びなさい。"
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = paperBg),
        border = BorderStroke(2.dp, paperBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Paper Header with Official Stamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = stampRed.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, stampRed)
                        ) {
                            Text(
                                text = "実施問題用紙",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = stampRed,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "日本語能力試験 JLPT ${item.jlptLevel}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = paperInk
                        )
                    }
                    Text(
                        text = "EXAM PAPER • HUMAN BRAIN RETRIEVAL MODE",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp,
                        color = Color.Gray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "第 $questionNumber / $totalQuestions 問",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 1.dp, color = paperBorder)
            Spacer(modifier = Modifier.height(12.dp))

            // Section Instructions (Printed exam instruction)
            Text(
                text = sectionInstruction,
                style = MaterialTheme.typography.labelSmall,
                color = if (isDark) Color.LightGray else Color(0xFF555555),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Question Box (Paper Printed Prompt)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isDark) Color(0xFF282A30) else Color(0xFFF2EFE6),
                border = BorderStroke(1.dp, paperBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "問 $questionNumber",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = stampRed
                        )

                        JapaneseSpeakerButton(
                            ttsManager = ttsManager,
                            textToSpeak = item.examQuestionPrompt.ifBlank { item.japanese },
                            size = 32.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = item.examQuestionPrompt.ifBlank { item.japanese },
                        fontSize = 17.sp,
                        lineHeight = 27.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = paperInk,
                        fontFamily = FontFamily.Serif
                    )

                    if (item.reading.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "対象語: ${item.japanese} (${item.reading})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Multiple Choices (1, 2, 3, 4 like authentic exam paper)
            displayOptions.forEachIndexed { index, option ->
                val choiceNum = index + 1
                val isSelected = selectedChoiceIndex == index
                val isCorrect = index == correctOptionIndex

                val cardBg = when {
                    !isAnswerRevealed -> if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent
                    isCorrect -> Color(0xFFE8F5E9)
                    isSelected && !isCorrect -> Color(0xFFFFEBEE)
                    else -> Color.Transparent
                }

                val borderTone = when {
                    !isAnswerRevealed -> if (isSelected) MaterialTheme.colorScheme.primary else paperBorder
                    isCorrect -> Color(0xFF4CAF50)
                    isSelected && !isCorrect -> Color(0xFFE57373)
                    else -> paperBorder.copy(alpha = 0.5f)
                }

                Card(
                    onClick = {
                        if (!isAnswerRevealed) {
                            selectedChoiceIndex = index
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderTone)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Number circle 1, 2, 3, 4
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else paperBorder,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$choiceNum",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else paperInk
                                    )
                                }
                            }

                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected || (isAnswerRevealed && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                                color = paperInk
                            )
                        }

                        if (isAnswerRevealed) {
                            if (isCorrect) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF2E7D32)
                                ) {
                                    Text(
                                        text = "正答",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else if (isSelected) {
                                Text("✗", color = Color(0xFFC62828), fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step 1: Reveal Paper Solution & Brain Synapse
            if (!isAnswerRevealed) {
                Button(
                    onClick = { isAnswerRevealed = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🧠", fontSize = 16.sp)
                        Text(
                            text = "Reveal Answer & Brain Feedback (解答確認)",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Step 2: Answer Breakdown & Neuro-Memory Panel
                AnimatedVisibility(
                    visible = isAnswerRevealed,
                    enter = fadeIn() + expandVertically()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Official Explanation Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Lightbulb,
                                        contentDescription = "Explanation",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "EXAM PAPER SOLUTION & LINGUISTIC NOTES",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.mnemonicOrNote.ifBlank {
                                        "Correct Answer: ${displayOptions[correctOptionIndex]}.\nMeaning: ${item.meaning}"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = paperInk,
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        // Human Brain Ebbinghaus Curve & Retention Metrics
                        EbbinghausMemoryCurveView(srsData = srsData)

                        // Grounded Sensei Search Button
                        if (onAskSenseiGrounded != null) {
                            OutlinedButton(
                                onClick = onAskSenseiGrounded,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFF4285F4))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("🌐", fontSize = 13.sp)
                                    Text(
                                        text = "Ask Sensei Official Grounded Intel",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF1967D2),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        HorizontalDivider(thickness = 1.dp, color = paperBorder)

                        // Anki SM-2 4-Button Grading Bar
                        AnkiSrsRatingBar(
                            srsData = srsData,
                            onRate = onRateRecall,
                            title = "Grade Your Brain's Recall Honesty (Anki SM-2)"
                        )
                    }
                }
            }
        }
    }
}
