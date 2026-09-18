# Hackathon Demo Mode Skill

## Purpose
Ensures a deterministic, fail-safe demonstration path for hackathon judges during live presentations.

## When to Use
Use when configuring demo sample images, offline fallback mock responses, developer toggle switches, or judge demonstration flows.

## Rules for Demo Mode
1. **Live First**: The default app behavior uses live Gemini Vision AI via internet connection.
2. **Demo Mode Fallback**: A local toggle in Settings/Developer options or automatic fallback on network timeout triggers Demo Mode.
3. **Sample Dataset**: Include local assets/resources for standard agricultural conditions:
   - Tomato Early Blight
   - Wheat Rust
   - Rice Blast
   - Healthy Crop
4. **UI Transparency**:
   - Results from Demo Mode are clearly tagged with a "Demo / Simulation" badge to maintain judges' trust.
   - Live AI results display a "Live Gemini AI" badge.
5. **Seamless Flow**: The demo journey must execute within 90 seconds without crashing or stalling.
