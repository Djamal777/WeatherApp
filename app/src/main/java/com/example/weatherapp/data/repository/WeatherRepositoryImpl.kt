package com.example.weatherapp.data.repository

import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.dto.ForecastWeather
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.other.Resource
import io.reactivex.rxjava3.core.Observable
import retrofit2.HttpException

class WeatherRepositoryImpl(
    private val api: WeatherApi
) : WeatherRepository {
    override fun getForecast(
        latitude: Double,
        longitude: Double,
        temperatureUnit: String
    ): Observable<Resource<ForecastWeather>> {
        return Observable.create<Resource<ForecastWeather>> { emitter ->
            try {
                emitter.onNext(Resource.Loading<ForecastWeather>())
                val forecast = api.getForecast(
                    latitude = latitude,
                    longitude = longitude,
                    temperatureUnit = temperatureUnit
                ).blockingGet()
                emitter.onNext(Resource.Success<ForecastWeather>(forecast))
                emitter.onComplete()
            } catch (e: HttpException) {
                emitter.onError(Throwable("An unexpected message occured"))
            } catch (e: RuntimeException) {
                emitter.onError(Throwable("Couldn't reach server. Check your internet connection"))
            }
        }
    }
}