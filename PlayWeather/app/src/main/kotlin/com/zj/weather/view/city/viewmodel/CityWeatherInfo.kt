package com.zj.weather.view.city.viewmodel

import com.zj.model.room.entity.CityInfo

data class CityWeatherInfo(
    val cityInfo: CityInfo,
    val temp: String? = null,
    val weatherText: String? = null,
    val weatherIcon: String? = null,
    val tempMax: String? = null,
    val tempMin: String? = null,
    val isDay: Boolean = true,
    val warningText: String? = null,
)
