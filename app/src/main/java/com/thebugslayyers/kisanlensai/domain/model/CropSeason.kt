package com.thebugslayyers.kisanlensai.domain.model

import java.util.Calendar

/**
 * The three cropping seasons of Indian agriculture.
 *
 * Sowing windows differ by [CropRegion]. The peninsula sows Rabi about a month earlier than the
 * northern plains - Sep-Oct instead of Oct-Dec - and harvests it by February, which shifts Zaid
 * forward to a Jan-Feb sowing as well. Both the month-to-season mapping and the displayed window
 * therefore need to know where the farmer is.
 */
enum class CropSeason(
    val displayNameEn: String,
    val displayNameHi: String
) {
    KHARIF(
        displayNameEn = "Kharif",
        displayNameHi = "खरीफ"
    ),
    RABI(
        displayNameEn = "Rabi",
        displayNameHi = "रबी"
    ),
    ZAID(
        displayNameEn = "Zaid",
        displayNameHi = "ज़ायद"
    );

    fun getLocalizedName(language: Language): String =
        if (language == Language.HINDI) displayNameHi else displayNameEn

    /** The sowing and harvest window for this season in [region]. */
    fun getLocalizedWindow(region: CropRegion, language: Language): String {
        val hindi = language == Language.HINDI
        return when (region) {
            CropRegion.NORTH_INDIAN_PLAINS -> when (this) {
                KHARIF -> if (hindi) "बोना जून–जुलाई · कटाई अक्टूबर–नवंबर"
                else "Sown Jun–Jul · Harvested Oct–Nov"

                RABI -> if (hindi) "बोना अक्टूबर–दिसंबर · कटाई मार्च–अप्रैल"
                else "Sown Oct–Dec · Harvested Mar–Apr"

                ZAID -> if (hindi) "बोना मार्च–अप्रैल · कटाई जून"
                else "Sown Mar–Apr · Harvested Jun"
            }

            CropRegion.PENINSULAR_INDIA -> when (this) {
                KHARIF -> if (hindi) "बोना जून–जुलाई · कटाई अक्टूबर–नवंबर"
                else "Sown Jun–Jul · Harvested Oct–Nov"

                RABI -> if (hindi) "बोना सितंबर–अक्टूबर · कटाई जनवरी–फरवरी"
                else "Sown Sep–Oct · Harvested Jan–Feb"

                ZAID -> if (hindi) "बोना जनवरी–फरवरी · कटाई अप्रैल–मई"
                else "Sown Jan–Feb · Harvested Apr–May"
            }
        }
    }

    companion object {
        /**
         * The season a farmer is standing in: either the crop still in the field or the one about
         * to be sown.
         *
         * The two regions diverge from September. In the north, September is late Kharif - the crop
         * is ripening and Rabi sowing is still weeks away. In the peninsula, Rabi sowing is already
         * under way.
         */
        fun forMonth(month: Int, region: CropRegion): CropSeason = when (region) {
            CropRegion.NORTH_INDIAN_PLAINS -> when (month) {
                6, 7, 8, 9 -> KHARIF
                10, 11, 12, 1, 2 -> RABI
                else -> ZAID // 3, 4, 5
            }

            CropRegion.PENINSULAR_INDIA -> when (month) {
                6, 7, 8 -> KHARIF
                9, 10, 11, 12, 1 -> RABI // sown Sep-Oct, harvested Jan-Feb
                else -> ZAID // 2, 3, 4, 5
            }
        }

        /** North-Indian plains, for callers that have no location to go on. */
        fun forMonth(month: Int): CropSeason = forMonth(month, CropRegion.NORTH_INDIAN_PLAINS)

        /**
         * Uses [Calendar] rather than java.time, which needs core library desugaring to run on
         * this app's minSdk of 24.
         */
        fun current(region: CropRegion): CropSeason =
            forMonth(Calendar.getInstance().get(Calendar.MONTH) + 1, region) // Calendar.MONTH is 0-based

        fun current(): CropSeason = current(CropRegion.NORTH_INDIAN_PLAINS)
    }
}
