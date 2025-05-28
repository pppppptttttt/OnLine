package ru.hse.online.client.repository.storage

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.hse.online.client.repository.networking.api_data.Friend

class UserRepository {
    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends.asStateFlow()

    suspend fun loadFriends() {}
    suspend fun addFriend(uuid: String) {}
    suspend fun deleteFriend(uuid: String) {}
}