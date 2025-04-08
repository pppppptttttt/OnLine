package ru.hse.online.client.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import ru.hse.online.client.R
import ru.hse.online.client.usecase.AuthUseCase
import ru.hse.online.client.ui.theme.ClientTheme
import kotlinx.coroutines.launch
import ru.hse.online.client.common.UI_LOGCAT_TAG
import ru.hse.online.client.networking.ClientApi
import ru.hse.online.client.networking.api_data.AuthResult
import ru.hse.online.client.networking.api_data.AuthType

class AuthView : ComponentActivity() {
    private lateinit var authUseCase: AuthUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        authUseCase = AuthUseCase(ClientApi.authApiService)

        setContent {
            ClientTheme(darkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Draw()
                }
            }
        }
    }

    @Composable
    fun Draw() {
        var email: String by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
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
                        onValueChange = { email = it }
                    )

                    OutlinedTextField(
                        value = password,
                        label = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onValueChange = { password = it }
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                when (val result = authUseCase.execute(authType, email, password)) {
                                    is AuthResult.Success -> startMapActivity()
                                    is AuthResult.Failure -> handleError(result.code, result.message)
                                }
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

    private fun startMapActivity() {
        val intent = Intent(this, MapView::class.java)
        startActivity(intent)
    }

    private fun handleError(code: Int, message: String?) {
        Log.i(UI_LOGCAT_TAG, "Failed to authenticate with code $code. Message: $message")
    }

}
