# CameraX Skill

## Purpose
Provides guidelines for implementing and managing CameraX lifecycle-aware camera capture in Jetpack Compose for KisanLens AI.

## When to Use
Use when working on the crop camera scanner, leaf frame detection overlay, photo capture, gallery fallbacks, or image pre-validation.

## Key Implementation Guidelines
- **Lifecycle Integration**: Bind `ImageCapture` and `Preview` to `LocalLifecycleOwner.current`.
- **Target Resolution**: Set optimal aspect ratio (4:3 or 16:9) suitable for mobile vision model analysis without excessive memory usage.
- **Preview View**: Use `AndroidView` wrapping `PreviewView` with `ImplementationMode.COMPATIBLE` or `PERFORMANCE`.
- **Image Pre-Validation**: Ensure image is checked for basic quality before passing to Gemini Vision (e.g. valid file size, non-empty byte buffer, daylight lighting check).
- **Farmer-Centric UX**:
  - Clear bounding overlay box "Place leaf here".
  - Simple guidance: "Use daylight", "Keep the leaf in focus".
  - Large touch targets for photo capture button.
  - Smooth transitions to preview/analysis state.
  - Gallery picker fallback if camera permission is rejected.

## Code Pattern Example
```kotlin
val context = LocalContext.current
val lifecycleOwner = LocalLifecycleOwner.current
val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
```
