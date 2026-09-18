package com.thebugslayyers.kisanlensai.domain.repository

import android.graphics.Bitmap
import com.thebugslayyers.kisanlensai.domain.model.AgroAdvisory
import com.thebugslayyers.kisanlensai.domain.model.ChatSeed
import com.thebugslayyers.kisanlensai.domain.model.CropAnalysisResult
import com.thebugslayyers.kisanlensai.domain.model.CropType
import com.thebugslayyers.kisanlensai.domain.model.Language
import com.thebugslayyers.kisanlensai.domain.model.SatelliteInsight
import com.thebugslayyers.kisanlensai.domain.model.SoilSnapshot
import com.thebugslayyers.kisanlensai.domain.model.WeatherSnapshot

interface AgroAdvisoryRepository {

    suspend fun analyzeCropImage(
        bitmap: Bitmap,
        language: Language,
        cropContext: CropType = CropType.AUTO,
        isDemoMode: Boolean = false,
        apiKey: String? = null
    ): CropAnalysisResult

    /** Starts a fresh chat conversation, optionally grounded in a past scan or a cropping season. */
    fun startNewChat(seed: ChatSeed? = null)

    /** Sends a farming question and returns the assistant's answer in the requested language. */
    suspend fun askFarmingQuestion(question: String, language: Language): Result<String>

    suspend fun getWeather(location: String = "Prayagraj, UP"): WeatherSnapshot

    suspend fun getSoilHealth(farmId: String = "farm_prayagraj_01"): SoilSnapshot

    suspend fun getSatelliteInsights(location: String = "Prayagraj, UP"): SatelliteInsight

    suspend fun getIntegratedAdvisory(
        location: String = "Prayagraj, UP",
        language: Language = Language.HINDI
    ): AgroAdvisory
}
