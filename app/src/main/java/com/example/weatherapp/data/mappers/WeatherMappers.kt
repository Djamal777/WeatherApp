package com.example.weatherapp.data.mappers

import com.example.weatherapp.data.remote.dto.Daily
import com.example.weatherapp.data.remote.dto.Hourly
import com.example.weatherapp.domain.models.DayWeather
import com.example.weatherapp.domain.models.HourWeather
import com.example.weatherapp.other.WeatherType
import com.example.weatherapp.other.isDay

fun Daily.toListDayWeather(): List<DayWeather> {
    val list = mutableListOf<DayWeather>()
    var weatherType:WeatherType
    for (i in 0..6) {
        weatherType = WeatherType.fromWMO(weathercode[i].toInt(), true)
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

fun Hourly.toListHourWeather(
    sunrise1: String,
    sunset1: String,
    sunrise2: String,
    sunset2: String,
): List<HourWeather> {
    val list = mutableListOf<HourWeather>()
    var weatherType:WeatherType
    for (i in 0..47) {
        weatherType = if (i in 0..23) {
            WeatherType.fromWMO(
                weathercode[i].toInt(),
                isDay(
                    time[i].takeLast(5).split(":")[0].toInt(),
                    time[i].takeLast(5).split(":")[1].toInt(),
                    sunset1,
                    sunrise1
                )
            )
        } else {
            WeatherType.fromWMO(
                weathercode[i].toInt(),
                isDay(
                    time[i].takeLast(5).split(":")[0].toInt(),
                    time[i].takeLast(5).split(":")[1].toInt(),
                    sunset2,
                    sunrise2
                )
            )
        }
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