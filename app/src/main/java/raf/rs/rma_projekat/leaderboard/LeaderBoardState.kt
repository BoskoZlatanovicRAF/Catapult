package raf.rs.rma_projekat.leaderboard

import raf.rs.rma_projekat.leaderboard.model.LeaderBoardEntry

data class LeaderBoardState(
    val leaderBoard: List<LeaderBoardEntry> = emptyList(),
    val error: String? = null
)

