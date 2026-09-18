package com.thebugslayyers.kisanlensai.data.mock

import com.thebugslayyers.kisanlensai.domain.model.CropRegion
import com.thebugslayyers.kisanlensai.domain.model.CropSeason
import com.thebugslayyers.kisanlensai.domain.model.SeasonalCrop

/**
 * Static, offline list of crops worth growing in each Indian cropping season, per region.
 *
 * Deliberately a local dataset rather than a Gemini call: the Home screen must render instantly
 * and must keep working offline, and a free-tier key only allows about 20 requests per model per
 * day - spending them on a list that never changes would be wasteful.
 *
 * Notes are short, practical, and avoid naming doses or chemical brands, matching the safety
 * stance taken by the chat prompt.
 */
object SeasonalCropsProvider {

    private val crops = listOf(
        // ================= North Indian plains =================
        // ---------------- Kharif: monsoon, sown Jun-Jul ----------------
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Rice (Paddy)", nameHi = "धान",
            noteEn = "Sow with the monsoon onset; the field needs standing water.",
            noteHi = "मानसून शुरू होते ही बोएं; खेत में पानी खड़ा रहना चाहिए।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Ragi (Finger Millet)", nameHi = "रागी",
            noteEn = "Very hardy; gives a crop even on poor soil and low rain.",
            noteHi = "बहुत कम खर्चीली; कमजोर मिट्टी और कम बारिश में भी फसल देती है।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Bajra (Pearl Millet)", nameHi = "बाजरा",
            noteEn = "Drought-tolerant and well suited to sandy soil.",
            noteHi = "सूखा सहने वाली; रेतीली मिट्टी के लिए अच्छी।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Jowar (Sorghum)", nameHi = "ज्वार",
            noteEn = "Needs moderate rain; the stalks double as cattle fodder.",
            noteHi = "मध्यम बारिश चाहिए; डंठल पशुओं के चारे के काम आते हैं।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Maize", nameHi = "मक्का",
            noteEn = "Needs well-drained loam; suffers badly in waterlogging.",
            noteHi = "अच्छे जल निकास वाली दोमट मिट्टी चाहिए; जलभराव से नुकसान होता है।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Arhar (Pigeon Pea)", nameHi = "अरहर (तूर)",
            noteEn = "Long duration, but it adds nitrogen back to the soil.",
            noteHi = "अवधि लंबी है, पर मिट्टी में नाइट्रोजन बढ़ाती है।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Groundnut", nameHi = "मूंगफली",
            noteEn = "Best on sandy loam; avoid heavy clay soils.",
            noteHi = "रेतीली दोमट मिट्टी सबसे अच्छी; भारी चिकनी मिट्टी से बचें।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Soybean", nameHi = "सोयाबीन",
            noteEn = "Short duration, but drainage must be good.",
            noteHi = "अवधि कम है, पर जल निकास अच्छा होना चाहिए।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Cotton", nameHi = "कपास",
            noteEn = "Black soil and a long frost-free period suit it best.",
            noteHi = "काली मिट्टी और लंबा पाला-मुक्त समय इसके लिए सबसे अच्छा है।"
        ),

        // ---------------- Rabi: winter, sown Oct-Dec ----------------
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Wheat", nameHi = "गेहूं",
            noteEn = "Sow late Oct-Nov; needs about 4-5 timely irrigations.",
            noteHi = "अक्टूबर के अंत से नवंबर में बोएं; लगभग 4-5 सिंचाई समय पर चाहिए।"
        ),
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Mustard", nameHi = "सरसों",
            noteEn = "Light soil, low irrigation, and a good oilseed price.",
            noteHi = "हल्की मिट्टी, कम सिंचाई, और तिलहन का अच्छा दाम।"
        ),
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Gram (Chana)", nameHi = "चना",
            noteEn = "Needs little water and fixes nitrogen for the next crop.",
            noteHi = "पानी कम चाहिए और अगली फसल के लिए नाइट्रोजन छोड़ती है।"
        ),
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Barley", nameHi = "जौ",
            noteEn = "Tolerates salty and poor soil better than wheat.",
            noteHi = "गेहूं की तुलना में खारी और कमजोर मिट्टी बेहतर सहती है।"
        ),
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Peas", nameHi = "मटर",
            noteEn = "Cool weather crop; wants well-drained soil.",
            noteHi = "ठंडे मौसम की फसल; अच्छे जल निकास वाली मिट्टी चाहिए।"
        ),
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Lentil (Masoor)", nameHi = "मसूर",
            noteEn = "Low input and rainfed-friendly; good for marginal land.",
            noteHi = "कम खर्चीली; बारिश पर निर्भर खेती और कमजोर जमीन के लिए अच्छी।"
        ),
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Linseed", nameHi = "अलसी",
            noteEn = "Hardy oilseed that manages on very little water.",
            noteHi = "कम पानी में भी टिकने वाली तिलहन फसल।"
        ),
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Coriander", nameHi = "धनिया",
            noteEn = "Short duration; works well as an intercrop.",
            noteHi = "अवधि कम; सहफसल के रूप में अच्छी।"
        ),

        // ---------------- Zaid: summer, sown Mar-Apr ----------------
        SeasonalCrop(
            season = CropSeason.ZAID,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Moong (Green Gram)", nameHi = "मूंग",
            noteEn = "Quick summer pulse that also improves the soil.",
            noteHi = "गर्मी की तेज़ दलहन, जो मिट्टी भी सुधारती है।"
        ),
        SeasonalCrop(
            season = CropSeason.ZAID,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Watermelon", nameHi = "तरबूज",
            noteEn = "Does well on sandy riverbed soil; needs warmth.",
            noteHi = "रेतीली नदी-किनारे की मिट्टी में अच्छा; गर्मी चाहिए।"
        ),
        SeasonalCrop(
            season = CropSeason.ZAID,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Muskmelon", nameHi = "खरबूजा",
            noteEn = "Well-drained soil with steady, regular watering.",
            noteHi = "अच्छे जल निकास वाली मिट्टी और नियमित पानी चाहिए।"
        ),
        SeasonalCrop(
            season = CropSeason.ZAID,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Cucumber", nameHi = "खीरा",
            noteEn = "Short duration; a trellis lifts the yield.",
            noteHi = "अवधि कम; सहारा देने से पैदावार बढ़ती है।"
        ),
        SeasonalCrop(
            season = CropSeason.ZAID,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Okra (Bhindi)", nameHi = "भिंडी",
            noteEn = "Warm-season crop; pick the pods regularly.",
            noteHi = "गर्मी की फसल; फलियां नियमित तोड़ते रहें।"
        ),
        SeasonalCrop(
            season = CropSeason.ZAID,
            region = CropRegion.NORTH_INDIAN_PLAINS,
            nameEn = "Fodder Maize", nameHi = "चारा मक्का",
            noteEn = "Fast green fodder to carry cattle through summer.",
            noteHi = "गर्मी में पशुओं के लिए जल्दी मिलने वाला हरा चारा।"
        ),

        // ================= Peninsular India =================
        // ---------------- Kharif: south-west monsoon, sown Jun-Jul ----------------
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Rice (Paddy)", nameHi = "धान",
            noteEn = "The delta and coastal belt's main crop; needs assured water.",
            noteHi = "डेल्टा और तटीय क्षेत्र की मुख्य फसल; पानी की पक्की व्यवस्था चाहिए।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Ragi (Finger Millet)", nameHi = "रागी",
            noteEn = "Karnataka's staple millet; crops well on red soil with little rain.",
            noteHi = "कर्नाटक की मुख्य मोटी अनाज; लाल मिट्टी और कम बारिश में भी अच्छी।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Jowar (Sorghum)", nameHi = "ज्वार",
            noteEn = "The Deccan's main kharif cereal; the stover is good cattle feed.",
            noteHi = "दक्कन की मुख्य खरीफ अनाज; डंठल पशुओं के लिए अच्छा चारा।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Bajra (Pearl Millet)", nameHi = "बाजरा",
            noteEn = "Handles long dry spells on light, shallow soil.",
            noteHi = "हल्की, उथली मिट्टी में लंबा सूखा भी सह लेती है।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Tur (Pigeon Pea)", nameHi = "तूर (अरहर)",
            noteEn = "The backbone pulse of the Deccan; it also rebuilds the soil.",
            noteHi = "दक्कन की मुख्य दलहन; मिट्टी को भी सुधारती है।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Groundnut", nameHi = "मूंगफली",
            noteEn = "The plateau's leading kharif oilseed; avoid waterlogged patches.",
            noteHi = "पठार की मुख्य खरीफ तिलहन; जलभराव वाले हिस्सों से बचें।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Cotton", nameHi = "कपास",
            noteEn = "Suits the black cotton soil of Maharashtra and Telangana.",
            noteHi = "महाराष्ट्र और तेलंगाना की काली मिट्टी के लिए उपयुक्त।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Soybean", nameHi = "सोयाबीन",
            noteEn = "Short-duration oilseed for the central Deccan.",
            noteHi = "मध्य दक्कन के लिए कम अवधि वाली तिलहन।"
        ),
        SeasonalCrop(
            season = CropSeason.KHARIF,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Maize", nameHi = "मक्का",
            noteEn = "Needs good drainage; strong poultry-feed demand.",
            noteHi = "अच्छा जल निकास चाहिए; पोल्ट्री चारे की अच्छी मांग।"
        ),

        // ---------------- Rabi: sown Sep-Oct, harvested Jan-Feb ----------------
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Rabi Jowar", nameHi = "रबी ज्वार",
            noteEn = "Sown on stored soil moisture after the monsoon; little watering needed.",
            noteHi = "मानसून के बाद मिट्टी की बची नमी पर बोई जाती है; पानी कम चाहिए।"
        ),
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Gram (Chana)", nameHi = "हरभरा (चना)",
            noteEn = "The Deccan's main rabi pulse; needs very little water.",
            noteHi = "दक्कन की मुख्य रबी दलहन; पानी बहुत कम चाहिए।"
        ),
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Groundnut", nameHi = "मूंगफली",
            noteEn = "Rabi groundnut thrives on residual moisture in coastal Andhra.",
            noteHi = "तटीय आंध्र में रबी मूंगफली बची नमी पर अच्छी होती है।"
        ),
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Wheat", nameHi = "गेहूं",
            noteEn = "Only where the winter is cool enough; needs irrigation here.",
            noteHi = "केवल जहां सर्दी पर्याप्त ठंडी हो; यहां सिंचाई जरूरी है।"
        ),
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Sunflower", nameHi = "सूरजमुखी",
            noteEn = "Short-duration oilseed that fits between two other crops.",
            noteHi = "कम अवधि की तिलहन, जो दो फसलों के बीच फिट हो जाती है।"
        ),
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Onion", nameHi = "प्याज",
            noteEn = "A major rabi cash crop in Maharashtra; cure bulbs well before storage.",
            noteHi = "महाराष्ट्र की बड़ी रबी नकदी फसल; भंडारण से पहले प्याज अच्छी तरह सुखाएं।"
        ),
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Safflower", nameHi = "करडई",
            noteEn = "Deep-rooted oilseed that lives on very little moisture.",
            noteHi = "गहरी जड़ों वाली तिलहन, जो बहुत कम नमी में निकल आती है।"
        ),
        SeasonalCrop(
            season = CropSeason.RABI,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Maize", nameHi = "मक्का",
            noteEn = "A rabi maize crop needs irrigation through the cool season.",
            noteHi = "रबी मक्का को ठंड के मौसम में सिंचाई चाहिए।"
        ),

        // ---------------- Zaid: sown Jan-Feb, harvested Apr-May ----------------
        SeasonalCrop(
            season = CropSeason.ZAID,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Summer Rice (Paddy)", nameHi = "गर्मी का धान",
            noteEn = "Delta areas take a third paddy crop where canal water allows.",
            noteHi = "डेल्टा क्षेत्र में नहर का पानी हो तो तीसरी धान फसल ली जाती है।"
        ),
        SeasonalCrop(
            season = CropSeason.ZAID,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Groundnut", nameHi = "मूंगफली",
            noteEn = "Summer groundnut under irrigation earns well.",
            noteHi = "सिंचाई वाली गर्मी की मूंगफली अच्छा मुनाफा देती है।"
        ),
        SeasonalCrop(
            season = CropSeason.ZAID,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Sesame (Til)", nameHi = "तिल",
            noteEn = "A short summer oilseed that manages on very little water.",
            noteHi = "गर्मी की कम अवधि वाली तिलहन, जो बहुत कम पानी में चलती है।"
        ),
        SeasonalCrop(
            season = CropSeason.ZAID,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Moong (Green Gram)", nameHi = "मूंग",
            noteEn = "A fast summer pulse that also improves the soil.",
            noteHi = "गर्मी की तेज़ दलहन, जो मिट्टी भी सुधारती है।"
        ),
        SeasonalCrop(
            season = CropSeason.ZAID,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Watermelon", nameHi = "तरबूज",
            noteEn = "Grown on sandy riverbeds and along the coast.",
            noteHi = "रेतीली नदी-किनारे की जमीन और तटीय क्षेत्र में उगाया जाता है।"
        ),
        SeasonalCrop(
            season = CropSeason.ZAID,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Cucumber", nameHi = "खीरा",
            noteEn = "Short duration and steady demand through the hot months.",
            noteHi = "अवधि कम और गर्मी के महीनों में अच्छी मांग।"
        ),
        SeasonalCrop(
            season = CropSeason.ZAID,
            region = CropRegion.PENINSULAR_INDIA,
            nameEn = "Fodder Maize", nameHi = "चारा मक्का",
            noteEn = "Fast green fodder to carry cattle through the dry months.",
            noteHi = "सूखे महीनों में पशुओं के लिए जल्दी मिलने वाला हरा चारा।"
        )
    )

    /** Crops for [season] in [region], in the order they are declared above. */
    fun cropsFor(season: CropSeason, region: CropRegion): List<SeasonalCrop> =
        crops.filter { it.season == season && it.region == region }

    /** English names only - used to ground the chat prompt, which translates on its own. */
    fun cropNamesFor(season: CropSeason, region: CropRegion): List<String> =
        cropsFor(season, region).map { it.nameEn }
}
