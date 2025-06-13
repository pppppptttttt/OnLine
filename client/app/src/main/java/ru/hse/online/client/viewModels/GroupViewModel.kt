package ru.hse.online.client.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.hse.online.client.repository.storage.AppDataStore
import ua.naiksoftware.stomp.dto.LifecycleEvent

class GroupViewModel(
        private val dataStore: AppDataStore,
        private val stompClient: StompClient
    ) : ViewModel() {

    companion object {
        private val gson = Gson()
        private const val TAG = "APP_GROUP_VIEWMODEL";
    }

    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus.asStateFlow()

    private val _email = dataStore.getValueFlow(
        AppDataStore.USER_EMAIL,
        defaultValue = ""
    ) // TODO

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _groupId = MutableStateFlow(-1L)
    val groupId: StateFlow<Long> = _groupId.asStateFlow()

    private val _logs = MutableStateFlow("")
    val logs: StateFlow<String> = _logs.asStateFlow()

    private val compositeDisposable = CompositeDisposable()

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
            stompClient.topic("/user/Anton/msg")
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
        val username = message.payload
        if (username == _username.value) {
            _groupId.value = -1
            addLog("Unregistered successfully")
        }
    }

    // TODO: actually handle message
    private fun handleMessage(message: StompMessage) {
        addLog("Received message: ${message.payload}")
    }

    private fun disconnect() {
        compositeDisposable.clear()
        //stompClient.disconnect()
    }

    fun register(username: String) {
        _username.value = username
        stompClient.send("/app/start", "\"$username\"")
            .subscribe()
        addLog("Registration sent: $username")
    }

    fun unregister() {
        val username = _username.value
        stompClient.send("/app/stop", "\"$username\"")
            .subscribe()
        addLog("Unregistration sent: $username")
    }

    fun sendInvite(toUser: String) {
        val invite = Invite(_username.value, toUser)
        stompClient.send("/app/invite", gson.toJson(invite))
            .subscribe()
        addLog("Invite sent to: $toUser")
    }

    fun joinGroup(inviter: String) {
        val invite = Invite(inviter, _username.value) // inviter -> fromWho, current user -> toWho
        stompClient.send("/app/joinGroup", gson.toJson(invite))
            .subscribe()
        addLog("Joining group of: $inviter")
    }

    fun quitGroup() {
        val username = _username.value
        stompClient.send("/app/quitGroup", "\"$username\"")
            .subscribe()
        addLog("Quit group request sent")
    }

    fun sendLocation(lat: Double, lng: Double) {
        val location = Location(lat, lng)
        val payload = gson.toJson(FromUsernameAndLocation(_username.value, location))
        stompClient.send("/app/updateLocation", payload)
            .subscribe()
        addLog("Location updated: ($lat, $lng)")
    }

    private fun addLog(message: String) {
        viewModelScope.launch {
            _logs.value += "$message\n"
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
    data class Location(val lat: Double, val lng: Double)
    data class FromUsernameAndLocation(val from: String, val location: Location)
}

