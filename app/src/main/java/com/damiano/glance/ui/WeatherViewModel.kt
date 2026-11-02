package com.damiano.glance.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.damiano.glance.data.repository.WeatherRepository
import com.damiano.glance.domain.CityResult
import com.damiano.glance.domain.LocationInfo
import com.damiano.glance.domain.WeatherData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Success(
        val weather: WeatherData,
        val location: LocationInfo,
        val searchResults: List<CityResult> = emptyList(),
        val isSearching: Boolean = false,
        val showCitySearch: Boolean = false
    ) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WeatherRepository(application)

    private val _state = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    private var searchJob: Job? = null
    private var periodicRefreshJob: Job? = null

    init {
        refresh()
        startPeriodicRefresh()
    }

    private fun startPeriodicRefresh() {
        periodicRefreshJob?.cancel()
        periodicRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(30 * 60 * 1000L) // 30 minutes
                silentRefresh()
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState !is WeatherUiState.Success) {
                _state.value = WeatherUiState.Loading
            }
            try {
                val location = repository.getOrInitLocation()
                val weather = repository.fetchWeather(location.lat, location.lon)
                _state.value = WeatherUiState.Success(weather = weather, location = location)
            } catch (e: Exception) {
                if (currentState is WeatherUiState.Success) {
                    // Keep previous success data or show toast/message if needed
                } else {
                    _state.value = WeatherUiState.Error(getUserFriendlyMessage(e))
                }
            }
        }
    }

    private fun getUserFriendlyMessage(e: Exception): String {
        val msg = e.localizedMessage ?: ""
        return when {
            msg.contains("Unable to resolve host", ignoreCase = true) ||
            msg.contains("No address associated", ignoreCase = true) ||
            msg.contains("timeout", ignoreCase = true) ||
            msg.contains("network", ignoreCase = true) -> "Nessuna connessione internet. Controlla la rete."
            else -> "Si è verificato un errore durante il caricamento dei dati."
        }
    }

    private fun silentRefresh() {
        val currentState = _state.value
        if (currentState is WeatherUiState.Success) {
            viewModelScope.launch {
                try {
                    val weather = repository.fetchWeather(currentState.location.lat, currentState.location.lon)
                    _state.value = currentState.copy(weather = weather)
                } catch (_: Exception) {}
            }
        }
    }

    fun setShowCitySearch(show: Boolean) {
        val currentState = _state.value
        if (currentState is WeatherUiState.Success) {
            _state.value = currentState.copy(showCitySearch = show, searchResults = emptyList(), isSearching = false)
        }
    }

    fun searchCities(query: String) {
        val currentState = _state.value
        if (currentState !is WeatherUiState.Success) return

        if (query.trim().length < 2) {
            _state.value = currentState.copy(searchResults = emptyList(), isSearching = false)
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.value = currentState.copy(isSearching = true)
            delay(300) // debounce
            try {
                val results = repository.searchCities(query)
                val latestState = _state.value
                if (latestState is WeatherUiState.Success) {
                    _state.value = latestState.copy(searchResults = results, isSearching = false)
                }
            } catch (_: Exception) {
                val latestState = _state.value
                if (latestState is WeatherUiState.Success) {
                    _state.value = latestState.copy(searchResults = emptyList(), isSearching = false)
                }
            }
        }
    }

    fun selectCity(city: CityResult) {
        viewModelScope.launch {
            _state.value = WeatherUiState.Loading
            try {
                val newLocation = LocationInfo(
                    lat = city.lat,
                    lon = city.lon,
                    name = city.name,
                    region = city.region,
                    country = city.country,
                    origin = "manual"
                )
                repository.updateLocation(newLocation)
                val weather = repository.fetchWeather(newLocation.lat, newLocation.lon)
                _state.value = WeatherUiState.Success(weather = weather, location = newLocation)
            } catch (e: Exception) {
                _state.value = WeatherUiState.Error(e.localizedMessage ?: "Errore nel caricamento della città")
            }
        }
    }
}
