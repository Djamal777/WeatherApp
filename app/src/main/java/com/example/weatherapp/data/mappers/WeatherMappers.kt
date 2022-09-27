package com.example.weatherapp.data.mappers

import com.example.weatherapp.data.remote.dto.Daily
import com.example.weatherapp.data.remote.dto.Hourly
import com.example.weatherapp.domain.models.DayWeather
import com.example.weatherapp.domain.models.HourWeather
import com.example.weatherapp.other.WeatherType

fun Daily.toListDayWeather(): List<DayWeather> {
    val list = mutableListOf<DayWeather>()
    for (i in 0..6) {
        val weatherType = WeatherType.fromWMO(weathercode[i].toInt())
        list.add(
            DayWeather(
                date = time[i],
                icon = weatherType.iconRes,
                textWeather = weatherType.weatherDesc,
                maxDegree = temperature2mMax[i].toInt(),
                minDegree = temperature2mMin[i].toInt()
            )
        )
    }
    return list
}

fun Hourly.toListHourWeather(): List<HourWeather> {
    val list = mutableListOf<HourWeather>()
    for (i in 0..47) {
        val weatherType = WeatherType.fromWMO(weathercode[i].toInt())
        list.add(
            HourWeather(
                time = time[i].takeLast(5),
                icon = weatherType.iconRes,
                degrees = temperature2m[i].toInt()
            )
        )
    }
    return list
}