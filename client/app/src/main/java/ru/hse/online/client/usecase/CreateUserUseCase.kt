package ru.hse.online.client.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.Koin
import ru.hse.online.client.repository.networking.api_data.User
import ru.hse.online.client.repository.networking.api_data.UserResult
import ru.hse.online.client.repository.networking.api_service.UserDataApiService

class CreateUserUseCase(private val userApiService: UserDataApiService) {
    suspend fun execute(
        token: String,
        user: User
    ): UserResult = withContext(Dispatchers.IO) {
        try {
            userApiService.createUser("Bearer $token", user)
            UserResult.Success(code = 204)
        } catch (e: Exception) {
            UserResult.Failure(
                code = 0,
                message = e.message ?: "Failed to create user"
            )
        }
    }
}