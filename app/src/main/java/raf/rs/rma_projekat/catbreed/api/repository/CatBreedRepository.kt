package raf.rs.rma_projekat.catbreed.api.repository

import android.util.Log
import raf.rs.rma_projekat.catbreed.api.CatBreedApi
import raf.rs.rma_projekat.catbreed.api.model.CatBreedApiModel
import raf.rs.rma_projekat.catbreed.api.model.CatBreedImage
import raf.rs.rma_projekat.networking.retrofit


object CatBreedRepository {
    private val catBreedApi: CatBreedApi = retrofit.create(CatBreedApi::class.java)

    suspend fun getAllCatBreeds(): List<CatBreedApiModel> {
        val catBreeds = catBreedApi.fetchAllCatBreeds()
        Log.d("CatBreedRepository", "getAllCatBreeds: $catBreeds")

        return catBreeds
    }

    suspend fun getBreedDetails(query: String): CatBreedApiModel {
        return catBreedApi.getBreed(query)
    }

    suspend fun getBreedImage(imageId: String) : CatBreedImage {
        return catBreedApi.fetchCatBreedImage(imageId)
    }
}