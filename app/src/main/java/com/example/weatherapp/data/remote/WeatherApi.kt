package com.example.weatherapp.data.remote

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.remote.dto.ForecastWeather
import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/v1/forecast.json")
    fun getForecast(
        @Query("key") key:String=BuildConfig.API_KEY,
        @Query("q") q:String,
        @Query("days") days:Int,
        @Query("aqi") aqi:String="no",
        @Query("alerts") alerts:String="no"
    ): Single<ForecastWeather>
}