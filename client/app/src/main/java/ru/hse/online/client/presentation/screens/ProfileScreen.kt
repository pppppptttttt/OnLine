package ru.hse.online.client.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ru.hse.online.client.presentation.Screen
import ru.hse.online.client.repository.networking.api_data.PathResponse
import ru.hse.online.client.viewModels.StatsViewModel
import ru.hse.online.client.viewModels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(userViewModel: UserViewModel, statsViewModel: StatsViewModel, navController: NavController, onBack: () -> Unit) {
    val achievements by userViewModel.achievements.collectAsState()
    val savedPaths by userViewModel.userPaths.collectAsState()
    
    val stepCount by statsViewModel.totalSteps.collectAsStateWithLifecycle(0)
    val calories by statsViewModel.totalCalories.collectAsStateWithLifecycle(0.0)
    val distance by statsViewModel.totalDistance.collectAsStateWithLifecycle(0.0)

    val stepLife by userViewModel.lifetimeSteps.collectAsStateWithLifecycle(0)
    val caloriesLife by userViewModel.lifetimeCalories.collectAsStateWithLifecycle(0.0)
    val distanceLife by userViewModel.lifetimeDistance.collectAsStateWithLifecycle(0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text ("Your profile") },
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
    ) {padding -> LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                SectionTitle("Lifetime totals")
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                LifetimeStatsRow(stepCount + stepLife, calories + caloriesLife, distance + distanceLife)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                SectionTitle("Achievements")
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            item {
                AchievementsList(achievements = achievements)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                SectionTitle("Saved Paths")
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            items(savedPaths) { path ->
                PathCard(
                    path = path,
                    onPreview = { 
                        userViewModel.previewPath(path)
                        navController.popBackStack()
                        navController.navigate(Screen.Map.route)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AchievementsList(achievements: List<Int>) {

}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
    )
}

@Composable
fun LifetimeStatsRow(steps: Int, calories: Double, distance: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LifetimeStatCard(
            value = steps.toString(),
            label = "Steps"
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        LifetimeStatCard(
            value = String.format("%.0f", calories),
            label = "Calories"
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        LifetimeStatCard(
            value = String.format("%.1f km", distance),
            label = "Distance"
        )
    }
}

@Composable
fun LifetimeStatCard(
    value: String,
    label: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PathCard(path: PathResponse, onPreview: () -> Unit) {
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
        }
    }
}
