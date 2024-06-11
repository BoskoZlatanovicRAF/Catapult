package raf.rs.rma_projekat.catbreed.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CatBreedImageEntity (
    @PrimaryKey val id: String,
    val breedId: String,
    val url: String,
)