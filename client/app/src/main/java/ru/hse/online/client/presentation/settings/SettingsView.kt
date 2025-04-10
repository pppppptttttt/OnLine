package ru.hse.online.client.presentation.settings

import androidx.compose.runtime.getValue
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.hse.online.client.common.UI_LOGCAT_TAG
import ru.hse.online.client.ui.theme.ClientTheme

class SettingsView() : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Log.i(UI_LOGCAT_TAG, "settings created!")
            ClientTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Draw()
                }
            }
        }
    }

    @Composable
    fun Draw() {
        val viewModel: SettingsViewModel = koinViewModel<SettingsViewModel>()
        val userName by viewModel.userName.collectAsState(initial = "")
        val userEmail by viewModel.userEmail.collectAsState(initial = "")
        val userPassword by viewModel.userPassword.collectAsState(initial = "")
        val userUUID by viewModel.userUUID.collectAsState(initial = "")

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            for (field in arrayOf(
                Pair(userName, "Nickname") to viewModel::saveUserName,
                Pair(userEmail, "Email") to viewModel::saveUserEmail,
                Pair(userPassword, "Password") to viewModel::saveUserPassword
            )) {
                OutlinedTextField(
                    value = field.first.first,
                    onValueChange = { field.second(it) },
                    label = { Text(field.first.second) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
            Text(text = userUUID)
        }
    }
}

