package ru.hse.online.client.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssistWalker
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Snowshoeing
import androidx.compose.material.icons.filled.SocialDistance
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.hse.online.client.presentation.pedometer.AdditionalMetricCard
import ru.hse.online.client.presentation.pedometer.MetricsGrid
import ru.hse.online.client.presentation.pedometer.formatTime
import ru.hse.online.client.viewModels.LocationViewModel
import ru.hse.online.client.viewModels.PedometerViewModel
import ru.hse.online.client.viewModels.UserViewModel

class MapOverlayView() {

    companion object {
        private val paddingTop = 30.dp
        private val paddingStart = 10.dp
        private val paddingEnd = 10.dp
    }

    @Composable
    fun Draw(viewModel: PedometerViewModel, locationViewModel: LocationViewModel, userViewModel: UserViewModel) {
        var showDialog by remember { mutableStateOf(false) }
        val isOnline by viewModel.isOnline.collectAsStateWithLifecycle(false)
        val isPaused by viewModel.isPaused.collectAsStateWithLifecycle(false)
        val isInGroup by viewModel.isInGroup.collectAsStateWithLifecycle(false)

        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().background(color = Color(0x77000000))
            ) {
                    val onLineStepCount by viewModel.onlineSteps.collectAsStateWithLifecycle(0)
                    val onLineCalories by viewModel.onlineCalories.collectAsStateWithLifecycle(0.0)
                    val onLineDistance by viewModel.onlineDistance.collectAsStateWithLifecycle(0.0)
                    val onLineTime by viewModel.onlineTime.collectAsStateWithLifecycle(0L)

                    SmallMetricCard(
                        icon = Icons.Filled.AssistWalker,
                        title = "steps",
                        value = "%d".format(onLineStepCount)
                    )

                    SmallMetricCard(
                        icon = Icons.Default.SocialDistance,
                        title = "km",
                        value = "%.1f".format(onLineDistance)
                    )
                    SmallMetricCard(
                        icon = Icons.Default.LocalFireDepartment,
                        title = "kcal",
                        value = "%.1f".format(onLineCalories)
                    )
                    SmallMetricCard(
                        icon = Icons.Default.AccessTime,
                        title = "time",
                        value = formatTime(onLineTime)
                    )

            }

            Row (
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                val previewPath by locationViewModel.previewPath.collectAsStateWithLifecycle()
                Button(
                    onClick = {
                        locationViewModel.clearPreview()
                    },
                    modifier = Modifier.widthIn(min = 50.dp).alpha(if (previewPath.isNotEmpty()) 1f else 0f),
                    enabled = previewPath.isNotEmpty()
                ) {
                    Icon(Icons.Filled.Clear, contentDescription = "Remove preview")
                }
            }
        }

        Row (
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = paddingTop, start = paddingStart, end = paddingEnd).fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (isPaused) {
                        viewModel.resumeOnline()
                        locationViewModel.resumeOnline()
                    } else {
                        viewModel.pauseOnline()
                        locationViewModel.pauseOnline()
                    }
                },
                modifier = Modifier.widthIn(min = 50.dp).alpha(if (isOnline) 1f else 0f),
                enabled = isOnline
            ) {
                if (!isPaused)
                    Icon(Icons.Filled.Pause, contentDescription = "Pause badtrip")
                else
                    Icon(Icons.Filled.DoubleArrow, contentDescription = "Resume badtrip")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    if (isOnline) {
                        viewModel.goOffLine()
                        locationViewModel.goOffLine()
                    } else {
                        viewModel.goOnLine()
                        locationViewModel.goOnLine()
                    }
                },
                modifier = Modifier.widthIn(min = 50.dp)
            ) {
                if (!isOnline)
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Start badtrip")
                else
                    Icon(Icons.Filled.Stop, contentDescription = "Stop badtrip")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.widthIn(min = 50.dp).alpha(if (!isOnline) 1f else 0f),
                enabled = !isOnline
            ) {
                Icon(Icons.Filled.Group, contentDescription = "GroupMenu")
            }
        }

        if (showDialog) {
            GroupCreationDialog(
                onConfirmation = { showDialog = false },
                userViewModel = userViewModel
            )
        }
    }

    @Composable
    fun GroupCreationDialog(
        onConfirmation: () -> Unit,
        userViewModel: UserViewModel
    ) {
        var groupId by remember { mutableStateOf("") }

        Dialog(onDismissRequest = onConfirmation) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            //viewModel.onCreateGroup()
                            onConfirmation()
                            userViewModel.createGroup()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create New Group")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
                            value = groupId,
                            onValueChange = { groupId = it },
                            label = { Text("Enter group id") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    //viewModel.onAddMember(groupId)
                                    groupId = ""
                                }
                            )
                        )

                        Button(
                            onClick = {
                                //viewModel.onAddMember(groupId)
                                groupId = ""
                            },
                            enabled = groupId.isNotBlank()
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Connect to group")
                        }
                    }
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

    @Preview
    @Composable
    fun TopSide() {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().background(color = Color(0x77000000))
            ) {
                val onLineStepCount = 6543
                val onLineCalories = 456.1
                val onLineDistance = 1.123
                val onLineTime: Long = 1232133

                SmallMetricCard(
                    icon = Icons.Filled.AssistWalker,
                    title = "steps",
                    value = "%d".format(onLineStepCount)
                )

                SmallMetricCard(
                    icon = Icons.Default.SocialDistance,
                    title = "km",
                    value = "%.1f".format(onLineDistance)
                )
                SmallMetricCard(
                    icon = Icons.Default.LocalFireDepartment,
                    title = "kcal",
                    value = "%.1f".format(onLineCalories)
                )
                SmallMetricCard(
                    icon = Icons.Default.AccessTime,
                    title = "time",
                    value = formatTime(onLineTime)
                )

            }
        }
    }


    @Composable
    fun SmallMetricCard(
        icon: ImageVector,
        title: String,
        value: String,
    ) {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
