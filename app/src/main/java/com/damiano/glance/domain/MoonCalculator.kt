package com.damiano.glance.domain

import kotlin.math.cos

data class MoonInfo(
    val phaseName: String,
    val illuminationPercent: Int,
    val emoji: String,
    val phaseFraction: Double
)

object MoonCalculator {
    // Known New Moon: January 6, 2000, 18:14 UTC
    private const val KNOWN_NEW_MOON_MS = 947182440000L
    private const val SYNODIC_MONTH_DAYS = 29.53058867
    private const val MS_PER_DAY = 24 * 60 * 60 * 1000.0

    fun calculateMoon(timestampMs: Long = System.currentTimeMillis()): MoonInfo {
        val diffMs = timestampMs - KNOWN_NEW_MOON_MS
        val diffDays = diffMs / MS_PER_DAY
        val cycles = diffDays / SYNODIC_MONTH_DAYS
        var fraction = cycles - cycles.toLong()
        if (fraction < 0) fraction += 1.0

        val illumination = ((1.0 - cos(2.0 * Math.PI * fraction)) / 2.0) * 100.0
        val illuminationInt = illumination.toInt()

        val phaseName = when {
            fraction < 0.03 || fraction >= 0.97 -> "Luna nuova"
            fraction < 0.22 -> "Luna crescente"
            fraction < 0.28 -> "Primo quarto"
            fraction < 0.47 -> "Gibbosa crescente"
            fraction < 0.53 -> "Luna piena"
            fraction < 0.72 -> "Gibbosa calante"
            fraction < 0.78 -> "Ultimo quarto"
            else -> "Luna calante"
        }

        val emoji = when {
            fraction < 0.03 || fraction >= 0.97 -> "🌑"
            fraction < 0.22 -> "🌒"
            fraction < 0.28 -> "🌓"
            fraction < 0.47 -> "🌔"
            fraction < 0.53 -> "🌕"
            fraction < 0.72 -> "🌖"
            fraction < 0.78 -> "🌗"
            else -> "🌘"
        }

        return MoonInfo(
            phaseName = phaseName,
            illuminationPercent = illuminationInt,
            emoji = emoji,
            phaseFraction = fraction
        )
    }
}
