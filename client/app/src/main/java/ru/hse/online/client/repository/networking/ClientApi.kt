package ru.hse.online.client.repository.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
// import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.hse.online.client.repository.networking.api_service.AuthApiService
import ru.hse.online.client.repository.networking.api_service.UserDataApiService

object ClientApi {
    private const val BASE_URL: String = "http://51.250.111.207:80"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
//        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val userDataApiService: UserDataApiService by lazy {
        retrofit.create(UserDataApiService::class.java)
    }
}
