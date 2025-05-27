package ru.hse.online.client.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ru.hse.online.client.presentation.Screen
import ru.hse.online.client.repository.networking.api_data.Friend
import ru.hse.online.client.repository.networking.api_data.Path
import ru.hse.online.client.viewModels.UserViewModel

@Composable
fun FriendsScreen(viewModel: UserViewModel, navController: NavController) {

    val friends by viewModel.friends.collectAsStateWithLifecycle()
    var newFriendName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newFriendName,
                onValueChange = { newFriendName = it },
                label = { Text("Friend's name") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.addFriend(newFriendName)
                        newFriendName = ""
                    }
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    viewModel.addFriend(newFriendName)
                    newFriendName = ""
                },
                enabled = newFriendName.isNotBlank()
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (friends.isEmpty()) {
                item {
                    Text(
                        text = "You don't have any friends, sad",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            items(friends) { friend ->
                FriendCard(friend = friend, vm = viewModel, navController = navController)
            }
        }
    }
}

@Composable
fun FriendCard(friend: Friend, vm: UserViewModel, navController: NavController) {
    val canInvite by vm.isInGroup.collectAsStateWithLifecycle()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("friendProfile/${friend.userId}") },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = friend.username,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Activities: ${friend.email}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = { vm.inviteToGroup(friend.userId) },
                modifier = Modifier.widthIn(min = 50.dp).alpha(if (canInvite) 1f else 0f),
                enabled = canInvite
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Invite to group")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendProfileScreen(
    userId: String,
    vm: UserViewModel,
    navController: NavController
) {
    val friendProfile by vm.friendProfile.collectAsStateWithLifecycle()
    val friendPublicPaths by vm.friendPublicPaths.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        vm.loadFriendProfile(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(friendProfile?.username ?: "Friend Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = friendProfile?.username ?: "Loading...",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = friendProfile?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        friendProfile?.stats?.get("steps")?.let { StatCard(title = "Steps", value = it.toString()) }
                        friendProfile?.stats?.get("kcals")?.let { StatCard(title = "Kcals", value = it.toString()) }
                        friendProfile?.stats?.get("distance")?.let { StatCard(title = "Distance", value = it.toString()) }
                    }
                }
            }

            item {
                Text(
                    text = "Public Paths",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            if (friendPublicPaths.isEmpty()) {
                item {
                    Text(
                        text = "User don't have any public paths",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(friendPublicPaths) { path ->
                    PathCard(
                        path = path,
                        onAddClick = { vm.addPathToCollection(path) },
                        onPreview = {
                            vm.previewPath(path)
                            navController.popBackStack()
                            navController.navigate(Screen.Map.route)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun PathCard(path: Path, onAddClick: () -> Unit, onPreview: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = path.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${path.distance} km • ${path.duration} min",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(
                onClick = onPreview,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.RemoveRedEye,
                    contentDescription = "Preview path"
                )
            }

            IconButton(
                onClick = onAddClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add to collection"
                )
            }
        }
    }
}
