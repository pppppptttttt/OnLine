package ru.hse.online.client.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import ru.hse.online.client.viewModels.GroupViewModel
import ru.hse.online.client.viewModels.UserViewModel

sealed class AppPage(val title: String, val route: String, val icon: ImageVector) {
    data object Profile : AppPage("Profile", "menu/profile", Icons.Default.Person)
    data object Friends : AppPage("Friends", "menu/friends", Icons.Default.People)
    data object Leaderboard : AppPage("Leaderboard", "menu/leaderboard", Icons.Default.Leaderboard)
    data object Statistics: AppPage("Statistics", "menu/stats", Icons.Default.QueryStats)
    data object Settings : AppPage("Settings", "menu/settings", Icons.Default.Settings)
}

@Composable
fun MenuScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
                onClick = {
                    navController.navigate(page.route)
                },
                icon = page.icon
            )
        }
    }
}

@Composable
fun MenuRow(title: String, onClick: () -> Unit, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(icon, title, modifier = Modifier.padding(10.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
