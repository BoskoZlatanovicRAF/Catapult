package raf.rs.rma_projekat.leaderboard

sealed class LeaderBoardUiEvent {
    object FetchLeaderBoard : LeaderBoardUiEvent()
    data class PostResult(val result: Float) : LeaderBoardUiEvent()
    object GetUnpublishedResults : LeaderBoardUiEvent()
    object GetPublishedResults : LeaderBoardUiEvent()
}

