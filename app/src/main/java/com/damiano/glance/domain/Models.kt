package com.damiano.glance.domain

import kotlinx.serialization.Serializable

@Serializable
data class LocationInfo(
    val lat: Double,
    val lon: Double,
    val name: String,
    val region: String,
    val country: String,
    val origin: String // "auto" or "manual"
)

@Serializable
data class WeatherData(
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,
    val airQuality: AirQualityData?,
    val lastUpdated: Long,
    val isCached: Boolean
)

@Serializable
data class HourlyWeather(
    val time: String,
    val temp: Double,
    val precipitationProbability: Int,
    val weatherCode: Int,
    val isDay: Int
)

@Serializable
data class CurrentWeather(
    val temp: Double,
    val apparentTemp: Double,
    val humidity: Int,
    val precipitation: Double,
    val weatherCode: Int,
    val pressure: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val windGusts: Double,
    val isDay: Int,
    val visibility: Double
)

@Serializable
data class DailyWeather(
    val date: String,
    val weatherCode: Int,
    val maxTemp: Double,
    val minTemp: Double,
    val sunrise: String,
    val sunset: String,
    val precipitationSum: Double,
    val precipitationProbability: Int,
    val uvIndexMax: Double
)

@Serializable
data class AirQualityData(
    val aqi: Int,
    val pm25: Double,
    val pm10: Double,
    val category: String,
    val colorHex: String
)

@Serializable
data class CityResult(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double
)
