package com.zj.utils.weather

import android.content.Context
import com.zj.utils.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.max
import kotlin.math.min

private val weekFormat: ThreadLocal<SimpleDateFormat> = object : ThreadLocal<SimpleDateFormat>() {
    override fun initialValue(): SimpleDateFormat = SimpleDateFormat("E", Locale.getDefault())
}

private val hourFormat: ThreadLocal<SimpleDateFormat> = object : ThreadLocal<SimpleDateFormat>() {
    override fun initialValue(): SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX", Locale.getDefault())
}

/**
 * 获取指定年月日的周几
 *
 * @param date 年月日 2013-12-30
 * @return 周几
 */
fun getDateWeekName(context: Context, date: String?): String {
    if (date.isNullOrEmpty()) return context.getString(R.string.time_today)
    return try {
        val dateArray = date.split("-")
        if (dateArray.size < 3) return date
        val year = dateArray[0].toIntOrNull() ?: return date
        val month = dateArray[1].toIntOrNull() ?: return date
        val day = dateArray[2].toIntOrNull() ?: return date
        val today = Calendar.getInstance()
        val todayWeek = today.get(Calendar.DAY_OF_WEEK)
        val target = Calendar.getInstance()
        target.clear()
        target.set(year, month - 1, day)
        val week = target.get(Calendar.DAY_OF_WEEK)
        if (todayWeek == week) {
            context.getString(R.string.time_today)
        } else {
            weekFormat.get()?.format(Date(target.timeInMillis)) ?: date
        }
    } catch (e: Exception) {
        e.printStackTrace()
        context.getString(R.string.time_today)
    }
}

/**
 * 获取指定时间为几点
 *
 * @param time 年月日 2013-12-30T13:00+08:00
 * @return 13时
 */
fun getTimeName(context: Context, time: String?, index: Int = 0): String =
    getHourlyTimeName(
        time = time,
        index = index,
        nowText = context.getString(R.string.time_now),
        hourText = context.getString(R.string.time_hour)
    )

fun getHourlyTimeName(time: String?, index: Int, nowText: String, hourText: String): String {
    if (time.isNullOrEmpty()) return if (index == 0) nowText else ""
    return try {
        "${getLocalHour(time)}$hourText"
    } catch (e: Exception) {
        e.printStackTrace()
        if (index == 0) nowText else ""
    }
}

fun getLocalHour(time: String): Int {
    val parsed = hourFormat.get()?.parse(time) ?: return 0
    val calendar = calendarForOffset(time)
    calendar.time = parsed
    return calendar.get(Calendar.HOUR_OF_DAY)
}

fun getLocalMinutes(time: String?): Int {
    if (time.isNullOrEmpty()) return localNowMinutes()
    return try {
        val parsed = hourFormat.get()?.parse(time) ?: return localNowMinutes()
        val calendar = calendarForOffset(time)
        calendar.time = parsed
        calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    } catch (e: Exception) {
        localNowMinutes()
    }
}

fun isDaytime(nowTime: String?, sunrise: String?, sunset: String?): Boolean {
    if (sunrise.isNullOrEmpty() || sunset.isNullOrEmpty()) return true
    val nowMinutes = getLocalMinutes(nowTime)
    return nowMinutes in getMinutes(sunrise)..getMinutes(sunset)
}

fun getSunProgress(nowTime: String?, sunrise: String?, sunset: String?): Float {
    if (sunrise.isNullOrEmpty() || sunset.isNullOrEmpty()) return 0.5f
    val sunriseMinutes = getMinutes(sunrise)
    val sunsetMinutes = getMinutes(sunset)
    val daylightMinutes = sunsetMinutes - sunriseMinutes
    if (daylightMinutes <= 0) return 0.5f
    val accounted = (getLocalMinutes(nowTime).toFloat() - sunriseMinutes) / daylightMinutes
    return min(1f, max(0f, accounted))
}

fun getMinutes(time: String): Int {
    val hour = time.substring(0, 2).toInt()
    val minutes = time.substring(3, 5).toInt()
    return hour * 60 + minutes
}

private fun calendarForOffset(time: String): Calendar {
    val offset = time.takeLast(6)
    return Calendar.getInstance(TimeZone.getTimeZone("GMT$offset"))
}

private fun localNowMinutes(): Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
}
