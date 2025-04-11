package ru.hse.online.client.presentation.map

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import org.koin.androidx.compose.koinViewModel
import ru.hse.online.client.presentation.LocationViewModel

class GoogleMapView : BaseMapView {

    @Composable
    override fun DrawMap() {
        val viewModel: LocationViewModel = koinViewModel()
        val routePoints by viewModel.routePoints.collectAsState()
        val currentLocation by viewModel.location.collectAsState()
        val cameraPositionState = rememberCameraPositionState()

        val markers = remember { mutableStateListOf<LatLng>() }
        var selectedMarker = remember { mutableStateOf<LatLng?>(null) }

        /*
        LaunchedEffect(routePoints) {
            if (routePoints.isNotEmpty()) {
                val bounds = LatLngBounds.builder().apply {
                    routePoints.forEach { include(it) }
                }.build()

                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds, 100)
                )
            }
        }*/

        LaunchedEffect(currentLocation) {
            currentLocation?.let { location ->
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(location.latitude, location.longitude),
                        15f
                    )
                )
            }
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            googleMapOptionsFactory = { GoogleMapOptions().mapColorScheme(MapColorScheme.FOLLOW_SYSTEM) },
            onMapClick = { coord ->
                markers.add(coord)
                Log.i("dbg", routePoints.toString())
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
}