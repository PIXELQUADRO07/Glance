package com.damiano.glance.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

@Composable
fun NatureLandscapeBackground(weatherCode: Int, isDay: Int, modifier: Modifier = Modifier) {
    val skyColors = when {
        weatherCode == 0 && isDay == 1 -> listOf(Color(0xFF2980B9), Color(0xFF6DD5FA), Color(0xFFFFFFFF))
        weatherCode == 0 && isDay == 0 -> listOf(Color(0xFF0B0F19), Color(0xFF1B2A4A), Color(0xFF0F172A))
        weatherCode in 1..3 && isDay == 1 -> listOf(Color(0xFF4CA1AF), Color(0xFF2C3E50))
        weatherCode in 1..3 && isDay == 0 -> listOf(Color(0xFF1F2937), Color(0xFF111827))
        weatherCode in 51..67 || weatherCode in 80..82 -> listOf(Color(0xFF2C5364), Color(0xFF203A43), Color(0xFF0F2027))
        weatherCode in 95..99 -> listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF090D16))
        weatherCode in 71..77 || weatherCode in 85..86 -> listOf(Color(0xFF60A5FA), Color(0xFF93C5FD), Color(0xFFE0F2FE))
        else -> listOf(Color(0xFF1E3A8A), Color(0xFF1E293B))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(skyColors))
    ) {
        // Nature & Landscape Vector Illustration Layer with subtle blur for soft focus effect
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(16.dp)
        ) {
            val width = size.width
            val height = size.height

            // Sun or Moon
            if (isDay == 1 && weatherCode == 0) {
                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = 0.8f),
                    radius = 80f,
                    center = Offset(width * 0.8f, height * 0.25f)
                )
            } else if (isDay == 0) {
                drawCircle(
                    color = Color(0xFFE2E8F0).copy(alpha = 0.7f),
                    radius = 50f,
                    center = Offset(width * 0.8f, height * 0.2f)
                )
            }

            // Distant Mountains Path
            val mountainPath1 = Path().apply {
                moveTo(0f, height * 0.65f)
                cubicTo(width * 0.2f, height * 0.45f, width * 0.4f, height * 0.55f, width * 0.7f, height * 0.4f)
                cubicTo(width * 0.85f, height * 0.48f, width * 0.95f, height * 0.42f, width, height * 0.5f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = mountainPath1,
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E293B).copy(alpha = 0.6f),
                        Color(0xFF0F172A).copy(alpha = 0.8f)
                    ),
                    startY = height * 0.4f,
                    endY = height
                )
            )

            // Foreground Hills & Nature Landscape Path
            val hillsPath = Path().apply {
                moveTo(0f, height * 0.75f)
                cubicTo(width * 0.25f, height * 0.65f, width * 0.55f, height * 0.82f, width, height * 0.7f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = hillsPath,
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF064E3B).copy(alpha = 0.5f),
                        Color(0xFF022C22).copy(alpha = 0.85f)
                    ),
                    startY = height * 0.65f,
                    endY = height
                )
            )
        }

        // Dark atmospheric overlay scrim to ensure UI readability and contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.65f)
                        )
                    )
                )
        )
    }
}
