package ru.hse.online.client.repository.storage

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import ru.hse.online.client.presentation.map.toGoogleMapsFormat
import ru.hse.online.client.repository.FriendshipRepository
import ru.hse.online.client.repository.StatisticsRepository
import ru.hse.online.client.repository.networking.api_data.Friend
import ru.hse.online.client.repository.networking.api_data.FriendshipResult
import ru.hse.online.client.repository.networking.api_data.PathRequest
import ru.hse.online.client.repository.networking.api_data.PathResponse
import ru.hse.online.client.repository.networking.api_data.PathResult
import ru.hse.online.client.repository.networking.api_data.userToFriendMap
import ru.hse.online.client.viewModels.LeaderBoardViewModel
import ru.hse.online.client.viewModels.StatsViewModel
import java.time.LocalDate
import java.util.UUID

class UserRepository(
    private val appDataStore: AppDataStore,
    private val pathRepository: PathRepository,
    private val friendshipRepository: FriendshipRepository,
    private val statisticsRepository: StatisticsRepository,
    private val statsViewModel: StatsViewModel
) {
    private val _friends = MutableStateFlow<List<Friend>>(listOf())
    val friends: StateFlow<List<Friend>> = _friends.asStateFlow()

    private val _paths = MutableStateFlow<List<PathRequest>>(emptyList())
    val paths: StateFlow<List<PathRequest>> = _paths.asStateFlow()

    private val _friendPublicPaths = MutableStateFlow<List<PathResponse>>(emptyList())
    val friendPublicPaths: StateFlow<List<PathResponse>> = _friendPublicPaths.asStateFlow()

    private val _friendProfile = MutableStateFlow<Friend?>(null)
    val friendProfile: StateFlow<Friend?> = _friendProfile.asStateFlow()

    private val _group = MutableStateFlow<Map<UUID, Friend>>(emptyMap())
    val group: StateFlow<Map<UUID, Friend>> = _group.asStateFlow()

    suspend fun loadFriends() {
        when (val result = friendshipRepository.getFriends()) {
            is FriendshipResult.Failure -> {}
            is FriendshipResult.SuccessAddFriend -> {}
            is FriendshipResult.SuccessGetFriends -> {
                result.friends.forEach {
                    _friends.value += userToFriendMap(it)!!
                }
            }
            is FriendshipResult.SuccessRemoveFriend -> {}
        }
    }

    suspend fun addFriend(email: String) {
        val result = friendshipRepository.addFriend(
            email
        );
        if (result.second != null) {
            _friends.value += (result.second!!)
        }
    }

    suspend fun deleteFriend(uuid: UUID) {
        _friends.value = _friends.value.dropWhile { it.userId == uuid }
        friendshipRepository.removeFriend(
            uuid
        );
    }

    suspend fun savePath(description: String, path: List<LatLng>) {
        val pathValue = PathRequest(
            userId = appDataStore.getUserIdFlow().first(),
            polyline = path.toGoogleMapsFormat(),
            created = LocalDate.now(),
            name = description,
            distance = statsViewModel.onlineDistance.first(),
            duration = statsViewModel.onlineTime.first().toDouble()
        )
        when (pathRepository.createPath(pathValue)) {
            is PathResult.Failure -> {}
            is PathResult.Success -> {
                _paths.value += pathValue
            }
        }
    }

    init {
        val fr = Friend(
            UUID.randomUUID(),
            "lol",
            "kek",
            hashMapOf("steps" to 123.0, "distance" to 1.0, "kcals" to 2.0)
        )
        _friends.value += fr
        _friendProfile.value = fr
        _friendPublicPaths.value += PathResponse(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "}__uHwg_uDslDoneEji_CxmvD?oohD",
            LocalDate.of(1, 1, 1),
            "aboba",
            1.0,
            1.0
        )
    }

    fun createGroup() {
        val fr = Friend(
            UUID.randomUUID(),
            "lol",
            "kek",
            hashMapOf("steps" to 123.0, "distance" to 1.0, "kcals" to 2.0),
            Color.Red
        )
        val fr2 = Friend(
            UUID.randomUUID(),
            "lol",
            "kek",
            hashMapOf("steps" to 123.0, "distance" to 1.0, "kcals" to 2.0),
            Color.Blue
        )
        _group.value += Pair(fr.userId, fr)
        _group.value += Pair(fr2.userId, fr2)
    }

    suspend fun getLeaderboard(timeFrame: LeaderBoardViewModel.TimeFrame): List<LeaderBoardViewModel.LeaderBoardUser> {
        val end = LocalDate.now()
        val start = when (timeFrame) {
            LeaderBoardViewModel.TimeFrame.DAILY -> end.minusDays(1)
            LeaderBoardViewModel.TimeFrame.WEEKLY -> end.minusWeeks(1)
            LeaderBoardViewModel.TimeFrame.MONTHLY -> end.minusMonths(1)
        }

        val res = statisticsRepository.getLeaderBoard(
            appDataStore.getUserIdFlow().first(),
            start,
            end
        )

        return res.map { userStat ->
            LeaderBoardViewModel.LeaderBoardUser(
                id = userStat.user.userId.toString(),
                email = userStat.user.email,
                username = userStat.user.username,
                steps = userStat.steps.toInt()
            )
        }
    }
}