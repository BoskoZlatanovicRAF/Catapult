package raf.rs.rma_projekat.db

import androidx.room.Database
import androidx.room.RoomDatabase
import raf.rs.rma_projekat.catbreed.db.CatBreedDao
import raf.rs.rma_projekat.catbreed.db.CatBreedEntity
import raf.rs.rma_projekat.catbreed.db.CatBreedImageEntity

@Database(
    entities = [
        CatBreedEntity::class,
        CatBreedImageEntity::class
               ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun catBreedDao(): CatBreedDao
}