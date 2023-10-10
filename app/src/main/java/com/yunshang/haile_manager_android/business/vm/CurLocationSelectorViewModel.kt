package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.tencent.lbssearch.TencentSearch
import com.tencent.lbssearch.httpresponse.BaseObject
import com.tencent.lbssearch.httpresponse.HttpResponseListener
import com.tencent.lbssearch.`object`.param.Geo2AddressParam
import com.tencent.lbssearch.`object`.result.Geo2AddressResultObject
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import com.yunshang.haile_manager_android.data.arguments.PoiResultData
import timber.log.Timber


/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/9 17:00
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class CurLocationSelectorViewModel : BaseViewModel() {

    var startParse = false

    val poiItemList: MutableLiveData<MutableList<PoiResultData>> by lazy { MutableLiveData() }

    fun parseLatLngAndNearBy(context: Context, latlng: LatLng) {
        if (startParse){
            loadingStatus.postValue(true)
            TencentSearch(context).geo2address(
                Geo2AddressParam(latlng).getPoi(true).setPoiOptions(
                    Geo2AddressParam.PoiOptions().setRadius(1000)
                        .setPolicy(Geo2AddressParam.PoiOptions.POLICY_O2O)
                ), object : HttpResponseListener<BaseObject> {
                    override fun onSuccess(p0: Int, p1: BaseObject?) {
                        loadingStatus.postValue(false)
                        if (p1 == null) {
                            return
                        }
                        val obj = p1 as Geo2AddressResultObject
                        val sb = StringBuilder()
                        sb.append("逆地址解析")
                        sb.append("地址：${obj.result.address}".trimIndent())
                        sb.append("\npois:")
                        poiItemList.postValue(obj.result.pois.map {poi->
                            PoiResultData(
                                poi.title,
                                poi.address,
                                poi.latLng.latitude,
                                poi.latLng.longitude,
                                poi._distance.toDouble()
                            )
                        }.toMutableList())
                        Timber.e( sb.toString())
                    }

                    override fun onFailure(p0: Int, p1: String?, p2: Throwable?) {
                        loadingStatus.postValue(false)
                        Timber.e("error code:$p0, msg:$p1")
                    }
                })
        }
    }
}