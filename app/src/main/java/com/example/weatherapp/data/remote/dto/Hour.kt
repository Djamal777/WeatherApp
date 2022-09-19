package com.example.weatherapp.data.remote.dto


import com.google.gson.annotations.SerializedName

data class Hour(
    @SerializedName("condition")
    val condition: Condition,
    @SerializedName("temp_c")
    val tempC: Double,
    @SerializedName("temp_f")
    val tempF: Double,
    @SerializedName("time")
    val time: String
)