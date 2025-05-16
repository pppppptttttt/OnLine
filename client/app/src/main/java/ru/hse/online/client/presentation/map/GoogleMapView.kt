package ru.hse.online.client.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import ru.hse.online.client.viewModels.LocationViewModel

@Composable
fun GoogleMapView(viewModel: LocationViewModel) {
    val routePoints by viewModel.routePoints.collectAsState()
    val currentLocation by viewModel.location.collectAsState()
    val cameraPositionState = rememberCameraPositionState()

    val markers = remember { mutableStateListOf<LatLng>() }

    val currentMarkerState = rememberMarkerState()

    LaunchedEffect(currentLocation) {
        currentMarkerState.position = currentLocation
    }

    LaunchedEffect(currentLocation) {
        currentLocation.let { location ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    15f
                )
            )
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        googleMapOptionsFactory = {
            GoogleMapOptions().mapColorScheme(MapColorScheme.FOLLOW_SYSTEM)
        },
        uiSettings = MapUiSettings(zoomControlsEnabled = false),
        onMapClick = { coord ->
            markers.add(coord)
        },
        onMapLongClick = { coord ->
            markers.remove(coord)
        }
    ) {
        Polyline(
            points = routePoints,
            color = Color(0x800000FF),
            width = 15f
        )

        Marker(
            state = currentMarkerState,
            icon = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_AZURE
            )
        )

        markers.forEach { coord ->
            Marker(
                state = rememberMarkerState(position = coord),
                icon = BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_AZURE
                )
            )
        }
    }
}
