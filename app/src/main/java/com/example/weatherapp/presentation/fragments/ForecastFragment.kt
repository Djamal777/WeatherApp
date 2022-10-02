package com.example.weatherapp.presentation.fragments

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.mappers.toListDayWeather
import com.example.weatherapp.data.mappers.toListHourWeather
import com.example.weatherapp.data.remote.dto.ForecastWeather
import com.example.weatherapp.databinding.FragmentForecastBinding
import com.example.weatherapp.other.Const.CELSIUS
import com.example.weatherapp.other.Const.FAHRENHEIT
import com.example.weatherapp.other.Resource
import com.example.weatherapp.other.WeatherType
import com.example.weatherapp.other.anim
import com.example.weatherapp.other.isDay
import com.example.weatherapp.presentation.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class ForecastFragment : Fragment() {

    private val viewModel by viewModel<ForecastViewModel>()
    private lateinit var binding: FragmentForecastBinding
    private lateinit var daysAdapter: DaysAdapter
    private lateinit var hoursAdapter: HoursAdapter
    private lateinit var hoursLinearLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForecastBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.statusBarColor = resources.getColor(R.color.day_status_bar)
        setupRecyclerView()
        observeForecast()
        binding.refresh.setOnRefreshListener {
            val deg = if (binding.spinnerCF.selectedItemPosition == 0) CELSIUS else FAHRENHEIT
            lifecycleScope.launch {
                viewModel.getForecast(deg)
                hoursLinearLayoutManager.scrollToPosition(0)
            }
        }
        binding.spinnerCF.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (++viewModel.spinnerCheck > 1) {
                    val deg = if (p2 == 0) CELSIUS else FAHRENHEIT
                    lifecycleScope.launch {
                        viewModel.getForecast(deg)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }
    }

    private fun setBackgroundColor(cond: String?, isDay: Boolean) {
        cond?.let {
            if (!isDay) {
                view?.setBackgroundResource(R.drawable.gradient_night)
                activity?.window?.statusBarColor = resources.getColor(R.color.night_status_bar)
            } else {
                when (it) {
                    "ClearSky" -> {
                        view?.setBackgroundResource(com.example.weatherapp.R.drawable.gradient_sunny)
                        activity?.window?.statusBarColor =
                            resources.getColor(R.color.sunny_status_bar)
                    }
                    "Foggy" -> {
                        view?.setBackgroundResource(com.example.weatherapp.R.drawable.gradient_foggy)
                        activity?.window?.statusBarColor =
                            resources.getColor(R.color.foggy_status_bar)
                    }
                    else -> {
                        view?.setBackgroundResource(com.example.weatherapp.R.drawable.gradient_day)
                        activity?.window?.statusBarColor =
                            resources.getColor(R.color.day_status_bar)
                    }
                }
            }
        }
    }

    private fun observeForecast() {
        viewModel.forecast.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.startAnimation(anim)
                }
                is Resource.Error -> {
                    binding.progressBar.setOnClickListener {
                        binding.progressBar.startAnimation(anim)
                        val deg =
                            if (binding.spinnerCF.selectedItemPosition == 0) CELSIUS else FAHRENHEIT
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.getForecast(deg)
                        }
                    }
                    if (!(activity as MainActivity).arePermissionsGranted()) {
                        Toast.makeText(
                            requireContext(),
                            "This app needs your location",
                            Toast.LENGTH_SHORT
                        ).show()
                        (activity as MainActivity).getPermissions()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    binding.progressBar.clearAnimation()
                    binding.refresh.isRefreshing = false
                }
                is Resource.Success -> {
                    refreshView(it.data)
                    binding.apply {
                        refresh.isRefreshing = false
                        progressBar.visibility = View.GONE
                        progressBar.animation = null
                        content.visibility = View.VISIBLE
                    }
                    it.data?.daily?.weathercode?.get(0)?.toInt()?.let { weatherCode ->
                        val isDay=isDay(
                            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                            Calendar.getInstance().get(Calendar.MINUTE),
                            it.data.daily.sunset[0],
                            it.data.daily.sunrise[0]
                        )
                        val weather = WeatherType.fromWMO(weatherCode,isDay)
                        setBackgroundColor(
                            weather.weatherDesc,
                            isDay
                        )
                        binding.apply {
                            textCurrentWeather.text = weather.weatherDesc
                            currentWeatherImage.setImageDrawable(
                                AppCompatResources.getDrawable(
                                    requireContext(),
                                    weather.iconRes
                                )
                            )
                        }
                    }
                    binding.apply {
                        val geocoder = Geocoder(requireContext(), Locale.US)
                        if(it.data!=null) {
                            town.text = geocoder.getFromLocation(it.data.latitude, it.data.longitude, 1)[0].locality
                        }
                        val calendar = Calendar.getInstance()
                        val date = it.data?.hourly?.time?.get(0)?.take(10)?.split("-")?.map {
                            it.toInt()
                        }!!
                        calendar.set(date[0], date[1] - 1, date[2])
                        toolbar.title = "${
                            calendar.getDisplayName(
                                Calendar.DAY_OF_WEEK,
                                Calendar.LONG,
                                Locale.US
                            )
                        }, ${
                            calendar.getDisplayName(
                                Calendar.MONTH,
                                Calendar.LONG,
                                Locale.US
                            )
                        } ${calendar.get(Calendar.DAY_OF_MONTH)}"
                    }
                }
            }
        }
    }

    private fun refreshView(forecastWeather: ForecastWeather?) {
        val days = forecastWeather?.daily?.toListDayWeather()
        daysAdapter.differ.submitList(days)
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val hours =
            forecastWeather?.hourly?.toListHourWeather(
                forecastWeather.daily.sunrise[0],
                forecastWeather.daily.sunset[0],
                forecastWeather.daily.sunrise[1],
                forecastWeather.daily.sunset[1],
            )?.subList(currentHour, 24 + currentHour)
        hoursAdapter.differ.submitList(hours)
        binding.apply {
            currentDegrees.text = requireContext().getString(
                R.string.degree,
                forecastWeather?.currentWeather?.temperature?.toInt()
            )
            highestCurrentDegrees.text = days?.get(0)?.maxDegree.toString()
            lowestCurrentDegrees.text = days?.get(0)?.minDegree.toString()
        }
    }

    private fun setupRecyclerView() {
        daysAdapter = DaysAdapter(requireContext())
        hoursAdapter = HoursAdapter(requireContext())
        binding.apply {
            hoursRecyclerView.apply {
                adapter = hoursAdapter
                hoursLinearLayoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                layoutManager = hoursLinearLayoutManager
            }
            daysRecyclerView.apply {
                adapter = daysAdapter
                layoutManager = object : LinearLayoutManager(requireContext()) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            }
        }
    }
}