package ru.hse.online.client.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.hse.online.client.repository.networking.api_data.Friend
import ru.hse.online.client.repository.storage.LocationRepository
import ru.hse.online.client.repository.storage.UserRepository
import ru.hse.online.client.services.ContextProvider
import ru.hse.online.client.services.LocationService
import java.util.UUID

class LocationViewModel(
    private val contextProvider: ContextProvider,
    private val locationRepository: LocationRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val TAG: String = "APP_LOCATION_VIEW_MODEL"

    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints.asStateFlow()

    private var _locationState: MutableStateFlow<LatLng> = MutableStateFlow(LatLng(0.0, 0.0))
    val location: StateFlow<LatLng> = _locationState.asStateFlow()

    private val _centerCameraEvents = Channel<Unit>(Channel.BUFFERED)
    val centerCameraEvents = _centerCameraEvents.receiveAsFlow()

    private val _isOnline = MutableStateFlow(false)
    private val _isPaused = MutableStateFlow(false)
    val previewPath: StateFlow<List<LatLng>> = locationRepository.previewPath
    private val _groupPaths = MutableStateFlow<Map<Friend, List<LatLng>>>(mutableMapOf())
    val groupPaths: StateFlow<Map<Friend, List<LatLng>>> = _groupPaths.asStateFlow()

    private class KalmanFilter(
        private val processNoise: Double = 1e-5,
        private val measurementNoise: Double = 0.1
    ) {
        private var latEstimate: Double = 0.0
        private var lngEstimate: Double = 0.0
        private var variance: Double = Double.MAX_VALUE

        fun update(newLat: Double, newLng: Double): Pair<Double, Double> {
            if (variance == Double.MAX_VALUE) {
                latEstimate = newLat
                lngEstimate = newLng
                variance = measurementNoise
            } else {
                variance += processNoise

                val kalmanGain = variance / (variance + measurementNoise)
                latEstimate += kalmanGain * (newLat - latEstimate)
                lngEstimate += kalmanGain * (newLng - lngEstimate)
                variance *= (1.0 - kalmanGain)
            }
            return latEstimate to lngEstimate
        }

        fun reset() {
            latEstimate = 0.0
            lngEstimate = 0.0
            variance = Double.MAX_VALUE
        }
    }

    private val kalmanFilter = KalmanFilter()

    init {
        locationRepository.locationState
            .onEach { state ->
                Log.i(TAG, "Updating location")
                when (state) {
                    is LocationRepository.LocationState.Available -> {
                        val newPoint = state.location.let {
                            val (filteredLat, filteredLng) = kalmanFilter.update(
                                it.latitude,
                                it.longitude
                            )
                            LatLng(filteredLat, filteredLng)
                        }

                        _locationState.value = newPoint
                        if (_isOnline.value && !_isPaused.value) {
                            _routePoints.update { currentPoints ->
                                currentPoints + newPoint
                            }
                        }
                        Log.i(TAG, "Filtered location: $newPoint")
                    }
                    is LocationRepository.LocationState.Error -> {
                        Log.i(TAG, state.message)
                    }
                    else -> {}
                }
            }
            .launchIn(viewModelScope)
        startService()
    }

    private fun startService() {
        Log.i(TAG, "Starting location service")
        LocationService.startService(contextProvider.getContext())
    }

    override fun onCleared() {
        LocationService.stopService(contextProvider.getContext())
        super.onCleared()
    }

    fun goOnLine() {
        _isOnline.value = true
        _isPaused.value = false
        kalmanFilter.reset()
    }

    fun pauseOnline() {
        if (_isOnline.value) {
            _isPaused.value = true
        }
    }

    fun resumeOnline() {
        if (_isOnline.value) {
            _isPaused.value = false
        }
    }

    fun goOffLine(savePath: Boolean, description: String = "") {
        _isOnline.value = false
        _isPaused.value = false
        if (savePath) {
            viewModelScope.launch {
                userRepository.savePath(description, _routePoints.value)
            }
        }
        _routePoints.value = emptyList()
        kalmanFilter.reset()
    }

    fun clearPreview() {
        locationRepository.clearPreview()
    }

    fun centerCamera() {
        viewModelScope.launch {
            _centerCameraEvents.send(Unit)
        }
    }

    fun updateFriendLocation(friend: Friend, lat: Double, lng: Double) {
        val newLocation = LatLng(lat, lng)
        val currentPaths = _groupPaths.value.toMutableMap()

        val currentFriendPath = currentPaths.getOrDefault(friend, emptyList()) + newLocation
        currentPaths[friend] = currentFriendPath
        _groupPaths.value = currentPaths
    }
}