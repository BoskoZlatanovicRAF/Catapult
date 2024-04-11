package raf.rs.rma_projekat.catbreed.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import raf.rs.rma_projekat.catbreed.api.model.CatBreedApiModel
import raf.rs.rma_projekat.catbreed.api.repository.CatBreedRepository
import raf.rs.rma_projekat.catbreed.list.model.CatBreedUiModel
import java.io.IOException

class CatBreedListViewModel (
    private val repository: CatBreedRepository = CatBreedRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CatBreedListState())
    val state = _state.asStateFlow()

    private fun setState(reducer: CatBreedListState.() -> CatBreedListState) =
        _state.getAndUpdate(reducer)

    private val events = MutableSharedFlow<CatBreedListUiEvent>()
    fun setEvent(event: CatBreedListUiEvent) = viewModelScope.launch {
//        when (event) {
//            is CatBreedListUiEvent.Search -> {
//                val filteredCatBreeds = _state.value.catBreeds.filter { it.name.contains(event.query, ignoreCase = true) }
//                _state.value = _state.value.copy(filteredCatBreeds = filteredCatBreeds, searchMode = true, searchText = event.query)
//            }
//            is CatBreedListUiEvent.SubmitSearch -> {
//                _state.value = _state.value.copy(searchMode = false)
//            }
//            is CatBreedListUiEvent.ClearSearch -> {
//                _state.value = _state.value.copy(filteredCatBreeds = emptyList(), searchMode = false, searchText = "")
//            }
//            // Handle other events...
//            is CatBreedListUiEvent.CatBreedSelected -> TODO()
//        }
        events.emit(event)
    }
    init {
        fetchCatBreeds()
        observeEvents()
    }

    private fun observeEvents(){
        viewModelScope.launch {
            events.collect{event->
                when (event) {
                    is CatBreedListUiEvent.Search -> {
                        val filteredCatBreeds = _state.value.catBreeds.filter { it.name.contains(event.query, ignoreCase = true) }
                        _state.value = _state.value.copy(filteredCatBreeds = filteredCatBreeds, searchMode = true, searchText = event.query)
                    }
                    is CatBreedListUiEvent.SubmitSearch -> {
                        _state.value = _state.value.copy(searchMode = false)
                    }
                    is CatBreedListUiEvent.ClearSearch -> {
                        _state.value = _state.value.copy(filteredCatBreeds = emptyList(), searchMode = false, searchText = "")
                    }
                    // Handle other events...
                    is CatBreedListUiEvent.CatBreedSelected -> TODO()
                }
            }
        }
    }

    private fun fetchCatBreeds() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            try {
                val catBreeds = withContext(Dispatchers.IO) {
                    repository.getAllCatBreeds().map { it.asCatBreedUiModel() }
                }

                _state.value = _state.value.copy(catBreeds = catBreeds, loading = false)
            } catch (e: Exception) {
                Log.e("BreedsListViewModel", "Error fetching breeds", e)
                _state.value = _state.value.copy(error = ListError.FetchError(e), loading = false)
            }
        }
    }

    private fun CatBreedApiModel.asCatBreedUiModel() = CatBreedUiModel(
        id = this.id,
        name = this.name,
        description = this.description,
        temperament = this.temperament,
        alt_names = this.alt_names
    )
}