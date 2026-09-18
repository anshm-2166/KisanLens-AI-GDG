package com.thebugslayyers.kisanlensai.feature.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.thebugslayyers.kisanlensai.R
import com.thebugslayyers.kisanlensai.data.mock.SeasonalCropsProvider
import com.thebugslayyers.kisanlensai.domain.model.CropRegion
import com.thebugslayyers.kisanlensai.domain.model.CropSeason
import com.thebugslayyers.kisanlensai.domain.model.CropType
import com.thebugslayyers.kisanlensai.domain.model.Language
import com.thebugslayyers.kisanlensai.domain.model.SeasonalCrop
import com.thebugslayyers.kisanlensai.ui.theme.KisanLensTheme

@Composable
fun HomeScreen(
    currentLanguage: Language,
    selectedCrop: CropType,
    isDemoMode: Boolean,
    onLanguageToggle: () -> Unit,
    onCropSelected: (CropType) -> Unit,
    onDemoModeToggle: (Boolean) -> Unit,
    onStartScan: () -> Unit,
    onNavigateToInsights: () -> Unit,
    onNavigateToScans: () -> Unit,
    onNavigateToChat: () -> Unit,
    selectedSeason: CropSeason,
    onSeasonSelected: (CropSeason) -> Unit,
    onDiscussSeasonWithAi: () -> Unit,
    selectedRegion: CropRegion,
    isRegionFromDevice: Boolean,
    hasAskedForLocation: Boolean,
    onRegionToggle: () -> Unit,
    onLocationPermissionGranted: () -> Unit,
    onLocationPromptShown: () -> Unit
) {
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) onLocationPermissionGranted()
    }

    // Asks once for coarse location so the cropping calendar can follow where the farmer actually
    // is. Coarse is deliberate - the peninsula boundary is a latitude line, so city-level accuracy
    // is more than enough and avoids the heavier precise-location prompt. Denial is not fatal:
    // the region chip still lets the farmer pick by hand, and it is never asked a second time.
    LaunchedEffect(hasAskedForLocation) {
        if (hasAskedForLocation) return@LaunchedEffect
        onLocationPromptShown()

        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            onLocationPermissionGranted()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.home_greeting),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(id = R.string.home_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Language Toggle Pill
            Card(
                onClick = onLanguageToggle,
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentLanguage == Language.HINDI) "हिन्दी" else "EN",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // HERO CAMERA SCAN BUTTON
        Card(
            onClick = onStartScan,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.scan_crop_hero),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(id = R.string.scan_crop_hero_sub),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Crop Selection Section
        Text(
            text = stringResource(id = R.string.select_crop_label),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CropType.entries) { crop ->
                val isSelected = selectedCrop == crop
                FilterChip(
                    selected = isSelected,
                    onClick = { onCropSelected(crop) },
                    label = {
                        Text(
                            text = crop.getLocalizedName(currentLanguage),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick action grid: every destination in the app, reachable from Home.
        Text(
            text = stringResource(id = R.string.home_quick_actions_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Two explicit rows rather than a LazyVerticalGrid: this screen already scrolls
        // vertically, and nesting a same-axis lazy grid inside it would fight for the scroll.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                icon = Icons.Default.CameraAlt,
                title = stringResource(id = R.string.home_card_scan_title),
                subtitle = stringResource(id = R.string.home_card_scan_sub),
                onClick = onStartScan,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            QuickActionCard(
                icon = Icons.AutoMirrored.Filled.Chat,
                title = stringResource(id = R.string.home_card_chat_title),
                subtitle = stringResource(id = R.string.home_card_chat_sub),
                onClick = onNavigateToChat,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                icon = Icons.Default.Cloud,
                title = stringResource(id = R.string.home_card_insights_title),
                subtitle = stringResource(id = R.string.home_card_insights_sub),
                onClick = onNavigateToInsights,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            QuickActionCard(
                icon = Icons.Default.History,
                title = stringResource(id = R.string.home_card_history_title),
                subtitle = stringResource(id = R.string.home_card_history_sub),
                onClick = onNavigateToScans,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Demo Mode Toggle Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.demo_mode_toggle),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(id = R.string.demo_mode_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = isDemoMode,
                    onCheckedChange = onDemoModeToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ---- Crops for this season ----
        // Keyed on the region: Sept and Feb land in a different season on either side of the
        // Vindhyas, so a stale remembered value would show the wrong crop list.
        val currentSeason = remember(selectedRegion) { CropSeason.current(selectedRegion) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.home_season_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Only shown when the farmer is looking at the season they are actually in.
            if (selectedSeason == currentSeason) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = stringResource(id = R.string.home_season_now_badge),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        RegionChip(
            region = selectedRegion,
            isFromDevice = isRegionFromDevice,
            currentLanguage = currentLanguage,
            onClick = onRegionToggle
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(CropSeason.entries) { season ->
                val isSelected = selectedSeason == season
                FilterChip(
                    selected = isSelected,
                    onClick = { onSeasonSelected(season) },
                    label = {
                        Text(
                            text = season.getLocalizedName(currentLanguage),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = selectedSeason.getLocalizedWindow(selectedRegion, currentLanguage),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(SeasonalCropsProvider.cropsFor(selectedSeason, selectedRegion)) { crop ->
                SeasonalCropCard(crop = crop, currentLanguage = currentLanguage)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onDiscussSeasonWithAi,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.home_discuss_with_ai),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Shows which cropping calendar the list below is following, and swaps to the other one on tap.
 *
 * The region is guessed from the device's location, which can be wrong or unavailable, so this is
 * always tappable - the farmer is never stuck with a calendar that does not match their field.
 */
@Composable
private fun RegionChip(
    region: CropRegion,
    isFromDevice: Boolean,
    currentLanguage: Language,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)

    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .clip(shape)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Place,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = region.getLocalizedName(currentLanguage),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (isFromDevice) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.home_region_from_device),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(id = R.string.home_region_change),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SeasonalCropCard(
    crop: SeasonalCrop,
    currentLanguage: Language
) {
    Card(
        modifier = Modifier.width(176.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = crop.getLocalizedName(currentLanguage),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Exactly three lines so every card in the row is the same height.
            Text(
                text = crop.getLocalizedNote(currentLanguage),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                minLines = 3,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * One tile of the Home quick-action grid. Kept uniform in colour, shape and icon treatment so the
 * four destinations read as one group rather than four unrelated cards.
 */
@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                // Reserve two lines so tiles sharing a row keep their text aligned.
                minLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    KisanLensTheme {
        HomeScreen(
            currentLanguage = Language.HINDI,
            selectedCrop = CropType.TOMATO,
            isDemoMode = true,
            onLanguageToggle = {},
            onCropSelected = {},
            onDemoModeToggle = {},
            onStartScan = {},
            onNavigateToInsights = {},
            onNavigateToScans = {},
            onNavigateToChat = {},
            selectedSeason = CropSeason.KHARIF,
            onSeasonSelected = {},
            onDiscussSeasonWithAi = {},
            selectedRegion = CropRegion.PENINSULAR_INDIA,
            isRegionFromDevice = true,
            hasAskedForLocation = true,
            onRegionToggle = {},
            onLocationPermissionGranted = {},
            onLocationPromptShown = {}
        )
    }
}
