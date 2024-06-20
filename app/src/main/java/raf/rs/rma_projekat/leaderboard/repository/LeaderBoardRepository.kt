package raf.rs.rma_projekat.leaderboard.repository

import raf.rs.rma_projekat.db.AppDatabase
import raf.rs.rma_projekat.leaderboard.api.LeaderBoardApi
import raf.rs.rma_projekat.leaderboard.db.entity.LeaderBoardEntity
import raf.rs.rma_projekat.leaderboard.model.LeaderBoardEntry
import raf.rs.rma_projekat.leaderboard.model.LeaderBoardPostBody
import javax.inject.Inject

class LeaderBoardRepository @Inject constructor(
    private val api: LeaderBoardApi,
    private val database: AppDatabase
) {

    suspend fun fetchLeaderBoard(): List<LeaderBoardEntry> {
        return api.getLeaderBoard()
    }

    suspend fun postResult(nickname: String, result: Float, category: Int) {
        val postBody = LeaderBoardPostBody(category, nickname, result)
        api.postResult(postBody)
    }

    suspend fun savePublishedResult(nickname: String, result: Float) {
        val entry = LeaderBoardEntity(0, nickname, result, System.currentTimeMillis(), true)
        database.leaderBoardDao().insert(entry)
    }

    suspend fun saveUnpublishedResult(nickname: String, result: Float) {
        val entry = LeaderBoardEntity(0, nickname, result, System.currentTimeMillis(), false)
        database.leaderBoardDao().insert(entry)
    }

    suspend fun getPublishedResults(): List<LeaderBoardEntity> {
        return database.leaderBoardDao().getPublishedResults()
    }

    suspend fun getUnpublishedResults(): List<LeaderBoardEntity> {
        return database.leaderBoardDao().getUnpublishedResults()
    }

}


