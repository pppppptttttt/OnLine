package ru.hse.online.client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import ru.hse.online.client.repository.networking.WebsocketClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GroupState(
    val username: String? = null,
    val groupId: Long? = null,
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val error: String? = null,
    val invitations: List<String> = emptyList(),
    val locations: Map<String, Location> = emptyMap()
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class LocationFromJson(
    val from: String,
    val lat: Double,
    val lng: Double
)

data class Invite(
    val fromWho: String,
    val toWho: String
)

class GroupViewModel : ViewModel() {
    companion object {
        private val gson = Gson()
    }

    private val wsClient = WebsocketClient()
    private val mutableState = MutableStateFlow(GroupState())
    val state: StateFlow<GroupState> = mutableState.asStateFlow();

    init {
        viewModelScope.launch {
            wsClient.incomingMessages.collect { handleMessage(it) }
        }
    }

    fun connect(username: String) {
        viewModelScope.launch {
            mutableState.update { it.copy(isConnecting = true) }
            try {
                wsClient.connect(username)
                mutableState.update {
                    it.copy(
                        username = username,
                        isConnected = true,
                        isConnecting = false
                    )
                }
            } catch (e: Exception) {
                mutableState.update {
                    it.copy(
                        error = "Connection failed: ${e.message}",
                        isConnecting = false
                    )
                }
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            mutableState.value.username?.let { wsClient.sendMessage("/stop $it") }
            wsClient.disconnect()
            mutableState.update { GroupState() }
        }
    }

    fun sendInvite(targetUser: String) {
        viewModelScope.launch {
            val currentUser = mutableState.value.username ?: return@launch
            wsClient.sendMessage("/invite {\"fromWho\":\"$currentUser\",\"toWho\":\"$targetUser\"}")
        }
    }

    fun acceptInvite(inviter: String) {
        viewModelScope.launch {
            val currentUser = mutableState.value.username ?: return@launch
            wsClient.sendMessage("/joinGroup {\"fromWho\":\"$currentUser\",\"toWho\":\"$inviter\"}")
        }
    }

    fun quitGroup() {
        viewModelScope.launch {
            mutableState.value.username?.let {
                wsClient.sendMessage("/quitGroup $it")
            }
        }
    }

    fun updateLocation(lat: Double, lng: Double) {
        viewModelScope.launch {
            val currentUser = mutableState.value.username ?: return@launch
            wsClient.sendMessage("/updateLocation {\"from\":\"$currentUser\",\"location\":{\"lat\":$lat,\"lng\":$lng}}")
        }
    }

    private fun handleMessage(message: String) {
        when {
            message.startsWith("{\"from\":") -> handleLocationUpdate(message)
            message.startsWith("{\"fromWho\":") -> handleInvite(message)
            else -> handleGroupId(message)
        }
    }
    private fun handleLocationUpdate(json: String) {
        val location = parseLocation(json)
        mutableState.update { state ->
            state.copy(locations = state.locations + (location.from to Location(location.lat, location.lng)))
        }
    }

    private fun handleInvite(json: String) {
        val invite = parseInvite(json)
        mutableState.update { state ->
            state.copy(invitations = state.invitations + invite.fromWho)
        }
    }

    private fun handleGroupId(message: String) {
        val groupId = message.toLongOrNull()
        if (groupId != null) {
            mutableState.update { it.copy(groupId = groupId) }
        }
    }

    private fun parseLocation(json: String): LocationFromJson {
        return gson.fromJson(json, LocationFromJson::class.java)
    }

    private fun parseInvite(json: String): Invite {
        return gson.fromJson(json, Invite::class.java)
    }

}
