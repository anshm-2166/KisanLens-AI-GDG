package com.thebugslayyers.kisanlensai.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Camera : Screen("camera")
    object Analysis : Screen("analysis")
    object Result : Screen("result")
    object FarmInsights : Screen("farm_insights")
    object Scans : Screen("scans")
    object Chat : Screen("chat")
}
