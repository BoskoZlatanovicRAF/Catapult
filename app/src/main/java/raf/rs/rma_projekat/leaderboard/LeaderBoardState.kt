package raf.rs.rma_projekat.leaderboard

import raf.rs.rma_projekat.leaderboard.model.LeaderBoardUiModel
import raf.rs.rma_projekat.leaderboard.model.PublishedLeaderBoardUiModel
import raf.rs.rma_projekat.leaderboard.model.UnpublishedLeaderBoardUiModel

data class LeaderBoardState(
    val leaderBoard: List<LeaderBoardUiModel> = emptyList(),
    val publishedResults: List<PublishedLeaderBoardUiModel> = emptyList(),
    val unpublishedResults: List<UnpublishedLeaderBoardUiModel> = emptyList(),
    val error: String? = null
)

