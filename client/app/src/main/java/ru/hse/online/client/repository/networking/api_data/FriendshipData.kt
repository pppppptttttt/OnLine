package ru.hse.online.client.repository.networking.api_data

sealed class FriendshipResult {
    data class SuccessGetFriends(val friends: List<User>, val code: Int) : FriendshipResult()
    data class SuccessAddFriend(val code: Int) : FriendshipResult()
    data class SuccessRemoveFriend(val code: Int) : FriendshipResult()
    data class Failure(val code: Int? = null, val message: String? = null) : FriendshipResult()
} 