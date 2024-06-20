package raf.rs.rma_projekat.catbreed.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import raf.rs.rma_projekat.catbreed.db.entity.CatBreedEntity
import raf.rs.rma_projekat.catbreed.db.entity.CatBreedImageEntity

@Dao
interface CatBreedDao {

    // CatBreedEntity
    @Query("SELECT * FROM CatBreedEntity")
    fun getAllCatBreeds(): List<CatBreedEntity>

    @Upsert
    fun upsertAllCatBreeds(data: List<CatBreedEntity>)

    @Query("SELECT * FROM CatBreedEntity")
    fun observeCatBreeds(): Flow<List<CatBreedEntity>>

    @Query("SELECT * FROM CatBreedEntity WHERE id = :breedId")
    suspend fun getBreed(breedId: String): CatBreedEntity

    @Query("SELECT id FROM CatBreedEntity")
    fun getAllBreedIds(): List<String>


    // CatBreedImageEntity

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

    @Query("SELECT DISTINCT breedId FROM CatBreedImageEntity")
    fun getAllExistingIds(): List<String>




}