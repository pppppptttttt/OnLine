package ru.hse.online.client.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext
import ru.hse.online.client.presentation.map.MapScreen
import ru.hse.online.client.presentation.pedometer.MainScreen
import ru.hse.online.client.viewModels.PedometerViewModel
import ru.hse.online.client.ui.theme.ClientTheme
import ru.hse.online.client.viewModels.LocationViewModel
import ru.hse.online.client.viewModels.UserViewModel

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Main : Screen("main", "Main", Icons.Default.Home)
    data object Map : Screen("map", "Map", Icons.Default.Map)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    data object Test : Screen("test", "Test", Icons.Outlined.Science)
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Main,
        Screen.Map,
        Screen.Test,
        Screen.Settings
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationComponent() {
    val navController = rememberNavController()
    val locationViewModel: LocationViewModel = koinViewModel()
    val pedometerViewModel: PedometerViewModel = koinViewModel()
    val userViewModel: UserViewModel = koinViewModel()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Main.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Main.route) { MainScreen(pedometerViewModel) }
            composable(Screen.Map.route) { MapScreen(pedometerViewModel, locationViewModel) }
            composable(Screen.Settings.route) { MenuScreen(userViewModel) }
            composable(Screen.Test.route) { TestScreen() }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KoinContext {
                ClientTheme {
                    PermissionScreen()
                }
            }
        }
    }
}
