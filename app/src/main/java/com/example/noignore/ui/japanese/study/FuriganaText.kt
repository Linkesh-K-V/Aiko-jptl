package com.example.noignore.ui.japanese.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.japanese.engine.FuriganaEngine
import com.example.noignore.japanese.engine.FuriganaSegment

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FuriganaText(
    surface: String,
    reading: String = "",
    mode: String = "ALWAYS_SHOW", // "ALWAYS_SHOW", "NEVER_SHOW"
    fontSize: TextUnit = 24.sp,
    rubyFontSize: TextUnit = 11.sp,
    color: Color = MaterialTheme.colorScheme.onSurface,
    rubyColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    val segments = remember(surface, reading) {
        if (reading.isNotBlank()) {
            FuriganaEngine.alignWord(surface, reading)
        } else if (surface.contains("[")) {
            FuriganaEngine.parseBracketedFurigana(surface)
        } else {
            listOf(FuriganaSegment(surface, null))
        }
    }

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalArrangement = Arrangement.Center
    ) {
        segments.forEach { segment ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 1.dp)
            ) {
                // Ruby line
                if (mode != "NEVER_SHOW" && segment.reading != null) {
                    Text(
                        text = segment.reading,
                        fontSize = rubyFontSize,
                        color = rubyColor,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = rubyFontSize
                    )
                } else if (mode != "NEVER_SHOW") {
                    // Spacer placeholder to maintain vertical baseline alignment
                    Text(
                        text = " ",
                        fontSize = rubyFontSize,
                        lineHeight = rubyFontSize
                    )
                }

                // Surface kanji/kana
                Text(
                    text = segment.surface,
                    fontSize = fontSize,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    lineHeight = fontSize
                )
            }
        }
    }
}
