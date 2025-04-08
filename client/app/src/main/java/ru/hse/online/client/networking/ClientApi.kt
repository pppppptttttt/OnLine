package ru.hse.online.client.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.hse.online.client.networking.api_service.AuthApiService

object ClientApi {
    private const val BASE_URL: String = "http://10.0.2.2:80"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}
