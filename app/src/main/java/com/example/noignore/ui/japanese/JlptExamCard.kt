package com.example.noignore.ui.japanese

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
fun JlptExamCard(
    item: JapaneseItem,
    questionNumber: Int,
    totalQuestions: Int,
    quizState: QuizState,
    onSelectOption: (optionIndex: Int, isCorrect: Boolean) -> Unit,
    onNext: () -> Unit,
    onAskSenseiGrounded: (() -> Unit)? = null,
    srsData: SrsCardData? = null,
    onRateSrs: ((AnkiRating) -> Unit)? = null,
    ttsManager: JapaneseTtsManager? = null
) {
    // Shuffled display options mapping. In JapaneseItem definition, options[0] is the correct answer.
    val (displayOptions, correctOptionIndex) = remember(item.id) {
        if (item.options.isNotEmpty()) {
            val original = item.options
            val correctAnswer = original[0]
            val shuffled = original.shuffled()
            shuffled to shuffled.indexOf(correctAnswer)
        } else {
            listOf("A", "B", "C", "D") to 0
        }
    }

    val isStarQuestion = item.examQuestionType.contains("組み立て") || item.examQuestionType.contains("★")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            // Header: Section type & Question indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🎯", fontSize = 12.sp)
                        Text(
                            text = "JLPT ${item.jlptLevel} MOCK",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Score: ${quizState.totalCorrect} / ${quizState.totalAnswered}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-category (e.g. Kanji Reading, Orthography, Grammar, Star question)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.examQuestionType.ifBlank { "Section: Language Knowledge (言語知識)" },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )

                if (isStarQuestion) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFF3E0),
                        border = BorderStroke(1.dp, Color(0xFFFFB74D))
                    ) {
                        Text(
                            text = "★ Star Question",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Context / Prompt Sentence
            val promptText = item.examQuestionPrompt.ifBlank { "次の文の（　）に入る最もよいものを、一つえらんでください。" }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = promptText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 26.sp
                        )

                        if (item.meaning.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Target: ${item.japanese} (${item.meaning})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    JapaneseSpeakerButton(
                        ttsManager = ttsManager,
                        textToSpeak = item.japanese.ifBlank { promptText },
                        size = 36.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = if (isStarQuestion) "SELECT THE PART THAT GOES INTO [ ★ ]:" else "CHOOSE THE BEST ANSWER (1〜4):",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4 Options
            displayOptions.forEachIndexed { index, option ->
                val isSelected = quizState.selectedOptionIndex == index
                val isCorrectAnswer = index == correctOptionIndex

                val (btnColor, textColor, borderColor) = when {
                    !quizState.isSubmitted -> {
                        Triple(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
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
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable(enabled = !quizState.isSubmitted) {
                            onSelectOption(index, index == correctOptionIndex)
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = btnColor),
                    border = BorderStroke(1.dp, borderColor)
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
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected || (quizState.isSubmitted && isCorrectAnswer)) FontWeight.Bold else FontWeight.Normal,
                                color = textColor
                            )
                        }

                        if (quizState.isSubmitted) {
                            if (isCorrectAnswer) {
                                Text("正解 (Correct!)", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 12.sp)
                            } else if (isSelected) {
                                Text("不正解 (Incorrect)", fontWeight = FontWeight.Bold, color = Color(0xFFC62828), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ask Sensei (Google Search Grounded) Button
            if (onAskSenseiGrounded != null) {
                OutlinedButton(
                    onClick = onAskSenseiGrounded,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFF4285F4)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFE8F0FE).copy(alpha = 0.5f),
                        contentColor = Color(0xFF1967D2)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🌐", fontSize = 14.sp)
                        Text(
                            "Ask Sensei (Google Search Grounded)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            if (quizState.isSubmitted) {
                if (item.mnemonicOrNote.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "💡 EXAM EXPLANATION:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.mnemonicOrNote,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                EbbinghausMemoryCurveView(srsData = srsData)

                if (onRateSrs != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    AnkiSrsRatingBar(
                        srsData = srsData,
                        onRate = { rating ->
                            onRateSrs(rating)
                        },
                        title = "Anki Brain Retention Recall (Optional Override)"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        if (questionNumber >= totalQuestions) "View Exam Results ➔" else "Next Question (${questionNumber + 1}/$totalQuestions) ➔",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
