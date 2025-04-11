package ru.hse.online.client.services.pedometer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.hse.online.client.repository.storage.AppDataStore
import kotlin.random.Random

class StepCounterService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private val dataStore: AppDataStore by inject()

    private var testJob: Job? = null
    private var autoSaveJob: Job? = null

    private val _KKAL_PER_STEP = 0.04
    private val _KM_PER_STEP = 0.00762

    private var isOnline = false

    private val _steps = MutableStateFlow<Int>(0)
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

    fun goOnline() {
        _stepsOnline.value = 0
        _caloriesBurnedOnline.value = 0.0
        _distanceTraveledOnline.value = 0.0
        _timeElapsedOnline.value = 0
        isOnline = true
    }

    fun pauseOnline() {
        isOnline = false
    }

    fun resumeOnline() {
        isOnline = true
    }

    fun goOffline() {
        isOnline = false
    }

    private val binder = LocalBinder()
    private lateinit var notificationManager: NotificationManager

    inner class LocalBinder : Binder() {
        fun getService(): StepCounterService = this@StepCounterService
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        loadSavedData()
        startForeground()
        registerSensor()
        startTesting()
        startAutoSave()
    }

    private fun loadSavedData() {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.getValueFlow(AppDataStore.USER_TOTAL_STEPS, 0).collect { value ->
                _steps.value = value
            }
            dataStore.getValueFlow(AppDataStore.USER_TOTAL_CALORIES, 0.0).collect { value ->
                _caloriesBurned.value = value
            }
            dataStore.getValueFlow(AppDataStore.USER_TOTAL_DISTANCE, 0.0).collect { value ->
                _distanceTraveled.value = value
            }
            dataStore.getValueFlow(AppDataStore.USER_TOTAL_TIME, 0).collect { value ->
                _timeElapsed.value = value
            }
            dataStore.getValueFlow(AppDataStore.USER_ONLINE_STEPS, 0).collect { value ->
                _stepsOnline.value = value
            }
            dataStore.getValueFlow(AppDataStore.USER_ONLINE_CALORIES, 0.0).collect { value ->
                _caloriesBurnedOnline.value = value
            }
            dataStore.getValueFlow(AppDataStore.USER_ONLINE_DISTANCE, 0.0).collect { value ->
                _distanceTraveledOnline.value = value
            }
            dataStore.getValueFlow(AppDataStore.USER_ONLINE_TIME, 0).collect { value ->
                _timeElapsedOnline.value = value
            }
        }
    }

    private fun startAutoSave() {
        autoSaveJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(1000 * 60 * 2)
                saveData()
            }
        }
    }


    private fun startTesting() {
        testJob = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(1000)
                _steps.value += Random.nextInt(1, 5)
                updateDerivedMetrics()
            }

        }
    }

    private fun startForeground() {
        createNotificationChannel()
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "OnLine",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "OnLine pedometer service"
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("OnLine")
            .setContentText("Counting your steps...")
            //.setSmallIcon(R.drawable.ic_walk)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(steps: Int) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("OnLine")
            .setContentText("Steps: $steps")
            //.addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun registerSensor() {
        stepSensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                _steps.value++
                if (isOnline) {
                    _stepsOnline.value++
                }
                updateDerivedMetrics()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        saveData()
        super.onDestroy()
        testJob?.cancel()
        testJob = null
        sensorManager.unregisterListener(this)
    }

    private fun updateDerivedMetrics() {
        _caloriesBurned.value = _steps.value * _KKAL_PER_STEP
        _distanceTraveled.value = _steps.value * _KM_PER_STEP
        if (isOnline) {
            _caloriesBurnedOnline.value = _stepsOnline.value * _KKAL_PER_STEP
            _distanceTraveledOnline.value = _stepsOnline.value * _KM_PER_STEP
        }
    }

    private fun saveData() {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveValue(AppDataStore.USER_TOTAL_STEPS, _steps.value)
            dataStore.saveValue(AppDataStore.USER_TOTAL_CALORIES, _caloriesBurned.value)
            dataStore.saveValue(AppDataStore.USER_TOTAL_DISTANCE, _distanceTraveled.value)
            dataStore.saveValue(AppDataStore.USER_TOTAL_TIME, _timeElapsed.value)
            dataStore.saveValue(AppDataStore.USER_ONLINE_STEPS, _stepsOnline.value)
            dataStore.saveValue(AppDataStore.USER_ONLINE_CALORIES, _caloriesBurnedOnline.value)
            dataStore.saveValue(AppDataStore.USER_ONLINE_DISTANCE, _distanceTraveledOnline.value)
            dataStore.saveValue(AppDataStore.USER_ONLINE_TIME, _timeElapsedOnline.value)
        }
    }

    companion object {
        private const val CHANNEL_ID = "online_pedometer_channel"
        private const val NOTIFICATION_ID = 1234

        fun startService(context: Context) {
            val intent = Intent(context, StepCounterService::class.java)
            context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, StepCounterService::class.java)
            context.stopService(intent)
        }
    }
}
