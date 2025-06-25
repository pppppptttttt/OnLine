package ru.hse.online.client.viewModels

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.delay
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.hse.online.client.repository.networking.api_data.Friend
import ru.hse.online.client.repository.storage.AppDataStore
import ru.hse.online.client.repository.storage.LocationRepository
import ua.naiksoftware.stomp.dto.LifecycleEvent
import kotlin.collections.plus

class GroupViewModel(
    private val dataStore: AppDataStore,
    private val stompClient: StompClient,
    private val locationRepository: LocationRepository,
    private val userViewModel: UserViewModel
) : ViewModel() {

    companion object {
        private val gson = Gson()
        private const val TAG = "APP_GROUP_VIEWMODEL"
        private const val DELAY_BETWEEN_LOCATION_UPDATES = 15000L
    }

    private var _locationState: MutableStateFlow<LatLng> = MutableStateFlow<LatLng>(LatLng(0.0,0.0))
    var location: StateFlow<LatLng> = _locationState.asStateFlow()

    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus.asStateFlow()

    private lateinit var email: String

    private var _receivedInvites: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
    var receivedInvites: StateFlow<Set<String>> = _receivedInvites.asStateFlow()

    private val _groupId = MutableStateFlow(-1L)
    val groupId: StateFlow<Long> = _groupId.asStateFlow()

    var isOnline = false

    private val compositeDisposable = CompositeDisposable()

    private val _groupPaths = MutableStateFlow<Map<Friend, List<LatLng>>>(mutableMapOf())
    val groupPaths: StateFlow<Map<Friend, List<LatLng>>> = _groupPaths.asStateFlow()


    init {
        viewModelScope.launch {
            email = dataStore.getValueFlow(
                AppDataStore.USER_EMAIL,
                defaultValue = ""
            ).first()
        }

        locationRepository.locationState
            .onEach { state ->
                Log.i(TAG, "Updating location")
                when (state) {
                    is LocationRepository.LocationState.Available -> {
                        _locationState.value = state.location.let {
                            LatLng(it.latitude, it.longitude)
                        }
                    }
                    is LocationRepository.LocationState.Error -> {
                        Log.i(TAG, state.message)
                    }
                    else -> {}
                }
            }.launchIn(viewModelScope)

        Log.e(TAG, "trying to connect")
        connect()
    }

    fun connect() {
        if (stompClient.isConnected) {
            _connectionStatus.value = true
        }

        compositeDisposable.add(
            stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { lifecycleEvent ->
                    when (lifecycleEvent.type) {
                        LifecycleEvent.Type.OPENED -> {
                            _connectionStatus.value = true
                            addLog("Connected to server")
                        }

                        LifecycleEvent.Type.CLOSED -> {
                            _connectionStatus.value = false
                            addLog("Disconnected")
                        }

                        LifecycleEvent.Type.ERROR -> {
                            _connectionStatus.value = false
                            addLog("Error: ${lifecycleEvent.exception?.message}")
                        }

                        else -> {}
                    }
                }
        )

        subscribeToTopics()
        stompClient.connect()

        register()

        viewModelScope.launch {
            while (true) {
                delay(DELAY_BETWEEN_LOCATION_UPDATES)
                if (shouldSendLocation(location.value)) {
                    Log.i(TAG, "1")
                    sendLocation(location.value.latitude, location.value.longitude)
                    Log.i(TAG, "2")
                }
            }
        }
    }

    private var prevLocation: LatLng? = null

    private fun shouldSendLocation(location: LatLng): Boolean {
        Log.i(TAG, "bebra ${isOnline}")
        if (!isOnline) {
            return false
        }

        if (location.latitude == 0.0 && location.longitude == 0.0) {
            return false
        }

        if (prevLocation != null) {
            // somehow, this is necessary
            val result = FloatArray(1)
            val la1 = prevLocation!!.latitude
            val lo1 = prevLocation!!.longitude
            val la2 = location.latitude
            val lo2 = location.longitude
            Location.distanceBetween(la1, lo1, la2, lo2, result)
            if (result[0] < 5) {
                Log.i(TAG, "${result[0]}")
                return false
            }
        }

        prevLocation = location
        return true
    }

    private fun subscribeToTopics() {
        compositeDisposable.add(
            stompClient.topic("/user/queue/startWalk")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleStartWalk, this::handleError)
        )

        compositeDisposable.add(
            stompClient.topic("/user/queue/endWalk")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleEndWalk, this::handleError)
        )

        compositeDisposable.add(
            stompClient.topic("/user/$email/msg")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleMessage, this::handleError)
        )
    }

    private fun handleStartWalk(message: StompMessage) {
        val groupId = message.payload.toLong()
        _groupId.value = groupId
        addLog("Registered! Group ID: $groupId")
    }

    private fun handleEndWalk(message: StompMessage) {
        val receivedEmail = message.payload
        if (receivedEmail == email) {
            _groupId.value = -1
            addLog("Unregistered successfully")
        }
    }

    private fun handleMessage(message: StompMessage) {
        val text = message.payload
        addLog("Received message: $text")

        if (text.contains("lat", ignoreCase = true)) {
            val nameAndLocation = gson.fromJson(text, UsernameAndLocation::class.java)
            handleUpdateLocation(nameAndLocation.from, nameAndLocation.lat, nameAndLocation.lng)
        } else {
            val invite = gson.fromJson(text, Invite::class.java)
            receiveInvite(invite.fromWho)
        }
    }

    private fun handleUpdateLocation(fromWho: String, lat: Double, lng: Double) {
        val friend = userViewModel.friends.value.find { friend ->
            friend.email == fromWho
        }
        if (friend != null) {
            val newLocation = LatLng(lat, lng)
            val currentPaths = _groupPaths.value.toMutableMap()

            val currentFriendPath = currentPaths.getOrDefault(friend, emptyList()) + newLocation
            currentPaths[friend] = currentFriendPath
            _groupPaths.value = currentPaths

            Log.i("TAGA ", "updateFriendLocation: ${_groupPaths.value}")
        }
    }

    fun receiveInvite(fromWho: String) {
        _receivedInvites.value += fromWho
        Log.e("TAG", "bebra: ${receivedInvites.value.size}")
    }

    fun rejectInvite(fromWho: String) {
        _receivedInvites.value -= fromWho
    }

    private fun disconnect() {
        compositeDisposable.clear()
        //stompClient.disconnect()
    }

    fun register() {
        stompClient.send("/app/start", "\"$email\"")
            .subscribe()
        addLog("Registration sent: $email")
    }

    fun unregister() {
        stompClient.send("/app/stop", "\"$email\"")
            .subscribe()
        addLog("Unregistration sent: $email")
    }

    fun sendInvite(toUser: String) {
        val invite = Invite(email, toUser)
        stompClient.send("/app/invite", gson.toJson(invite))
            .subscribe()
        addLog("Invite sent to: $toUser")
    }

    fun joinGroup(inviter: String) {
        _receivedInvites.value -= inviter
        val invite = Invite(inviter, email) // inviter -> fromWho, current user -> toWho
        stompClient.send("/app/joinGroup", gson.toJson(invite))
            .subscribe()
        addLog("Joining group of: $inviter")
    }

    fun quitGroup() {
        stompClient.send("/app/quitGroup", "\"$email\"")
            .subscribe()
        addLog("Quit group request sent")
    }

    fun sendLocation(lat: Double, lng: Double) {
        val payload = gson.toJson(UsernameAndLocation(email, lat, lng))
        stompClient.send("/app/updateLocation", payload)
            .subscribe()
        addLog("Location updated: ($lat, $lng)")
    }

    private fun addLog(message: String) {
        viewModelScope.launch {
            Log.i(TAG, message)
        }
    }

    private fun handleError(throwable: Throwable) {
        addLog("Error: ${throwable.message}")
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

    data class Invite(val fromWho: String, val toWho: String)
    data class UsernameAndLocation(val from: String, val lat: Double, val lng: Double)
}

