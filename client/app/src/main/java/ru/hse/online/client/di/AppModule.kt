package ru.hse.online.client.di

import android.content.Context
import androidx.activity.ComponentActivity
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.hse.online.client.repository.FriendshipRepository
import ru.hse.online.client.repository.StatisticsRepository
import ru.hse.online.client.viewModels.LocationViewModel
import ru.hse.online.client.viewModels.AuthViewModel
import ru.hse.online.client.viewModels.StatsViewModel
import ru.hse.online.client.viewModels.SettingsViewModel
import ru.hse.online.client.repository.storage.AppDataStore
import ru.hse.online.client.repository.storage.LocationRepository
import ru.hse.online.client.repository.storage.PathRepository
import ru.hse.online.client.repository.storage.UserRepository
import ru.hse.online.client.services.ContextProvider
import ru.hse.online.client.services.StepServiceConnector
import ru.hse.online.client.viewModels.GroupViewModel
import ru.hse.online.client.viewModels.LeaderBoardViewModel
import ru.hse.online.client.viewModels.UserViewModel

val appModule = module {
    includes(networkModule)

    single<AppDataStore> { AppDataStore(context = get()) }
    single { LocationRepository() }
    single {
        UserRepository(
            appDataStore = get(),
            pathRepository = get(),
            friendshipRepository = get(),
            statisticsRepository = get(),
            statsViewModel = get()
        )
    }

    single<StepServiceConnector> {
        StepServiceConnector(
            context = get<Context>()
        )
    }

    single<ContextProvider> {
        object : ContextProvider {
            override fun getContext() = get<Context>()
        }
    }

    factory<PathRepository> {
        PathRepository(
            pathApiService = get(),
            appDataStore = get()
        )
    }

    factory<StatisticsRepository> {
        StatisticsRepository(
            statisticsApiService = get(),
            appDataStore = get()
        )
    }

    factory<FriendshipRepository> {
        FriendshipRepository(
            friendshipApiService = get(),
            appDataStore = get()
        )
    }

    viewModel<SettingsViewModel> { SettingsViewModel(get()) }

    viewModel<StatsViewModel> {
        StatsViewModel(
            connector = get(),
            contextProvider = get()
        )
    }

    viewModel<LocationViewModel> {
        LocationViewModel(
            contextProvider = get(),
            locationRepository = get(),
            userRepository = get()
        )
    }

    viewModel<UserViewModel> {
        UserViewModel(
            repository = get(),
            locationRepository = get()
        )
    }

    viewModel<LeaderBoardViewModel> {
        LeaderBoardViewModel(
            userRepository = get()
        )
    }

    viewModel { (activity: ComponentActivity) ->
        AuthViewModel(
            authUseCase = get(),
            createUserUseCase = get(),
            getUserUseCase = get(),
            dataStore = get(),
            authView = activity
        )
    }
}
