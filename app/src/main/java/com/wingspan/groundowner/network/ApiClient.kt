package com.wingspan.groundowner.network



import com.wingspan.groundowner.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiClient {
    //BuildConfig.BASE_URL
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://ground-owner.onrender.com/") // Ensure BASE_URL is defined in gradle.properties
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = retrofit

    @Provides
    @Singleton
    fun provideApiService(): ApiService = retrofit.create(ApiService::class.java)
}