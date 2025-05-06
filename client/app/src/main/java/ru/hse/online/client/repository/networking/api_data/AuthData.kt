package ru.hse.online.client.repository.networking.api_data

import java.util.UUID

data class AuthRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String? = null,
    val userId: UUID? = null,
    val code: Int? = null,
    val message: String? = null
)

sealed class AuthResult {
    data class Success(val token: String, val userId: UUID) : AuthResult()
    data class Failure(val code: Int, val message: String? = null) : AuthResult()
}

enum class AuthType {
    LOGIN, SIGNUP, NONE
}
