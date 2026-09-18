package com.thebugslayyers.kisanlensai.domain.model

/**
 * A crop recommended for a particular [CropSeason] in a particular [CropRegion], with a one-line
 * reason a farmer would care about. Follows the same paired-display-name convention as [CropType].
 *
 * The same crop can appear under both regions when it is genuinely grown in both - Ragi, for
 * instance, is a Kharif crop in the north and a Kharif crop in the peninsula too - but with notes
 * written for each.
 */
data class SeasonalCrop(
    val season: CropSeason,
    val region: CropRegion,
    val nameEn: String,
    val nameHi: String,
    val noteEn: String,
    val noteHi: String
) {
    fun getLocalizedName(language: Language): String =
        if (language == Language.HINDI) nameHi else nameEn

    fun getLocalizedNote(language: Language): String =
        if (language == Language.HINDI) noteHi else noteEn
}
