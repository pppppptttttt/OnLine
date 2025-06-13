package ru.hse.online.client.viewModels

import android.icu.util.Calendar
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.hse.online.client.repository.StatisticsRepository
import ru.hse.online.client.services.ContextProvider
import ru.hse.online.client.services.StepCounterService
import ru.hse.online.client.services.StepServiceConnector
import java.time.LocalDate
import java.util.Date

class StatsViewModel(
    private val connector: StepServiceConnector,
    private val contextProvider: ContextProvider,
    private val statisticsRepository: StatisticsRepository
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

    private val _prevSevenDaysStats = MutableStateFlow<MutableMap<LocalDate, Int>>(mutableMapOf())
    val prevSevenDaysStats: StateFlow<MutableMap<LocalDate, Int>> = _prevSevenDaysStats.asStateFlow()

    init {
        connector.bind()
        StepCounterService.startService(contextProvider.getContext())

        val prevDays = List(7) { index ->
            LocalDate.now().minusDays(index.toLong())
        }.reversed()

        var value = 1;
        for (prev in prevDays) {
            _prevSevenDaysStats.value[prev] = 1234*value
            value++;
        }
    }

    fun pauseAll() {
        connector.pauseAll()
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
