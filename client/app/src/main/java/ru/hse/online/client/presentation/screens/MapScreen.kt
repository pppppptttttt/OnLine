package ru.hse.online.client.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.hse.online.client.presentation.map.GoogleMapView
import ru.hse.online.client.presentation.map.MapOverlayView
import ru.hse.online.client.viewModels.GroupViewModel
import ru.hse.online.client.viewModels.LocationViewModel
import ru.hse.online.client.viewModels.StatsViewModel
import ru.hse.online.client.viewModels.UserViewModel

@Composable
fun MapScreen(statsViewModel: StatsViewModel, locationViewModel: LocationViewModel, userViewModel: UserViewModel, groupViewModel: GroupViewModel) {
    Surface(modifier = Modifier.fillMaxSize()) {
        GoogleMapView(locationViewModel)
        MapOverlayView(statsViewModel, locationViewModel, userViewModel, groupViewModel)
    }
}
