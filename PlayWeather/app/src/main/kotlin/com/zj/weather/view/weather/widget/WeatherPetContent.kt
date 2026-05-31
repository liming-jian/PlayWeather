package com.zj.weather.view.weather.widget

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zj.model.weather.WeatherHourlyBean
import com.zj.weather.theme.WeatherCardBackground
import kotlin.math.sin

@Composable
fun WeatherPetContent(
    weatherIcon: String?,
    temp: Int?,
    @Suppress("UNUSED_PARAMETER") weatherText: String?,
    tempMin: Int? = null,
    tempMax: Int? = null,
    hourlyList: List<WeatherHourlyBean.HourlyBean> = emptyList()
) {
    val petState = getWeatherPetState(weatherIcon, temp, tempMin, tempMax, hourlyList)

    val transition = rememberInfiniteTransition(label = "pet")
    val bobAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse),
        label = "bob"
    )
    val accessoryAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "acc"
    )
    val blinkAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "blink"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        backgroundColor = WeatherCardBackground,
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(80.dp)) {
                drawAnimatedBear(petState, bobAnim, accessoryAnim, blinkAnim)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = petState.greeting,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = petState.tip,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}

private fun DrawScope.drawAnimatedBear(petState: PetState, bob: Float, accAnim: Float, blink: Float) {
    val centerX = size.width / 2f
    val bobY = sin(bob * Math.PI.toFloat()) * size.height * 0.02f
    val centerY = size.height / 2f + bobY
    val bodyColor = Color(0xFFFFC107)
    val earInnerColor = Color(0xFFFF9800)
    val noseColor = Color(0xFF5D4037)

    drawOval(
        color = bodyColor,
        topLeft = Offset(centerX - size.width * 0.25f, centerY + size.height * 0.12f),
        size = Size(size.width * 0.50f, size.height * 0.34f)
    )
    drawOutfit(petState.outfit, centerX, centerY)

    drawCircle(color = bodyColor, radius = size.width * 0.14f, center = Offset(centerX - size.width * 0.28f, centerY - size.height * 0.28f))
    drawCircle(color = earInnerColor, radius = size.width * 0.08f, center = Offset(centerX - size.width * 0.28f, centerY - size.height * 0.28f))
    drawCircle(color = bodyColor, radius = size.width * 0.14f, center = Offset(centerX + size.width * 0.28f, centerY - size.height * 0.28f))
    drawCircle(color = earInnerColor, radius = size.width * 0.08f, center = Offset(centerX + size.width * 0.28f, centerY - size.height * 0.28f))

    drawCircle(color = bodyColor, radius = size.width * 0.36f, center = Offset(centerX, centerY))
    drawCircle(color = Color(0xFFFFF3E0), radius = size.width * 0.22f, center = Offset(centerX, centerY + size.height * 0.05f))

    val isBlinking = blink > 0.92f
    if (isBlinking) {
        drawLine(noseColor, Offset(centerX - size.width * 0.14f, centerY - size.height * 0.06f), Offset(centerX - size.width * 0.08f, centerY - size.height * 0.06f), strokeWidth = size.width * 0.025f)
        drawLine(noseColor, Offset(centerX + size.width * 0.08f, centerY - size.height * 0.06f), Offset(centerX + size.width * 0.14f, centerY - size.height * 0.06f), strokeWidth = size.width * 0.025f)
    } else {
        drawCircle(color = noseColor, radius = size.width * 0.045f, center = Offset(centerX - size.width * 0.11f, centerY - size.height * 0.06f))
        drawCircle(color = noseColor, radius = size.width * 0.045f, center = Offset(centerX + size.width * 0.11f, centerY - size.height * 0.06f))
        drawCircle(color = Color.White, radius = size.width * 0.02f, center = Offset(centerX - size.width * 0.10f, centerY - size.height * 0.07f))
        drawCircle(color = Color.White, radius = size.width * 0.02f, center = Offset(centerX + size.width * 0.12f, centerY - size.height * 0.07f))
    }

    drawCircle(color = noseColor, radius = size.width * 0.05f, center = Offset(centerX, centerY + size.height * 0.04f))
    val smilePath = Path().apply {
        moveTo(centerX - size.width * 0.08f, centerY + size.height * 0.10f)
        quadraticBezierTo(centerX, centerY + size.height * 0.18f, centerX + size.width * 0.08f, centerY + size.height * 0.10f)
    }
    drawPath(smilePath, color = noseColor, style = Stroke(width = size.width * 0.02f))

    val blushAlpha = 0.2f + sin(bob * Math.PI.toFloat()) * 0.1f
    drawCircle(color = Color(0xFFFF8A80).copy(alpha = blushAlpha), radius = size.width * 0.06f, center = Offset(centerX - size.width * 0.22f, centerY + size.height * 0.06f))
    drawCircle(color = Color(0xFFFF8A80).copy(alpha = blushAlpha), radius = size.width * 0.06f, center = Offset(centerX + size.width * 0.22f, centerY + size.height * 0.06f))

    drawAnimatedAccessory(petState.accessory, centerX, centerY, accAnim, bob)
}

private fun DrawScope.drawOutfit(outfit: PetOutfit, cx: Float, cy: Float) {
    val top = cy + size.height * 0.14f
    val bodyWidth = size.width * 0.52f
    val bodyHeight = size.height * 0.34f
    when (outfit) {
        PetOutfit.T_SHIRT -> {
            drawRoundRect(color = Color(0xFF29B6F6), topLeft = Offset(cx - bodyWidth / 2, top), size = Size(bodyWidth, bodyHeight), cornerRadius = CornerRadius(size.width * 0.06f))
            drawOval(color = Color(0xFF29B6F6), topLeft = Offset(cx - size.width * 0.34f, top + size.height * 0.04f), size = Size(size.width * 0.18f, size.height * 0.12f))
            drawOval(color = Color(0xFF29B6F6), topLeft = Offset(cx + size.width * 0.16f, top + size.height * 0.04f), size = Size(size.width * 0.18f, size.height * 0.12f))
            drawRoundRect(color = Color.White.copy(alpha = 0.9f), topLeft = Offset(cx - size.width * 0.08f, top + size.height * 0.13f), size = Size(size.width * 0.16f, size.height * 0.035f), cornerRadius = CornerRadius(size.width * 0.02f))
        }
        PetOutfit.LIGHT_JACKET -> {
            drawRoundRect(color = Color(0xFFFFB74D), topLeft = Offset(cx - bodyWidth / 2, top), size = Size(bodyWidth, bodyHeight), cornerRadius = CornerRadius(size.width * 0.06f))
            drawRoundRect(color = Color(0xFFFFE0B2), topLeft = Offset(cx - size.width * 0.10f, top), size = Size(size.width * 0.20f, size.height * 0.08f), cornerRadius = CornerRadius(size.width * 0.03f))
            drawLine(Color(0xFF6D4C41), Offset(cx, top + size.height * 0.04f), Offset(cx, top + bodyHeight), strokeWidth = size.width * 0.016f)
            drawLine(Color.White.copy(alpha = 0.35f), Offset(cx - size.width * 0.18f, top + size.height * 0.18f), Offset(cx + size.width * 0.18f, top + size.height * 0.18f), strokeWidth = size.width * 0.012f)
        }
        PetOutfit.COAT -> {
            drawRoundRect(color = Color(0xFF5C6BC0), topLeft = Offset(cx - bodyWidth / 2, top - size.height * 0.01f), size = Size(bodyWidth, bodyHeight + size.height * 0.03f), cornerRadius = CornerRadius(size.width * 0.07f))
            drawRoundRect(color = Color(0xFF90CAF9), topLeft = Offset(cx - size.width * 0.14f, top - size.height * 0.02f), size = Size(size.width * 0.28f, size.height * 0.08f), cornerRadius = CornerRadius(size.width * 0.04f))
            drawLine(Color(0xFF303F9F), Offset(cx, top + size.height * 0.04f), Offset(cx, top + bodyHeight), strokeWidth = size.width * 0.018f)
            drawCircle(Color.White, radius = size.width * 0.018f, center = Offset(cx - size.width * 0.08f, top + size.height * 0.17f))
            drawCircle(Color.White, radius = size.width * 0.018f, center = Offset(cx + size.width * 0.08f, top + size.height * 0.17f))
        }
        PetOutfit.DOWN_JACKET -> {
            drawRoundRect(color = Color(0xFF37474F), topLeft = Offset(cx - bodyWidth / 2, top - size.height * 0.02f), size = Size(bodyWidth, bodyHeight + size.height * 0.05f), cornerRadius = CornerRadius(size.width * 0.08f))
            drawRoundRect(color = Color(0xFFFFF3E0), topLeft = Offset(cx - size.width * 0.16f, top - size.height * 0.04f), size = Size(size.width * 0.32f, size.height * 0.09f), cornerRadius = CornerRadius(size.width * 0.05f))
            for (i in 0..3) {
                val y = top + size.height * (0.07f + i * 0.065f)
                drawLine(Color.White.copy(alpha = 0.25f), Offset(cx - size.width * 0.22f, y), Offset(cx + size.width * 0.22f, y), strokeWidth = size.width * 0.012f)
            }
        }
        PetOutfit.NONE -> Unit
    }
}

private fun DrawScope.drawAnimatedAccessory(accessory: PetAccessory, cx: Float, cy: Float, acc: Float, bob: Float) {
    when (accessory) {
        PetAccessory.SUN_HAT -> {
            drawOval(color = Color(0xFFFF7043), topLeft = Offset(cx - size.width * 0.35f, cy - size.height * 0.42f), size = Size(size.width * 0.7f, size.height * 0.14f))
            drawRoundRect(color = Color(0xFFFF7043), topLeft = Offset(cx - size.width * 0.2f, cy - size.height * 0.50f), size = Size(size.width * 0.4f, size.height * 0.16f), cornerRadius = CornerRadius(size.width * 0.05f))
            drawRoundRect(color = Color(0xFF212121), topLeft = Offset(cx - size.width * 0.22f, cy - size.height * 0.10f), size = Size(size.width * 0.16f, size.height * 0.10f), cornerRadius = CornerRadius(size.width * 0.03f))
            drawRoundRect(color = Color(0xFF212121), topLeft = Offset(cx + size.width * 0.06f, cy - size.height * 0.10f), size = Size(size.width * 0.16f, size.height * 0.10f), cornerRadius = CornerRadius(size.width * 0.03f))
            drawLine(Color(0xFF212121), Offset(cx - size.width * 0.06f, cy - size.height * 0.05f), Offset(cx + size.width * 0.06f, cy - size.height * 0.05f), strokeWidth = size.width * 0.015f)
            val fanSwing = sin(acc * Math.PI.toFloat() * 2) * 0.08f
            val fanX = cx + size.width * 0.32f
            val fanY = cy + size.height * 0.10f + fanSwing * size.height
            drawOval(color = Color(0xFF81D4FA), topLeft = Offset(fanX - size.width * 0.10f, fanY - size.height * 0.08f), size = Size(size.width * 0.20f, size.height * 0.16f))
            drawLine(Color(0xFF5D4037), Offset(fanX, fanY + size.height * 0.08f), Offset(fanX, fanY + size.height * 0.18f), strokeWidth = size.width * 0.018f)
        }
        PetAccessory.UMBRELLA -> {
            val umbrellaSwing = sin(acc * Math.PI.toFloat()) * size.width * 0.02f
            val ux = cx + size.width * 0.28f + umbrellaSwing
            val umbrellaPath = Path().apply {
                moveTo(ux - size.width * 0.18f, cy - size.height * 0.38f)
                quadraticBezierTo(ux, cy - size.height * 0.58f, ux + size.width * 0.18f, cy - size.height * 0.38f)
                close()
            }
            drawPath(umbrellaPath, color = Color(0xFF42A5F5))
            drawLine(Color(0xFF5D4037), Offset(ux, cy - size.height * 0.38f), Offset(ux, cy + size.height * 0.15f), strokeWidth = size.width * 0.02f)
            for (i in 0..2) {
                val dropY = cy - size.height * 0.15f + (acc + i * 0.33f).rem(1f) * size.height * 0.5f
                val dropX = cx - size.width * 0.30f + i * size.width * 0.15f
                drawCircle(Color(0x9042A5F5), radius = size.width * 0.02f, center = Offset(dropX, dropY))
            }
        }
        PetAccessory.SCARF -> {
            val tailSwing = sin(acc * Math.PI.toFloat() * 2) * size.width * 0.03f
            drawRoundRect(color = Color(0xFFE53935), topLeft = Offset(cx - size.width * 0.25f, cy + size.height * 0.22f), size = Size(size.width * 0.50f, size.height * 0.10f), cornerRadius = CornerRadius(size.width * 0.03f))
            drawRoundRect(color = Color(0xFFE53935), topLeft = Offset(cx + size.width * 0.10f + tailSwing, cy + size.height * 0.28f), size = Size(size.width * 0.08f, size.height * 0.15f), cornerRadius = CornerRadius(size.width * 0.02f))
            drawOval(color = Color(0xFF1565C0), topLeft = Offset(cx - size.width * 0.18f, cy - size.height * 0.50f), size = Size(size.width * 0.36f, size.height * 0.24f))
            drawCircle(Color.White, radius = size.width * 0.03f, center = Offset(cx + size.width * 0.05f, cy - size.height * 0.44f))
            val shakeX = sin(bob * Math.PI.toFloat() * 3) * size.width * 0.01f
            drawOval(color = bodyColorConst, topLeft = Offset(cx - size.width * 0.10f + shakeX, cy + size.height * 0.15f), size = Size(size.width * 0.10f, size.height * 0.14f))
            drawOval(color = bodyColorConst, topLeft = Offset(cx + size.width * 0.02f + shakeX, cy + size.height * 0.15f), size = Size(size.width * 0.10f, size.height * 0.14f))
        }
        PetAccessory.MASK -> {
            drawRoundRect(color = Color(0xFFECEFF1), topLeft = Offset(cx - size.width * 0.16f, cy + size.height * 0.02f), size = Size(size.width * 0.32f, size.height * 0.11f), cornerRadius = CornerRadius(size.width * 0.05f))
            drawLine(Color(0xFFB0BEC5), Offset(cx - size.width * 0.15f, cy + size.height * 0.055f), Offset(cx - size.width * 0.29f, cy - size.height * 0.02f), strokeWidth = size.width * 0.012f)
            drawLine(Color(0xFFB0BEC5), Offset(cx + size.width * 0.15f, cy + size.height * 0.055f), Offset(cx + size.width * 0.29f, cy - size.height * 0.02f), strokeWidth = size.width * 0.012f)
            drawLine(Color(0xFFB0BEC5), Offset(cx - size.width * 0.10f, cy + size.height * 0.07f), Offset(cx + size.width * 0.10f, cy + size.height * 0.07f), strokeWidth = size.width * 0.008f)
            if (bob > 0.7f) {
                val coughOffset = sin(bob * Math.PI.toFloat() * 4) * size.width * 0.01f
                drawCircle(Color(0x40BDBDBD), radius = size.width * 0.04f, center = Offset(cx + size.width * 0.25f + coughOffset, cy + size.height * 0.05f))
                drawCircle(Color(0x30BDBDBD), radius = size.width * 0.03f, center = Offset(cx + size.width * 0.32f + coughOffset, cy + size.height * 0.02f))
            }
        }
        PetAccessory.HIDING -> {
            val roofPath = Path().apply {
                moveTo(cx - size.width * 0.45f, cy - size.height * 0.30f)
                lineTo(cx, cy - size.height * 0.50f)
                lineTo(cx + size.width * 0.45f, cy - size.height * 0.30f)
                close()
            }
            drawPath(roofPath, color = Color(0xFF795548))
            val tremble = sin(acc * Math.PI.toFloat() * 6) * size.width * 0.008f
            drawCircle(color = bodyColorConst, radius = size.width * 0.03f, center = Offset(cx - size.width * 0.15f + tremble, cy + size.height * 0.30f))
            drawCircle(color = bodyColorConst, radius = size.width * 0.03f, center = Offset(cx + size.width * 0.15f + tremble, cy + size.height * 0.30f))
            val lightningAlpha = if (acc > 0.6f) (acc - 0.6f) / 0.4f else 0f
            val lPath = Path().apply {
                moveTo(cx + size.width * 0.35f, cy - size.height * 0.50f)
                lineTo(cx + size.width * 0.28f, cy - size.height * 0.35f)
                lineTo(cx + size.width * 0.38f, cy - size.height * 0.35f)
                lineTo(cx + size.width * 0.30f, cy - size.height * 0.18f)
            }
            drawPath(lPath, color = Color(0xFFFFEB3B).copy(alpha = lightningAlpha), style = Stroke(width = size.width * 0.03f))
        }
        PetAccessory.WINDY -> {
            val windOffset = acc * size.width * 0.15f
            for (i in 0..2) {
                val yOff = cy - size.height * 0.15f + i * size.height * 0.15f
                val windPath = Path().apply {
                    moveTo(cx + size.width * 0.20f + windOffset * 0.3f, yOff)
                    quadraticBezierTo(cx + size.width * 0.32f + windOffset * 0.5f, yOff - size.height * 0.04f, cx + size.width * 0.48f + windOffset, yOff)
                }
                drawPath(windPath, color = Color(0xB3FFFFFF), style = Stroke(width = size.width * 0.018f))
            }
            val tiltX = sin(acc * Math.PI.toFloat()) * size.width * 0.03f
            drawOval(color = bodyColorConst, topLeft = Offset(cx - size.width * 0.12f + tiltX, cy - size.height * 0.52f), size = Size(size.width * 0.24f, size.height * 0.10f))
        }
        PetAccessory.HAPPY -> {
            val handX = cx + size.width * 0.32f
            val handY = cy + size.height * 0.02f + sin(acc * Math.PI.toFloat() * 2) * size.height * 0.05f
            drawOval(color = bodyColorConst, topLeft = Offset(handX - size.width * 0.06f, handY), size = Size(size.width * 0.12f, size.height * 0.18f))
            val starAlpha = 0.5f + sin(bob * Math.PI.toFloat() * 2) * 0.5f
            drawCircle(color = Color(0xFFFFEB3B).copy(alpha = starAlpha), radius = size.width * 0.035f, center = Offset(cx - size.width * 0.38f, cy - size.height * 0.35f))
            drawCircle(color = Color(0xFFFFEB3B).copy(alpha = 1f - starAlpha), radius = size.width * 0.025f, center = Offset(cx + size.width * 0.40f, cy - size.height * 0.40f))
            drawCircle(color = Color(0xFFFFEB3B).copy(alpha = starAlpha * 0.7f), radius = size.width * 0.02f, center = Offset(cx - size.width * 0.42f, cy - size.height * 0.15f))
        }
        PetAccessory.WARM -> {
            drawRoundRect(color = Color(0xFFFF8A65), topLeft = Offset(cx - size.width * 0.12f, cy + size.height * 0.20f), size = Size(size.width * 0.24f, size.height * 0.18f), cornerRadius = CornerRadius(size.width * 0.04f))
            val heartBeat = 1f + sin(acc * Math.PI.toFloat() * 2) * 0.15f
            val heartSize = size.width * 0.03f * heartBeat
            val heartPath = Path().apply {
                moveTo(cx, cy + size.height * 0.27f + heartSize)
                quadraticBezierTo(cx - heartSize * 1.5f, cy + size.height * 0.25f, cx, cy + size.height * 0.26f)
                quadraticBezierTo(cx + heartSize * 1.5f, cy + size.height * 0.25f, cx, cy + size.height * 0.27f + heartSize)
            }
            drawPath(heartPath, color = Color.White)
            val breathAlpha = sin(bob * Math.PI.toFloat()) * 0.4f
            if (breathAlpha > 0.1f) {
                drawOval(color = Color.White.copy(alpha = breathAlpha), topLeft = Offset(cx + size.width * 0.08f, cy + size.height * 0.0f), size = Size(size.width * 0.12f, size.height * 0.06f))
            }
        }
        PetAccessory.SLEEPY -> {
            val zzOffset = acc * size.height * 0.1f
            val zzAlpha = 0.4f + acc * 0.6f
            drawCircle(Color.White.copy(alpha = zzAlpha * 0.6f), radius = size.width * 0.025f, center = Offset(cx + size.width * 0.28f, cy - size.height * 0.25f - zzOffset))
            drawCircle(Color.White.copy(alpha = zzAlpha * 0.4f), radius = size.width * 0.02f, center = Offset(cx + size.width * 0.34f, cy - size.height * 0.35f - zzOffset * 0.7f))
        }
    }
}

private val bodyColorConst = Color(0xFFFFC107)

data class PetState(
    val greeting: String,
    val tip: String,
    val accessory: PetAccessory,
    val outfit: PetOutfit
)

enum class PetAccessory {
    SUN_HAT, UMBRELLA, SCARF, MASK, HIDING, WINDY, HAPPY, WARM, SLEEPY
}

enum class PetOutfit {
    NONE, T_SHIRT, LIGHT_JACKET, COAT, DOWN_JACKET
}

private fun getWeatherPetState(
    weatherIcon: String?,
    temp: Int?,
    tempMin: Int?,
    tempMax: Int?,
    hourlyList: List<WeatherHourlyBean.HourlyBean>
): PetState {
    val currentTemp = temp ?: 20
    val outfit = getOutfit(currentTemp)
    val nextTemps = hourlyList.drop(1).take(6).mapNotNull { it.temp?.toIntOrNull() }
    val nextMin = nextTemps.minOrNull() ?: tempMin
    val nextMax = nextTemps.maxOrNull() ?: tempMax
    val cooling = nextMin != null && currentTemp - nextMin >= 8
    val warming = nextMax != null && nextMax - currentTemp >= 8
    val rangeTip = when {
        cooling -> " 接下来降温明显，记得添衣保暖。"
        warming -> " 稍后升温明显，注意防晒补水。"
        tempMin != null && tempMax != null && tempMax - tempMin >= 12 -> " 今天温差大，出门带件外套更稳妥。"
        else -> ""
    }

    return when {
        weatherIcon in listOf("302", "303", "304") -> PetState(
            greeting = "打雷啦！小熊先躲好",
            tip = "雷电天气远离空旷地带和大树，尽量待在室内。$rangeTip",
            accessory = PetAccessory.HIDING,
            outfit = outfit
        )
        weatherIcon in listOf("306", "307", "308", "310", "311", "312") -> PetState(
            greeting = "大雨来了，小熊撑大伞",
            tip = "暴雨尽量减少外出，避开低洼积水路段，鞋子也要防滑。$rangeTip",
            accessory = PetAccessory.UMBRELLA,
            outfit = outfit
        )
        weatherIcon?.startsWith("3") == true -> PetState(
            greeting = "下雨啦，小熊撑伞出门",
            tip = "带好雨伞，路面湿滑慢一点，包里可以备纸巾。$rangeTip",
            accessory = PetAccessory.UMBRELLA,
            outfit = outfit
        )
        weatherIcon?.startsWith("4") == true -> PetState(
            greeting = "下雪啦，小熊穿厚点",
            tip = "雪天注意保暖防滑，围巾帽子都安排上。$rangeTip",
            accessory = PetAccessory.SCARF,
            outfit = if (currentTemp <= 0) PetOutfit.DOWN_JACKET else PetOutfit.COAT
        )
        weatherIcon?.startsWith("5") == true -> PetState(
            greeting = "雾霾天，小熊戴口罩",
            tip = "空气质量差，减少户外运动，出门戴好口罩。$rangeTip",
            accessory = PetAccessory.MASK,
            outfit = outfit
        )
        weatherIcon in listOf("200", "201", "202", "203", "204", "205", "206", "207") -> PetState(
            greeting = "风有点大，小熊压住帽子",
            tip = "大风天气关好门窗，外出注意高空坠物。$rangeTip",
            accessory = PetAccessory.WINDY,
            outfit = outfit
        )
        weatherIcon in listOf("100", "101", "102", "103", "150", "151", "152", "153") -> PetState(
            greeting = if (currentTemp >= 30) "晴朗偏热，小熊戴帽墨镜" else "天气不错，小熊戴帽出门",
            tip = sunnyTip(currentTemp, rangeTip),
            accessory = PetAccessory.SUN_HAT,
            outfit = outfit
        )
        currentTemp >= 30 -> PetState(
            greeting = "今天偏热，小熊换短袖",
            tip = "穿短袖更舒服，记得补水、防晒，午后少暴晒。$rangeTip",
            accessory = PetAccessory.SUN_HAT,
            outfit = PetOutfit.T_SHIRT
        )
        currentTemp <= 5 -> PetState(
            greeting = "有点冷，小熊抱暖手宝",
            tip = "当前温度低，外套围巾别忘了，小心着凉。$rangeTip",
            accessory = PetAccessory.WARM,
            outfit = if (currentTemp <= 0) PetOutfit.DOWN_JACKET else PetOutfit.COAT
        )
        weatherIcon == "104" || weatherIcon == "154" -> PetState(
            greeting = "阴天犯困，小熊穿舒服点",
            tip = "阴天光线弱，注意精神状态，出门按当前温度穿搭。$rangeTip",
            accessory = PetAccessory.SLEEPY,
            outfit = outfit
        )
        else -> PetState(
            greeting = "小熊已按温度换好衣服",
            tip = "当前${currentTemp}°，按体感穿搭就好，记得关注天气变化。$rangeTip",
            accessory = PetAccessory.HAPPY,
            outfit = outfit
        )
    }
}

private fun getOutfit(temp: Int): PetOutfit = when {
    temp >= 26 -> PetOutfit.T_SHIRT
    temp >= 16 -> PetOutfit.LIGHT_JACKET
    temp >= 8 -> PetOutfit.COAT
    else -> PetOutfit.DOWN_JACKET
}

private fun sunnyTip(temp: Int, rangeTip: String): String = when {
    temp >= 32 -> "太阳强又偏热，帽子墨镜和防晒都安排上，多喝水。$rangeTip"
    temp >= 24 -> "适合出门活动，戴帽子墨镜更舒服，也别忘了补水。$rangeTip"
    temp >= 12 -> "天气不错但不算热，薄外套配帽子刚好。$rangeTip"
    else -> "天晴但温度低，晒太阳也要穿暖一点。$rangeTip"
}