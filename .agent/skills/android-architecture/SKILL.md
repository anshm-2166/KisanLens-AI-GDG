# Android Architecture Skill

## Purpose
Defines the clean MVVM feature-based architecture for KisanLens AI, enforcing reactive StateFlow data streams, Jetpack Compose UI, domain abstractions, and repository patterns.

## When to Use
Use when creating or refactoring viewmodels, repositories, data models, or UI screens across features in KisanLens AI.

## Project Rules & Architecture Constraints
- **Pattern**: Clean-ish MVVM + Repository pattern.
- **UI Framework**: Pure Jetpack Compose with Material 3 design system.
- **State Management**: Unidirectional Data Flow (UDF) using `StateFlow` and `UiState` sealed interfaces or data classes in ViewModels.
- **Concurrency**: Kotlin Coroutines with `viewModelScope` and `Dispatchers.IO` for repository/network operations.
- **Navigation**: Single Activity (`MainActivity.kt`) with `NavHost` and Compose Navigation.
- **Data Sharing**: DataStore Preferences for lightweight key-value settings (e.g. language selection, onboarding status, demo mode flag).
- **Prohibitions**:
  - Do NOT leak Android Context into ViewModels or domain repositories.
  - Do NOT make network or disk calls on `Dispatchers.Main`.
  - Do NOT put business logic inside Composable UI functions.

## Package Structure
```
app/src/main/java/com/thebugslayyers/kisanlensai/
├── core/
│   ├── ai/            # Gemini & LLM engine integrations
│   ├── audio/         # TextToSpeech audio player engine
│   ├── camera/        # CameraX image capture & validation utilities
│   ├── network/       # Connectivity monitor
│   └── common/        # Extensions, Result wrappers, Theme
├── data/
│   ├── local/         # DataStore preferences & local storage
│   ├── mock/          # Mock data providers (Weather, Soil, Satellite)
│   ├── repository/    # Repository implementations
│   └── model/         # Data DTOs
├── domain/
│   ├── model/         # Domain entities (CropAnalysisResult, FarmProfile, Advisory)
│   ├── repository/    # Abstractions (AgroAdvisoryRepository, WeatherRepository)
│   └── usecase/       # Business logic usecases
└── feature/
    ├── onboarding/    # Onboarding & Language selection screens
    ├── home/          # Home dashboard screen
    ├── camera/        # CameraX scanner screen
    ├── analysis/      # Disease analysis & loading screen
    ├── result/        # Analysis result & TextToSpeech screen
    ├── farm/          # Farm Insights (Weather/Soil/Satellite) screen
    └── impact/        # Impact & DPG scalability screen
```
