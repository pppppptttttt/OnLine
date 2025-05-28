package ru.hse.online.client.repository.networking.api_service

import retrofit2.Response
import retrofit2.http.*
import ru.hse.online.client.repository.networking.api_data.PathRequest
import ru.hse.online.client.repository.networking.api_data.PathResponse
import java.util.*

interface PathApiService {
    @GET("/data/api/v1/get/path/{userId}")
    suspend fun getPaths(
        @Header("Authorization") token: String,
        @Path("userId") userId: UUID
    ): Response<List<PathResponse>>

    @POST("/data/api/v1/create/path")
    suspend fun createPath(
        @Header("Authorization") token: String,
        @Body path: PathRequest
    ): Response<Void>

    @DELETE("/data/api/v1/remove/path/")
    suspend fun deletePath(
        @Header("Authorization") token: String,
        @Query("userId") userId: UUID,
        @Query("pathId") pathId: UUID
    ): Response<Void>
}