package com.zj.weather.view.city.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zj.model.room.entity.CityInfo
import com.zj.utils.swipe.SwipeDeleteLayout
import com.zj.utils.view.ImageLoader
import com.zj.utils.weather.IconUtils
import com.zj.weather.R
import com.zj.weather.view.city.viewmodel.CityWeatherInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CityListItem(
    cityInfo: CityInfo,
    weatherInfo: CityWeatherInfo? = null,
    isShowDelete: Boolean = true,
    toWeatherDetails: (CityInfo) -> Unit,
    onDeleteListener: (CityInfo) -> Unit
) {
    val swipeState = rememberSwipeableState(0)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val weatherBack = IconUtils.getWeatherBack(
        context,
        weatherInfo?.weatherIcon,
        weatherInfo?.isDay
    )
    val lottieRes = IconUtils.getWeatherAnimationIcon(
        context,
        weatherInfo?.weatherIcon,
        weatherInfo?.isDay
    )
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    val textShadow = TextStyle(
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.45f),
            offset = Offset(0f, 1f),
            blurRadius = 4f
        )
    )

    SwipeDeleteLayout(swipeState = swipeState, isShowChild = isShowDelete, childContent = {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 4.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(0.dp, 14.dp, 14.dp, 0.dp),
                backgroundColor = MaterialTheme.colors.primaryVariant
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(color = Color.Red)
                        .clickable {
                            onDeleteListener(cityInfo)
                            coroutineScope.launch {
                                swipeState.animateTo(0)
                            }
                        }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.city_delete),
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }) {
        Card(
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(vertical = 4.dp)
                .clickable { toWeatherDetails(cityInfo) },
            elevation = 0.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ImageLoader(
                    data = weatherBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.15f))
                )
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier
                        .size(180.dp)
                        .align(Alignment.CenterEnd)
                        .offset(x = 34.dp)
                        .alpha(0.24f),
                    progress = { progress }
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.08f))
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = cityInfo.name,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                style = textShadow
                            )
                            if (cityInfo.isLocation == 1) {
                                ImageLoader(
                                    data = R.drawable.ic_baseline_location_on_24,
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = weatherInfo?.weatherText ?: "",
                            modifier = Modifier.padding(top = 2.dp),
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            style = textShadow
                        )
                        if (weatherInfo?.warningText != null) {
                            Text(
                                text = weatherInfo.warningText,
                                modifier = Modifier.padding(top = 2.dp),
                                color = Color(0xFFFFD54F),
                                fontSize = 11.sp,
                                maxLines = 1,
                                style = textShadow
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (weatherInfo?.temp != null) "${weatherInfo.temp}°" else "--",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Light,
                            style = textShadow
                        )
                        if (weatherInfo?.tempMax != null && weatherInfo.tempMin != null) {
                            Text(
                                text = "最高${weatherInfo.tempMax}° 最低${weatherInfo.tempMin}°",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                style = textShadow
                            )
                        }
                    }
                }
            }
        }
    }
}