# KisanLens AI — Judging Criteria Mapping

This document details how **KisanLens AI** explicitly addresses and satisfies each of the 5 official judging criteria for **Problem Statement 04 — Agricultural Intelligence**.

---

## 1. AI Execution — 25%

* **Multimodal Google AI Integration**: KisanLens uses Google Gemini 1.5/2.0 Flash Vision directly through the Google Generative AI SDK to perform real-time, end-to-end multimodal image diagnosis of crop leaves.
* **Deterministic Structured JSON Output**: Gemini is prompted to return strict, typed JSON schema containing `crop`, `diseaseDetected`, `diseaseName`, `confidence`, `severity`, `visualEvidence`, `immediateActions`, `preventiveActions`, and localized `advisory`.
* **Multi-Stage AI Analysis Pipeline**: The app guides the user through visible analysis stages (*Examining leaf → Identifying symptoms → Assessing severity → Preparing advice*) to deliver a transparent, interactive AI experience rather than a decorative chatbot.
* **Agricultural Safety & Quality Checks**: Incorporates image quality validation (blurriness, lighting) and safe advisory wording (*"Likely..."*, conservative remedies, mandatory advice to consult local extension officers for chemical application).

---

## 2. Deployability — 25%

* **Production-Grade Native Android Architecture**: Built using Modern Android Development (MAD) standards with Kotlin, Jetpack Compose (Material 3), MVVM, Coroutines, StateFlow, CameraX, and DataStore.
* **Network & Offline Resilience**: Fully functional offline camera experience with graceful error handling, cached advisories, and fail-safe local Demo Mode for low-connectivity rural environments.
* **Scalable Backend Structure**: Firebase AI Logic + Firestore architecture designed with state, district, and farm data partitioning (`stateCode`, `districtCode`, `farmId`) for seamless cross-state rollout.
* **Dual-Language Localization**: Full string localization layer in English (`res/values/`) and Hindi (`res/values-hi/`), persisting user preference across app sessions.

---

## 3. Inclusivity — 15%

* **Voice-First Zero-Literacy Design**: Integrated Android `TextToSpeech` engine allows farmers to hear complete advisories in Hindi or English with simple Play, Pause/Stop, and Replay controls.
* **Camera-First User Journey**: Replaces complex text typing and search with a simple **See → Capture → AI → Hear advice** workflow.
* **Farmer-Centric Accessibility**: Designed with large touch targets, high contrast sunlight-readable colors, visual icons alongside text, and simple language avoiding complex technical jargon.
* **Immediate Language Onboarding**: First-launch onboarding explicitly prompts the user to select Hindi or English with zero mandatory registration barriers.

---

## 4. Problem Fit — 20%

* **Direct PS-04 Alignment**: Solves the core challenge of real-time localized agro-advisories for small and marginal farmers.
* **Interoperable Digital Agriculture Network**: Extends beyond disease diagnosis with an integrated `AgroAdvisoryRepository` combining crop vision, weather forecasting, soil health metrics, and satellite vegetation indices.
* **Clear Data Transparency**: Live AI results are clearly distinguished from simulated extension point data (weather/soil/satellite), maintaining data integrity.

---

## 5. Impact — 10%

* **Early Disease Detection**: Helps farmers identify crop threats before widespread yield loss occurs.
* **Resource & Economic Efficiency**: Encourages preventative measures and avoids unnecessary chemical overuse.
* **Democratized Agricultural Expertise**: Brings university-level agricultural advisory directly to smartphone-holding smallholder farmers.
* **Digital Public Good Scalability**: Modular architecture ready for integration with national agricultural data networks (Agristack / ICAR / State Krishi Vigyan Kendras).
