package com.thebugslayyers.kisanlensai.core.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.thebugslayyers.kisanlensai.domain.model.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Speech-to-text for the chat screen, so a farmer can ask a question by speaking.
 *
 * Companion to [TtsManager] (which handles the speaking-out half). Unlike TTS, Android's
 * [SpeechRecognizer] must be created and driven on the main thread, and it is single-use per
 * listening session, so a fresh instance is built for each request.
 */
class SpeechRecognizerManager(private val context: Context) {

    private var recognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private var onResult: ((String) -> Unit)? = null
    private var onError: ((String) -> Unit)? = null

    /** Some devices/emulators ship without a recognition service. */
    val isAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context)

    fun startListening(
        language: Language,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isAvailable) {
            Log.w(TAG, "Speech recognition is not available on this device")
            onError("Voice input is not available on this device.")
            return
        }

        this.onResult = onResult
        this.onError = onError

        release()

        val created = try {
            SpeechRecognizer.createSpeechRecognizer(context)
        } catch (e: Exception) {
            Log.e(TAG, "Could not create SpeechRecognizer", e)
            onError("Voice input could not be started.")
            return
        }

        recognizer = created
        created.setRecognitionListener(listener)

        val locale = if (language == Language.HINDI) Locale("hi", "IN") else Locale.US
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale.toString())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        _isListening.value = true
        try {
            created.startListening(intent)
        } catch (e: Exception) {
            Log.e(TAG, "startListening failed", e)
            _isListening.value = false
            onError("Voice input could not be started.")
        }
    }

    fun stopListening() {
        try {
            recognizer?.stopListening()
        } catch (e: Exception) {
            Log.w(TAG, "stopListening failed", e)
        }
        _isListening.value = false
    }

    fun release() {
        try {
            recognizer?.destroy()
        } catch (e: Exception) {
            Log.w(TAG, "destroy failed", e)
        }
        recognizer = null
        _isListening.value = false
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) = Unit
        override fun onBeginningOfSpeech() = Unit
        override fun onRmsChanged(rmsdB: Float) = Unit
        override fun onBufferReceived(buffer: ByteArray?) = Unit
        override fun onEndOfSpeech() {
            _isListening.value = false
        }

        override fun onPartialResults(partialResults: Bundle?) = Unit
        override fun onEvent(eventType: Int, params: Bundle?) = Unit

        override fun onResults(results: Bundle?) {
            _isListening.value = false
            val spoken = results
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                ?.trim()
                .orEmpty()

            if (spoken.isNotBlank()) {
                onResult?.invoke(spoken)
            } else {
                onError?.invoke("We didn't catch that. Please try again.")
            }
        }

        override fun onError(error: Int) {
            _isListening.value = false
            Log.w(TAG, "Recognition error code: $error")
            onError?.invoke(describeError(error))
        }
    }

    private fun describeError(error: Int): String = when (error) {
        SpeechRecognizer.ERROR_NO_MATCH,
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "We didn't catch that. Please try again in a quieter place."

        SpeechRecognizer.ERROR_AUDIO -> "There was a problem recording audio. Please try again."
        SpeechRecognizer.ERROR_NETWORK,
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Voice input needs an internet connection."
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is needed for voice input."
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Voice input is busy. Please try again."
        else -> "Voice input failed. Please try typing your question."
    }

    private companion object {
        const val TAG = "SpeechRecognizerManager"
    }
}
