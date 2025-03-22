package ru.hse.online.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.androidx.compose.koinViewModel
import ru.hse.online.client.ui.theme.ClientTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ClientTheme {
                MainScreen()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: PedometerViewModel = koinViewModel()) {
    val stepCount by viewModel.totalSteps.collectAsStateWithLifecycle(0)
    val calories by viewModel.totalCalories.collectAsStateWithLifecycle(0.0)
    val distance by viewModel.totalDistance.collectAsStateWithLifecycle(0.0)
    val time by viewModel.totalTime.collectAsStateWithLifecycle(0L)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("OnLine")
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            MetricsGrid(
                stepCount = stepCount,
                stepGoal = 6000,
                calories = calories,
                distance = distance,
                time = time
            )
        }
    }
}


@Composable
fun AdditionalMetricCard(
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
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


@Composable
fun StepsMetricCard(
    stepCount: Int,
    stepGoal: Int
) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "%d".format(stepCount),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "/%d Steps".format(stepGoal),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "There will be goal progress",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Composable
fun MetricsGrid(
    stepCount: Int,
    stepGoal: Int,
    calories: Double,
    distance: Double,
    time: Long
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StepsMetricCard(
            stepCount = stepCount,
            stepGoal = stepGoal
        )
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            AdditionalMetricCard(
                icon = Icons.Default.Add,
                title = "km",
                value = "%.1f".format(distance),
            )
            AdditionalMetricCard(
                icon = Icons.Default.Info,
                title = "kcal",
                value = "%.1f".format(calories),
            )
            AdditionalMetricCard(
                icon = Icons.Default.Call,
                title = "time",
                value = formatTime(time),
            )
        }
    }
}


fun formatTime(millis: Long): String {
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60))
    return "%dh %dm".format(hours, minutes)
}


//@Preview(showBackground = true)
//@Composable
//fun PreviewCard() {
//    ClientTheme {
//        MainScreen()
//    }
//}
