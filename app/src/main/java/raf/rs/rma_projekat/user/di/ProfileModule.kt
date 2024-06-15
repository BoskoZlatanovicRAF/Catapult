package raf.rs.rma_projekat.user.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import raf.rs.rma_projekat.user.ProfileData
import raf.rs.rma_projekat.user.ProfileDataSerializer
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {
    @Singleton
    @Provides
    fun provideProfileDataStore(
        @ApplicationContext context: Context
    ): DataStore<ProfileData> {
        return DataStoreFactory.create(
            produceFile = { context.dataStoreFile(fileName = "profile.json") },
            serializer = ProfileDataSerializer(),
        )
    }
}