package raf.rs.rma_projekat.catbreed.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CatBreedEntity (
    @PrimaryKey val id: String,
    val name: String,
    val temperament: String,
    val origin: String,
    val alt_names: String = "",
    val description: String,
    val life_span: String,
//    val weight: Weight,
    val rare: Int,
    val adaptability: Int,
    val affection_level: Int,
    val child_friendly: Int,
    val dog_friendly: Int,
    val energy_level: Int,
    val grooming: Int,
    val health_issues: Int,
    val intelligence: Int,
    val shedding_level: Int,
    val social_needs: Int,
    val stranger_friendly: Int,
    val vocalisation: Int,
    val reference_image_id: String? = null,
    val wikipedia_url: String? = null,
)