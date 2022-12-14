package com.example.weatherapp.data.remote.dto


import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @SerializedName("temperature")
    val temperature: Double,
    @SerializedName("time")
    val time: String,
    @SerializedName("weathercode")
    val weathercode: Double,
    @SerializedName("winddirection")
    val winddirection: Double,
    @SerializedName("windspeed")
    val windspeed: Double
)