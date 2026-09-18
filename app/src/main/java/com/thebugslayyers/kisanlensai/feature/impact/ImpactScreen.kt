package com.thebugslayyers.kisanlensai.feature.impact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thebugslayyers.kisanlensai.R
import com.thebugslayyers.kisanlensai.ui.theme.KisanLensTheme

@Composable
fun ImpactScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.impact_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(id = R.string.impact_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Pillar 1: Early Detection
        PillarCard(
            title = stringResource(id = R.string.impact_point1_title),
            description = stringResource(id = R.string.impact_point1_desc),
            icon = Icons.Default.Agriculture
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Pillar 2: Resource Efficiency
        PillarCard(
            title = stringResource(id = R.string.impact_point2_title),
            description = stringResource(id = R.string.impact_point2_desc),
            icon = Icons.Default.AutoAwesome
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Pillar 3: Zero Literacy
        PillarCard(
            title = stringResource(id = R.string.impact_point3_title),
            description = stringResource(id = R.string.impact_point3_desc),
            icon = Icons.Default.Accessible
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Pillar 4: Pan India DPG Architecture
        PillarCard(
            title = stringResource(id = R.string.impact_point4_title),
            description = stringResource(id = R.string.impact_point4_desc),
            icon = Icons.Default.Public
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.impact_simulation_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PillarCard(
    title: String,
    description: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImpactScreenPreview() {
    KisanLensTheme {
        ImpactScreen()
    }
}
