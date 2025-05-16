package ru.hse.online.client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.hse.online.client.repository.networking.api_data.Friend
import ru.hse.online.client.repository.storage.UserRepository

class UserViewModel(
    private val repository: UserRepository
): ViewModel() {
    val friends: StateFlow<List<Friend>> = repository.friends

    init {
        viewModelScope.launch {
            repository.loadFriends()
        }
    }

    fun addFriend(uuid: String) {
        viewModelScope.launch {
            repository.addFriend(uuid)
        }
    }

    fun deleteFriend(uuid: String) {
        viewModelScope.launch {
            repository.deleteFriend(uuid)
        }
    }

}