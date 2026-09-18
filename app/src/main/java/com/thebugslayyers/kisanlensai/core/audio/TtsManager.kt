package com.thebugslayyers.kisanlensai.core.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.thebugslayyers.kisanlensai.domain.model.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                }
            })
            Log.d("TtsManager", "TTS initialized successfully")
        } else {
            Log.e("TtsManager", "TTS Initialization failed")
        }
    }

    fun speak(text: String, language: Language) {
        if (text.isBlank()) return
        if (!isInitialized || tts == null) {
            Log.w("TtsManager", "TTS not initialized yet")
            return
        }

        stop()

        val locale = if (language == Language.HINDI) Locale("hi", "IN") else Locale.US
        val langResult = tts?.setLanguage(locale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w("TtsManager", "Language $locale not supported, falling back to default")
            tts?.language = Locale.getDefault()
        }

        val params = HashMap<String, String>()
        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "kisanlens_utterance_${System.currentTimeMillis()}"

        _isSpeaking.value = true
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params)
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        _isSpeaking.value = false
    }
}
