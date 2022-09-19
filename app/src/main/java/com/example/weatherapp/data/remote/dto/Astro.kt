package com.example.weatherapp.data.remote.dto


import com.google.gson.annotations.SerializedName

data class Astro(
    @SerializedName("sunrise")
    val sunrise: String,
    @SerializedName("sunset")
    val sunset: String
)