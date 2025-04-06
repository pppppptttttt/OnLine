package ru.hse.online.client.view.map

import androidx.activity.ComponentActivity
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import ru.hse.online.client.view.SettingsView
import ru.hse.online.client.common.UI_LOGCAT_TAG
import ru.hse.online.client.view.common.DrawNavButtonUseCase

class MapOverlayView {

    @Composable
    fun Draw(currentActivity: ComponentActivity) {
        val navButtonDrawer = DrawNavButtonUseCase()

        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(top = 30.dp, start = 10.dp, end = 10.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                navButtonDrawer.Execute("St") {
                    Log.i(UI_LOGCAT_TAG, "stats click!")
                }

                navButtonDrawer.Execute("Se") {
                    Log.i(UI_LOGCAT_TAG, "settings click!")
                    val intent = Intent(currentActivity, SettingsView::class.java)
                    currentActivity.startActivity(intent)
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
                navButtonDrawer.Execute(text = "Fr") {
                    Log.i(
                        UI_LOGCAT_TAG,
                        "friends click!"
                    )
                }

                navButtonDrawer.Execute(text = "Ro", modifier = Modifier
                    .scale(1.3f)
                    .padding(bottom = 40.dp)) {
                    Log.i(
                        UI_LOGCAT_TAG,
                        "routes click!"
                    )
                }

                navButtonDrawer.Execute(text = "Ra") {
                    Log.i(
                        UI_LOGCAT_TAG,
                        "ratings click!"
                    )
                }
            }
        }
    }
}