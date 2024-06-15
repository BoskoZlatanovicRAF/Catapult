package raf.rs.rma_projekat.networking

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import raf.rs.rma_projekat.catbreed.api.CatBreedApi
import raf.rs.rma_projekat.networking.serialization.AppJson
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                val updatedRequest = it.request().newBuilder()
                    .addHeader("CustomHeader", "CustomValue")
                    .addHeader("x-api-key", "live_kxHbFgrMyDKms5oUXsXqQaO6khkmXQfhvplAWjUHGKMaPKNRiIiFw7gH3uW2WIRv")
                    .build()
                it.proceed(updatedRequest)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            )
            .build()
    }

    @Provides
    @Singleton
    @CatApiRetrofit
    fun provideRetrofitClient(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(AppJson.asConverterFactory("application/json".toMediaType()))
            .build()
    }


    @Provides
    @Singleton
    @LeaderBoardRetrofit
    fun provideLeaderBoardRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://rma.finlab.rs/")
            .client(okHttpClient)
            .addConverterFactory(AppJson.asConverterFactory("application/json".toMediaType()))
            .build()
    }

}


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CatApiRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LeaderBoardRetrofit
