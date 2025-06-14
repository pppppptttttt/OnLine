package ru.hse.online.client.presentation.map

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AssistWalker
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SocialDistance
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.hse.online.client.presentation.screens.formatTime
import ru.hse.online.client.repository.networking.api_data.Friend
import ru.hse.online.client.viewModels.LocationViewModel
import ru.hse.online.client.viewModels.StatsViewModel
import ru.hse.online.client.viewModels.UserViewModel
import java.util.UUID

@Composable
fun MapOverlayView(statsViewModel: StatsViewModel, locationViewModel: LocationViewModel, userViewModel: UserViewModel) {
    var showGroupDialog by remember { mutableStateOf(false) }
    var showPathSaveDialog by remember { mutableStateOf(false) }
    val isOnline by statsViewModel.isOnline.collectAsStateWithLifecycle(false)
    val isPaused by statsViewModel.isPaused.collectAsStateWithLifecycle(false)
    val isInGroup by statsViewModel.isInGroup.collectAsStateWithLifecycle(false)
    val previewPath by locationViewModel.previewPath.collectAsStateWithLifecycle()
    val group by userViewModel.group.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        // Top metrics panel
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

        // Group list in top-right corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(fraction = 0.4f)
                .fillMaxHeight(fraction = 0.25f)
                .background(color = Color(0x77000000))
                .padding(8.dp)
                .alpha(if (group.isNotEmpty()) 1f else 0f)
        ) {
            GroupList(group = group)
        }

        // Clear preview button in top-right corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp)
        ) {
            Button(
                onClick = { locationViewModel.clearPreview() },
                modifier = Modifier.size(36.dp),
                enabled = previewPath.isNotEmpty()
            ) {
                Icon(Icons.Filled.Clear, contentDescription = "Remove preview")
            }
        }

        // Bottom control panel
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MapControlButton(
                icon = Icons.Filled.NearMe,
                onClick = { locationViewModel.centerCamera() },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(4.dp))

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
                modifier = Modifier.weight(1f),
                enabled = isOnline
            )

            Spacer(modifier = Modifier.width(4.dp))

            MapControlButton(
                icon = Icons.Filled.Task,
                onClick = { userViewModel.createGroup() },
                modifier = Modifier.weight(1f)
            )

            MapControlButton(
                icon = if (!isOnline) Icons.Filled.PlayArrow else Icons.Filled.Stop,
                onClick = {
                    if (isOnline) {
                        showPathSaveDialog = true
                    } else {
                        statsViewModel.goOnLine()
                        locationViewModel.goOnLine()
                    }
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(4.dp))

            MapControlButton(
                icon = Icons.Filled.Group,
                onClick = { showGroupDialog = true },
                modifier = Modifier.weight(1f),
                enabled = !isOnline
            )
        }
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

    if (showGroupDialog) {
        GroupCreationDialog(
            onConfirmation = { showGroupDialog = false },
            userViewModel = userViewModel
        )
    }
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
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp),
        enabled = enabled
    ) {
        Icon(icon, contentDescription = null)
    }
}

@Composable
fun GroupList(group: Map<UUID, Friend>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(group.values.toList()) { friend ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(friend.color)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = friend.username,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Divider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
        }
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