package raf.rs.rma_projekat.catbreed.list

import raf.rs.rma_projekat.catbreed.list.model.CatBreedUiModel

sealed class CatBreedListUiEvent {
    data class CatBreedSelected(val catBreed: String) : CatBreedListUiEvent()
    object SubmitSearch : CatBreedListUiEvent()
    data class Search(val query: String) : CatBreedListUiEvent()
    object ClearSearch : CatBreedListUiEvent()
}