package com.example.weatherapp.other

fun isDay(curHour: Int, curMinute: Int, sunset: String, sunrise: String): Boolean {
    val (sunsetHour, sunsetMinute) = sunset.takeLast(5).split(":").map{it.toInt()}
    val (sunriseHour, sunriseMinute) = sunrise.takeLast(5).split(":").map{it.toInt()}
    if (curHour in sunriseHour .. sunsetHour){
        if(curHour==sunriseHour){
            return curMinute>=sunriseMinute
        }
        if(curHour==sunsetHour){
            return curMinute<sunsetMinute
        }
        return true
    }
    return false
}