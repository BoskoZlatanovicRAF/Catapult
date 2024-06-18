package raf.rs.rma_projekat.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import raf.rs.rma_projekat.leaderboard.repository.LeaderBoardRepository
import raf.rs.rma_projekat.user.profile.ProfileDataStore
import javax.inject.Inject

@HiltViewModel
class LeaderBoardViewModel @Inject constructor(
    private val repository: LeaderBoardRepository,
    private val profileDataStore: ProfileDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(LeaderBoardState())
    val state: StateFlow<LeaderBoardState> = _state

    private val _event = MutableSharedFlow<LeaderBoardUiEvent>()
    fun setEvent(event: LeaderBoardUiEvent) = viewModelScope.launch { _event.emit(event) }

    init {
        handleEvents()
        fetchLeaderBoard()
    }

    private fun handleEvents() {
        viewModelScope.launch {
            _event.collect { event ->
                when (event) {
                    is LeaderBoardUiEvent.FetchLeaderBoard -> fetchLeaderBoard()
                    is LeaderBoardUiEvent.PostResult -> postResult(event.result)
                }
            }
        }
    }

    private fun fetchLeaderBoard() {
        viewModelScope.launch {
            try {
                val leaderBoard = repository.fetchLeaderBoard()
                setState { copy(leaderBoard = leaderBoard) }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
            }
        }
    }

    private fun postResult(result: Float) {
        viewModelScope.launch {
            try {
                val profileData = profileDataStore.data.first()
                repository.postResult(profileData.nickname, result, 1)
                fetchLeaderBoard()
            } catch (e: Exception) {
                setState { copy(error = e.message) }
            }
        }
    }

    private fun setState(reducer: LeaderBoardState.() -> LeaderBoardState) {
        _state.update(reducer)
    }
}

