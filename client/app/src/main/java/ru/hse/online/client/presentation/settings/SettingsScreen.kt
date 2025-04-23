package ru.hse.online.client.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = koinViewModel<SettingsViewModel>()
    val userName by viewModel.userName.collectAsState(initial = "")
    val userEmail by viewModel.userEmail.collectAsState(initial = "")

    var selectedUnitsIndex by remember { mutableIntStateOf(0) }
    val units = listOf("Metric system", "Burgers per eagle")

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight(),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        for (field in arrayOf(
            Pair(userName, "Nickname") to viewModel::saveUserName,
            Pair(userEmail, "Email") to viewModel::saveUserEmail,
        )) {
            OutlinedTextField(
                value = field.first.first,
                onValueChange = { field.second(it) },
                label = { Text(field.first.second) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }


        SingleChoiceSegmentedButtonRow {
            units.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = units.size
                    ),
                    onClick = { selectedUnitsIndex = index },
                    selected = index == selectedUnitsIndex,
                    label = { Text(label) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

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
