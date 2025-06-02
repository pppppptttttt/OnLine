package ru.hse.online.client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
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
    val isInGroup: StateFlow<Boolean> = repository.isInGroup
    val friendPublicPaths: StateFlow<List<PathResponse>> = repository.friendPublicPaths
    val friendProfile: StateFlow<Friend?> = repository.friendProfile
    val group: StateFlow<Map<UUID, Friend>> = repository.group

    init {
        viewModelScope.launch {
            repository.loadFriends()
        }
    }

    fun addFriend(uuid: String) {
        viewModelScope.launch {
            // FIXME
//            repository.addFriend(uuid)
        }
    }

    fun deleteFriend(uuid: String) {
        viewModelScope.launch {
            // FIXME
//            repository.deleteFriend(uuid)
        }
    }

    fun createGroup() {
        repository.createGroup()
    }

    fun joinGroup(uuid: String) {}
    fun inviteToGroup(uuid: UUID) {}
    fun loadFriendProfile(userId: String) {}
    fun loadPublicPaths(userId: UUID) {}
    fun addPathToCollection(path: PathResponse) {}
    fun previewPath(path: PathResponse) {
        locationRepository.loadPreviewPath(path.polyline)
    }

}