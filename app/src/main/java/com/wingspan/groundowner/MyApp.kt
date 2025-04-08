package com.wingspan.groundowner

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp:Application() {

    override fun onCreate() {
        super.onCreate()
        val config = resources.configuration
        config.fontScale = 0.6f
        resources.updateConfiguration(config, resources.displayMetrics)
        // Set the app-wide light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

}