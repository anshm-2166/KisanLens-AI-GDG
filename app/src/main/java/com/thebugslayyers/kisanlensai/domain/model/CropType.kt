package com.thebugslayyers.kisanlensai.domain.model

enum class CropType(val displayNameEn: String, val displayNameHi: String) {
    AUTO("Auto Detect", "स्वचालित पहचान"),
    TOMATO("Tomato", "टमाटर"),
    WHEAT("Wheat", "गेहूं"),
    RICE("Rice", "धान / चावल"),
    POTATO("Potato", "आलू"),
    COTTON("Cotton", "कपास"),
    OTHER("Other Crop", "अन्य फसल");

    fun getLocalizedName(language: Language): String {
        return if (language == Language.HINDI) displayNameHi else displayNameEn
    }
}
