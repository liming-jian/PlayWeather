package com.zj.weather.view.city.viewmodel

import android.app.Application
import com.zj.model.SUCCESSFUL
import com.zj.model.room.PlayWeatherDatabase
import com.zj.model.room.entity.CityInfo
import com.zj.network.PlayWeatherNetwork
import com.zj.utils.XLog
import com.zj.utils.weather.getLocationForCityInfo
import com.zj.utils.weather.isDaytime
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class CityListRepository @Inject constructor(private val context: Application) {

    private val cityInfoDao = PlayWeatherDatabase.getDatabase(context = context).cityInfoDao()
    private val network = PlayWeatherNetwork(context.applicationContext)

    fun refreshCityList(): Flow<List<CityInfo>> = cityInfoDao.getCityInfoList()

    suspend fun deleteCityInfo(cityInfo: CityInfo) {
        cityInfoDao.delete(cityInfo)
    }

    suspend fun getCityWeather(cityInfo: CityInfo): CityWeatherInfo {
        return try {
            val location = getLocationForCityInfo(cityInfo)
            val weatherNow = network.getWeatherNow(location)
            val weather7Day = network.getWeather7Day(location)
            val nowCode = weatherNow.code?.toIntOrNull()
            val dayCode = weather7Day.code?.toIntOrNull()
            val nowBean = if (nowCode == SUCCESSFUL) weatherNow.now else null
            val todayBean = if (dayCode == SUCCESSFUL) weather7Day.daily.firstOrNull() else null
            val warningText = try {
                val warning = network.getWeatherWarning(location)
                if (warning.code?.toIntOrNull() == SUCCESSFUL && warning.warning.isNotEmpty()) {
                    warning.warning.first().title
                } else null
            } catch (_: Exception) { null }
            CityWeatherInfo(
                cityInfo = cityInfo,
                temp = nowBean?.temp,
                weatherText = nowBean?.text,
                weatherIcon = nowBean?.icon,
                tempMax = todayBean?.tempMax,
                tempMin = todayBean?.tempMin,
                isDay = isDaytime(nowBean?.obsTime, todayBean?.sunrise, todayBean?.sunset),
                warningText = warningText
            )
        } catch (e: Exception) {
            XLog.w("getCityWeather error: ${e.message}")
            CityWeatherInfo(cityInfo = cityInfo)
        }
    }
}