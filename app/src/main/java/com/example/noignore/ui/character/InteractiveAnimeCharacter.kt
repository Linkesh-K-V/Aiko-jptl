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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.TouchApp
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
import androidx.compose.ui.draw.scale
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
 * Expression state for the anime character
 */
enum class AnimeExpression {
    IDLE_HAPPY,
    BLUSHING_HEADPAT,
    POUT_POKED,
    MOTIVATED_FIERY,
    DISAPPOINTED_SWEAT,
    SMUG_SAGE
}

/**
 * Floating particle for touch interactions
 */
data class AnimeParticle(
    val id: Long = Random.nextLong(),
    val emoji: String,
    val initialX: Float, // Relative -1f to 1f
    val initialY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val alpha: Float = 1f,
    val scale: Float = 1f
)

/**
 * Interactive anime character companion for the home screen
 */
@Composable
fun InteractiveAnimeCharacter(
    mood: CharacterMood,
    streakCount: Int,
    level: CharacterLevel = CharacterLevel.BEGINNER,
    initialDialogue: String? = null,
    personality: CharacterPersonality = CharacterPersonality.SERGEANT,
    personalityTheme: PersonalityTheme? = null,
    onPersonalityClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    // Temporary expression triggered by touches (reverts back to mood expression)
    var temporaryExpression by remember { mutableStateOf<AnimeExpression?>(null) }

    // Resolve base expression from logic mood
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

    // Squash and stretch animations for interactions
    val scaleXAnim = remember { Animatable(1f) }
    val scaleYAnim = remember { Animatable(1f) }
    val rotationAnim = remember { Animatable(0f) }

    // Interactive speech bubble text
    var currentDialogue by remember(initialDialogue, personality) {
        mutableStateOf(
            if (!initialDialogue.isNullOrBlank()) initialDialogue
            else getDefaultDialogue(personality, streakCount)
        )
    }

    // Reaction particles list
    val particles = remember { mutableStateListOf<AnimeParticle>() }

    // Blinking cycle animation (closes eyes briefly every 3.5s)
    val infiniteTransition = rememberInfiniteTransition(label = "anime_idle")
    val idleBlink by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )

    // Breathing float animation
    val breathingOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    // Hair sway animation
    val hairSway by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hair_sway"
    )

    // Trigger headpat reaction
    fun triggerHeadpat() {
        coroutineScope.launch {
            temporaryExpression = AnimeExpression.BLUSHING_HEADPAT
            currentDialogue = getHeadpatDialogue(personality)

            // Spawn heart & sparkle particles
            repeat(4) {
                particles.add(
                    AnimeParticle(
                        emoji = if (it % 2 == 0) "💖" else "✨",
                        initialX = (Random.nextFloat() - 0.5f) * 80f,
                        initialY = -30f - (it * 15f),
                        velocityX = (Random.nextFloat() - 0.5f) * 30f,
                        velocityY = -60f - Random.nextFloat() * 40f
                    )
                )
            }

            // Headpat squish animation
            scaleYAnim.animateTo(0.88f, animationSpec = tween(120))
            scaleYAnim.animateTo(1.05f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            scaleYAnim.animateTo(1f, animationSpec = spring())

            delay(2800)
            if (temporaryExpression == AnimeExpression.BLUSHING_HEADPAT) {
                temporaryExpression = null
            }
        }
    }

    // Trigger cheek poke reaction
    fun triggerCheekPoke(isLeftCheek: Boolean) {
        coroutineScope.launch {
            temporaryExpression = AnimeExpression.POUT_POKED
            currentDialogue = getPokeDialogue(personality)

            // Spawn sweat / puff particles
            particles.add(
                AnimeParticle(
                    emoji = if (isLeftCheek) "👈" else "👉",
                    initialX = if (isLeftCheek) -60f else 60f,
                    initialY = 10f,
                    velocityX = if (isLeftCheek) -20f else 20f,
                    velocityY = -40f
                )
            )
            particles.add(
                AnimeParticle(
                    emoji = "💢",
                    initialX = if (isLeftCheek) -40f else 40f,
                    initialY = -40f,
                    velocityX = 0f,
                    velocityY = -30f
                )
            )

            // Jiggle animation
            val tiltAngle = if (isLeftCheek) 8f else -8f
            rotationAnim.animateTo(tiltAngle, animationSpec = tween(90))
            rotationAnim.animateTo(-tiltAngle * 0.6f, animationSpec = tween(90))
            rotationAnim.animateTo(0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))

            delay(2600)
            if (temporaryExpression == AnimeExpression.POUT_POKED) {
                temporaryExpression = null
            }
        }
    }

    // Trigger motivation boost
    fun triggerMotivation() {
        coroutineScope.launch {
            temporaryExpression = AnimeExpression.MOTIVATED_FIERY
            currentDialogue = getMotivationDialogue(personality, streakCount)

            // Spawn fire & lightning particles
            repeat(5) {
                particles.add(
                    AnimeParticle(
                        emoji = if (it % 2 == 0) "⚡" else "🔥",
                        initialX = (Random.nextFloat() - 0.5f) * 100f,
                        initialY = 20f,
                        velocityX = (Random.nextFloat() - 0.5f) * 50f,
                        velocityY = -70f - Random.nextFloat() * 50f
                    )
                )
            }

            // Power surge pulse
            scaleXAnim.animateTo(1.12f, animationSpec = tween(150))
            scaleYAnim.animateTo(1.12f, animationSpec = tween(150))
            scaleXAnim.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            scaleYAnim.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))

            delay(3200)
            if (temporaryExpression == AnimeExpression.MOTIVATED_FIERY) {
                temporaryExpression = null
            }
        }
    }

    // Trigger quick chat dialogue
    fun triggerChat() {
        currentDialogue = getChatDialogue(personality, streakCount)
    }

    // Particle cleanup coroutine
    LaunchedEffect(particles.size) {
        if (particles.isNotEmpty()) {
            delay(1500)
            particles.clear()
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Character Title & Persona Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Companion Name & Level Tag
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "LVL ${level.levelNumber}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                        Text(
                            text = level.title,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Persona switcher badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = personalityTheme?.containerColor ?: MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.clickable { onPersonalityClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = personalityTheme?.iconEmoji ?: "🪖", fontSize = 12.sp)
                            Text(
                                text = personality.displayName.substringBefore(" "),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = personalityTheme?.primaryColor ?: MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    // Mood indicator
                    MoodPill(mood = mood)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Interactive Anime Character Stage
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .graphicsLayer {
                        scaleX = scaleXAnim.value
                        scaleY = scaleYAnim.value
                        rotationZ = rotationAnim.value
                        translationY = breathingOffset
                    },
                contentAlignment = Alignment.Center
            ) {
                // Interactive Vector Canvas for Anime Face, Hair, and Body
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
                                        // Tap on forehead/hair -> Headpat!
                                        relY < 0.38f -> triggerHeadpat()
                                        // Tap on left cheek
                                        relY in 0.38f..0.68f && relX < 0.38f -> triggerCheekPoke(isLeftCheek = true)
                                        // Tap on right cheek
                                        relY in 0.38f..0.68f && relX > 0.62f -> triggerCheekPoke(isLeftCheek = false)
                                        // Tap on center / chest -> Motivation
                                        else -> triggerMotivation()
                                    }
                                },
                                onDoubleTap = {
                                    triggerMotivation()
                                }
                            )
                        }
                ) {
                    drawAnimeCharacter(
                        expression = currentExpression,
                        blinkProgress = idleBlink,
                        hairSway = hairSway,
                        primaryColor = personalityTheme?.primaryColor ?: Color(0xFF6750A4)
                    )
                }

                // Floating reaction particles
                particles.forEach { particle ->
                    Text(
                        text = particle.emoji,
                        fontSize = 22.sp,
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

            Spacer(modifier = Modifier.height(6.dp))

            // Interactive Speech Bubble
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { triggerChat() },
                shape = RoundedCornerShape(16.dp),
                color = when (currentExpression) {
                    AnimeExpression.BLUSHING_HEADPAT -> Color(0xFFFFF0F5)
                    AnimeExpression.MOTIVATED_FIERY -> MaterialTheme.colorScheme.primaryContainer
                    AnimeExpression.POUT_POKED -> Color(0xFFFFF8E1)
                    AnimeExpression.DISAPPOINTED_SWEAT -> MaterialTheme.colorScheme.surfaceVariant
                    else -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "\"$currentDialogue\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        color = when (currentExpression) {
                            AnimeExpression.BLUSHING_HEADPAT -> Color(0xFFC2185B)
                            AnimeExpression.MOTIVATED_FIERY -> MaterialTheme.colorScheme.onPrimaryContainer
                            AnimeExpression.POUT_POKED -> Color(0xFFE65100)
                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Touch Action Affordances
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InteractionPill(
                    icon = "🌸",
                    label = "Headpat",
                    onClick = { triggerHeadpat() },
                    modifier = Modifier.weight(1f)
                )
                InteractionPill(
                    icon = "👈",
                    label = "Poke",
                    onClick = { triggerCheekPoke(isLeftCheek = true) },
                    modifier = Modifier.weight(1f)
                )
                InteractionPill(
                    icon = "⚡",
                    label = "Inspire",
                    onClick = { triggerMotivation() },
                    modifier = Modifier.weight(1f)
                )
                InteractionPill(
                    icon = "💬",
                    label = "Chat",
                    onClick = { triggerChat() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun InteractionPill(
    icon: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(vertical = 7.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 13.sp)
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MoodPill(mood: CharacterMood) {
    val (bgColor, textColor) = when (mood) {
        CharacterMood.CALM -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        CharacterMood.PROUD -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        CharacterMood.DISAPPOINTED -> Color(0xFFECEFF1) to Color(0xFF455A64)
        CharacterMood.CONCERNED -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        CharacterMood.SILENT -> Color(0xFF212121) to Color(0xFFEEEEEE)
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
                text = mood.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

/**
 * Draws the high-res anime companion on the Compose canvas
 */
private fun DrawScope.drawAnimeCharacter(
    expression: AnimeExpression,
    blinkProgress: Float,
    hairSway: Float,
    primaryColor: Color
) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    drawScale(scale = (size.minDimension / 185f), pivot = Offset(cx, cy)) {
        // 1. Back hair (twin-tails / flowing locks)
        val backHairBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF5E35B1), Color(0xFF311B92))
        )
        val leftPigtail = Path().apply {
            moveTo(cx - 50f, cy - 20f)
            cubicTo(cx - 95f + hairSway, cy + 10f, cx - 85f + hairSway, cy + 90f, cx - 60f, cy + 80f)
            cubicTo(cx - 45f, cy + 50f, cx - 40f, cy + 20f, cx - 40f, cy)
            close()
        }
        drawPath(leftPigtail, backHairBrush)

    val rightPigtail = Path().apply {
        moveTo(cx + 50f, cy - 20f)
        cubicTo(cx + 95f - hairSway, cy + 10f, cx + 85f - hairSway, cy + 90f, cx + 60f, cy + 80f)
        cubicTo(cx + 45f, cy + 50f, cx + 40f, cy + 20f, cx + 40f, cy)
        close()
    }
    drawPath(rightPigtail, backHairBrush)

    // 2. Torso / Uniform Collar & Discipline Ribbon
    val uniformPath = Path().apply {
        moveTo(cx - 42f, cy + 65f)
        lineTo(cx + 42f, cy + 65f)
        lineTo(cx + 52f, size.height)
        lineTo(cx - 52f, size.height)
        close()
    }
    drawPath(
        uniformPath,
        Brush.verticalGradient(listOf(Color(0xFF263238), Color(0xFF1A1A24)))
    )

    // White shirt collar
    val collarPath = Path().apply {
        moveTo(cx - 26f, cy + 65f)
        lineTo(cx, cy + 82f)
        lineTo(cx + 26f, cy + 65f)
        lineTo(cx + 14f, cy + 65f)
        lineTo(cx, cy + 74f)
        lineTo(cx - 14f, cy + 65f)
        close()
    }
    drawPath(collarPath, Color(0xFFF5F5F7))

    // Discipline Valkyrie Tie/Ribbon
    val ribbonColor = when (expression) {
        AnimeExpression.MOTIVATED_FIERY -> Color(0xFFFF3D00)
        AnimeExpression.BLUSHING_HEADPAT -> Color(0xFFE91E63)
        else -> primaryColor
    }
    val ribbonPath = Path().apply {
        moveTo(cx, cy + 76f)
        lineTo(cx - 12f, cy + 96f)
        lineTo(cx, cy + 90f)
        lineTo(cx + 12f, cy + 96f)
        close()
    }
    drawPath(ribbonPath, ribbonColor)

    // Golden Discipline Star Crest
    drawCircle(Color(0xFFFFD54F), radius = 5f, center = Offset(cx, cy + 76f))

    // 3. Neck
    drawRect(
        color = Color(0xFFFFDFD0),
        topLeft = Offset(cx - 14f, cy + 50f),
        size = Size(28f, 20f)
    )

    // 4. Face Base (Smooth Anime Chibi Contours)
    val facePath = Path().apply {
        moveTo(cx - 48f, cy - 20f)
        cubicTo(cx - 50f, cy + 25f, cx - 35f, cy + 62f, cx, cy + 64f)
        cubicTo(cx + 35f, cy + 62f, cx + 50f, cy + 25f, cx + 48f, cy - 20f)
        cubicTo(cx + 42f, cy - 55f, cx - 42f, cy - 55f, cx - 48f, cy - 20f)
        close()
    }
    drawPath(facePath, Color(0xFFFFF0E5))

    // Face shadow under hair bangs
    drawOval(
        color = Color(0x14000000),
        topLeft = Offset(cx - 44f, cy - 42f),
        size = Size(88f, 22f)
    )

    // 5. Blushing Cheeks
    val blushAlpha = when (expression) {
        AnimeExpression.BLUSHING_HEADPAT -> 0.85f
        AnimeExpression.POUT_POKED -> 0.65f
        AnimeExpression.MOTIVATED_FIERY -> 0.5f
        else -> 0.35f
    }
    // Left blush
    drawCircle(
        color = Color(0xFFFF8A80).copy(alpha = blushAlpha),
        radius = 11f,
        center = Offset(cx - 28f, cy + 26f)
    )
    // Left blush lines
    drawLine(
        color = Color(0xFFE57373).copy(alpha = blushAlpha),
        start = Offset(cx - 32f, cy + 22f),
        end = Offset(cx - 26f, cy + 30f),
        strokeWidth = 2f
    )
    drawLine(
        color = Color(0xFFE57373).copy(alpha = blushAlpha),
        start = Offset(cx - 27f, cy + 22f),
        end = Offset(cx - 21f, cy + 30f),
        strokeWidth = 2f
    )

    // Right blush
    drawCircle(
        color = Color(0xFFFF8A80).copy(alpha = blushAlpha),
        radius = 11f,
        center = Offset(cx + 28f, cy + 26f)
    )
    drawLine(
        color = Color(0xFFE57373).copy(alpha = blushAlpha),
        start = Offset(cx + 24f, cy + 22f),
        end = Offset(cx + 30f, cy + 30f),
        strokeWidth = 2f
    )
    drawLine(
        color = Color(0xFFE57373).copy(alpha = blushAlpha),
        start = Offset(cx + 29f, cy + 22f),
        end = Offset(cx + 35f, cy + 30f),
        strokeWidth = 2f
    )

    // 6. Anime Eyes
    val isBlinking = blinkProgress < 0.08f

    when {
        // Joyful smiling curved eyes
        expression == AnimeExpression.BLUSHING_HEADPAT -> {
            drawSmilingEye(cx - 24f, cy + 12f, isLeft = true)
            drawSmilingEye(cx + 24f, cy + 12f, isLeft = false)
        }
        // Squinting angry / pout eyes
        expression == AnimeExpression.POUT_POKED -> {
            drawPoutEye(cx - 24f, cy + 12f, isLeft = true)
            drawPoutEye(cx + 24f, cy + 12f, isLeft = false)
        }
        // Blinking
        isBlinking -> {
            drawLine(
                Color(0xFF2E1C44),
                start = Offset(cx - 34f, cy + 14f),
                end = Offset(cx - 14f, cy + 14f),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
            drawLine(
                Color(0xFF2E1C44),
                start = Offset(cx + 14f, cy + 14f),
                end = Offset(cx + 34f, cy + 14f),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
        }
        // Normal / Motivated open anime eyes
        else -> {
            drawBigAnimeEye(
                centerX = cx - 24f,
                centerY = cy + 12f,
                isMotivated = expression == AnimeExpression.MOTIVATED_FIERY
            )
            drawBigAnimeEye(
                centerX = cx + 24f,
                centerY = cy + 12f,
                isMotivated = expression == AnimeExpression.MOTIVATED_FIERY
            )
        }
    }

    // 7. Eyebrows
    when (expression) {
        AnimeExpression.MOTIVATED_FIERY -> {
            // Sharp determined angle
            drawLine(Color(0xFF4A148C), Offset(cx - 35f, cy - 2f), Offset(cx - 16f, cy + 2f), strokeWidth = 3f, cap = StrokeCap.Round)
            drawLine(Color(0xFF4A148C), Offset(cx + 35f, cy - 2f), Offset(cx + 16f, cy + 2f), strokeWidth = 3f, cap = StrokeCap.Round)
        }
        AnimeExpression.POUT_POKED -> {
            // Angled inward pout
            drawLine(Color(0xFF4A148C), Offset(cx - 34f, cy + 2f), Offset(cx - 15f, cy - 2f), strokeWidth = 3f, cap = StrokeCap.Round)
            drawLine(Color(0xFF4A148C), Offset(cx + 34f, cy + 2f), Offset(cx + 15f, cy - 2f), strokeWidth = 3f, cap = StrokeCap.Round)
        }
        AnimeExpression.BLUSHING_HEADPAT -> {
            // Gentle raised happy brows
            drawLine(Color(0xFF4A148C), Offset(cx - 32f, cy - 4f), Offset(cx - 16f, cy - 2f), strokeWidth = 2.5f, cap = StrokeCap.Round)
            drawLine(Color(0xFF4A148C), Offset(cx + 32f, cy - 4f), Offset(cx + 16f, cy - 2f), strokeWidth = 2.5f, cap = StrokeCap.Round)
        }
        else -> {
            // Soft friendly arches
            drawLine(Color(0xFF4A148C), Offset(cx - 33f, cy - 1f), Offset(cx - 16f, cy - 1f), strokeWidth = 2.5f, cap = StrokeCap.Round)
            drawLine(Color(0xFF4A148C), Offset(cx + 16f, cy - 1f), Offset(cx + 33f, cy - 1f), strokeWidth = 2.5f, cap = StrokeCap.Round)
        }
    }

    // 8. Cute Anime Nose
    drawCircle(Color(0xFFD7CCC8), radius = 2f, center = Offset(cx, cy + 27f))

    // 9. Expressive Mouth
    when (expression) {
        AnimeExpression.BLUSHING_HEADPAT -> {
            // Cheerful open mouth
            val openMouth = Path().apply {
                moveTo(cx - 8f, cy + 38f)
                quadraticBezierTo(cx, cy + 47f, cx + 8f, cy + 38f)
                close()
            }
            drawPath(openMouth, Color(0xFFE53935))
            // Cute tongue highlight
            drawCircle(Color(0xFFFF8A80), radius = 4f, center = Offset(cx, cy + 42f))
        }
        AnimeExpression.POUT_POKED -> {
            // Cute wavy pout `w`
            val poutPath = Path().apply {
                moveTo(cx - 8f, cy + 38f)
                quadraticBezierTo(cx - 4f, cy + 34f, cx, cy + 38f)
                quadraticBezierTo(cx + 4f, cy + 34f, cx + 8f, cy + 38f)
            }
            drawPath(poutPath, Color(0xFFC2185B), style = Stroke(width = 3f, cap = StrokeCap.Round))
        }
        AnimeExpression.MOTIVATED_FIERY -> {
            // Confident toothy smirk
            val smirkPath = Path().apply {
                moveTo(cx - 7f, cy + 38f)
                quadraticBezierTo(cx + 4f, cy + 44f, cx + 9f, cy + 36f)
            }
            drawPath(smirkPath, Color(0xFF2E1C44), style = Stroke(width = 3f, cap = StrokeCap.Round))
        }
        else -> {
            // Sweet slight smile
            val smilePath = Path().apply {
                moveTo(cx - 6f, cy + 38f)
                quadraticBezierTo(cx, cy + 42f, cx + 6f, cy + 38f)
            }
            drawPath(smilePath, Color(0xFF2E1C44), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
        }
    }

    // 10. Front Hair Bangs (Detailed anime layered bangs)
    val hairGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF7E57C2), Color(0xFF5E35B1))
    )

    // Left bangs lock
    val leftBang = Path().apply {
        moveTo(cx - 48f, cy - 30f)
        cubicTo(cx - 52f, cy - 10f, cx - 44f, cy + 18f, cx - 36f, cy + 22f)
        cubicTo(cx - 38f, cy + 5f, cx - 40f, cy - 15f, cx - 28f, cy - 22f)
        close()
    }
    drawPath(leftBang, hairGradient)

    // Center forehead bangs (classic anime V-cut strands)
    val centerBangs = Path().apply {
        moveTo(cx - 36f, cy - 42f)
        lineTo(cx - 24f, cy - 4f)
        lineTo(cx - 18f, cy - 15f)
        lineTo(cx - 8f, cy + 2f)
        lineTo(cx, cy - 12f)
        lineTo(cx + 8f, cy + 4f)
        lineTo(cx + 18f, cy - 14f)
        lineTo(cx + 26f, cy - 2f)
        lineTo(cx + 36f, cy - 42f)
        close()
    }
    drawPath(centerBangs, hairGradient)

    // Right bangs lock
    val rightBang = Path().apply {
        moveTo(cx + 48f, cy - 30f)
        cubicTo(cx + 52f, cy - 10f, cx + 44f, cy + 18f, cx + 36f, cy + 22f)
        cubicTo(cx + 38f, cy + 5f, cx + 40f, cy - 15f, cx + 28f, cy - 22f)
        close()
    }
    drawPath(rightBang, hairGradient)

    // Top hair volume and crown
    val hairCrown = Path().apply {
        moveTo(cx - 48f, cy - 28f)
        cubicTo(cx - 52f, cy - 65f, cx + 52f, cy - 65f, cx + 48f, cy - 28f)
        cubicTo(cx + 30f, cy - 45f, cx - 30f, cy - 45f, cx - 48f, cy - 28f)
        close()
    }
    drawPath(hairCrown, hairGradient)

    // Hair Shine / Halo reflection (Signature Anime Light Band)
    val shinePath = Path().apply {
        moveTo(cx - 36f, cy - 34f)
        quadraticBezierTo(cx, cy - 38f, cx + 36f, cy - 34f)
    }
    drawPath(
        shinePath,
        Color(0x77FFFFFF),
        style = Stroke(width = 4f, cap = StrokeCap.Round)
    )

    // 11. Discipline Star Ribbon Hairclip on left hair
    val clipCenterX = cx - 36f
    val clipCenterY = cy - 32f
    drawCircle(Color(0xFFFFD54F), radius = 7f, center = Offset(clipCenterX, clipCenterY))
    drawCircle(Color(0xFFFFA000), radius = 3.5f, center = Offset(clipCenterX, clipCenterY))
    }
}

/**
 * Draws large sparkling anime eye with specular highlights
 */
private fun DrawScope.drawBigAnimeEye(
    centerX: Float,
    centerY: Float,
    isMotivated: Boolean
) {
    // Sclera (Eye white)
    drawOval(
        color = Color(0xFFFFFFFF),
        topLeft = Offset(centerX - 10f, centerY - 12f),
        size = Size(20f, 24f)
    )

    // Iris gradient (Deep purple to luminous violet / fiery amber)
    val irisBrush = if (isMotivated) {
        Brush.verticalGradient(listOf(Color(0xFFFF6F00), Color(0xFFFFD54F)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF311B92), Color(0xFF7E57C2), Color(0xFFB388FF)))
    }
    drawOval(
        brush = irisBrush,
        topLeft = Offset(centerX - 8f, centerY - 10f),
        size = Size(16f, 21f)
    )

    // Pupil
    drawCircle(
        color = Color(0xFF1A1A24),
        radius = 4f,
        center = Offset(centerX, centerY + 1f)
    )

    // Specular Reflection Highlight #1 (Big upper highlight)
    drawCircle(
        color = Color(0xFFFFFFFF),
        radius = 4f,
        center = Offset(centerX - 3f, centerY - 4f)
    )

    // Specular Reflection Highlight #2 (Lower secondary star highlight)
    drawCircle(
        color = Color(0xCCFFFFFF),
        radius = 2f,
        center = Offset(centerX + 3f, centerY + 4f)
    )

    // Upper Anime Eyeliner Arch
    val linerPath = Path().apply {
        moveTo(centerX - 12f, centerY - 9f)
        cubicTo(centerX - 5f, centerY - 14f, centerX + 6f, centerY - 14f, centerX + 12f, centerY - 8f)
    }
    drawPath(linerPath, Color(0xFF261C38), style = Stroke(width = 3.8f, cap = StrokeCap.Round))

    // Cute outer cat-eye lash flick
    drawLine(
        color = Color(0xFF261C38),
        start = Offset(centerX + 11f, centerY - 9f),
        end = Offset(centerX + 14f, centerY - 12f),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )
}

/**
 * Draws happy curved eye `( ⌒ )`
 */
private fun DrawScope.drawSmilingEye(centerX: Float, centerY: Float, isLeft: Boolean) {
    val smileEye = Path().apply {
        moveTo(centerX - 10f, centerY + 2f)
        quadraticBezierTo(centerX, centerY - 8f, centerX + 10f, centerY + 2f)
    }
    drawPath(smileEye, Color(0xFF2E1C44), style = Stroke(width = 3.8f, cap = StrokeCap.Round))

    // Lash flick
    val flickOffset = if (isLeft) Offset(centerX - 13f, centerY - 1f) else Offset(centerX + 13f, centerY - 1f)
    val flickStart = if (isLeft) Offset(centerX - 9f, centerY + 1f) else Offset(centerX + 9f, centerY + 1f)
    drawLine(Color(0xFF2E1C44), flickStart, flickOffset, strokeWidth = 2.5f, cap = StrokeCap.Round)
}

/**
 * Draws cute pout / squint eye
 */
private fun DrawScope.drawPoutEye(centerX: Float, centerY: Float, isLeft: Boolean) {
    val poutEye = Path().apply {
        moveTo(centerX - 9f, centerY - 2f)
        lineTo(centerX + 9f, centerY + 2f)
    }
    drawPath(poutEye, Color(0xFF2E1C44), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
}

// Dialogues database
private fun getDefaultDialogue(personality: CharacterPersonality, streak: Int): String {
    return when (personality) {
        CharacterPersonality.SENSEI -> "こんにちは！継続は力なり (Keizoku wa chikara nari)! Ready for today's Japanese study, Senpai?"
        CharacterPersonality.SERGEANT -> "Discipline Officer standing by! No slacking allowed, cadet!"
        CharacterPersonality.COACH -> "You've got this! Streak is at $streak days! Let's conquer today!"
        CharacterPersonality.SAGE -> "Patience and relentless action. Every small directive shapes destiny."
        CharacterPersonality.ANALYST -> "Current consistency score is optimal. Ready for scheduled operations."
        CharacterPersonality.JESTER -> "Hey! Stop admiring my ribbon and check off those tasks, will ya?"
    }
}

private fun getHeadpatDialogue(personality: CharacterPersonality): String {
    if (personality == CharacterPersonality.SENSEI) {
        val senseiQuotes = listOf(
            "えへへ、ありがとう！でも勉強をサボっちゃダメですよ！ (Ehehe, thank you! But don't slack on your Japanese study!)",
            "よしよし！頭を撫でたら漢字を3つ復習しましょう！ (Good good! After this headpat, let's review 3 kanji!)",
            "センパイ、甘えてないで単語帳を開いてくださいね〜 (Senpai, don't just spoil me, open your Renshuu flashcards!)"
        )
        return senseiQuotes.random()
    }
    val headpatQuotes = listOf(
        "H-hey! It's not like I enjoy you patting my head, baka! ...Just make sure you finish your tasks!",
        "Mmm~ That feels comforting... But don't think you can skip your next directive!",
        "Ehehe~ Patting my head won't save your streak if you miss your alarm, Senpai!",
        "A-ah! My bangs will get messy! ...Fine, 5 more seconds of headpats, then back to work!"
    )
    return headpatQuotes.random()
}

private fun getPokeDialogue(personality: CharacterPersonality): String {
    if (personality == CharacterPersonality.SENSEI) {
        val senseiQuotes = listOf(
            "いたっ！突く暇があったら「食べる」の活用を言ってください！ (Ouch! If you have time to poke me, conjugate 'taberu'!)",
            "ほっぺた突かないで〜！日本語の復習タイマーが鳴りますよ！ (Don't poke my cheeks! Your Japanese study alarm is ringing!)",
            "七転び八起き！突かれてもめげずに勉強を教えますよ！ (Nana korobi ya oki! Even if poked, I'll teach you Japanese!)"
        )
        return senseiQuotes.random()
    }
    val pokeQuotes = listOf(
        "Ouch! Hands off the cheeks! Channel that energy into your tasks instead!",
        "W-whoa! Why are you poking me?! Are your daily directives all completed yet?",
        "Eep! Cheeks are off-limits until your streak reaches the next milestone!",
        "Hey! Stop squishing my face and lock in on your priorities, slacker!"
    )
    return pokeQuotes.random()
}

private fun getMotivationDialogue(personality: CharacterPersonality, streak: Int): String {
    if (personality == CharacterPersonality.SENSEI) {
        val senseiQuotes = listOf(
            "千里の道も一歩から (A journey of 1,000 miles begins with 1 step)! Today's study streak is at $streak days!",
            "塵も積もれば山となる (Even dust forms a mountain)! 5 kanji a day makes you fluent!",
            "初志貫徹 (Carry out your intent to the end)! Don't let hesitation stop your Japanese journey!",
            "今日も一日、日本語学習を頑張りましょう！ (Let's do our best studying Japanese today!)"
        )
        return senseiQuotes.random()
    }
    val motivationQuotes = listOf(
        "Directives locked in! You have a streak of $streak days—we fight together till the end!",
        "Ignite your focus! Procrastination has zero power over someone with true discipline!",
        "Stand tall! No excuses, no retreat! Finish what you started today!",
        "Your future self is forged right now, in this very hour! Let's go!!"
    )
    return motivationQuotes.random()
}

private fun getChatDialogue(personality: CharacterPersonality, streak: Int): String {
    if (personality == CharacterPersonality.SENSEI) {
        val senseiQuotes = listOf(
            "Renshuu Tip: Reviewing 5 flashcards right before sleeping boosts long-term memory retention by over 40%!",
            "Struggling with particles? Remember: は (wa) sets the topic, while を (o) catches the action!",
            "Streak check: $streak days! In Japan, the word 継続 (keizoku) means continuous, unstoppable flow.",
            "Can't focus today? Do our 2-Minute Consistency Sprint! Even 5 questions keeps your habit loop alive.",
            "Tap ⛩️ Renshuu Study above to test yourself on JLPT N5 kanji and earn Kao-coins!"
        )
        return senseiQuotes.random()
    }
    val chatQuotes = listOf(
        "Did you know? Consistent action for 7 days rewires neural pathways for unbreakable discipline!",
        "I'm keeping guard over your schedule. The strict confirmation window is active!",
        "If a task feels overwhelming, tap ✨ AI Assistant to deconstruct it into 2-minute steps!",
        "Your streak of $streak days is proof of your warrior spirit. Don't let it slip today!",
        "Remember: motivation comes and goes, but discipline stays forever!"
    )
    return chatQuotes.random()
}
