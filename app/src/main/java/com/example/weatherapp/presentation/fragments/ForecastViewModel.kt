package com.example.weatherapp.presentation.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.dto.ForecastWeather
import com.example.weatherapp.domain.location.LocationTracker
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.other.Const.CELSIUS
import com.example.weatherapp.other.Resource
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForecastViewModel(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    var spinnerCheck = 0

    private val compositeDisposable = CompositeDisposable()

    private val _forecast = MutableLiveData<Resource<ForecastWeather>>()
    val forecast: LiveData<Resource<ForecastWeather>> = _forecast

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getForecast(CELSIUS)
        }
    }

    suspend fun getForecast(unit: String) {
        locationTracker.getCurrentLocation()?.let { location ->
            compositeDisposable.add(
                repository.getForecast(
                    location.latitude,
                    location.longitude,
                    unit
                )
                    .subscribe({
                        _forecast.postValue(it)
                    }, {
                        _forecast.postValue(Resource.Error(it.message!!))
                    })
            )
        } ?: _forecast.postValue(Resource.Error("Check your GPS!"))
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}