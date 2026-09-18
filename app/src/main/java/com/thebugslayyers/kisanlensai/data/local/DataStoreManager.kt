package com.thebugslayyers.kisanlensai.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.thebugslayyers.kisanlensai.domain.model.CropAnalysisResult
import com.thebugslayyers.kisanlensai.domain.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "kisan_lens_prefs")

class DataStoreManager(private val context: Context) {

    private val gson = Gson()

    companion object {
        val KEY_LANGUAGE = stringPreferencesKey("selected_language")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_DEMO_MODE = booleanPreferencesKey("demo_mode_enabled")
        val KEY_SCAN_HISTORY = stringPreferencesKey("scan_history_json")
    }

    val selectedLanguageFlow: Flow<Language> = context.dataStore.data.map { prefs ->
        val code = prefs[KEY_LANGUAGE] ?: Language.HINDI.code
        Language.fromCode(code)
    }

    val isOnboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDING_COMPLETED] ?: false
    }

    val isDemoModeEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DEMO_MODE] ?: false
    }

    val scanHistoryFlow: Flow<List<CropAnalysisResult>> = context.dataStore.data.map { prefs ->
        val json = prefs[KEY_SCAN_HISTORY] ?: ""
        if (json.isBlank()) {
            emptyList()
        } else {
            try {
                val type = object : TypeToken<List<CropAnalysisResult>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun setLanguage(language: Language) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = language.code
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setDemoModeEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DEMO_MODE] = enabled
        }
    }

    suspend fun addScanToHistory(scan: CropAnalysisResult) {
        context.dataStore.edit { prefs ->
            val json = prefs[KEY_SCAN_HISTORY] ?: ""
            val currentList = if (json.isBlank()) {
                mutableListOf()
            } else {
                try {
                    val type = object : TypeToken<List<CropAnalysisResult>>() {}.type
                    val list: List<CropAnalysisResult> = gson.fromJson(json, type) ?: emptyList()
                    list.toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
            }

            currentList.add(0, scan) // add newest first
            // keep up to 50 scans
            if (currentList.size > 50) {
                currentList.removeAt(currentList.lastIndex)
            }

            prefs[KEY_SCAN_HISTORY] = gson.toJson(currentList)
        }
    }

    suspend fun clearScanHistory() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_SCAN_HISTORY)
        }
    }

    suspend fun deleteScanFromHistory(scanId: String) {
        context.dataStore.edit { prefs ->
            val json = prefs[KEY_SCAN_HISTORY] ?: ""
            if (json.isNotBlank()) {
                try {
                    val type = object : TypeToken<List<CropAnalysisResult>>() {}.type
                    val list: List<CropAnalysisResult> = gson.fromJson(json, type) ?: emptyList()
                    val filtered = list.filter { it.id != scanId }
                    prefs[KEY_SCAN_HISTORY] = gson.toJson(filtered)
                } catch (e: Exception) {
                    prefs.remove(KEY_SCAN_HISTORY)
                }
            }
        }
    }
}
