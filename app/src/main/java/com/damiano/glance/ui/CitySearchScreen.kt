package com.damiano.glance.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.damiano.glance.domain.CityResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySearchScreen(
    searchResults: List<CityResult>,
    isSearching: Boolean,
    onDismiss: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onSelectCity: (CityResult) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101418))
            .padding(48.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cambia città",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                TvButton(text = "Annulla", onClick = onDismiss)
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    onQueryChanged(it)
                },
                placeholder = { Text("Digita il nome della città (es. Roma, Milano...)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF98CCFD),
                    unfocusedBorderColor = Color(0xFF2A3441),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isSearching) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF98CCFD))
                }
            } else if (searchResults.isEmpty() && query.trim().length >= 2) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Nessun risultato trovato",
                        color = Color(0xFFC2C7CE),
                        fontSize = 18.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(searchResults) { city ->
                        CityResultItem(city = city, onClick = { onSelectCity(city) })
                    }
                }
            }
        }
    }
}

@Composable
fun CityResultItem(
    city: CityResult,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.03f else 1.0f)
    val backgroundColor = if (isFocused) Color(0xFF98CCFD) else Color(0x33000000)
    val textColor = if (isFocused) Color(0xFF101418) else Color.White
    val subtitleColor = if (isFocused) Color(0xFF2A3441) else Color(0xFFC2C7CE)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .then(Modifier.clickable(onClick = onClick)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = city.name,
                color = textColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${city.region.ifEmpty { "--" }} • ${city.country}",
                color = subtitleColor,
                fontSize = 16.sp
            )
        }
    }
}
