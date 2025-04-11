package ru.hse.online.client.presentation

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.hse.online.client.services.location.LocationProvider

class LocationViewModel(private val locationProvider: LocationProvider) : ViewModel() {
    private val TAG: String = "APP_LOCATION_VIEW_MODEL"

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location.asStateFlow()

    init {
        locationProvider.locationState
            .onEach { state ->
                when (state) {
                    is LocationProvider.LocationState.Available -> {
                        _location.value = state.location
                        Log.e(TAG, "New location " + _location.value);
                    }
                    is LocationProvider.LocationState.Error ->
                        Log.e(TAG, state.message)
                    else -> {}
                }
            }
            .launchIn(viewModelScope)
    }

    fun startUpdates() = locationProvider.startLocationUpdates(viewModelScope)

    fun stopUpdates() = locationProvider.stopLocationUpdates()

    override fun onCleared() {
        locationProvider.stopLocationUpdates()
        super.onCleared()
    }
}
