package com.thebugslayyers.kisanlensai.ui

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thebugslayyers.kisanlensai.core.audio.SpeechRecognizerManager
import com.thebugslayyers.kisanlensai.core.audio.TtsManager
import com.thebugslayyers.kisanlensai.core.location.RegionLocator
import com.thebugslayyers.kisanlensai.data.local.DataStoreManager
import com.thebugslayyers.kisanlensai.data.repository.AgroAdvisoryRepositoryImpl
import com.thebugslayyers.kisanlensai.domain.model.ChatMessage
import com.thebugslayyers.kisanlensai.domain.model.ChatSeed
import com.thebugslayyers.kisanlensai.domain.model.CropAnalysisResult
import com.thebugslayyers.kisanlensai.domain.model.CropRegion
import com.thebugslayyers.kisanlensai.domain.model.CropSeason
import com.thebugslayyers.kisanlensai.domain.model.CropType
import com.thebugslayyers.kisanlensai.domain.model.Language
import com.thebugslayyers.kisanlensai.domain.repository.AgroAdvisoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = DataStoreManager(application)
    private val repository: AgroAdvisoryRepository = AgroAdvisoryRepositoryImpl()
    val ttsManager = TtsManager(application)

    private val _selectedLanguage = MutableStateFlow(Language.HINDI)
    val selectedLanguage: StateFlow<Language> = _selectedLanguage.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(false)
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _isDemoModeEnabled = MutableStateFlow(false)
    val isDemoModeEnabled: StateFlow<Boolean> = _isDemoModeEnabled.asStateFlow()

    private val _selectedCrop = MutableStateFlow(CropType.AUTO)
    val selectedCrop: StateFlow<CropType> = _selectedCrop.asStateFlow()

    private val regionLocator = RegionLocator(application)

    /**
     * The cropping region the Home screen is showing crops for. Starts on the northern plains
     * calendar and is replaced as soon as the device's own location is known.
     */
    private val _selectedRegion = MutableStateFlow(CropRegion.NORTH_INDIAN_PLAINS)
    val selectedRegion: StateFlow<CropRegion> = _selectedRegion.asStateFlow()

    /** True once the region came from the device rather than the default or a manual toggle. */
    private val _isRegionFromDevice = MutableStateFlow(false)
    val isRegionFromDevice: StateFlow<Boolean> = _isRegionFromDevice.asStateFlow()

    private val _isResolvingRegion = MutableStateFlow(false)
    val isResolvingRegion: StateFlow<Boolean> = _isResolvingRegion.asStateFlow()

    /**
     * True once the farmer has been shown the location prompt. Kept here rather than in the screen
     * because Compose discards screen state on navigation, which would make the app re-ask on every
     * visit to Home after a refusal. The region chip remains the way to change region by hand.
     */
    private val _hasAskedForLocation = MutableStateFlow(false)
    val hasAskedForLocation: StateFlow<Boolean> = _hasAskedForLocation.asStateFlow()

    /**
     * Latches once a location lookup has actually been started, so recomposition cannot fire it
     * repeatedly. Deliberately NOT set when the permission is missing - otherwise a farmer who
     * grants location on the second prompt would never get a detected region.
     */
    private var hasRequestedRegionLookup = false

    /** Which cropping season the Home screen is showing crops for; follows the selected region. */
    private val _selectedSeason = MutableStateFlow(CropSeason.current())
    val selectedSeason: StateFlow<CropSeason> = _selectedSeason.asStateFlow()

    private val _capturedBitmap = MutableStateFlow<Bitmap?>(null)
    val capturedBitmap: StateFlow<Bitmap?> = _capturedBitmap.asStateFlow()

    private val _currentAnalysis = MutableStateFlow<CropAnalysisResult?>(null)
    val currentAnalysis: StateFlow<CropAnalysisResult?> = _currentAnalysis.asStateFlow()

    private val _scanHistory = MutableStateFlow<List<CropAnalysisResult>>(emptyList())
    val scanHistory: StateFlow<List<CropAnalysisResult>> = _scanHistory.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisStage = MutableStateFlow(1)
    val analysisStage: StateFlow<Int> = _analysisStage.asStateFlow()

    private val _analysisError = MutableStateFlow<String?>(null)
    val analysisError: StateFlow<String?> = _analysisError.asStateFlow()

    val isAudioSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking

    private val speechRecognizer = SpeechRecognizerManager(application)
    val isListening: StateFlow<Boolean> = speechRecognizer.isListening

    // --- Chat state. Session-only by design: closing the app starts a fresh conversation. ---

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatSending = MutableStateFlow(false)
    val isChatSending: StateFlow<Boolean> = _isChatSending.asStateFlow()

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    /** Context the current conversation was opened with, or null for a plain chat. */
    private val _chatSeed = MutableStateFlow<ChatSeed?>(null)
    val chatSeed: StateFlow<ChatSeed?> = _chatSeed.asStateFlow()

    /**
     * Which assistant message is currently being read aloud. [isAudioSpeaking] is global to the
     * single TTS engine, so without this every assistant bubble would render a "Stop" button at
     * once instead of only the one actually playing.
     */
    private val _speakingMessageId = MutableStateFlow<String?>(null)
    val speakingMessageId: StateFlow<String?> = _speakingMessageId.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.selectedLanguageFlow.collect { lang ->
                _selectedLanguage.value = lang
            }
        }
        viewModelScope.launch {
            dataStore.isOnboardingCompletedFlow.collect { completed ->
                _isOnboardingCompleted.value = completed
            }
        }
        viewModelScope.launch {
            dataStore.isDemoModeEnabledFlow.collect { demo ->
                _isDemoModeEnabled.value = demo
            }
        }
        viewModelScope.launch {
            dataStore.scanHistoryFlow.collect { history ->
                _scanHistory.value = history
            }
        }
        viewModelScope.launch {
            // Clear the "now speaking" marker as soon as playback actually finishes or is stopped.
            ttsManager.isSpeaking.collect { speaking ->
                if (!speaking) _speakingMessageId.value = null
            }
        }
    }

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            _selectedLanguage.value = language
            dataStore.setLanguage(language)
        }
    }

    fun setOnboardingCompleted() {
        viewModelScope.launch {
            _isOnboardingCompleted.value = true
            dataStore.setOnboardingCompleted(true)
        }
    }

    fun setDemoMode(enabled: Boolean) {
        viewModelScope.launch {
            _isDemoModeEnabled.value = enabled
            dataStore.setDemoModeEnabled(enabled)
        }
    }

    fun selectCrop(crop: CropType) {
        _selectedCrop.value = crop
    }

    fun selectSeason(season: CropSeason) {
        _selectedSeason.value = season
    }

    /**
     * Detects the cropping region from the device's location. Safe to call on every Home
     * composition: it no-ops once a lookup has started, and no-ops while the permission is still
     * missing so the call made right after a grant still works.
     */
    fun resolveRegionFromDevice() {
        if (hasRequestedRegionLookup || _isResolvingRegion.value) return
        if (!regionLocator.hasLocationPermission()) return

        hasRequestedRegionLookup = true
        _isResolvingRegion.value = true
        viewModelScope.launch {
            val detected = regionLocator.resolveRegion()
            _isResolvingRegion.value = false
            if (detected != null) applyRegion(detected, fromDevice = true)
            // A null result leaves the default region in place; the farmer can still switch by hand.
        }
    }

    /** Records that the location prompt has been shown, so it is never shown twice. */
    fun markLocationPromptShown() {
        _hasAskedForLocation.value = true
    }

    /** Flips to the other region and resets the season to whatever is current there. */
    fun toggleRegion() {
        applyRegion(_selectedRegion.value.other(), fromDevice = false)
    }

    private fun applyRegion(region: CropRegion, fromDevice: Boolean) {
        _selectedRegion.value = region
        _isRegionFromDevice.value = fromDevice
        _selectedSeason.value = CropSeason.current(region)
    }

    fun setCapturedBitmap(bitmap: Bitmap) {
        _capturedBitmap.value = bitmap
    }

    fun startAnalysis(onSuccess: () -> Unit) {
        val bitmap = _capturedBitmap.value
        val lang = _selectedLanguage.value
        val crop = _selectedCrop.value
        val isDemo = _isDemoModeEnabled.value

        _isAnalyzing.value = true
        _analysisError.value = null
        _analysisStage.value = 1

        viewModelScope.launch {
            // Stage progress simulation for interactive feedback
            launch {
                delay(600)
                _analysisStage.value = 2
                delay(600)
                _analysisStage.value = 3
                delay(600)
                _analysisStage.value = 4
            }

            try {
                val dummyBitmap = bitmap ?: Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                val result = repository.analyzeCropImage(
                    bitmap = dummyBitmap,
                    language = lang,
                    cropContext = crop,
                    isDemoMode = isDemo || bitmap == null
                )

                _currentAnalysis.value = result
                dataStore.addScanToHistory(result)
                _isAnalyzing.value = false
                onSuccess()
            } catch (e: Exception) {
                _analysisError.value = e.localizedMessage
                _isAnalyzing.value = false
            }
        }
    }

    fun selectScanFromHistory(scan: CropAnalysisResult) {
        _currentAnalysis.value = scan
    }

    fun clearScanHistory() {
        viewModelScope.launch {
            dataStore.clearScanHistory()
        }
    }

    fun deleteScanFromHistory(id: String) {
        viewModelScope.launch {
            dataStore.deleteScanFromHistory(id)
        }
    }

    fun playAudioAdvisory() {
        val analysis = _currentAnalysis.value ?: return
        val textToSpeak = analysis.advisory.ifBlank {
            "${analysis.crop}. ${analysis.diseaseName}."
        }
        ttsManager.speak(textToSpeak, _selectedLanguage.value)
    }

    fun stopAudioAdvisory() {
        ttsManager.stop()
    }

    fun resetScan() {
        ttsManager.stop()
        _capturedBitmap.value = null
        _currentAnalysis.value = null
        _analysisError.value = null
        _isAnalyzing.value = false
        _analysisStage.value = 1
    }

    // --- Chat ---

    /**
     * Starts a brand-new standalone conversation, discarding any previous one.
     * Note: simply navigating to the Chat tab does NOT call this - the ViewModel is
     * Activity-scoped, so an in-progress conversation survives navigation within the session.
     */
    fun startFreshChat() {
        beginNewChat(seed = null)
    }

    /** Starts a new conversation grounded in a specific past scan. */
    fun openChatAboutScan(scan: CropAnalysisResult) {
        beginNewChat(ChatSeed.Scan(scan))
    }

    /** Starts a new conversation grounded in the crops of a cropping season in the selected region. */
    fun openChatAboutSeason(season: CropSeason, region: CropRegion) {
        beginNewChat(ChatSeed.Season(season, region))
    }

    private fun beginNewChat(seed: ChatSeed?) {
        ttsManager.stop()
        speechRecognizer.release()
        repository.startNewChat(seed)
        _chatMessages.value = emptyList()
        _chatError.value = null
        _isChatSending.value = false
        _speakingMessageId.value = null
        _chatSeed.value = seed
    }

    fun sendChatMessage(text: String) {
        val question = text.trim()
        if (question.isEmpty() || _isChatSending.value) return

        _chatError.value = null
        _chatMessages.value = _chatMessages.value + ChatMessage(text = question, isUser = true)
        _isChatSending.value = true

        viewModelScope.launch {
            repository.askFarmingQuestion(question, _selectedLanguage.value)
                .onSuccess { answer ->
                    _chatMessages.value = _chatMessages.value + ChatMessage(text = answer, isUser = false)
                }
                .onFailure { error ->
                    Log.e("MainViewModel", "Chat question failed", error)
                    _chatError.value = error.localizedMessage ?: "Could not reach the AI assistant."
                }
            _isChatSending.value = false
        }
    }

    fun dismissChatError() {
        _chatError.value = null
    }

    /** Surfaces a screen-level problem (e.g. a denied microphone permission) in the error banner. */
    fun showChatError(message: String) {
        _chatError.value = message
    }

    fun speakChatMessage(message: ChatMessage) {
        _speakingMessageId.value = message.id
        ttsManager.speak(message.text, _selectedLanguage.value)
    }

    fun startVoiceInput() {
        _chatError.value = null
        speechRecognizer.startListening(
            language = _selectedLanguage.value,
            onResult = { spoken -> sendChatMessage(spoken) },
            onError = { message -> _chatError.value = message }
        )
    }

    fun stopVoiceInput() {
        speechRecognizer.stopListening()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
        speechRecognizer.release()
    }
}
