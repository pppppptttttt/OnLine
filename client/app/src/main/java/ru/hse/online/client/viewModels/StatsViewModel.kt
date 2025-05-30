package ru.hse.online.client.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.hse.online.client.services.ContextProvider
import ru.hse.online.client.services.StepCounterService
import ru.hse.online.client.services.StepServiceConnector
import java.util.Date

class StatsViewModel(
    private val connector: StepServiceConnector,
    private val contextProvider: ContextProvider
) : ViewModel() {

    val totalSteps: StateFlow<Int> = connector.steps
    val totalCalories: StateFlow<Double> = connector.caloriesBurned
    val totalDistance: StateFlow<Double> = connector.distanceTraveled
    val totalTime: StateFlow<Long> = connector.timeElapsed

    val onlineSteps: StateFlow<Int> = connector.stepsOnline
    val onlineCalories: StateFlow<Double> = connector.caloriesBurnedOnline
    val onlineDistance: StateFlow<Double> = connector.distanceTraveledOnline
    val onlineTime: StateFlow<Long> = connector.timeElapsedOnline

    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _isInGroup = MutableStateFlow(false)
    val isInGroup: StateFlow<Boolean> = _isInGroup.asStateFlow()

    private val _stepsByDate = MutableStateFlow<Map<Date, Int>>(emptyMap())
    val stepsByDate: StateFlow<Map<Date, Int>> = _stepsByDate.asStateFlow()

    init {
        connector.bind()
        StepCounterService.startService(contextProvider.getContext())
    }

    override fun onCleared() {
        connector.unbind()
        super.onCleared()
    }

    fun goOnLine() {
        _isOnline.value = true
        connector.goOnline()
    }

    fun pauseOnline() {
        if (_isOnline.value) {
            _isPaused.value = true
            connector.pauseOnline()
        }
    }

    fun resumeOnline() {
        if (_isOnline.value) {
            _isPaused.value = false
            connector.resumeOnline()
        }
    }

    fun goOffLine() {
        _isOnline.value = false
        connector.goOffline()
    }
}
