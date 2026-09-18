package com.thebugslayyers.kisanlensai.domain.model

data class WeatherSnapshot(
    val locationName: String = "Prayagraj, UP",
    val temperatureCelsius: Int = 28,
    val rainProbabilityPercent: Int = 72,
    val humidityPercent: Int = 78,
    val forecastSummaryEn: String = "High rain probability in next 24-48 hrs. High humidity.",
    val forecastSummaryHi: String = "अगले 24-48 घंटों में बारिश की 72% संभावना। उच्च आर्द्रता।"
)

data class SoilSnapshot(
    val moistureLevel: String = "Moderate (42%)",
    val pH: Float = 6.8f,
    val npkStatus: String = "Optimal (N: Balanced, P: Good, K: High)",
    val summaryEn: String = "Soil moisture is adequate. Postpone immediate irrigation.",
    val summaryHi: String = "मिट्टी में पर्याप्त नमी है। सिंचाई अभी टालें।"
)

data class SatelliteInsight(
    val ndviScore: Float = 0.76f,
    val vegetationHealth: String = "Good",
    val canopyDensity: String = "High Coverage",
    val summaryEn: String = "Healthy vegetation canopy detected across Prayagraj cluster.",
    val summaryHi: String = "प्रयागराज क्षेत्र में अच्छी फसल सघनता दिखाई दे रही है।"
)

data class AgroAdvisory(
    val cropAnalysis: CropAnalysisResult? = null,
    val weather: WeatherSnapshot = WeatherSnapshot(),
    val soil: SoilSnapshot = SoilSnapshot(),
    val satellite: SatelliteInsight = SatelliteInsight(),
    val integratedAdviceEn: String = "High rain probability (72%) with adequate soil moisture. Avoid overhead watering or spraying fungicides immediately before rainfall.",
    val integratedAdviceHi: String = "72% बारिश की संभावना और मिट्टी में नमी के कारण अभी सिंचाई रोकें। बारिश से पहले पत्तियों पर कीटनाशक का छिड़काव न करें।"
)
