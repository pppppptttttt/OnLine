package ru.hse.online.client.repository.networking.api_service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import ru.hse.online.client.repository.networking.api_data.User
import ru.hse.online.client.repository.networking.api_data.UserStatistics
import java.util.UUID

interface StatisticsApiService {
    @GET("/data/api/v1/get/statistics/")
    suspend fun getStatistics(
        @Header("Authorization") token: String,
        @Query("userId") userId: UUID,
        @Query("name") name: String,
        @Query("start") start: String, // YYYY-MM-DD
        @Query("end") end: String      // YYYY-MM-DD
    ): Response<List<UserStatistics>>

    @GET("/data/api/v1/get/leaderboard/")
    suspend fun getLeaderBoard(
        @Header("Authorization") token: String,
        @Query("userId") userId: UUID,
        @Query("start") start: String,
        @Query("end") end: String
    ): Response<List<Pair<User, Double>>>

    @POST("/data/api/v1/create/statistics/")
    suspend fun addStatistics(
        @Header("Authorization") token: String,
        @Body stats: List<UserStatistics>
    ): Response<Void>
} 