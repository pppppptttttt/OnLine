package ru.hse.online.client.repository

import ru.hse.online.client.repository.networking.api_data.StatisticsResult
import ru.hse.online.client.repository.networking.api_data.UserStatistics
import ru.hse.online.client.repository.networking.api_service.StatisticsApiService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class StatisticsRepository(private val statisticsApiService: StatisticsApiService) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    suspend fun getStatistics(
        token: String,
        userId: UUID,
        name: String,
        start: LocalDate,
        end: LocalDate
    ): StatisticsResult {
        return try {
            val response = statisticsApiService.getStatistics(
                token = "Bearer $token",
                userId = userId,
                name = name,
                start = start.format(dateFormatter),
                end = end.format(dateFormatter)
            )
            when (response.code()) {
                200 -> response.body()?.let { StatisticsResult.SuccessGet(it, response.code()) }
                    ?: StatisticsResult.Failure(response.code(), "Response body is null")
                else -> StatisticsResult.Failure(response.code(), response.message())
            }
        } catch (e: Exception) {
            StatisticsResult.Failure(message = e.localizedMessage)
        }
    }

    suspend fun addStatistics(token: String, stats: List<UserStatistics>): StatisticsResult {
        return try {
            val response = statisticsApiService.addStatistics("Bearer $token", stats)
            when (response.code()) {
                204 -> StatisticsResult.SuccessPost(response.code())
                else -> StatisticsResult.Failure(response.code(), response.message())
            }
        } catch (e: Exception) {
            StatisticsResult.Failure(message = e.localizedMessage)
        }
    }
} 