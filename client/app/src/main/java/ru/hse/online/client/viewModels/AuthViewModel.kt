package ru.hse.online.client.viewModels

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import ru.hse.online.client.presentation.MainActivity
import ru.hse.online.client.repository.networking.api_data.AuthResult
import ru.hse.online.client.repository.networking.api_data.AuthType
import ru.hse.online.client.usecase.AuthUseCase
import ru.hse.online.client.repository.networking.api_data.User
import ru.hse.online.client.repository.networking.api_data.UserResult
import ru.hse.online.client.repository.storage.AppDataStore
import ru.hse.online.client.repository.storage.UserRepository
import ru.hse.online.client.usecase.CreateUserUseCase
import ru.hse.online.client.usecase.GetUserUseCase
import kotlin.random.Random

class AuthViewModel(
    private val authUseCase: AuthUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val dataStore: AppDataStore,
    private val authView: ComponentActivity
) : ViewModel() {
    private val TAG: String = "APP_AUTH_VIEWMODEL"

    suspend fun handleAuth(
        authType: AuthType,
        email: String,
        password: String,
        username: String,
        settingsModel: SettingsViewModel
    ) {
        when (val authResult = authUseCase.execute(authType, email, password)) {
            is AuthResult.Success -> {
                if (authType == AuthType.SIGNUP) {
                    val createResult = createUserUseCase.execute(
                        token = authResult.token,
                        User(
                            email = email,
                            username = username,
                            userId = authResult.userId
                        )
                    )
                    when (createResult) {
                        is UserResult.Success -> {
                            settingsModel.saveUserToken(authResult.token)
                            settingsModel.saveUserId(authResult.userId)
                            dataStore.saveCredentials(authResult.token, authResult.userId, email, username, password)
                            startMainActivity()
                        }

                        is UserResult.Failure -> handleError(
                            createResult.code,
                            createResult.message
                        )
                    }
                } else {
                    settingsModel.saveUserToken(authResult.token)
                    settingsModel.saveUserId(authResult.userId)
                    dataStore.saveCredentials(authResult.token, authResult.userId, email, username, password)
                    // userRepository.loadAll(User)
                    startMainActivity()
                }
            }

            is AuthResult.Failure -> handleError(authResult.code, authResult.message)
        }
    }

    fun start() {
        startMainActivity()
    }

    private fun startMainActivity() {
        val intent = Intent(authView, MainActivity::class.java)
        authView.startActivity(intent)
    }

    private fun handleError(code: Int, message: String?) {
        Log.i(TAG, "Failed to authenticate with code $code. Message: $message")
    }
}