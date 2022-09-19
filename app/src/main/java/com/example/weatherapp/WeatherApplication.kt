package com.example.weatherapp

import android.app.Application
import android.content.res.Configuration
import com.example.weatherapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WeatherApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@WeatherApplication)
            modules(appModule)
        }
    }
}