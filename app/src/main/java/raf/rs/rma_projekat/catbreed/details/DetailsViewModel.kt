package raf.rs.rma_projekat.catbreed.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import raf.rs.rma_projekat.catbreed.api.repository.CatBreedRepository
import raf.rs.rma_projekat.catbreed.mappers.asBreedsDetailUiModel
import raf.rs.rma_projekat.catbreed.mappers.asCatBreedImage
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val catBreedRepository: CatBreedRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val breedId: String = savedStateHandle["breedId"] ?: ""

    private val _state = MutableStateFlow(DetailsState())
    val state = _state.asStateFlow()
    private fun setState(reducer: DetailsState.() -> DetailsState) = _state.update(reducer)

    private val _event = MutableSharedFlow<DetailsUiEvent>()
    fun setEvent(event: DetailsUiEvent) = viewModelScope.launch { _event.emit(event) }

    init {
        fetchBreedDetails()
    }

    private fun fetchBreedDetails() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                val breedDetails = withContext(Dispatchers.IO) {
                    catBreedRepository.getBreedDetails(breedId).asBreedsDetailUiModel()
                }
                val breedImage = breedDetails.reference_image_id?.let { imageId ->
                    catBreedRepository.getBreedImage(imageId)
                }
                setState { copy(breedsDetail = breedDetails, breedImage = breedImage?.asCatBreedImage(), loading = false) }
            } catch (e: Exception) {
                setState { copy(loading = false, error = DetailsError.FetchError(e)) }
            }
        }

    }

}
