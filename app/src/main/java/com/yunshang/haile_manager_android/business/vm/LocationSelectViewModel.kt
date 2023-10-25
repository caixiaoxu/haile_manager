package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.amap.api.services.core.LatLonPoint
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.data.arguments.PoiResultData
import com.yunshang.haile_manager_android.utils.LocationUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 11:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class LocationSelectViewModel : BaseViewModel() {
    var cityCode: String = ""
    var shopTypeName: String = ""

    val poiItemList: MutableLiveData<MutableList<PoiResultData>> by lazy { MutableLiveData() }

    var curLocation: PoiResultData? = null

    /**
     * 搜索周边
     */
    fun searchNearbyOfAMap(context: Context, lat: Double, lng: Double) {
        loadingStatus.postValue(true)
        LocationUtils.searchPoiListOfAMap(
            context,
            shopTypeName,
            cityCode,
            latLon = LatLonPoint(lat, lng)
        ) { rCode, poiResult ->
            loadingStatus.postValue(false)
            if (1000 == rCode && poiResult.isNotEmpty()) {
                poiItemList.postValue(poiResult.map {
                    PoiResultData(
                        it.title,
                        it.provinceName + it.cityName + it.adName + it.snippet,
                        it.latLonPoint.latitude,
                        it.latLonPoint.longitude,
                        it.distance.toDouble()
                    )
                }.toMutableList())
            } else {
                SToast.showToast(msg = "请求附近位置失败")
            }
        }
    }

    fun searchNearbyOfTMap(context: Context, lat: Double, lng: Double) {
        loadingStatus.postValue(true)
        LocationUtils.searchPoiListOfTMap(
            context,
            shopTypeName,
            cityCode,
            latLon = LatLonPoint(lat, lng)
        ) { rCode, poiResult ->
            loadingStatus.postValue(false)
            if (poiResult.isNotEmpty()) {
                poiItemList.postValue(poiResult.map {
                    PoiResultData(
                        it.title,
                        it.address,
                        it.latLng.getLatitude(),
                        it.latLng.getLongitude(),
                        it.distance
                    )
                }.toMutableList().apply {
                    curLocation?.let {
                        add(0, it)
                    }
                })
            } else {
                SToast.showToast(msg = "请求附近位置失败")
            }
        }
    }
}