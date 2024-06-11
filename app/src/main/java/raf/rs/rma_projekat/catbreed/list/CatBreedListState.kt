package raf.rs.rma_projekat.catbreed.list

import raf.rs.rma_projekat.catbreed.list.model.CatBreedUiModel

data class CatBreedListState(
    val catBreeds: List<CatBreedUiModel> = emptyList(),
    val filteredCatBreeds: List<CatBreedUiModel> = emptyList(),
    val loading: Boolean = false,
    val searchMode: Boolean = false,
    val error: ListError? = null,
    val searchText : String = ""
)

sealed class ListError{
    data class FetchError(val cause: Throwable? = null) : ListError()
}


