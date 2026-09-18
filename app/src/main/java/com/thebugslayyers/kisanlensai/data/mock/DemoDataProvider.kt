package com.thebugslayyers.kisanlensai.data.mock

import com.thebugslayyers.kisanlensai.domain.model.CropAnalysisResult
import com.thebugslayyers.kisanlensai.domain.model.CropType
import com.thebugslayyers.kisanlensai.domain.model.Language
import com.thebugslayyers.kisanlensai.domain.model.Severity

object DemoDataProvider {

    fun getDemoResult(cropType: CropType, language: Language): CropAnalysisResult {
        return when (cropType) {
            CropType.TOMATO, CropType.AUTO -> if (language == Language.HINDI) {
                CropAnalysisResult(
                    crop = "टमाटर (Tomato)",
                    diseaseDetected = true,
                    diseaseName = "अगेती झुलसा (Early Blight)",
                    confidence = 0.92f,
                    severity = Severity.MODERATE,
                    visualEvidence = listOf("पत्तियों पर गहरे भूरे गोल धब्बे", "धब्बों के चारों ओर पीलापन", "निचली पत्तियों पर अधिक प्रभाव"),
                    immediateActions = listOf("अधिक संक्रमित पत्तियों को तोड़कर नष्ट कर दें।", "पौधों की जड़ों पर ही पानी दें, पत्तियों पर पानी छिड़कने से बचें।", "हवा के संचार के लिए पौधों के बीच पर्याप्त स्थान रखें।"),
                    correctiveMeasures = listOf(
                        "जैविक उपचार: 10% नीम के तेल के घोल (5 मि.ली./लीटर पानी) का छिड़काव पत्तियों पर करें।",
                        "ट्राइकोडरमा विरिडी (5 ग्राम/लीटर) से जड़ों का जल उपचार करें।",
                        "सिंचाई प्रबंधन: ड्रिप या जड़ के पास ही पानी दें ताकि पत्तियों पर नमी न ठहरे।",
                        "पौधों को लकड़ी या बांस के सहारे बांधें (Staking) जिससे पत्तियां जमीन और मिट्टी से दूर रहें।"
                    ),
                    preventiveActions = listOf("फसल चक्र अपनाएं और टमाटर के बाद तुरंत बैंगन या मिर्च न लगाएं।", "वर्षा के मौसम में जल निकासी की सही व्यवस्था रखें।"),
                    chemicalSafetyGuidance = "फफूंदनाशी (जैसे मैनकोज़ेब 75% WP, 2 ग्राम/लीटर पानी) का उपयोग केवल अत्यधिक संक्रमण में करें। छिड़काव के समय दस्ताने पहनें और उत्पाद के लेबल की हिदायतों का पालन करें।",
                    whenToSeekExpertHelp = "यदि धब्बे पौधे के मुख्य तने पर फैल जाएं या पूरे खेत में तेजी से फैलें तो तुरंत निकटतम कृषि विज्ञान केंद्र (KVK) से संपर्क करें।",
                    advisory = "आपकी टमाटर की फसल में अगेती झुलसा रोग के लक्षण दिखे हैं। तुरंत प्रभावित पत्तियां हटा दें, नीम तेल का छिड़काव करें और पत्तियों को सूखा रखें।",
                    language = "hi",
                    isDemoResult = true
                )
            } else {
                CropAnalysisResult(
                    crop = "Tomato",
                    diseaseDetected = true,
                    diseaseName = "Early Blight (Alternaria solani)",
                    confidence = 0.92f,
                    severity = Severity.MODERATE,
                    visualEvidence = listOf("Dark brown concentric lesions on leaves", "Yellow halo surrounding spots", "Primary impact on lower foliage"),
                    immediateActions = listOf("Prune and destroy severely infected lower leaves.", "Water at root base; avoid overhead foliage irrigation.", "Improve plant spacing to ensure healthy air circulation."),
                    correctiveMeasures = listOf(
                        "Organic Treatment: Spray 0.5% Neem oil extract (5 ml/L water) evenly over leaf surfaces.",
                        "Bio-Fungicide: Apply Trichoderma viride root drenching to suppress soil-borne fungal spores.",
                        "Canopy Elevation: Stake plants with wooden poles to elevate lower leaves away from soil contact.",
                        "Field Moisture Management: Use drip irrigation or furrow watering to eliminate leaf wetness."
                    ),
                    preventiveActions = listOf("Practice crop rotation with non-solanaceous crops (e.g., maize, legumes).", "Ensure proper field drainage during monsoon seasons."),
                    chemicalSafetyGuidance = "For severe spread, consult extension officers for copper hydroxide or Mancozeb 75% WP spray application. Always wear protective gear and strictly follow product safety labels.",
                    whenToSeekExpertHelp = "If concentric spots spread rapidly to stems or neighboring plots, contact your local Krishi Vigyan Kendra officer.",
                    advisory = "Symptoms indicate Early Blight on Tomato. Remove affected foliage immediately, apply bio-fungicide, and keep foliage dry.",
                    language = "en",
                    isDemoResult = true
                )
            }

            CropType.WHEAT -> if (language == Language.HINDI) {
                CropAnalysisResult(
                    crop = "गेहूं (Wheat)",
                    diseaseDetected = true,
                    diseaseName = "पत्ती का गेरुई रोग (Leaf Rust)",
                    confidence = 0.88f,
                    severity = Severity.HIGH,
                    visualEvidence = listOf("पत्तियों के ऊपरी हिस्से पर नारंगी-भूरे धब्बे", "पत्ती छूने पर पाउडर जैसा पदार्थ निकलना"),
                    immediateActions = listOf("खेत में अत्यधिक नाइट्रोजन खाद देने से बचें।", "सिंचाई की मात्रा नियंत्रित रखें और जलभराव न होने दें।"),
                    correctiveMeasures = listOf(
                        "पोटेशियम उर्वरक की संतुलित मात्रा दें जिससे पौधे में प्रतिरोधक क्षमता बढ़े।",
                        "संक्रमित पत्तियों को खेत से दूर हटाकर नष्ट करें।",
                        "फफूंदनाशी (प्रोपीकोनाज़ोल 25% EC, 1 मि.ली./लीटर पानी) का छिड़काव विशेषज्ञ की सलाह अनुसार करें।"
                    ),
                    preventiveActions = listOf("गेरुई प्रतिरोधी उन्नत किस्मों के बीज का चयन करें।", "बुआई के समय उचित बीज उपचार करें।"),
                    chemicalSafetyGuidance = "रासायनिक छिड़काव के समय हवा की दिशा का ध्यान रखें और सुरक्षात्मक मास्क पहनें।",
                    whenToSeekExpertHelp = "गेरुई फैलने की स्थिति में संस्तुत फफूंदनाशी के उचित उपयोग के लिए तुरंत ब्लॉक कृषि अधिकारी से संपर्क करें।",
                    advisory = "गेहूं में पत्ती के गेरुई रोग के लक्षण पाए गए हैं। अत्यधिक नाइट्रोजन खाद न दें और संस्तुत फफूंदनाशक का छिड़काव करें।",
                    language = "hi",
                    isDemoResult = true
                )
            } else {
                CropAnalysisResult(
                    crop = "Wheat",
                    diseaseDetected = true,
                    diseaseName = "Leaf Rust (Puccinia triticina)",
                    confidence = 0.88f,
                    severity = Severity.HIGH,
                    visualEvidence = listOf("Bright orange-brown pustules on upper leaf blade", "Dusty spore powder rubbed off on touch"),
                    immediateActions = listOf("Avoid excess nitrogenous fertilizer top-dressing.", "Regulate field moisture and avoid stagnant water."),
                    correctiveMeasures = listOf(
                        "Apply balanced Potash (MOP) to boost plant cell wall resistance.",
                        "Clear and destroy volunteer wheat plants along field bunds.",
                        "Foliar Spray: Apply Propiconazole 25% EC (1 ml/L) as per agricultural officer guidance."
                    ),
                    preventiveActions = listOf("Sow rust-resistant certified wheat seed varieties.", "Treat seeds with bio-fungicides prior to sowing."),
                    chemicalSafetyGuidance = "Follow strict dilution ratios for chemical sprays. Never mix unauthorized pesticides.",
                    whenToSeekExpertHelp = "Consult block agricultural extension officers immediately if rust pustules cover more than 15% leaf area.",
                    advisory = "Leaf Rust symptoms identified in Wheat. Avoid excessive nitrogen applications and consult local extension experts for fungicide spraying.",
                    language = "en",
                    isDemoResult = true
                )
            }

            CropType.RICE -> if (language == Language.HINDI) {
                CropAnalysisResult(
                    crop = "धान (Rice)",
                    diseaseDetected = false,
                    diseaseName = "स्वस्थ फसल (Healthy Crop)",
                    confidence = 0.95f,
                    severity = Severity.LOW,
                    visualEvidence = listOf("पत्तियां गहरी हरी और चमकदार हैं", "कोई कीट या धब्बे नहीं दिखे"),
                    immediateActions = listOf("वर्तमान संतुलित सिंचाई और पोषण प्रबंधन जारी रखें।", "नियमित रूप से खेत का निरीक्षण करते रहें।"),
                    correctiveMeasures = listOf(
                        "संतुलित एनपीके (NPK) उर्वरकों का प्रयोग जारी रखें।",
                        "खेत के मेड़ों की सफाई रखें ताकि कीट शरण न ले सकें।"
                    ),
                    preventiveActions = listOf("खरपतवार नियंत्रण समय पर करें।", "मेड़ों को साफ रखें।"),
                    chemicalSafetyGuidance = "फसल स्वस्थ है। किसी भी रासायनिक फफूंदनाशी या कीटनाशक के छिड़काव की आवश्यकता नहीं है।",
                    whenToSeekExpertHelp = "यदि पत्तियों के रंग में कोई असामान्य परिवर्तन दिखे तो फोटो खींचकर पुनः जांच करें।",
                    advisory = "आपकी धान की फसल बिल्कुल स्वस्थ दिखाई दे रही है। संतुलित सिंचाई बनाए रखें और नियमित देखभाल करते रहें।",
                    language = "hi",
                    isDemoResult = true
                )
            } else {
                CropAnalysisResult(
                    crop = "Rice",
                    diseaseDetected = false,
                    diseaseName = "Healthy Crop",
                    confidence = 0.95f,
                    severity = Severity.LOW,
                    visualEvidence = listOf("Vibrant green uniform leaf color", "No visible lesions or pest infestation"),
                    immediateActions = listOf("Maintain current balanced irrigation and fertilization routines.", "Continue periodic visual inspection of tillers."),
                    correctiveMeasures = listOf(
                        "Maintain intermittent wetting and drying irrigation cycles.",
                        "Keep field borders clean to prevent pest harboring."
                    ),
                    preventiveActions = listOf("Keep field bunds clean to prevent weed hosts."),
                    chemicalSafetyGuidance = "Crop is in healthy condition. No chemical intervention required.",
                    whenToSeekExpertHelp = "Rescan if any leaf discoloration or stem borers appear.",
                    advisory = "Your Rice crop appears healthy with excellent vigor. Maintain current field management and clean bunds.",
                    language = "en",
                    isDemoResult = true
                )
            }

            else -> if (language == Language.HINDI) {
                CropAnalysisResult(
                    crop = "आलू (Potato)",
                    diseaseDetected = true,
                    diseaseName = "पछेती झुलसा (Late Blight)",
                    confidence = 0.89f,
                    severity = Severity.MODERATE,
                    visualEvidence = listOf("पत्तियों के किनारों पर काले-गहरे धब्बे", "पत्ती के निचले हिस्से पर सफेद फफूंद"),
                    immediateActions = listOf("सिंचाई तुरंत रोकें।", "अत्यधिक प्रभावित पत्तियों को हटा दें।"),
                    correctiveMeasures = listOf(
                        "जलभराव तुरंत खत्म करें और नालियों को साफ करें।",
                        "कॉपर ऑक्सीक्लोराइड (3 ग्राम/लीटर पानी) का छिड़काव पत्तियों के नीचे और ऊपर करें।",
                        "संक्रमित पौधों को उखाड़कर गड्ढे में दबा दें।"
                    ),
                    preventiveActions = listOf("जलभराव न होने दें।", "प्रमाणित बीजों का उपयोग करें।"),
                    chemicalSafetyGuidance = "आद्र मौसम में फफूंदनाशी के छिड़काव के बाद 48 घंटे तक सिंचाई न करें।",
                    whenToSeekExpertHelp = "मौसम में अधिक नमी रहने पर तुरंत कृषि अधिकारी से संपर्क करें।",
                    advisory = "आपकी फसल में पछेती झुलसा के लक्षण मिले हैं। सिंचाई रोकें, प्रभावित पत्तियां हटाएं और कॉपर आधारित फफूंदनाशक का प्रयोग करें।",
                    language = "hi",
                    isDemoResult = true
                )
            } else {
                CropAnalysisResult(
                    crop = "Potato",
                    diseaseDetected = true,
                    diseaseName = "Late Blight (Phytophthora infestans)",
                    confidence = 0.89f,
                    severity = Severity.MODERATE,
                    visualEvidence = listOf("Dark water-soaked lesions on leaf margins", "White velvety fungal growth on leaf undersides"),
                    immediateActions = listOf("Halt field irrigation temporarily.", "Remove severely infected foliage."),
                    correctiveMeasures = listOf(
                        "Drain standing water immediately and ridging soil around root zone.",
                        "Spray Copper Oxychloride 50% WP (3 g/L water) or Cymoxanil combinations as directed by extension officers.",
                        "Destroy infected haulms if tubers are near maturity to prevent tuber rot."
                    ),
                    preventiveActions = listOf("Ensure well-drained soil ridging.", "Use certified disease-free tubers."),
                    chemicalSafetyGuidance = "Wear mask and goggles during fungicide spray. Do not irrigate for 48 hours post-treatment.",
                    whenToSeekExpertHelp = "Contact Krishi Vigyan Kendra immediately if humid cloudy weather persists.",
                    advisory = "Symptoms correspond to Late Blight. Suspend irrigation, apply protective bio/copper fungicides, and consult local agriculture officers.",
                    language = "en",
                    isDemoResult = true
                )
            }
        }
    }
}
