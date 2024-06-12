package raf.rs.rma_projekat.catbreed.api.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import raf.rs.rma_projekat.catbreed.api.CatBreedApi
import raf.rs.rma_projekat.catbreed.api.model.CatBreedApiModel
import raf.rs.rma_projekat.catbreed.api.model.CatBreedImage
import raf.rs.rma_projekat.catbreed.db.CatBreedEntity
import raf.rs.rma_projekat.catbreed.db.CatBreedImageEntity
import raf.rs.rma_projekat.catbreed.list.model.CatBreedUiModel
import raf.rs.rma_projekat.catbreed.mappers.asCatBreedDbModel
import raf.rs.rma_projekat.catbreed.mappers.asCatBreedImage
import raf.rs.rma_projekat.catbreed.mappers.asCatBreedImageEntity
import raf.rs.rma_projekat.db.AppDatabase
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

//    suspend fun getBreedImage(imageId: String): CatBreedImage {
//        return catBreedApi.fetchCatBreedImage(imageId)
//    }
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
        Log.d("CatBreedRepository", "usao u fetchBreedImages")
        if (imagesFromDb.isEmpty()) {
            Log.d("CatBreedRepository", "usao u fetchBreedImages, fetching from api")

            val imagesFromApi = catBreedApi.fetchBreedImages(breedId = breedId)
            val imagesToInsert = imagesFromApi.map {
                it.asCatBreedImageEntity(breedId)
            }
            database.catBreedDao().upsertAllImages(imagesToInsert)
            return imagesToInsert
        }
        return imagesFromDb
    }
    fun observeBreedImages(breedId: String) = database.catBreedDao().observeImagesForBreed(breedId)


}