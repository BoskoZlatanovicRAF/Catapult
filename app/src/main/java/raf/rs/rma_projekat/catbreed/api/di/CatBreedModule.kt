package raf.rs.rma_projekat.catbreed.api.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import raf.rs.rma_projekat.catbreed.api.CatBreedApi
import raf.rs.rma_projekat.networking.CatApiRetrofit
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object CatBreedModule {

    @Provides
    @Singleton
    fun provideCatBreedApi(@CatApiRetrofit retrofit: Retrofit): CatBreedApi = retrofit.create()
}