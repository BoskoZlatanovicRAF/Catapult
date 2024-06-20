package raf.rs.rma_projekat.catbreed.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CatBreedImageEntity (
    @PrimaryKey val id: String,
    val breedId: String,
    val url: String,
)