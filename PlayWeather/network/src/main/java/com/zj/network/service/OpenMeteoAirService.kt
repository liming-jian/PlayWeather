package com.zj.network.service

import com.zj.model.air.OpenMeteoAirBean
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Open-Meteo Air Quality API (free, no key, global coverage).
 * Used as a fallback when QWeather has no overseas AQI data.
 */
interface OpenMeteoAirService {

    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("current") current: String =
            "us_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone",
        @Query("timezone") timezone: String = "auto",
    ): OpenMeteoAirBean
}
