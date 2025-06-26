package ru.hse.online.client.repository.storage

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.hse.online.client.presentation.map.googleMapsFormatToLatLngList

class LocationRepository {
    private val MIN_DISTANCE_CHANGE_METERS = 2.0

    private val _locationState = MutableStateFlow<LocationState>(LocationState.Idle)
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()
    private var _location: Location? = null

    private var _previewPath: MutableStateFlow<List<LatLng>> =
        MutableStateFlow<List<LatLng>>(listOf())
    val previewPath: StateFlow<List<LatLng>> = _previewPath.asStateFlow()

    private val _currentSpeed = MutableStateFlow(0f)
    val currentSpeed: StateFlow<Float> = _currentSpeed.asStateFlow()

    fun updateLocation(newLocation: Location, speed: Float) {
        if (_location != null && _location?.distanceTo(newLocation)!! <= MIN_DISTANCE_CHANGE_METERS) {
            return;
        }
        _locationState.value = LocationState.Available(newLocation)
        _location = newLocation
        _currentSpeed.value = speed
    }

    fun updateLocation(error: String, speed: Float) {
        _locationState.value = LocationState.Error(error)
        _currentSpeed.value = speed
    }

    fun setActive() {
        _locationState.value = LocationState.Active
    }

    fun loadPreviewPath(pathLine: String) {
        _previewPath.value = pathLine.googleMapsFormatToLatLngList()
    }

    fun clearPreview() {
        _previewPath.value = emptyList()
    }

    sealed class LocationState {
        data object Idle : LocationState()
        data object Active : LocationState()
        data class Available(val location: Location) : LocationState()
        data class Error(val message: String) : LocationState()
    }
}
