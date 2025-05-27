package ru.hse.online.client.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.hse.online.client.viewModels.LocationViewModel
import ru.hse.online.client.viewModels.PedometerViewModel
import ru.hse.online.client.viewModels.UserViewModel

@Composable
fun MapScreen(pedometerViewModel: PedometerViewModel, locationViewModel: LocationViewModel, userViewModel: UserViewModel) {
    Surface(modifier = Modifier.fillMaxSize()) {
        GoogleMapView(locationViewModel)
        MapOverlayView(pedometerViewModel, locationViewModel, userViewModel)
    }
}
