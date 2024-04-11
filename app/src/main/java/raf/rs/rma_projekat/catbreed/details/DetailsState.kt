package raf.rs.rma_projekat.catbreed.details

import raf.rs.rma_projekat.catbreed.api.model.CatBreedImage
import raf.rs.rma_projekat.catbreed.details.model.DetailsUiModel

data class DetailsState(
    val loading: Boolean = false,
    val breedsDetail: DetailsUiModel? = null,
    val error: DetailsError? = null,
    val breedImage: CatBreedImage? = null
)

sealed class DetailsError {
    data class FetchError(val cause: Throwable? = null) : DetailsError()
}