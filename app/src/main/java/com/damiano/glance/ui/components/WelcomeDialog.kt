package com.damiano.glance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.damiano.glance.ui.TvButton

@Composable
fun WelcomeDialog(
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(700.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(36.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Benvenuto su Glance 🌤️",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Il tuo assistente meteo elegante e immersivo.",
                    color = Color(0xFF98CCFD),
                    fontSize = 18.sp
                )

                HorizontalDivider(color = Color(0xFF334155))

                // Mini Tutorial points
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TutorialItem(
                        emoji = "🌍",
                        title = "Meteo in Tempo Reale & 24 Ore",
                        description = "Consulta temperatura percepita, vento, umidità, qualità dell'aria e previsioni orarie."
                    )
                    TutorialItem(
                        emoji = "🌄",
                        title = "Sfondi di Natura Dinamici",
                        description = "Sfondi di paesaggi naturali mozzafiato che cambiano in base al meteo e all'orario."
                    )
                    TutorialItem(
                        emoji = "🌙",
                        title = "Fasi Lunari & Sole",
                        description = "Tieni traccia delle fasi lunari, dell'illuminazione, dell'alba e del tramonto."
                    )
                    TutorialItem(
                        emoji = "⚙️",
                        title = "Impostazioni & Personalizzazione",
                        description = "Gestisci la frequenza di aggiornamento dati, i permessi e le preferenze."
                    )
                }

                HorizontalDivider(color = Color(0xFF334155))

                // Credits
                Text(
                    text = "Sviluppato da Damiano • Powered by Open-Meteo",
                    color = Color(0xFFC2C7CE),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                TvButton(
                    text = "Inizia a esplorare",
                    onClick = onDismiss
                )
            }
        }
    }
}

@Composable
fun TutorialItem(emoji: String, title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            fontSize = 28.sp
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = Color(0xFFC2C7CE),
                fontSize = 14.sp
            )
        }
    }
}
