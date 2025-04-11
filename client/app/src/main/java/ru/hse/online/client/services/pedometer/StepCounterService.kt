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
import kotlin.random.Random

class StepCounterService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private lateinit var sharedPrefs: SharedPreferences

    private var testJob: Job? = null

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
        sharedPrefs = getSharedPreferences("pedometer_prefs", Context.MODE_PRIVATE)
        loadSavedData()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        startForeground()
        registerSensor()
        startTesting()
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
        //_timeElapsed.value = System.currentTimeMillis() - startTime
    }

    private val autoSaveJob = CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            delay(1000 * 60 * 2)
            saveData()
        }
    }

    private fun loadSavedData() {
        _steps.value = sharedPrefs.getInt("steps", 0)
        _caloriesBurned.value = sharedPrefs.getFloat("calories", 0f).toDouble()
        _distanceTraveled.value = sharedPrefs.getFloat("distance", 0f).toDouble()
    }

    private fun saveData() {
        sharedPrefs.edit().apply {
            putInt("steps", _steps.value)
            putFloat("calories", _caloriesBurned.value.toFloat())
            putFloat("distance", _distanceTraveled.value.toFloat())
            apply()
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
