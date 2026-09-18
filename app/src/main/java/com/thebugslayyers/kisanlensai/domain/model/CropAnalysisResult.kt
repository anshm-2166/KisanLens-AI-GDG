package com.thebugslayyers.kisanlensai.domain.model

data class CropAnalysisResult(
    val id: String = System.currentTimeMillis().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val crop: String = "Unknown Crop",
    val diseaseDetected: Boolean = false,
    val diseaseName: String = "Healthy Crop",
    val confidence: Float = 0.90f,
    val severity: Severity = Severity.LOW,
    val visualEvidence: List<String> = emptyList(),
    val immediateActions: List<String> = emptyList(),
    val correctiveMeasures: List<String> = emptyList(),
    val preventiveActions: List<String> = emptyList(),
    val chemicalSafetyGuidance: String = "",
    val whenToSeekExpertHelp: String = "",
    val advisory: String = "",
    val language: String = "hi",
    val isDemoResult: Boolean = false,
    val imagePath: String? = null
)
