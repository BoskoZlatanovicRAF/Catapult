package raf.rs.rma_projekat.catbreed.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import raf.rs.rma_projekat.catbreed.api.repository.CatBreedRepository
import javax.inject.Inject

@HiltViewModel
class PhotoGalleryViewModel @Inject constructor(
    private val catBreedRepository: CatBreedRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(){

    private val breedId: String = savedStateHandle["breedId"] ?: ""

    private val _state = MutableStateFlow(PhotoGalleryState())
    val state = _state.asStateFlow()
    private fun setState(reducer: PhotoGalleryState.() -> PhotoGalleryState) = _state.update(reducer)

    init {
        fetchBreedImages()
    }

    private fun fetchBreedImages() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                val breedImages = withContext(Dispatchers.IO) {
                    catBreedRepository.fetchBreedImages(breedId)
                }
                setState { copy(images = breedImages, loading = false) }
            } catch (e: Exception) {
                setState { copy(loading = false, error = e.message) }
            }
        }
    }

    fun getImageIndex(imageId: String): Int {
        return state.value.images.indexOfFirst { it.id == imageId }
    }
}
