package com.zj.network

import android.content.Context
import com.zj.model.Lang
import com.zj.model.air.AirNowBean
import com.zj.model.air.OpenMeteoAirBean
import com.zj.model.city.GeoBean
import com.zj.model.city.TopGeoBean
import com.zj.model.indices.WeatherLifeIndicesBean
import com.zj.model.weather.WeatherDailyBean
import com.zj.model.weather.WeatherHourlyBean
import com.zj.model.weather.WeatherNowBean
import com.zj.model.weather.WeatherWarningBean
import com.zj.network.service.AirNowService
import com.zj.network.service.CityLookupService
import com.zj.network.service.CityWeatherService
import com.zj.network.service.OpenMeteoAirService
import com.zj.network.service.WeatherLifeIndicesService
import com.zj.utils.XLog
import com.zj.utils.getDefaultLocale

class PlayWeatherNetwork(private val context: Context) {

    private var language: Lang = context.getDefaultLocale()

    private val airNowService = ServiceCreator.create(AirNowService::class.java)

    suspend fun getWeatherLifeIndicesBean(location: String): WeatherLifeIndicesBean =
        airNowService.getWeatherLifeIndicesBean(
            location = location,
            lang = language.code,
            type = "1,2,3,5,6,9"
        )

    private val cityLookupService = ServiceCreator.createCity(CityLookupService::class.java)

    suspend fun getCityLookup(location: String): GeoBean {
        XLog.w(context.packageName)
        return cityLookupService.getCityLookup(location = location, lang = language.code)
    }

    suspend fun getCityTop(): TopGeoBean {
        XLog.w(context.packageName)
        return cityLookupService.getCityTop(lang = language.code)
    }

    private val cityWeatherService = ServiceCreator.create(CityWeatherService::class.java)

    suspend fun getWeatherNow(location: String): WeatherNowBean =
        cityWeatherService.getWeatherNow(location = location, lang = language.code)

    suspend fun getWeather24Hour(location: String): WeatherHourlyBean =
        cityWeatherService.getWeather24Hour(location = location, lang = language.code)

    suspend fun getWeather3Day(location: String): WeatherDailyBean =
        cityWeatherService.getWeather3Day(location = location, lang = language.code)

    suspend fun getWeather7Day(location: String): WeatherDailyBean =
        cityWeatherService.getWeather7Day(location = location, lang = language.code)

    suspend fun getWeatherWarning(location: String): WeatherWarningBean =
        cityWeatherService.getWeatherWarning(location = location, lang = language.code)

    private val weatherLifeIndicesService =
        ServiceCreator.create(WeatherLifeIndicesService::class.java)

    suspend fun getAirNowBean(location: String): AirNowBean =
        weatherLifeIndicesService.getAirNowBean(location = location, lang = language.code)

    private val openMeteoAirService = ServiceCreator.createOpenMeteo(OpenMeteoAirService::class.java)

    suspend fun getOpenMeteoAir(lat: String, lon: String): OpenMeteoAirBean =
        openMeteoAirService.getAirQuality(latitude = lat, longitude = lon)

}