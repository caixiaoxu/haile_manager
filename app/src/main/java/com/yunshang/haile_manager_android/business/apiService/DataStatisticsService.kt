package com.yunshang.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.yunshang.haile_manager_android.data.entities.*
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Title : 设备接口
 * Author: Lsy
 * Date: 2023/4/23 10:13
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface DataStatisticsService {

    /**
     * 请求数据统计总额接口
     */
    @POST("/statistics/getStatisticsTotalVO")
    suspend fun requestStatisticsTotal(@Body params: RequestBody): ResponseWrapper<DataStatisticsShopListEntity>

    /**
     * 请求数据统计店铺总额接口
     */
    @POST("/statistics/getShopStatisticsTotalVOPage")
    suspend fun requestStatisticsShopTotal(@Body params: RequestBody): ResponseWrapper<ResponseList<DataStatisticsShopListEntity>>

    /**
     * 请求数据统计店铺详情接口
     */
    @POST("/statistics/getStatisticsDetailVO")
    suspend fun requestStatisticsShopDetail(@Body params: RequestBody): ResponseWrapper<DataStatisticsShopDetailEntity>
}