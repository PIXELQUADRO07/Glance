package com.damiano.glance.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.damiano.glance.domain.LocationInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "glance_settings")

class DataStoreManager(private val context: Context) {
    companion object {
        private val LAT_KEY = doublePreferencesKey("lat")
        private val LON_KEY = doublePreferencesKey("lon")
        private val CITY_KEY = stringPreferencesKey("city_name")
        private val REGION_KEY = stringPreferencesKey("region")
        private val COUNTRY_KEY = stringPreferencesKey("country")
        private val ORIGIN_KEY = stringPreferencesKey("origin")
        private val LAST_UPDATE_KEY = longPreferencesKey("last_update")
        private val CACHED_WEATHER_KEY = stringPreferencesKey("cached_weather_json")
        private val CACHED_LAT_KEY = doublePreferencesKey("cached_lat")
        private val CACHED_LON_KEY = doublePreferencesKey("cached_lon")
        private val REFRESH_INTERVAL_KEY = longPreferencesKey("refresh_interval")
        private val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch")
    }

    val locationFlow: Flow<LocationInfo> = context.dataStore.data.map { prefs ->
        LocationInfo(
            lat = prefs[LAT_KEY] ?: 41.9028,
            lon = prefs[LON_KEY] ?: 12.4964,
            name = prefs[CITY_KEY] ?: "Roma",
            region = prefs[REGION_KEY] ?: "Lazio",
            country = prefs[COUNTRY_KEY] ?: "Italia",
            origin = prefs[ORIGIN_KEY] ?: ""
        )
    }

    val refreshIntervalFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[REFRESH_INTERVAL_KEY] ?: 30L
    }

    val firstLaunchFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[FIRST_LAUNCH_KEY] ?: true
    }

    val cachedWeatherFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[CACHED_WEATHER_KEY]
    }

    val cachedLatFlow: Flow<Double?> = context.dataStore.data.map { prefs ->
        prefs[CACHED_LAT_KEY]
    }

    val cachedLonFlow: Flow<Double?> = context.dataStore.data.map { prefs ->
        prefs[CACHED_LON_KEY]
    }

    suspend fun saveLocation(info: LocationInfo) {
        context.dataStore.edit { prefs ->
            prefs[LAT_KEY] = info.lat
            prefs[LON_KEY] = info.lon
            prefs[CITY_KEY] = info.name
            prefs[REGION_KEY] = info.region
            prefs[COUNTRY_KEY] = info.country
            prefs[ORIGIN_KEY] = info.origin
        }
    }

    suspend fun saveWeatherCache(lat: Double, lon: Double, json: String) {
        context.dataStore.edit { prefs ->
            prefs[CACHED_LAT_KEY] = lat
            prefs[CACHED_LON_KEY] = lon
            prefs[CACHED_WEATHER_KEY] = json
            prefs[LAST_UPDATE_KEY] = System.currentTimeMillis()
        }
    }

    suspend fun saveRefreshInterval(minutes: Long) {
        context.dataStore.edit { prefs ->
            prefs[REFRESH_INTERVAL_KEY] = minutes
        }
    }

    suspend fun saveFirstLaunch(isFirstLaunch: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[FIRST_LAUNCH_KEY] = isFirstLaunch
        }
    }

    suspend fun clearAllData() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
