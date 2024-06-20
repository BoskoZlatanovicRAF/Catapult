package raf.rs.rma_projekat.db

import androidx.room.Database
import androidx.room.RoomDatabase
import raf.rs.rma_projekat.catbreed.db.dao.CatBreedDao
import raf.rs.rma_projekat.catbreed.db.entity.CatBreedEntity
import raf.rs.rma_projekat.catbreed.db.entity.CatBreedImageEntity
import raf.rs.rma_projekat.leaderboard.db.dao.LeaderBoardDao
import raf.rs.rma_projekat.leaderboard.db.entity.LeaderBoardEntity

@Database(
    entities = [
        CatBreedEntity::class,
        CatBreedImageEntity::class,
        LeaderBoardEntity::class
               ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun catBreedDao(): CatBreedDao

    abstract fun leaderBoardDao(): LeaderBoardDao
}