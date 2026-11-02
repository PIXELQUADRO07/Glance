package com.damiano.glance.data.repository

import android.content.Context
import com.damiano.glance.data.local.DataStoreManager
import com.damiano.glance.data.remote.RemoteDataSource
import com.damiano.glance.domain.CityResult
import com.damiano.glance.domain.LocationInfo
import com.damiano.glance.domain.WeatherData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WeatherRepository(context: Context) {
    private val dataStore = DataStoreManager(context)
    private val remote = RemoteDataSource()
    private val json = Json { ignoreUnknownKeys = true }

    val locationFlow: Flow<LocationInfo> = dataStore.locationFlow
    val refreshIntervalFlow: Flow<Long> = dataStore.refreshIntervalFlow
    val firstLaunchFlow: Flow<Boolean> = dataStore.firstLaunchFlow

    suspend fun saveRefreshInterval(minutes: Long) {
        dataStore.saveRefreshInterval(minutes)
    }

    suspend fun saveFirstLaunch(isFirstLaunch: Boolean) {
        dataStore.saveFirstLaunch(isFirstLaunch)
    }

    suspend fun clearAllData() {
        dataStore.clearAllData()
    }

    suspend fun getOrInitLocation(): LocationInfo {
        val currentLoc = dataStore.locationFlow.first()
        if (currentLoc.origin.isNotEmpty() && currentLoc.name != "Roma") {
            return currentLoc
        }
        // First launch or default fallback: try IP geolocation
        val ipLoc = remote.getIpLocation()
        if (ipLoc != null) {
            dataStore.saveLocation(ipLoc)
            return ipLoc
        }
        if (currentLoc.origin.isNotEmpty()) {
            return currentLoc
        }
        // Fallback default (Rome)
        val defaultLoc = LocationInfo(
            lat = 41.9028,
            lon = 12.4964,
            name = "Roma",
            region = "Lazio",
            country = "Italia",
            origin = "auto"
        )
        dataStore.saveLocation(defaultLoc)
        return defaultLoc
    }

    suspend fun updateLocation(location: LocationInfo) {
        dataStore.saveLocation(location)
    }

    suspend fun searchCities(query: String): List<CityResult> {
        return remote.searchCities(query)
    }

    suspend fun fetchWeather(lat: Double, lon: Double): WeatherData {
        return try {
            val weather = remote.fetchWeather(lat, lon)
            // Cache successful JSON with location
            try {
                val encoded = json.encodeToString(weather)
                dataStore.saveWeatherCache(lat, lon, encoded)
            } catch (_: Exception) {}
            weather
        } catch (e: Exception) {
            // Try loading from cache if location matches
            val cachedJson = dataStore.cachedWeatherFlow.first()
            val cachedLat = dataStore.cachedLatFlow.first()
            val cachedLon = dataStore.cachedLonFlow.first()
            if (!cachedJson.isNullOrEmpty() && cachedLat != null && cachedLon != null &&
                kotlin.math.abs(cachedLat - lat) < 0.05 && kotlin.math.abs(cachedLon - lon) < 0.05) {
                try {
                    val cachedWeather = json.decodeFromString<WeatherData>(cachedJson)
                    return cachedWeather.copy(isCached = true)
                } catch (_: Exception) {}
            }
            throw e
        }
    }
}
