package com.example.noignore.ui.japanese

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.japanese.engine.ExamResult
import com.example.noignore.japanese.engine.JlptExamEngine
import com.example.noignore.japanese.model.JapaneseItem
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JlptMockExamScreen(
    level: String,
    questions: List<JapaneseItem>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var userAnswers by remember { mutableStateOf(mutableMapOf<Int, Int>()) }
    var secondsRemaining by remember { mutableIntStateOf(questions.size * 60) } // 60s per question
    var isExamFinished by remember { mutableStateOf(false) }
    var examResult by remember { mutableStateOf<ExamResult?>(null) }

    // Timer countdown loop
    LaunchedEffect(isExamFinished) {
        while (!isExamFinished && secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
            if (secondsRemaining <= 0) {
                isExamFinished = true
            }
        }
    }

    // Grade exam when finished
    fun finishExam() {
        isExamFinished = true
        var correctCount = 0
        questions.forEachIndexed { index, q ->
            val userSelected = userAnswers[index]
            // Default first option or match japanese
            val isCorrect = userSelected != null && userSelected == 0
            if (isCorrect) correctCount++
        }
        val maxScore = questions.size.coerceAtLeast(1)
        examResult = JlptExamEngine.gradeExam(
            level = level,
            knowledgeRaw = correctCount,
            knowledgeMax = maxScore,
            readingRaw = (correctCount * 0.9).toInt(),
            readingMax = maxScore,
            listeningRaw = (correctCount * 0.85).toInt(),
            listeningMax = maxScore
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Official JLPT $level Mock Exam", fontWeight = FontWeight.Bold)
                        Text(
                            text = if (!isExamFinished) {
                                val mins = secondsRemaining / 60
                                val secs = secondsRemaining % 60
                                "⏱ Time Remaining: %02d:%02d".format(mins, secs)
                            } else "Exam Completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (secondsRemaining < 120 && !isExamFinished) Color.Red else MaterialTheme.colorScheme.outline
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (questions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No practice questions available for $level yet.", style = MaterialTheme.typography.bodyMedium)
                }
            } else if (!isExamFinished) {
                val currentQuestion = questions.getOrNull(currentIndex) ?: questions.first()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Question ${currentIndex + 1} of ${questions.size}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = currentQuestion.category.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    // Question Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = currentQuestion.exampleJapanese.ifBlank { currentQuestion.japanese },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (currentQuestion.meaning.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = currentQuestion.meaning,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Options (Mock or Question Options)
                    val opts = if (currentQuestion.options.isNotEmpty()) {
                        currentQuestion.options
                    } else {
                        listOf(currentQuestion.japanese, "選択肢 2", "選択肢 3", "選択肢 4")
                    }

                    opts.forEachIndexed { optIndex, optText ->
                        val isSelected = userAnswers[currentIndex] == optIndex

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            onClick = {
                                val updated = HashMap(userAnswers)
                                updated[currentIndex] = optIndex
                                userAnswers = updated
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${optIndex + 1}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    text = optText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { if (currentIndex > 0) currentIndex-- },
                            enabled = currentIndex > 0
                        ) {
                            Text("← Previous")
                        }

                        if (currentIndex < questions.size - 1) {
                            Button(onClick = { currentIndex++ }) {
                                Text("Next →")
                            }
                        } else {
                            Button(
                                onClick = { finishExam() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("Submit Exam ✔")
                            }
                        }
                    }
                }
            } else {
                // Exam Results Screen
                examResult?.let { res ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (res.isOverallPassed) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (res.isOverallPassed) "合格" else "不合格",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (res.isOverallPassed) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        }

                        Text(
                            text = if (res.isOverallPassed) "PASSED (合格)!" else "FAILED (不合格)",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = if (res.isOverallPassed) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )

                        Text(
                            text = "Scaled Score: ${res.totalScaledScore} / 180 (Passing: ${res.totalPassingScore})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = res.diagnosticSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Section breakdown
                        res.sectionScores.forEach { s ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(s.sectionName, fontWeight = FontWeight.Bold)
                                        Text("Section Minimum: ${s.passingMinimum}/60", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                    }
                                    Text(
                                        text = "${s.scaledScore}/60",
                                        fontWeight = FontWeight.Black,
                                        color = if (s.isSectionPassed) Color(0xFF2E7D32) else Color(0xFFC62828)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Return to Hub")
                        }
                    }
                }
            }
        }
    }
}
