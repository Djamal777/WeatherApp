package com.example.weatherapp.other

import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation

val anim = RotateAnimation(
    0.0f,
    360.0f,
    Animation.RELATIVE_TO_SELF,
    0.5f,
    Animation.RELATIVE_TO_SELF,
    0.5f
).apply {
    interpolator = LinearInterpolator()
    repeatCount = Animation.INFINITE
    duration = 700
}