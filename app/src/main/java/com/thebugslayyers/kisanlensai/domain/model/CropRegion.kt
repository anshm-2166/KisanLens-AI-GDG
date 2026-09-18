package com.thebugslayyers.kisanlensai.domain.model

/**
 * The two broad cropping calendars the app supports.
 *
 * This is deliberately coarse. India has many agro-climatic zones, but the single most useful
 * division is the one the Vindhya-Satpura range already makes: the Indo-Gangetic plains to the
 * north, and the Deccan peninsula to the south. Their sowing calendars genuinely differ - Rabi is
 * sown Sep-Oct in the peninsula but Oct-Dec in the north, and Zaid runs Jan-Feb there against
 * Mar-Apr in the north.
 *
 * Tamil Nadu is folded into [PENINSULAR_INDIA] rather than split out, but note that its main
 * cropping season is driven by the north-east monsoon (Oct-Dec) rather than the south-west one, so
 * its dates run a little later than the rest of the peninsula.
 */
enum class CropRegion(
    val displayNameEn: String,
    val displayNameHi: String
) {
    NORTH_INDIAN_PLAINS(
        displayNameEn = "North India",
        displayNameHi = "उत्तर भारत"
    ),
    PENINSULAR_INDIA(
        displayNameEn = "Peninsular India",
        displayNameHi = "प्रायद्वीपीय भारत"
    );

    fun getLocalizedName(language: Language): String =
        if (language == Language.HINDI) displayNameHi else displayNameEn

    /** The other region, for a two-way toggle. */
    fun other(): CropRegion = if (this == NORTH_INDIAN_PLAINS) PENINSULAR_INDIA else NORTH_INDIAN_PLAINS

    companion object {
        /**
         * Roughly the Vindhya-Satpura line, which is the traditional north/peninsula divide.
         *
         * Checked against real cities: Chennai (13.1), Bengaluru (13.0), Hyderabad (17.4),
         * Mumbai (19.1), Pune (18.5) and Nagpur (21.2) all fall to the peninsula, while
         * Bhopal (23.3), Ahmedabad (23.0), Kolkata (22.6) and Prayagraj (25.4) stay north.
         */
        const val PENINSULAR_BOUNDARY_LATITUDE = 21.5

        fun forCoordinates(latitude: Double, longitude: Double): CropRegion =
            if (latitude < PENINSULAR_BOUNDARY_LATITUDE) PENINSULAR_INDIA else NORTH_INDIAN_PLAINS
    }
}
