package ru.hse.online.client.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.hse.online.client.viewModels.LocationViewModel
import ru.hse.online.client.viewModels.PedometerViewModel

@Composable
fun MapScreen(pedometerViewModel: PedometerViewModel, locationViewModel: LocationViewModel) {
    val mapOverlay: MapOverlayView = MapOverlayView()
    Surface(modifier = Modifier.fillMaxSize()) {
        GoogleMapView(locationViewModel)
        mapOverlay.Draw(pedometerViewModel)
    }
}
