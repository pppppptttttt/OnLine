package ru.hse.online.client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.hse.online.client.repository.networking.api_data.Friend
import ru.hse.online.client.repository.networking.api_data.PathResponse
import ru.hse.online.client.repository.storage.LocationRepository
import ru.hse.online.client.repository.storage.UserRepository
import java.util.UUID

class UserViewModel(
    private val repository: UserRepository,
    private val locationRepository: LocationRepository
): ViewModel() {
    val friends: StateFlow<List<Friend>> = repository.friends
    val friendPublicPaths: StateFlow<List<PathResponse>> = repository.friendPublicPaths
    val friendProfile: StateFlow<Friend?> = repository.friendProfile
    val userPaths: StateFlow<List<PathResponse>> = repository.paths

    val lifetimeSteps: StateFlow<Int> = repository.lifetimeSteps
    val lifetimeCalories: StateFlow<Double> = repository.lifetimeCalories
    val lifetimeDistance: StateFlow<Double> = repository.lifetimeDistance

    private val _achievements = MutableStateFlow<List<Int>>(emptyList())
    val achievements: StateFlow<List<Int>> = _achievements.asStateFlow()

    init {
        viewModelScope.launch {
            repository.loadFriends()
        }
        viewModelScope.launch {
            repository.loadPaths()
        }
        viewModelScope.launch {
            repository.loadLifeTimeStats()
        }
    }

    fun addFriend(email: String) {
        viewModelScope.launch {
            repository.addFriend(email)
        }
    }

    fun deleteFriend(uuid: String) {
        viewModelScope.launch {
            repository.deleteFriend(UUID.fromString(uuid))
        }
    }

    fun loadFriendProfile(userId: String) {
        viewModelScope.launch {
            repository.loadFriendProfile(userId)
        }
    }

    fun addPathToCollection(path: PathResponse) {
        viewModelScope.launch {
            repository.savePath(path)
        }
    }
    fun previewPath(path: PathResponse) {
        locationRepository.loadPreviewPath(path.polyline)
    }
}
