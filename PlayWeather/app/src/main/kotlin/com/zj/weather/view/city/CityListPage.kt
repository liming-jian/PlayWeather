package com.zj.weather.view.city

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.zj.model.room.entity.CityInfo
import com.zj.utils.defaultCityState
import com.zj.utils.lce.NoContent
import com.zj.weather.R
import com.zj.weather.view.city.viewmodel.CityListViewModel
import com.zj.weather.view.city.viewmodel.CityWeatherInfo
import com.zj.weather.view.city.widget.CityListItem
import com.zj.weather.view.city.widget.TitleBar

@Composable
fun CityListPage(
    cityListViewModel: CityListViewModel,
    onBack: () -> Unit
) {
    val cityInfoList by cityListViewModel.cityInfoList.collectAsState(initial = arrayListOf())
    val cityWeatherList by cityListViewModel.cityWeatherList.collectAsState()

    LaunchedEffect(cityInfoList) {
        if (cityInfoList.isNotEmpty()) {
            cityListViewModel.loadCityWeathers(cityInfoList)
        }
    }

    CityListPage(
        cityInfoList = cityInfoList,
        cityWeatherList = cityWeatherList,
        onBack = onBack,
        toWeatherDetails = {
            defaultCityState.value = it
            onBack()
        }
    ) {
        cityListViewModel.deleteCityInfo(it)
    }
}

@Composable
fun CityListPage(
    cityInfoList: List<CityInfo>,
    cityWeatherList: List<CityWeatherInfo>,
    onBack: () -> Unit,
    toWeatherDetails: (CityInfo) -> Unit,
    deleteCityInfo: (CityInfo) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.page_margin))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TitleBar(R.string.city_title, onBack)
        if (cityInfoList.isNotEmpty()) {
            val listState = rememberLazyListState()
            LazyColumn(state = listState) {
                items(cityInfoList) { cityInfo ->
                    val weatherInfo = cityWeatherList.find { it.cityInfo.uid == cityInfo.uid }
                    CityListItem(
                        cityInfo = cityInfo,
                        weatherInfo = weatherInfo,
                        isShowDelete = cityInfo.isLocation != 1,
                        toWeatherDetails = toWeatherDetails,
                        onDeleteListener = deleteCityInfo
                    )
                }
            }
        } else {
            NoContent(tip = stringResource(id = R.string.add_location_warn2))
        }
    }
}
