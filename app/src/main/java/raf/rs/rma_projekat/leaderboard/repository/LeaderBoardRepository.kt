package raf.rs.rma_projekat.leaderboard.repository

import raf.rs.rma_projekat.leaderboard.api.LeaderBoardApi
import raf.rs.rma_projekat.leaderboard.model.LeaderBoardEntry
import raf.rs.rma_projekat.leaderboard.model.LeaderBoardPostBody
import javax.inject.Inject

class LeaderBoardRepository @Inject constructor(
    private val api: LeaderBoardApi
) {

    suspend fun fetchLeaderBoard(): List<LeaderBoardEntry> {
        return api.getLeaderBoard()
    }

    suspend fun postResult(nickname: String, result: Float, category: Int) {
        val postBody = LeaderBoardPostBody(category, nickname, result)
        api.postResult(postBody)
    }
}
