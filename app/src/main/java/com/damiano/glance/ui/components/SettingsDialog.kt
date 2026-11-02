package com.damiano.glance.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.damiano.glance.ui.TvButton

@Composable
fun SettingsDialog(
    currentRefreshInterval: Long,
    onRefreshIntervalChanged: (Long) -> Unit,
    onResetData: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showResetConfirm by remember { mutableStateOf(false) }

    val hasLocationPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(650.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Impostazioni",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TvButton(text = "Chiudi", onClick = onDismiss)
                }

                // Section 1: Refresh Frequency
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Frequenza di aggiornamento dati",
                        color = Color(0xFF98CCFD),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Seleziona ogni quanto tempo aggiornare automaticamente i dati meteo.",
                        color = Color(0xFFC2C7CE),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val intervals = listOf(15L to "15 min", 30L to "30 min", 60L to "1 ora", 180L to "3 ore")
                        intervals.forEach { (mins, label) ->
                            val isSelected = currentRefreshInterval == mins
                            TvButton(
                                text = if (isSelected) "✓ $label" else label,
                                onClick = { onRefreshIntervalChanged(mins) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF334155))

                // Section 2: App Permissions
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Permessi dell'app",
                        color = Color(0xFF98CCFD),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Stato permesso Posizione: ${if (hasLocationPermission) "Consentito" else "Non consentito / Approssimativo"}",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TvButton(
                        text = "Gestisci permessi nelle impostazioni di sistema",
                        onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }
                    )
                }

                HorizontalDivider(color = Color(0xFF334155))

                // Section 3: Danger Zone (Red Outline / Background)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, Color(0xFFEF4444), RoundedCornerShape(16.dp))
                        .background(Color(0xFF7F1D1D).copy(alpha = 0.3f))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "⚠️ Danger Zone",
                            color = Color(0xFFFCA5A5),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "La rimozione totale dei dati cancellerà la cache, le preferenze salvate e ripristinerà l'applicazione allo stato iniziale.",
                            color = Color(0xFFFEE2E2),
                            fontSize = 14.sp
                        )
                        TvButton(
                            text = "Elimina dati e ripristina app",
                            onClick = { showResetConfirm = true }
                        )
                    }
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Conferma ripristino") },
            text = { Text("Sei sicuro di voler eliminare tutti i dati e ripristinare l'applicazione?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetConfirm = false
                        onResetData()
                    }
                ) {
                    Text("Conferma", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Annulla")
                }
            },
            containerColor = Color(0xFF1E293B),
            titleContentColor = Color.White,
            textContentColor = Color(0xFFC2C7CE)
        )
    }
}
