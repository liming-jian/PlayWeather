package com.zj.weather.view.weather.viewmodel

import com.zj.model.indices.WeatherLifeIndicesBean
import com.zj.model.weather.WeatherNowBean

/**
 * 海外城市生活指数本地推算兜底。
 * 和风免费版海外只返 type 1/2/5/9，缺失项（穿衣 3、旅游 6）用当前天气近似推算。
 * 已存在的 type 不会被覆盖。
 */
object LifeIndicesFallback {

    fun fillMissing(
        original: List<WeatherLifeIndicesBean.WeatherLifeIndicesItem>,
        weatherNow: WeatherNowBean.NowBaseBean?,
    ): List<WeatherLifeIndicesBean.WeatherLifeIndicesItem> {
        val have = original.mapNotNull { it.type }.toHashSet()
        val temp = weatherNow?.temp?.toIntOrNull()
        val text = weatherNow?.text ?: ""
        val merged = original.toMutableList()

        if ("1" !in have) merged += build("1", "运动指数", sportCategory(temp, text))
        if ("2" !in have) merged += build("2", "洗车指数", carCategory(text))
        if ("3" !in have) merged += build("3", "穿衣指数", clothesCategory(temp))
        if ("5" !in have) merged += build("5", "紫外线指数", uvCategory(text))
        if ("6" !in have) merged += build("6", "旅游指数", travelCategory(temp, text))
        if ("9" !in have) merged += build("9", "感冒指数", coldCategory(temp, text))
        return merged
    }

    private fun build(type: String, name: String, category: String) =
        WeatherLifeIndicesBean.WeatherLifeIndicesItem(
            date = null, level = null, name = name,
            text = category, type = type, category = category,
        )

    private fun clothesCategory(temp: Int?): String = when {
        temp == null -> "适宜"
        temp >= 28 -> "炎热"
        temp >= 22 -> "舒适"
        temp >= 16 -> "较舒适"
        temp >= 8 -> "较冷"
        temp >= 0 -> "冷"
        else -> "寒冷"
    }

    private fun travelCategory(temp: Int?, text: String): String {
        val bad = text.containsAny("雨", "雪", "雾", "霾", "沙尘", "暴")
        if (bad) return "较不宜"
        if (temp == null) return "适宜"
        return when {
            temp in 15..28 -> "适宜"
            temp in 8..14 || temp in 29..32 -> "较适宜"
            else -> "较不宜"
        }
    }

    private fun coldCategory(temp: Int?, text: String): String {
        if (text.containsAny("雨", "雪")) return "较易发"
        if (temp == null) return "少发"
        return when {
            temp <= 0 -> "易发"
            temp in 1..10 -> "较易发"
            temp in 11..28 -> "少发"
            else -> "较易发"
        }
    }

    private fun sportCategory(temp: Int?, text: String): String {
        if (text.containsAny("雨", "雪", "雾", "霾", "暴")) return "较不宜"
        if (temp == null) return "适宜"
        return when {
            temp in 12..28 -> "适宜"
            temp in 5..11 || temp in 29..32 -> "较适宜"
            else -> "较不宜"
        }
    }

    private fun carCategory(text: String): String =
        if (text.containsAny("雨", "雪", "沙尘", "霾")) "不宜" else "适宜"

    private fun uvCategory(text: String): String = when {
        text.containsAny("晴") -> "强"
        text.containsAny("多云") -> "中等"
        text.containsAny("阴", "雨", "雪", "雾", "霾") -> "弱"
        else -> "中等"
    }

    private fun String.containsAny(vararg keys: String): Boolean =
        keys.any { this.contains(it) }
}
