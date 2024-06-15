package raf.rs.rma_projekat.leaderboard.api.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import raf.rs.rma_projekat.leaderboard.api.LeaderBoardApi
import raf.rs.rma_projekat.networking.LeaderBoardRetrofit
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LeaderBoardModule {

    @Provides
    @Singleton
    fun provideLeaderBoardApi(@LeaderBoardRetrofit retrofit: Retrofit): LeaderBoardApi = retrofit.create(LeaderBoardApi::class.java)
}