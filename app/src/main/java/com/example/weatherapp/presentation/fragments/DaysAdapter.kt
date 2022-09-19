package com.example.weatherapp.presentation.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.DayWeatherItemBinding
import com.example.weatherapp.domain.models.DayWeather
import java.util.*

class DaysAdapter(
    private val c: Context
) : RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {

    inner class DayViewHolder(private val binding: DayWeatherItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DayWeather) {
            binding.apply {
                val calendar = Calendar.getInstance()
                val date = item.date.split("-").map {
                    it.toInt()
                }
                calendar.set(date[0], date[1] - 1, date[2])
                if (date[2] == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                    dayOfWeek.text = "Today"
                } else {
                    dayOfWeek.text = calendar.getDisplayName(
                        Calendar.DAY_OF_WEEK,
                        Calendar.LONG,
                        Locale.US
                    )
                }
                highestTemperature.text = item.maxDegree.toString()
                lowestTemperature.text = item.minDegree.toString()
                Glide.with(root.context)
                    .load(item.icon)
                    .into(weatherIcon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysAdapter.DayViewHolder {
        return DayViewHolder(
            DayWeatherItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DaysAdapter.DayViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount() = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<DayWeather>() {
        override fun areItemsTheSame(oldItem: DayWeather, newItem: DayWeather): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: DayWeather, newItem: DayWeather): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}