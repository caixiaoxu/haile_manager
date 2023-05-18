package com.shangyun.haile_manager_android.business.apiService

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.network.response.ResponseWrapper
import com.shangyun.haile_manager_android.data.entities.DiscountsBusinessTypeEntity
import com.shangyun.haile_manager_android.data.entities.DiscountsDeviceTypeEntity
import com.shangyun.haile_manager_android.data.entities.DiscountsEntity
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/11 19:58
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface DiscountsService {
    @POST("/coupon/subject/activateCompensate")
    suspend fun activateCompensate(@Body body: RequestBody): ResponseWrapper<Any>
    @POST("/timeMarket/list")
    suspend fun requestDiscountList(@Body body: RequestBody): ResponseWrapper<ResponseList<DiscountsEntity>>
    @POST("/timeMarket/listBizType")
    suspend fun requestBusinessType(@Body body: RequestBody): ResponseWrapper<MutableList<DiscountsBusinessTypeEntity>>
    @POST("/timeMarket/listDeviceType")
    suspend fun requestDeviceCategory(@Body body: RequestBody): ResponseWrapper<MutableList<DiscountsDeviceTypeEntity>>
}