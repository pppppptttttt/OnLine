package ru.hse.online.client.repository.networking.api_data

import com.google.gson.annotations.JsonAdapter
import ru.hse.online.client.repository.networking.adapter.LocalDateAdapter
import java.time.LocalDate
import java.util.*

data class PathRequest(
    val userId: UUID,
    val polyline: String,
    @JsonAdapter(LocalDateAdapter::class)
    val created: LocalDate,
    val name: String,
    val distance: Double,
    val duration: Double
)

data class PathResponse(
    val userId: UUID,
    val pathId: UUID,
    val polyline: String,
    @JsonAdapter(LocalDateAdapter::class)
    val created: LocalDate,
    val name: String,
    val distance: Double,
    val duration: Double
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