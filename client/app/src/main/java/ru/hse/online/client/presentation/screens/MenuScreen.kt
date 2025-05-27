package ru.hse.online.client.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import ru.hse.online.client.viewModels.UserViewModel

sealed class AppPage(val title: String) {
    data object Profile : AppPage("Profile")
    data object Friends : AppPage("Friends")
    data object Leaderboard : AppPage("Leaderboard")
    data object Statistics: AppPage("Statistics")
    data object Settings : AppPage("Settings")
}

@Composable
fun MenuScreen(viewModel: UserViewModel, navController: NavController) {
    var selectedPage by remember { mutableStateOf<AppPage>(AppPage.Profile) }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            listOf(
                AppPage.Profile,
                AppPage.Friends,
                AppPage.Leaderboard,
                AppPage.Statistics,
                AppPage.Settings
            ).forEach { page ->
                MenuRow(
                    title = page.title,
                    isSelected = page == selectedPage,
                    onClick = {
                        selectedPage = page
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = true,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { -it } + fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedPage) {
                    AppPage.Profile -> ProfileScreen()
                    AppPage.Leaderboard -> LeaderboardScreen()
                    AppPage.Friends -> FriendsScreen(viewModel, navController)
                    AppPage.Statistics -> StatisticsScreen()
                    AppPage.Settings -> SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun MenuRow(title: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.background
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}
