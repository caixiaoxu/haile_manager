package com.shangyun.haile_manager_android.business.vm

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.utils.LocationUtils
import timber.log.Timber

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

    val poiItemList:MutableLiveData<MutableList<PoiItem>> by lazy { MutableLiveData() }

    /**
     * 搜索周边
     */
    fun searchNearby(context: Context, keywords: String, lat: Double, lng: Double) {
        LocationUtils.searchPoiList(context,keywords,cityCode, latLon = LatLonPoint(lat,lng)){rCode, poiResult ->
            if (1000 == rCode){
                poiItemList.postValue(poiResult.pois)
            } else {
                SToast.showToast(msg="请求附近位置失败")
            }
        }
    }
}