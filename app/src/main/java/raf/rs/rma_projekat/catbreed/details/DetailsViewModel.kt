package raf.rs.rma_projekat.catbreed.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import raf.rs.rma_projekat.catbreed.api.model.CatBreedApiModel
import raf.rs.rma_projekat.catbreed.api.repository.CatBreedRepository
import raf.rs.rma_projekat.catbreed.details.model.DetailsUiModel

class DetailsViewModel(
    private val breedId: String,
    private val catBreedRepository: CatBreedRepository = CatBreedRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsState())
    val state = _state.asStateFlow()
    private fun setState(reducer: DetailsState.() -> DetailsState) = _state.update(reducer)

    private val _event = MutableSharedFlow<DetailsUiEvent>()
    fun setEvent(event: DetailsUiEvent) = viewModelScope.launch { _event.emit(event) }

    init {
        fetchBreedDetails()
    }

    fun fetchBreedDetails() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                // Assuming getBreedDetails() fetches the details of the breed including the image ID
                val breedDetails = withContext(Dispatchers.IO) {
                    catBreedRepository.getBreedDetails(breedId).asBreedsDetailUiModel()
                }
                val breedImage = breedDetails.reference_image_id?.let { imageId ->
                    catBreedRepository.getBreedImage(imageId)
                }
                setState { copy(breedsDetail = breedDetails, breedImage = breedImage, loading = false) }
            } catch (e: Exception) {
                setState { copy(loading = false, error = e.localizedMessage ?: "Unknown error") }
            }
        }
    }

    private fun CatBreedApiModel.asBreedsDetailUiModel() = DetailsUiModel(
        id = this.id,
        name = this.name,
        temperament = this.temperament,
        origin = this.origin,
        description = this.description,
        life_span = this.life_span,
        weight = this.weight,
        rare = this.rare,
        adaptability = this.adaptability,
        affection_level = this.affection_level,
        child_friendly = this.child_friendly,
        dog_friendly = this.dog_friendly,
        energy_level = this.energy_level,
        grooming = this.grooming,
        health_issues = this.health_issues,
        intelligence = this.intelligence,
        shedding_level = this.shedding_level,
        social_needs = this.social_needs,
        stranger_friendly = this.stranger_friendly,
        vocalisation = this.vocalisation,
        reference_image_id = this.reference_image_id,
        wikipedia_url = this.wikipedia_url,
    )
}
