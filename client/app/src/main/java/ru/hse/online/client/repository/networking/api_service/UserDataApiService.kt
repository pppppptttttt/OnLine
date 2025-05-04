package ru.hse.online.client.repository.networking.api_service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import ru.hse.online.client.repository.networking.api_data.User
import java.util.UUID

interface UserDataApiService {
    @GET("/data/api/v1/get/user/")
    suspend fun getUserByIdOrEmail(
        @Header("Authorization") token: String,
        @Query("email") email: String?,
        @Query("userId") userId: UUID?
    ): User

    @POST("/data/api/v1/create/user")
    suspend fun createUser(
        @Header("Authorization") token: String,
        @Body user: User
    ): Response<Void>
}