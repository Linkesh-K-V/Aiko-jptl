package com.example.noignore.ui.streak

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.model.AikoApprovalBadge

/**
 * Visual section displaying 'Aiko's Approval' badges for 7-day and 30-day streaks.
 * Users can tap each badge to inspect Sensei Aiko's official certification and criteria.
 */
@Composable
fun AikoApprovalBadgeSection(
    currentStreak: Int,
    modifier: Modifier = Modifier,
    onBadgeClick: ((AikoApprovalBadge) -> Unit)? = null
) {
    var selectedBadge by remember { mutableStateOf<AikoApprovalBadge?>(null) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("aiko_approval_badges_section"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFD81B60).copy(alpha = 0.15f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("💮", fontSize = 14.sp)
                        }
                    }

                    Column {
                        Text(
                            text = "AIKO'S APPROVAL BADGES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFC2185B),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "愛子の承認印 • Maintain 7 & 30 day streaks",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val totalUnlocked = AikoApprovalBadge.entries.count { it.isUnlocked(currentStreak) }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (totalUnlocked > 0) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "$totalUnlocked / 2 Earned",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (totalUnlocked > 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Two Badge Cards Side by Side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AikoApprovalBadgeCard(
                    badge = AikoApprovalBadge.SEVEN_DAY,
                    currentStreak = currentStreak,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedBadge = AikoApprovalBadge.SEVEN_DAY
                        onBadgeClick?.invoke(AikoApprovalBadge.SEVEN_DAY)
                    }
                )

                AikoApprovalBadgeCard(
                    badge = AikoApprovalBadge.THIRTY_DAY,
                    currentStreak = currentStreak,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedBadge = AikoApprovalBadge.THIRTY_DAY
                        onBadgeClick?.invoke(AikoApprovalBadge.THIRTY_DAY)
                    }
                )
            }
        }
    }

    // Detail / Certificate Dialog
    selectedBadge?.let { badge ->
        AikoApprovalCertificateDialog(
            badge = badge,
            currentStreak = currentStreak,
            onDismiss = { selectedBadge = null }
        )
    }
}

/**
 * Individual badge tile designed like a Japanese Hanko / Seal.
 */
@Composable
fun AikoApprovalBadgeCard(
    badge: AikoApprovalBadge,
    currentStreak: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val unlocked = badge.isUnlocked(currentStreak)
    val progress = badge.progress(currentStreak)
    val daysLeft = badge.daysRemaining(currentStreak)

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("badge_card_${badge.id}"),
        shape = RoundedCornerShape(16.dp),
        color = if (unlocked) badge.accentColor else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.5.dp,
            if (unlocked) badge.primaryColor else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hanko Japanese Seal Icon
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (unlocked) {
                            Brush.radialGradient(
                                colors = listOf(badge.primaryColor.copy(alpha = 0.2f), badge.primaryColor)
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (unlocked) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = badge.hankoSeal,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = 16.sp
                        )
                        Text(
                            text = "印",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${badge.requiredStreak}d",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badge Name
            Text(
                text = badge.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = if (unlocked) badge.primaryColor else MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = badge.japaneseTitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = if (unlocked) badge.primaryColor.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status or Progress Bar
            if (unlocked) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badge.primaryColor
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Earned",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "EARNED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(5.dp)
                            .clip(CircleShape),
                        color = badge.primaryColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$currentStreak / ${badge.requiredStreak} days ($daysLeft left)",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Certificate Dialog showing Sensei Aiko's official Seal and approval proclamation.
 */
@Composable
fun AikoApprovalCertificateDialog(
    badge: AikoApprovalBadge,
    currentStreak: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val unlocked = badge.isUnlocked(currentStreak)
    val progress = badge.progress(currentStreak)
    val daysLeft = badge.daysRemaining(currentStreak)

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("aiko_certificate_dialog"),
        shape = RoundedCornerShape(24.dp),
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Traditional Certificate Header Banner
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = badge.primaryColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, badge.primaryColor.copy(alpha = 0.4f)),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "📜", fontSize = 13.sp)
                        Text(
                            text = "OFFICIAL CERTIFICATE OF APPROVAL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = badge.primaryColor,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                Text(
                    text = "愛子先生の承認状",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Official Approval from Sensei Aiko",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Large Hanko Seal Stamp
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            if (unlocked) {
                                Brush.radialGradient(
                                    colors = listOf(badge.primaryColor.copy(alpha = 0.15f), badge.primaryColor)
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                                    )
                                )
                            }
                        )
                        .testTag("certificate_hanko_seal"),
                    contentAlignment = Alignment.Center
                ) {
                    if (unlocked) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = badge.hankoSeal,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "愛子認定印",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "LOCKED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Badge Title & Rank
                Text(
                    text = badge.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (unlocked) badge.primaryColor else MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (unlocked) badge.accentColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Rank: ${badge.rankTitle} • ${badge.japaneseTitle}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (unlocked) badge.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = badge.fullDescription,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Streak Progress Status Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (unlocked) "Status: Approved & Earned!" else "Streak Progress",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (unlocked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$currentStreak / ${badge.requiredStreak} Days",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = badge.primaryColor
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = badge.primaryColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        if (!unlocked) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Maintain your daily streak for $daysLeft more consecutive ${if (daysLeft == 1) "day" else "days"} to unlock.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sensei Aiko's Quote
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF9FBE7),
                    border = BorderStroke(1.dp, Color(0xFFDCE775)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "🌸", fontSize = 22.sp)
                        Column {
                            Text(
                                text = "SENSEI AIKO SAYS:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF558B2F),
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (unlocked) badge.senseiQuote else "“Discipline is formed one day at a time. Do not neglect your practice, and you will soon receive this seal!”",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF33691E),
                                lineHeight = 16.sp,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("certificate_close_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = badge.primaryColor
                )
            ) {
                Text(
                    text = if (unlocked) "Hai, Sensei! (ありがとう)" else "I Will Keep My Streak! 🔥",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    )
}
