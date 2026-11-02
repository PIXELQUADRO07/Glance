package com.damiano.glance.ui.components

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object WeatherBackground {
    fun getGradient(weatherCode: Int, isDay: Int): Brush {
        val colors = when {
            weatherCode == 0 && isDay == 1 -> listOf(Color(0xFF1E3C72), Color(0xFF2A5298), Color(0xFF0F2027))
            weatherCode == 0 && isDay == 0 -> listOf(Color(0xFF090A0F), Color(0xFF131826), Color(0xFF1C2541))
            weatherCode in 1..3 && isDay == 1 -> listOf(Color(0xFF2C3E50), Color(0xFF4CA1AF))
            weatherCode in 1..3 && isDay == 0 -> listOf(Color(0xFF16222A), Color(0xFF3A6073))
            weatherCode in 51..67 || weatherCode in 80..82 -> listOf(Color(0xFF203A43), Color(0xFF2C5364), Color(0xFF0F2027))
            weatherCode in 95..99 -> listOf(Color(0xFF232526), Color(0xFF414345), Color(0xFF141E30))
            weatherCode in 71..77 || weatherCode in 85..86 -> listOf(Color(0xFF2980B9), Color(0xFF6DD5FA), Color(0xFF243B55))
            else -> listOf(Color(0xFF141E30), Color(0xFF243B55))
        }
        return Brush.verticalGradient(colors)
    }
}
