package ru.hse.online.client.services.pedometer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StepServiceConnector(
    private val context: Context
) : ServiceConnection {
    private var service: StepCounterService? = null

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private val _caloriesBurned = MutableStateFlow(0.0)
    val caloriesBurned: StateFlow<Double> = _caloriesBurned.asStateFlow()

    private val _distanceTraveled = MutableStateFlow(0.0)
    val distanceTraveled: StateFlow<Double> = _distanceTraveled.asStateFlow()

    private val _timeElapsed = MutableStateFlow(0L)
    val timeElapsed: StateFlow<Long> = _timeElapsed.asStateFlow()

    private val _stepsOnline = MutableStateFlow<Int>(0)
    val stepsOnline: StateFlow<Int> = _stepsOnline.asStateFlow()

    private val _caloriesBurnedOnline = MutableStateFlow(0.0)
    val caloriesBurnedOnline: StateFlow<Double> = _caloriesBurnedOnline.asStateFlow()

    private val _distanceTraveledOnline = MutableStateFlow(0.0)
    val distanceTraveledOnline: StateFlow<Double> = _distanceTraveledOnline.asStateFlow()

    private val _timeElapsedOnline = MutableStateFlow(0L)
    val timeElapsedOnline: StateFlow<Long> = _timeElapsedOnline.asStateFlow()

    fun bind() {
        val intent = Intent(context, StepCounterService::class.java)
        context.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    fun unbind() {
        context.unbindService(this)
    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        val serviceBinder = binder as StepCounterService.LocalBinder
        service = serviceBinder.getService()

        CoroutineScope(Dispatchers.Default).launch {
            launch {
                service?.steps?.collect { steps ->
                    _steps.value = steps
                }
            }
            launch {
                service?.caloriesBurned?.collect { value ->
                    _caloriesBurned.value = value
                }
            }
            launch {
                service?.distanceTraveled?.collect { steps ->
                    _distanceTraveled.value = steps
                }
            }
            launch {
                service?.timeElapsed?.collect { steps ->
                    _timeElapsed.value = steps
                }
            }

            launch {
                service?.stepsOnline?.collect { steps ->
                    _stepsOnline.value = steps
                }
            }

            launch {
                service?.caloriesBurnedOnline?.collect { steps ->
                    _caloriesBurnedOnline.value = steps
                }
            }

            launch {
                service?.distanceTraveledOnline?.collect { steps ->
                    _distanceTraveledOnline.value = steps
                }
            }

            launch {
                service?.timeElapsedOnline?.collect { steps ->
                    _timeElapsedOnline.value = steps
                }
            }
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
    }

    fun goOnline() {
        service?.goOnline()
    }

    fun pauseOnline() {
        service?.pauseOnline()
    }

    fun resumeOnline() {
        service?.resumeOnline()
    }

    fun goOffline() {
        service?.goOffline()
    }
}
