package ru.hse.online.client.presentation.screens

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import ru.hse.online.client.repository.StatisticsRepository
import ru.hse.online.client.repository.networking.api_data.StatisticsResult
import ru.hse.online.client.services.StepCounterService.Stats
import ru.hse.online.client.viewModels.SettingsViewModel
import ru.hse.online.client.viewModels.StatsViewModel
import ru.hse.online.client.viewModels.UserViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(onBack: () -> Unit, statsViewModel: StatsViewModel, settingsViewModel: SettingsViewModel = koinViewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text ("Your statistics") },
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
    ) {padding -> Column(modifier = Modifier.padding(padding).fillMaxSize()) {
        Statistics(statsViewModel, settingsViewModel)
    } }
}

@Composable
fun Statistics(statsViewModel: StatsViewModel, settingsViewModel: SettingsViewModel) {
    val uiState by statsViewModel.uiState.collectAsStateWithLifecycle()
    val dailyStepGoal by settingsViewModel.dailyStepGoal.collectAsStateWithLifecycle(6000)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        StatSelectionMenu(uiState.selectedStat, onStatSelected = { statsViewModel.selectStat(it) })

        Spacer(modifier = Modifier.height(16.dp))

        WeekNavigation(
            weekStart = uiState.currentWeek.with(DayOfWeek.MONDAY),
            onPrevious = { statsViewModel.moveWeek(false) },
            onNext = { statsViewModel.moveWeek(true) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
            uiState.errorMessage != null -> ErrorCard()
            uiState.weekData.isNotEmpty() -> WeekBarChart(uiState.weekData, uiState.selectedStat, dailyStepGoal)
            else -> PlaceholderCard("No data available")
        }
    }
}

@Composable
fun StatSelectionMenu(selectedStat: Stats, onStatSelected: (Stats) -> Unit) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Stats.entries.forEach { stat ->
                FilterChip(
                    selected = (stat == selectedStat),
                    onClick = { onStatSelected(stat) },
                    label = { Text(stat.name) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun WeekNavigation(weekStart: LocalDate, onPrevious: () -> Unit, onNext: () -> Unit) {
    val weekEnd = weekStart.plusDays(6)

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Previous Week")
        }

        Text(
            text = "${weekStart.dayOfMonth} ${weekStart.month.name.lowercase().replaceFirstChar { it.titlecase() }} " +
                    "- ${weekEnd.dayOfMonth} ${weekEnd.month.name.lowercase().replaceFirstChar { it.titlecase() }}",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next Week")
        }
    }
}

@Composable
fun WeekBarChart(data: Map<LocalDate, Double>, stat: Stats, dailyStepsGoal: Int) {
    Column {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            val maxValue = if (stat != Stats.STEPS) (data.values.maxOrNull() ?: 1.0) else dailyStepsGoal.toDouble()
            data.keys.sorted().forEach { date ->
                val value = data[date] ?: 0.0
                val heightPercent = (value / if (maxValue > 0) maxValue else 1.0).coerceIn(0.0, 1.0)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(40.dp)
                ) {
                    Text(
                        text = when (stat) {
                            Stats.TIME -> formatTime(value.toLong())
                            Stats.STEPS -> value.toInt().toString()
                            else -> "%.1f".format(value)
                        },
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((180 * heightPercent).dp)
                            .background(
                                color = if ((heightPercent == 1.0) && (stat == Stats.STEPS)) Color.Green else MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            )
                    )

                    Text(
                        text = date.dayOfMonth.toString() + "/" + date.monthValue.toString(),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceholderCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        ) {
            Text(message, fontSize = 18.sp)
        }
    }
}

@Composable
fun ErrorCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text("Error loading data", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
        }
    }
}
