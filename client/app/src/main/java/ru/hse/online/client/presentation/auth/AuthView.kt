package ru.hse.online.client.presentation.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.hse.online.client.R
import ru.hse.online.client.presentation.settings.SettingsViewModel
import ru.hse.online.client.repository.networking.api_data.AuthType
import ru.hse.online.client.ui.theme.ClientTheme

class AuthView : ComponentActivity() {
    private val authModel: AuthViewModel = AuthViewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ClientTheme(darkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Draw()
                }
            }
        }
    }

    @Composable
    fun Draw(settingsModel: SettingsViewModel = koinViewModel()) {
        val email by settingsModel.userEmail.collectAsState(initial = "")
        val password by settingsModel.userPassword.collectAsState(initial = "")
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        var authType by rememberSaveable { mutableStateOf(AuthType.NONE) }

        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painterResource(id = R.drawable.frost),
                    contentScale = ContentScale.Crop
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Greetings!",
                    modifier = Modifier.padding(32.dp),
                    style = MaterialTheme.typography.headlineMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        authType = AuthType.SIGNUP
                    }) {
                        Text("Sign Up")
                    }

                    Button(onClick = {
                        authType = AuthType.LOGIN
                    }) {
                        Text("Log In")
                    }
                }

                if (authType != AuthType.NONE) {
                    OutlinedTextField(
                        value = email,
                        label = { Text("Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onValueChange = {
                            settingsModel.saveUserName(it)
                            settingsModel.saveUserEmail(it)
                        }
                    )

                    OutlinedTextField(
                        value = password,
                        label = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onValueChange = { settingsModel.saveUserPassword(it) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            val description =
                                if (passwordVisible) "Hide password" else "Show password"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, description)
                            }
                        }
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                authModel.handleAuth(authType, email, password, settingsModel)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (authType == AuthType.LOGIN) "Log In" else "Sign Up")
                    }
                }
            }
        }
    }
}
