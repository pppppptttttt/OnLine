package ru.hse.online.client.repository.networking.api_data

import java.util.*

data class PathRequest(
    val userId: UUID,
    val polyline: String
)

data class PathResponse(
    val userId: UUID,
    val pathId: UUID,
    val polyline: String,
    val created: Date
)

sealed class PathResult {
    data class Success(
        val paths: List<PathResponse>? = null,
        val code: Int
    ) : PathResult()

    data class Failure(
        val code: Int? = null,
        val message: String?
    ) : PathResult()
}