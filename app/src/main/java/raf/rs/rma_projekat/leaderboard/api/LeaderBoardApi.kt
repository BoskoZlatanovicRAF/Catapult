package raf.rs.rma_projekat.leaderboard.api

import raf.rs.rma_projekat.leaderboard.model.LeaderBoardEntry
import raf.rs.rma_projekat.leaderboard.model.LeaderBoardPostBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LeaderBoardApi {

    @GET("leaderboard?category=1")
    suspend fun getLeaderBoard(): List<LeaderBoardEntry>

    @POST("leaderboard")
    suspend fun postResult(@Body result: LeaderBoardPostBody)

}


