package raf.rs.rma_projekat.leaderboard.model

import java.util.Date

data class LeaderBoardUiModel(
    val globalRank: Int,
    val nickname: String,
    val score: Float,
    val date: String,
    val quizzesPlayed: Int
)

data class PublishedLeaderBoardUiModel(
    val nickname: String,
    val score: Float,
    val date: String
)

data class UnpublishedLeaderBoardUiModel(
    val nickname: String,
    val score: Float,
    val date: String
)
