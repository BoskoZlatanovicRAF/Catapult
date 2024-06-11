package raf.rs.rma_projekat.db.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import raf.rs.rma_projekat.db.AppDatabase
import raf.rs.rma_projekat.db.AppDatabaseBuilder
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(builder: AppDatabaseBuilder): AppDatabase {
        return builder.build()
    }
}