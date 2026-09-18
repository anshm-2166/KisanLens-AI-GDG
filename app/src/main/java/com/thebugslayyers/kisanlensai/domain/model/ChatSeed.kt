package com.thebugslayyers.kisanlensai.domain.model

/**
 * Optional context a chat conversation is opened with, so the assistant already knows what the
 * farmer is looking at instead of making them repeat it.
 *
 * Sealed rather than a bare string so each entry point can supply its own grounding facts and the
 * chat screen can label the conversation correctly.
 */
sealed interface ChatSeed {

    /** Chat opened about a specific past crop scan. */
    data class Scan(val result: CropAnalysisResult) : ChatSeed

    /**
     * Chat opened about the crops worth growing in a cropping season. Carries the region too,
     * because the sowing window and the crop list both differ between the plains and the peninsula.
     */
    data class Season(val season: CropSeason, val region: CropRegion) : ChatSeed
}
