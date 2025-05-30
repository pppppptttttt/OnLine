package ru.hse.online.client.repository

import kotlinx.coroutines.flow.first
import ru.hse.online.client.repository.networking.api_data.FriendshipResult
import ru.hse.online.client.repository.networking.api_service.FriendshipApiService
import ru.hse.online.client.repository.storage.AppDataStore
import java.util.UUID

class FriendshipRepository(
    private val friendshipApiService: FriendshipApiService,
    private val appDataStore: AppDataStore
) {

    suspend fun getFriends(): FriendshipResult {
        val token: String = appDataStore.getValueFlow(AppDataStore.USER_TOKEN, "").first()
        val userId: UUID = appDataStore.getUserIdFlow().first()
        return try {
            val response = friendshipApiService.getFriends("Bearer $token", userId)
            when (response.code()) {
                200 -> response.body()?.let { FriendshipResult.SuccessGetFriends(it, response.code()) }
                    ?: FriendshipResult.Failure(response.code(), "Response body is null")
                else -> FriendshipResult.Failure(response.code(), response.message())
            }
        } catch (e: Exception) {
            FriendshipResult.Failure(message = e.localizedMessage)
        }
    }

    suspend fun addFriend(friendId: UUID): FriendshipResult {
        val token: String = appDataStore.getValueFlow(AppDataStore.USER_TOKEN, "").first()
        val userId: UUID = appDataStore.getUserIdFlow().first()
        return try {
            val response = friendshipApiService.addFriend("Bearer $token", userId, friendId)
            when (response.code()) {
                200 -> FriendshipResult.SuccessAddFriend(response.code()) // As per controller: 200 OK
                else -> FriendshipResult.Failure(response.code(), response.message())
            }
        } catch (e: Exception) {
            FriendshipResult.Failure(message = e.localizedMessage)
        }
    }

    suspend fun removeFriend(friendId: UUID): FriendshipResult {
        val token: String = appDataStore.getValueFlow(AppDataStore.USER_TOKEN, "").first()
        val userId: UUID = appDataStore.getUserIdFlow().first()
        return try {
            val response = friendshipApiService.removeFriend("Bearer $token", userId, friendId)
            when (response.code()) {
                204 -> FriendshipResult.SuccessRemoveFriend(response.code()) // As per controller: 204 No Content
                else -> FriendshipResult.Failure(response.code(), response.message())
            }
        } catch (e: Exception) {
            FriendshipResult.Failure(message = e.localizedMessage)
        }
    }
} 