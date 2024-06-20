package raf.rs.rma_projekat.catbreed.api.repository

import android.util.Log
import raf.rs.rma_projekat.catbreed.api.CatBreedApi
import raf.rs.rma_projekat.catbreed.db.entity.CatBreedEntity
import raf.rs.rma_projekat.catbreed.db.entity.CatBreedImageEntity
import raf.rs.rma_projekat.catbreed.mappers.asCatBreedDbModel
import raf.rs.rma_projekat.catbreed.mappers.asCatBreedImageEntity
import raf.rs.rma_projekat.db.AppDatabase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


class CatBreedRepository @Inject constructor(
    private val catBreedApi: CatBreedApi,
    private val database : AppDatabase
) {
    suspend fun fetchAllCatBreeds() {
        val catBreedsFromDb = database.catBreedDao().getAllCatBreeds()
        if (catBreedsFromDb.isEmpty()) {
            Log.d("CatBreedRepository", "fetching from api")
            val catBreeds = catBreedApi.fetchAllCatBreeds()
                .map { it.asCatBreedDbModel() }
                .toMutableList()

            database.catBreedDao().upsertAllCatBreeds(catBreeds)
        }
    }

    suspend fun getBreedDetails(id: String): CatBreedEntity {
        return database.catBreedDao().getBreed(id)
    }


    fun getAllCatBreeds(): List<CatBreedEntity> {
        return database.catBreedDao().getAllCatBreeds()
    }


    suspend fun getBreedImage(imageId: String): CatBreedImageEntity {

        val imageFromDb = database.catBreedDao().getImage(imageId)

        if(imageFromDb == null) {
            val imageFromApi = catBreedApi.fetchCatBreedImage(imageId)
            database.catBreedDao().upsertImage(imageFromApi.asCatBreedImageEntity())

            return imageFromApi.asCatBreedImageEntity()
        }

        return imageFromDb

    }


    fun observeCatBreeds() = database.catBreedDao().observeCatBreeds()



    suspend fun fetchBreedImages(breedId: String): List<CatBreedImageEntity> {
        val imagesFromDb = database.catBreedDao().getImagesForBreed(breedId)
        if (imagesFromDb.isEmpty()) {

            val imagesFromApi = catBreedApi.fetchBreedImages(breedId = breedId)
            val imagesToInsert = imagesFromApi.map {
                it.asCatBreedImageEntity(breedId)
            }
            database.catBreedDao().upsertAllImages(imagesToInsert)
            return imagesToInsert
        }
        return imagesFromDb
    }



    fun getBreedImages(breedId: String): List<CatBreedImageEntity> {
        return database.catBreedDao().getImagesForBreed(breedId)
    }



    fun observeBreedImages(breedId: String) = database.catBreedDao().observeImagesForBreed(breedId)



    suspend fun updateMissingImages() = coroutineScope {
        // Fetch all breed IDs from the CatBreedEntity table
        val breedIds = database.catBreedDao().getAllBreedIds()

        // Fetch all breed IDs that already have images in the CatBreedImageEntity table
        val breedIdsWithImages = database.catBreedDao().getAllExistingIds()

        // Determine the breed IDs that do not have images
        val breedIdsWithoutImages = breedIds.filterNot { it in breedIdsWithImages }

        // Fetch images in batches
        breedIdsWithoutImages.chunked(10).forEach { batch ->
            val deferreds = batch.map { breedId ->
                async {
                    val imagesFromApi = catBreedApi.fetchBreedImages(breedId = breedId)
                    imagesFromApi.map { apiImage -> CatBreedImageEntity(apiImage.id, breedId, apiImage.url) }
                }
            }
            val results = deferreds.awaitAll()
            results.flatten().let { newImages ->
                database.catBreedDao().upsertAllImages(newImages)
            }
        }
    }




}