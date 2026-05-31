package com.zj.model.air

data class OpenMeteoAirBean(
    val current: Current? = null
) {
    data class Current(
        val us_aqi: Double? = null,
        val pm10: Double? = null,
        val pm2_5: Double? = null,
        val carbon_monoxide: Double? = null,
        val nitrogen_dioxide: Double? = null,
        val sulphur_dioxide: Double? = null,
        val ozone: Double? = null,
    )
}
