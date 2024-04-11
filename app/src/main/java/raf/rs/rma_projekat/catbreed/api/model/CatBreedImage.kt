package raf.rs.rma_projekat.catbreed.api.model

import kotlinx.serialization.Serializable

@Serializable
data class CatBreedImage (
    val id : String,
    val width: Int,
    val height: Int,
    val url: String
)
