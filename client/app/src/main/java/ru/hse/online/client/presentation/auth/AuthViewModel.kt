package ru.hse.online.client.presentation.auth

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import ru.hse.online.client.common.UI_LOGCAT_TAG
import ru.hse.online.client.presentation.TestView
import ru.hse.online.client.presentation.map.MapView
import ru.hse.online.client.presentation.settings.SettingsViewModel
import ru.hse.online.client.repository.networking.ClientApi
import ru.hse.online.client.repository.networking.api_data.AuthResult
import ru.hse.online.client.repository.networking.api_data.AuthType
import ru.hse.online.client.repository.networking.api_data.User
import ru.hse.online.client.repository.networking.api_data.UserResult
import ru.hse.online.client.usecase.AuthUseCase
import ru.hse.online.client.usecase.CreateUserUseCase
import kotlin.random.Random

class AuthViewModel(private val authView: ComponentActivity) {
    private var authUseCase: AuthUseCase = AuthUseCase(ClientApi.authApiService)
    private var createUserUseCase: CreateUserUseCase =
        CreateUserUseCase(ClientApi.userDataApiService)

    suspend fun handleAuth(
        authType: AuthType,
        email: String,
        password: String,
        settingsModel: SettingsViewModel
    ) {
        when (val authResult = authUseCase.execute(authType, email, password)) {
            is AuthResult.Success -> {
                if (authType == AuthType.SIGNUP) {
                    val createResult = createUserUseCase.execute(
                        token = authResult.token,
                        User(
                            email = email,
                            username = "aboba" + Random.nextInt(1, 100),
                            userId = authResult.userId
                        )
                    )
                    when (createResult) {
                        is UserResult.Success -> {
                            settingsModel.saveUserToken(authResult.token)
                            settingsModel.saveUserId(authResult.userId)
                            startMapActivity()
                        }

                        is UserResult.Failure -> handleError(
                            createResult.code,
                            createResult.message
                        )
                    }
                } else {
                    settingsModel.saveUserToken(authResult.token)
                    settingsModel.saveUserId(authResult.userId)
                    startMapActivity()
                }
            }

            is AuthResult.Failure -> handleError(authResult.code, authResult.message)
        }
    }

    private fun startMapActivity() {
        val intent = Intent(authView, TestView::class.java)
        authView.startActivity(intent)
    }

    private fun handleError(code: Int, message: String?) {
        Log.i(UI_LOGCAT_TAG, "Failed to authenticate with code $code. Message: $message")
    }
}