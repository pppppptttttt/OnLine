package ru.hse.online.client.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.hse.online.client.repository.networking.api_data.UserResult
import ru.hse.online.client.repository.networking.api_service.UserDataApiService
import java.util.UUID

class GetUserUseCase(private val userApiService: UserDataApiService) {
    suspend fun execute(
        token: String,
        email: String?,
        userId: UUID?
    ): UserResult = withContext(Dispatchers.IO) {
        try {
            val user = userApiService.getUserByIdOrEmail("Bearer $token", email, userId)
            UserResult.Success(user = user, code = 200)
        } catch (e: Exception) {
            UserResult.Failure(
                code = 0,
                message = e.message ?: "Failed to get user"
            )
        }
    }
}