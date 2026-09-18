package com.thebugslayyers.kisanlensai.feature.farm

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thebugslayyers.kisanlensai.R
import com.thebugslayyers.kisanlensai.domain.model.AgroAdvisory
import com.thebugslayyers.kisanlensai.domain.model.Language
import com.thebugslayyers.kisanlensai.ui.theme.KisanLensTheme

@Composable
fun FarmInsightsScreen(
    advisory: AgroAdvisory = AgroAdvisory(),
    currentLanguage: Language
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Location Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.farm_location),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = stringResource(id = R.string.demo_data_badge),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Integrated AI Agro-Advisory Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(id = R.string.integrated_advisory_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (currentLanguage == Language.HINDI) advisory.integratedAdviceHi else advisory.integratedAdviceEn,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Weather Card
        InsightCard(
            title = stringResource(id = R.string.weather_title),
            icon = Icons.Default.Cloud,
            summary = if (currentLanguage == Language.HINDI) advisory.weather.forecastSummaryHi else advisory.weather.forecastSummaryEn,
            metrics = listOf(
                "Rain Prob" to "${advisory.weather.rainProbabilityPercent}%",
                "Temperature" to "${advisory.weather.temperatureCelsius}°C",
                "Humidity" to "${advisory.weather.humidityPercent}%"
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Soil Health Card
        InsightCard(
            title = stringResource(id = R.string.soil_title),
            icon = Icons.Default.Grass,
            summary = if (currentLanguage == Language.HINDI) advisory.soil.summaryHi else advisory.soil.summaryEn,
            metrics = listOf(
                "Moisture" to advisory.soil.moistureLevel,
                "pH Level" to advisory.soil.pH.toString(),
                "NPK Balance" to "Optimal"
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Satellite Health Index Card
        InsightCard(
            title = stringResource(id = R.string.satellite_title),
            icon = Icons.Default.Satellite,
            summary = if (currentLanguage == Language.HINDI) advisory.satellite.summaryHi else advisory.satellite.summaryEn,
            metrics = listOf(
                "NDVI Score" to advisory.satellite.ndviScore.toString(),
                "Veg Health" to advisory.satellite.vegetationHealth,
                "Canopy" to advisory.satellite.canopyDensity
            )
        )
    }
}

@Composable
private fun InsightCard(
    title: String,
    icon: ImageVector,
    summary: String,
    metrics: List<Pair<String, String>>
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
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                metrics.forEach { (label, value) ->
                    Column {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FarmInsightsScreenPreview() {
    KisanLensTheme {
        FarmInsightsScreen(
            advisory = AgroAdvisory(),
            currentLanguage = Language.HINDI
        )
    }
}
