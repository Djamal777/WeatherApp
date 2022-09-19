package com.example.weatherapp.domain.models

data class DayWeather(
    val date:String,
    val icon:String,
    val textWeather:String,
    val maxDegree:Int,
    val minDegree:Int
)
