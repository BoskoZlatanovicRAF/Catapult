package raf.rs.rma_projekat.catbreed.api.model

import kotlinx.serialization.Serializable

@Serializable
data class CatBreedImage (
    val id : String,
    val url: String
)
