package ru.hse.online.client.di

import android.content.Context
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.hse.online.client.services.pedometer.ContextProvider
import ru.hse.online.client.presentation.PedometerViewModel
import ru.hse.online.client.presentation.settings.SettingsViewModel
import ru.hse.online.client.repository.storage.AppDataStore
import ru.hse.online.client.services.pedometer.StepServiceConnector

val appModule = module {

    single<StepServiceConnector> {
        StepServiceConnector(
            context = get<Context>()
        )
    }

    single<ContextProvider> { object : ContextProvider {
        override fun getContext() = get<Context>()
    }}

    viewModel<PedometerViewModel> {
        PedometerViewModel(
            connector = get(),
            contextProvider = get()
        )
    }

    single<AppDataStore> { AppDataStore(context = get()) }

    viewModel<SettingsViewModel> { SettingsViewModel(get()) }
}
