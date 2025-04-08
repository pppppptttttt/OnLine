package ru.hse.online.client.view

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.hse.online.client.services.pedometer.ContextProvider
import ru.hse.online.client.services.pedometer.StepCounterService
import ru.hse.online.client.services.pedometer.StepServiceConnector

class PedometerViewModel(private val connector: StepServiceConnector, private val contextProvider: ContextProvider) : ViewModel() {
    val totalSteps: StateFlow<Int> = connector.steps
    val totalCalories: StateFlow<Double> = connector.caloriesBurned
    val totalDistance: StateFlow<Double> = connector.distanceTraveled
    val totalTime: StateFlow<Long> = connector.timeElapsed

    val onlineSteps: StateFlow<Int> = connector.stepsOnline
    val onlineCalories: StateFlow<Double> = connector.caloriesBurnedOnline
    val onlineDistance: StateFlow<Double> = connector.distanceTraveledOnline
    val onlineTime: StateFlow<Long> = connector.timeElapsedOnline

    init {
        connector.bind()
        StepCounterService.startService(contextProvider.getContext())
    }

    override fun onCleared() {
        connector.unbind()
        super.onCleared()
    }

    fun goOnLine() {
        connector.goOnline()
    }

    fun pauseOnline() {
        connector.pauseOnline()
    }

    fun resumeOnline() {
        connector.resumeOnline()
    }

    fun goOffLine() {
        connector.goOffline()
    }
}
