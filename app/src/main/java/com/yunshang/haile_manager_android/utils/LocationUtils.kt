package com.yunshang.haile_manager_android.utils

import android.content.Context
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.tencent.lbssearch.TencentSearch
import com.tencent.lbssearch.`object`.param.SearchParam
import com.tencent.lbssearch.`object`.result.SearchResultObject
import com.tencent.map.tools.net.http.HttpResponseListener
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import java.util.ArrayList


/**
 * Title : 定位工具类
 * Author: Lsy
 * Date: 2023/4/19 13:40
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object LocationUtils {

    // 腾讯搜索的secretkey
    private const val secretKey = "ebuVGZMAxQCBDwonHPvReX9EEJqB2p1f"


    /**
     * 搜索Poi 列表
     */
    fun searchPoiListOfAMap(
        context: Context,
        keywords: String,
        cityCode: String,
        pageNum: Int = 1,
        pageSize: Int = 10,
        latLon: LatLonPoint? = null,
        bound: Int = 1000,
        callBack: (rCode: Int, poiResult: List<PoiItem>) -> Unit
    ) {
        val query = PoiSearch.Query(keywords, "", cityCode)
        query.pageNum = pageNum   //页数
        query.pageSize = pageSize // 设置每页最多返回多少条poiItem
        query.isDistanceSort = true
        try {
            val poiSearch = PoiSearch(context, query)
            //设置周边搜索的中心点以及半径
            latLon?.let {
                poiSearch.bound = PoiSearch.SearchBound(latLon, bound)
            }
            poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
                override fun onPoiSearched(poiResult: PoiResult, rCode: Int) {
                    callBack.invoke(rCode, poiResult.pois)
                }

                override fun onPoiItemSearched(poiItem: PoiItem, i: Int) {}
            })
            poiSearch.searchPOIAsyn()
        } catch (e: AMapException) {
            e.printStackTrace()
        }
    }

    /**
     * 搜索Poi 列表
     */
    fun searchPoiListOfTMap(
        context: Context,
        keywords: String,
        cityCode: String,
        pageNum: Int = 1,
        pageSize: Int = 10,
        latLon: LatLonPoint? = null,
        bound: Int = 10000,
        isNearBy: Boolean = true,
        callBack: (rCode: Int, poiResult: List<SearchResultObject.SearchResultData>) -> Unit
    ) {
        val tencentSearch = TencentSearch(context, secretKey)
        //搜索条件
        val boundary = if (null != latLon && isNearBy) {
            SearchParam.Nearby(
                LatLng(
                    latLon.latitude,
                    latLon.longitude
                ), bound
            )
        } else {
            SearchParam.Region(cityCode).autoExtend(false)
        }
        val searchParam = SearchParam(keywords, boundary).apply {
            pageIndex(pageNum)
            pageSize(pageSize)
        }
        tencentSearch.search(searchParam, object : HttpResponseListener<SearchResultObject> {
            override fun onSuccess(p0: Int, obj: SearchResultObject?) {
                if (obj?.data == null) {
                    callBack(p0, listOf())
                    return
                }
                callBack(p0, obj.data)
            }

            override fun onFailure(p0: Int, p1: String?, p2: Throwable?) {
                callBack(p0, listOf())
            }
        })
    }
}