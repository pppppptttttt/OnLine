package ru.hse.online.client.repository.networking.api_data

import com.google.gson.annotations.JsonAdapter
import ru.hse.online.client.repository.networking.adapter.LocalDateAdapter
import java.time.LocalDate
import java.util.UUID

data class UserStatistics(
    val userId: UUID,
    val name: String,
    @JsonAdapter(LocalDateAdapter::class)
    val date: LocalDate,
    val value: Double
)

sealed class StatisticsResult {
    data class SuccessGet(val statistics: List<UserStatistics>, val code: Int) : StatisticsResult()
    data class SuccessPost(val code: Int) : StatisticsResult()
    data class Failure(val code: Int? = null, val message: String? = null) : StatisticsResult()
} 