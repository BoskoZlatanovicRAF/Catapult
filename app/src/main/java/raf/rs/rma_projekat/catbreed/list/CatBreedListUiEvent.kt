package raf.rs.rma_projekat.catbreed.list

import raf.rs.rma_projekat.catbreed.list.model.CatBreedUiModel

sealed class CatBreedListUiEvent {

    // zasto je nesto data class, data object, object, sealed class
    data class CatBreedSelected(val catBreed: String) : CatBreedListUiEvent()
    data class SubmitSearch(val searchText: String) : CatBreedListUiEvent()
    data class Search(val query: String) : CatBreedListUiEvent()
    data object ClearSearch : CatBreedListUiEvent()
}