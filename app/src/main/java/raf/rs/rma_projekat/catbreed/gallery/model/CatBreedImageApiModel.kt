package raf.rs.rma_projekat.catbreed.gallery.model

import kotlinx.serialization.Serializable
import raf.rs.rma_projekat.catbreed.api.model.CatBreedApiModel

@Serializable
data class CatBreedImageApiModel (
    val id: String,
    val url: String,
    val breeds: List<CatBreedApiModel>
)