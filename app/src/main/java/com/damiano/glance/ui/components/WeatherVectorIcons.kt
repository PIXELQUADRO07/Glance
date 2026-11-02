package com.damiano.glance.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon

@Composable
fun WeatherIcon(weatherCode: Int, isDay: Int, modifier: Modifier = Modifier, tint: Color = Color.White) {
    val icon = when (weatherCode) {
        0 -> Icons.Default.Star
        1, 2, 3 -> Icons.Default.Info
        45, 48 -> Icons.Default.Settings
        51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> Icons.Default.Place
        71, 73, 75, 77, 85, 86 -> Icons.Default.Check
        95, 96, 99 -> Icons.Default.Warning
        else -> Icons.Default.Star
    }
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = modifier,
        tint = tint
    )
}
