package raf.rs.rma_projekat.catbreed.api

import raf.rs.rma_projekat.catbreed.api.model.CatBreedApiModel
import raf.rs.rma_projekat.catbreed.api.model.CatBreedImage
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CatBreedApi {

    @GET("breeds")
    suspend fun fetchAllCatBreeds(): List<CatBreedApiModel>

    @GET("breeds/{id}")
    suspend fun getBreed(@Path("id") breedId: String): CatBreedApiModel

    @GET("images/{image_id}")
    suspend fun fetchCatBreedImage(@Path("image_id") imageId: String): CatBreedImage
}