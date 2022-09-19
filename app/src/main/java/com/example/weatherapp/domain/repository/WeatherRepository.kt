package com.example.weatherapp.domain.repository

import com.example.weatherapp.data.remote.dto.ForecastWeather
import com.example.weatherapp.other.Resource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface WeatherRepository {

    fun getForecast(
        q: String,
        days: Int,
    ): Observable<Resource<ForecastWeather>>
}