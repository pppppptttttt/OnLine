package ru.hse.online.client.repository.storage

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import ru.hse.online.client.presentation.map.toGoogleMapsFormat
import ru.hse.online.client.repository.FriendshipRepository
import ru.hse.online.client.repository.StatisticsRepository
import ru.hse.online.client.repository.networking.api_data.Friend
import ru.hse.online.client.repository.networking.api_data.FriendshipResult
import ru.hse.online.client.repository.networking.api_data.PathRequest
import ru.hse.online.client.repository.networking.api_data.PathResponse
import ru.hse.online.client.repository.networking.api_data.PathResult
import ru.hse.online.client.repository.networking.api_data.userToFriendMap
import ru.hse.online.client.services.StepCounterService
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

    private val _paths = MutableStateFlow<List<PathResponse>>(emptyList())
    val paths: StateFlow<List<PathResponse>> = _paths.asStateFlow()

    private val _friendPublicPaths = MutableStateFlow<List<PathResponse>>(emptyList())
    val friendPublicPaths: StateFlow<List<PathResponse>> = _friendPublicPaths.asStateFlow()

    private val _friendProfile = MutableStateFlow<Friend?>(null)
    val friendProfile: StateFlow<Friend?> = _friendProfile.asStateFlow()

    private val _lifetimeSteps = MutableStateFlow<Int>(0)
    val lifetimeSteps: StateFlow<Int> = _lifetimeSteps.asStateFlow()

    private val _lifetimeCalories = MutableStateFlow<Double>(0.0)
    val lifetimeCalories: StateFlow<Double> = _lifetimeCalories.asStateFlow()

    private val _lifetimeDistance = MutableStateFlow<Double>(0.0)
    val lifetimeDistance: StateFlow<Double> = _lifetimeDistance.asStateFlow()

    suspend fun loadFriendProfile(userId: String) {
        _friends.value.forEach {
            if (it.userId.toString() == userId) {
                _friendProfile.value = it
                when (val result = pathRepository.getPaths(it.userId)) {
                    is PathResult.Success -> {
                         _friendPublicPaths.value = result.paths!!
                    }
                    is PathResult.Failure -> {}
                }
            }
        }
    }

    suspend fun loadLifeTimeStats() {
        val result = statisticsRepository.getLifeTime()
        _lifetimeSteps.value = result[StepCounterService.Stats.STEPS]!!.toInt()
        _lifetimeDistance.value = result[StepCounterService.Stats.DISTANCE]!!
        _lifetimeCalories.value = result[StepCounterService.Stats.KCALS]!!
    }

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

    suspend fun loadPaths() {
        when (val result = pathRepository.getPaths()) {
            is PathResult.Success -> {
                _paths.value = result.paths!!
            }
            is PathResult.Failure -> {}
        }
    }

    suspend fun addFriend(email: String) {
        if (_friends.value.any { it.email == email }) {
            return
        }
        val result = friendshipRepository.addFriend(
            email
        )
        if (result.second != null) {
            _friends.value += (result.second!!)
        }
    }

    suspend fun deleteFriend(uuid: UUID) {
        _friends.value = _friends.value.dropWhile { it.userId == uuid }
        friendshipRepository.removeFriend(
            uuid
        )
    }

    suspend fun savePath(path: PathResponse) {
        val pathValue = PathRequest(
            userId = appDataStore.getUserIdFlow().first(),
            polyline = path.polyline,
            created = path.created,
            name = path.name,
            distance = path.distance,
            duration = path.duration
        )
        when (pathRepository.createPath(pathValue)) {
            is PathResult.Failure -> {}
            is PathResult.Success -> {
                _paths.value += path
            }            
        }
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
        val pathResponse = PathResponse(
            userId = appDataStore.getUserIdFlow().first(),
            pathId = UUID(0,0),
            polyline = path.toGoogleMapsFormat(),
            created = LocalDate.now(),
            name = description,
            distance = statsViewModel.onlineDistance.first(),
            duration = statsViewModel.onlineTime.first().toDouble()
        )
        when (val res = pathRepository.createPath(pathValue)) {
            is PathResult.Failure -> {
            }
            is PathResult.Success -> {
                _paths.value += pathResponse
            }
        }
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
                id = userStat.first.userId.toString(),
                email = userStat.first.email,
                username = userStat.first.username,
                steps = userStat.second.toInt()
            )
        }
    }
}