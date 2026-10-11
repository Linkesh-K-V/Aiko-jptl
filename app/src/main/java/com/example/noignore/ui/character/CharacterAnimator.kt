package com.example.noignore.ui.character

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.R

@Composable
fun CharacterAnimator(
    state: CharacterState,
    modifier: Modifier = Modifier
) {
    val rawRes = when (state) {
        CharacterState.IDLE -> R.raw.char_idle
        CharacterState.NEUTRAL -> R.raw.char_neutral
        CharacterState.HAPPY -> R.raw.char_happy
        CharacterState.PROUD -> R.raw.char_proud
        CharacterState.DISAPPOINTED -> R.raw.char_disappointed
        CharacterState.ANGRY -> R.raw.char_angry
        CharacterState.SHAME -> R.raw.char_shame
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize()
        )
    }
}
