package ru.hse.online.client.repository

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import ru.hse.online.client.repository.networking.api_data.LeaderBoardResponse
import ru.hse.online.client.repository.networking.api_data.StatisticsResult
import ru.hse.online.client.repository.networking.api_data.UserStatistics
import ru.hse.online.client.repository.networking.api_service.StatisticsApiService
import ru.hse.online.client.repository.storage.AppDataStore
import ru.hse.online.client.services.StepCounterService.Stats
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class StatisticsRepository(
    private val statisticsApiService: StatisticsApiService,
    private val appDataStore: AppDataStore
) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    suspend fun getTodayStats(): MutableMap<Stats, Double> {
        val result: MutableMap<Stats, Double> = mutableMapOf()
        val date = LocalDate.now()
        Stats.entries.forEach {
            result[it] = 0.0
            when (val getRes = getStatistics(it, date, date)) {
                is StatisticsResult.SuccessGet -> {
                    result[it] = if (getRes.statistics.isEmpty()) 0.0 else getRes.statistics[0].value
                }
                is StatisticsResult.SuccessPost -> {}
                is StatisticsResult.Failure -> {
                    result[it] = 0.0
                }
            }
        }
        return result
    }

    suspend fun getStatistics(
        name: Stats,
        start: LocalDate,
        end: LocalDate
    ): StatisticsResult {
        val token: String = appDataStore.getValueFlow(AppDataStore.USER_TOKEN, "").first()
        val userId: UUID = appDataStore.getUserIdFlow().first()
        return try {
            val response = statisticsApiService.getStatistics(
                token = "Bearer $token",
                userId = userId,
                name = name.name,
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

    suspend fun sendStats(statsMap: MutableMap<Stats, MutableStateFlow<Double>>, date: LocalDate = LocalDate.now()): StatisticsResult {
        val token: String = appDataStore.getValueFlow(AppDataStore.USER_TOKEN, "").first()
        val userId: UUID = appDataStore.getUserIdFlow().first()
        val stats: MutableList<UserStatistics> = mutableListOf()
        Stats.entries.forEach {
            val value = statsMap[it]?.value ?: 0.0
            stats.add(UserStatistics(userId, it.name, date, value))
        }
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

    suspend fun getLeaderBoard(
        userId: UUID,
        start: LocalDate,
        end: LocalDate
    ): List<LeaderBoardResponse> {
        val token = "Bearer " + appDataStore.getValueFlow(AppDataStore.USER_TOKEN, "").first()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val response = statisticsApiService.getLeaderBoard(
            token = token,
            userId = userId,
            start = start.format(dateFormatter),
            end = end.format(dateFormatter)
        )

        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to load leaderboard: ${response.code()}")
        }
    }
} 