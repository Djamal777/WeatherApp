package com.example.weatherapp.di

import com.example.weatherapp.data.location.DefaultLocationTracker
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.domain.location.LocationTracker
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.presentation.fragments.ForecastViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com")
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
    single<WeatherRepository> {
        WeatherRepositoryImpl(get())
    }
    single<FusedLocationProviderClient>{
        LocationServices.getFusedLocationProviderClient(androidApplication())
    }
    viewModel{
        ForecastViewModel(get(), DefaultLocationTracker(get(),get()))
    }
}