package com.thebugslayyers.kisanlensai.feature.scans

import android.text.format.DateFormat
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thebugslayyers.kisanlensai.R
import com.thebugslayyers.kisanlensai.data.mock.DemoDataProvider
import com.thebugslayyers.kisanlensai.domain.model.CropAnalysisResult
import com.thebugslayyers.kisanlensai.domain.model.CropType
import com.thebugslayyers.kisanlensai.domain.model.Language
import com.thebugslayyers.kisanlensai.domain.model.Severity
import com.thebugslayyers.kisanlensai.ui.theme.KisanLensTheme
import com.thebugslayyers.kisanlensai.ui.theme.OrangeSeverityMod
import com.thebugslayyers.kisanlensai.ui.theme.RedSeverityHigh
import java.util.Date

@Composable
fun ScansScreen(
    scans: List<CropAnalysisResult>,
    currentLanguage: Language,
    onSelectScan: (CropAnalysisResult) -> Unit,
    onDeleteScan: (String) -> Unit,
    onClearAll: () -> Unit,
    onStartScan: () -> Unit,
    onChatAboutScan: (CropAnalysisResult) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.scans_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(id = R.string.scans_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (scans.isNotEmpty()) {
                IconButton(onClick = onClearAll) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear All",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (scans.isEmpty()) {
            EmptyScansContent(onStartScan = onStartScan)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(scans, key = { it.id }) { scan ->
                    ScanHistoryItemCard(
                        scan = scan,
                        currentLanguage = currentLanguage,
                        onClick = { onSelectScan(scan) },
                        onDelete = { onDeleteScan(scan.id) },
                        onChatAboutScan = { onChatAboutScan(scan) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanHistoryItemCard(
    scan: CropAnalysisResult,
    currentLanguage: Language,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onChatAboutScan: () -> Unit
) {
    val formattedDate = try {
        DateFormat.format("dd MMM yyyy, hh:mm a", Date(scan.timestamp)).toString()
    } catch (e: Exception) {
        ""
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (scan.diseaseDetected) Color(0xFFD32F2F) else Color(0xFF388E3C)
                    ) {
                        Text(
                            text = if (scan.diseaseDetected) stringResource(id = R.string.status_disease_detected) else stringResource(id = R.string.status_healthy),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (scan.isDemoResult) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = stringResource(id = R.string.demo_data_badge),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "🌿 ${scan.crop}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = scan.diseaseName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (formattedDate.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onChatAboutScan) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = stringResource(id = R.string.chat_about_scan_short),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete scan",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun EmptyScansContent(onStartScan: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.no_scans_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.no_scans_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStartScan,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(52.dp)
        ) {
            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.scan_crop_hero))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScansScreenPreview() {
    KisanLensTheme {
        ScansScreen(
            scans = listOf(
                DemoDataProvider.getDemoResult(CropType.TOMATO, Language.HINDI),
                DemoDataProvider.getDemoResult(CropType.WHEAT, Language.HINDI)
            ),
            currentLanguage = Language.HINDI,
            onSelectScan = {},
            onDeleteScan = {},
            onClearAll = {},
            onStartScan = {},
            onChatAboutScan = {}
        )
    }
}
