package ru.hse.online.client.di

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.scalars.ScalarsConverterFactory
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.hse.online.client.repository.networking.api_service.AuthApiService
import ru.hse.online.client.repository.networking.api_service.UserDataApiService
import ru.hse.online.client.usecase.AuthUseCase
import ru.hse.online.client.usecase.CreateUserUseCase
import ru.hse.online.client.usecase.GetUserUseCase

val networkModule = module {
    single { provideBaseUrl() }
    single { provideOkHttpClient() }

    single { provideRetrofit(get(), get()) }

    single<AuthApiService> { provideAuthService(get()) }
    single<UserDataApiService> { provideUserDataService(get()) }

    factory<CreateUserUseCase> { CreateUserUseCase(get()) }
    factory<GetUserUseCase> { GetUserUseCase(get()) }
    single<AuthUseCase> { AuthUseCase(get()) }
}

private fun provideBaseUrl(): String = "http://51.250.111.207:80"

private fun provideOkHttpClient(): OkHttpClient {
    val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    return OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()
}

private fun provideRetrofit(
    baseUrl: String,
    client: OkHttpClient
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

private fun provideAuthService(retrofit: Retrofit): AuthApiService {
    return retrofit.create(AuthApiService::class.java)
}

private fun provideUserDataService(retrofit: Retrofit): UserDataApiService {
    return retrofit.create(UserDataApiService::class.java)
}