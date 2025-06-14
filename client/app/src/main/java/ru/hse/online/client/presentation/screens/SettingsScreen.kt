package ru.hse.online.client.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.hse.online.client.viewModels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val viewModel: SettingsViewModel = koinViewModel<SettingsViewModel>()
    val userName by viewModel.userName.collectAsState(initial = "")
    val userEmail by viewModel.userEmail.collectAsState(initial = "")
    val userDailyGoal by viewModel.dailyStepGoal.collectAsState(initial = "")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text ("Settings") },
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
    ) { padding ->  Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            for (field in arrayOf(
                Pair(userName, "Nickname") to viewModel::saveUserName,
                Pair(userEmail, "Email") to viewModel::saveUserEmail,
                Pair(userDailyGoal.toString(), "Daily goal") to viewModel::saveDailyStepGoal
            )) {
                OutlinedTextField(
                    value = field.first.first,
                    onValueChange = { field.second(it) },
                    label = { Text(field.first.second) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f, false)
            ) {
                Row {
                    Button(onClick = {}) { Text("Share profile") }
                }

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF900020),
                        contentColor = Color.LightGray
                    )
                ) {
                    Text("Reset password")
                }
            }
        }
    }
}
