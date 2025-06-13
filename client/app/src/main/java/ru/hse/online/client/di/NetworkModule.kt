package ru.hse.online.client.di

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.viewModel
import retrofit2.converter.scalars.ScalarsConverterFactory
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.hse.online.client.repository.FriendshipRepository
import ru.hse.online.client.repository.networking.api_service.AuthApiService
import ru.hse.online.client.repository.networking.api_service.PathApiService
import ru.hse.online.client.repository.networking.api_service.StatisticsApiService
import ru.hse.online.client.repository.networking.api_service.FriendshipApiService
import ru.hse.online.client.repository.StatisticsRepository
import ru.hse.online.client.repository.networking.adapter.LocalDateAdapter
import ru.hse.online.client.repository.networking.api_service.UserDataApiService
import ru.hse.online.client.repository.storage.PathRepository
import ru.hse.online.client.usecase.AuthUseCase
import ru.hse.online.client.usecase.CreateUserUseCase
import ru.hse.online.client.usecase.GetUserUseCase
import ru.hse.online.client.viewModels.GroupViewModel
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

val networkModule = module {
    single { provideBaseUrl() }
    single { provideGson() }
    single { provideOkHttpClient() }

    single { provideRetrofit(get(), get(), get()) }

    viewModel<GroupViewModel> {
        GroupViewModel(
            dataStore = get(),
            stompClient = provideStompClient()
        )
    }

    single<AuthApiService> { provideAuthService(get()) }
    single<UserDataApiService> { provideUserDataService(get()) }
    single<PathApiService> { providePathApiService(get()) }
    single<StatisticsApiService> { provideStatisticsApiService(get()) }
    single<FriendshipApiService> { provideFriendshipApiService(get()) }

    factory<CreateUserUseCase> { CreateUserUseCase(get()) }
    factory<GetUserUseCase> { GetUserUseCase(get()) }
    factory<AuthUseCase> { AuthUseCase(get()) }
}

private fun provideBaseUrl(): String = "http://51.250.111.207:80"

private fun provideOkHttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
}

private fun provideStompClient(): StompClient {
    return Stomp.over(
        Stomp.ConnectionProvider.OKHTTP,
        provideBaseUrl() + "/group/ws"
    )
}

private fun provideGson() = GsonBuilder()
    .registerTypeAdapter(java.time.LocalDate::class.java, LocalDateAdapter())
    .create()

private fun provideRetrofit(
    baseUrl: String,
    client: OkHttpClient,
    gson: com.google.gson.Gson
): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

private fun provideAuthService(retrofit: Retrofit): AuthApiService {
    return retrofit.create(AuthApiService::class.java)
}

private fun provideUserDataService(retrofit: Retrofit): UserDataApiService {
    return retrofit.create(UserDataApiService::class.java)
}

private fun providePathApiService(retrofit: Retrofit): PathApiService {
    return retrofit.create(PathApiService::class.java)
}

private fun provideStatisticsApiService(retrofit: Retrofit): StatisticsApiService {
    return retrofit.create(StatisticsApiService::class.java)
}

private fun provideFriendshipApiService(retrofit: Retrofit): FriendshipApiService {
    return retrofit.create(FriendshipApiService::class.java)
}