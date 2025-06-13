package ru.hse.online.client.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val previewPath by viewModel.previewPath.collectAsState()
    val currentLocation by viewModel.location.collectAsState()
    val centerCameraEvents = viewModel.centerCameraEvents
    val cameraPositionState = rememberCameraPositionState()
    var isFirstAppearance by remember { mutableStateOf(true) }

    val markers = remember { mutableStateListOf<LatLng>() }

    val currentMarkerState = rememberMarkerState()

    LaunchedEffect(currentLocation) {
        if (isFirstAppearance) {
            currentLocation.let { location ->
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        location,
                        15f
                    )
                )
            }
            isFirstAppearance = false
        }
        currentMarkerState.position = currentLocation
    }

    LaunchedEffect(centerCameraEvents) {
        centerCameraEvents.collect {
            currentLocation.let { location ->
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        location,
                        15f
                    )
                )
            }
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        googleMapOptionsFactory = {
            GoogleMapOptions().mapColorScheme(MapColorScheme.DARK)
        },
        uiSettings = MapUiSettings(zoomControlsEnabled = false),
        onMapClick = { coord ->
            markers.add(coord)
        },
        onMapLongClick = { coord ->
            markers.remove(coord)
        },
    ) {
        Polyline(
            points = routePoints,
            color = Color(0x800000FF),
            width = 15f
        )

        Polyline(
            points = previewPath,
            color = Color(0xffff6347),
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