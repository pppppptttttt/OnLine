package ru.hse.online.client.repository.networking.api_service

import retrofit2.http.Body
import retrofit2.http.POST
import ru.hse.online.client.repository.networking.api_data.AuthRequest
import ru.hse.online.client.repository.networking.api_data.AuthResponse

interface AuthApiService {
    @POST("/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    @POST("/auth")
    suspend fun auth(@Body request: AuthRequest): AuthResponse
}
