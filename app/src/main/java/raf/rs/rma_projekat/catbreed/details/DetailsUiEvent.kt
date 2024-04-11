package raf.rs.rma_projekat.catbreed.details

sealed class DetailsUiEvent {
    data class DeleteBreed(val breedId: String) : DetailsUiEvent()
}