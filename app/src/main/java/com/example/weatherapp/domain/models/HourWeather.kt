package com.example.weatherapp.domain.models

data class HourWeather(
    val time: String,
    val icon: Int,
    val degrees: Int
)
