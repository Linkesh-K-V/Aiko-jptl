package com.example.noignore.ui.japanese.srs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.japanese.model.AnkiRating
import com.example.noignore.japanese.model.SrsCardData

@Composable
fun AnkiSrsRatingBar(
    srsData: SrsCardData?,
    onRate: (AnkiRating) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Rate Your Brain's Memory Recall (FSRS v4.5)"
) {
    val cardData = srsData ?: SrsCardData(itemId = "")

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("🧠", fontSize = 14.sp)
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Again
            AnkiRateButton(
                rating = AnkiRating.AGAIN,
                intervalText = cardData.estimateNextIntervalLabel(AnkiRating.AGAIN),
                containerColor = Color(0xFFFFEBEE),
                borderColor = Color(0xFFE57373),
                textColor = Color(0xFFC62828),
                badgeColor = Color(0xFFD32F2F),
                onClick = { onRate(AnkiRating.AGAIN) },
                modifier = Modifier.weight(1f)
            )

            // Hard
            AnkiRateButton(
                rating = AnkiRating.HARD,
                intervalText = cardData.estimateNextIntervalLabel(AnkiRating.HARD),
                containerColor = Color(0xFFFFF3E0),
                borderColor = Color(0xFFFFB74D),
                textColor = Color(0xFFE65100),
                badgeColor = Color(0xFFF57C00),
                onClick = { onRate(AnkiRating.HARD) },
                modifier = Modifier.weight(1f)
            )

            // Good
            AnkiRateButton(
                rating = AnkiRating.GOOD,
                intervalText = cardData.estimateNextIntervalLabel(AnkiRating.GOOD),
                containerColor = Color(0xFFE8F5E9),
                borderColor = Color(0xFF81C784),
                textColor = Color(0xFF2E7D32),
                badgeColor = Color(0xFF388E3C),
                onClick = { onRate(AnkiRating.GOOD) },
                modifier = Modifier.weight(1f)
            )

            // Easy
            AnkiRateButton(
                rating = AnkiRating.EASY,
                intervalText = cardData.estimateNextIntervalLabel(AnkiRating.EASY),
                containerColor = Color(0xFFE3F2FD),
                borderColor = Color(0xFF64B5F6),
                textColor = Color(0xFF1565C0),
                badgeColor = Color(0xFF1976D2),
                onClick = { onRate(AnkiRating.EASY) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AnkiRateButton(
    rating: AnkiRating,
    intervalText: String,
    containerColor: Color,
    borderColor: Color,
    textColor: Color,
    badgeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(68.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Next Interval Tag
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = badgeColor
            ) {
                Text(
                    text = intervalText,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = rating.label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
