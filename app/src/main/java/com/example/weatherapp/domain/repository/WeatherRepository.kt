package com.example.weatherapp.domain.repository

import com.example.weatherapp.data.remote.dto.ForecastWeather
import com.example.weatherapp.other.Resource
import io.reactivex.rxjava3.core.Observable

interface WeatherRepository {

    fun getForecast(
        latitude: Double,
        longitude: Double,
        temperatureUnit: String
    ): Observable<Resource<ForecastWeather>>
}