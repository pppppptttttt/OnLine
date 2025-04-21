package ru.hse.online.client.repository.storage

import android.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationRepository {
    private val _locationState = MutableStateFlow<LocationState>(LocationState.Idle)
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()

    fun updateLocation(location: Location) {
        _locationState.value = LocationState.Available(location)
    }

    fun updateLocation(error: String) {
        _locationState.value = LocationState.Error(error)
    }

    fun setActive() {
        _locationState.value = LocationState.Active
    }

    sealed class LocationState {
        data object Idle : LocationState()
        data object Active : LocationState()
        data class Available(val location: Location) : LocationState()
        data class Error(val message: String) : LocationState()
    }
}