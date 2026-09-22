package com.example.noignore.ui.japanese.study

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.audio.JapaneseTtsManager
import com.example.noignore.japanese.engine.ConjugationEngine
import com.example.noignore.japanese.engine.ConjugationResult
import com.example.noignore.japanese.engine.VerbGroup
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.ui.japanese.audio.JapaneseSpeakerButton

@Composable
fun ConjugationDrillCard(
    item: JapaneseItem,
    ttsManager: JapaneseTtsManager?,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val verbGroup = remember(item.japanese) {
        ConjugationEngine.determineVerbGroup(item.japanese)
    }

    val allConjugations = remember(item.japanese) {
        ConjugationEngine.getAllConjugations(item.japanese, item.reading)
    }

    // Pick a random target form for the drill
    val targetIndex = remember(item.japanese) {
        if (allConjugations.isNotEmpty()) (0 until allConjugations.size).random() else 0
    }
    val target = remember(item.japanese, targetIndex) {
        if (allConjugations.isNotEmpty()) allConjugations[targetIndex] else ConjugationResult("Te-form", item.japanese, item.reading, "", "")
    }

    // Generate 3 plausible distractors from other conjugations or variations
    val options = remember(item.japanese, target) {
        val others = allConjugations.filter { it.conjugated != target.conjugated }.map { it.conjugated }.shuffled().take(3)
        (others + target.conjugated).shuffled()
    }

    var selectedAnswer by remember(item.japanese) { mutableStateOf<String?>(null) }
    var isSubmitted by remember(item.japanese) { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Category and Verb Group
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Conjugation Trainer",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    val groupText = when (verbGroup) {
                        VerbGroup.GODAN -> "五段 (Godan)"
                        VerbGroup.ICHIDAN -> "一段 (Ichidan)"
                        VerbGroup.KURU_IRREGULAR -> "来る (Kuru)"
                        VerbGroup.SURU_IRREGULAR -> "する (Suru)"
                    }
                    Text(
                        text = groupText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dictionary Word Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = item.reading,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.japanese,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.meaning,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                JapaneseSpeakerButton(
                    textToSpeak = item.japanese,
                    ttsManager = ttsManager
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prompt
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Target Form:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Transform into 「${target.formName}」",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = target.englishRule,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options
            options.forEach { option ->
                val isSelected = selectedAnswer == option
                val isCorrectAnswer = option == target.conjugated

                val btnColor = when {
                    !isSubmitted && isSelected -> MaterialTheme.colorScheme.primary
                    isSubmitted && isCorrectAnswer -> Color(0xFF2E7D32)
                    isSubmitted && isSelected && !isCorrectAnswer -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.surface
                }

                val txtColor = when {
                    !isSubmitted && isSelected -> MaterialTheme.colorScheme.onPrimary
                    isSubmitted && (isCorrectAnswer || isSelected) -> Color.White
                    else -> MaterialTheme.colorScheme.onSurface
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = btnColor,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    onClick = {
                        if (!isSubmitted) {
                            selectedAnswer = option
                            isSubmitted = true
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = txtColor
                        )
                        if (isSubmitted && isCorrectAnswer) {
                            Text("正解 (Correct)", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }
                }
            }

            // Explanatory Result & Next Button
            if (isSubmitted) {
                Spacer(modifier = Modifier.height(12.dp))
                val isSuccess = selectedAnswer == target.conjugated
                Text(
                    text = if (isSuccess) "正解！ (${target.reading} - ${target.englishRule})" else "正解は 「${target.conjugated}」 (${target.reading}) です。",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSuccess) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Next Conjugation →")
                }
            }
        }
    }
}
