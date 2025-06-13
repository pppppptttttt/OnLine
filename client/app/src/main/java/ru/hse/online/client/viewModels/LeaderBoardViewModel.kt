package ru.hse.online.client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.hse.online.client.repository.storage.UserRepository

class LeaderBoardViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    enum class TimeFrame { DAILY, WEEKLY, MONTHLY }

    private val _selectedTimeFrame = MutableStateFlow(TimeFrame.DAILY)
    val selectedTimeFrame: StateFlow<TimeFrame> = _selectedTimeFrame.asStateFlow()

    private val _leaderboardState = MutableStateFlow<LeaderBoardState>(LeaderBoardState.Loading)
    val leaderboardState: StateFlow<LeaderBoardState> = _leaderboardState.asStateFlow()

    private val _currentUser = MutableStateFlow<LeaderBoardUser?>(null)
    val currentUser: StateFlow<LeaderBoardUser?> = _currentUser.asStateFlow()

    sealed class LeaderBoardState {
        data object Loading : LeaderBoardState()
        data class Success(val users: List<LeaderBoardUser>) : LeaderBoardState()
        data class Error(val message: String) : LeaderBoardState()
    }

    data class LeaderBoardUser(
        val id: String,
        val email: String,
        val username: String,
        val steps: Int
    )

    fun loadLeaderboard(timeFrame: TimeFrame) {
        _selectedTimeFrame.value = timeFrame
        _leaderboardState.value = LeaderBoardState.Loading

        viewModelScope.launch {
            try {
                val leaderboard = userRepository.getLeaderboard(timeFrame)
                _leaderboardState.value = LeaderBoardState.Success(leaderboard)
            } catch (e: Exception) {
                _leaderboardState.value = LeaderBoardState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

