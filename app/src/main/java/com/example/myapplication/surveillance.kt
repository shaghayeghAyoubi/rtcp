package com.example.myapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import android.content.Context

@HiltAndroidApp
class Surveillance : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}