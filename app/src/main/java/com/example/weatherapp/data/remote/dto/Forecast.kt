package com.example.weatherapp.data.remote.dto


import com.google.gson.annotations.SerializedName

data class Forecast(
    @SerializedName("forecastday")
    val forecastday: List<Forecastday>
)