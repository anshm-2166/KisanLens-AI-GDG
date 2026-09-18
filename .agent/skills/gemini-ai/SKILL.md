# Gemini AI Integration Skill

## Purpose
Defines the integration standards for Google AI Gemini Vision multimodal analysis in KisanLens AI.

## When to Use
Use when implementing or modifying crop disease analysis, prompt engineering, structured JSON parsing, or error resilience for Gemini models.

## Multimodal Vision Rules
1. **Model Selection**: Use `gemini-1.5-flash` or `gemini-2.0-flash` for high speed and accurate vision reasoning.
2. **Structured JSON Output**: System instructions must enforce strict JSON format without prose headers or markdown code block fences if requesting JSON directly.
3. **Prompt Design**:
   - Provide explicit context: selected language (`hi` for Hindi, `en` for English), optional crop type context.
   - Instruct Gemini to act as an expert agricultural extension officer.
   - Mandate conservative, practical remedies avoiding specific chemical dosages.
   - Mandate confidence score and visual evidence listing.
4. **Safety & Accuracy Guardrails**:
   - Never invent dosage or illegal chemical advice.
   - Always append recommendation to consult local agricultural experts for severe cases.
   - Handle low-confidence / blurry images gracefully by setting `diseaseDetected = false` or returning low confidence with advice to retake photo.

## Expected JSON Schema
```json
{
  "crop": "Tomato",
  "diseaseDetected": true,
  "diseaseName": "Early blight",
  "confidence": 0.91,
  "severity": "MODERATE",
  "visualEvidence": ["dark lesions on leaves", "yellowing around affected areas"],
  "immediateActions": ["Remove affected leaves", "Avoid overhead watering"],
  "preventiveActions": ["Ensure proper spacing", "Monitor nearby crops"],
  "whenToSeekExpertHelp": "If spots spread rapidly to stems or adjacent fields.",
  "advisory": "Clean affected area and keep foliage dry.",
  "language": "hi"
}
```
