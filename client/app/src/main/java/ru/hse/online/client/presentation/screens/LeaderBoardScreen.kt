package ru.hse.online.client.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ru.hse.online.client.viewModels.LeaderBoardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderBoardScreen(viewModel: LeaderBoardViewModel = koinViewModel(), onBack: () -> Unit) {
    val selectedTimeFrame by viewModel.selectedTimeFrame.collectAsStateWithLifecycle()
    val leaderboardState by viewModel.leaderboardState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    viewModel.loadLeaderboard(selectedTimeFrame)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboards") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimeFrameButton(
                    text = "Daily",
                    selected = selectedTimeFrame == LeaderBoardViewModel.TimeFrame.DAILY,
                    onClick = { viewModel.loadLeaderboard(LeaderBoardViewModel.TimeFrame.DAILY) }
                )
                TimeFrameButton(
                    text = "Weekly",
                    selected = selectedTimeFrame == LeaderBoardViewModel.TimeFrame.WEEKLY,
                    onClick = { viewModel.loadLeaderboard(LeaderBoardViewModel.TimeFrame.WEEKLY) }
                )
                TimeFrameButton(
                    text = "Monthly",
                    selected = selectedTimeFrame == LeaderBoardViewModel.TimeFrame.MONTHLY,
                    onClick = { viewModel.loadLeaderboard(LeaderBoardViewModel.TimeFrame.MONTHLY) }
                )
            }

            when (val state = leaderboardState) {
                is LeaderBoardViewModel.LeaderBoardState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LeaderBoardViewModel.LeaderBoardState.Success -> {
                    LeaderBoardList(
                        users = state.users,
                        currentUser = currentUser,
                        modifier = Modifier.weight(1f)
                    )
                }

                is LeaderBoardViewModel.LeaderBoardState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error loading leaderboard: ${state.message}")
                    }
                }
            }
        }
    }
}

@Composable
fun TimeFrameButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(text)
    }
}

@Composable
fun LeaderBoardList(users: List<LeaderBoardViewModel.LeaderBoardUser>, currentUser: LeaderBoardViewModel.LeaderBoardUser?, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            itemsIndexed(users) { index, user ->
                LeaderBoardItem(
                    user = user,
                    rank = index + 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        currentUser?.let { user ->
            val rank = users.indexOfFirst { it.id == user.id } + 1
            val userData = if (rank > 0) users[rank - 1] else null

//            CurrentUserCard(
//                user = user,
//                rank = rank,
//                steps = userData?.steps ?: 0,
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(16.dp)
//            )
        }
    }
}

@Composable
fun LeaderBoardItem(user: LeaderBoardViewModel.LeaderBoardUser, rank: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$rank",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.width(36.dp)
            )
            Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${user.steps}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
