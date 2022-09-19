package com.example.weatherapp.data.remote.dto


import com.google.gson.annotations.SerializedName

data class Condition(
    @SerializedName("icon")
    val icon: String,
    @SerializedName("text")
    val text: String
)