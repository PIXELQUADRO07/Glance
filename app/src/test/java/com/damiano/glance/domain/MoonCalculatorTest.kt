package com.damiano.glance.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoonCalculatorTest {

    @Test
    fun testKnownNewMoon() {
        // January 6, 2000, 18:14 UTC -> 947182440000L
        val moon = MoonCalculator.calculateMoon(947182440000L)
        assertEquals("Luna nuova", moon.phaseName)
        assertEquals(0, moon.illuminationPercent)
        assertEquals("🌑", moon.emoji)
    }

    @Test
    fun testFullMoonApprox() {
        // Half a synodic month after January 6, 2000 (~14.76 days)
        val fullMoonMs = 947182440000L + (14.765 * 24 * 60 * 60 * 1000).toLong()
        val moon = MoonCalculator.calculateMoon(fullMoonMs)
        assertEquals("Luna piena", moon.phaseName)
        assertTrue(moon.illuminationPercent > 95)
        assertEquals("🌕", moon.emoji)
    }
}
