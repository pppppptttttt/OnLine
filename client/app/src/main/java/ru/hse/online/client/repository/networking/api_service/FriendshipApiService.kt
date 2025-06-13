package ru.hse.online.client.repository.networking.api_service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.hse.online.client.repository.networking.api_data.User // from UserData.kt
import java.util.UUID

interface FriendshipApiService {
    @GET("/data/api/v1/get/friend/{userId}")
    suspend fun getFriends(
        @Header("Authorization") token: String,
        @Path("userId") userId: UUID
    ): Response<List<User>>

    @POST("/data/api/v1/create/friend/")
    suspend fun addFriend(
        @Header("Authorization") token: String,
        @Query("userId") userId: UUID,
        @Query("friendId") friendId: UUID
    ): Response<Void>

    @DELETE("/data/api/v1/remove/friend/")
    suspend fun removeFriend(
        @Header("Authorization") token: String,
        @Query("userId") userId: UUID,
        @Query("friendId") friendId: UUID
    ): Response<Void>
} 