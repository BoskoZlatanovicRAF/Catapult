package raf.rs.rma_projekat.leaderboard.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LeaderBoardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nickname: String,
    val result: Float,
    val createdAt: Long,
    val isPublished: Boolean = false // Add a flag to distinguish published and unpublished results
)