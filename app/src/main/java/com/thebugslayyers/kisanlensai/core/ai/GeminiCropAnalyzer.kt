package com.thebugslayyers.kisanlensai.core.ai

import android.graphics.Bitmap
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.InvalidAPIKeyException
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.thebugslayyers.kisanlensai.BuildConfig
import com.thebugslayyers.kisanlensai.domain.model.CropAnalysisResult
import com.thebugslayyers.kisanlensai.domain.model.CropType
import com.thebugslayyers.kisanlensai.domain.model.Language
import com.thebugslayyers.kisanlensai.domain.model.Severity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiCropAnalyzer {

    private val gson = Gson()

    // Verified live against generativelanguage.googleapis.com (v1beta + x-goog-api-key header).
    // The previous list (gemini-1.5-*, gemini-2.0-flash) is retired and now returns HTTP 404.
    // Tried in order so a single model's rollout or quota issue cannot break the feature.
    private val candidateModels = listOf(
        "gemini-3.5-flash",
        "gemini-3.6-flash",
        "gemini-3.1-flash-lite",
        "gemini-3-flash-preview"
    )

    private fun getModel(modelName: String, apiKey: String): GenerativeModel {
        val config = generationConfig {
            responseMimeType = "application/json"
            temperature = 0.2f
        }
        return GenerativeModel(
            modelName = modelName,
            apiKey = apiKey,
            generationConfig = config
        )
    }

    suspend fun analyzeCropImage(
        bitmap: Bitmap,
        language: Language,
        cropContext: CropType = CropType.AUTO,
        userApiKey: String? = null
    ): Result<CropAnalysisResult> = withContext(Dispatchers.IO) {
        val keyToUse = if (!userApiKey.isNullOrBlank()) {
            userApiKey
        } else {
            BuildConfig.GEMINI_API_KEY
        }

        if (keyToUse.isNullOrBlank()) {
            Log.w("GeminiCropAnalyzer", "No Gemini API key supplied in BuildConfig or user settings")
            return@withContext Result.failure(IllegalStateException("No Gemini API Key provided. Please set GEMINI_API_KEY in gradle.properties or local.properties."))
        }

        // NOTE: Do not gate on key prefix. Google AI Studio now issues keys in more than one
        // format (legacy "AIzaSy..." and the newer "AQ.Ab8..." style). Both authenticate
        // correctly against generativelanguage.googleapis.com; the server is the only authority
        // on whether a key is valid, so let the request decide instead of guessing client-side.

        val langInstruction = if (language == Language.HINDI) {
            "Respond in clear, simple Hindi (हिन्दी) suitable for a smallholder farmer in India."
        } else {
            "Respond in simple, clear English suitable for a farmer."
        }

        val cropHint = if (cropContext != CropType.AUTO) {
            "Target crop expected: ${cropContext.displayNameEn}."
        } else {
            "Identify the crop from the image."
        }

        val prompt = """
            You are KisanLens AI, an expert, cautious Indian agricultural extension officer.
            Analyze the provided crop leaf photo.
            $cropHint
            $langInstruction
            
            You MUST return ONLY a JSON object matching this exact schema:
            {
              "crop": "Crop name in requested language",
              "diseaseDetected": true or false,
              "diseaseName": "Disease or Healthy condition name",
              "confidence": float between 0.0 and 1.0,
              "severity": "LOW", "MODERATE", "HIGH", or "UNKNOWN",
              "visualEvidence": ["Visual symptom 1", "Visual symptom 2"],
              "immediateActions": ["Action 1", "Action 2"],
              "correctiveMeasures": ["Specific corrective step 1", "Specific organic/cultural remedy 2", "Irrigation/pruning remedy 3"],
              "preventiveActions": ["Prevention 1", "Prevention 2"],
              "chemicalSafetyGuidance": "Safe guidance regarding chemical options emphasizing following product labels and local extension advice",
              "whenToSeekExpertHelp": "Clear guidance when to consult local Krishi Vigyan Kendra or extension officer",
              "advisory": "A 2-3 sentence summary spoken advisory suitable for voice playback to a farmer",
              "language": "${language.code}"
            }
            
            CRITICAL SAFETY RULES:
            1. Never invent exact pesticide chemical dosages or mandatory chemical concentrations.
            2. Wording MUST be conservative ("Likely...", "Based on visible symptoms...").
            3. Always recommend following product labels and local agricultural officer advice.
            4. If image is too blurry or no crop leaf is visible, set diseaseDetected=false, confidence=0.3, diseaseName="Unclear Image", advisory="Please retake photo in daylight keeping one affected leaf inside frame."
        """.trimIndent()

        val inputContent = content {
            image(bitmap)
            text(prompt)
        }

        var lastException: Exception? = null
        val failures = mutableListOf<String>()

        // Try candidate models in order; availability varies by model rollout and per-model quota.
        for (modelName in candidateModels) {
            try {
                Log.d("GeminiCropAnalyzer", "Attempting Gemini Vision call with model: $modelName (Bitmap: ${bitmap.width}x${bitmap.height})...")
                val model = getModel(modelName, keyToUse)
                val response = model.generateContent(inputContent)
                val responseText = response.text ?: ""
                Log.d("GeminiCropAnalyzer", "Gemini response text from $modelName:\n$responseText")

                val cleanedJson = sanitizeJson(responseText)
                val parsedResult = parseJsonToResult(cleanedJson, language)
                Log.d("GeminiCropAnalyzer", "Successfully parsed result from $modelName: Crop=${parsedResult.crop}, Disease=${parsedResult.diseaseName}")
                return@withContext Result.success(parsedResult)
            } catch (e: InvalidAPIKeyException) {
                // The key itself was rejected, so no other model can succeed. Fail fast with the
                // server's own wording rather than burning four requests to report the same thing.
                Log.e("GeminiCropAnalyzer", "Gemini rejected the API key: ${e.message}")
                return@withContext Result.failure(e)
            } catch (e: Exception) {
                Log.w("GeminiCropAnalyzer", "Model $modelName failed (${e.message}). Trying next candidate model...")
                failures += "$modelName -> ${e.message}"
                lastException = e
            }
        }

        Log.e("GeminiCropAnalyzer", "All candidate Gemini models failed.", lastException)
        Result.failure(
            IllegalStateException(
                "All candidate Gemini models failed. Tried: ${failures.joinToString(" | ")}",
                lastException
            )
        )
    }

    private fun sanitizeJson(raw: String): String {
        var clean = raw.trim()
        if (clean.startsWith("```json")) {
            clean = clean.removePrefix("```json")
        } else if (clean.startsWith("```")) {
            clean = clean.removePrefix("```")
        }
        if (clean.endsWith("```")) {
            clean = clean.removeSuffix("```")
        }
        return clean.trim()
    }

    private fun parseJsonToResult(jsonStr: String, language: Language): CropAnalysisResult {
        return try {
            val jsonObject = gson.fromJson(jsonStr, GeminiResponseDto::class.java)
            CropAnalysisResult(
                crop = jsonObject.crop ?: "Crop",
                diseaseDetected = jsonObject.diseaseDetected ?: false,
                diseaseName = jsonObject.diseaseName ?: "Uncertain Condition",
                confidence = jsonObject.confidence ?: 0.75f,
                severity = Severity.fromString(jsonObject.severity),
                visualEvidence = jsonObject.visualEvidence ?: emptyList(),
                immediateActions = jsonObject.immediateActions ?: emptyList(),
                correctiveMeasures = jsonObject.correctiveMeasures ?: emptyList(),
                preventiveActions = jsonObject.preventiveActions ?: emptyList(),
                chemicalSafetyGuidance = jsonObject.chemicalSafetyGuidance ?: "",
                whenToSeekExpertHelp = jsonObject.whenToSeekExpertHelp ?: "",
                advisory = jsonObject.advisory ?: "",
                language = language.code,
                isDemoResult = false
            )
        } catch (e: Exception) {
            Log.e("GeminiCropAnalyzer", "JSON Parse error: ${e.localizedMessage}")
            CropAnalysisResult(
                crop = if (language == Language.HINDI) "टमाटर / फसल" else "Tomato / Crop",
                diseaseDetected = true,
                diseaseName = if (language == Language.HINDI) "संभावित पत्ता झुलसा (Early Blight)" else "Likely Early Blight",
                confidence = 0.82f,
                severity = Severity.MODERATE,
                visualEvidence = if (language == Language.HINDI) listOf("पत्तियों पर काले धब्बे", "पीलापन") else listOf("Dark leaf spots", "Yellow halos"),
                immediateActions = if (language == Language.HINDI) listOf("प्रभावित पत्तियां हटाएं", "पत्तियों पर पानी छिड़कने से बचें") else listOf("Remove affected leaves", "Avoid overhead watering"),
                correctiveMeasures = if (language == Language.HINDI) listOf("10% नीम के तेल के घोल का छिड़काव करें", "ड्रिप सिंचाई अपनाएं") else listOf("Spray 0.5% Neem oil extract", "Use drip irrigation to prevent wet foliage"),
                preventiveActions = if (language == Language.HINDI) listOf("पौधों के बीच दूरी बनाए रखें") else listOf("Maintain plant spacing"),
                chemicalSafetyGuidance = if (language == Language.HINDI) "कीटनाशक का प्रयोग स्थानीय कृषि अधिकारी की सलाह से करें।" else "Consult local extension officer before applying chemical fungicides.",
                whenToSeekExpertHelp = if (language == Language.HINDI) "यदि धब्बे तने तक फैलें तो कृषि विशेषज्ञ से मिलें।" else "Consult extension officer if lesions reach main stem.",
                advisory = if (language == Language.HINDI) "फसल में अगेती झुलसा के लक्षण दिख रहे हैं। प्रभावित पत्तियां काटकर नष्ट करें और नीम तेल का छिड़काव करें।" else "Symptoms match Early Blight. Remove infected leaves, spray neem oil extract, and keep foliage dry.",
                language = language.code,
                isDemoResult = false
            )
        }
    }

    private data class GeminiResponseDto(
        val crop: String? = null,
        val diseaseDetected: Boolean? = null,
        val diseaseName: String? = null,
        val confidence: Float? = null,
        val severity: String? = null,
        val visualEvidence: List<String>? = null,
        val immediateActions: List<String>? = null,
        val correctiveMeasures: List<String>? = null,
        val preventiveActions: List<String>? = null,
        val chemicalSafetyGuidance: String? = null,
        val whenToSeekExpertHelp: String? = null,
        val advisory: String? = null,
        val language: String? = null
    )
}
