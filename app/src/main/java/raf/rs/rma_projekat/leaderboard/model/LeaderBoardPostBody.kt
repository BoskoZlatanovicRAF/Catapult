package raf.rs.rma_projekat.leaderboard.model

import kotlinx.serialization.Serializable

@Serializable
data class LeaderBoardPostBody(
    val category: Int,
    val nickname: String,
    val result: Float
)
