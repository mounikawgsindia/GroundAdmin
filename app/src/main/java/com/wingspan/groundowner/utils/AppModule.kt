package com.wingspan.groundowner.utils

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class) // This means it will be available for the entire app
object AppModule {

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext // Provide Context
    }

    @Provides
    fun provideContentResolver(application: Application): ContentResolver {
        return application.contentResolver
    }
}