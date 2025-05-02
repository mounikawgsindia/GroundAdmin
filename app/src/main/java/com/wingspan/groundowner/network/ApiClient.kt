package com.wingspan.groundowner.network



import com.wingspan.groundowner.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiClient {

    //https://ground-owner.onrender.com/
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)   // Connection timeout
        .readTimeout(60, TimeUnit.SECONDS)      // Server read timeout
        .writeTimeout(60, TimeUnit.SECONDS)     // Client write timeout
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)// Ensure BASE_URL is defined in gradle.properties
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofitGroundOwner by lazy {
        Retrofit.Builder()
            .baseUrl("https://ground-booking-live-scores-main12.onrender.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = retrofit

    @Provides
    @Singleton
    fun provideApiService(): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideGroundOwnerApi(): OwnerApiService  = retrofitGroundOwner.create(OwnerApiService ::class.java)
}