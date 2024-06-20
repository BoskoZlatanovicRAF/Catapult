package raf.rs.rma_projekat.leaderboard.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import raf.rs.rma_projekat.leaderboard.db.entity.LeaderBoardEntity

@Dao
interface LeaderBoardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(leaderBoardEntity: LeaderBoardEntity)

    @Query("SELECT * FROM LeaderBoardEntity WHERE isPublished = 1 ORDER BY result DESC")
    suspend fun getPublishedResults(): List<LeaderBoardEntity>

    @Query("SELECT * FROM LeaderBoardEntity WHERE isPublished = 0 ORDER BY result DESC")
    suspend fun getUnpublishedResults(): List<LeaderBoardEntity>

    @Query("SELECT COUNT(*) FROM LeaderBoardEntity WHERE nickname = :nickname")
    suspend fun getQuizzesPlayedCount(nickname: String): Int
}
