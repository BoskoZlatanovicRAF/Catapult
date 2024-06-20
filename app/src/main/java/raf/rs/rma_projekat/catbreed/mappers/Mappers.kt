package raf.rs.rma_projekat.catbreed.mappers

import raf.rs.rma_projekat.catbreed.api.model.CatBreedApiModel
import raf.rs.rma_projekat.catbreed.api.model.CatBreedImage
import raf.rs.rma_projekat.catbreed.db.entity.CatBreedEntity
import raf.rs.rma_projekat.catbreed.db.entity.CatBreedImageEntity
import raf.rs.rma_projekat.catbreed.details.model.DetailsUiModel
import raf.rs.rma_projekat.catbreed.gallery.model.CatBreedImageApiModel
import raf.rs.rma_projekat.catbreed.list.model.CatBreedUiModel

fun CatBreedApiModel.asCatBreedDbModel(): CatBreedEntity {
    return CatBreedEntity(
        id = this.id,
        name = this.name,
        temperament = this.temperament,
        origin = this.origin,
        alt_names = this.alt_names,
        description = this.description,
        life_span = this.life_span,
        rare = this.rare,
//        weight = this.weight,
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
        wikipedia_url = this.wikipedia_url
    )

}

fun CatBreedEntity.asCatBreedUiModel(): CatBreedUiModel {
    return CatBreedUiModel(
        id = this.id,
        name = this.name,
        description = this.description,
        temperament = this.temperament,
        alt_names = this.alt_names
    )
}

fun CatBreedEntity.asBreedsDetailUiModel() : DetailsUiModel {
    return DetailsUiModel(
        id = this.id,
        name = this.name,
        temperament = this.temperament,
        origin = this.origin,
        description = this.description,
        life_span = this.life_span,
//        weight = this.weight,
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



fun CatBreedImageApiModel.asCatBreedImageEntity(breedId: String) : CatBreedImageEntity {
    return CatBreedImageEntity(
        id = this.id,
        breedId = breedId,
        url = this.url
    )
}

fun CatBreedImageEntity.asCatBreedImage() : CatBreedImage {
    return CatBreedImage(
        id = this.id,
        url = this.url
    )
}

fun CatBreedImage.asCatBreedImageEntity() : CatBreedImageEntity {
    return CatBreedImageEntity(
        id = this.id,
        breedId = "",
        url = this.url
    )
}


