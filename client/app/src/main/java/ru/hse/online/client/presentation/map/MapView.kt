package ru.hse.online.client.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.KoinContext

@Composable
fun MapScreen() {
    val mapView: BaseMapView = GoogleMapView()
    val mapOverlay: MapOverlayView = MapOverlayView()
    KoinContext {
        Surface(modifier = Modifier.fillMaxSize()) {
            mapView.DrawMap()
            mapOverlay.Draw()
        }
    }
}
