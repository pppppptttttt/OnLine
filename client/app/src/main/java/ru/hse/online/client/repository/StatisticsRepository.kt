package ru.hse.online.client.repository

import kotlinx.coroutines.flow.first
import ru.hse.online.client.repository.networking.api_data.LeaderBoardResponse
import ru.hse.online.client.repository.networking.api_data.StatisticsResult
import ru.hse.online.client.repository.networking.api_data.UserStatistics
import ru.hse.online.client.repository.networking.api_service.StatisticsApiService
import ru.hse.online.client.repository.storage.AppDataStore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class StatisticsRepository(
    private val statisticsApiService: StatisticsApiService,
    private val appDataStore: AppDataStore
) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    enum class Stats {
        STEPS,
        KCALS,
        DISTANCE
    }

    suspend fun getTodayStats(): Map<Pair<Stats, LocalDate>, Double> {
        val result: MutableMap<Pair<Stats, LocalDate>, Double> = emptyMap<Pair<Stats, LocalDate>, Double>().toMutableMap()
        val date = LocalDate.now()
        Stats.entries.forEach {
            when (val getRes = getStatistics(it, date, date)) {
                is StatisticsResult.SuccessGet -> {
                    result[Pair(it, date)] = getRes.statistics[0].value
                }
                is StatisticsResult.SuccessPost -> {}
                is StatisticsResult.Failure -> {
                    result[Pair(it, date)] = 0.0
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

    suspend fun addStatistics(stats: List<UserStatistics>): StatisticsResult {
        val token: String = appDataStore.getValueFlow(AppDataStore.USER_TOKEN, "").first()
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