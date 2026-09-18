package com.thebugslayyers.kisanlensai

import com.thebugslayyers.kisanlensai.core.ai.GeminiChatService
import com.thebugslayyers.kisanlensai.data.mock.DemoDataProvider
import com.thebugslayyers.kisanlensai.data.mock.SeasonalCropsProvider
import com.thebugslayyers.kisanlensai.domain.model.ChatSeed
import com.thebugslayyers.kisanlensai.domain.model.CropRegion
import com.thebugslayyers.kisanlensai.domain.model.CropSeason
import com.thebugslayyers.kisanlensai.domain.model.CropType
import com.thebugslayyers.kisanlensai.domain.model.Language
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-logic tests for the Home seasonal-crops section and the chat context seeding.
 *
 * Deliberately offline: [GeminiLiveTest] is the only place that spends API quota, and the free
 * tier allows roughly 20 requests per model per day, so anything that can be checked without the
 * network is checked here instead.
 */
class SeasonalCropsTest {

    @Test
    fun everySeasonHasRecommendedCropsInBothRegions() {
        CropRegion.entries.forEach { region ->
            CropSeason.entries.forEach { season ->
                val crops = SeasonalCropsProvider.cropsFor(season, region)
                assertTrue("No crops listed for ${season.displayNameEn} in $region", crops.isNotEmpty())
            }
        }
    }

    /** Crops must belong to the season they are filed under, or the tabs would show wrong data. */
    @Test
    fun cropsAreFiledUnderTheirOwnSeasonAndRegion() {
        CropRegion.entries.forEach { region ->
            CropSeason.entries.forEach { season ->
                SeasonalCropsProvider.cropsFor(season, region).forEach { crop ->
                    assertEquals("${crop.nameEn} is filed under the wrong season", season, crop.season)
                    assertEquals("${crop.nameEn} is filed under the wrong region", region, crop.region)
                }
            }
        }
    }

    @Test
    fun cropNamesAreUniqueWithinASeasonAndRegion() {
        CropRegion.entries.forEach { region ->
            CropSeason.entries.forEach { season ->
                val names = SeasonalCropsProvider.cropsFor(season, region).map { it.nameEn }
                assertEquals(
                    "Duplicate crop names in ${season.displayNameEn} / $region",
                    names.size,
                    names.toSet().size
                )
            }
        }
    }

    /** A crop card with a blank name or note renders as an empty tile. */
    @Test
    fun everyCropHasBothLanguagesFilledIn() {
        CropRegion.entries.forEach { region ->
            CropSeason.entries.forEach { season ->
                SeasonalCropsProvider.cropsFor(season, region).forEach { crop ->
                    listOf(crop.nameEn, crop.nameHi, crop.noteEn, crop.noteHi).forEach { value ->
                        assertTrue("Blank field on ${crop.nameEn}", value.isNotBlank())
                    }
                }
            }
        }
    }

    /** Every month must land in a season, or the Home screen would have nothing to show. */
    @Test
    fun everyMonthMapsToASeasonInBothRegions() {
        CropRegion.entries.forEach { region ->
            (1..12).forEach { month ->
                val season = CropSeason.forMonth(month, region)
                assertTrue("Month $month mapped outside the enum", season in CropSeason.entries)
            }
        }
    }

    @Test
    fun seasonsFollowTheNorthIndianSowingCalendar() {
        // Kharif: monsoon, in the field from June through the September harvest run-up.
        assertEquals(CropSeason.KHARIF, CropSeason.forMonth(6, CropRegion.NORTH_INDIAN_PLAINS))
        assertEquals(CropSeason.KHARIF, CropSeason.forMonth(9, CropRegion.NORTH_INDIAN_PLAINS))

        // Rabi: sown Oct-Dec, standing through Jan-Feb.
        assertEquals(CropSeason.RABI, CropSeason.forMonth(11, CropRegion.NORTH_INDIAN_PLAINS))
        assertEquals(CropSeason.RABI, CropSeason.forMonth(1, CropRegion.NORTH_INDIAN_PLAINS))

        // Zaid: the short summer season.
        assertEquals(CropSeason.ZAID, CropSeason.forMonth(4, CropRegion.NORTH_INDIAN_PLAINS))
    }

    @Test
    fun seasonsFollowThePeninsularSowingCalendar() {
        // Same Kharif window - both regions get the south-west monsoon.
        assertEquals(CropSeason.KHARIF, CropSeason.forMonth(6, CropRegion.PENINSULAR_INDIA))
        assertEquals(CropSeason.KHARIF, CropSeason.forMonth(8, CropRegion.PENINSULAR_INDIA))

        // Rabi is sown a month earlier and harvested by February.
        assertEquals(CropSeason.RABI, CropSeason.forMonth(9, CropRegion.PENINSULAR_INDIA))
        assertEquals(CropSeason.RABI, CropSeason.forMonth(1, CropRegion.PENINSULAR_INDIA))

        // Zaid runs Jan-Feb through Apr-May, not Mar-Apr through June.
        assertEquals(CropSeason.ZAID, CropSeason.forMonth(2, CropRegion.PENINSULAR_INDIA))
        assertEquals(CropSeason.ZAID, CropSeason.forMonth(5, CropRegion.PENINSULAR_INDIA))
    }

    /**
     * The whole point of the region split: September and February must not give the same answer on
     * both sides of the Vindhyas, or the two calendars are not actually different.
     */
    @Test
    fun theTwoRegionsDisagreeInTheTransitionMonths() {
        assertNotEquals(
            "September should be late Kharif in the north but Rabi sowing in the peninsula",
            CropSeason.forMonth(9, CropRegion.NORTH_INDIAN_PLAINS),
            CropSeason.forMonth(9, CropRegion.PENINSULAR_INDIA)
        )
        assertNotEquals(
            "February should be Rabi in the north but Zaid sowing in the peninsula",
            CropSeason.forMonth(2, CropRegion.NORTH_INDIAN_PLAINS),
            CropSeason.forMonth(2, CropRegion.PENINSULAR_INDIA)
        )
    }

    // --- Region detection from coordinates ---

    @Test
    fun citiesMapToTheExpectedCroppingRegion() {
        val peninsular = listOf(
            "Chennai" to (13.08 to 80.27),
            "Bengaluru" to (12.97 to 77.59),
            "Hyderabad" to (17.39 to 78.49),
            "Mumbai" to (19.08 to 72.88),
            "Pune" to (18.52 to 73.86),
            "Nagpur" to (21.15 to 79.09)
        )
        peninsular.forEach { (city, coords) ->
            assertEquals(
                "$city should be in the peninsula",
                CropRegion.PENINSULAR_INDIA,
                CropRegion.forCoordinates(coords.first, coords.second)
            )
        }

        val northern = listOf(
            "Prayagraj" to (25.44 to 81.85),
            "Delhi" to (28.61 to 77.21),
            "Ludhiana" to (30.90 to 75.86),
            "Bhopal" to (23.26 to 77.41),
            "Kolkata" to (22.57 to 88.36),
            "Ahmedabad" to (23.02 to 72.57)
        )
        northern.forEach { (city, coords) ->
            assertEquals(
                "$city should be in the northern plains",
                CropRegion.NORTH_INDIAN_PLAINS,
                CropRegion.forCoordinates(coords.first, coords.second)
            )
        }
    }

    @Test
    fun regionsHaveLocalisedNamesThatDifferByLanguage() {
        CropRegion.entries.forEach { region ->
            assertTrue(region.getLocalizedName(Language.ENGLISH).isNotBlank())
            assertTrue(region.getLocalizedName(Language.HINDI).isNotBlank())
            assertNotEquals(
                "Region name is identical in both languages",
                region.getLocalizedName(Language.ENGLISH),
                region.getLocalizedName(Language.HINDI)
            )
        }
    }

    @Test
    fun togglingRegionReturnsTheOtherOneAndComesBack() {
        assertEquals(
            CropRegion.PENINSULAR_INDIA,
            CropRegion.NORTH_INDIAN_PLAINS.other()
        )
        assertEquals(
            CropRegion.NORTH_INDIAN_PLAINS,
            CropRegion.PENINSULAR_INDIA.other()
        )
    }

    @Test
    fun seasonsHaveLocalisedNamesAndWindowsInBothRegions() {
        CropRegion.entries.forEach { region ->
            CropSeason.entries.forEach { season ->
                Language.entries.forEach { language ->
                    assertTrue(
                        "Empty name for $season in $language",
                        season.getLocalizedName(language).isNotBlank()
                    )
                    assertTrue(
                        "Empty sowing window for $season / $region in $language",
                        season.getLocalizedWindow(region, language).isNotBlank()
                    )
                }
            }
        }
        // The two languages must not silently collapse to the same string.
        assertNotEquals(
            CropSeason.KHARIF.getLocalizedName(Language.HINDI),
            CropSeason.KHARIF.getLocalizedName(Language.ENGLISH)
        )
    }

    /** Rabi's window is the one that genuinely differs between the two regions. */
    @Test
    fun rabiWindowReflectsTheRegion() {
        assertNotEquals(
            "Rabi sowing window should differ between the plains and the peninsula",
            CropSeason.RABI.getLocalizedWindow(CropRegion.NORTH_INDIAN_PLAINS, Language.ENGLISH),
            CropSeason.RABI.getLocalizedWindow(CropRegion.PENINSULAR_INDIA, Language.ENGLISH)
        )
    }

    // --- Chat seeding ---

    @Test
    fun plainChatCarriesNoContext() {
        val service = GeminiChatService()
        service.startNewConversation(null)

        val instruction = service.buildSystemInstruction(Language.ENGLISH)
        assertFalse("An unseeded chat should not mention a season", instruction.contains("Kharif season"))
        assertFalse("An unseeded chat should not mention a scan", instruction.contains("recently scanned"))
    }

    @Test
    fun seasonSeedGroundsThePromptInThatSeasonsCrops() {
        val service = GeminiChatService()
        service.startNewConversation(
            ChatSeed.Season(CropSeason.KHARIF, CropRegion.NORTH_INDIAN_PLAINS)
        )
        assertTrue("Service should report context", service.hasContext)

        val instruction = service.buildSystemInstruction(Language.ENGLISH)
        assertTrue("Prompt should name the season", instruction.contains("Kharif"))
        // A crop unique to Kharif proves the right season's list was injected.
        assertTrue("Prompt should list Kharif crops", instruction.contains("Ragi"))
        assertFalse("Prompt must not leak another season's crops", instruction.contains("Mustard"))
    }

    /**
     * The seed now carries a region, so a peninsular Rabi chat must get the peninsular crop list -
     * rabi jowar and safflower - and none of the northern-only crops.
     */
    @Test
    fun seasonSeedIsScopedToTheRegionNotJustTheSeason() {
        val service = GeminiChatService()
        service.startNewConversation(
            ChatSeed.Season(CropSeason.RABI, CropRegion.PENINSULAR_INDIA)
        )

        val instruction = service.buildSystemInstruction(Language.ENGLISH)
        assertTrue("Prompt should name the region", instruction.contains("Peninsular India"))
        assertTrue("Prompt should carry peninsular rabi crops", instruction.contains("Rabi Jowar"))
        assertTrue("Prompt should carry peninsular rabi crops", instruction.contains("Safflower"))
        assertFalse("Mustard is a northern rabi crop", instruction.contains("Mustard"))
        assertFalse("Barley is a northern rabi crop", instruction.contains("Barley"))
    }

    @Test
    fun scanSeedGroundsThePromptInThatScan() {
        val scan = DemoDataProvider.getDemoResult(CropType.TOMATO, Language.ENGLISH)
        val service = GeminiChatService()
        service.startNewConversation(ChatSeed.Scan(scan))

        val instruction = service.buildSystemInstruction(Language.ENGLISH)
        assertTrue("Prompt should carry the scanned crop", instruction.contains(scan.crop))
        assertTrue("Prompt should carry the finding", instruction.contains(scan.diseaseName))
    }

    /**
     * The season block is written in English but the language rule still governs the reply, so a
     * Hindi request must still be told to answer in Hindi.
     */
    @Test
    fun seasonSeedStillHonoursTheRequestedLanguage() {
        val service = GeminiChatService()
        service.startNewConversation(
            ChatSeed.Season(CropSeason.RABI, CropRegion.PENINSULAR_INDIA)
        )

        val instruction = service.buildSystemInstruction(Language.HINDI)
        assertTrue("Prompt should ask for Hindi", instruction.contains("Hindi"))
        assertTrue("Prompt should still carry the season", instruction.contains("Rabi"))
    }
}
