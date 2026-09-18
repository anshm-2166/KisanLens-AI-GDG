package com.thebugslayyers.kisanlensai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thebugslayyers.kisanlensai.domain.model.Language
import com.thebugslayyers.kisanlensai.feature.analysis.AnalysisScreen
import com.thebugslayyers.kisanlensai.feature.camera.CameraScreen
import com.thebugslayyers.kisanlensai.feature.chat.ChatScreen
import com.thebugslayyers.kisanlensai.feature.farm.FarmInsightsScreen
import com.thebugslayyers.kisanlensai.feature.home.HomeScreen
import com.thebugslayyers.kisanlensai.feature.onboarding.OnboardingScreen
import com.thebugslayyers.kisanlensai.feature.result.ResultScreen
import com.thebugslayyers.kisanlensai.feature.scans.ScansScreen
import com.thebugslayyers.kisanlensai.ui.MainViewModel
import com.thebugslayyers.kisanlensai.ui.navigation.Screen
import com.thebugslayyers.kisanlensai.ui.theme.KisanLensTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KisanLensTheme {
                KisanLensApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun KisanLensApp(viewModel: MainViewModel) {
    val navController = rememberNavController()

    val currentLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()
    val isDemoMode by viewModel.isDemoModeEnabled.collectAsStateWithLifecycle()
    val selectedCrop by viewModel.selectedCrop.collectAsStateWithLifecycle()
    val currentAnalysis by viewModel.currentAnalysis.collectAsStateWithLifecycle()
    val scanHistory by viewModel.scanHistory.collectAsStateWithLifecycle()
    val analysisStage by viewModel.analysisStage.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isAudioSpeaking.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatSending by viewModel.isChatSending.collectAsStateWithLifecycle()
    val chatError by viewModel.chatError.collectAsStateWithLifecycle()
    val chatSeed by viewModel.chatSeed.collectAsStateWithLifecycle()
    val speakingMessageId by viewModel.speakingMessageId.collectAsStateWithLifecycle()
    val selectedSeason by viewModel.selectedSeason.collectAsStateWithLifecycle()
    val selectedRegion by viewModel.selectedRegion.collectAsStateWithLifecycle()
    val isRegionFromDevice by viewModel.isRegionFromDevice.collectAsStateWithLifecycle()
    val hasAskedForLocation by viewModel.hasAskedForLocation.collectAsStateWithLifecycle()
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val startDestination = if (isOnboardingCompleted) Screen.Home.route else Screen.Onboarding.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Scans.route,
        Screen.FarmInsights.route,
        Screen.Chat.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
                        label = { Text(text = stringResource(id = R.string.nav_home)) }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Camera.route,
                        onClick = {
                            viewModel.resetScan()
                            navController.navigate(Screen.Camera.route)
                        },
                        icon = { Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null) },
                        label = { Text(text = stringResource(id = R.string.nav_camera)) }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Scans.route,
                        onClick = {
                            navController.navigate(Screen.Scans.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = Icons.Default.History, contentDescription = null) },
                        label = { Text(text = stringResource(id = R.string.nav_scans)) }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.FarmInsights.route,
                        onClick = {
                            navController.navigate(Screen.FarmInsights.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = Icons.Default.Cloud, contentDescription = null) },
                        label = { Text(text = stringResource(id = R.string.nav_insights)) }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Chat.route,
                        onClick = {
                            navController.navigate(Screen.Chat.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
                        label = { Text(text = stringResource(id = R.string.nav_chat)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    currentLanguage = currentLanguage,
                    onLanguageSelected = { lang ->
                        viewModel.setLanguage(lang)
                    },
                    onContinue = {
                        viewModel.setOnboardingCompleted()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    currentLanguage = currentLanguage,
                    selectedCrop = selectedCrop,
                    isDemoMode = isDemoMode,
                    onLanguageToggle = {
                        val nextLang = if (currentLanguage == Language.HINDI) Language.ENGLISH else Language.HINDI
                        viewModel.setLanguage(nextLang)
                    },
                    onCropSelected = { crop ->
                        viewModel.selectCrop(crop)
                    },
                    onDemoModeToggle = { enabled ->
                        viewModel.setDemoMode(enabled)
                    },
                    onStartScan = {
                        viewModel.resetScan()
                        navController.navigate(Screen.Camera.route)
                    },
                    onNavigateToInsights = {
                        navController.navigate(Screen.FarmInsights.route)
                    },
                    onNavigateToScans = {
                        navController.navigate(Screen.Scans.route)
                    },
                    onNavigateToChat = {
                        navController.navigate(Screen.Chat.route)
                    },
                    selectedSeason = selectedSeason,
                    onSeasonSelected = { season ->
                        viewModel.selectSeason(season)
                    },
                    onDiscussSeasonWithAi = {
                        viewModel.openChatAboutSeason(selectedSeason, selectedRegion)
                        navController.navigate(Screen.Chat.route)
                    },
                    selectedRegion = selectedRegion,
                    isRegionFromDevice = isRegionFromDevice,
                    hasAskedForLocation = hasAskedForLocation,
                    onRegionToggle = {
                        viewModel.toggleRegion()
                    },
                    onLocationPermissionGranted = {
                        viewModel.resolveRegionFromDevice()
                    },
                    onLocationPromptShown = {
                        viewModel.markLocationPromptShown()
                    }
                )
            }

            composable(Screen.Camera.route) {
                CameraScreen(
                    onImageCaptured = { bitmap ->
                        viewModel.setCapturedBitmap(bitmap)
                        navController.navigate(Screen.Analysis.route)
                    },
                    onClose = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Analysis.route) {
                AnalysisScreen(
                    currentStage = analysisStage,
                    onStartAnalysis = {
                        viewModel.startAnalysis {
                            navController.navigate(Screen.Result.route) {
                                popUpTo(Screen.Camera.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Screen.Result.route) {
                currentAnalysis?.let { analysis ->
                    ResultScreen(
                        analysis = analysis,
                        currentLanguage = currentLanguage,
                        isSpeaking = isSpeaking,
                        onPlayAudio = {
                            viewModel.playAudioAdvisory()
                        },
                        onStopAudio = {
                            viewModel.stopAudioAdvisory()
                        },
                        onScanAnother = {
                            viewModel.resetScan()
                            navController.navigate(Screen.Camera.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }
            }

            composable(Screen.Scans.route) {
                ScansScreen(
                    scans = scanHistory,
                    currentLanguage = currentLanguage,
                    onSelectScan = { scan ->
                        viewModel.selectScanFromHistory(scan)
                        navController.navigate(Screen.Result.route)
                    },
                    onDeleteScan = { id ->
                        viewModel.deleteScanFromHistory(id)
                    },
                    onClearAll = {
                        viewModel.clearScanHistory()
                    },
                    onStartScan = {
                        viewModel.resetScan()
                        navController.navigate(Screen.Camera.route)
                    },
                    onChatAboutScan = { scan ->
                        viewModel.openChatAboutScan(scan)
                        navController.navigate(Screen.Chat.route)
                    }
                )
            }

            composable(Screen.Chat.route) {
                ChatScreen(
                    messages = chatMessages,
                    currentLanguage = currentLanguage,
                    isSending = isChatSending,
                    isListening = isListening,
                    speakingMessageId = speakingMessageId,
                    chatSeed = chatSeed,
                    errorMessage = chatError,
                    onSendMessage = { text -> viewModel.sendChatMessage(text) },
                    onStartVoiceInput = { viewModel.startVoiceInput() },
                    onStopVoiceInput = { viewModel.stopVoiceInput() },
                    onSpeakMessage = { message -> viewModel.speakChatMessage(message) },
                    onStopSpeaking = { viewModel.stopAudioAdvisory() },
                    onNewChat = { viewModel.startFreshChat() },
                    onDismissError = { viewModel.dismissChatError() },
                    onError = { message -> viewModel.showChatError(message) }
                )
            }

            composable(Screen.FarmInsights.route) {
                FarmInsightsScreen(
                    currentLanguage = currentLanguage
                )
            }
        }
    }
}
