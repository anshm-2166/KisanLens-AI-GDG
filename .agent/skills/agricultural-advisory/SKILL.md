# Agricultural Advisory Skill

## Purpose
Defines domain concepts, data models, mock data providers, and safety guardrails for localized agricultural advisories in KisanLens AI.

## When to Use
Use when implementing `AgroAdvisoryRepository`, weather/soil/satellite mock services, or advisory generation logic for PS-04 alignment.

## Core Concepts & Data Abstractions
- **Domain Repositories**:
  - `AgroAdvisoryRepository`: Orchestrates image diagnosis, weather context, soil metrics, and satellite health.
  - `WeatherRepository`: Delivers localized weather forecasts (rain probability, temperature, humidity).
  - `SoilRepository`: Delivers soil health metrics (moisture, pH, NPK balance).
  - `SatelliteRepository`: Delivers vegetation health indices (NDVI, canopy cover status).
- **Transparency & Safety**:
  - Clear labeling of mock / simulated data ("Demo data").
  - Non-prescriptive chemical guidance; emphasize local extension officer consultation.
  - Wording: "Likely...", "Possible...", "Based on visible symptoms...".
- **State & District Scalability**:
  - All domain observations include `stateCode`, `districtCode`, `cropType`, and `timestamp` to demonstrate national digital public good scalability.
