package ru.hse.online.client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import ru.hse.online.client.map.ActualMapView
import ru.hse.online.client.map.BaseMapView
import ru.hse.online.client.map.StaticMapView
import ru.hse.online.client.ui.theme.ClientTheme

class MapActivity : ComponentActivity() {
//    private val mapView: BaseMapView = StaticMapView()
    private val mapView: BaseMapView = ActualMapView()

    override fun onCreate(savedInstanceState: Bundle?) {
        val currentActivity = this

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ClientTheme(darkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    mapView.DrawMap()

                    Column(
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier.padding(top = 30.dp, start = 10.dp, end = 10.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            NavButton("St") {
                                Log.i(ui_tag, "stats click!")
                            }

                            NavButton("Se") {
                                Log.i(ui_tag, "settings click!")
                                startActivity(Intent(currentActivity, SettingsActivity::class.java))
                            }
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth()
                                .padding(start = 60.dp, end = 60.dp)
                        ) {
                            NavButton(text = "Fr") {
                                Log.i(
                                    ui_tag,
                                    "friends click!"
                                )
                            }

                            NavButton(text = "Ro", modifier = Modifier
                                .scale(1.3f)
                                .padding(bottom = 40.dp)) {
                                Log.i(
                                    ui_tag,
                                    "routes click!"
                                )
                            }

                            NavButton(text = "Ra") {
                                Log.i(
                                    ui_tag,
                                    "ratings click!"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
