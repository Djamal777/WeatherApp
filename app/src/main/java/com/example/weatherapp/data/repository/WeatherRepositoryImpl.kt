package com.example.weatherapp.data.repository

import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.dto.ForecastWeather
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.other.Resource
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.io.IOException

class WeatherRepositoryImpl(
    private val api: WeatherApi
) : WeatherRepository {
    override fun getForecast(q: String, days: Int): Observable<Resource<ForecastWeather>> {
        return Observable.create<Resource<ForecastWeather>> { emitter ->
            try {
                emitter.onNext(Resource.Loading<ForecastWeather>())
                val forecast = api.getForecast(q = q, days = days).blockingGet()
                emitter.onNext(Resource.Success<ForecastWeather>(forecast))
            } catch (e: HttpException) {
                emitter.onError(Throwable("An unexpected message occured"))
            } catch (e: IOException) {
                emitter.onError(Throwable("Couldn't reach server. Check your internet connection"))
            }
        }
    }
}