package ru.hse.online.client.presentation.map

//import androidx.compose.material.icons.filled.RocketLaunch
//import androidx.compose.material.icons.filled.Route
//import androidx.compose.material.icons.filled.Settings
//import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ru.hse.online.client.presentation.common.NavButtonDrawer
import ru.hse.online.client.presentation.friendlist.FriendListView
import ru.hse.online.client.presentation.pedometer.PedometerView
import ru.hse.online.client.presentation.pedometer.PedometerViewModel
import ru.hse.online.client.presentation.routelist.RouteListView
import ru.hse.online.client.presentation.settings.SettingsView

class MapOverlayView(private val currentActivity: ComponentActivity) {

    companion object {
        private val paddingTop = 30.dp
        private val paddingStart = 10.dp
        private val paddingEnd = 10.dp
    }

    @Composable
    fun Draw(viewModel: PedometerViewModel = koinViewModel()) {
        val navButtonDrawer = NavButtonDrawer()

        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(top = paddingTop, start = paddingStart, end = paddingEnd)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.background(color = Color(0x77000000))) {
                    val onLineStepCount by viewModel.onlineSteps.collectAsStateWithLifecycle(0)
                    val onLineCalories by viewModel.onlineCalories.collectAsStateWithLifecycle(0.0)
                    val onLineDistance by viewModel.onlineDistance.collectAsStateWithLifecycle(0.0)
                    val onLineTime by viewModel.onlineTime.collectAsStateWithLifecycle(0L)

                    Column {
                        for (stat in arrayOf(
                            Pair(onLineStepCount, "steps"),
                            Pair(onLineDistance, "meters"),
                            Pair(onLineTime, "hm"),
                            Pair(onLineCalories, "kcal")
                        )) {
                            Text(
                                text = "${stat.first.format(2)} ${stat.second} on line!",
                                fontSize = 16.sp,
                                style = TextStyle(
                                    shadow = Shadow(
                                        blurRadius = 2.0f,
                                        offset = Offset(2.0f, 5.0f)
                                    )
                                )
                            )
                        }
                    }
                }

                Column {
                    navButtonDrawer.Draw(
                        from = currentActivity,
                        to = SettingsView::class.java,
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }

                    var iconPlay by remember { mutableStateOf(false) }
                    var dialogDraw by remember { mutableStateOf(false) }
                    Button(
                        onClick = {
                            iconPlay = !iconPlay
                            if (iconPlay) viewModel.goOnLine()
                            else {
                                viewModel.goOffLine()
                                dialogDraw = true
                            }
                        },
                    ) {
                        if (iconPlay)
                            Icon(Icons.Filled.Pause, contentDescription = "Start badtrip")
                        else
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Pause badtrip")
                    }

                    if (dialogDraw) {
                        Dialog_({ dialogDraw = false }, { dialogDraw = false })
                    }
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.padding(bottom = 70.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(start = 60.dp, end = 60.dp)
            ) {
                navButtonDrawer.Draw(
                    from = currentActivity,
                    to = FriendListView::class.java,
                    function = {
                        Icon(Icons.Filled.SupervisedUserCircle, contentDescription = "Settings")
                    }
                )

                navButtonDrawer.Draw(
                    from = currentActivity,
                    to = PedometerView::class.java,
                    modifier = Modifier
                        .scale(1.3f)
                        .padding(bottom = 40.dp)
                ) {
                    Icon(Icons.Filled.RocketLaunch, contentDescription = "Settings")
                }

                navButtonDrawer.Draw(
                    currentActivity,
                    RouteListView::class.java
                ) {
                    Icon(Icons.Filled.Route, contentDescription = "Settings")
                }
            }
        }
    }

    @Composable
    fun Dialog_(
        onDismissRequest: () -> Unit,
        onConfirmation: () -> Unit,
    ) {
        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Would you like to save path you walked on line?",
                        modifier = Modifier.padding(16.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        TextButton(
                            onClick = { onDismissRequest() },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Dismiss")
                        }
                        TextButton(
                            onClick = { onConfirmation() },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}

private fun Number.format(scale: Int): String =
    if (this is Int) {
        "%d".format(this)
    } else if (this is Double) {
        "%.${scale}f".format(this)
    } else {
        toString()
    }

