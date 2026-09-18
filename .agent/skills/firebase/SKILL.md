# Firebase & Backend Architecture Skill

## Purpose
Outlines the Firebase backend structure, Firestore data schemas, App Check security, and fallback strategy for KisanLens AI.

## When to Use
Use when configuring Firebase services, Firestore collections, cloud storage references, or backend abstractions.

## Core Services & Usage
1. **Firebase AI Logic / Vertex AI**: Cloud AI execution endpoint for Gemini.
2. **Firestore**: Storing crop observations, advisories, and farm profiles for digital public good interoperability.
3. **Firebase Storage**: Storing compressed crop images when cloud backup is enabled.
4. **Firebase App Check**: Securing API endpoints from unauthorized client abuse.

## Firestore Data Hierarchy
```
farmers/{farmerId}
  └── farmProfiles/{farmId}
cropObservations/{observationId}
  ├── cropType: String
  ├── location: GeoPoint (State/District)
  ├── imageUrl: String
  ├── timestamp: Long
  └── analysisId: String
cropAnalyses/{analysisId}
  ├── diseaseName: String
  ├── confidence: Float
  ├── severity: String
  ├── advisory: String
  └── language: String
```

## Security & Privacy Rule
- No personal identifiable information (PII) required for core disease analysis.
- Offline-first cache ensures features work gracefully if Firebase connectivity is intermittent.
