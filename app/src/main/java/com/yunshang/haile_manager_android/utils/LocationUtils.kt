package com.yunshang.haile_manager_android.utils

import android.content.Context
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.tencent.lbssearch.TencentSearch
import com.tencent.lbssearch.httpresponse.HttpResponseListener
import com.tencent.lbssearch.`object`.param.ExploreParam
import com.tencent.lbssearch.`object`.param.SearchParam
import com.tencent.lbssearch.`object`.result.ExploreResultObject
import com.tencent.lbssearch.`object`.result.SearchResultObject
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import timber.log.Timber


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

    /**
     * 搜索附近列表
     */
    fun searchNearByOfTMap(
        context: Context,
        latLng: LatLng,
        radius: Int = 5000,
        callBack: (data: List<ExploreResultObject.SearchResultData>?) -> Unit
    ) {
        //周边搜索（圆形范围）： boundary=nearby(lat,lng(中心坐标),radius(半径/米)[, auto_extend])
        val nearBy = ExploreParam.Nearby(latLng, radius)
        //创建创建explore参数请求
        val exploreParam = ExploreParam(nearBy)
        //周边推荐模式  1.DEFAULT：默认 地点签到场景，针对用户签到的热门 地点进行优先排序    2.SHARE_LOCATION：位置共享场景
        exploreParam.policy(ExploreParam.Policy.DEFAULT)
        //创建TencentSearch
        val tencentSearch = TencentSearch(context)
        //周边推荐(Explore接口)数据请求
        tencentSearch.explore(exploreParam, object : HttpResponseListener<ExploreResultObject?> {
            override fun onFailure(
                arg0: Int, arg2: String,
                arg3: Throwable
            ) {
                Timber.v("onFailure$arg0")
            }

            override fun onSuccess(arg0: Int, obj: ExploreResultObject?) {
                if (obj?.data == null) {
                    return
                }
                callBack(obj.data)
            }
        })

    }
}