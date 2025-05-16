package ru.hse.online.client.repository.networking.api_data

import java.util.UUID

data class User(
    val userId: UUID,
    val username: String,
    val email: String,
    val friends: List<String> = emptyList()
)

sealed class UserResult {
    data class Success(val user: User? = null, val code: Int) : UserResult()
    data class Failure(val code: Int, val message: String) : UserResult()
}