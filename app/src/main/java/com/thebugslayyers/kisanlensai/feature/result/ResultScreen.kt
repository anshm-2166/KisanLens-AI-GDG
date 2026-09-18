package com.thebugslayyers.kisanlensai.feature.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Sanitizer
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

@Composable
fun ResultScreen(
    analysis: CropAnalysisResult,
    currentLanguage: Language,
    isSpeaking: Boolean,
    onPlayAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onScanAnother: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Hero Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (analysis.diseaseDetected) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Top Tag Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (analysis.diseaseDetected) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                    ) {
                        Text(
                            text = if (analysis.diseaseDetected) stringResource(id = R.string.status_disease_detected) else stringResource(id = R.string.status_healthy),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    // Demo / Live badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (analysis.isDemoResult) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = if (analysis.isDemoResult) stringResource(id = R.string.demo_data_badge) else stringResource(id = R.string.live_ai_badge),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (analysis.isDemoResult) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Crop Name & Disease
                Text(
                    text = "🌿 ${analysis.crop}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = analysis.diseaseName,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Metrics Row: Confidence & Severity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Confidence Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = stringResource(id = R.string.confidence_label),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(analysis.confidence * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Severity Pill
                    val severityColor = when (analysis.severity) {
                        Severity.HIGH -> RedSeverityHigh
                        Severity.MODERATE -> OrangeSeverityMod
                        else -> Color(0xFF388E3C)
                    }
                    val severityText = when (analysis.severity) {
                        Severity.HIGH -> stringResource(id = R.string.severity_high)
                        Severity.MODERATE -> stringResource(id = R.string.severity_moderate)
                        Severity.LOW -> stringResource(id = R.string.severity_low)
                        Severity.UNKNOWN -> stringResource(id = R.string.severity_unknown)
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = severityColor.copy(alpha = 0.15f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = stringResource(id = R.string.severity_label),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = severityText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = severityColor
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // HERO VOICE ACTION BUTTON (Zero-Literacy Support)
        Button(
            onClick = { if (isSpeaking) onStopAudio() else onPlayAudio() },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSpeaking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isSpeaking) stringResource(id = R.string.stop_audio_button) else stringResource(id = R.string.listen_button),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // WHAT WE SEE (Visual Evidence)
        if (analysis.visualEvidence.isNotEmpty()) {
            ResultSectionCard(
                title = stringResource(id = R.string.visual_evidence_header),
                icon = Icons.Default.Info,
                items = analysis.visualEvidence
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // STEP-BY-STEP CORRECTIVE MEASURES (Gemini AI Remedy)
        if (analysis.correctiveMeasures.isNotEmpty()) {
            ResultSectionCard(
                title = stringResource(id = R.string.corrective_measures_header),
                icon = Icons.Default.Healing,
                items = analysis.correctiveMeasures,
                accentColor = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // WHAT TO DO NOW (Immediate Actions)
        if (analysis.immediateActions.isNotEmpty()) {
            ResultSectionCard(
                title = stringResource(id = R.string.immediate_actions_header),
                icon = Icons.Default.CheckCircle,
                items = analysis.immediateActions,
                accentColor = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // CHEMICAL & FUNGICIDE SAFETY GUIDANCE
        if (analysis.chemicalSafetyGuidance.isNotBlank()) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sanitizer,
                            contentDescription = null,
                            tint = OrangeSeverityMod
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(id = R.string.chemical_guidance_header),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = analysis.chemicalSafetyGuidance,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // PREVENTIVE CARE
        if (analysis.preventiveActions.isNotEmpty()) {
            ResultSectionCard(
                title = stringResource(id = R.string.prevention_header),
                icon = Icons.Default.MedicalServices,
                items = analysis.preventiveActions
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // WHEN TO CONSULT AN EXPERT
        if (analysis.whenToSeekExpertHelp.isNotBlank()) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = OrangeSeverityMod
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(id = R.string.expert_help_header),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = analysis.whenToSeekExpertHelp,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Safety Disclaimer Note
        Text(
            text = stringResource(id = R.string.advisory_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // SCAN ANOTHER LEAF ACTION
        Button(
            onClick = onScanAnother,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(id = R.string.scan_another_button),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ResultSectionCard(
    title: String,
    icon: ImageVector,
    items: List<String>,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "• ",
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    KisanLensTheme {
        ResultScreen(
            analysis = DemoDataProvider.getDemoResult(CropType.TOMATO, Language.HINDI),
            currentLanguage = Language.HINDI,
            isSpeaking = false,
            onPlayAudio = {},
            onStopAudio = {},
            onScanAnother = {}
        )
    }
}
