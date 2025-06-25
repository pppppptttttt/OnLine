package ru.hse.online.client.presentation.map

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AssistWalker
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SocialDistance
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.hse.online.client.presentation.screens.formatTime
import ru.hse.online.client.repository.networking.api_data.Friend
import ru.hse.online.client.viewModels.GroupViewModel
import ru.hse.online.client.viewModels.LocationViewModel
import ru.hse.online.client.viewModels.StatsViewModel
import ru.hse.online.client.viewModels.UserViewModel

@Composable
fun MapOverlayView(
    statsViewModel: StatsViewModel,
    locationViewModel: LocationViewModel,
    groupViewModel: GroupViewModel
) {
    var showPathSaveDialog by remember { mutableStateOf(false) }
    val isOnline by statsViewModel.isOnline.collectAsStateWithLifecycle(false)
    val isPaused by statsViewModel.isPaused.collectAsStateWithLifecycle(false)
    val previewPath by locationViewModel.previewPath.collectAsStateWithLifecycle()
    var showGroupPanel by remember { mutableStateOf(false) }
    val group by groupViewModel.groupPaths.collectAsStateWithLifecycle()
    Log.i("TAGA ", "check $group")

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 80.dp)
                .background(color = Color(0x77000000)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val onLineStepCount by statsViewModel.onlineSteps.collectAsStateWithLifecycle(0)
            val onLineCalories by statsViewModel.onlineCalories.collectAsStateWithLifecycle(0.0)
            val onLineDistance by statsViewModel.onlineDistance.collectAsStateWithLifecycle(0.0)
            val onLineTime by statsViewModel.onlineTime.collectAsStateWithLifecycle(0L)

            SmallMetricCard(
                icon = Icons.Filled.AssistWalker,
                title = "steps",
                value = "%d".format(onLineStepCount),
                modifier = Modifier.weight(1f)
            )

            SmallMetricCard(
                icon = Icons.Default.SocialDistance,
                title = "km",
                value = "%.1f".format(onLineDistance),
                modifier = Modifier.weight(1f)
            )

            SmallMetricCard(
                icon = Icons.Default.LocalFireDepartment,
                title = "kcal",
                value = "%.1f".format(onLineCalories),
                modifier = Modifier.weight(1f)
            )

            SmallMetricCard(
                icon = Icons.Default.AccessTime,
                title = "time",
                value = formatTime(onLineTime),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MapControlButton(
                icon = Icons.Filled.Clear,
                onClick = { locationViewModel.clearPreview() },
                modifier = Modifier.size(56.dp),
                enabled = previewPath.isNotEmpty()
            )

            MapControlButton(
                icon = Icons.Filled.NearMe,
                onClick = { locationViewModel.centerCamera() },
                modifier = Modifier.size(56.dp),
                enabled = true
            )

            MapControlButton(
                icon = if (isPaused) Icons.Filled.DoubleArrow else Icons.Filled.Pause,
                onClick = {
                    if (isPaused) {
                        statsViewModel.resumeOnline()
                        locationViewModel.resumeOnline()
                    } else {
                        statsViewModel.pauseOnline()
                        locationViewModel.pauseOnline()
                    }
                },
                modifier = Modifier.size(56.dp),
                enabled = isOnline
            )

            MapControlButton(
                icon = if (!isOnline) Icons.Filled.PlayArrow else Icons.Filled.Stop,
                onClick = {
                    if (isOnline) {
                        groupViewModel.quitGroup()
                        showPathSaveDialog = true
                    } else {
                        statsViewModel.goOnLine()
                        locationViewModel.goOnLine()
                    }
                },
                modifier = Modifier.size(56.dp),
                enabled = true
            )

            MapControlButton(
                icon = Icons.Filled.People,
                onClick = { if (group.isNotEmpty()) showGroupPanel = true },
                modifier = Modifier.size(56.dp),
                enabled = group.isNotEmpty()
            )
        }
    }

    if (showGroupPanel) {
        GroupPanel(
            group = group.keys.toList(),
            onClose = { showGroupPanel = false }
        )
    }

    if (showPathSaveDialog) {
        PathSavingDialog(
            onDismissRequest = {
                showPathSaveDialog = false
                statsViewModel.goOffLine()
                locationViewModel.goOffLine(savePath = false)
            },
            onConfirmation = { value: String ->
                showPathSaveDialog = false
                statsViewModel.goOffLine()
                locationViewModel.goOffLine(savePath = true, value)
            },
            statsViewModel = statsViewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupPanel(group: List<Friend>, onClose: () -> Unit) {
    ModalNavigationDrawer(
        drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(240.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Group Members", style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Log.i("TAGA ", "check foreach $group")
                    group.forEach { friend ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color.Green, shape = CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(friend.username)
                        }
                    }
                }
            }
        },
        content = {}
    )
}

@Composable
fun MapControlButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun PathSavingDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    statsViewModel: StatsViewModel
) {
    val distance by statsViewModel.onlineDistance.collectAsStateWithLifecycle(0.0)
    val time by statsViewModel.onlineTime.collectAsStateWithLifecycle(0)
    var pathDescription by remember { mutableStateOf("") }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .wrapContentSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Would you like to save this path?",
                    modifier = Modifier.padding(16.dp),
                )

                Text(
                    text = "You walked ${"%.1f".format(distance)} km for ${formatTime(time)}",
                    modifier = Modifier.padding(16.dp),
                )

                TextField(
                    value = pathDescription,
                    onValueChange = { pathDescription = it },
                    label = { Text("Enter description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    IconButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NotInterested,
                            contentDescription = "Dismiss save",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = {
                            onConfirmation(pathDescription)
                            pathDescription = ""
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Confirm save",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SmallMetricCard(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(16.dp)
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview
@Composable
fun TopSide() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0x77000000)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
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