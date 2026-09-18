# KisanLens AI

[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-green.svg)](https://developer.android.com/jetpack/compose)
[![Google AI](https://img.shields.io/badge/Google%20AI-Gemini%20Flash-orange.svg)](https://ai.google.dev)

**KisanLens AI** is an Android app that turns a phone camera into a crop doctor. A farmer photographs an affected leaf; Google Gemini's vision model identifies the crop and the likely disease, rates its severity, and returns practical remedies — spoken aloud in **Hindi** or **English**.

> **Camera → Leaf Image → Gemini Vision → Diagnosis → Severity & Remedy → Voice Advisory**

---

## Features

1. **Camera-first leaf scanner** — CameraX preview with an alignment frame, lighting guidance, and a gallery fallback.
2. **Gemini multimodal diagnosis** — Returns structured JSON (crop, disease status, confidence, severity, visual evidence, immediate and preventive actions, localized advisory).
3. **Voice advisory** — Android `TextToSpeech` (`hi_IN` / `en_US`) reads the full advisory aloud, with play/stop controls.
4. **Ask Kisan AI** — Voice or text chat for general farming questions, answered in the farmer's chosen language.
5. **Scan history** — Every diagnosis is saved on device, reopenable, and deletable.
6. **Farm insights** — Weather, soil health, and satellite vegetation (NDVI) panels for the detected region.
7. **Seasonal crop guide** — Offline dataset of what to sow this season, selected by region.
8. **Bilingual UI** — Complete English (`res/values/`) and Hindi (`res/values-hi/`) string sets; language choice persisted in DataStore.
9. **Demo mode** — On-device sample dataset so the app is fully explorable without spending API quota.
10. **Safety-first advisories** — Conservative wording ("likely…"), no chemical doses or brand names, and a standing prompt to consult a local Krishi Vigyan Kendra or extension officer for severe cases.

---

## Tech Stack

| Layer | Technology |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM, Coroutines, StateFlow, Navigation Compose |
| AI | Google Generative AI SDK — Gemini Flash for vision diagnosis and chat |
| Camera | CameraX (core, camera2, lifecycle, view) |
| Audio | Android `TextToSpeech` (`hi_IN` / `en_US`), `SpeechRecognizer` |
| Local persistence | DataStore Preferences + on-device scan history |
| Image loading | Coil |
| JSON | Gson |
| Location | Platform `LocationManager` (no Play Services dependency) |

---

## Setup & Build

**Prerequisites:** Android Studio Ladybug or newer, JDK 17+, Android SDK 35+.

1. Clone the repository:
   ```bash
   git clone <repo-url>
   cd KisanLensAI
   ```
2. Add your Gemini API key to `local.properties`:
   ```properties
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```
3. Build the debug APK:
   ```bash
   ./gradlew app:assembleDebug
   ```
4. Run the unit tests:
   ```bash
   ./gradlew app:testDebugUnitTest
   ```

The app is fully usable without a key: point it at the bundled demo dataset and every screen renders end to end.

---

## Project Structure

```
app/src/main/java/com/thebugslayyers/kisanlensai/
├── core/            ai (Gemini analyzer + chat), audio (TTS, speech), location
├── data/            local (DataStore), mock (demo + seasonal crop datasets), repository
├── domain/          models and repository interfaces
├── feature/         onboarding, home, camera, analysis, result, farm, scans, chat, impact
└── ui/              theme, navigation, MainViewModel
```

---

## Notes & Limitations

- The **Farm Insights** weather, soil, and satellite panels are simulated extension points. They demonstrate how a live data feed would slot in; they are not connected to a live provider, and the UI labels them as demo data.
- The crop vision and chat calls run against Google AI Studio's free tier, so throughput is bounded by that quota. **Demo mode** exists so the app stays fully demonstrable when the quota is spent.
- Advisories are guidance, not a prescription. Chemical treatment decisions are deliberately left to local agricultural officers.
