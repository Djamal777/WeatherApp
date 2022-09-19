package com.example.weatherapp.data.remote.dto


import com.google.gson.annotations.SerializedName

data class Day(
    @SerializedName("avgtemp_c")
    val avgtempC: Double,
    @SerializedName("avgtemp_f")
    val avgtempF: Double,
    @SerializedName("condition")
    val condition: Condition,
    @SerializedName("daily_chance_of_rain")
    val dailyChanceOfRain: Int,
    @SerializedName("daily_chance_of_snow")
    val dailyChanceOfSnow: Int,
    @SerializedName("daily_will_it_rain")
    val dailyWillItRain: Int,
    @SerializedName("daily_will_it_snow")
    val dailyWillItSnow: Int,
    @SerializedName("maxtemp_c")
    val maxtempC: Double,
    @SerializedName("maxtemp_f")
    val maxtempF: Double,
    @SerializedName("maxwind_kph")
    val maxwindKph: Double,
    @SerializedName("mintemp_c")
    val mintempC: Double,
    @SerializedName("mintemp_f")
    val mintempF: Double,
    @SerializedName("uv")
    val uv: Double
)