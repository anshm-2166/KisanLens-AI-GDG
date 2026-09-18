# KisanLens AI — PS-04 Agricultural Intelligence

[![GDG Prayagraj Hackathon](https://img.shields.io/badge/Hackathon-GDG%20Prayagraj-green.svg)](https://github.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-green.svg)](https://developer.android.com/jetpack/compose)
[![Google AI](https://img.shields.io/badge/Google%20AI-Gemini%201.5%2F2.0-orange.svg)](https://ai.google.dev)

**KisanLens AI** is an AI-powered crop intelligence assistant built for small and marginal farmers across India for **Problem Statement 04 — Agricultural Intelligence** at **GDG Prayagraj Code for Community Hackathon**.

The primary experience is:
> **Camera → Crop Leaf Image → Gemini Vision → Disease Identification → Severity & Remedy → Localized Explanation → Voice Playback**

---

## 🌟 Key Features

1. **Camera-First Leaf Scanner**: CameraX powered real-time leaf alignment overlay with lighting tips and gallery fallback.
2. **Google Gemini Multimodal AI**: Real-time leaf analysis returning structured JSON (Crop Name, Disease Status, Severity, Symptoms, Immediate & Preventive Actions).
3. **Zero-Literacy Voice Advisory**: Integrated Android `TextToSpeech` engine playing localized advisories in **Hindi** (`hi`) and **English** (`en`).
4. **Dual-Language Onboarding**: Instant Hindi/English language choice persisted across sessions via DataStore.
5. **Farm Insights Engine**: Regional extension points combining Weather forecasting (rain probability), Soil health (moisture/pH), and Satellite Vegetation Health Indices (NDVI).
6. **Deterministic Hackathon Demo Mode**: Fail-safe offline sample mode ensuring seamless 90-second judge presentation regardless of internet conditions.
7. **Pan-India Digital Public Good Architecture**: State, District, and Farm level data partitioning ready for national AgriStack integration.

---

## 🛠 Tech Stack & Google Technologies

- **Language**: Kotlin 2.0+
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: Modern Android Development (MAD) MVVM + Coroutines + StateFlow
- **AI Core**: Google Generative AI (Gemini 1.5/2.0 Flash Vision)
- **Camera**: CameraX
- **Backend Infrastructure**: Firebase AI Logic, Firestore, Storage
- **Local Persistence**: DataStore Preferences
- **Audio Engine**: Android TextToSpeech (`hi_IN`, `en_US`)

---

## 🚀 Setup & Build Instructions

### Prerequisites
- Android Studio Ladybug or newer
- JDK 17+
- Android SDK 35+

### Build Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/KisanLensAI.git
   cd KisanLensAI
   ```
2. Set your Gemini API Key in `local.properties`:
   ```properties
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```
3. Assemble the debug APK:
   ```bash
   ./gradlew app:assembleDebug
   ```
4. Run Unit Tests:
   ```bash
   ./gradlew app:testDebugUnitTest
   ```

---

## 🎯 90-Second Demo Flow for Hackathon Judges

1. **Launch App**: Open KisanLens. First launch displays Onboarding.
2. **Choose Language**: Select **हिन्दी** (or English) and press **आगे बढ़ें (Continue)**.
3. **Home Screen**: View greeting, optional crop selector (Tomato, Wheat, Rice, Potato), and quick Farm Insights.
4. **Tap Hero Button**: Press `📷 फसल की पत्ती स्कैन करें` (Scan Crop Leaf).
5. **Point Camera**: Align leaf inside the green target frame and tap `📷 Analyze Crop`.
6. **AI Analysis Loading**: Watch multi-stage progress (*✓ Examining leaf → ✓ Identifying symptoms → ✓ Assessing severity → ✓ Preparing advice*).
7. **Result Screen**: View disease status, confidence score, severity pill, visual evidence, and immediate remedies.
8. **Press Voice Button**: Tap `[ 🔊 सुनें / Listen ]` to hear the localized voice advisory aloud!
9. **Farm Insights & Impact**: Navigate to **खेत स्थिति (Farm Insights)** and **प्रभाव (Impact)** tabs to inspect weather/soil/satellite extension points and DPG architecture.
