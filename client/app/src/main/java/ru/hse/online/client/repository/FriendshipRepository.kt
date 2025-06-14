package ru.hse.online.client.repository

import kotlinx.coroutines.flow.first
import ru.hse.online.client.repository.networking.api_data.Friend
import ru.hse.online.client.repository.networking.api_data.FriendshipResult
import ru.hse.online.client.repository.networking.api_data.UserResult
import ru.hse.online.client.repository.networking.api_data.userToFriendMap
import ru.hse.online.client.repository.networking.api_service.FriendshipApiService
import ru.hse.online.client.repository.storage.AppDataStore
import ru.hse.online.client.usecase.GetUserUseCase
import java.util.UUID

class FriendshipRepository(
    private val friendshipApiService: FriendshipApiService,
    private val appDataStore: AppDataStore,
    private val user_getter: GetUserUseCase
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

    suspend fun addFriend(friendMail: String): Pair<FriendshipResult, Friend?> {
        val token: String = appDataStore.getValueFlow(AppDataStore.USER_TOKEN, "").first()
        val userId: UUID = appDataStore.getUserIdFlow().first()
        return try {
            when (val friendResponse = user_getter.execute(token, friendMail, null)) {
                is UserResult.Success -> {
                    val response = friendshipApiService.addFriend("Bearer $token", userId, friendResponse.user?.userId)
                    when (response.code()) {
                        200 -> {
                            Pair(FriendshipResult.SuccessAddFriend(response.code()),
                                userToFriendMap(friendResponse.user)
                            )
                        }
                        else -> Pair(FriendshipResult.Failure(response.code(), response.message()), null)
                    }
                }
                is UserResult.Failure -> {
                    Pair(FriendshipResult.Failure(friendResponse.code, friendResponse.message), null)
                }
            }
        } catch (e: Exception) {
            Pair(FriendshipResult.Failure(message = e.localizedMessage), null)
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