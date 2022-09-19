package com.example.weatherapp.presentation.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.databinding.HourWeatherItemBinding
import com.example.weatherapp.domain.models.HourWeather

class HoursAdapter(
    private val c: Context
) : RecyclerView.Adapter<HoursAdapter.HourViewHolder>() {

    inner class HourViewHolder(private val binding: HourWeatherItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HourWeather) {
            binding.apply {
                time.text = item.time
                degrees.text = c.resources.getString(R.string.degree, item.degrees)
                Glide.with(root.context)
                    .load(item.icon)
                    .into(weatherIcon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        return HourViewHolder(
            HourWeatherItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount() = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<HourWeather>() {
        override fun areItemsTheSame(oldItem: HourWeather, newItem: HourWeather): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: HourWeather, newItem: HourWeather): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}