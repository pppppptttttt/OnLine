package ru.hse.online.client

import android.content.Context
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

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
}
