package ru.hse.online.client.repository.networking.api_data
import androidx.compose.ui.graphics.Color
import java.util.UUID


data class Friend(
    val userId: UUID,
    val username: String,
    val email: String,
    val stats: Map<String, Double> = emptyMap(),
    var color: Color = Color(0)
)

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
