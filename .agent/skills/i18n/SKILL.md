# Internationalization & Voice Accessibility Skill (i18n)

## Purpose
Enforces dual-language support (Hindi & English) and zero-literacy voice playback capabilities across KisanLens AI.

## When to Use
Use when writing strings, UI layouts, TextToSpeech engine wrappers, or language selection onboarding components.

## Core Directives
1. **Language Choice**: Onboarding explicitly offers Hindi (`hi`) and English (`en`).
2. **Persistence**: Choice stored in `DataStoreManager` and applied globally via app locale configuration and strings.
3. **Resource Management**:
   - Primary default strings in `res/values/strings.xml` (English).
   - Hindi translations in `res/values-hi/strings.xml`.
   - Never hardcode user-facing visible UI text in Kotlin source code.
4. **Voice Output (TTS)**:
   - Android `TextToSpeech` engine configured with locale matching user preference (`Locale("hi", "IN")` for Hindi, `Locale.ENGLISH` for English).
   - Audio controller providing Play, Pause/Stop, Replay state controls.
   - Core user flow MUST enable "See -> Capture -> AI -> Hear advice" without mandatory reading.
5. **UI Accessibility**:
   - Minimum tap target size 48dp x 48dp (preferred 56dp+ for primary actions).
   - High contrast colors for sunlight outdoor visibility.
   - Text with paired visual icons for low-literacy clarity.
