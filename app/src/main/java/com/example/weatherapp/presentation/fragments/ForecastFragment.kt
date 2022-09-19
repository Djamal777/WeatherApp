package com.example.weatherapp.presentation.fragments

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
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.data.mappers.Temperature
import com.example.weatherapp.data.mappers.toDayWeather
import com.example.weatherapp.data.mappers.toHourWeather
import com.example.weatherapp.data.remote.dto.ForecastWeather
import com.example.weatherapp.data.remote.dto.Hour
import com.example.weatherapp.databinding.FragmentForecastBinding
import com.example.weatherapp.other.Resource
import com.example.weatherapp.other.anim
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
    private var currentSelection = 0

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
        setupRecyclerView()
        observeForecast()
        binding.refresh.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.getForecast()
                hoursLinearLayoutManager.scrollToPosition(0)
            }
        }
        binding.spinnerCF.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (currentSelection != p2) {
                    if (p2 == 0) {
                        refreshView(viewModel.forecast.value?.data, Temperature.CELSIUS)
                    } else {
                        refreshView(viewModel.forecast.value?.data, Temperature.FAHRENHEIT)
                    }
                    currentSelection = p2
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }
    }

    private fun setBackgroundColor(cond: String?, isDay: Int?) {
        cond?.let {
            if (isDay == 0) {
                view?.setBackgroundResource(com.example.weatherapp.R.drawable.gradient_night)
                activity?.window?.statusBarColor=resources.getColor(R.color.night_status_bar)
            } else {
                when (it) {
                    "Sunny" -> {
                        view?.setBackgroundResource(com.example.weatherapp.R.drawable.gradient_sunny)
                        activity?.window?.statusBarColor=resources.getColor(R.color.sunny_status_bar)
                    }
                    "Foggy" ->{
                        view?.setBackgroundResource(com.example.weatherapp.R.drawable.gradient_foggy)
                        activity?.window?.statusBarColor=resources.getColor(R.color.foggy_status_bar)
                    }
                    else -> {
                        view?.setBackgroundResource(com.example.weatherapp.R.drawable.gradient_day)
                        activity?.window?.statusBarColor=resources.getColor(R.color.day_status_bar)
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
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.getForecast()
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
                    val deg =
                        if (binding.spinnerCF.selectedItemPosition == 0) Temperature.CELSIUS else Temperature.FAHRENHEIT
                    refreshView(it.data, deg)
                    binding.refresh.isRefreshing = false
                    binding.progressBar.visibility = View.GONE
                    binding.progressBar.animation = null
                    binding.content.visibility = View.VISIBLE
                    setBackgroundColor(it.data?.current?.condition?.text, it.data?.current?.isDay)
                    binding.apply {
                        town.text = it.data?.location?.name
                        textCurrentWeather.text = it.data?.current?.condition?.text
                        Glide.with(requireContext())
                            .load("https:${it.data?.current?.condition?.icon}")
                            .into(currentWeatherImage)
                        val calendar = Calendar.getInstance()
                        val date = it.data?.location?.localtime?.take(10)?.split("-")?.map {
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

    private fun refreshView(forecastWeather: ForecastWeather?, deg: Temperature) {
        daysAdapter.differ.submitList(forecastWeather?.forecast?.forecastday?.map { day ->
            day.toDayWeather(deg)
        })
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val hoursThisDay =
            forecastWeather?.forecast?.forecastday?.get(0)?.hour?.takeLast(24 - currentHour)
        val hoursNextDay = forecastWeather?.forecast?.forecastday?.get(1)?.hour?.take(currentHour)
        val hours = mutableListOf<Hour?>()
        if (hoursThisDay != null && hoursNextDay != null) {
            hours.addAll(hoursThisDay)
            hours.addAll(hoursNextDay)
        }
        hoursAdapter.differ.submitList(hours.map { hour ->
            hour?.toHourWeather(deg)
        })
        binding.apply {
            currentDegrees.text =
                if (deg == Temperature.CELSIUS) "${
                    forecastWeather?.current?.tempC?.toInt().toString()
                }\u00B0" else "${forecastWeather?.current?.tempF?.toInt().toString()}\u00B0"
            highestCurrentDegrees.text =
                if (deg == Temperature.CELSIUS) forecastWeather?.forecast?.forecastday?.get(0)?.day?.maxtempC?.toInt()
                    .toString() else forecastWeather?.forecast?.forecastday?.get(
                    0
                )?.day?.maxtempF?.toInt().toString()
            lowestCurrentDegrees.text =
                if (deg == Temperature.CELSIUS) forecastWeather?.forecast?.forecastday?.get(0)?.day?.mintempC?.toInt()
                    .toString() else forecastWeather?.forecast?.forecastday?.get(
                    0
                )?.day?.mintempF?.toInt().toString()
            feelsLike.text =
                if (deg == Temperature.CELSIUS) requireContext().resources.getString(
                    com.example.weatherapp.R.string.feels_like,
                    forecastWeather?.current?.feelslikeC?.toInt()
                ) else requireContext().resources.getString(
                    R.string.feels_like,
                    forecastWeather?.current?.feelslikeF?.toInt()
                )
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