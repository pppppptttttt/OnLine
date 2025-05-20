package ru.hse.online.client.repository.storage

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.hse.online.client.repository.networking.api_data.Friend
import ru.hse.online.client.repository.networking.api_data.Path
import java.util.Date
import java.util.UUID

class UserRepository {
    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends.asStateFlow()

    private val _isInGroup = MutableStateFlow(false)
    val isInGroup: StateFlow<Boolean> = _isInGroup.asStateFlow()

    private val _groupId = MutableStateFlow<UUID?>(null)
    val groupId: StateFlow<UUID?> = _groupId.asStateFlow()

    private val _friendPublicPaths = MutableStateFlow<List<Path>>(emptyList())
    val friendPublicPaths: StateFlow<List<Path>> = _friendPublicPaths.asStateFlow()

    private val _friendProfile = MutableStateFlow<Friend?>(null)
    val friendProfile: StateFlow<Friend?> = _friendProfile.asStateFlow()

    suspend fun loadFriends() {}
    suspend fun addFriend(uuid: String) {}
    suspend fun deleteFriend(uuid: String) {}

    init {
        val fr = Friend(UUID.randomUUID(), "lol", "kek", hashMapOf("steps" to 123.0, "distance" to 1.0, "kcals" to 2.0))
        _friends.value += fr
        _friendProfile.value = fr
        _friendPublicPaths.value += Path(UUID.randomUUID(), UUID.randomUUID(), "}__uHwg_uDslDoneEji_CxmvD?oohD", Date(1,1,1), "aboba", 1.0,1.0)
    }

    fun createGroup() {
        _isInGroup.value = true
    }
}