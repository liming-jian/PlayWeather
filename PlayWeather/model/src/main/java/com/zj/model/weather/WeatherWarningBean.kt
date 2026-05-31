package com.zj.model.weather

import com.zj.model.Refer

data class WeatherWarningBean(
    val code: String? = null,
    val refer: Refer? = null,
    val updateTime: String? = null,
    val warning: List<WarningItem> = arrayListOf()
) {

    data class WarningItem(
        val id: String? = null,
        val sender: String? = null,
        val pubTime: String? = null,
        val title: String? = null,
        val startTime: String? = null,
        val endTime: String? = null,
        val status: String? = null,
        val level: String? = null,
        val severity: String? = null,
        val severityColor: String? = null,
        val type: String? = null,
        val typeName: String? = null,
        val urgency: String? = null,
        val certainty: String? = null,
        val text: String? = null,
        val related: String? = null,
    )
}
