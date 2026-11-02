package com.damiano.glance.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import com.damiano.glance.domain.DailyWeather
import com.damiano.glance.domain.HourlyWeather
import com.damiano.glance.domain.LocationInfo
import com.damiano.glance.domain.MoonCalculator
import com.damiano.glance.domain.WeatherData
import com.damiano.glance.ui.components.NatureLandscapeBackground
import com.damiano.glance.ui.components.SettingsDialog
import com.damiano.glance.ui.components.WeatherIcon
import com.damiano.glance.ui.components.WelcomeDialog
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WeatherScreen(
    state: WeatherUiState,
    onRefresh: () -> Unit,
    onShowCitySearch: (Boolean) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSelectCity: (com.damiano.glance.domain.CityResult) -> Unit,
    onShowSettings: (Boolean) -> Unit,
    onUpdateRefreshInterval: (Long) -> Unit,
    onResetAppData: () -> Unit,
    onDismissWelcome: () -> Unit
) {
    BackHandler(enabled = state is WeatherUiState.Success && (state.showCitySearch || state.showSettings)) {
        if (state is WeatherUiState.Success) {
            if (state.showCitySearch) onShowCitySearch(false)
            if (state.showSettings) onShowSettings(false)
        }
    }

    when (state) {
        is WeatherUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF101418)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF98CCFD))
            }
        }
        is WeatherUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF101418)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = state.message, color = Color(0xFFFFB4AB), fontSize = 22.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    TvButton(text = "Riprova", onClick = onRefresh)
                }
            }
        }
        is WeatherUiState.Success -> {
            Box(modifier = Modifier.fillMaxSize()) {
                NatureLandscapeBackground(
                    weatherCode = state.weather.current.weatherCode,
                    isDay = state.weather.current.isDay
                )

                if (state.showWelcome) {
                    WelcomeDialog(onDismiss = onDismissWelcome)
                } else if (state.showCitySearch) {
                    CitySearchScreen(
                        searchResults = state.searchResults,
                        isSearching = state.isSearching,
                        onDismiss = { onShowCitySearch(false) },
                        onQueryChanged = onSearchQueryChanged,
                        onSelectCity = onSelectCity
                    )
                } else if (state.showSettings) {
                    SettingsDialog(
                        currentRefreshInterval = state.refreshInterval,
                        onRefreshIntervalChanged = { minutes ->
                            onUpdateRefreshInterval(minutes)
                        },
                        onResetData = onResetAppData,
                        onDismiss = { onShowSettings(false) }
                    )
                } else {
                    MainWeatherContent(
                        weather = state.weather,
                        location = state.location,
                        onRefresh = onRefresh,
                        onChangeCityClick = { onShowCitySearch(true) },
                        onSettingsClick = { onShowSettings(true) }
                    )
                }
            }
        }
    }
}

@Composable
fun MainWeatherContent(
    weather: WeatherData,
    location: LocationInfo,
    onRefresh: () -> Unit,
    onChangeCityClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val current = weather.current
    val moon = MoonCalculator.calculateMoon()
    val windDirText = getWindDirectionText(current.windDirection)

    val updateTimeStr = remember(weather.lastUpdated) {
        try {
            val instant = java.time.Instant.ofEpochMilli(weather.lastUpdated)
            val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ITALIAN)
                .withZone(java.time.ZoneId.systemDefault())
            formatter.format(instant)
        } catch (_: Exception) {
            "--"
        }
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 28.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${location.name}, ${location.region.ifEmpty { location.country }}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (weather.isCached) "Modalità offline (dati salvati alle $updateTimeStr)" else "Ultimo aggiornamento: $updateTimeStr",
                            color = Color(0xFFC2C7CE),
                            fontSize = 16.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        TvButton(
                            text = "Cambia città",
                            onClick = onChangeCityClick,
                            modifier = Modifier.focusRequester(focusRequester)
                        )
                        TvButton(text = "Impostazioni", onClick = onSettingsClick)
                        TvButton(text = "Aggiorna", onClick = onRefresh)
                    }
                }
            }

            // Main TV Dashboard Grid (Hero Left & Right Details/Hourly)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Left Column: Hero Card
                    Card(
                        modifier = Modifier
                            .weight(1.2f)
                            .height(340.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x33000000))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(28.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = getWeatherConditionText(current.weatherCode),
                                    color = Color(0xFF98CCFD),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                WeatherIcon(
                                    weatherCode = current.weatherCode,
                                    isDay = current.isDay,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color(0xFF98CCFD)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "${kotlin.math.round(current.temp).toInt()}°",
                                    color = Color.White,
                                    fontSize = 96.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                                    Text(
                                        text = "Percepita ${kotlin.math.round(current.apparentTemp).toInt()}°",
                                        color = Color(0xFFC2C7CE),
                                        fontSize = 18.sp
                                    )
                                    val today = weather.daily.firstOrNull()
                                    if (today != null) {
                                        Text(
                                            text = "Max ${kotlin.math.round(today.maxTemp).toInt()}° • Min ${kotlin.math.round(today.minTemp).toInt()}°",
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Umidità: ${current.humidity}%",
                                    color = Color(0xFFC2C7CE),
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Vento: ${current.windSpeed.toInt()} km/h $windDirText",
                                    color = Color(0xFFC2C7CE),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    // Right Column: 24h Hourly Forecast Strip & Air Quality / Sun
                    Column(
                        modifier = Modifier
                            .weight(1.8f)
                            .height(340.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 24h Hourly Strip
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1.3f),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0x33000000))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Prossime ore",
                                    color = Color(0xFF98CCFD),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(weather.hourly.take(24)) { hourly ->
                                        HourlyItem(hourly = hourly)
                                    }
                                }
                            }
                        }

                        // Air Quality & Moon Banner Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Air Quality Card
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0x33000000))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Qualità dell'aria",
                                        color = Color(0xFF98CCFD),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (weather.airQuality != null) {
                                        Text(
                                            text = weather.airQuality.category,
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "AQI: ${weather.airQuality.aqi} • PM2.5: ${weather.airQuality.pm25}",
                                            color = Color(0xFFC2C7CE),
                                            fontSize = 14.sp
                                        )
                                    } else {
                                        Text(
                                            text = "Non disponibile",
                                            color = Color(0xFFC2C7CE),
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }

                            // Moon & Sun Card
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0x33000000))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Fase Lunare & Sole",
                                        color = Color(0xFF98CCFD),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${moon.phaseName} (${moon.illuminationPercent}%)",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val today = weather.daily.firstOrNull()
                                    Text(
                                        text = "Alba: ${today?.sunrise ?: "--"} • Tramonto: ${today?.sunset ?: "--"}",
                                        color = Color(0xFFC2C7CE),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5-Day Forecast Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Previsioni prossimi giorni",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(weather.daily) { day ->
                            ForecastCard(day = day)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HourlyItem(hourly: HourlyWeather) {
    val timeStr = remember(hourly.time) {
        try {
            val parts = hourly.time.split("T")
            if (parts.size > 1) parts[1].take(5) else hourly.time
        } catch (_: Exception) {
            hourly.time
        }
    }

    Column(
        modifier = Modifier
            .width(72.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x22FFFFFF))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = timeStr, color = Color(0xFFC2C7CE), fontSize = 14.sp)
        WeatherIcon(weatherCode = hourly.weatherCode, isDay = hourly.isDay, modifier = Modifier.size(28.dp), tint = Color.White)
        Text(
            text = "${kotlin.math.round(hourly.temp).toInt()}°",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${hourly.precipitationProbability}%",
            color = Color(0xFF98CCFD),
            fontSize = 12.sp
        )
    }
}

@Composable
fun ForecastCard(day: DailyWeather) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f)
    val formattedDate = formatForecastDate(day.date)

    androidx.tv.material3.Card(
        modifier = Modifier
            .width(180.dp)
            .height(160.dp)
            .scale(scale)
            .focusable()
            .onFocusChanged { isFocused = it.isFocused },
        onClick = {},
        shape = androidx.tv.material3.CardDefaults.shape(RoundedCornerShape(16.dp)),
        colors = androidx.tv.material3.CardDefaults.colors(
            containerColor = if (isFocused) Color(0xFF98CCFD) else Color(0x33000000)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formattedDate,
                color = if (isFocused) Color(0xFF101418) else Color(0xFF98CCFD),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getWeatherConditionText(day.weatherCode),
                    color = if (isFocused) Color(0xFF101418) else Color.White,
                    fontSize = 16.sp
                )
                WeatherIcon(weatherCode = day.weatherCode, isDay = 1, modifier = Modifier.size(32.dp), tint = if (isFocused) Color(0xFF101418) else Color.White)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${kotlin.math.round(day.maxTemp).toInt()}°",
                    color = if (isFocused) Color(0xFF101418) else Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${kotlin.math.round(day.minTemp).toInt()}°",
                    color = if (isFocused) Color(0xFF2A3441) else Color(0xFFC2C7CE),
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun TvButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f)
    val backgroundColor = if (isFocused) Color(0xFF98CCFD) else Color(0x33FFFFFF)
    val textColor = if (isFocused) Color(0xFF101418) else Color.White

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .then(Modifier.clickable(onClick = onClick))
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

fun getWeatherConditionText(code: Int): String {
    return when (code) {
        0 -> "Sereno"
        1 -> "Quasi sereno"
        2 -> "Parzialmente nuvoloso"
        3 -> "Coperto"
        45, 48 -> "Nebbia"
        51, 53, 55 -> "Pioviggine"
        56, 57 -> "Pioviggine gelata"
        61, 63, 65 -> "Pioggia"
        66, 67 -> "Pioggia gelata"
        71, 73, 75 -> "Neve"
        77 -> "Granuli di neve"
        80, 81, 82 -> "Rovesci di pioggia"
        85, 86 -> "Rovesci di neve"
        95, 96, 99 -> "Temporale"
        else -> "Variabile"
    }
}

fun getWindDirectionText(degrees: Double): String {
    val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
    val index = ((degrees % 360) / 22.5 + 0.5).toInt() % 16
    return directions[index]
}

fun formatForecastDate(dateStr: String): String {
    return try {
        val date = java.time.LocalDate.parse(dateStr)
        val today = java.time.LocalDate.now()
        when (date) {
            today -> "Oggi"
            today.plusDays(1) -> "Domani"
            else -> date.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.ITALIAN)
                .replaceFirstChar { it.uppercase() }
        }
    } catch (_: Exception) {
        dateStr
    }
}
