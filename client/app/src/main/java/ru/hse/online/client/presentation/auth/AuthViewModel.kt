package ru.hse.online.client.presentation.auth

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import ru.hse.online.client.common.UI_LOGCAT_TAG
import ru.hse.online.client.repository.networking.ClientApi
import ru.hse.online.client.repository.networking.api_data.AuthResult
import ru.hse.online.client.repository.networking.api_data.AuthType
import ru.hse.online.client.usecase.AuthUseCase
import ru.hse.online.client.presentation.map.MapView
import ru.hse.online.client.presentation.settings.SettingsViewModel

class AuthViewModel(private val authView: ComponentActivity) {
    private var authUseCase: AuthUseCase = AuthUseCase(ClientApi.authApiService)

    suspend fun handleAuth(
        authType: AuthType,
        email: String,
        password: String,
        settingsModel: SettingsViewModel
    ) {
        when (val result = authUseCase.execute(authType, email, password)) {
            is AuthResult.Success -> {
                settingsModel.saveUserToken(result.token)
                startMapActivity()
            }

            is AuthResult.Failure -> handleError(result.code, result.message)
        }
    }

    private fun startMapActivity() {
        val intent = Intent(authView, MapView::class.java)
        authView.startActivity(intent)
    }

    private fun handleError(code: Int, message: String?) {
        Log.i(UI_LOGCAT_TAG, "Failed to authenticate with code $code. Message: $message")
    }
}