package com.thebugslayyers.kisanlensai.domain.model

enum class Language(val code: String, val displayName: String, val nativeName: String) {
    HINDI("hi", "Hindi", "हिन्दी"),
    ENGLISH("en", "English", "English");

    companion object {
        fun fromCode(code: String): Language {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: HINDI
        }
    }
}
