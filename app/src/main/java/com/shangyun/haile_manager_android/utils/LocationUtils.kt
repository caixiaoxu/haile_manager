package com.shangyun.haile_manager_android.utils

import android.content.Context
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch

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


    /**
     * 搜索Poi 列表
     */
    fun searchPoiList(
        context: Context,
        keywords: String,
        cityCode: String,
        pageNum: Int = 1,
        pageSize: Int = 10,
        latLon: LatLonPoint? = null,
        bound: Int = 1000,
        callBack: (rCode: Int, poiResult: PoiResult) -> Unit
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
                    callBack.invoke(rCode, poiResult)
                }

                override fun onPoiItemSearched(poiItem: PoiItem, i: Int) {}
            })
            poiSearch.searchPOIAsyn()
        } catch (e: AMapException) {
            e.printStackTrace()
        }
    }
}