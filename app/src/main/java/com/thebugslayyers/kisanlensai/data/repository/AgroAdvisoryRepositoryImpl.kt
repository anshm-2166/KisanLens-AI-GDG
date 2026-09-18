package com.thebugslayyers.kisanlensai.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.thebugslayyers.kisanlensai.core.ai.GeminiChatService
import com.thebugslayyers.kisanlensai.core.ai.GeminiCropAnalyzer
import com.thebugslayyers.kisanlensai.data.mock.DemoDataProvider
import com.thebugslayyers.kisanlensai.domain.model.AgroAdvisory
import com.thebugslayyers.kisanlensai.domain.model.ChatSeed
import com.thebugslayyers.kisanlensai.domain.model.CropAnalysisResult
import com.thebugslayyers.kisanlensai.domain.model.CropType
import com.thebugslayyers.kisanlensai.domain.model.Language
import com.thebugslayyers.kisanlensai.domain.model.SatelliteInsight
import com.thebugslayyers.kisanlensai.domain.model.Severity
import com.thebugslayyers.kisanlensai.domain.model.SoilSnapshot
import com.thebugslayyers.kisanlensai.domain.model.WeatherSnapshot
import com.thebugslayyers.kisanlensai.domain.repository.AgroAdvisoryRepository

class AgroAdvisoryRepositoryImpl(
    private val geminiAnalyzer: GeminiCropAnalyzer = GeminiCropAnalyzer(),
    private val geminiChatService: GeminiChatService = GeminiChatService()
) : AgroAdvisoryRepository {

    override suspend fun analyzeCropImage(
        bitmap: Bitmap,
        language: Language,
        cropContext: CropType,
        isDemoMode: Boolean,
        apiKey: String?
    ): CropAnalysisResult {
        if (isDemoMode) {
            Log.d("AgroAdvisoryRepo", "Demo mode enabled -> returning mock result")
            return DemoDataProvider.getDemoResult(cropContext, language)
        }

        Log.d("AgroAdvisoryRepo", "Executing live Gemini Vision analysis...")
        val liveResult = geminiAnalyzer.analyzeCropImage(
            bitmap = bitmap,
            language = language,
            cropContext = cropContext,
            userApiKey = apiKey
        )

        return liveResult.getOrElse { error ->
            val errorMsg = error.localizedMessage ?: "Unknown Gemini API Error"
            Log.e("AgroAdvisoryRepo", "Live Gemini call failed with error: $errorMsg", error)

            CropAnalysisResult(
                crop = if (language == Language.HINDI) "लाइव एआई त्रुटि" else "Live AI Connection Error",
                diseaseDetected = false,
                diseaseName = if (language == Language.HINDI) "मिथुन एआई (Gemini) कनेक्शन विफलता" else "Gemini API Error",
                confidence = 0f,
                severity = Severity.UNKNOWN,
                visualEvidence = listOf(
                    errorMsg,
                    "Note: Gemini API keys may start with either 'AIzaSy...' or 'AQ.Ab8...' - both are valid. Check the Gradle logs under tag 'GeminiCropAnalyzer' for the raw server response."
                ),
                immediateActions = listOf(
                    "1. Get a free Gemini API key from https://aistudio.google.com/app/apikey",
                    "2. Add GEMINI_API_KEY=<your key> in gradle.properties or local.properties.",
                    "3. Ensure active internet connection."
                ),
                correctiveMeasures = listOf(
                    "Or enable 'Demo Mode' toggle on Home Screen for offline judge presentations."
                ),
                advisory = "Gemini API Error: $errorMsg. Please check your API key in gradle.properties.",
                language = language.code,
                isDemoResult = false
            )
        }
    }

    override fun startNewChat(seed: ChatSeed?) {
        geminiChatService.startNewConversation(seed)
    }

    override suspend fun askFarmingQuestion(
        question: String,
        language: Language
    ): Result<String> {
        Log.d("AgroAdvisoryRepo", "Sending chat question to Gemini (has context: ${geminiChatService.hasContext})...")
        return geminiChatService.ask(question, language)
    }

    override suspend fun getWeather(location: String): WeatherSnapshot {
        return WeatherSnapshot(locationName = location)
    }

    override suspend fun getSoilHealth(farmId: String): SoilSnapshot {
        return SoilSnapshot()
    }

    override suspend fun getSatelliteInsights(location: String): SatelliteInsight {
        return SatelliteInsight()
    }

    override suspend fun getIntegratedAdvisory(
        location: String,
        language: Language
    ): AgroAdvisory {
        val weather = getWeather(location)
        val soil = getSoilHealth()
        val satellite = getSatelliteInsights(location)

        val adviceEn = "Forecast indicates high rain probability (${weather.rainProbabilityPercent}%) in $location. Soil moisture (${soil.moistureLevel}) is adequate. Hold immediate irrigation and monitor leaves for fungal spotting."
        val adviceHi = "$location में मौसम के अनुसार अगले 48 घंटों में बारिश (${weather.rainProbabilityPercent}%) की संभावना है। मिट्टी में नमी पर्याप्त (${soil.moistureLevel}) है। सिंचाई रोकें और पत्तियों पर फफूंद के धब्बों की निगरानी करें।"

        return AgroAdvisory(
            weather = weather,
            soil = soil,
            satellite = satellite,
            integratedAdviceEn = adviceEn,
            integratedAdviceHi = adviceHi
        )
    }
}
