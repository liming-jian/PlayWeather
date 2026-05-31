package com.zj.weather.view.weather.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zj.model.weather.WeatherDailyBean
import com.zj.utils.view.ImageLoader
import com.zj.utils.weather.IconUtils
import com.zj.weather.R
import com.zj.weather.theme.WeatherCardBackground
import com.zui.animate.placeholder.placeholder

@Composable
fun WeekDayWeather(dayBeanList: List<WeatherDailyBean.DailyBean>?) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        backgroundColor = WeatherCardBackground,
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.seven_day_title),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 7.dp, start = 10.dp, end = 10.dp)
            )
            val dailyBeanList = if (dayBeanList.isNullOrEmpty()) {
                buildDayItemList()
            } else {
                dayBeanList
            }
            dailyBeanList.forEach { dailyBean ->
                Divider(modifier = Modifier.padding(horizontal = 10.dp), thickness = 0.4.dp)
                WeekDayWeatherItem(dailyBean)
            }
        }
    }
}

private fun buildDayItemList(): List<WeatherDailyBean.DailyBean?> {
    val dailyBeanArrayList: ArrayList<WeatherDailyBean.DailyBean?> = arrayListOf()
    for (index in 0..6) {
        dailyBeanArrayList.add(WeatherDailyBean.DailyBean())
    }
    return dailyBeanArrayList
}

@Composable
private fun WeekDayWeatherItem(dailyBean: WeatherDailyBean.DailyBean?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = dailyBean?.fxDate ?: "今天",
            modifier = Modifier
                .weight(1f)
                .padding(start = 3.dp)
                .placeholder(dailyBean?.fxDate),
            fontSize = 15.sp,
            color = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.weight(0.7f))

        ImageLoader(
            data = IconUtils.getWeatherIcon(dailyBean?.iconDay),
            modifier = Modifier
                .padding(start = 7.dp)
                .placeholder(dailyBean?.iconDay)
        )

        Spacer(modifier = Modifier.weight(0.7f))

        Text(
            text = "${dailyBean?.tempMin ?: "0"}°",
            modifier = Modifier
                .width(50.dp)
                .padding(end = 15.dp)
                .placeholder(dailyBean?.tempMin),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary
        )
        TemperatureChart(
            Modifier.weight(2f),
            dailyBean?.weekMin ?: -20,
            dailyBean?.weekMax ?: 40,
            dailyBean?.tempMin?.toInt() ?: -20,
            dailyBean?.tempMax?.toInt() ?: 40,
            dailyBean?.temp ?: 20
        )
        Text(
            text = "${dailyBean?.tempMax ?: "0"}°",
            modifier = Modifier
                .width(50.dp)
                .padding(start = 10.dp, end = 5.dp)
                .placeholder(dailyBean?.tempMax),
            fontSize = 15.sp,
            textAlign = TextAlign.End,
            color = MaterialTheme.colors.primary
        )
    }
}

/**
 * 气温条状图
 *
 * @param min 未来几天最低温度
 * @param max 未来几天最高温度
 * @param currentMin 这一天的最低温度
 * @param currentMax 这一天的最高温度
 * @param currentTemperature 当天当前温度
 */
@Composable
private fun TemperatureChart(
    modifier: Modifier,
    min: Int,
    max: Int,
    currentMin: Int,
    currentMax: Int,
    currentTemperature: Int = -1
) {
    val currentMinColor: Color = getTemperatureColor(currentMin)
    val currentMaxColor: Color = getTemperatureColor(currentMax)
    val num = max - min
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
            .padding(horizontal = 2.dp)
    ) {
        val centerY = size.height / 2f
        drawLine(
            color = Color(0x40FFFFFF),
            start = Offset(0f, centerY),
            end = Offset(size.width, centerY),
            strokeWidth = 10f,
            cap = StrokeCap.Round,
        )
        if (num > 0) {
            val startX = size.width / num * (currentMin - min)
            val endX = size.width / num * (currentMax - min)
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(currentMinColor, currentMaxColor),
                    start = Offset(startX, centerY),
                    end = Offset(endX, centerY)
                ),
                start = Offset(startX, centerY),
                end = Offset(endX, centerY),
                strokeWidth = 10f,
                cap = StrokeCap.Round,
            )
            if (currentTemperature > -100) {
                drawPoints(
                    points = listOf(Offset(size.width / num * (currentTemperature - min), centerY)),
                    pointMode = PointMode.Points,
                    color = Color.White,
                    strokeWidth = 12f,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

/**
 * 获取不同气温的颜色值，参考iOS天气app的温度渐变配色
 */
private fun getTemperatureColor(temperature: Int): Color {
    return when {
        temperature < -30 -> Color(0xFF1A3CB4)
        temperature < -20 -> Color(0xFF2050D2)
        temperature < -10 -> Color(0xFF3078EB)
        temperature < -5 -> Color(0xFF4096F5)
        temperature < 0 -> Color(0xFF5AB4FA)
        temperature < 5 -> Color(0xFF6EC8E0)
        temperature < 10 -> Color(0xFF82D7DC)
        temperature < 15 -> Color(0xFF50C382)
        temperature < 20 -> Color(0xFF78D250)
        temperature < 25 -> Color(0xFFC8D232)
        temperature < 30 -> Color(0xFFF0B428)
        temperature < 35 -> Color(0xFFFA821E)
        temperature < 40 -> Color(0xFFF05014)
        else -> Color(0xFFC8200F)
    }
}

@Preview(showBackground = false, name = "天item")
@Composable
fun DayWeatherItemPreview() {
    val dailyBean = WeatherDailyBean.DailyBean()
    dailyBean.fxDate = "周一"
    dailyBean.iconDay = "100"
    dailyBean.tempMin = "10"
    dailyBean.tempMax = "20"
    WeekDayWeatherItem(dailyBean = dailyBean)
}
