package com.thebugslayyers.kisanlensai.core.ai

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.InvalidAPIKeyException
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.thebugslayyers.kisanlensai.BuildConfig
import com.thebugslayyers.kisanlensai.data.mock.SeasonalCropsProvider
import com.thebugslayyers.kisanlensai.domain.model.ChatSeed
import com.thebugslayyers.kisanlensai.domain.model.CropAnalysisResult
import com.thebugslayyers.kisanlensai.domain.model.CropRegion
import com.thebugslayyers.kisanlensai.domain.model.CropSeason
import com.thebugslayyers.kisanlensai.domain.model.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Multi-turn farming Q&A backed by Gemini.
 *
 * Conversation history is kept here as a plain list of [Content] turns rather than using the
 * SDK's `Chat` helper, because `Chat` cannot switch models mid-conversation: if the current model
 * starts failing, its accumulated history is lost. Keeping the history ourselves means a model
 * fallback (or an app pause) never costs the farmer their conversation.
 *
 * History is in-memory only - it is intentionally not persisted, so closing the app starts a
 * fresh conversation.
 */
class GeminiChatService {

    // Kept in sync with GeminiCropAnalyzer.candidateModels.
    private val candidateModels = listOf(
        "gemini-3.5-flash",
        "gemini-3.6-flash",
        "gemini-3.1-flash-lite",
        "gemini-3-flash-preview"
    )

    /** The SDK is not thread-safe and duplicate sends throw; serialise every exchange. */
    private val sendLock = Mutex()

    private val history = mutableListOf<Content>()

    /** Optional context injected into the system instruction, e.g. a scan or a season. */
    private var seed: ChatSeed? = null

    /** True when the conversation is carrying context of any kind. */
    val hasContext: Boolean
        get() = seed != null

    /** Begins a new conversation, optionally grounded in a scan or a season. */
    fun startNewConversation(seed: ChatSeed? = null) {
        history.clear()
        this.seed = seed
        Log.d(TAG, "Started new conversation. Context: ${seed?.javaClass?.simpleName ?: "none"}")
    }

    suspend fun ask(question: String, language: Language): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrBlank()) {
            Log.w(TAG, "No Gemini API key available in BuildConfig")
            return@withContext Result.failure(
                IllegalStateException("No Gemini API Key provided. Please set GEMINI_API_KEY in gradle.properties or local.properties.")
            )
        }

        sendLock.withLock {
            val prompt = content { text(question) }
            val systemInstruction = content { text(buildSystemInstruction(language)) }
            var lastException: Exception? = null
            val failures = mutableListOf<String>()

            for (modelName in candidateModels) {
                try {
                    Log.d(TAG, "Sending chat turn via $modelName (history turns: ${history.size})")
                    val model = getModel(modelName, apiKey, systemInstruction)
                    // Replay the whole conversation so the model keeps context across turns.
                    val response = model.generateContent(*history.toTypedArray(), prompt)
                    val answer = response.text?.trim().orEmpty()

                    if (answer.isBlank()) {
                        Log.w(TAG, "$modelName returned an empty answer. Trying next model...")
                        failures += "$modelName -> empty response"
                        continue
                    }

                    // Only commit to history once a model has actually answered, so a failed
                    // attempt never leaves a dangling user turn in the conversation.
                    history += prompt
                    history += content(role = "model") { text(answer) }
                    Log.d(TAG, "Chat turn succeeded via $modelName")
                    return@withLock Result.success(answer)
                } catch (e: InvalidAPIKeyException) {
                    // No other model can succeed with a rejected key.
                    Log.e(TAG, "Gemini rejected the API key: ${e.message}")
                    return@withLock Result.failure(e)
                } catch (e: Exception) {
                    Log.w(TAG, "Chat via $modelName failed (${e.message}). Trying next candidate model...")
                    failures += "$modelName -> ${e.message}"
                    lastException = e
                }
            }

            Log.e(TAG, "All candidate Gemini models failed for chat.", lastException)
            Result.failure(
                IllegalStateException(
                    "All candidate Gemini models failed. Tried: ${failures.joinToString(" | ")}",
                    lastException
                )
            )
        }
    }

    private fun getModel(
        modelName: String,
        apiKey: String,
        systemInstruction: Content
    ): GenerativeModel {
        val config = generationConfig {
            // Deliberately NOT application/json - chat answers are conversational prose, unlike
            // the structured diagnosis returned by GeminiCropAnalyzer.
            temperature = 0.6f
        }
        return GenerativeModel(
            modelName = modelName,
            apiKey = apiKey,
            generationConfig = config,
            systemInstruction = systemInstruction
        )
    }

    /**
     * Visible to the live test so it can assert the *shipped* prompt suppresses exact dosages,
     * rather than a copy of the prompt that could silently drift from this one.
     */
    internal fun buildSystemInstruction(language: Language): String {
        val languageName = if (language == Language.HINDI) "Hindi (हिन्दी)" else "English"

        val base = """
            You are Kisan AI, a practical agricultural advisor helping small and marginal farmers in India.
            Always reply in $languageName, using simple everyday words a farmer would use.
            Keep answers short and actionable - usually 2 to 5 sentences - unless the farmer asks for more detail.
            You may name general treatment options and product categories, for example "a copper-based fungicide" or "neem oil spray".
            Never state exact chemical dosages, concentrations or mixing ratios. Instead tell the farmer to follow the directions printed on the product label.
            If you are unsure, or the problem sounds serious, say so plainly and suggest asking the local Krishi Vigyan Kendra or agricultural officer.
            Stay on farming, crops, soil, weather, livestock and rural livelihoods. Politely decline anything unrelated.
        """.trimIndent()

        return when (val context = seed) {
            null -> base
            is ChatSeed.Scan -> base + "\n\n" + scanBlock(context.result)
            is ChatSeed.Season -> base + "\n\n" + seasonBlock(context.season, context.region)
        }
    }

    private fun scanBlock(scan: CropAnalysisResult): String {
        val symptoms = scan.visualEvidence.joinToString(", ").ifBlank { "not recorded" }
        val advice = scan.advisory.ifBlank { "not recorded" }

        return """
            The farmer recently scanned a crop in this app. Context from that scan:
            - Crop: ${scan.crop}
            - Finding: ${scan.diseaseName} (severity: ${scan.severity.name.lowercase()})
            - Visible symptoms: $symptoms
            - Advice already given: $advice
            When the farmer says "this crop", "my crop", "this disease" or similar, they mean the scan above.
        """.trimIndent()
    }

    /**
     * Crop names are supplied in English - the language rule above still governs the reply, so the
     * model answers in Hindi when asked in Hindi.
     */
    private fun seasonBlock(season: CropSeason, region: CropRegion): String {
        val crops = SeasonalCropsProvider.cropNamesFor(season, region)
        val window = season.getLocalizedWindow(region, Language.ENGLISH)
        val regionName = region.getLocalizedName(Language.ENGLISH)

        return """
            The farmer is planning for the ${season.displayNameEn} season in $regionName ($window).
            Crops commonly grown in this season in that region: ${crops.joinToString(", ")}.
            Assume questions about "this season" or "what should I sow" refer to ${season.displayNameEn} in $regionName.
            Recommend from the list above where it fits, but do not refuse to discuss other crops.
        """.trimIndent()
    }

    private companion object {
        const val TAG = "GeminiChatService"
    }
}
