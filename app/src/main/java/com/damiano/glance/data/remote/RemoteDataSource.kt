package com.damiano.glance.data.remote

import com.damiano.glance.domain.AirQualityData
import com.damiano.glance.domain.CityResult
import com.damiano.glance.domain.CurrentWeather
import com.damiano.glance.domain.DailyWeather
import com.damiano.glance.domain.LocationInfo
import com.damiano.glance.domain.WeatherData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class IpWhoResponse(
    val success: Boolean,
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val region: String,
    val country: String
)

@Serializable
data class GeocodingResponse(
    val results: List<GeocodingItem>? = null
)

@Serializable
data class GeocodingItem(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val admin1: String? = null
)

@Serializable
data class OpenMeteoForecastResponse(
    val current: OpenMeteoCurrent,
    val hourly: OpenMeteoHourly,
    val daily: OpenMeteoDaily
)

@Serializable
data class OpenMeteoCurrent(
    val temperature_2m: Double,
    val apparent_temperature: Double,
    val relative_humidity_2m: Int,
    val precipitation: Double,
    val weather_code: Int,
    val pressure_msl: Double,
    val wind_speed_10m: Double,
    val wind_direction_10m: Double,
    val wind_gusts_10m: Double,
    val is_day: Int,
    val visibility: Double? = null
)

@Serializable
data class OpenMeteoHourly(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val precipitation_probability: List<Int>,
    val weather_code: List<Int>,
    val is_day: List<Int>
)

@Serializable
data class OpenMeteoDaily(
    val time: List<String>,
    val weather_code: List<Int>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    val precipitation_sum: List<Double>,
    val precipitation_probability_max: List<Int>,
    val uv_index_max: List<Double>
)

@Serializable
data class AirQualityResponse(
    val current: AirQualityCurrent? = null
)

@Serializable
data class AirQualityCurrent(
    val european_aqi: Int? = null,
    val pm2_5: Double? = null,
    val pm10: Double? = null
)

class RemoteDataSource {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getIpLocation(): LocationInfo? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://ipwho.is/")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                requestMethod = "GET"
            }
            if (connection.responseCode == 200) {
                val text = connection.inputStream.bufferedReader().use { it.readText() }
                val resp = json.decodeFromString<IpWhoResponse>(text)
                if (resp.success) {
                    return@withContext LocationInfo(
                        lat = resp.latitude,
                        lon = resp.longitude,
                        name = resp.city,
                        region = resp.region,
                        country = resp.country,
                        origin = "auto"
                    )
                }
            }
        } catch (_: Exception) {}
        null
    }

    suspend fun searchCities(query: String): List<CityResult> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = URL("https://geocoding-api.open-meteo.com/v1/search?name=$encodedQuery&count=8&language=it")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                requestMethod = "GET"
            }
            if (connection.responseCode == 200) {
                val text = connection.inputStream.bufferedReader().use { it.readText() }
                val resp = json.decodeFromString<GeocodingResponse>(text)
                return@withContext resp.results?.map {
                    CityResult(
                        name = it.name,
                        region = it.admin1 ?: "",
                        country = it.country ?: "",
                        lat = it.latitude,
                        lon = it.longitude
                    )
                } ?: emptyList()
            }
        } catch (_: Exception) {}
        emptyList()
    }

    suspend fun fetchWeather(lat: Double, lon: Double): WeatherData = withContext(Dispatchers.IO) {
        val urlStr = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,apparent_temperature,relative_humidity_2m,precipitation,weather_code,pressure_msl,wind_speed_10m,wind_direction_10m,wind_gusts_10m,is_day,visibility&hourly=temperature_2m,precipitation_probability,weather_code,is_day&daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,precipitation_sum,precipitation_probability_max,uv_index_max&timezone=auto&wind_speed_unit=kmh&forecast_days=5"
        val connection = (URL(urlStr).openConnection() as HttpURLConnection).apply {
            connectTimeout = 10000
            readTimeout = 10000
            requestMethod = "GET"
        }
        val text = connection.inputStream.bufferedReader().use { it.readText() }
        val forecast = json.decodeFromString<OpenMeteoForecastResponse>(text)

        val current = forecast.current
        val hourly = forecast.hourly
        val daily = forecast.daily

        val currentWeather = CurrentWeather(
            temp = current.temperature_2m,
            apparentTemp = current.apparent_temperature,
            humidity = current.relative_humidity_2m,
            precipitation = current.precipitation,
            weatherCode = current.weather_code,
            pressure = current.pressure_msl,
            windSpeed = current.wind_speed_10m,
            windDirection = current.wind_direction_10m,
            windGusts = current.wind_gusts_10m,
            isDay = current.is_day,
            visibility = current.visibility ?: 10.0
        )

        val hourlyList = mutableListOf<com.damiano.glance.domain.HourlyWeather>()
        for (i in hourly.time.indices) {
            hourlyList.add(
                com.damiano.glance.domain.HourlyWeather(
                    time = hourly.time[i],
                    temp = hourly.temperature_2m[i],
                    precipitationProbability = hourly.precipitation_probability[i],
                    weatherCode = hourly.weather_code[i],
                    isDay = hourly.is_day[i]
                )
            )
        }

        val dailyList = mutableListOf<DailyWeather>()
        for (i in daily.time.indices) {
            dailyList.add(
                DailyWeather(
                    date = daily.time[i],
                    weatherCode = daily.weather_code[i],
                    maxTemp = daily.temperature_2m_max[i],
                    minTemp = daily.temperature_2m_min[i],
                    sunrise = daily.sunrise[i].takeLast(5),
                    sunset = daily.sunset[i].takeLast(5),
                    precipitationSum = daily.precipitation_sum[i],
                    precipitationProbability = daily.precipitation_probability_max[i],
                    uvIndexMax = daily.uv_index_max[i]
                )
            )
        }

        // Fetch air quality separately (non-blocking if fails)
        val airQuality = fetchAirQuality(lat, lon)

        WeatherData(
            current = currentWeather,
            hourly = hourlyList,
            daily = dailyList,
            airQuality = airQuality,
            lastUpdated = System.currentTimeMillis(),
            isCached = false
        )
    }

    private suspend fun fetchAirQuality(lat: Double, lon: Double): AirQualityData? = withContext(Dispatchers.IO) {
        try {
            val urlStr = "https://air-quality-api.open-meteo.com/v1/air-quality?latitude=$lat&longitude=$lon&current=european_aqi,pm2_5,pm10"
            val connection = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                connectTimeout = 5000
                readTimeout = 5000
                requestMethod = "GET"
            }
            if (connection.responseCode == 200) {
                val text = connection.inputStream.bufferedReader().use { it.readText() }
                val resp = json.decodeFromString<AirQualityResponse>(text)
                val aq = resp.current
                val aqiVal = aq?.european_aqi ?: return@withContext null
                val (category, colorHex) = when {
                    aqiVal <= 20 -> "Buona" to "#4CAF50"
                    aqiVal <= 40 -> "Discreta" to "#8BC34A"
                    aqiVal <= 60 -> "Moderata" to "#FFEB3B"
                    aqiVal <= 80 -> "Scadente" to "#FF9800"
                    aqiVal <= 100 -> "Molto scadente" to "#F44336"
                    else -> "Pessima" to "#9C27B0"
                }
                return@withContext AirQualityData(
                    aqi = aqiVal,
                    pm25 = aq?.pm2_5 ?: 0.0,
                    pm10 = aq?.pm10 ?: 0.0,
                    category = category,
                    colorHex = colorHex
                )
            }
        } catch (_: Exception) {}
        null
    }
}
