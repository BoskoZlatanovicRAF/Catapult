package raf.rs.rma_projekat.catbreed.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CatBreedDao {
    @Query("SELECT * FROM CatBreedEntity")
    fun getAllCatBreeds(): List<CatBreedEntity>

    @Upsert
    fun upsertAllCatBreeds(data: List<CatBreedEntity>)

    @Query("SELECT * FROM CatBreedEntity")
    fun observeCatBreeds(): Flow<List<CatBreedEntity>>

    @Query("SELECT * FROM CatBreedEntity WHERE id = :breedId")
    suspend fun getBreed(breedId: String): CatBreedEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAllImages(images: List<CatBreedImageEntity>)

    @Query("SELECT * FROM CatBreedImageEntity WHERE breedId = :breedId")
    fun getImagesForBreed(breedId: String): List<CatBreedImageEntity>

    @Query("SELECT * FROM CatBreedImageEntity WHERE breedId = :breedId")
    fun observeImagesForBreed(breedId: String): Flow<List<CatBreedImageEntity>>

    @Query("SELECT * FROM CatBreedImageEntity WHERE id = :imageId")
    suspend fun getImage(imageId: String): CatBreedImageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertImage(image: CatBreedImageEntity)

}