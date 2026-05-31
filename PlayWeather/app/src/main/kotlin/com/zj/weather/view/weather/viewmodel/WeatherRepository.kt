package com.zj.weather.view.weather.viewmodel

import android.app.Application
import android.location.Address
import android.location.Location
import com.zj.model.SUCCESSFUL
import com.zj.model.air.AirNowBean
import com.zj.model.getErrorText
import com.zj.model.indices.WeatherLifeIndicesBean
import com.zj.model.room.PlayWeatherDatabase
import com.zj.model.room.entity.CityInfo
import com.zj.model.weather.WeatherDailyBean
import com.zj.model.weather.WeatherHourlyBean
import com.zj.model.weather.WeatherNowBean
import com.zj.model.weather.WeatherWarningBean
import com.zj.network.PlayWeatherNetwork
import com.zj.utils.XLog
import com.zj.utils.view.showToast
import com.zj.utils.weather.getDateWeekName
import com.zj.utils.weather.getTimeName
import com.zj.utils.weather.getTodayBean
import com.zj.weather.R
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import kotlin.math.abs

@ViewModelScoped
class WeatherRepository @Inject constructor(private val context: Application) {

    private val cityInfoDao = PlayWeatherDatabase.getDatabase(context).cityInfoDao()
    private val network = PlayWeatherNetwork(context.applicationContext)

    /**
     * 获取现在的天气
     *
     * @param location 位置 可能是地点、或者是地点id
     */
    suspend fun getWeatherNow(location: String): WeatherNowBean.NowBaseBean? {
        return try {
            val weatherNow = network.getWeatherNow(location)
            val code = weatherNow.code?.toIntOrNull()
            if (code == SUCCESSFUL) {
                weatherNow.now
            } else {
                val text = getErrorText(code)
                showToast(context, text)
                XLog.w("code:$code, text:$text")
                null
            }
        } catch (e: Exception) {
            XLog.w("getWeatherNow error: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取未来24小时的天气
     *
     * @param location 位置 可能是地点、或者是地点id
     */
    suspend fun getWeather24Hour(location: String, weatherNow: WeatherNowBean.NowBaseBean? = null): List<WeatherHourlyBean.HourlyBean> {
        return try {
            val weather24Hour = network.getWeather24Hour(location)
            val code = weather24Hour.code?.toIntOrNull()
            if (code == SUCCESSFUL) {
                weather24Hour.hourly.forEachIndexed { index, hourlyBean ->
                    hourlyBean.fxTime = getTimeName(context, hourlyBean.fxTime, index)
                }
                val result = ArrayList<WeatherHourlyBean.HourlyBean>()
                val nowItem = WeatherHourlyBean.HourlyBean()
                nowItem.fxTime = context.getString(com.zj.utils.R.string.time_now)
                nowItem.temp = weatherNow?.temp ?: weather24Hour.hourly.firstOrNull()?.temp
                nowItem.icon = weatherNow?.icon ?: weather24Hour.hourly.firstOrNull()?.icon
                result.add(nowItem)
                result.addAll(weather24Hour.hourly)
                result
            } else {
                val text = getErrorText(code)
                showToast(context, text)
                XLog.w("code:$code, text:$text")
                arrayListOf()
            }
        } catch (e: Exception) {
            XLog.w("getWeather24Hour error: ${e.message}")
            e.printStackTrace()
            arrayListOf()
        }
    }

    /**
     * 获取未来7天的天气
     *
     * @param location 位置 可能是地点、或者是地点id
     */
    suspend fun getWeather7Day(location: String): Pair<WeatherDailyBean.DailyBean?, List<WeatherDailyBean.DailyBean>?>? {
        return try {
            val weather7Day = network.getWeather7Day(location)
            val code = weather7Day.code?.toIntOrNull()
            if (code == SUCCESSFUL) {
                val dailyBean = getTodayBean(weather7Day.daily)
                weather7Day.daily.forEach { daily ->
                    daily.fxDate =
                        getDateWeekName(context, daily.fxDate)
                }
                Pair(dailyBean, weather7Day.daily)
            } else {
                val text = getErrorText(code)
                showToast(context, text)
                XLog.w("code:$code, text:$text")
                null
            }
        } catch (e: Exception) {
            XLog.w("getWeather7Day error: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取对应位置的空气质量
     *
     * @param location 位置 可能是地点、或者是地点id
     */
    /**
     * 获取对应位置的空气质量。和风免费版不支持海外，失败时自动回退 Open-Meteo。
     *
     * @param location 和风的 locationId 或经纬度
     * @param cityInfo 用于回退取经纬度
     */
    suspend fun getAirNow(location: String, cityInfo: CityInfo? = null): AirNowBean.NowBean? {
        return try {
            val airNowBean = network.getAirNowBean(location)
            val code = airNowBean.code?.toIntOrNull()
            if (code == SUCCESSFUL) {
                airNowBean.now.primary = if (airNowBean.now.primary == "NA") "" else {
                    "${context.getString(R.string.air_quality_warn)}${airNowBean.now.primary}"
                }
                airNowBean.now
            } else {
                XLog.w("getAirNow QWeather code:$code, fallback to Open-Meteo")
                getAirNowFromOpenMeteo(cityInfo)
            }
        } catch (e: Exception) {
            XLog.w("getAirNow QWeather error: ${e.message}, fallback to Open-Meteo")
            getAirNowFromOpenMeteo(cityInfo)
        }
    }

    private suspend fun getAirNowFromOpenMeteo(cityInfo: CityInfo?): AirNowBean.NowBean? {
        val lat = cityInfo?.lat?.takeIf { it.isNotBlank() }
        val lon = cityInfo?.lon?.takeIf { it.isNotBlank() }
        if (lat == null || lon == null) {
            XLog.w("Open-Meteo fallback: missing lat/lon")
            return AirNowBean.NowBean(aqi = "--", category = "暂无数据", primary = "")
        }
        return try {
            val data = network.getOpenMeteoAir(lat, lon).current
                ?: return AirNowBean.NowBean(aqi = "--", category = "暂无数据", primary = "")
            val aqi = data.us_aqi?.toInt() ?: -1
            AirNowBean.NowBean(
                aqi = if (aqi >= 0) aqi.toString() else "--",
                category = aqiToCategory(aqi),
                pm2p5 = data.pm2_5?.toInt()?.toString(),
                pm10 = data.pm10?.toInt()?.toString(),
                no2 = data.nitrogen_dioxide?.toInt()?.toString(),
                so2 = data.sulphur_dioxide?.toInt()?.toString(),
                co = data.carbon_monoxide?.toInt()?.toString(),
                o3 = data.ozone?.toInt()?.toString(),
                primary = "",
                level = aqiToLevel(aqi),
            )
        } catch (e: Exception) {
            XLog.w("Open-Meteo fallback error: ${e.message}")
            AirNowBean.NowBean(aqi = "--", category = "暂无数据", primary = "")
        }
    }

    private fun aqiToCategory(aqi: Int): String = when {
        aqi < 0 -> "暂无数据"
        aqi <= 50 -> "优"
        aqi <= 100 -> "良"
        aqi <= 150 -> "轻度污染"
        aqi <= 200 -> "中度污染"
        aqi <= 300 -> "重度污染"
        else -> "严重污染"
    }

    private fun aqiToLevel(aqi: Int): String = when {
        aqi < 0 -> "0"
        aqi <= 50 -> "1"
        aqi <= 100 -> "2"
        aqi <= 150 -> "3"
        aqi <= 200 -> "4"
        aqi <= 300 -> "5"
        else -> "6"
    }

    /**
     * 获取对应位置的生活指数
     *
     * @param location 位置 可能是地点、或者是地点id
     */
    suspend fun getWeatherLifeIndicesList(location: String): List<WeatherLifeIndicesBean.WeatherLifeIndicesItem> {
        return try {
            val weatherLifeIndicesBean = network.getWeatherLifeIndicesBean(location)
            val code = weatherLifeIndicesBean.code?.toIntOrNull()
            if (code == SUCCESSFUL) {
                weatherLifeIndicesBean.daily ?: arrayListOf()
            } else {
                val text = getErrorText(code)
                showToast(context, text)
                XLog.w("code:$code, text:$text")
                arrayListOf()
            }
        } catch (e: Exception) {
            XLog.w("getWeatherLifeIndicesList error: ${e.message}")
            e.printStackTrace()
            arrayListOf()
        }
    }

    /**
     * 修改当前的位置信息
     *
     * @param location 位置
     * @param result Address
     */
    suspend fun updateCityInfo(
        location: Location,
        result: MutableList<Address>,
    ) {
        if (result.isEmpty()) return
        val address = result[0]
        val isLocationList = cityInfoDao.getIsLocationList()
        val cityInfo = buildCityInfo(location, address)
        XLog.w("updateCityInfo: address:${address}")
        if (isLocationList.isNotEmpty()) {
            cityInfo.uid = isLocationList[0].uid
            if (cityInfo == isLocationList[0]) {
                XLog.w("updateCityInfo: The need to modify:${cityInfo.uid}  ")
            } else {
                XLog.w("updateCityInfo: Need to modify:${cityInfo.uid}")
                cityInfoDao.update(cityInfo)
            }
        } else {
            cityInfoDao.insert(cityInfo)
            XLog.w("updateCityInfo: Need to add")
        }
    }

    private fun buildCityInfo(
        location: Location,
        address: Address
    ): CityInfo {
        return CityInfo(
            location = "${abs(location.longitude)},${
                abs(location.latitude)
            }",
            lat = abs(location.latitude).toString(),
            lon = abs(location.longitude).toString(),
            name = address.subLocality ?: "",
            isLocation = 1,
            province = address.adminArea,
            city = address.locality
        )
    }

    fun refreshCityList() = cityInfoDao.getCityInfoList()

    suspend fun getWeatherWarning(location: String): List<WeatherWarningBean.WarningItem> {
        return try {
            val warningBean = network.getWeatherWarning(location)
            val code = warningBean.code?.toIntOrNull()
            if (code == SUCCESSFUL) {
                warningBean.warning
            } else {
                XLog.w("getWeatherWarning code:$code")
                arrayListOf()
            }
        } catch (e: Exception) {
            XLog.w("getWeatherWarning error: ${e.message}")
            arrayListOf()
        }
    }

}