package ru.hse.online.client.di

import android.content.Context
import androidx.activity.ComponentActivity
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.hse.online.client.viewModels.LocationViewModel
import ru.hse.online.client.presentation.auth.AuthViewModel
import ru.hse.online.client.viewModels.PedometerViewModel
import ru.hse.online.client.presentation.settings.SettingsViewModel
import ru.hse.online.client.repository.storage.AppDataStore
import ru.hse.online.client.repository.storage.LocationRepository
import ru.hse.online.client.repository.storage.UserRepository
import ru.hse.online.client.services.pedometer.ContextProvider
import ru.hse.online.client.services.pedometer.StepServiceConnector
import ru.hse.online.client.viewModels.UserViewModel

val appModule = module {
    includes(networkModule)

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

    single<AppDataStore> { AppDataStore(context = get()) }

    viewModel<SettingsViewModel> { SettingsViewModel(get()) }

    viewModel<PedometerViewModel> {
        PedometerViewModel(
            connector = get(),
            contextProvider = get()
        )
    }

    single { LocationRepository() }

    viewModel<LocationViewModel> {
        LocationViewModel(
            contextProvider = get(),
            locationRepository = get(),
            userRepository = get()
        )
    }

    single { UserRepository() }

    viewModel<UserViewModel> {
        UserViewModel(
            repository = get(),
            locationRepository = get()
        )
    }

    viewModel { (activity: ComponentActivity) ->
        AuthViewModel(
            authUseCase = get(),
            createUserUseCase = get(),
            authView = activity

        )
    }
}
