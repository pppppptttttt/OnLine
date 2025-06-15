package ru.hse.online.client.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.hse.online.client.viewModels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val viewModel: SettingsViewModel = koinViewModel()
    val dailyStepGoal by viewModel.dailyStepGoal.collectAsState(initial = 6000)
    val userWeight by viewModel.userWeight.collectAsState(initial = 0)
    val userHeight by viewModel.userHeight.collectAsState(initial = 0)
    val userGender by viewModel.userGender.collectAsState(initial = "")

    var dailyGoalText by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var heightText by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        dailyGoalText = if (dailyStepGoal != 0) dailyStepGoal.toString() else ""
        weightText = if (userWeight != 0) userWeight.toString() else ""
        heightText = if (userHeight != 0) userHeight.toString() else ""
        selectedGender = userGender
    }

    LaunchedEffect(dailyStepGoal) {
        dailyGoalText = if (dailyStepGoal != 0) dailyStepGoal.toString() else ""
    }

    LaunchedEffect(userWeight) {
        weightText = if (userWeight != 0) userWeight.toString() else ""
    }

    LaunchedEffect(userHeight) {
        heightText = if (userHeight != 0) userHeight.toString() else ""
    }

    LaunchedEffect(userGender) {
        selectedGender = userGender
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = dailyGoalText,
                onValueChange = {
                    dailyGoalText = it
                    viewModel.saveDailyStepGoal(it)
                },
                label = { Text("Daily step goal") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = weightText,
                onValueChange = {
                    weightText = it
                    viewModel.saveUserWeight(it)
                },
                label = { Text("Weight (kg)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = heightText,
                onValueChange = {
                    heightText = it
                    viewModel.saveUserHeight(it)
                },
                label = { Text("Height (cm)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Text(
                text = "Gender:",
                modifier = Modifier.padding(top = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedGender == "male",
                    onClick = {
                        viewModel.saveUserGender("male")
                        selectedGender = "male"
                    }
                )
                Text("Male", modifier = Modifier.padding(end = 16.dp))

                RadioButton(
                    selected = selectedGender == "female",
                    onClick = {
                        viewModel.saveUserGender("female")
                        selectedGender = "female"
                    }
                )
                Text("Female")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}