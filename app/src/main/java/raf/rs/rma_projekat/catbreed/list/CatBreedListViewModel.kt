package raf.rs.rma_projekat.catbreed.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import raf.rs.rma_projekat.catbreed.api.repository.CatBreedRepository
import raf.rs.rma_projekat.catbreed.mappers.asCatBreedUiModel
import javax.inject.Inject
@HiltViewModel
class CatBreedListViewModel @Inject constructor(
    private val repository: CatBreedRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CatBreedListState())
    val state = _state.asStateFlow()

    private fun setState(reducer: CatBreedListState.() -> CatBreedListState) =
        _state.getAndUpdate(reducer)

    private val events = MutableSharedFlow<CatBreedListUiEvent>()
    fun setEvent(event: CatBreedListUiEvent) = viewModelScope.launch {
        events.emit(event)
    }
    init {
        fetchCatBreeds()
        observeCatBreeds()
        observeEvents()

    }

    private fun observeCatBreeds() {
//        Log.d("CatBreedListViewModel", "pozvana observeCatBreeds()")
        viewModelScope.launch {
            setState { copy(loading = true) }
            repository.observeCatBreeds()
                .distinctUntilChanged()
                .collect{
                    setState{
                        copy(
                            loading = false,
                            catBreeds = it.map { it.asCatBreedUiModel() }
                        )
                    }
                }
        }

    }

    private fun observeEvents(){
        viewModelScope.launch {
            events.collect{ event->
                when (event) {
                    is CatBreedListUiEvent.Search -> {
                        setState {
                            val filteredCatBreeds = catBreeds.filter { it.name.contains(event.query, ignoreCase = true) }
                            copy(filteredCatBreeds = filteredCatBreeds, searchMode = true, searchText = event.query)
                        }
                    }
                    is CatBreedListUiEvent.SubmitSearch -> {
                        setState { copy(searchMode = false) }
                    }
                    is CatBreedListUiEvent.ClearSearch -> {
                        setState { copy(filteredCatBreeds = emptyList(), searchMode = false, searchText = "") }
                    }
                    is CatBreedListUiEvent.CatBreedSelected -> TODO()
                }
            }
        }
    }

    private fun fetchCatBreeds() {
//        Log.d("CatBreedListViewModel", "pozvana fetchCatBreeds()")
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                withContext(Dispatchers.IO) {
                    // delegira dalje, ili ce fetchovati sa apija, ili ce povuci iz baze ukoliko postoji
                    repository.fetchAllCatBreeds()
                }

            } catch (e: Exception) {
                Log.e("BreedsListViewModel", "Error fetching breeds", e)
                setState { copy(error = ListError.FetchError(e), loading = false) }
            }
        }
    }


}