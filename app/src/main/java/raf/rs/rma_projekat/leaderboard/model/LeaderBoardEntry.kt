package raf.rs.rma_projekat.leaderboard.model

import kotlinx.serialization.Serializable

@Serializable
data class LeaderBoardEntry(
    val category: Int,
    val nickname: String,
    val result: Float,
    val createdAt: Long
)
