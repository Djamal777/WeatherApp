package com.example.weatherapp.data.mappers

import com.example.weatherapp.data.remote.dto.Forecastday
import com.example.weatherapp.data.remote.dto.Hour
import com.example.weatherapp.domain.models.DayWeather
import com.example.weatherapp.domain.models.HourWeather

enum class Temperature{
    CELSIUS, FAHRENHEIT
}

fun Hour.toHourWeather(temp:Temperature): HourWeather {
    return if(temp==Temperature.CELSIUS){
        HourWeather(
            time = time.takeLast(5),
            icon = "https:${condition.icon}",
            degrees = tempC.toInt()
        )
    }else{
        HourWeather(
            time = time.takeLast(5),
            icon = "https:${condition.icon}",
            degrees = tempF.toInt()
        )
    }
}

fun Forecastday.toDayWeather(temp:Temperature): DayWeather {
    return if(temp==Temperature.CELSIUS){
        DayWeather(
            date = date,
            icon = "https:${day.condition.icon}",
            textWeather = day.condition.text,
            maxDegree = day.maxtempC.toInt(),
            minDegree = day.mintempC.toInt()
        )
    }else{
        DayWeather(
            date = date,
            icon = "https:${day.condition.icon}",
            textWeather = day.condition.text,
            maxDegree = day.maxtempF.toInt(),
            minDegree = day.mintempF.toInt()
        )
    }
}