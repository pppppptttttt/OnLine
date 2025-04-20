package ru.hse.online.client.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.java.KoinJavaComponent.inject
import ru.hse.online.client.services.location.LocationService

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = "APP_LOCATION_VIEW_MODEL"

    private val _location = MutableStateFlow<LatLng>(LatLng(0.0, 0.0))
    val location: StateFlow<LatLng> = _location.asStateFlow()
    private var _routePoints: MutableStateFlow<List<LatLng>> = MutableStateFlow<List<LatLng>>(listOf());
    val routePoints: StateFlow<List<LatLng>> = _routePoints.asStateFlow()

    private val locationService: LocationService by inject(LocationService::class.java)

    init {
        LocationService.startService(getApplication())

        locationService.locationState
            .onEach { state ->
                when (state) {
                    is LocationService.LocationState.Available -> {
                        val newPoint = state.location.let {
                            LatLng(it.latitude, it.longitude)
                        }
                        _location.value = newPoint
                        _routePoints.value += newPoint
                        Log.i(TAG, "New location: ${_location.value}")
                    }
                    is LocationService.LocationState.Error -> {
                        Log.e(TAG, state.message)
                    }
                    else -> {}
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        LocationService.stopService(getApplication())
        super.onCleared()
    }
}
