package ru.hse.online.client.presentation.screens

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ru.hse.online.client.viewModels.SettingsViewModel
import ru.hse.online.client.viewModels.StatsViewModel
import java.util.Locale
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: StatsViewModel) {
    val stepCount by viewModel.totalSteps.collectAsStateWithLifecycle(0)
    val calories by viewModel.totalCalories.collectAsStateWithLifecycle(0.0)
    val distance by viewModel.totalDistance.collectAsStateWithLifecycle(0.0)
    val time by viewModel.totalTime.collectAsStateWithLifecycle(0L)

    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val dailyStepGoal by settingsViewModel.dailyStepGoal.collectAsState(initial = 6000)
    
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
            Row {
                MetricsGrid(
                    stepCount = stepCount,
                    stepGoal = dailyStepGoal,
                    calories = calories,
                    distance = distance,
                    time = time
                )

                TextField(
                    value = "$dailyStepGoal",
                    onValueChange = { if (it.isDigitsOnly()) settingsViewModel.saveDailyStepGoal(it.toInt()) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                StepsProgress(
                    viewModel,
                    dailyStepGoal = dailyStepGoal
                )
            }
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
        DailyStepProgress(
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

@Composable
fun DailyStepProgress(stepCount: Int, stepGoal: Int) {
    val progress = remember(stepCount, stepGoal) {
        if (stepGoal > 0) min(stepCount.toFloat() / stepGoal, 1f) else 0f
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
        ) {
            LinearProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceVariant,
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = if (progress >= 1f) Color.Green else MaterialTheme.colorScheme.primary,
            )
        }

        if (progress >= 1f) {
            Text(
                text = "Daily goal achieved!",
                color = Color.Green,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun StepsProgress(statsViewModel: StatsViewModel, dailyStepGoal: Int) {
    val stepsMap by statsViewModel.stepsByDate.collectAsState()
    val dateFormat = remember { SimpleDateFormat("EEE", Locale.getDefault()) }
    val prevDays = remember {
        List(7) { index ->
            Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -index)
            }.time
        }.reversed()
    }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        prevDays.forEach { date ->
            val steps = stepsMap[date] ?: 0
            val progress = if (dailyStepGoal > 0) min(steps.toFloat() / dailyStepGoal, 1f) else 0f

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = dateFormat.format(date).take(3),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(36.dp),
                        color = Color.LightGray,
                        strokeWidth = 3.dp,
                        trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                    )
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(36.dp),
                        color = if (progress >= 1f) Color.Green else MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                        trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                    )
                }

                Text(
                    text = steps.toString(),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

fun formatTime(millis: Long): String {
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60))
    return "%dh %dm".format(hours, minutes)
}
