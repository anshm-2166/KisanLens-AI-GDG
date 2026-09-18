package com.thebugslayyers.kisanlensai

import com.thebugslayyers.kisanlensai.data.mock.DemoDataProvider
import com.thebugslayyers.kisanlensai.domain.model.CropType
import com.thebugslayyers.kisanlensai.domain.model.Language
import com.thebugslayyers.kisanlensai.domain.model.Severity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoDataProviderTest {

    @Test
    fun testTomatoDemoResultInHindi() {
        val result = DemoDataProvider.getDemoResult(CropType.TOMATO, Language.HINDI)
        assertNotNull(result)
        assertTrue(result.diseaseDetected)
        assertEquals("hi", result.language)
        assertEquals(Severity.MODERATE, result.severity)
        assertTrue(result.advisory.isNotBlank())
        assertTrue(result.isDemoResult)
    }

    @Test
    fun testTomatoDemoResultInEnglish() {
        val result = DemoDataProvider.getDemoResult(CropType.TOMATO, Language.ENGLISH)
        assertNotNull(result)
        assertTrue(result.diseaseDetected)
        assertEquals("en", result.language)
        assertEquals("Tomato", result.crop)
        assertTrue(result.advisory.isNotBlank())
    }

    @Test
    fun testRiceHealthyResult() {
        val result = DemoDataProvider.getDemoResult(CropType.RICE, Language.ENGLISH)
        assertNotNull(result)
        assertEquals(false, result.diseaseDetected)
        assertEquals(Severity.LOW, result.severity)
    }
}
