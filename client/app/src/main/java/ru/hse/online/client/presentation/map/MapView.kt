package ru.hse.online.client.presentation.map

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import org.koin.compose.KoinContext
import ru.hse.online.client.presentation.common.BottomScreenName
import ru.hse.online.client.ui.theme.ClientTheme

class MapView : ComponentActivity() {
    //    private val mapView: BaseMapView = StaticMapView()
    private val mapView: BaseMapView = GoogleMapView()
    private val mapOverlay: MapOverlayView = MapOverlayView(this)
    private val bottomScreenName: BottomScreenName = BottomScreenName("Map")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ClientTheme(darkTheme = true) {
                KoinContext {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        bottomScreenName.DisplayNameAndDraw {
                            mapView.DrawMap()
                        }
                        mapOverlay.Draw()
                    }
                }
            }
        }
    }
}
