package ru.hse.online.client.services.location
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.core.content.PermissionChecker
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LocationProvider(
    private val context: Context
) {
    private val TAG: String = "APP_LOCATION_PROVIDER"
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context.applicationContext)
    }

    private val _locationState = MutableStateFlow<LocationState>(LocationState.Idle)
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()

    private var locationUpdatesJob: Job? = null
    private var lastKnownLocation: Location? = null

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
        .setMinUpdateIntervalMillis(5000)
        .build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation?.let { location ->
                lastKnownLocation = location
                _locationState.value = LocationState.Available(location)
            }
        }

        override fun onLocationAvailability(availability: LocationAvailability) {
            super.onLocationAvailability(availability)
            if (!availability.isLocationAvailable) {
                _locationState.value = LocationState.Error("Location services unavailable")
            }
        }
    }

    fun startLocationUpdates(scope: CoroutineScope) {
        if (!hasLocationPermissions()) {
            _locationState.value = LocationState.Error("Location permissions not granted")
            return
        }

        locationUpdatesJob?.cancel()
        locationUpdatesJob = scope.launch(Dispatchers.IO) {
            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
                _locationState.value = LocationState.Active
            } catch (e: SecurityException) {
                _locationState.value = LocationState.Error("Security exception: ${e.message}")
            } catch (e: Exception) {
                _locationState.value = LocationState.Error("Location updates failed: ${e.message}")
            }
        }
    }

    fun stopLocationUpdates() {
        locationUpdatesJob?.cancel()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        _locationState.value = LocationState.Idle
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        return if (hasLocationPermissions()) {
            try {
                fusedLocationClient.lastLocation.await()
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return PermissionChecker.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED || PermissionChecker.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    sealed class LocationState {
        data object Idle : LocationState()
        data object Active : LocationState()
        data class Available(val location: Location) : LocationState()
        data class Error(val message: String) : LocationState()
    }
}
