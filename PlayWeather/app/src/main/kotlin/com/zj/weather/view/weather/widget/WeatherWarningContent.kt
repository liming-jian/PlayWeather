package com.zj.weather.view.weather.widget

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zj.model.weather.WeatherWarningBean

@Composable
fun WeatherWarningContent(warningList: List<WeatherWarningBean.WarningItem>?) {
    if (warningList.isNullOrEmpty()) return

    warningList.forEach { warning ->
        WeatherWarningItem(warning)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun WeatherWarningItem(warning: WeatherWarningBean.WarningItem) {
    var expanded by remember { mutableStateOf(false) }
    val warningColor = getWarningColor(warning.severityColor, warning.level)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = warningColor.copy(alpha = 0.25f),
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = warningColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = warning.title ?: (warning.typeName ?: "气象预警"),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
            if (expanded && !warning.text.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = warning.text!!,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

private fun getWarningColor(severityColor: String?, level: String?): Color {
    return when (severityColor?.lowercase() ?: level?.lowercase()) {
        "red", "红色" -> Color(0xFFE53935)
        "orange", "橙色" -> Color(0xFFFF9800)
        "yellow", "黄色" -> Color(0xFFFDD835)
        "blue", "蓝色" -> Color(0xFF42A5F5)
        "white", "白色" -> Color(0xFFEEEEEE)
        "green", "绿色" -> Color(0xFF66BB6A)
        else -> Color(0xFFFF9800)
    }
}
