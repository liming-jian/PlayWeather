package com.zj.weather.view.city.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zj.model.room.entity.CityInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityListViewModel @Inject constructor(
    application: Application,
    private val cityListRepository: CityListRepository
) : AndroidViewModel(application) {

    val cityInfoList: Flow<List<CityInfo>> = cityListRepository.refreshCityList()

    private val _cityWeatherList = MutableStateFlow<List<CityWeatherInfo>>(emptyList())
    val cityWeatherList: StateFlow<List<CityWeatherInfo>> = _cityWeatherList.asStateFlow()

    fun loadCityWeathers(cityList: List<CityInfo>) {
        viewModelScope.launch(Dispatchers.IO) {
            val results = cityList.map { city ->
                async { cityListRepository.getCityWeather(city) }
            }.awaitAll()
            _cityWeatherList.value = results
        }
    }

    fun deleteCityInfo(cityInfo: CityInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            cityListRepository.deleteCityInfo(cityInfo)
        }
    }

}