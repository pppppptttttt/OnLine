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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AssistWalker
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SocialDistance
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.ImeAction
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

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().background(color = Color(0x77000000))
        ) {
                val onLineStepCount by statsViewModel.onlineSteps.collectAsStateWithLifecycle(0)
                val onLineCalories by statsViewModel.onlineCalories.collectAsStateWithLifecycle(0.0)
                val onLineDistance by statsViewModel.onlineDistance.collectAsStateWithLifecycle(0.0)
                val onLineTime by statsViewModel.onlineTime.collectAsStateWithLifecycle(0L)

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
            val group by userViewModel.group.collectAsStateWithLifecycle()

            GroupList(group)

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
        modifier = Modifier.padding(bottom = 30.dp, start = 10.dp, end = 10.dp).fillMaxWidth()
    ) {
        Button(
            onClick = {
                if (isPaused) {
                    statsViewModel.resumeOnline()
                    locationViewModel.resumeOnline()
                } else {
                    statsViewModel.pauseOnline()
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
                userViewModel.createGroup()
            },
            modifier = Modifier.widthIn(min = 50.dp)
        ) {
            Icon(Icons.Filled.Task, contentDescription = "Stop badtrip")
        }

        Button(
            onClick = {
                if (isOnline) {
                    showPathSaveDialog = true
                } else {
                    statsViewModel.goOnLine()
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
            onClick = { showGroupDialog = true },
            modifier = Modifier.widthIn(min = 50.dp).alpha(if (!isOnline) 1f else 0f),
            enabled = !isOnline
        ) {
            Icon(Icons.Filled.Group, contentDescription = "GroupMenu")
        }
    }

    if (showPathSaveDialog) {
        PathSavingDialog(
            onDismissRequest = {
                showPathSaveDialog = false
                statsViewModel.goOffLine()
                locationViewModel.goOffLine(savePath = false)

            },
            onConfirmation = {
                showPathSaveDialog = false
                statsViewModel.goOffLine()
                locationViewModel.goOffLine(savePath = true)
            },
            onCancel = {
                showPathSaveDialog = false
            }
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
fun GroupList(group: Map<UUID, Friend>) {
    Box(
        modifier = Modifier
            .background(color = Color(0x77000000))
            .padding(16.dp)
            .height(120.dp)
            .alpha(if (group.isNotEmpty()) 1f else 0f)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
            }
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
fun PathSavingDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onCancel: () -> Unit
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
                    text = "Save path?",
                    modifier = Modifier.padding(16.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    IconButton(
                        onClick = { onCancel() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cancel",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

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
                        onClick = { onConfirmation() },
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
