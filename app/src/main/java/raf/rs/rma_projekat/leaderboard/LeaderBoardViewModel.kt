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
import raf.rs.rma_projekat.leaderboard.model.LeaderBoardUiModel
import raf.rs.rma_projekat.leaderboard.model.PublishedLeaderBoardUiModel
import raf.rs.rma_projekat.leaderboard.model.UnpublishedLeaderBoardUiModel
import raf.rs.rma_projekat.leaderboard.repository.LeaderBoardRepository
import raf.rs.rma_projekat.user.profile.ProfileDataStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

    private fun setState(reducer: LeaderBoardState.() -> LeaderBoardState) {
        _state.update(reducer)
    }

    init {
        handleEvents()
        fetchLeaderBoard()
    }

    private fun handleEvents() {
        viewModelScope.launch {
            _event.collect { event ->
                when (event) {
                    is LeaderBoardUiEvent.PostResult -> postResult(event.result)
                    LeaderBoardUiEvent.FetchLeaderBoard -> fetchLeaderBoard()
                    LeaderBoardUiEvent.GetPublishedResults -> getPublishedResults()
                    LeaderBoardUiEvent.GetUnpublishedResults -> getUnpublishedResults()
                }
            }
        }
    }

    private fun fetchLeaderBoard() {
        viewModelScope.launch {
            try {
                val leaderBoardEntries = repository.fetchLeaderBoard()
                val quizzesPlayedCount = leaderBoardEntries.groupingBy { it.nickname }.eachCount()
                val leaderBoard = leaderBoardEntries.mapIndexed { index, entry ->
                    LeaderBoardUiModel(
                        globalRank = index + 1,
                        nickname = entry.nickname,
                        score = entry.result,
                        date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(entry.createdAt)),
                        quizzesPlayed = quizzesPlayedCount[entry.nickname] ?: 0
                    )
                }
                setState { copy(leaderBoard = leaderBoard, publishedResults = emptyList(), unpublishedResults = emptyList()) }
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
                savePublishedResult(result)
            } catch (e: Exception) {
                setState { copy(error = e.message) }
            }
        }
    }

    private fun savePublishedResult(result: Float) {
        viewModelScope.launch {
            try {
                val profileData = profileDataStore.data.first()
                repository.savePublishedResult(profileData.nickname, result)
                fetchLeaderBoard()
            } catch (e: Exception) {
                setState { copy(error = e.message) }
            }
        }
    }

    fun saveUnpublishedResult(result: Float) {
        viewModelScope.launch {
            try {
                val profileData = profileDataStore.data.first()
                repository.saveUnpublishedResult(profileData.nickname, result)
                fetchLeaderBoard()
            } catch (e: Exception) {
                setState { copy(error = e.message) }
            }
        }
    }



    private fun getUnpublishedResults() {
        viewModelScope.launch {
            try {
                val results = repository.getUnpublishedResults().map { entry ->
                    UnpublishedLeaderBoardUiModel(
                        nickname = entry.nickname,
                        score = entry.result,
                        date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(entry.createdAt))
                    )
                }
                setState { copy(unpublishedResults = results, publishedResults = emptyList(), leaderBoard = emptyList()) }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
            }
        }
    }

    private fun getPublishedResults() {
        viewModelScope.launch {
            try {
                val results = repository.getPublishedResults().map { entry ->
                    PublishedLeaderBoardUiModel(
                        nickname = entry.nickname,
                        score = entry.result,
                        date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(entry.createdAt))
                    )
                }
                setState { copy(publishedResults = results, unpublishedResults = emptyList(), leaderBoard = emptyList()) }
            } catch (e: Exception) {
                setState { copy(error = e.message) }
            }
        }
    }
}





