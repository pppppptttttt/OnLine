package ru.hse.online.client.networking.api_data

data class AuthRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String? = null,
    val userId: String? = null,
    val code: Int? = null,
    val message: String? = null
)

sealed class AuthResult {
    data class Success(val token: String, val userId: String) : AuthResult()
    data class Failure(val code: Int, val message: String? = null) : AuthResult()
}

enum class AuthType {
    LOGIN, SIGNUP, NONE
}
