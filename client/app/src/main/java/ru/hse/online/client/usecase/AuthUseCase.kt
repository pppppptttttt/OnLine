package ru.hse.online.client.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.Koin
import ru.hse.online.client.repository.networking.api_data.AuthRequest
import ru.hse.online.client.repository.networking.api_data.AuthResult
import ru.hse.online.client.repository.networking.api_data.AuthType
import ru.hse.online.client.repository.networking.api_service.AuthApiService

class AuthUseCase(private val authApiService: AuthApiService) {
    suspend fun execute(
        authType: AuthType,
        email: String,
        password: String,
    ): AuthResult = withContext(Dispatchers.IO) {
        try {
            val request = AuthRequest(email, email, password)

            val response = when (authType) {
                AuthType.LOGIN -> authApiService.login(request)
                AuthType.SIGNUP -> authApiService.register(request)
                else -> throw IllegalArgumentException()
            }

            if (response.token != null && response.userId != null) {
                AuthResult.Success(response.token, response.userId)
            } else {
                AuthResult.Failure(
                    response.code ?: 500,
                    response.message ?: "Unknown error"
                )
            }
        } catch (e: Exception) {
            AuthResult.Failure(
                code = 0, // Custom code for network/unknown errors
                message = e.message ?: "Network request failed"
            )
        }
    }
}
