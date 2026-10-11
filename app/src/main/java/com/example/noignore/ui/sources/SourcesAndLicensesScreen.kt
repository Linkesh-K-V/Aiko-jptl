package com.example.noignore.ui.sources

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DatasetSourceInfo(
    val name: String,
    val reference: String,
    val license: String,
    val attributionRequirement: String,
    val importDate: String,
    val version: String,
    val isVerified: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcesAndLicensesScreen(
    onBack: () -> Unit
) {
    val sources = listOf(
        DatasetSourceInfo(
            name = "JLPT Core Curriculum Assets (N5–N1)",
            reference = "Bundled JSON Curriculum (assets/jlpt/)",
            license = "Educational / Open Linguistic Study",
            attributionRequirement = "Attribution to open linguistic databases (JMdict / Tatoeba / Tanos project benchmarks)",
            importDate = "2026-09-20",
            version = "5.0",
            isVerified = false // Transparently labeled as SOURCE UNVERIFIED per Phase 2E requirement
        ),
        DatasetSourceInfo(
            name = "EDRDG JMdict & Kanjidic Reference",
            reference = "Electronic Dictionary Research & Development Group (EDRDG)",
            license = "CC BY-SA 3.0 / EDRDG Licence",
            attributionRequirement = "Contains material from EDRDG conforming to EDRDG licence conditions",
            importDate = "2026-09-20",
            version = "2026-09 Release",
            isVerified = true
        ),
        DatasetSourceInfo(
            name = "Tatoeba Corpus Example Sentences",
            reference = "Tatoeba Project (tatoeba.org)",
            license = "Creative Commons Attribution 2.0 France (CC BY 2.0 FR)",
            attributionRequirement = "Attribution to original contributors on Tatoeba",
            importDate = "2026-09-20",
            version = "Corpus Snapshot 2026",
            isVerified = true
        ),
        DatasetSourceInfo(
            name = "Android Text-To-Speech Synthesizer",
            reference = "Google / AOSP Japanese TTS Engine",
            license = "Android Open Source Project / Google Play Services",
            attributionRequirement = "Device speech synthesis runtime",
            importDate = "System Runtime",
            version = "Platform Default",
            isVerified = true
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Sources & Licenses",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Curriculum Transparency & Attribution",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("sources_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Official JLPT Non-Affiliation Disclaimer
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Disclaimer",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "OFFICIAL NON-AFFILIATION NOTICE",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "NoIgnore: Aiko JLPT is an independent educational tool. This application is NOT endorsed by, directly affiliated with, maintained, authorized, or sponsored by the Japan Foundation or Japan Educational Exchanges and Services (JEES). All JLPT level groupings reflect common educational study benchmarks rather than official fixed requirements.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            items(sources) { source ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = source.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (source.isVerified) Color(0xFF2E7D32).copy(alpha = 0.15f) else Color(0xFFED6C02).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (source.isVerified) "VERIFIED" else "SOURCE UNVERIFIED",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    color = if (source.isVerified) Color(0xFF2E7D32) else Color(0xFFED6C02),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Reference: ${source.reference}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "License: ${source.license}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Attribution: ${source.attributionRequirement}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Import Date: ${source.importDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "Dataset v${source.version}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
