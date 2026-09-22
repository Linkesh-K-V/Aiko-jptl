package com.example.noignore.ui.character

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale as drawScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.character.CharacterLevel
import com.example.noignore.character.CharacterMood
import com.example.noignore.character.personality.CharacterPersonality
import com.example.noignore.character.personality.PersonalityTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * High-Scale, 3D-Shaded Original Anime Japanese Sensei Character Companion (Aoi Sensei - 葵先生).
 * 100% Original artwork crafted with vector geometry, depth gradients, soft ambient lighting,
 * responsive physics, touch zones, speech dialogue, and Japanese language coaching prompts.
 */
@Composable
fun AoiSenseiFullAnimeCharacter(
    mood: CharacterMood,
    streakCount: Int,
    level: CharacterLevel = CharacterLevel.BEGINNER,
    initialDialogue: String? = null,
    personality: CharacterPersonality = CharacterPersonality.SENSEI,
    personalityTheme: PersonalityTheme? = null,
    onPersonalityClick: () -> Unit = {},
    onStudyClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var temporaryExpression by remember { mutableStateOf<AnimeExpression?>(null) }

    val baseExpression = remember(mood) {
        when (mood) {
            CharacterMood.CALM -> AnimeExpression.IDLE_HAPPY
            CharacterMood.PROUD -> AnimeExpression.MOTIVATED_FIERY
            CharacterMood.DISAPPOINTED -> AnimeExpression.DISAPPOINTED_SWEAT
            CharacterMood.CONCERNED -> AnimeExpression.POUT_POKED
            CharacterMood.SILENT -> AnimeExpression.SMUG_SAGE
        }
    }

    val currentExpression = temporaryExpression ?: baseExpression

    // Physics squashing, breathing & 3D tilt
    val scaleXAnim = remember { Animatable(1f) }
    val scaleYAnim = remember { Animatable(1f) }
    val rotationAnim = remember { Animatable(0f) }
    val tiltAnim = remember { Animatable(0f) }

    // Particles system for interactive rewards (sakura petals, stars, hearts)
    val particles = remember { mutableStateListOf<AnimeParticle>() }

    // Infinite breathing cycle
    val infiniteTransition = rememberInfiniteTransition(label = "anime_idle")
    val breathingOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    // Gentle hair wind sway
    val hairSway by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hair_sway"
    )

    // Blinking animation
    val idleBlink by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blinking"
    )

    // Current dialogue state
    var currentDialogue by remember { mutableStateOf(initialDialogue ?: getDefaultSenseiDialogue(streakCount)) }

    LaunchedEffect(initialDialogue) {
        if (!initialDialogue.isNullOrBlank()) {
            currentDialogue = initialDialogue
        }
    }

    fun spawnParticles(type: String, count: Int = 8) {
        particles.clear()
        for (i in 0 until count) {
            val emoji = when (type) {
                "blush" -> listOf("🌸", "💖", "✨", "🌸").random()
                "poke" -> listOf("💢", "❓", "💦", "🥺").random()
                "inspire" -> listOf("🔥", "⚡", "⭐", "🎌", "💯").random()
                else -> listOf("🌸", "📚", "🇯🇵", "💬").random()
            }
            particles.add(
                AnimeParticle(
                    emoji = emoji,
                    initialX = Random.nextInt(-110, 110).toFloat(),
                    initialY = Random.nextInt(-140, 20).toFloat(),
                    velocityX = Random.nextFloat() * 4 - 2,
                    velocityY = -Random.nextFloat() * 4 - 2
                )
            )
        }
    }

    fun triggerHeadpat() {
        coroutineScope.launch {
            temporaryExpression = AnimeExpression.BLUSHING_HEADPAT
            currentDialogue = getSenseiHeadpatDialogue()
            spawnParticles("blush", count = 9)

            // Delight squash animation
            scaleXAnim.animateTo(1.08f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            scaleYAnim.animateTo(0.92f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            rotationAnim.animateTo(3f, tween(150))
            rotationAnim.animateTo(-3f, tween(150))
            rotationAnim.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
            scaleXAnim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            scaleYAnim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))

            delay(3200)
            temporaryExpression = null
            currentDialogue = getDefaultSenseiDialogue(streakCount)
        }
    }

    fun triggerCheekPoke(isLeftCheek: Boolean) {
        coroutineScope.launch {
            temporaryExpression = AnimeExpression.POUT_POKED
            currentDialogue = getSenseiPokeDialogue()
            spawnParticles("poke", count = 6)

            // Asymmetric recoil
            val tilt = if (isLeftCheek) -8f else 8f
            rotationAnim.animateTo(tilt, spring(dampingRatio = Spring.DampingRatioHighBouncy))
            scaleXAnim.animateTo(0.94f, tween(120))
            scaleYAnim.animateTo(1.06f, tween(120))
            rotationAnim.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            scaleXAnim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
            scaleYAnim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy))

            delay(3200)
            temporaryExpression = null
            currentDialogue = getDefaultSenseiDialogue(streakCount)
        }
    }

    fun triggerMotivation() {
        coroutineScope.launch {
            temporaryExpression = AnimeExpression.MOTIVATED_FIERY
            currentDialogue = getSenseiMotivationDialogue(streakCount)
            spawnParticles("inspire", count = 10)

            // Energetic jump animation
            scaleXAnim.animateTo(1.07f, tween(100))
            scaleYAnim.animateTo(1.12f, tween(100))
            delay(150)
            scaleXAnim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            scaleYAnim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))

            delay(3600)
            temporaryExpression = null
            currentDialogue = getDefaultSenseiDialogue(streakCount)
        }
    }

    fun triggerChat() {
        coroutineScope.launch {
            currentDialogue = getSenseiChatDialogue(streakCount)
            spawnParticles("chat", count = 6)
            rotationAnim.animateTo(-2f, tween(120))
            rotationAnim.animateTo(2f, tween(120))
            rotationAnim.animateTo(0f, tween(120))
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(28.dp), spotColor = Color(0x33D81B60)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(2.dp, Color(0xFFF06292).copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, start = 18.dp, end = 18.dp, bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Sensei Name Banner, Japanese Sensei Level Badge & Persona Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sensei Identity Pill
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFCE4EC),
                    border = BorderStroke(1.dp, Color(0xFFF48FB1))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🌸", fontSize = 14.sp)
                        Text(
                            text = "愛子先生 (Teacher Aiko)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFC2185B)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick Persona / Japanese Theme switch
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.clickable { onPersonalityClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "🥋", fontSize = 12.sp)
                            Text(
                                text = personality.displayName.substringBefore(" "),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Study Habit Mood indicator
                    JapaneseSenseiMoodBadge(mood = mood)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle Tagline
            Text(
                text = "YOUR 3D JAPANESE LEARNING COMPANION • 継続は力なり",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Full Scale 3D Shaded Anime Character Canvas Stage
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .graphicsLayer {
                        scaleX = scaleXAnim.value
                        scaleY = scaleYAnim.value
                        rotationZ = rotationAnim.value
                        translationY = breathingOffset
                        cameraDistance = 12f * density
                    },
                contentAlignment = Alignment.Center
            ) {
                // Background Soft Aura / Japanese Sakura Radial Glow
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val auraBrush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFCE4EC).copy(alpha = 0.9f),
                            Color(0xFFF8BBD0).copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2f, size.height * 0.45f),
                        radius = size.width * 0.55f
                    )
                    drawCircle(brush = auraBrush, radius = size.width * 0.5f)
                }

                // Main High-Scale 3D Rendered Anime Sensei Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { offset ->
                                    val canvasW = size.width
                                    val canvasH = size.height
                                    val relX = offset.x / canvasW
                                    val relY = offset.y / canvasH

                                    when {
                                        // Tap on hair or sakura hairclip -> Headpat!
                                        relY < 0.36f -> triggerHeadpat()
                                        // Tap on left cheek
                                        relY in 0.36f..0.66f && relX < 0.38f -> triggerCheekPoke(isLeftCheek = true)
                                        // Tap on right cheek
                                        relY in 0.36f..0.66f && relX > 0.62f -> triggerCheekPoke(isLeftCheek = false)
                                        // Tap on textbook or chest -> Motivation & Japanese drill
                                        else -> triggerMotivation()
                                    }
                                },
                                onDoubleTap = {
                                    triggerMotivation()
                                }
                            )
                        }
                ) {
                    draw3DShadedAoiSensei(
                        expression = currentExpression,
                        blinkProgress = idleBlink,
                        hairSway = hairSway
                    )
                }

                // Interactive Floating Particles (Sakura petals, Sparks, Kanji)
                particles.forEach { particle ->
                    Text(
                        text = particle.emoji,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = particle.initialX.toInt(),
                                    y = particle.initialY.toInt()
                                )
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Speech Bubble with Sensei Wisdom & Japanese Context
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { triggerChat() },
                shape = RoundedCornerShape(18.dp),
                color = when (currentExpression) {
                    AnimeExpression.BLUSHING_HEADPAT -> Color(0xFFFFF0F5)
                    AnimeExpression.MOTIVATED_FIERY -> Color(0xFFFFF8E1)
                    AnimeExpression.POUT_POKED -> Color(0xFFFBE9E7)
                    AnimeExpression.DISAPPOINTED_SWEAT -> MaterialTheme.colorScheme.surfaceVariant
                    else -> Color(0xFFFCE4EC).copy(alpha = 0.5f)
                },
                border = BorderStroke(
                    1.5.dp,
                    when (currentExpression) {
                        AnimeExpression.BLUSHING_HEADPAT -> Color(0xFFF48FB1)
                        AnimeExpression.MOTIVATED_FIERY -> Color(0xFFFFB74D)
                        AnimeExpression.POUT_POKED -> Color(0xFFFF8A65)
                        else -> Color(0xFFF06292).copy(alpha = 0.4f)
                    }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("💬", fontSize = 14.sp)
                        Text(
                            text = "AOI SENSEI SAYS:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFC2185B)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Tap to chat",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "\"$currentDialogue\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Sensei Touch Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SenseiActionPill(
                    icon = "🌸",
                    label = "Headpat",
                    onClick = { triggerHeadpat() },
                    modifier = Modifier.weight(1f)
                )
                SenseiActionPill(
                    icon = "👈",
                    label = "Poke",
                    onClick = { triggerCheekPoke(isLeftCheek = true) },
                    modifier = Modifier.weight(1f)
                )
                SenseiActionPill(
                    icon = "🔥",
                    label = "Inspire",
                    onClick = { triggerMotivation() },
                    modifier = Modifier.weight(1f)
                )
                SenseiActionPill(
                    icon = "⛩️",
                    label = "Drill",
                    onClick = { onStudyClick() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SenseiActionPill(
    icon: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(vertical = 9.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun JapaneseSenseiMoodBadge(mood: CharacterMood) {
    val (bgColor, textColor, label) = when (mood) {
        CharacterMood.CALM -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Ready • 準備中")
        CharacterMood.PROUD -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "Proud • 誇らしい")
        CharacterMood.DISAPPOINTED -> Triple(Color(0xFFECEFF1), Color(0xFF455A64), "Concerned • 心配")
        CharacterMood.CONCERNED -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Strict • 厳格")
        CharacterMood.SILENT -> Triple(Color(0xFF212121), Color(0xFFEEEEEE), "Silent • 瞑想")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(textColor, CircleShape)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

/**
 * High-Scale 3D Shaded Anime Sensei Drawing Routine
 * Uses multi-layered gradients, specular highlights, rim lights, and distinct 3D anime proportions.
 */
private fun DrawScope.draw3DShadedAoiSensei(
    expression: AnimeExpression,
    blinkProgress: Float,
    hairSway: Float
) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    drawScale(scale = (size.minDimension / 215f), pivot = Offset(cx, cy)) {
        // 1. Volumetric Back Hair with 3D Depth Shading (Deep navy-violet gradient)
        val backHairGradient = Brush.verticalGradient(
            colors = listOf(Color(0xFF4A148C), Color(0xFF311B92), Color(0xFF1A237E))
        )

        // Left long flowing twin ponytail
        val leftHairTail = Path().apply {
            moveTo(cx - 56f, cy - 24f)
            cubicTo(cx - 105f + hairSway, cy + 10f, cx - 98f + hairSway, cy + 105f, cx - 68f, cy + 96f)
            cubicTo(cx - 50f, cy + 60f, cx - 44f, cy + 20f, cx - 44f, cy - 6f)
            close()
        }
        drawPath(leftHairTail, backHairGradient)

        // Left hair rim light for 3D depth
        val leftHairRim = Path().apply {
            moveTo(cx - 98f + hairSway, cy + 18f)
            cubicTo(cx - 104f + hairSway, cy + 50f, cx - 96f + hairSway, cy + 96f, cx - 72f, cy + 94f)
        }
        drawPath(leftHairRim, Color(0x66B388FF), style = Stroke(width = 3.5f, cap = StrokeCap.Round))

        // Right long flowing twin ponytail
        val rightHairTail = Path().apply {
            moveTo(cx + 56f, cy - 24f)
            cubicTo(cx + 105f - hairSway, cy + 10f, cx + 98f - hairSway, cy + 105f, cx + 68f, cy + 96f)
            cubicTo(cx + 50f, cy + 60f, cx + 44f, cy + 20f, cx + 44f, cy - 6f)
            close()
        }
        drawPath(rightHairTail, backHairGradient)

        // Right hair rim light
        val rightHairRim = Path().apply {
            moveTo(cx + 98f - hairSway, cy + 18f)
            cubicTo(cx + 104f - hairSway, cy + 50f, cx + 96f - hairSway, cy + 96f, cx + 72f, cy + 94f)
        }
        drawPath(rightHairRim, Color(0x66B388FF), style = Stroke(width = 3.5f, cap = StrokeCap.Round))

        // 2. Sensei Torso & Japanese Teacher Blazer Uniform with 3D Lighting
        val blazerBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF283593), Color(0xFF1A237E), Color(0xFF0D1B2A))
        )
        val torsoPath = Path().apply {
            moveTo(cx - 48f, cy + 70f)
            lineTo(cx + 48f, cy + 70f)
            lineTo(cx + 62f, size.height)
            lineTo(cx - 62f, size.height)
            close()
        }
        drawPath(torsoPath, blazerBrush)

        // White Teacher Shirt Inner & Collar
        val shirtPath = Path().apply {
            moveTo(cx - 30f, cy + 70f)
            lineTo(cx, cy + 92f)
            lineTo(cx + 30f, cy + 70f)
            lineTo(cx + 16f, cy + 70f)
            lineTo(cx, cy + 82f)
            lineTo(cx - 16f, cy + 70f)
            close()
        }
        drawPath(shirtPath, Color(0xFFFFFFFF))
        drawPath(shirtPath, Color(0x22000000), style = Stroke(width = 1.5f))

        // Japanese Sensei Red Ribbon Tie
        val tieBrush = Brush.verticalGradient(
            listOf(Color(0xFFE91E63), Color(0xFFC2185B))
        )
        val tiePath = Path().apply {
            moveTo(cx, cy + 84f)
            lineTo(cx - 14f, cy + 104f)
            lineTo(cx, cy + 98f)
            lineTo(cx + 14f, cy + 104f)
            close()
        }
        drawPath(tiePath, tieBrush)

        // Golden Sakura Cherry Blossom Button on collar
        drawCircle(Color(0xFFFFD54F), radius = 6f, center = Offset(cx, cy + 84f))
        drawCircle(Color(0xFFFFA000), radius = 3.5f, center = Offset(cx, cy + 84f))

        // Japanese Learning Textbook held in Sensei's hands ("日本語")
        val bookPath = Path().apply {
            moveTo(cx - 44f, cy + 94f)
            lineTo(cx + 10f, cy + 84f)
            lineTo(cx + 14f, cy + 118f)
            lineTo(cx - 40f, cy + 128f)
            close()
        }
        drawPath(bookPath, Brush.linearGradient(listOf(Color(0xFFE53935), Color(0xFFC62828))))
        // Gold book spine and edge
        drawLine(
            Color(0xFFFFD54F),
            Offset(cx - 44f, cy + 94f),
            Offset(cx - 40f, cy + 128f),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )

        // 3. Slender Anime Neck with Ambient Occlusion Shadow
        drawRect(
            color = Color(0xFFFFCCBC),
            topLeft = Offset(cx - 15f, cy + 50f),
            size = Size(30f, 24f)
        )
        // Shadow cast by chin onto neck
        drawOval(
            color = Color(0x33000000),
            topLeft = Offset(cx - 18f, cy + 55f),
            size = Size(36f, 10f)
        )

        // 4. Smooth 3D Shaded Face (Soft anime aesthetic with porcelain skin tones)
        val facePath = Path().apply {
            moveTo(cx - 52f, cy - 22f)
            cubicTo(cx - 54f, cy + 28f, cx - 38f, cy + 66f, cx, cy + 68f)
            cubicTo(cx + 38f, cy + 66f, cx + 54f, cy + 28f, cx + 52f, cy - 22f)
            cubicTo(cx + 46f, cy - 60f, cx - 46f, cy - 60f, cx - 52f, cy - 22f)
            close()
        }
        // Base skin
        drawPath(facePath, Color(0xFFFFF3E0))
        // 3D Rim lighting / side shading on face
        drawPath(
            facePath,
            Brush.horizontalGradient(
                colors = listOf(Color(0x11FF8A80), Color.Transparent, Color(0x18000000)),
                startX = cx - 54f,
                endX = cx + 54f
            )
        )

        // 5. Rosy Anime Blushing Cheeks with multi-toned warmth
        val blushAlpha = when (expression) {
            AnimeExpression.BLUSHING_HEADPAT -> 0.85f
            AnimeExpression.POUT_POKED -> 0.7f
            AnimeExpression.MOTIVATED_FIERY -> 0.55f
            else -> 0.4f
        }
        // Left cheek blush
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF8A80).copy(alpha = blushAlpha), Color.Transparent),
                center = Offset(cx - 30f, cy + 28f),
                radius = 16f
            ),
            radius = 16f,
            center = Offset(cx - 30f, cy + 28f)
        )
        // Left cute hash blush marks
        drawLine(Color(0xFFE57373).copy(alpha = blushAlpha), Offset(cx - 34f, cy + 24f), Offset(cx - 28f, cy + 32f), strokeWidth = 2.5f)
        drawLine(Color(0xFFE57373).copy(alpha = blushAlpha), Offset(cx - 29f, cy + 24f), Offset(cx - 23f, cy + 32f), strokeWidth = 2.5f)

        // Right cheek blush
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF8A80).copy(alpha = blushAlpha), Color.Transparent),
                center = Offset(cx + 30f, cy + 28f),
                radius = 16f
            ),
            radius = 16f,
            center = Offset(cx + 30f, cy + 28f)
        )
        // Right cute hash blush marks
        drawLine(Color(0xFFE57373).copy(alpha = blushAlpha), Offset(cx + 25f, cy + 24f), Offset(cx + 31f, cy + 32f), strokeWidth = 2.5f)
        drawLine(Color(0xFFE57373).copy(alpha = blushAlpha), Offset(cx + 30f, cy + 24f), Offset(cx + 36f, cy + 32f), strokeWidth = 2.5f)

        // 6. High-Luster 3D Anime Eyes (Large, expressive anime pupils with multi-level reflections)
        val isBlinking = blinkProgress < 0.08f

        when {
            expression == AnimeExpression.BLUSHING_HEADPAT -> {
                drawHappySenseiEye(cx - 26f, cy + 14f, isLeft = true)
                drawHappySenseiEye(cx + 26f, cy + 14f, isLeft = false)
            }
            expression == AnimeExpression.POUT_POKED -> {
                drawPoutSenseiEye(cx - 26f, cy + 14f, isLeft = true)
                drawPoutSenseiEye(cx + 26f, cy + 14f, isLeft = false)
            }
            isBlinking -> {
                drawLine(
                    Color(0xFF21153B),
                    start = Offset(cx - 38f, cy + 16f),
                    end = Offset(cx - 14f, cy + 16f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    Color(0xFF21153B),
                    start = Offset(cx + 14f, cy + 16f),
                    end = Offset(cx + 38f, cy + 16f),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
            else -> {
                drawLustrous3DAnimeEye(
                    centerX = cx - 26f,
                    centerY = cy + 14f,
                    isMotivated = expression == AnimeExpression.MOTIVATED_FIERY
                )
                drawLustrous3DAnimeEye(
                    centerX = cx + 26f,
                    centerY = cy + 14f,
                    isMotivated = expression == AnimeExpression.MOTIVATED_FIERY
                )
            }
        }

        // 7. Refined Eyebrows
        when (expression) {
            AnimeExpression.MOTIVATED_FIERY -> {
                drawLine(Color(0xFF4A148C), Offset(cx - 38f, cy - 3f), Offset(cx - 16f, cy + 2f), strokeWidth = 3.5f, cap = StrokeCap.Round)
                drawLine(Color(0xFF4A148C), Offset(cx + 38f, cy - 3f), Offset(cx + 16f, cy + 2f), strokeWidth = 3.5f, cap = StrokeCap.Round)
            }
            AnimeExpression.POUT_POKED -> {
                drawLine(Color(0xFF4A148C), Offset(cx - 36f, cy + 3f), Offset(cx - 15f, cy - 3f), strokeWidth = 3.5f, cap = StrokeCap.Round)
                drawLine(Color(0xFF4A148C), Offset(cx + 36f, cy + 3f), Offset(cx + 15f, cy - 3f), strokeWidth = 3.5f, cap = StrokeCap.Round)
            }
            AnimeExpression.BLUSHING_HEADPAT -> {
                drawLine(Color(0xFF4A148C), Offset(cx - 35f, cy - 6f), Offset(cx - 16f, cy - 3f), strokeWidth = 3f, cap = StrokeCap.Round)
                drawLine(Color(0xFF4A148C), Offset(cx + 35f, cy - 6f), Offset(cx + 16f, cy - 3f), strokeWidth = 3f, cap = StrokeCap.Round)
            }
            else -> {
                drawLine(Color(0xFF4A148C), Offset(cx - 36f, cy - 2f), Offset(cx - 16f, cy - 2f), strokeWidth = 3f, cap = StrokeCap.Round)
                drawLine(Color(0xFF4A148C), Offset(cx + 16f, cy - 2f), Offset(cx + 36f, cy - 2f), strokeWidth = 3f, cap = StrokeCap.Round)
            }
        }

        // 8. Dainty Anime Nose with soft shadow
        drawCircle(Color(0xFFFFAB91), radius = 2.5f, center = Offset(cx, cy + 29f))
        drawCircle(Color(0x33000000), radius = 1.5f, center = Offset(cx + 1f, cy + 30f))

        // 9. Charming Anime Mouth
        when (expression) {
            AnimeExpression.BLUSHING_HEADPAT -> {
                // Cheerful open singing mouth
                val openMouth = Path().apply {
                    moveTo(cx - 9f, cy + 40f)
                    quadraticBezierTo(cx, cy + 50f, cx + 9f, cy + 40f)
                    close()
                }
                drawPath(openMouth, Color(0xFFE53935))
                // Tongue highlight
                drawCircle(Color(0xFFFF8A80), radius = 4.5f, center = Offset(cx, cy + 44f))
            }
            AnimeExpression.POUT_POKED -> {
                // Cute cat-mouth wavy pout `w`
                val poutPath = Path().apply {
                    moveTo(cx - 9f, cy + 40f)
                    quadraticBezierTo(cx - 4.5f, cy + 36f, cx, cy + 40f)
                    quadraticBezierTo(cx + 4.5f, cy + 36f, cx + 9f, cy + 40f)
                }
                drawPath(poutPath, Color(0xFFC2185B), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
            }
            AnimeExpression.MOTIVATED_FIERY -> {
                // Confident Japanese teacher smile
                val smilePath = Path().apply {
                    moveTo(cx - 8f, cy + 39f)
                    quadraticBezierTo(cx + 4f, cy + 46f, cx + 10f, cy + 37f)
                }
                drawPath(smilePath, Color(0xFF21153B), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
            }
            else -> {
                // Sweet encouraging teacher smile
                val smilePath = Path().apply {
                    moveTo(cx - 7f, cy + 40f)
                    quadraticBezierTo(cx, cy + 44f, cx + 7f, cy + 40f)
                }
                drawPath(smilePath, Color(0xFF21153B), style = Stroke(width = 3f, cap = StrokeCap.Round))
            }
        }

        // 10. Front Anime Hair with Volumetric 3D Strands & Signature Light Halo
        val hairFrontBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF7E57C2), Color(0xFF5E35B1), Color(0xFF4527A0))
        )

        // Left face-framing hair bang
        val leftBang = Path().apply {
            moveTo(cx - 52f, cy - 32f)
            cubicTo(cx - 58f, cy - 10f, cx - 48f, cy + 22f, cx - 40f, cy + 26f)
            cubicTo(cx - 42f, cy + 8f, cx - 44f, cy - 14f, cx - 32f, cy - 24f)
            close()
        }
        drawPath(leftBang, hairFrontBrush)

        // Center stylized anime bangs (classic V-cut strands layered for 3D depth)
        val centerBangs = Path().apply {
            moveTo(cx - 40f, cy - 44f)
            lineTo(cx - 28f, cy - 6f)
            lineTo(cx - 20f, cy - 16f)
            lineTo(cx - 10f, cy + 2f)
            lineTo(cx, cy - 14f)
            lineTo(cx + 10f, cy + 4f)
            lineTo(cx + 20f, cy - 16f)
            lineTo(cx + 28f, cy - 4f)
            lineTo(cx + 40f, cy - 44f)
            close()
        }
        drawPath(centerBangs, hairFrontBrush)

        // Right face-framing hair bang
        val rightBang = Path().apply {
            moveTo(cx + 52f, cy - 32f)
            cubicTo(cx + 58f, cy - 10f, cx + 48f, cy + 22f, cx + 40f, cy + 26f)
            cubicTo(cx + 42f, cy + 8f, cx + 44f, cy - 14f, cx + 32f, cy - 24f)
            close()
        }
        drawPath(rightBang, hairFrontBrush)

        // Top crown hair with 3D volume
        val hairCrown = Path().apply {
            moveTo(cx - 52f, cy - 30f)
            cubicTo(cx - 56f, cy - 70f, cx + 56f, cy - 70f, cx + 52f, cy - 30f)
            cubicTo(cx + 34f, cy - 48f, cx - 34f, cy - 48f, cx - 52f, cy - 30f)
            close()
        }
        drawPath(hairCrown, hairFrontBrush)

        // Signature Anime Light Band (Angel Ring / 天使の輪 - 3D Specular Hair Shine)
        val haloPath = Path().apply {
            moveTo(cx - 40f, cy - 36f)
            quadraticBezierTo(cx, cy - 42f, cx + 40f, cy - 36f)
        }
        drawPath(
            haloPath,
            Brush.horizontalGradient(
                colors = listOf(Color.Transparent, Color(0xCCFFFFFF), Color.Transparent)
            ),
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )

        // 11. Traditional Sakura Cherry Blossom Hairclip (桜の髪飾り) on left hair
        val clipX = cx - 40f
        val clipY = cy - 34f
        for (i in 0 until 5) {
            val angle = i * (360f / 5f) * (Math.PI.toFloat() / 180f)
            val petalX = clipX + cos(angle) * 8f
            val petalY = clipY + sin(angle) * 8f
            drawCircle(Color(0xFFFF80AB), radius = 5f, center = Offset(petalX, petalY))
        }
        drawCircle(Color(0xFFFFEB3B), radius = 4f, center = Offset(clipX, clipY))
        drawCircle(Color(0xFFE91E63), radius = 2f, center = Offset(clipX, clipY))
    }
}

/**
 * Renders large, multi-layered 3D anime eye with specular reflections
 */
private fun DrawScope.drawLustrous3DAnimeEye(
    centerX: Float,
    centerY: Float,
    isMotivated: Boolean
) {
    // Sclera (Eye white)
    drawOval(
        color = Color(0xFFFFFFFF),
        topLeft = Offset(centerX - 11f, centerY - 13f),
        size = Size(22f, 26f)
    )

    // Deep Iris Gradient (3D Shaded from deep indigo to glowing magenta or golden amber)
    val irisBrush = if (isMotivated) {
        Brush.verticalGradient(listOf(Color(0xFFBF360C), Color(0xFFFF6D00), Color(0xFFFFD54F)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF1A237E), Color(0xFF6A1B9A), Color(0xFFE040FB), Color(0xFFEA80FC)))
    }
    drawOval(
        brush = irisBrush,
        topLeft = Offset(centerX - 9f, centerY - 11f),
        size = Size(18f, 23f)
    )

    // Pupil
    drawCircle(
        color = Color(0xFF0F0B1A),
        radius = 4.5f,
        center = Offset(centerX, centerY + 1f)
    )

    // Specular Reflection Highlight #1 (Upper big round reflection)
    drawCircle(
        color = Color(0xFFFFFFFF),
        radius = 4.5f,
        center = Offset(centerX - 3.5f, centerY - 4.5f)
    )

    // Specular Reflection Highlight #2 (Lower secondary star glimmer)
    drawCircle(
        color = Color(0xDDFFFFFF),
        radius = 2.5f,
        center = Offset(centerX + 3.5f, centerY + 4.5f)
    )

    // Upper Anime Eyeliner Arch
    val linerPath = Path().apply {
        moveTo(centerX - 14f, centerY - 10f)
        cubicTo(centerX - 6f, centerY - 16f, centerX + 6f, centerY - 16f, centerX + 14f, centerY - 9f)
    }
    drawPath(linerPath, Color(0xFF1F1433), style = Stroke(width = 4.2f, cap = StrokeCap.Round))

    // Cat-eye lash flick
    drawLine(
        color = Color(0xFF1F1433),
        start = Offset(centerX + 12f, centerY - 10f),
        end = Offset(centerX + 16f, centerY - 14f),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
}

/**
 * Smiling happy curved anime eye `( ⌒ )`
 */
private fun DrawScope.drawHappySenseiEye(centerX: Float, centerY: Float, isLeft: Boolean) {
    val smileEye = Path().apply {
        moveTo(centerX - 12f, centerY + 2f)
        quadraticBezierTo(centerX, centerY - 9f, centerX + 12f, centerY + 2f)
    }
    drawPath(smileEye, Color(0xFF26183D), style = Stroke(width = 4.2f, cap = StrokeCap.Round))

    val lashEnd = if (isLeft) Offset(centerX - 15f, centerY - 1f) else Offset(centerX + 15f, centerY - 1f)
    val lashStart = if (isLeft) Offset(centerX - 10f, centerY + 1f) else Offset(centerX + 10f, centerY + 1f)
    drawLine(Color(0xFF26183D), lashStart, lashEnd, strokeWidth = 3f, cap = StrokeCap.Round)
}

/**
 * Pout / squint eye
 */
private fun DrawScope.drawPoutSenseiEye(centerX: Float, centerY: Float, isLeft: Boolean) {
    val poutEye = Path().apply {
        moveTo(centerX - 10f, centerY - 2f)
        lineTo(centerX + 10f, centerY + 2f)
    }
    drawPath(poutEye, Color(0xFF26183D), style = Stroke(width = 4f, cap = StrokeCap.Round))
}

// Dialogues database specifically tailored for Japanese Study Sensei Aoi
private fun getDefaultSenseiDialogue(streak: Int): String {
    val defaults = listOf(
        "こんにちは！継続は力なり (Continuance is power)! Ready for today's Japanese practice, Senpai?",
        "Streak check: $streak days! Every kanji and flashcard brings you closer to fluency!",
        "千里の道も一歩から (A journey of 1,000 miles starts with 1 step). Let's review 5 words together!",
        "Teacher Aiko is here to guide you! Tap 'Drill' below to open our Kanji Lab and JLPT Mock Exam!"
    )
    return defaults.random()
}

private fun getSenseiHeadpatDialogue(): String {
    val quotes = listOf(
        "えへへ、ありがとう！でも勉強をサボっちゃダメですよ！ (Ehehe, thank you! But don't slack on your Japanese study!)",
        "よしよし！頭を撫でたら漢字を3つ復習しましょう！ (Good good! After this headpat, let's review 3 kanji!)",
        "センパイ、甘えてないで単語帳を開いてくださいね〜 (Senpai, don't just spoil me, open your Renshuu cards!)",
        "A-ah! My sakura hairclip! ...Fine, 5 more seconds of headpats, then back to your Japanese cards!"
    )
    return quotes.random()
}

private fun getSenseiPokeDialogue(): String {
    val quotes = listOf(
        "いたっ！突く暇があったら「食べる」の活用を言ってください！ (Ouch! If you have time to poke me, conjugate 'taberu'!)",
        "ほっぺた突かないで〜！日本語の復習タイマーが鳴りますよ！ (Don't poke my cheeks! Your Japanese study alarm is ringing!)",
        "七転び八起き (Fall down 7 times, get up 8)! Even if poked, I'll teach you Japanese!",
        "Hey! Stop squishing my cheeks and test yourself on today's JLPT N5 kanji!"
    )
    return quotes.random()
}

private fun getSenseiMotivationDialogue(streak: Int): String {
    val quotes = listOf(
        "塵も積もれば山となる (Even dust forms a mountain)! 5 kanji a day makes you fluent! Streak: $streak days!",
        "初志貫徹 (Carry out your intent to the end)! Don't let hesitation stop your Japanese journey!",
        "今日も一日、日本語学習を頑張りましょう！ (Let's do our best studying Japanese today!)",
        "Consistency is the ultimate weapon! Your future self in Tokyo is cheering for you right now!"
    )
    return quotes.random()
}

private fun getSenseiChatDialogue(streak: Int): String {
    val quotes = listOf(
        "Sensei Tip: Reviewing 5 flashcards right before sleeping boosts long-term memory retention by over 40%!",
        "Struggling with particles? Remember: は (wa) sets the topic, while を (o) catches the action!",
        "Can't focus today? Try our 2-Minute Micro Sprint! Even 5 questions keeps your habit loop alive.",
        "Tap 'Drill' or open ⛩️ Renshuu Practice to tackle JLPT N5 & N4 questions and earn Kao-coins!",
        "Did you know? In Japanese, the word 継続 (keizoku) means continuous, unstoppable flow. That's your streak!"
    )
    return quotes.random()
}
