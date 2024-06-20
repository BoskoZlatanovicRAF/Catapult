package raf.rs.rma_projekat.catbreed.gallery

import raf.rs.rma_projekat.catbreed.db.entity.CatBreedImageEntity

data class PhotoGalleryState (
    val images: List<CatBreedImageEntity> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val showPager: Boolean = false,
    val selectedImageId: String? = null
)