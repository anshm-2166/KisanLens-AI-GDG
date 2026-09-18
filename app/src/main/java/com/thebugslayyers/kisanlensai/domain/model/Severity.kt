package com.thebugslayyers.kisanlensai.domain.model

enum class Severity {
    LOW,
    MODERATE,
    HIGH,
    UNKNOWN;

    companion object {
        fun fromString(value: String?): Severity {
            if (value == null) return UNKNOWN
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
        }
    }
}
