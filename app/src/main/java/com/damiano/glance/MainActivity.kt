package com.damiano.glance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.damiano.glance.ui.WeatherScreen
import com.damiano.glance.ui.WeatherViewModel
import com.damiano.glance.ui.theme.GlanceTheme

class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GlanceTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                WeatherScreen(
                    state = state,
                    onRefresh = viewModel::refresh,
                    onShowCitySearch = viewModel::setShowCitySearch,
                    onSearchQueryChanged = viewModel::searchCities,
                    onSelectCity = { city ->
                        viewModel.selectCity(city)
                        viewModel.setShowCitySearch(false)
                    }
                )
            }
        }
    }
}
